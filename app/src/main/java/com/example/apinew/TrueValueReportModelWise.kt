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

class TrueValueReportModelWise : AppCompatActivity() {

    private var ouId: Int = 0
    private var locId: Int = 0
    private lateinit var login_name: String
    private lateinit var attribute1: String
    private lateinit var location: String
    private lateinit var fetchData: Button
    private lateinit var tableLayout: TableLayout
    private lateinit var headerTableLayout: TableLayout
    private lateinit var headerHorizontalScrollView: HorizontalScrollView
    private lateinit var dataHorizontalScrollView: HorizontalScrollView
    private lateinit var progressBar: ProgressBar
    private lateinit var modelCountTxt: TextView
    private lateinit var modelCount: TextView
    private lateinit var TextProgressBar: TextView
    private lateinit var citySpinner: Spinner
    private lateinit var deptName: String
    private lateinit var typeSpinner: Spinner


    private var fromDate: String = ""
    private var toDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_true_value_model_wise)

        ouId = intent.getIntExtra("ouId", 0)
        locId = intent.getIntExtra("locId", 0)
        attribute1 = intent.getStringExtra("attribute1") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""
        location = intent.getStringExtra("location") ?: ""
        deptName = intent.getStringExtra("deptName") ?: ""
        progressBar = findViewById(R.id.progressBar)
        modelCountTxt = findViewById(R.id.modelCountTxt)
        modelCount = findViewById(R.id.modelCount)
        modelCount.visibility = View.GONE
        TextProgressBar = findViewById(R.id.TextProgressBar)
        TextProgressBar.visibility = View.GONE
        citySpinner = findViewById(R.id.citySpinner)
        typeSpinner = findViewById(R.id.typeSpinner)


        fetchData = findViewById(R.id.fetchData)
        tableLayout = findViewById(R.id.tableLayout)
        headerTableLayout = findViewById(R.id.headerTableLayout)
        headerHorizontalScrollView = findViewById(R.id.headerHorizontalScrollView)
        dataHorizontalScrollView = findViewById(R.id.dataHorizontalScrollView)

        headerHorizontalScrollView.setOnScrollChangeListener { _, scrollX, _, _, _ ->
            dataHorizontalScrollView.scrollTo(scrollX, dataHorizontalScrollView.scrollY)
        }
        dataHorizontalScrollView.setOnScrollChangeListener { _, scrollX, _, _, _ ->
            headerHorizontalScrollView.scrollTo(scrollX, headerHorizontalScrollView.scrollY)
        }

        fetchCityData()

        if (deptName != "TVSUPERADMIN") {
            citySpinner.visibility = View.GONE
        }


        fetchData.setOnClickListener {
            tableLayout.removeAllViews()
            headerTableLayout.removeAllViews()
            fetchCountWise()

        }

        val typeOptions = mutableListOf("SELECT VEHICLE TYPE", "NEXA", "ARENA", "ALL")
        val typeAdapter =
            object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, typeOptions) {
                override fun isEnabled(position: Int): Boolean {
                    return position != 0
                }

                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
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
                            this@TrueValueReportModelWise, android.R.layout.simple_spinner_item, cities
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

    private fun fetchCountWise() {
        tableLayout.removeAllViews()
        headerTableLayout.removeAllViews()
        val client = OkHttpClient()
        val selectedCity = citySpinner.selectedItem.toString()
        val selectedCityCode = if (selectedCity != "Select City") {
            selectedCity.split("-")[0].trim()
        } else {
            ""
        }

        val url = if (deptName == "TVSUPERADMIN") {
            ApiFile.APP_URL + "/accounts/reportModelCount?ouId=$selectedCityCode"
        } else {
            ApiFile.APP_URL + "/accounts/reportModelCount?ouId=$ouId"
        }

        val url2 = if (deptName == "TVSUPERADMIN") {
            ApiFile.APP_URL + "/accounts/reportAllLocationCount?ouId=$selectedCityCode"
        } else {
            ApiFile.APP_URL + "/accounts/reportAllLocationCount?ouId=$ouId"
        }

        val request = Request.Builder()
            .url(url)
            .build()

        progressBar.visibility = View.VISIBLE
        TextProgressBar.visibility = View.VISIBLE

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                Log.d("json", jsonData.toString())
                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    val jsonArray = jsonObject.getJSONArray("obj")
                    val summaryDataList = mutableListOf<List<String>>()

                    for (i in 0 until jsonArray.length()) {
                        val stockItem = jsonArray.getJSONObject(i)
                        val data = listOf(
                            stockItem.getInt("SR_NO").toString(),
                            stockItem.optString("DESCRIPTION"),
                            stockItem.getInt("INTRANSIT").toString(),
                            stockItem.getInt("STOCK").toString(),
                            stockItem.getInt("STKTRF").toString(),
                            stockItem.getInt("TOTALSTOCK").toString(),
                            stockItem.getInt("UNALLOTED").toString(),
                            stockItem.getInt("ALLOTED").toString(),
                            stockItem.getInt("INVOICE").toString(),
                            stockItem.getInt("FTOTALSTOCK").toString()
                        )
                        summaryDataList.add(data)
                    }

                    val request2 = Request.Builder().url(url2).build()
                    val response2 = client.newCall(request2).execute()
                    val jsonData2 = response2.body?.string()
                    Log.d("json totalstock", jsonData2.toString())

                    var allModelCountData = ""
                    jsonData2?.let {
                        val jsonObject2 = JSONObject(it)
                        val jsonArray2 = jsonObject2.getJSONArray("obj")

                        for (i in 0 until jsonArray2.length()) {
                            val stockItem = jsonArray2.getJSONObject(i)
                            allModelCountData += "${stockItem.getInt("FTOTALSTOCK")}"
                        }
                    }
                    runOnUiThread {
                        updateTableView(summaryDataList)
                        findViewById<TextView>(R.id.modelCountTxt).text = allModelCountData
                        progressBar.visibility = View.GONE
                        modelCount.visibility = View.VISIBLE
                        TextProgressBar.visibility = View.GONE
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

        val headers = listOf(
            "SRNO",
            "DESC",
            "INTRANSIT",
            "STOCK",
            "STK INTRANSIT",
            "TOTALSTK",
            "UNALLOTED",
            "ALLOTED NOT INV",
            "INV NOT DELIVERED",
            "TOTAL STOCK"
        )

        val maxWidths = MutableList(headers.size) { 0 }
        val padding = 110

        headers.forEachIndexed { index, header ->
            val headerTextView = createTextView(header, true)
            headerTextView.measure(0, 0)
            maxWidths[index] = headerTextView.measuredWidth + padding
        }

        stockItems.forEach { row ->
            row.forEachIndexed { index, data ->
                val dataTextView = createTextView(data, false)
                dataTextView.measure(0, 0)
                maxWidths[index] = max(maxWidths[index], dataTextView.measuredWidth + padding)
            }
        }

        val mainHeaderRow = TableRow(this)
        mainHeaderRow.addView(createBlankHeaderTextView(maxWidths[0]))
        mainHeaderRow.addView(createBlankHeaderTextView(maxWidths[1]))
        mainHeaderRow.addView(createHeaderTextView("GROUP A", 4, R.color.purple_200))
        mainHeaderRow.addView(createHeaderTextView("GROUP B", 5, R.color.teal_200))
        headerTableLayout.addView(mainHeaderRow)

        val subHeaderRow = TableRow(this)
        headers.forEachIndexed { index, header ->
            val subHeaderTextView = createTextView(header, true)
            subHeaderTextView.layoutParams = TableRow.LayoutParams(maxWidths[index], TableRow.LayoutParams.WRAP_CONTENT)
            subHeaderRow.addView(subHeaderTextView)
        }
        headerTableLayout.addView(subHeaderRow)
        stockItems.forEach { item ->
            val dataRow = TableRow(this)
            item.forEachIndexed { index, data ->
                val dataTextView = createTextView(data, false)
                dataTextView.layoutParams = TableRow.LayoutParams(maxWidths[index], TableRow.LayoutParams.WRAP_CONTENT)
                dataRow.addView(dataTextView)
            }
            tableLayout.addView(dataRow)
        }
        val totalRow = TableRow(this)
        totalRow.addView(createTextView("TOTAL", true).apply {
            layoutParams = TableRow.LayoutParams(maxWidths[0], TableRow.LayoutParams.WRAP_CONTENT)
        })
        for (i in 1 until headers.size) {
            val total = if (i > 1) stockItems.sumBy { it[i].toIntOrNull() ?: 0 } else 0
            totalRow.addView(createTextView(total.toString(), true).apply {
                layoutParams = TableRow.LayoutParams(maxWidths[i], TableRow.LayoutParams.WRAP_CONTENT)
            })
        }
        tableLayout.addView(totalRow)
    }

    private fun createBlankHeaderTextView(width: Int): TextView {
        return TextView(this).apply {
            text = ""
            layoutParams = TableRow.LayoutParams(width, TableRow.LayoutParams.WRAP_CONTENT)
        }
    }

    private fun createTextView(text: String, isHeader: Boolean): TextView {
        return TextView(this).apply {
            this.text = text
            setPadding(24, 16, 24, 16)
            setTextColor(if (isHeader) ContextCompat.getColor(context, R.color.black) else ContextCompat.getColor(context, R.color.black))
            gravity = if (isHeader || isNumeric(text)) Gravity.CENTER else Gravity.START
            setTypeface(null, if (isHeader) Typeface.BOLD else Typeface.NORMAL)
            setBackgroundResource(if (isHeader) R.drawable.header_cell_background else R.drawable.data_cell_background)
        }
    }

    private fun isNumeric(text: String): Boolean {
        return text.all { it.isDigit() }
    }

    private fun createHeaderTextView(text: String, span: Int, colorResId: Int): TextView {
        return TextView(this).apply {
            this.text = text
            setPadding(24, 16, 24, 16)
            setTextColor(ContextCompat.getColor(context, R.color.black))
            gravity = Gravity.CENTER
            setTypeface(null, Typeface.BOLD)
            setBackgroundColor(ContextCompat.getColor(context, colorResId))
            setBackgroundResource(R.drawable.header_cell_background)
            layoutParams = TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                span.toFloat()
            )
        }
    }

}

