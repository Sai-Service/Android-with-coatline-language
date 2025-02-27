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

class WorkshopStockTransferReport : AppCompatActivity() {

    private var ouId: Int = 0
    private var locId: Int = 0
    private lateinit var login_name: String
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workshop_stock_transfer_report)

        ouId = intent.getIntExtra("ouId", 0)
        locId = intent.getIntExtra("locId", 0)
        attribute1 = intent.getStringExtra("attribute1") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""
        location = intent.getStringExtra("location") ?: ""
        deptName = intent.getStringExtra("deptName") ?: ""
        rowCountTextView = findViewById(R.id.rowCountTextView)
        rowCountTextView.visibility=View.GONE

        fromDatePicker=findViewById(R.id.fromDatePicker)
        toDatePicker=findViewById(R.id.toDatePicker)
        fromDateLabel=findViewById(R.id.fromDateLabel)
        toDateLabel=findViewById(R.id.toDateLabel)

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

        val typeOptions = mutableListOf("SELECT DEPARTMENT", "ALL", "SERVICE", "DP")
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

//        fetchData.setOnClickListener {
//            fetchUninvoice()
//            rowCountTextView.visibility=View.GONE
//            tableLayout.removeAllViews()
//            headerTableLayout.removeAllViews()
//        }

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
                            this@WorkshopStockTransferReport,
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
        val selectedType = typeSpinner.selectedItem.toString()
        val selectedCityCode = if (selectedCity != "Select City") {
            selectedCity.split("-")[0].trim()
        } else {
            ""
        }
        if (selectedType == "SELECT DEPARTMENT") {
            Toast.makeText(
                this@WorkshopStockTransferReport,
                "Select department to proceed!",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val incrementedToDate = getIncrementedDate(toDate)

            val url = if (deptName == "SUPERADMIN") {
                ApiFile.APP_URL + "/srAccounts/wsStkTransDetailsByOu?ou=$selectedCityCode&dept=$selectedType&fromDate=$fromDate&toDate=$incrementedToDate"
            } else {
                ApiFile.APP_URL + "/srAccounts/wsStkTransDetailsByOu?ou=$ouId&dept=$selectedType&fromDate=$fromDate&toDate=$incrementedToDate"
            }

            val request = Request.Builder()
                .url(url)
                .get()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build()
            progressBar.visibility = View.VISIBLE
            TextProgressBar.visibility = View.VISIBLE
            Log.d("URL-->",url)

            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val response = client.newCall(request).execute()

                    if (!response.isSuccessful) {
                        Log.e("API Error", "HTTP Error: ${response.code}")
                        Log.d("API-Failed",url)
                        return@launch
                    }

                    val jsonData = response.body?.string()

                    jsonData?.let {
                        Log.d("JSON Response", it)

                        val jsonObject = JSONObject(it)
                        if (jsonObject.has("obj")) {
                            val jsonArray = jsonObject.getJSONArray("obj")

                            val summaryDataList = mutableListOf<List<String>>()

                            for (i in 0 until jsonArray.length()) {
                                val stockItem = jsonArray.getJSONObject(i)
                                val data = listOf(
                                    stockItem.getString("SR_NO"),
                                    stockItem.optString("JOBCARDNO", ""),
                                    stockItem.getString("DEPT"),
                                    stockItem.optString("REG_NO", ""),
                                    stockItem.optString("VIN", ""),
                                    stockItem.optString("CHASSIS_NO", ""),
                                    stockItem.getString("FROM_LOCATION"),
                                    stockItem.getString("FROM_KM"),
                                    stockItem.getString("TO_LOCATION"),
                                    stockItem.getString("TO_KM"),
                                    stockItem.getString("STOCK_TRF_NO"),
                                    stockItem.getString("MADE_BY"),
                                    formatDateTime(stockItem.getString("STOCK_TRF_DATE")),
                                    stockItem.getString("DRIVER_NAME"),
                                    stockItem.getString("RECEIVED_BY"),
                                    formatDateTime(stockItem.getString("RECD_DATE"))
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
                            Log.e("JSON Response", "Key 'obj' not found in JSON response")
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
    }

    private fun formatDateTime(dateTime: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
            val outputDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val outputTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(dateTime)
            val formattedDate = date?.let { outputDateFormat.format(it) }
            val formattedTime = date?.let { outputTimeFormat.format(it) }
            "$formattedDate "+ "$formattedTime"
        } catch (e: Exception) {
            dateTime
        }
    }

    private fun updateTableView(stockItems: List<List<String>>) {
        tableLayout.removeAllViews()
        headerTableLayout.removeAllViews()
        rowCountTextView.text=""


        val headers = listOf(
            "SR NO",
            "JOB CARD NO",
            "DEPARTMENT",
            "REG N0.",
            "VIN",
            "CHASSIS NUM",
            "FROM LOC", "FROM KM","TO LOC", "TO KM",
            "STK TRF NO","TRANSFERRED BY","STK TRF DATE",
            "DRIVER",
            "RECEIVED BY",
            "RECEIVED DATE"
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
