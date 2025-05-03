package com.example.apinew

import android.app.DatePickerDialog
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import kotlin.math.max
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class WorkshopDemoVehicleReport : AppCompatActivity() {

    private var ouId: Int = 0
    private var locId: Int = 0
    private lateinit var login_name: String
    private lateinit var location_name:String
    private lateinit var attribute1: String
    private lateinit var location: String
    private lateinit var fetchData: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tableLayout: TableLayout
    private lateinit var headerTableLayout: TableLayout
    private lateinit var headerHorizontalScrollView: HorizontalScrollView
    private lateinit var dataHorizontalScrollView: HorizontalScrollView
    private lateinit var TextProgressBar:TextView
    private lateinit var deptName:String
    private lateinit var citySpinner: Spinner
    private lateinit var rowCountTextView: TextView
    private lateinit var typeSpinner:Spinner
    private lateinit var fromDatePicker: Button
    private lateinit var toDatePicker: Button
    private var fromDate: String = ""
    private var toDate: String = ""
    private lateinit var fromDateLabel:TextView
    private lateinit var toDateLabel:TextView
    private lateinit var username:TextView
    private lateinit var usernameIntent:TextView
    private lateinit var locationName:TextView
    private lateinit var locationIntent:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workshop_demo_vehicle_report)

        ouId = intent.getIntExtra("ouId", 0)
        locId = intent.getIntExtra("locId", 0)
        attribute1 = intent.getStringExtra("attribute1") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""
        location = intent.getStringExtra("location") ?: ""
        deptName = intent.getStringExtra("deptName") ?: ""
        location_name=intent.getStringExtra("location_name")?:""
        rowCountTextView = findViewById(R.id.rowCountTextView)
        rowCountTextView.visibility=View.GONE

        fromDatePicker=findViewById(R.id.fromDatePicker)
        toDatePicker=findViewById(R.id.toDatePicker)
        fromDateLabel=findViewById(R.id.fromDateLabel)
        toDateLabel=findViewById(R.id.toDateLabel)
        username=findViewById(R.id.username)
        usernameIntent=findViewById(R.id.usernameIntent)
        locationName=findViewById(R.id.locationName)
        locationIntent=findViewById(R.id.locationIntent)

        usernameIntent.text=login_name
        locationIntent.text=location_name


        val calendar = Calendar.getInstance()

        val dateSetListener = { textView: TextView, isFromDate: Boolean ->
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selectedDate = String.format(
                        Locale.getDefault(),
                        "%02d-%s-%d",
                        dayOfMonth,
                        SimpleDateFormat("MMM", Locale.getDefault()).format(Date(year - 1900, month, dayOfMonth)),
                        year
                    )
                    textView.text = selectedDate
                    if (isFromDate) {
                        fromDate = selectedDate
                    } else {
                        toDate = selectedDate
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        fromDatePicker.setOnClickListener {
            dateSetListener(fromDateLabel, true)
        }

        toDatePicker.setOnClickListener {
            dateSetListener(toDateLabel, false)
        }



        fetchData = findViewById(R.id.fetchData)
        progressBar = findViewById(R.id.progressBar)
        tableLayout = findViewById(R.id.tableLayout)
        citySpinner=findViewById(R.id.citySpinner)
        headerTableLayout = findViewById(R.id.headerTableLayout)
        headerHorizontalScrollView = findViewById(R.id.headerHorizontalScrollView)
        dataHorizontalScrollView = findViewById(R.id.dataHorizontalScrollView)
        TextProgressBar=findViewById(R.id.TextProgressBar)
        TextProgressBar.visibility=View.GONE
        typeSpinner=findViewById(R.id.typeSpinner)

        val typeOptions = mutableListOf("SELECT DEPARTMENT","ALL", "SERVICE", "DP")
        val typeAdapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            typeOptions
        ) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view as TextView
                if (position == 0) {
                    textView.setTextColor(ContextCompat.getColor(context, R.color.red))
                } else {
                    textView.setTextColor(ContextCompat.getColor(context, R.color.black))

                }
                return view
            }
        }
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = typeAdapter

        typeSpinner.setSelection(0, false)


        headerHorizontalScrollView.setOnScrollChangeListener { _, scrollX, _, _, _ ->
            dataHorizontalScrollView.scrollTo(scrollX, dataHorizontalScrollView.scrollY)
        }
        dataHorizontalScrollView.setOnScrollChangeListener { _, scrollX, _, _, _ ->
            headerHorizontalScrollView.scrollTo(scrollX, headerHorizontalScrollView.scrollY)
        }


        fetchCityData()


        if(deptName!="SUPERADMIN"){
            citySpinner.visibility= View.GONE
        }

        fetchData.setOnClickListener {
            rowCountTextView.visibility=View.GONE
            tableLayout.removeAllViews()
            headerTableLayout.removeAllViews()
            if (fromDate.isEmpty() || toDate.isEmpty()) {
                Toast.makeText(this, "Please select both dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            fetchUninvoice()
        }
    }


    private fun fetchCityData() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.APP_URL}/fndcom/cmnType?cmnType=City")
            .build()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val cities = parseCities(it)
                    runOnUiThread {
                        val adapter = ArrayAdapter(
                            this@WorkshopDemoVehicleReport,
                            android.R.layout.simple_spinner_item,
                            cities
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        citySpinner.adapter = adapter
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun parseCities(jsonData: String): List<String> {
        val cities = mutableListOf<String>()
        try {
            val jsonObject = JSONObject(jsonData)
            val jsonArray = jsonObject.getJSONArray("obj")
            cities.add("Select City")
            for (i in 0 until jsonArray.length()) {
                val city = jsonArray.getJSONObject(i)
                val code = city.getString("ATTRIBUTE1")
                val desc = city.getString("CMNDESC")
                cities.add("$code-$desc")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return cities
    }

    private fun getIncrementedDate(date: String): String {
        return try {
            val inputFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.time = inputFormat.parse(date)!!
            calendar.add(Calendar.DATE, 1)
            inputFormat.format(calendar.time)
        } catch (e: Exception) {
            date
        }
    }

    private fun fetchUninvoice() {
        rowCountTextView.visibility = View.GONE
        tableLayout.removeAllViews()
        headerTableLayout.removeAllViews()
        val client = OkHttpClient()
        if (fromDate.isEmpty() || toDate.isEmpty()) {
            Toast.makeText(this, "Please select both From Date and To Date", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedCity = citySpinner.selectedItem.toString()
        val selectedCityCode = if (selectedCity != "Select City") {
            selectedCity.split("-")[0].trim()
        }
        else {
            ""
        }
        val incrementedToDate = getIncrementedDate(toDate)

        val url = if (deptName == "SUPERADMIN") {
            ApiFile.APP_URL + "/demoVehicleTrans/demoVehReportByOuAndLoc?ouId=$selectedCityCode&locId=$locId&fromDate=$fromDate&toDate=$incrementedToDate"
        } else {
            ApiFile.APP_URL + "/demoVehicleTrans/demoVehReportByOuAndLoc?ouId=$ouId&locId=$locId&fromDate=$fromDate&toDate=$incrementedToDate"
        }

        val request = Request.Builder()
            .url(url)
            .get()
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .build()
        progressBar.visibility = View.VISIBLE
        TextProgressBar.visibility = View.VISIBLE

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()

                if (!response.isSuccessful) {
                    return@launch
                }

                val jsonData = response.body?.string()

                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    if (jsonObject.has("obj")) {
                        val jsonArray = jsonObject.getJSONArray("obj")

                        val summaryDataList = mutableListOf<List<String>>()

                        for (i in 0 until jsonArray.length()) {
                            val stockItem = jsonArray.getJSONObject(i)
                            val data = listOf(
                                stockItem.optString("SR_NO",""),
                                stockItem.getString("REG_NO"),
                                stockItem.optString("CHASSIS_NO",""),
                                stockItem.optString("MODEL_DESC", ""),
                                stockItem.optString("VARIANT_DESC", ""),
                                stockItem.optString("GATE_PASS_NO",""),
                                stockItem.optString("OUT_KM", ""),
                                stockItem.optString("OUT_TIME", ""),
                                stockItem.optString("CUST_NAME", ""),
                                stockItem.optString("CUST_CONTACT_NO", ""),
                                stockItem.optString("CUST_ADDRESS", ""),
                                stockItem.optString("IN_KM", ""),
                                stockItem.optString("IN_TIME", ""),
                                stockItem.optString("LOCATION", ""),
                                stockItem.optString("REMARKS",""),
                                stockItem.optString("ATTRIBUTE3",""),
                                stockItem.optString("CREATED_BY","")
                                )
                            summaryDataList.add(data)
                        }

                        runOnUiThread {
                            updateTableView(summaryDataList)
                            progressBar.visibility = View.GONE
                            TextProgressBar.visibility = View.GONE
                            rowCountTextView.visibility = View.VISIBLE


                        }
                    } else {
                        runOnUiThread {
                            progressBar.visibility = View.GONE
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    progressBar.visibility = View.GONE
                }
            }
        }

    }


    private fun updateTableView(stockItems: List<List<String>>) {
        tableLayout.removeAllViews()
        headerTableLayout.removeAllViews()
        rowCountTextView.text=""


        val headers = listOf(
            "SR NO",
            "REG NO",
            "CHASSIS NO",
            "MODEL","VARIANT","GATE PASS NO","OUT KM","OUT TIME","CUSTOMER NAME","CUST CONTACT","CUST ADDRESS",
            "IN KM","IN TIME","LOCATION","REMARKS","EXECUTIVE NAME","ATTENDED BY"
        )
        val maxWidths = MutableList(headers.size) { 0 }
        val textViewPadding = 24 * 2

        for ((index, header) in headers.withIndex()) {
            val headerTextView = createTextView(header, true)
            headerTextView.measure(0, 0)
            maxWidths[index] = headerTextView.measuredWidth + textViewPadding
        }

        for (row in stockItems) {
            for ((index, data) in row.withIndex()) {
                val dataTextView = createTextView(data, false)
                dataTextView.measure(0, 0)
                maxWidths[index] =
                    max(maxWidths[index], dataTextView.measuredWidth + textViewPadding)
            }
        }

        val headerRow = TableRow(this)
        headerRow.layoutParams = TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT,
            TableLayout.LayoutParams.WRAP_CONTENT
        )
        for ((index, header) in headers.withIndex()) {
            val headerText = createTextView(header, true)
            headerText.layoutParams =
                TableRow.LayoutParams(maxWidths[index], TableRow.LayoutParams.WRAP_CONTENT)
            headerRow.addView(headerText)
        }
        headerTableLayout.addView(headerRow)

        for (item in stockItems) {
            val dataRow = TableRow(this)
            dataRow.layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
            )
            for ((index, data) in item.withIndex()) {
                val dataText = createTextView(data, false)
                dataText.layoutParams =
                    TableRow.LayoutParams(maxWidths[index], TableRow.LayoutParams.WRAP_CONTENT)
                dataRow.addView(dataText)
            }
            tableLayout.addView(dataRow)

            val rowCount = stockItems.size
            rowCountTextView.text = "Total Rows: $rowCount"
        }
    }

    private fun createTextView(text: String, isHeader: Boolean): TextView {
        val textView = TextView(this)
        textView.text = text
        textView.setPadding(24, 16, 24, 16)
        textView.setTextColor(resources.getColor(R.color.black))
        textView.gravity = Gravity.LEFT
        textView.maxLines = 1
        if (isHeader) {
            textView.setBackgroundResource(R.drawable.header_background)
            textView.setTypeface(null, Typeface.BOLD)
        } else {
            textView.setBackgroundResource(R.drawable.table_row_divider)
        }
        return textView
    }
}
