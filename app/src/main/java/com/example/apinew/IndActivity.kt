package com.example.apinew

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
import java.util.Date
import java.util.Locale

class IndActivity : AppCompatActivity() {

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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ind_activity)

        ouId = intent.getIntExtra("ouId", 0)
        locId = intent.getIntExtra("locId", 0)
        attribute1 = intent.getStringExtra("attribute1") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""
        location = intent.getStringExtra("location") ?: ""
        deptName = intent.getStringExtra("deptName") ?: ""
        rowCountTextView = findViewById(R.id.rowCountTextView)
        rowCountTextView.visibility=View.GONE


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

        val typeOptions = mutableListOf("SELECT VEHICLE TYPE", "NEXA", "ARENA", "ALL")
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

        fetchData.setOnClickListener {
            fetchUninvoice()
            rowCountTextView.visibility=View.GONE
            tableLayout.removeAllViews()
            headerTableLayout.removeAllViews()
        }

        fetchCityData()


        if(deptName!="SUPERADMIN"){
            citySpinner.visibility= View.GONE
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
                            this@IndActivity,
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

    private fun fetchUninvoice() {
        rowCountTextView.visibility = View.GONE
        tableLayout.removeAllViews()
        headerTableLayout.removeAllViews()
        val client = OkHttpClient()
        val todayDate = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date())

        val selectedCity = citySpinner.selectedItem.toString()
        val selectedType = typeSpinner.selectedItem.toString()
        val selectedCityCode = if (selectedCity != "Select City") {
            selectedCity.split("-")[0].trim()
        } else {
            ""
        }

        if (selectedType == "SELECT VEHICLE TYPE") {
            Toast.makeText(
                this@IndActivity,
                "Select vehicle model to proceed!",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val url = if (deptName == "SUPERADMIN") {
                ApiFile.APP_URL + "/accounts/reportIND?ouId=$selectedCityCode&toDate=$todayDate&vehType=$selectedType"
            } else {
                ApiFile.APP_URL + "/accounts/reportIND?ouId=$ouId&toDate=$todayDate&vehType=$selectedType"
            }


//        val url = ApiFile.APP_URL + "/accounts/reportIND?ouId=$ouId&toDate=$todayDate"

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
                                    stockItem.optString("SR_NO", ""),
                                    stockItem.optString("VIN", ""),
                                    stockItem.optString("CHASSIS_NUM", ""),
                                    stockItem.optString("ENGINE_NUM", ""),
                                    stockItem.optString("MODEL_CD", ""),
                                    stockItem.optString("VARIANT_CD", ""),
                                    stockItem.optString("DMSINVNO", ""),
                                    stockItem.optString("SOBNO", ""),
                                    stockItem.optString("DMSINVOICENO", ""),
                                    formatDateTime(stockItem.optString("DMS_INV_DATE", "")),
                                    stockItem.optString("VEH_STATUS", ""),
                                    stockItem.optString("CUST_NAME", ""),
                                    stockItem.optString("EXECUTIVE", ""),
                                    stockItem.optString("TEAM_HEAD", "")
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
            "SR.NO.",
            "VIN",
            "CHASSIS NUM",
            "ENGINE NUM",
            "MODEL CODE",
            "VARIANT CODE",
            "DMS INV NO",
            "SOB NO",
            "DMS INV NO",
            "DMS INV DATE",
            "VEH STATUS",
            "CUSTOMER NAME",
            "EXECUTIVE",
            "TEAM HEAD"
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
