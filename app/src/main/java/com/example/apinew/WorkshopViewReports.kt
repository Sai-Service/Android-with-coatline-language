package com.example.apinew

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.apinew.ApiFile
import com.example.apinew.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlin.math.max

class WorkshopViewReports : AppCompatActivity() {

    private lateinit var viewReportsButton: Button
    private lateinit var refreshButton: Button
    private lateinit var tableLayout: TableLayout
    private lateinit var headerTableLayout: TableLayout
    private val client = OkHttpClient()
    private lateinit var homepage: ImageButton
    private lateinit var batchNameSpinner: Spinner
    private val spinnerItems = mutableListOf<String>()
    private lateinit var login_name: String
    private var locId: Int = 0
    private lateinit var rowCountTextView:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workshop_view_reports)
        batchNameSpinner = findViewById(R.id.batch_name_spinner)
        login_name = intent.getStringExtra("login_name") ?: ""
        locId = intent.getIntExtra("locId", 0)

        viewReportsButton = findViewById(R.id.view_reports_button)
        refreshButton = findViewById(R.id.refresh_button)
        tableLayout = findViewById(R.id.table_layout)
        headerTableLayout = findViewById(R.id.header_table_layout)
        homepage = findViewById(R.id.homepage)
        rowCountTextView=findViewById(R.id.rowCountTextView)
        rowCountTextView.visibility= View.GONE

        fetchSpinnerData()

        homepage.setOnClickListener {
            backToHome()
        }

        viewReportsButton.setOnClickListener {
            val batchName = batchNameSpinner.selectedItem.toString()
            if (batchName == "Select batch name") {
                Toast.makeText(this, "Please select a batch name", Toast.LENGTH_SHORT).show()
            } else {
                fetchReports(batchName)
            }
        }

        refreshButton.setOnClickListener {
            refreshFields()
        }

    }

    private fun fetchSpinnerData() {
        val loc=locId.toString()
        val url = ApiFile.APP_URL +"/srAccounts/srBatchNameByLocation?locationId=$loc&createdBy=$login_name"
        val request = Request.Builder().url(url).build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()

                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    if (jsonObject.has("obj")) {
                        val objArray = jsonObject.getJSONArray("obj")
                        spinnerItems.clear()
                        spinnerItems.add("Select batch name")

                        for (i in 0 until objArray.length()) {
                            val obj = objArray.getJSONObject(i)
                            if (obj.has("BATCHNAME")) {
                                val batchName = obj.getString("BATCHNAME")
                                spinnerItems.add(batchName)
                            }
                        }
                        runOnUiThread {
                            val adapter = ArrayAdapter(
                                this@WorkshopViewReports,
                                android.R.layout.simple_spinner_item,
                                spinnerItems
                            )
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            batchNameSpinner.adapter = adapter
                            batchNameSpinner.setSelection(0)
                            batchNameSpinner.setBackgroundResource(R.drawable.spinner_border)
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@WorkshopViewReports,
                                "No batch names found",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@WorkshopViewReports,
                        "Failed to fetch batch names",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun fetchReports(batchName: String) {
        val url = ApiFile.APP_URL + "/srAccounts/srVehDetailsByBatchName?batchName=$batchName"
        val request = Request.Builder().url(url).build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    val objArray = jsonObject.getJSONArray("obj")
                    val reportsList = mutableListOf<ReportItem3>()

                    for (i in 0 until objArray.length()) {
                        val obj = objArray.getJSONObject(i)
                        val reportItem = ReportItem3(
                            SRNO = obj.getInt("SRNO"),
                            BATCHNAME = obj.getString("BATCHNAME"),
                            VIN = obj.getString("VIN"),
                            CHASSIS_NO = obj.getString("CHASSIS_NO"),
                            FUEL_DESC = obj.getString("FUEL_DESC"),
                            MODEL_DESC = obj.getString("MODEL_DESC"),
                            COLOUR = obj.getString("COLOUR"),
                            REG_NO = obj.getString("REG_NO"),
                            ENGINE_NO = obj.getString("ENGINE_NO"),
                            LOCATIONNAME=obj.getString("LOCATIONNAME"),
                            UPDATEDBY=obj.getString("UPDATEDBY"),
                            JOBCARDNO=obj.getString("JOBCARDNO")
                        )
                        reportsList.add(reportItem)
                    }

                    runOnUiThread {
                        updateTableView(reportsList)
                        rowCountTextView.visibility= View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@WorkshopViewReports,
                        "Failed to fetch reports",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun refreshFields() {
        tableLayout.removeAllViews()
        headerTableLayout.removeAllViews()
        batchNameSpinner.setSelection(0)
        rowCountTextView.text=""
        rowCountTextView.visibility=View.GONE

    }

    private fun updateTableView(reportsList: List<ReportItem3>) {
        tableLayout.removeAllViews()
        headerTableLayout.removeAllViews()

        val headers = listOf("Sr.No.","Location","Job Card No","Vehicle No", "Vin","Chassis No","Engine No", "Model Desc", "Colour","Fuel Desc","Updated By")
        val maxWidths = MutableList(headers.size) { 0 }
        val textViewPadding = 24 * 2

        for ((index, header) in headers.withIndex()) {
            val headerTextView = createTextView(header, true)
            headerTextView.measure(0, 0)
            maxWidths[index] = headerTextView.measuredWidth + textViewPadding
        }

        for (report in reportsList) {
            val dataRow = listOf(
                report.SRNO,report.LOCATIONNAME,report.JOBCARDNO,report.REG_NO,report.VIN, report.CHASSIS_NO,report.ENGINE_NO,
                 report.MODEL_DESC, report.COLOUR,report.FUEL_DESC,report.UPDATEDBY
            )
            for ((index, data) in dataRow.withIndex()) {
                val dataTextView = createTextView(data.toString(), false)
                dataTextView.measure(0, 0)
                maxWidths[index] = max(maxWidths[index], dataTextView.measuredWidth + textViewPadding)
            }
        }

        val headerRow = TableRow(this)
        for ((index, header) in headers.withIndex()) {
            val headerText = createTextView(header, true)
            headerText.layoutParams = TableRow.LayoutParams(maxWidths[index], TableRow.LayoutParams.WRAP_CONTENT)
            headerRow.addView(headerText)
        }
        headerTableLayout.addView(headerRow)

        for (report in reportsList) {
            val dataRow = TableRow(this)
            val reportData = listOf(
                report.SRNO,report.LOCATIONNAME,report.JOBCARDNO,report.REG_NO,report.VIN, report.CHASSIS_NO,report.ENGINE_NO,
                report.MODEL_DESC, report.COLOUR,report.FUEL_DESC,report.UPDATEDBY
            )
            for ((index, data) in reportData.withIndex()) {
                val dataText = createTextView(data.toString(), false)
                dataText.layoutParams = TableRow.LayoutParams(maxWidths[index], TableRow.LayoutParams.WRAP_CONTENT)
                dataRow.addView(dataText)
            }
            tableLayout.addView(dataRow)
        }
        rowCountTextView.text = "Total Records: ${reportsList.size}"
    }

    private fun createTextView(text: String, isHeader: Boolean): TextView {
        val textView = TextView(this)
        textView.text = text
        textView.setPadding(24, 16, 24, 16)
        textView.setTextColor(resources.getColor(R.color.black))
        textView.maxLines = 1
        if (isHeader) {
            textView.setBackgroundResource(R.drawable.header_background)
            textView.setTypeface(null, Typeface.BOLD)
        } else {
            textView.setBackgroundResource(R.drawable.table_row_divider)
        }
        return textView
    }

    private fun backToHome() {
        finish()
    }
}

data class ReportItem3(
    val SRNO:Int,
    val BATCHNAME: String,
    val REG_NO: String,
    val VIN: String,
    val ENGINE_NO: String,
    val COLOUR: String,
    val MODEL_DESC: String,
    val CHASSIS_NO: String,
    val FUEL_DESC:String,
    val LOCATIONNAME:String,
    val UPDATEDBY:String,
    val JOBCARDNO:String
)