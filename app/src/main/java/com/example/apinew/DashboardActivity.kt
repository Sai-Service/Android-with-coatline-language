package com.example.apinew

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
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
import java.util.concurrent.TimeUnit

class DashboardActivity : AppCompatActivity() {

    private lateinit var fetchDataButton: Button
//    private lateinit var logoutButton: ImageButton
//    private lateinit var redirectButton: Button
    private lateinit var tableLayout: TableLayout
    private lateinit var headerTableLayout: TableLayout
    private lateinit var headerHorizontalScrollView: HorizontalScrollView
    private lateinit var dataHorizontalScrollView: HorizontalScrollView
//    private lateinit var ouIdTextView: TextView
//    private lateinit var attributeTextView: TextView
    private var ouId: Int = 0
    private lateinit var attribute1: String
    private lateinit var progressBar: ProgressBar
    private lateinit var TextProgressBar:TextView
//    private lateinit var homePage: ImageButton
    private lateinit var deptName:String
    private lateinit var citySpinner: Spinner
    private lateinit var rowCountTextView: TextView
    private lateinit var typeSpinner:Spinner


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        ouId = intent.getIntExtra("ouId", 0)
        attribute1 = intent.getStringExtra("attribute1") ?: ""
        deptName = intent.getStringExtra("deptName") ?: ""
        citySpinner=findViewById(R.id.citySpinner)
        rowCountTextView = findViewById(R.id.rowCountTextView)
        rowCountTextView.visibility=View.GONE



//        ouIdTextView = findViewById(R.id.ouIdTextView)
//        ouIdTextView.text = "$ouId"
//        attributeTextView = findViewById(R.id.attributeTextView)
//        attributeTextView.text = "$attribute1"
        fetchDataButton = findViewById(R.id.fetchData)
//        logoutButton = findViewById(R.id.logoutButton)
//        redirectButton = findViewById(R.id.redirectButton)
        tableLayout = findViewById(R.id.tableLayout)
        headerTableLayout = findViewById(R.id.headerTableLayout)
        headerHorizontalScrollView = findViewById(R.id.headerHorizontalScrollView)
        dataHorizontalScrollView = findViewById(R.id.dataHorizontalScrollView)
        progressBar = findViewById(R.id.progressBar)
        TextProgressBar=findViewById(R.id.TextProgressBar)
        TextProgressBar.visibility=View.GONE
        typeSpinner=findViewById(R.id.typeSpinner)

//        val typeOptions = mutableListOf("Choose Vehicle Status")
//        typeOptions.add("NEXA")
//        typeOptions.add("ARENA")
//        typeOptions.add("ALL")
////        val typeOptions = listOf("NEXA", "ARENA", "ALL")
//        val typeAdapter = ArrayAdapter(
//            this,
//            android.R.layout.simple_spinner_item,
//            typeOptions
//        )
//        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        typeSpinner.adapter = typeAdapter



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

        fetchCityData()


        if(deptName!="SUPERADMIN"){
            citySpinner.visibility=GONE
        }

        fetchDataButton.setOnClickListener {
            fetchUninvoice()
            rowCountTextView.visibility=View.GONE
            tableLayout.removeAllViews()
            headerTableLayout.removeAllViews()
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
                            this@DashboardActivity,
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
        tableLayout.removeAllViews()
        headerTableLayout.removeAllViews()
        rowCountTextView.visibility = View.GONE
        val client = OkHttpClient.Builder()
            .connectTimeout(50, TimeUnit.SECONDS)
            .readTimeout(50, TimeUnit.SECONDS)
            .writeTimeout(50, TimeUnit.SECONDS)
            .build()

        val todayDate = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date())
//        val url = ApiFile.APP_URL + "/fndcom/stockDetails?ouId=$ouId"
        val selectedCity = citySpinner.selectedItem.toString()
        val selectedCityCode = if (selectedCity != "Select City") {
            selectedCity.split("-")[0].trim()
        } else {
            ""
        }

        val selectedType = typeSpinner.selectedItem.toString()
        if (selectedType == "SELECT VEHICLE TYPE") {
            Toast.makeText(
                this@DashboardActivity,
                "Select vehicle model to proceed!",
                Toast.LENGTH_SHORT
            ).show()
        } else {

            val url = if (deptName == "SUPERADMIN") {
                ApiFile.APP_URL + "/fndcom/stockDetails?ouId=$selectedCityCode&vehType=$selectedType"
            } else {
                ApiFile.APP_URL + "/fndcom/stockDetails?ouId=$ouId&vehType=$selectedType"
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
                                    stockItem.getString("SR_NO"),
                                    stockItem.getString("VIN"),
                                    stockItem.getString("CHASSIS_NO"),
                                    stockItem.getString("ENGINE_NO"),
                                    stockItem.getString("MODEL_DESC"),
                                    stockItem.getString("FUEL_DESC"),
                                    stockItem.optString("COLOUR"),
                                    stockItem.optString("KEY_NO", "N/A"),
                                    stockItem.optString("GRN_NO", "N/A"),
                                    formatDateTime(stockItem.getString("GRN_DATE")),
                                    stockItem.optString("VEH_STATUS", "N/A"),
                                    stockItem.optString("STATUS", "N/A"),
                                    stockItem.optString("LOCATION", "N/A"),
                                    stockItem.optString("AGEING","N/A"),
//                                stockItem.optString("TRX_NUMBER", "N/A"),
                                    stockItem.optString("CUST_ACC_NO", "N/A"),
                                    stockItem.optString("PARTY_NAME", "N/A")
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
                                TextProgressBar.visibility = View.GONE
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        TextProgressBar.visibility = View.GONE
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
            "SR NO", "VIN", "CHASSIS NO", "ENGINE NO", "MODEL DESC", "FUEL DESC",
             "COLOUR","KEY NO" ,"GRN NO",
            "GRN DATE", "VEH STATUS","STATUS","LOCATION","AGEING",
//            "TRX NUMBER",
            "CUST ACC NO",
            "CUST NAME"
//

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

        private fun logout() {
        Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(this)
        }
        finish()
    }

    private fun redirect() {
        Intent(this, ChassisActivity::class.java).also {
            startActivity(it)
        }
    }

    private fun backToHome() {
        finish()
    }
}

