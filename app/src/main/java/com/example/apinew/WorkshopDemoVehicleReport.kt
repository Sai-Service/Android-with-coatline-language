package com.example.apinew

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ProgressBar
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
    private lateinit var location_name: String
    private lateinit var attribute1: String
    private lateinit var location: String
    private lateinit var deptName: String
    private lateinit var fetchData: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tableLayout: TableLayout
    private lateinit var headerTableLayout: TableLayout
    private lateinit var headerHorizontalScrollView: HorizontalScrollView
    private lateinit var dataHorizontalScrollView: HorizontalScrollView
    private lateinit var TextProgressBar: TextView
    private lateinit var citySpinner: Spinner
    private lateinit var rowCountTextView: TextView
    private lateinit var typeSpinner: Spinner
    private lateinit var fromDatePicker: Button
    private lateinit var toDatePicker: Button
    private var fromDate: String = ""
    private var toDate: String = ""
    private lateinit var fromDateLabel: TextView
    private lateinit var toDateLabel: TextView
    private lateinit var usernameIntent: TextView
    private lateinit var locationIntent: TextView
    private lateinit var loadingOverlay: LinearLayout

    // ── Design palette — consistent with WorkshopAvailableDemoVehicle ───────
    private val COLOR_HEADER_BG     = Color.parseColor("#1C1C1E")
    private val COLOR_HEADER_TEXT   = Color.parseColor("#FFFFFF")
    private val COLOR_HEADER_ACCENT = Color.parseColor("#22C55E")
    private val COLOR_ROW_ODD       = Color.parseColor("#FFFFFF")
    private val COLOR_ROW_EVEN      = Color.parseColor("#F0FDF4")   // very faint green tint
    private val COLOR_TEXT_PRIMARY  = Color.parseColor("#1C1C1E")
    private val COLOR_TEXT_MUTED    = Color.parseColor("#6B7280")
    private val COLOR_DIVIDER       = Color.parseColor("#E5E7EB")
    private val COLOR_SR_BG         = Color.parseColor("#F9FAFB")

    // ── Lifecycle ─────────────────────────────────────────────────────────────
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workshop_demo_vehicle_report)

        ouId          = intent.getIntExtra("ouId", 0)
        locId         = intent.getIntExtra("locId", 0)
        attribute1    = intent.getStringExtra("attribute1") ?: ""
        login_name    = intent.getStringExtra("login_name") ?: ""
        location      = intent.getStringExtra("location") ?: ""
        deptName      = intent.getStringExtra("deptName") ?: ""
        location_name = intent.getStringExtra("location_name") ?: ""

        // View bindings
        rowCountTextView             = findViewById(R.id.rowCountTextView)
        fromDatePicker               = findViewById(R.id.fromDatePicker)
        toDatePicker                 = findViewById(R.id.toDatePicker)
        fromDateLabel                = findViewById(R.id.fromDateLabel)
        toDateLabel                  = findViewById(R.id.toDateLabel)
        usernameIntent               = findViewById(R.id.usernameIntent)
        locationIntent               = findViewById(R.id.locationIntent)
        fetchData                    = findViewById(R.id.fetchData)
        progressBar                  = findViewById(R.id.progressBar)
        tableLayout                  = findViewById(R.id.tableLayout)
        citySpinner                  = findViewById(R.id.citySpinner)
        headerTableLayout            = findViewById(R.id.headerTableLayout)
//        headerHorizontalScrollView   = findViewById(R.id.headerHorizontalScrollView)
//        dataHorizontalScrollView     = findViewById(R.id.dataHorizontalScrollView)
        TextProgressBar              = findViewById(R.id.TextProgressBar)
        typeSpinner                  = findViewById(R.id.typeSpinner)
        loadingOverlay               = findViewById(R.id.loadingOverlay)

        rowCountTextView.visibility = View.GONE
        TextProgressBar.visibility  = View.GONE

        usernameIntent.text = login_name
        locationIntent.text = location_name

        // ── City spinner visibility — SALESSUPERADMIN only ──
        if (deptName != "SALESSUPERADMIN") {
            citySpinner.visibility = View.GONE
            // Also hide its label row
            (citySpinner.parent as? ViewGroup)?.let {
                it.visibility = View.GONE
            }
        }

        // ── Date pickers ──
        val calendar = Calendar.getInstance()
        val dateSetListener = { label: TextView, isFrom: Boolean ->
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selected = String.format(
                        Locale.getDefault(), "%02d-%s-%d",
                        dayOfMonth,
                        SimpleDateFormat("MMM", Locale.getDefault())
                            .format(Date(year - 1900, month, dayOfMonth)),
                        year
                    )
                    label.text = selected
                    label.setTextColor(COLOR_TEXT_PRIMARY)
                    if (isFrom) fromDate = selected else toDate = selected
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        fromDatePicker.setOnClickListener { dateSetListener(fromDateLabel, true) }
        toDatePicker.setOnClickListener   { dateSetListener(toDateLabel, false) }

        // ── Type (Dept) spinner ──
        val typeOptions = mutableListOf("SELECT DEPARTMENT", "ALL", "SERVICE", "DP")
        val typeAdapter = object : ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item, typeOptions
        ) {
            override fun isEnabled(position: Int) = position != 0

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                (view as? TextView)?.apply {
                    setTextColor(COLOR_TEXT_PRIMARY)
                    textSize = 13f
                    typeface = Typeface.DEFAULT_BOLD
                }
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                (view as? TextView)?.apply {
                    setTextColor(
                        if (position == 0) Color.parseColor("#9A9A9F")
                        else Color.parseColor("#F97316")   // orange for dept options
                    )
                    typeface = if (position == 0) Typeface.DEFAULT else Typeface.DEFAULT_BOLD
                    textSize = 13f
                    setPadding(24, 18, 24, 18)
                    setBackgroundColor(COLOR_HEADER_BG)
                }
                return view
            }
        }
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = typeAdapter
        typeSpinner.setSelection(0, false)

        // ── Sync horizontal scrolls (sticky header) ──
//        headerHorizontalScrollView.setOnScrollChangeListener { _, scrollX, _, _, _ ->
//            dataHorizontalScrollView.scrollTo(scrollX, dataHorizontalScrollView.scrollY)
//        }
//        dataHorizontalScrollView.setOnScrollChangeListener { _, scrollX, _, _, _ ->
//            headerHorizontalScrollView.scrollTo(scrollX, headerHorizontalScrollView.scrollY)
//        }

        fetchCityData()

        // ── Fetch button ──
        fetchData.setOnClickListener {
            rowCountTextView.visibility = View.GONE
            tableLayout.removeAllViews()
            headerTableLayout.removeAllViews()
            if (fromDate.isEmpty() || toDate.isEmpty()) {
                Toast.makeText(this, "⚠ Please select both FROM and TO dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            fetchUninvoice()
        }
    }

    // ── City spinner data ─────────────────────────────────────────────────────
    private fun fetchCityData() {
        val client  = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.ERP_APP_URL}/demoVehicleTrans/locationDetails?ouId=$ouId")
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val cities = parseCities(it)
                    runOnUiThread {
                        val adapter = object : ArrayAdapter<String>(
                            this@WorkshopDemoVehicleReport,
                            android.R.layout.simple_spinner_item, cities
                        ) {
                            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                                val view = super.getView(position, convertView, parent)
                                (view as? TextView)?.apply {
                                    setTextColor(COLOR_TEXT_PRIMARY)
                                    textSize = 13f
                                    typeface = Typeface.DEFAULT_BOLD
                                }
                                return view
                            }
                            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                                val view = super.getDropDownView(position, convertView, parent)
                                (view as? TextView)?.apply {
                                    setTextColor(
                                        if (position == 0) Color.parseColor("#9A9A9F")
                                        else COLOR_HEADER_ACCENT
                                    )
                                    typeface = if (position == 0) Typeface.DEFAULT else Typeface.DEFAULT_BOLD
                                    textSize = 13f
                                    setPadding(24, 18, 24, 18)
                                    setBackgroundColor(COLOR_HEADER_BG)
                                }
                                return view
                            }
                        }
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
        return try {
            val jsonObject = JSONObject(jsonData)
            val jsonArray  = jsonObject.getJSONArray("obj")
            cities.add("Select City")
            for (i in 0 until jsonArray.length()) {
                val city = jsonArray.getJSONObject(i)
                cities.add("${city.getString("LOC_ID")}-${city.getString("LOCATION")}")
            }
            cities
        } catch (e: JSONException) {
            e.printStackTrace()
            cities
        }
    }

    // ── Date helper ──────────────────────────────────────────────────────────
    private fun getIncrementedDate(date: String): String {
        return try {
            val fmt = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
            val cal = Calendar.getInstance()
            cal.time = fmt.parse(date)!!
            cal.add(Calendar.DATE, 1)
            fmt.format(cal.time)
        } catch (e: Exception) { date }
    }

    // ── Fetch report data ─────────────────────────────────────────────────────
    private fun fetchUninvoice() {
        rowCountTextView.visibility = View.GONE
        tableLayout.removeAllViews()
        headerTableLayout.removeAllViews()

        if (fromDate.isEmpty() || toDate.isEmpty()) {
            Toast.makeText(this, "Please select both dates", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedCity     = citySpinner.selectedItem?.toString() ?: ""
        val selectedCityCode = if (selectedCity != "Select City" && selectedCity.isNotBlank())
            selectedCity.split("-")[0].trim() else ""

        val incrementedToDate = getIncrementedDate(toDate)

        val url = if (deptName == "SALESSUPERADMIN") {
            "${ApiFile.ERP_APP_URL}/demoVehicleTrans/demoVehReportByOuAndLoc?ouId=$ouId&locId=$selectedCityCode&fromDate=$fromDate&toDate=$incrementedToDate"
        } else {
            "${ApiFile.ERP_APP_URL}/demoVehicleTrans/demoVehReportByOuAndLoc?ouId=$ouId&locId=$locId&fromDate=$fromDate&toDate=$incrementedToDate"
        }

        Log.d("URL->", url)

        val client  = OkHttpClient()
        val request = Request.Builder()
            .url(url).get()
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .build()

        // Show loading
        progressBar.visibility  = View.VISIBLE
        TextProgressBar.visibility = View.VISIBLE
        loadingOverlay.visibility  = View.VISIBLE
        fetchData.isEnabled = false
        fetchData.alpha     = 0.7f

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    runOnUiThread { hideLoading() }
                    return@launch
                }

                val jsonData = response.body?.string()
                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    if (jsonObject.has("obj")) {
                        val jsonArray       = jsonObject.getJSONArray("obj")
                        val summaryDataList = mutableListOf<List<String>>()

                        for (i in 0 until jsonArray.length()) {
                            val item = jsonArray.getJSONObject(i)
                            summaryDataList.add(listOf(
                                item.optString("SR_NO", "").nullSafe(),
                                item.optString("REG_NO", "").nullSafe(),
                                item.optString("CHASSIS_NO", "").nullSafe(),
                                item.optString("MODEL_DESC", "").nullSafe(),
                                item.optString("VARIANT_DESC", "").nullSafe(),
                                item.optString("GATE_PASS_NO", "").nullSafe(),
                                item.optString("SOB_ENQ_DET","").nullSafe(),
                                item.optString("OUT_KM", "").nullSafe(),
                                item.optString("OUT_TIME", "").nullSafe(),
                                item.optString("CUST_NAME", "").nullSafe(),
                                item.optString("CUST_CONTACT_NO", "").nullSafe(),
                                item.optString("CUST_ADDRESS", "").nullSafe(),
                                item.optString("IN_KM", "").nullSafe(),
                                item.optString("IN_TIME", "").nullSafe(),
                                item.optString("LOCATION", "").nullSafe(),
                                item.optString("REMARKS", "").nullSafe(),
                                item.optString("IN_REMARKS", "").nullSafe(),
                                item.optString("FUEL_QTY", "").nullSafe(),
                                item.optString("FUEL_QTY_IN", "").nullSafe(),
                                item.optString("ATTRIBUTE3", "").nullSafe(),
                                item.optString("CREATED_BY", "").nullSafe()
                            ))
                        }

                        runOnUiThread {
                            updateTableView(summaryDataList)
                            hideLoading()
                            rowCountTextView.visibility = View.VISIBLE
                        }
                    } else {
                        runOnUiThread { hideLoading() }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    hideLoading()
                    Toast.makeText(this@WorkshopDemoVehicleReport,
                        "⚠ Failed to load report. Please retry.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun hideLoading() {
        progressBar.visibility     = View.GONE
        TextProgressBar.visibility = View.GONE
        loadingOverlay.visibility  = View.GONE
        fetchData.isEnabled = true
        fetchData.alpha     = 1f
    }

    // ── Table rendering ───────────────────────────────────────────────────────
    private fun updateTableView(stockItems: List<List<String>>) {
        tableLayout.removeAllViews()
        headerTableLayout.removeAllViews()

        val headers = listOf(
            "SR NO", "REG NO", "CHASSIS NO", "MODEL", "VARIANT",
            "GATE PASS","Enquiry/DMS No", "OUT KM", "OUT TIME", "CUST NAME", "CONTACT",
            "ADDRESS", "IN KM", "IN TIME", "LOCATION", "OUT REMARKS","IN REMARKS",
            "FUEL QTY OUT","FUEL QTY IN", "EXECUTIVE", "ATTENDED BY"
        )

        // Measure max widths for each column
        val padding = 24 * 2
        val maxWidths = MutableList(headers.size) { 0 }
        headers.forEachIndexed { i, h ->
            val tv = createHeaderCell(h,i)
            tv.measure(0, 0)
            maxWidths[i] = tv.measuredWidth + padding
        }
        stockItems.forEach { row ->
            row.forEachIndexed { i, data ->
                val tv = createDataCell(data, i, 0)
                tv.measure(0, 0)
                maxWidths[i] = max(maxWidths[i], tv.measuredWidth + padding)
            }
        }

        // Header row
        val headerRow = TableRow(this).apply {
            layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT)
            setBackgroundColor(COLOR_HEADER_BG)
        }
        headers.forEachIndexed { i, h ->
            val tv = createHeaderCell(h,i)
            tv.layoutParams = TableRow.LayoutParams(maxWidths[i], TableRow.LayoutParams.WRAP_CONTENT)
            headerRow.addView(tv)
        }
        headerTableLayout.addView(headerRow)

        // Data rows
        stockItems.forEachIndexed { rowIdx, item ->
            val dataRow = TableRow(this).apply {
                layoutParams = TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT)
                setBackgroundColor(if (rowIdx % 2 == 0) COLOR_ROW_ODD else COLOR_ROW_EVEN)
            }
            item.forEachIndexed { colIdx, data ->
                val tv = createDataCell(data, colIdx, rowIdx)
                tv.layoutParams = TableRow.LayoutParams(maxWidths[colIdx], TableRow.LayoutParams.WRAP_CONTENT)
                dataRow.addView(tv)
            }
            tableLayout.addView(dataRow)

            // Bottom divider
            val divRow = TableRow(this)
            divRow.layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT, 1)
            divRow.setBackgroundColor(COLOR_DIVIDER)
            tableLayout.addView(divRow)
        }

        rowCountTextView.text = "✦  ${stockItems.size} RECORDS FOUND"
    }

    // ── Cell factories ────────────────────────────────────────────────────────

//    private fun createHeaderCell(text: String): TextView {
//        return TextView(this).apply {
//            this.text  = text
//            textSize   = 10.5f
//            typeface   = Typeface.DEFAULT_BOLD
//            gravity    = Gravity.CENTER
//            setPadding(20, 16, 20, 16)
//            setTextColor(COLOR_HEADER_ACCENT)
//            setBackgroundColor(COLOR_HEADER_BG)
//            letterSpacing = 0.04f
//            maxLines = 1
//        }
//    }


    private fun createHeaderCell(text: String, colIndex: Int): TextView {
        return TextView(this).apply {
            this.text  = text
            textSize   = 10.5f
            typeface   = Typeface.DEFAULT_BOLD
            gravity    = gravityForColumn(colIndex)
            setPadding(20, 16, 20, 16)
            setTextColor(COLOR_HEADER_ACCENT)
            setBackgroundColor(COLOR_HEADER_BG)
            letterSpacing = 0.04f
            maxLines = 1
        }
    }

//    private fun createDataCell(text: String, colIndex: Int, rowIndex: Int): TextView {
//        val bgColor = if (rowIndex % 2 == 0) COLOR_ROW_ODD else COLOR_ROW_EVEN
//        return TextView(this).apply {
//            this.text = text
//            textSize  = 10f
//            maxLines  = 1
//            setPadding(20, 14, 20, 14)
//            when (colIndex) {
//                0 -> {  // SR NO — bold, anchored
//                    gravity  = Gravity.CENTER
//                    typeface = Typeface.DEFAULT_BOLD
//                    setTextColor(Color.parseColor("#374151"))
//                    setBackgroundColor(COLOR_SR_BG)
//                }
//                1 -> {  // REG NO — prominent
//                    gravity  = Gravity.CENTER
//                    typeface = Typeface.DEFAULT_BOLD
//                    setTextColor(COLOR_TEXT_PRIMARY)
//                    setBackgroundColor(bgColor)
//                }
//                6, 11 -> {  // OUT KM / IN KM — muted
//                    gravity = Gravity.CENTER
//                    setTextColor(COLOR_TEXT_MUTED)
//                    setBackgroundColor(bgColor)
//                }
//                7, 12 -> {  // OUT TIME / IN TIME — green accent
//                    gravity  = Gravity.CENTER
//                    setTextColor(Color.parseColor("#15803D"))
//                    typeface = Typeface.DEFAULT_BOLD
//                    textSize = 9.5f
//                    setBackgroundColor(bgColor)
//                }
//                else -> {
//                    gravity = Gravity.START or Gravity.CENTER_VERTICAL
//                    setTextColor(COLOR_TEXT_PRIMARY)
//                    setBackgroundColor(bgColor)
//                }
//            }
//        }
//    }


    private fun createDataCell(text: String, colIndex: Int, rowIndex: Int): TextView {
        val bgColor = if (rowIndex % 2 == 0) COLOR_ROW_ODD else COLOR_ROW_EVEN
        return TextView(this).apply {
            this.text = text
            textSize  = 10f
            maxLines  = 1
            setPadding(20, 14, 20, 14)
            gravity = gravityForColumn(colIndex)   // ← now always matches the header
            when (colIndex) {
                0 -> { typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#374151")); setBackgroundColor(COLOR_SR_BG) }
                1 -> { typeface = Typeface.DEFAULT_BOLD; setTextColor(COLOR_TEXT_PRIMARY); setBackgroundColor(bgColor) }
                6, 11 -> { setTextColor(COLOR_TEXT_MUTED); setBackgroundColor(bgColor) }
                7, 12 -> { setTextColor(Color.parseColor("#15803D")); typeface = Typeface.DEFAULT_BOLD; textSize = 9.5f; setBackgroundColor(bgColor) }
                else -> { setTextColor(COLOR_TEXT_PRIMARY); setBackgroundColor(bgColor) }
            }
        }
    }

    // ── Shared column alignment rule ────────────────────────────────────────
    private fun gravityForColumn(colIndex: Int): Int = when (colIndex) {
        0, 1, 6, 7, 11, 12 -> Gravity.CENTER
        else -> Gravity.START or Gravity.CENTER_VERTICAL
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun String?.nullSafe(): String =
        if (this.isNullOrBlank() || this.equals("null", ignoreCase = true)) "—" else this
}