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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlin.math.max

class ViewReportsActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_view_reports)
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

        fetchSpinnerData()
    }

    private fun fetchSpinnerData() {
        val url = ApiFile.APP_URL + "/accounts/batchNameByLocation?locationId=$locId&createdBy=$login_name"
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
                                this@ViewReportsActivity,
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
                                this@ViewReportsActivity,
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
                        this@ViewReportsActivity,
                        "Failed to fetch batch names",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun fetchReports(batchName: String) {
        val url = ApiFile.APP_URL + "/accounts/vehDetailsByBatchName?batchName=$batchName"
        val request = Request.Builder().url(url).build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    val objArray = jsonObject.getJSONArray("obj")
                    val reportsList = mutableListOf<ReportItem>()

                    for (i in 0 until objArray.length()) {
                        val obj = objArray.getJSONObject(i)
                        val reportItem = ReportItem(
                            srNo = obj.getInt("SRNO"),
                            batchName = obj.getString("BATCHNAME"),
                            vin = obj.getString("VIN"),
                            chassis_no = obj.getString("CHASSIS_NO"),
                            fuel_desc = obj.getString("FUEL_DESC"),
                            model_desc = obj.getString("MODEL_DESC"),
                            colour = obj.getString("COLOUR"),
                            locationName = obj.getString("LOCATIONNAME"),
                            UPDATEDBY=obj.getString("UPDATEDBY")
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
                        this@ViewReportsActivity,
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

    private fun updateTableView(reportsList: List<ReportItem>) {
        tableLayout.removeAllViews()
        headerTableLayout.removeAllViews()

        val headers = listOf("Sr.No.","Batch Name", "VIN", "Chassis No","Fuel Desc", "Model Desc", "Colour", "Location","Updated By")
        val maxWidths = MutableList(headers.size) { 0 }
        val textViewPadding = 24 * 2

        for ((index, header) in headers.withIndex()) {
            val headerTextView = createTextView(header, true)
            headerTextView.measure(0, 0)
            maxWidths[index] = headerTextView.measuredWidth + textViewPadding
        }

        for (report in reportsList) {
            val dataRow = listOf(
                report.srNo,report.batchName, report.vin, report.chassis_no,
                report.fuel_desc, report.model_desc, report.colour, report.locationName,report.UPDATEDBY
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
                report.srNo,report.batchName, report.vin, report.chassis_no,
                report.fuel_desc, report.model_desc, report.colour, report.locationName,report.UPDATEDBY
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

data class ReportItem(
    val srNo:Int,
    val batchName: String,
    val vin: String,
    val chassis_no: String,
    val fuel_desc: String,
    val model_desc: String,
    val colour: String,
    val locationName: String,
    val UPDATEDBY:String
)

