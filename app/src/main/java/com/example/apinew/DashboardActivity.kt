//package com.example.apinew
//
//import android.content.Intent
//import android.graphics.Typeface
//import android.os.Bundle
//import android.view.View
//import android.widget.*
//import androidx.appcompat.app.AppCompatActivity
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.launch
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import org.json.JSONException
//import org.json.JSONObject
//import kotlin.math.max
//import android.view.ViewGroup
//import okhttp3.Call
//import okhttp3.Callback
//import okhttp3.Response
//import java.util.concurrent.TimeUnit
//
//class DashboardActivity : AppCompatActivity() {
//
//    //    private lateinit var citySpinner: Spinner
//    private lateinit var fetchDataButton: Button
//    private lateinit var logoutButton: ImageButton
//    private lateinit var redirectButton: Button
//    private lateinit var tableLayout: TableLayout
//    private lateinit var headerTableLayout: TableLayout
//    private lateinit var headerHorizontalScrollView: HorizontalScrollView
//    private lateinit var dataHorizontalScrollView: HorizontalScrollView
//    private lateinit var ouIdTextView: TextView
//    private lateinit var attributeTextView: TextView
//    private var ouId: Int = 0
//    private lateinit var attribute1: String
//    private lateinit var progressBar: ProgressBar
//    private lateinit var homePage: ImageButton
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_dashboard)
//
//        ouId = intent.getIntExtra("ouId", 0)
//        attribute1 = intent.getStringExtra("attribute1") ?: ""
//
//
//        ouIdTextView = findViewById(R.id.ouIdTextView)
//        ouIdTextView.text = "$ouId"   //Disaply ouId on page after successful login
//
//        attributeTextView = findViewById(R.id.attributeTextView)
//        attributeTextView.text = "$attribute1" //Disaply city on page after successful login
//
////        citySpinner = findViewById(R.id.citySpinner)
//        fetchDataButton = findViewById(R.id.fetchDataButton)
//        logoutButton = findViewById(R.id.logoutButton)
//        redirectButton = findViewById(R.id.redirectButton)
//        tableLayout = findViewById(R.id.tableLayout)
//        headerTableLayout = findViewById(R.id.headerTableLayout)
//        headerHorizontalScrollView = findViewById(R.id.headerHorizontalScrollView)
//        dataHorizontalScrollView = findViewById(R.id.dataHorizontalScrollView)
//        progressBar = findViewById(R.id.progressBar)
//        homePage=findViewById(R.id.homePage)
//
//        headerHorizontalScrollView.setOnScrollChangeListener { _, scrollX, _, _, _ ->
//            dataHorizontalScrollView.scrollTo(scrollX, dataHorizontalScrollView.scrollY)
//        }
//        dataHorizontalScrollView.setOnScrollChangeListener { _, scrollX, _, _, _ ->
//            headerHorizontalScrollView.scrollTo(scrollX, headerHorizontalScrollView.scrollY)
//        }
//
//        fetchDataButton.setOnClickListener {
//            fetchStockDetails()
//        }
//
//        logoutButton.setOnClickListener {
//            logout()
//        }
//
//        redirectButton.setOnClickListener {
//            redirect()
//        }
//
//        homePage.setOnClickListener {
//            backToHome()
//        }
//
////        fetchCityData()
//
//    }
//
////    private fun fetchCityData() {
////        val client = OkHttpClient()
////        val request = Request.Builder()
////            .url("http://182.73.44.117:8080/ErpAndroid/fndcom/cmnType?cmnType=City")
////            .build()
////        GlobalScope.launch(Dispatchers.IO) {
////            try {
////                val response = client.newCall(request).execute()
////                val jsonData = response.body?.string()
////                jsonData?.let{
////                    val cities = parseCities(it)
////                    runOnUiThread {
////                        val adapter = ArrayAdapter(
////                            this@DashboardActivity,
////                            android.R.layout.simple_spinner_item,
////                            cities
////                        )
////                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
////                        citySpinner.adapter = adapter
////                    }
////                }
////            } catch (e: Exception) {
////                e.printStackTrace()
////            }
////        }
////    }
//
////    private fun parseCities(jsonData: String): List<String> {
////        val cities = mutableListOf<String>()
////        try {
////            val jsonObject = JSONObject(jsonData)
////            val jsonArray = jsonObject.getJSONArray("obj")
////            for (i in 0 until jsonArray.length()) {
////                val code = jsonArray.getJSONObject(i).getString("ATTRIBUTE1")
////                cities.add(code)
////            }
////        } catch (e: JSONException) {
////            e.printStackTrace()
////        }
////        return cities
////    }
//
//    private fun fetchStockDetails() {
////        val selectedCity = citySpinner.selectedItem.toString()
//        val selectedCity = ouIdTextView.text.toString()
//        val selectedAttr = attributeTextView.text.toString()
//
//
//        val client = OkHttpClient.Builder()
//            .connectTimeout(30, TimeUnit.SECONDS)
//            .writeTimeout(30, TimeUnit.SECONDS)
//            .readTimeout(30, TimeUnit.SECONDS)
//            .build()
//
//
////        val client = OkHttpClient()
//        val request = Request.Builder()
//            .url("${ApiFile.APP_URL}/fndcom/stockDetails?ouId=$ouId")
//
////            .url("${ApiFile.APP_URL}/fndcom/stockDetails?attribute1=$selectedCity")
//
//            .build()
//
//        progressBar.visibility = View.VISIBLE
//
//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                val response = client.newCall(request).execute()
//                val jsonData = response.body?.string()
//                jsonData?.let {
//                    val jsonObject = JSONObject(it)
//                    val jsonArray = jsonObject.getJSONArray("obj")
//                    val stockItems = mutableListOf<List<String>>()
//
//                    for (i in 0 until jsonArray.length()) {
//                        val stockItem = jsonArray.getJSONObject(i)
//                        val data = listOf(
//                            stockItem.getString("VIN"),
//                            stockItem.getString("CHASSIS_NO"),
//                            stockItem.getString("ENGINE_NO"),
//                            stockItem.getString("MODEL_DESC"),
//                            stockItem.getString("FUEL_DESC"),
//                            stockItem.optString("VARIANT_CODE", "N/A"),
//                            stockItem.getString("COLOUR_CODE"),
//                            stockItem.getString("COLOUR"),
//                            stockItem.optString("GRN_NO", "N/A"),
//                            stockItem.getString("GRN_DATE"),
//                            stockItem.optString("ALLOTMENT_NO", "N/A"),
//                            stockItem.optString("ALLOTMENT_DT", "N/A"),
//                            stockItem.optString("ORDER_NUMBER", "N/A"),
//                            stockItem.optString("OPERATING_UNIT", "N/A"),
//                            stockItem.optString("VEH_STATUS", "N/A"),
//                            stockItem.optString("DEALER_LOCATION", "N/A"),
//                            stockItem.optString("KEY_NO", "N/A"),
//                            stockItem.optString("TRANS_ID", "N/A"),
//                            stockItem.optString("CUST_ACC_NO", "N/A"),
//                            stockItem.optString("PARTY_NAME", "N/A"),
//                            stockItem.optString("BOOKIN_DATE", "N/A"),
//                            stockItem.optString("TRX_NUMBER", "N/A"),
//                            stockItem.optString("TRX_DATE", "N/A")
//                        )
//                        stockItems.add(data)
//                    }
//
//                    runOnUiThread {
//                        updateTableView(stockItems)
//                        Toast.makeText(this@DashboardActivity, "Data displayed for $selectedAttr", Toast.LENGTH_LONG).show()
//                        progressBar.visibility = View.GONE
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                runOnUiThread {
//                    progressBar.visibility = View.GONE
//                }
//            }
//        }
//    }
//
//
//    private fun updateTableView(stockItems: List<List<String>>) {
////        tableLayout.removeAllViews()
////        headerTableLayout.removeAllViews()
//
//        val headers = listOf(
//            "VIN", "Chassis No", "ENGINE_NO", "MODEL_DESC", "FUEL_DESC",
//            "VARIANT_CODE", "COLOUR_CODE", "COLOUR", "GRN_NO",
//            "GRN_DATE", "ALLOTMENT_NO", "ALLOTMENT_DT", "ORDER_NUMBER",
//            "OPERATING_UNIT", "VEH_STATUS", "DEALER_LOCATION",
//            "KEY_NO", "TRANS_ID", "CUST_ACC_NO", "PARTY_NAME", "BOOKIN_DATE",
//            "TRX_NUMBER", "TRX_DATE"
//        )
//        val maxWidths = MutableList(headers.size) { 0 }
//        val textViewPadding = 24 * 2
//
//        for ((index, header) in headers.withIndex()) {
//            val headerTextView = createTextView(header, true)
//            headerTextView.measure(0, 0)
//            maxWidths[index] = headerTextView.measuredWidth + textViewPadding
//        }
//
//        for (row in stockItems) {
//            for ((index, data) in row.withIndex()) {
//                val dataTextView = createTextView(data, false)
//                dataTextView.measure(0, 0)
//                maxWidths[index] = max(maxWidths[index], dataTextView.measuredWidth + textViewPadding)
//            }
//        }
//
//        val headerRow = TableRow(this)
//        for ((index, header) in headers.withIndex()) {
//            val headerText = createTextView(header, true)
//            headerText.layoutParams = TableRow.LayoutParams(maxWidths[index], TableRow.LayoutParams.WRAP_CONTENT)
//            headerRow.addView(headerText)
//        }
//        headerTableLayout.addView(headerRow)
//
//        for (item in stockItems) {
//            val dataRow = TableRow(this)
//            for ((index, data) in item.withIndex()) {
//                val dataText = createTextView(data, false)
//                dataText.layoutParams = TableRow.LayoutParams(maxWidths[index], TableRow.LayoutParams.WRAP_CONTENT)
//                dataRow.addView(dataText)
//            }
//            tableLayout.addView(dataRow)
//        }
//    }
//
//
//        private fun createTextView(text: String, isHeader: Boolean): TextView {
//        val textView = TextView(this)
//        textView.text = text
//        textView.setPadding(24, 16, 24, 16)
//        textView.setTextColor(resources.getColor(R.color.black))
//        textView.maxLines = 1
//        if (isHeader) {
//            textView.setBackgroundResource(R.drawable.header_background)
//            textView.setTypeface(null,Typeface.BOLD)
//        } else {
//            textView.setBackgroundResource(R.drawable.table_row_divider)
//        }
//        return textView
//    }
//    private fun logout() {
//        val intent = Intent(this@DashboardActivity, MainActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        startActivity(intent)
//        finish()
//    }
//
//    private fun backToHome() {
//        finish()
//    }
//
//    private fun redirect() {
//        val intent = Intent(this@DashboardActivity, ChassisActivity::class.java)
//        intent.putExtra("attribute1", attribute1)
//        startActivity(intent)
//    }
//}
//














//
//package com.example.apinew
//
//import android.content.Intent
//import android.graphics.Typeface
//import android.os.Bundle
//import android.view.View
//import android.widget.*
//import androidx.appcompat.app.AppCompatActivity
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.launch
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import org.json.JSONObject
//import kotlin.math.max
//import java.util.concurrent.TimeUnit
//
//class DashboardActivity : AppCompatActivity() {
//
//    private lateinit var fetchDataButton: Button
//    private lateinit var logoutButton: ImageButton
//    private lateinit var redirectButton: Button
//    private lateinit var tableLayout: TableLayout
//    private lateinit var headerTableLayout: TableLayout
//    private lateinit var headerHorizontalScrollView: HorizontalScrollView
//    private lateinit var dataHorizontalScrollView: HorizontalScrollView
//    private lateinit var ouIdTextView: TextView
//    private lateinit var attributeTextView: TextView
//    private var ouId: Int = 0
//    private lateinit var attribute1: String
//    private lateinit var progressBar: ProgressBar
//    private lateinit var homePage: ImageButton
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_dashboard)
//
//        ouId = intent.getIntExtra("ouId", 0)
//        attribute1 = intent.getStringExtra("attribute1") ?: ""
//
//        ouIdTextView = findViewById(R.id.ouIdTextView)
//        ouIdTextView.text = "$ouId"
//        attributeTextView = findViewById(R.id.attributeTextView)
//        attributeTextView.text = "$attribute1"
//        fetchDataButton = findViewById(R.id.fetchDataButton)
//        logoutButton = findViewById(R.id.logoutButton)
//        redirectButton = findViewById(R.id.redirectButton)
//        tableLayout = findViewById(R.id.tableLayout)
//        headerTableLayout = findViewById(R.id.headerTableLayout)
//        headerHorizontalScrollView = findViewById(R.id.headerHorizontalScrollView)
//        dataHorizontalScrollView = findViewById(R.id.dataHorizontalScrollView)
//        progressBar = findViewById(R.id.progressBar)
//        homePage = findViewById(R.id.homePage)
//
//        headerHorizontalScrollView.setOnScrollChangeListener { _, scrollX, _, _, _ ->
//            dataHorizontalScrollView.scrollTo(scrollX, dataHorizontalScrollView.scrollY)
//        }
//        dataHorizontalScrollView.setOnScrollChangeListener { _, scrollX, _, _, _ ->
//            headerHorizontalScrollView.scrollTo(scrollX, headerHorizontalScrollView.scrollY)
//        }
//
//        fetchDataButton.setOnClickListener { fetchStockDetails() }
//        logoutButton.setOnClickListener { logout() }
//        redirectButton.setOnClickListener { redirect() }
//        homePage.setOnClickListener { backToHome() }
//    }
//
//    private fun fetchStockDetails() {
//        val client = OkHttpClient.Builder()
//            .connectTimeout(30, TimeUnit.SECONDS)
//            .writeTimeout(30, TimeUnit.SECONDS)
//            .readTimeout(30, TimeUnit.SECONDS)
//            .build()
//
//        val request = Request.Builder()
//            .url("${ApiFile.APP_URL}/fndcom/stockDetails?ouId=$ouId")
//            .build()
//
//        progressBar.visibility = View.VISIBLE
//
//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                val response = client.newCall(request).execute()
//                val jsonData = response.body?.string()
//                jsonData?.let {
//                    val jsonObject = JSONObject(it)
//                    val jsonArray = jsonObject.getJSONArray("obj")
//                    val stockItems = mutableListOf<List<String>>()
//
//                    for (i in 0 until jsonArray.length()) {
//                        val stockItem = jsonArray.getJSONObject(i)
//                        val data = listOf(
//                            stockItem.getString("VIN"),
//                            stockItem.getString("CHASSIS_NO"),
//                            stockItem.getString("ENGINE_NO"),
//                            stockItem.getString("MODEL_DESC"),
//                            stockItem.getString("FUEL_DESC"),
//                            stockItem.getString("COLOUR"),
//                            stockItem.optString("GRN_NO", "N/A"),
//                            stockItem.getString("GRN_DATE"),
////                          stockItem.getString("MUL_INV_NO"),
//                      //      stockItem.getString("MUL_INV_DT"),
//                      //      stockItem.getString("STK_REMARK"),
//                      //      stockItem.getString("STOCK_TRF_NO"),
//                       //     stockItem.getString("PURCHASE_PRICE"),
//                            // stockItem.getString("RATE_OF_INTEREST"),
////                            stockItem.optString("ALLOTMENT_NO", "N/A"),
////                            stockItem.optString("ALLOTMENT_DT", "N/A"),
////                            stockItem.optString("ORDER_NUMBER", "N/A"),
////                            stockItem.optString("OPERATING_UNIT", "N/A"),
//                       //     stockItem.optString("NET_BASIC", "N/A"),
//                        //    stockItem.optString("TRANSPORT_AMOUNT", "N/A"),
//                          //  stockItem.optString("SERVICE_CHARGES", "N/A"),
//                           // stockItem.optString("CGST_PAYABLE", "N/A"),
//                           // stockItem.optInt("SGST_PAYABLE").toString(),
//                           // stockItem.optInt("IGST_PAYABLE").toString(),
//                           // stockItem.optInt("CESS_PAYABLE").toString(),
//                           // stockItem.optInt("SPOT_DISCOUNT").toString(),
//                           // stockItem.optInt("DISCOUNT").toString(),
//                            stockItem.optString("VEH_STATUS", "N/A"),
//                            stockItem.optString("DEALER_LOCATION", "N/A"),
////                            stockItem.optString("KEY_NO", "N/A"),
//                            stockItem.optString("CUST_ACC_NO", "N/A"),
//                            stockItem.optString("PARTY_NAME", "N/A"),
//                            stockItem.optString("TRANS_ID", "N/A"),
//                            stockItem.optString("BOOKIN_DATE", "N/A"),
//                            stockItem.optString("TRX_NUMBER", "N/A"),
//                            stockItem.optString("TRX_DATE", "N/A")
//                        )
//                        stockItems.add(data)
//                    }
//
//                    runOnUiThread {
//                        updateTableView(stockItems)
//                        Toast.makeText(this@DashboardActivity, "Data displayed for $attribute1", Toast.LENGTH_LONG).show()
//                        progressBar.visibility = View.GONE
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                runOnUiThread { progressBar.visibility = View.GONE }
//            }
//        }
//    }
//
//    private fun updateTableView(stockItems: List<List<String>>) {
//        tableLayout.removeAllViews()
//        headerTableLayout.removeAllViews()
//
//        val headers = listOf(
//            "VIN", "Chassis No", "ENGINE_NO", "MODEL_DESC", "FUEL_DESC",
//             "COLOUR", "GRN_NO",
//            "GRN_DATE",
//           // "MUL_INV_NO", "MUL_INV_DT", "STK_REMARK", "STOCK_TRF_NO", "PURCHASE_PRICE",
//           // "RATE_OF_INTEREST",
//           // "NET_BASIC", "TRANSPORT_AMOUNT", "SERVICE_CHARGES",
//           // "CGST_PAYABLE", "SGST_PAYABLE", "IGST_PAYABLE", "CESS_PAYABLE",
//           // "SPOT_DISCOUNT", "DISCOUNT",
//            "VEH_STATUS", "DEALER_LOCATION",
////            "KEY_NO",
//            "CUST_ACC_NO", "CUST_NAME","TRANS_ID" ,"BOOKIN_DATE",
//            "TRX_NUMBER", "TRX_DATE"
//        )
//
//        val maxWidths = MutableList(headers.size) { 0 }
//        val textViewPadding = 24 * 2
//
//        for ((index, header) in headers.withIndex()) {
//            val headerTextView = createTextView(header, true)
//            headerTextView.measure(0, 0)
//            maxWidths[index] = headerTextView.measuredWidth + textViewPadding
//        }
//
//        for (row in stockItems) {
//            for ((index, data) in row.withIndex()) {
//                val dataTextView = createTextView(data, false)
//                dataTextView.measure(0, 0)
//                maxWidths[index] = max(maxWidths[index], dataTextView.measuredWidth + textViewPadding)
//            }
//        }
//
//        val headerRow = TableRow(this)
//        for ((index, header) in headers.withIndex()) {
//            val headerText = createTextView(header, true).apply {
//                layoutParams = TableRow.LayoutParams(maxWidths[index], TableRow.LayoutParams.WRAP_CONTENT)
//            }
//            headerRow.addView(headerText)
//        }
//        headerTableLayout.addView(headerRow)
//
//        for (item in stockItems) {
//            val dataRow = TableRow(this)
//            for ((index, data) in item.withIndex()) {
//                val dataText = createTextView(data, false).apply {
//                    layoutParams = TableRow.LayoutParams(maxWidths[index], TableRow.LayoutParams.WRAP_CONTENT)
//                }
//                dataRow.addView(dataText)
//            }
//            tableLayout.addView(dataRow)
//        }
//    }
//
//    private fun createTextView(text: String, isHeader: Boolean): TextView {
//        return TextView(this).apply {
//            this.text = text
//            setPadding(24, 24, 24, 24)
//            setTextColor(if (isHeader) 0xFF000000.toInt() else 0xFF000000.toInt())
//            setTypeface(null, if (isHeader) Typeface.BOLD else Typeface.NORMAL)
//            layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
//        }
//    }
//
//
//
//    private fun logout() {
//        Intent(this, MainActivity::class.java).apply {
//            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(this)
//        }
//        finish()
//    }
//
//    private fun redirect() {
//        Intent(this, ChassisActivity::class.java).also {
//            startActivity(it)
//        }
//    }
//
//    private fun backToHome() {
//        finish()
//    }
//}



//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
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



//    private fun fetchUninvoice() {
//        val client = OkHttpClient()
//        val todayDate = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date())
//        val url = ApiFile.APP_URL + "/fndcom/stockDetails?ouId=$ouId"
//        val request = Request.Builder()
//            .url(url)
//            .get()
//            .header("Accept", "application/json")
//            .header("Content-Type", "application/json")
//            .build()
//        progressBar.visibility=View.VISIBLE
//        TextProgressBar.visibility=View.VISIBLE
//
//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                val response = client.newCall(request).execute()
//
//                if (!response.isSuccessful) {
//                    Log.e("API Error", "HTTP Error: ${response.code}")
//                    return@launch
//                }
//
//                val jsonData = response.body?.string()
//
//                jsonData?.let {
//                    Log.d("JSON Response", it)
//
//                    val jsonObject = JSONObject(it)
//                    if (jsonObject.has("obj")) {
//                        val jsonArray = jsonObject.getJSONArray("obj")
//
//                        val summaryDataList = mutableListOf<List<String>>()
//
//                        for (i in 0 until jsonArray.length()) {
//                            val stockItem = jsonArray.getJSONObject(i)
//                            val data = listOf(
//                                stockItem.getString("VIN"),
//                            stockItem.getString("CHASSIS_NO"),
//                            stockItem.getString("ENGINE_NO"),
//                            stockItem.getString("MODEL_DESC"),
//                            stockItem.getString("FUEL_DESC"),
//                            stockItem.getString("COLOUR"),
//                            stockItem.optString("GRN_NO", "N/A"),
//                            stockItem.getString("GRN_DATE"),
////                          stockItem.getString("MUL_INV_NO"),
//                      //      stockItem.getString("MUL_INV_DT"),
//                      //      stockItem.getString("STK_REMARK"),
//                      //      stockItem.getString("STOCK_TRF_NO"),
//                       //     stockItem.getString("PURCHASE_PRICE"),
//                            // stockItem.getString("RATE_OF_INTEREST"),
////                            stockItem.optString("ALLOTMENT_NO", "N/A"),
////                            stockItem.optString("ALLOTMENT_DT", "N/A"),
////                            stockItem.optString("ORDER_NUMBER", "N/A"),
////                            stockItem.optString("OPERATING_UNIT", "N/A"),
//                       //     stockItem.optString("NET_BASIC", "N/A"),
//                        //    stockItem.optString("TRANSPORT_AMOUNT", "N/A"),
//                          //  stockItem.optString("SERVICE_CHARGES", "N/A"),
//                           // stockItem.optString("CGST_PAYABLE", "N/A"),
//                           // stockItem.optInt("SGST_PAYABLE").toString(),
//                           // stockItem.optInt("IGST_PAYABLE").toString(),
//                           // stockItem.optInt("CESS_PAYABLE").toString(),
//                           // stockItem.optInt("SPOT_DISCOUNT").toString(),
//                           // stockItem.optInt("DISCOUNT").toString(),
//                            stockItem.optString("VEH_STATUS", "N/A"),
//                            stockItem.optString("DEALER_LOCATION", "N/A"),
////                            stockItem.optString("KEY_NO", "N/A"),
//                            stockItem.optString("CUST_ACC_NO", "N/A"),
//                            stockItem.optString("PARTY_NAME", "N/A")
////                            stockItem.optString("TRANS_ID", "N/A"),
////                            stockItem.optString("BOOKIN_DATE", "N/A"),
////                            stockItem.optString("TRX_NUMBER", "N/A"),
////                            stockItem.optString("TRX_DATE", "N/A")
//                            )
//                            summaryDataList.add(data)
//                        }
//
//
//                        runOnUiThread {
//                            updateTableView(summaryDataList)
//                            progressBar.visibility=View.GONE
//                            TextProgressBar.visibility=View.GONE
//
//                        }
//                    } else {
//                        Log.e("JSON Response", "Key 'obj' not found in JSON response")
//                        runOnUiThread {
//                            progressBar.visibility=View.GONE
//                            TextProgressBar.visibility=View.GONE
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                runOnUiThread {
//                }
//            }
//        }
//    }


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
            Log.d("fetchUninvoice", "Selected City: $selectedCity")
            Log.d("fetchUninvoice", "Selected City Code: $selectedCityCode")
//        val selectedCity = citySpinner.selectedItem.toString()

            val url = if (deptName == "SUPERADMIN") {
                ApiFile.APP_URL + "/fndcom/stockDetails?ouId=$selectedCityCode&vehType=$selectedType"
            } else {
                ApiFile.APP_URL + "/fndcom/stockDetails?ouId=$ouId&vehType=$selectedType"
            }

            Log.d("fetchUninvoice", "Constructed URL: $url")
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
                        Log.e("API Error", "HTTP Error: ${response.code}")
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
                            Log.e("JSON Response", "Key 'obj' not found in JSON response")
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

