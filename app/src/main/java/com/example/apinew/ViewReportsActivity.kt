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
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
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
    private lateinit var locId:String
    private lateinit var role:String
    private lateinit var ouId:String
    private lateinit var rowCountTextView:TextView
    private lateinit var closeBatch:Button
    private lateinit var multipleBatchNameSpinner:Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_reports)
        batchNameSpinner = findViewById(R.id.batch_name_spinner)
        login_name = intent.getStringExtra("login_name") ?: ""
        locId = intent.getStringExtra("locId") ?: ""
        role = intent.getStringExtra("role") ?: ""
        ouId = intent.getStringExtra("ouId") ?: ""

        Log.d("LocId", locId)
        multipleBatchNameSpinner=findViewById(R.id.multipleBatchNameSpinner)

        viewReportsButton = findViewById(R.id.view_reports_button)
        refreshButton = findViewById(R.id.refresh_button)
        tableLayout = findViewById(R.id.table_layout)
        headerTableLayout = findViewById(R.id.header_table_layout)
        homepage = findViewById(R.id.homepage)
        rowCountTextView=findViewById(R.id.rowCountTextView)
        rowCountTextView.visibility= View.GONE
        closeBatch=findViewById(R.id.closeBatch)

        closeBatch.setOnClickListener{
            val batchName=multipleBatchNameSpinner.selectedItem.toString()
            updateBatchStatus_1(batchName)
        }

        fetchBatchData()

        homepage.setOnClickListener {
            backToHome()
        }

        viewReportsButton.setOnClickListener {
            val batchName = batchNameSpinner.selectedItem.toString()
            if (batchName == "Select batch name") {
                Toast.makeText(this, "Please select a batch name", Toast.LENGTH_SHORT).show()
            } else {
                fetchReports(batchName)
                Log.d("locId", locId)
            }
        }



        refreshButton.setOnClickListener {
            refreshFields()
        }

        fetchSpinnerData()
    }


    private fun fetchBatchData() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.APP_URL}/faBatch/batchNameOpen?locId=${locId}")
            .build()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                Log.d("jsonDataBatchList", jsonData.toString())
                jsonData?.let {
                    val batchCodeList = parseCities(it)
                    runOnUiThread {
                        val adapter = ArrayAdapter(
                            this@ViewReportsActivity,
                            android.R.layout.simple_spinner_item,
                            batchCodeList
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        multipleBatchNameSpinner.adapter = adapter
                        if (batchCodeList.size > 1) {
                            runOnUiThread {
//                                multipleBatchNameSpinner.visibility = View.VISIBLE
//                                closeBatch.visibility=View.VISIBLE
                                if(ouId=="108"&&role=="FASUPER"){
                                    multipleBatchNameSpinner.visibility=View.VISIBLE
                                    closeBatch.visibility=View.VISIBLE
                                } else if (ouId=="108"){
                                    multipleBatchNameSpinner.visibility=View.GONE
                                    closeBatch.visibility=View.GONE
                                } else{
                                    multipleBatchNameSpinner.visibility=View.VISIBLE
                                    closeBatch.visibility=View.VISIBLE
                                }
                            }
                        } else {
                            runOnUiThread {
                                multipleBatchNameSpinner.visibility = View.GONE
                                closeBatch.visibility=View.GONE
                            }
                        }

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun parseCities(jsonData: String): List<String> {
        val batchCodeList = mutableListOf<String>()
        Log.d("batchList Checking----",batchCodeList.toString())
        try {
            val jsonObject = JSONObject(jsonData)
            val jsonArray = jsonObject.getJSONArray("obj")
            batchCodeList.add("Select Batch Name")
            for (i in 0 until jsonArray.length()) {
                val batchList = jsonArray.getJSONObject(i)
                val code = batchList.getString("batchName")
                batchCodeList.add("$code")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return batchCodeList
    }


    private fun updateBatchStatus_1(batchName:String) {
        val batchNameVR = multipleBatchNameSpinner.selectedItem.toString()
        if (batchName == "Select Batch Name") {
            Toast.makeText(
                this@ViewReportsActivity,
                "Please select the batchname to close",
                Toast.LENGTH_SHORT
            ).show()
        } else {
        val url = "${ApiFile.APP_URL}/faBatch/updateFaBatchStatus?batchName=$batchName"
        val json = JSONObject().apply {
            put("batchName", batchName)
        }
        Log.d("URL:", url)

        val requestBody =
            json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        Log.d("URL FOR UPDATE:", json.toString())
        val request = Request.Builder()
            .url(url)
            .put(requestBody)
            .addHeader("Content-Type", "application/json")
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@ViewReportsActivity,
                        "Failed to update batch status",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(
                                this@ViewReportsActivity,
                                "Batch status updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            multipleBatchNameSpinner.visibility = View.GONE
                            closeBatch.visibility = View.GONE
                            fetchBatchData()
                        }
                    } else {
                        val responseBody = it.body?.string() ?: ""
                        val errorMessage = if (responseBody.contains("Invalid VIN")) {
                            "Error While Updating"
                        } else {
                            "Unexpected code ${it.code}"
                        }
                        runOnUiThread {
                            Toast.makeText(
                                this@ViewReportsActivity,
                                errorMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        })
    }
    }


    private fun fetchSpinnerData() {
        val url = ApiFile.APP_URL + "/faBatch/batchNameOpen?locId=${locId}"
        val request = Request.Builder().url(url).build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                Log.d("fetchSpinnerData", "Response: $jsonData")

                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    if (jsonObject.has("obj")) {
                        val objArray = jsonObject.getJSONArray("obj")
                        spinnerItems.clear()
                        spinnerItems.add("Select batch name")

                        for (i in 0 until objArray.length()) {
                            val obj = objArray.getJSONObject(i)
                            if (obj.has("batchName")) {
                                val batchName = obj.getString("batchName")
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
        val url = ApiFile.APP_URL + "/faBatch/faAssetDetailsByBatchName?batchName=$batchName"
        val request = Request.Builder().url(url).build()
        Log.d("batchName", batchName)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                Log.d("Response", response.toString())
                val jsonData = response.body?.string()
                Log.d("jsonData", jsonData.toString())
                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    val objArray = jsonObject.getJSONArray("obj")
                    val reportsList = mutableListOf<ReportItem>()

                    for (i in 0 until objArray.length()) {
                        val obj = objArray.getJSONObject(i)
                        val reportItem = ReportItem(
                            srNo = obj.getInt("srno"),
                            batchName = obj.getString("batchName"),
                            prodSrlNo = obj.getString("prodSrlNo"),
                            itemName = obj.getString("itemName"),
                            locId = obj.getString("locId"),
                            batchStatus = obj.getString("batchStatus"),
                            insertedBy = obj.getString("insertedBy"),
                            userName = obj.getString("userName"),
                            unitNo =obj.getString("unitNo"),
                            prodInvNo = obj.getString("prodInvNo"),
                            assetNo = obj.getString("assetNo")
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

        val headers = listOf("Sr.No.","Batch Name", "Prod Sr No","Asset No", "Item Name","Inserted By", "Username","Unit Count","Prod Inv No")
        val maxWidths = MutableList(headers.size) { 0 }
        val textViewPadding = 24 * 2

        for ((index, header) in headers.withIndex()) {
            val headerTextView = createTextView(header, true)
            headerTextView.measure(0, 0)
            maxWidths[index] = headerTextView.measuredWidth + textViewPadding
        }

        for (report in reportsList) {
            val dataRow = listOf(
                report.srNo,report.batchName, report.prodSrlNo,report.assetNo ,report.itemName,report.insertedBy, report.userName,report.unitNo,report.prodInvNo
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
                report.srNo,report.batchName, report.prodSrlNo,report.assetNo,report.itemName,
                report.insertedBy, report.userName,report.unitNo,report.prodInvNo
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
    val batchName:String,
    val prodSrlNo:String,
    val itemName:String,
    val locId:String,
    val batchStatus:String,
    val insertedBy:String,
    val userName:String,
    val unitNo:String,
    val prodInvNo:String,
    val assetNo:String
)
