//package com.example.apinew
//
//import android.graphics.Color
//import android.graphics.Typeface
//import android.os.Bundle
//import android.util.Log
//import android.view.Gravity
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ArrayAdapter
//import android.widget.LinearLayout
//import android.widget.Spinner
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.launch
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import org.json.JSONArray
//import org.json.JSONException
//import org.json.JSONObject
//import java.net.HttpURLConnection
//import java.net.URL
//import kotlin.concurrent.thread
//
//class WorkshopAvailableDemoVehicle : AppCompatActivity() {
//
//    private lateinit var login_name: String
//    private lateinit var deptName: String
//    private var ouId: Int = 0
//    private var locId: Int = 0
//    private lateinit var location_name: String
//    private lateinit var attribute1: String
//    private lateinit var location: String
//    private lateinit var username: TextView
//    private lateinit var locationName: TextView
//    private lateinit var tableLayout: LinearLayout
//    private lateinit var citySpinner: Spinner
//    private lateinit var refreshButton: TextView
//    private lateinit var cityTextView: TextView
//    private lateinit var labelView: View
//    private lateinit var spinnerLableView : View
//
//    // ── Column definitions ──────────────────────────────────────────────────
//    private val columnHeaders = listOf("SR", "VEHICLE NO", "MODEL","STATUS", "OUT TIME", "CREATED BY","EXECUTIVE NAME", "CUST NAME", "ADDRESS")
//    private val columnWeights = floatArrayOf(0.45f, 1.1f, 1.15f, 1.1f, 1.0f, 1.1f, 1.3f)
//
//    // ── Palette (ZERO blue) ─────────────────────────────────────────────────
//    private val COLOR_HEADER_BG      = Color.parseColor("#1C1C1E")
//    private val COLOR_HEADER_TEXT    = Color.parseColor("#FFFFFF")
//    private val COLOR_HEADER_ACCENT  = Color.parseColor("#22C55E")
//    private val COLOR_ROW_ODD        = Color.parseColor("#FFFFFF")
//    private val COLOR_ROW_EVEN       = Color.parseColor("#F8FAFB")
//    private val COLOR_TEXT_PRIMARY   = Color.parseColor("#1C1C1E")
//    private val COLOR_TEXT_SECONDARY = Color.parseColor("#6B7280")
//    private val COLOR_SR_TEXT        = Color.parseColor("#374151")
//    private val COLOR_DIVIDER        = Color.parseColor("#E5E7EB")
//    private val STATUS_OUT_DEMO_BG   = Color.parseColor("#FFF7ED")
//    private val STATUS_OUT_DEMO_TEXT = Color.parseColor("#C2410C")
//    private val STATUS_AVAIL_BG      = Color.parseColor("#F0FDF4")
//    private val STATUS_AVAIL_TEXT    = Color.parseColor("#15803D")
//    private val STATUS_BOOKED_BG     = Color.parseColor("#FEF2F2")
//    private val STATUS_BOOKED_TEXT   = Color.parseColor("#B91C1C")
//    private val STATUS_SERVICE_BG    = Color.parseColor("#F5F3FF")
//    private val STATUS_SERVICE_TEXT  = Color.parseColor("#7C3AED")
//    private val STATUS_DEFAULT_BG    = Color.parseColor("#F3F4F6")
//    private val STATUS_DEFAULT_TEXT  = Color.parseColor("#374151")
//
//    // ── Lifecycle ────────────────────────────────────────────────────────────
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_workshop_available_demo_vehicle)
//
//        locId         = intent.getIntExtra("locId", 0)
//        ouId          = intent.getIntExtra("ouId", 0)
//        deptName      = intent.getStringExtra("deptName") ?: ""
//        location_name = intent.getStringExtra("location_name") ?: ""
//        login_name    = intent.getStringExtra("login_name") ?: ""
//        location      = intent.getStringExtra("location") ?: ""
//        attribute1    = intent.getStringExtra("attribute1") ?: ""
//
//        username      = findViewById(R.id.username)
//        locationName  = findViewById(R.id.locIdTxt)
//        tableLayout   = findViewById(R.id.tableLayout)
//        citySpinner   = findViewById(R.id.citySpinner)
//        refreshButton = findViewById(R.id.refreshButton)
//        cityTextView  = findViewById(R.id.cityTextView)
//        labelView = findViewById(R.id.labelView)
//        spinnerLableView = findViewById(R.id.spinnerLableView)
//
//        username.text     = login_name
//        locationName.text = location_name
//
//        // Show spinner only for SUPERADMIN; others use location_name directly
//        fetchCityData()
//
//        if (deptName == "SALESSUPERADMIN") {
//            citySpinner.visibility = View.VISIBLE
//            cityTextView.visibility = View.VISIBLE
//            spinnerLableView.visibility=View.VISIBLE
//            labelView.visibility=View.VISIBLE
//            citySpinner.isEnabled  = true
//            citySpinner.alpha      = 1f
//        } else {
//            citySpinner.visibility = View.GONE
//            cityTextView.visibility = View.GONE
//            spinnerLableView.visibility=View.GONE
//            labelView.visibility=View.GONE
//            citySpinner.isEnabled  = false
//        }
//
//        refreshButton.setOnClickListener {
//            // Button press feedback
//            it.alpha = 0.75f
//            it.postDelayed({ it.alpha = 1f }, 180)
//
//            // ── KEY FIX: Clear table immediately, show loading, then fetch ──
//            clearTableAndShowLoading()
//            fetchDemoVehicleStatus()
//        }
//    }
//
//    // ── Table clear + loading placeholder ───────────────────────────────────
//
//    /**
//     * Clears all previous rows and shows a "Loading…" spinner row
//     * so the user immediately sees feedback when they tap the button.
//     */
//    private fun clearTableAndShowLoading() {
//        tableLayout.removeAllViews()   // ← wipes all previous data
//
//        // Header still shows while loading
//        addTableHeader()
//
//        // Loading row
//        val loadingRow = LinearLayout(this).apply {
//            layoutParams = LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//            orientation = LinearLayout.HORIZONTAL
//            gravity = Gravity.CENTER
//            setPadding(0, 40, 0, 40)
//            setBackgroundColor(Color.WHITE)
//        }
//        val loadingText = TextView(this).apply {
//            text      = "⏳  Fetching vehicle status…"
//            textSize  = 13f
//            setTextColor(Color.parseColor("#9A9A9F"))
//            gravity   = Gravity.CENTER
//            layoutParams = LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//        }
//        loadingRow.addView(loadingText)
//        tableLayout.addView(loadingRow)
//
//        // Disable button while loading
//        refreshButton.isClickable = false
//        refreshButton.text        = "⏳  LOADING…"
//        refreshButton.alpha       = 0.7f
//    }
//
//    /** Re-enables the button after data loads */
//    private fun resetButton() {
//        refreshButton.isClickable = true
//        refreshButton.text        = "⟳   CHECK AVAILABILITY"
//        refreshButton.alpha       = 1f
//    }
//
//    // ── Network — City Spinner ───────────────────────────────────────────────
//    private fun fetchCityData() {
//        val client  = OkHttpClient()
//        val request = Request.Builder()
//            .url("${ApiFile.ERP_APP_URL}/demoVehicleTrans/locationDetails?ouId=$ouId")
//            .build()
//        Log.d("URL->",request.toString())
//
//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                val response = client.newCall(request).execute()
//                val jsonData = response.body?.string()
//                Log.d("jsonData",jsonData.toString())
//                Log.d("response",response.toString())
//                jsonData?.let {
//                    val cities = parseCities(it)
//                    runOnUiThread {
//                        // Custom adapter — dark popup background, green accent text
//                        val adapter = object : ArrayAdapter<String>(
//                            this@WorkshopAvailableDemoVehicle,
//                            android.R.layout.simple_spinner_item,
//                            cities
//                        ) {
//                            override fun getView(position: Int, convertView: android.view.View?, parent: ViewGroup): android.view.View {
//                                val view = super.getView(position, convertView, parent)
//                                (view as? TextView)?.apply {
//                                    setTextColor(Color.parseColor("#1C1C1E"))
//                                    textSize = 13f
//                                    typeface = Typeface.DEFAULT_BOLD
//                                    setPadding(4, 0, 0, 0)
//                                }
//                                return view
//                            }
//
//                            override fun getDropDownView(position: Int, convertView: android.view.View?, parent: ViewGroup): android.view.View {
//                                val view = super.getDropDownView(position, convertView, parent)
//                                (view as? TextView)?.apply {
//                                    // Highlight first "Select City" item differently
//                                    if (position == 0) {
//                                        setTextColor(Color.parseColor("#9A9A9F"))
//                                        typeface = Typeface.DEFAULT
//                                    } else {
//                                        setTextColor(Color.parseColor("#22C55E"))
//                                        typeface = Typeface.DEFAULT_BOLD
//                                    }
//                                    textSize = 13f
//                                    setPadding(24, 18, 24, 18)
//                                    setBackgroundColor(Color.parseColor("#1C1C1E"))
//                                }
//                                return view
//                            }
//                        }
//                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                        citySpinner.adapter = adapter
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    private fun parseCities(jsonData: String): List<String> {
//        val cities = mutableListOf<String>()
//        return try {
//            val jsonObject = JSONObject(jsonData)
//            val jsonArray  = jsonObject.getJSONArray("obj")
//            cities.add("Select City")
//            for (i in 0 until jsonArray.length()) {
//                val city = jsonArray.getJSONObject(i)
////                val code = city.getString("LOC_ID")
//                val desc = city.getString("LOCATION")
////                cities.add("$code-$desc")
//                cities.add("$desc")
//            }
//            cities
//        } catch (e: JSONException) {
//            e.printStackTrace()
//            cities
//        }
//    }
//
//    // ── Network — Vehicle Status ─────────────────────────────────────────────
//    private fun fetchDemoVehicleStatus() {
//        // SUPERADMIN picks city from spinner; everyone else uses their own location_name
//        val locationParam = if (deptName == "SALESSUPERADMIN") {
//            val selectedCity = citySpinner.selectedItem?.toString() ?: ""
//            if (selectedCity != "Select City" && selectedCity.isNotBlank())
//                selectedCity.trim()   // ← no more split("-"), spinner item IS the city name
//            else ""
//        } else {
//            location_name
//        }
//
//        thread {
//            try {
//                val url = URL("${ApiFile.ERP_APP_URL}/demoVehicleTrans/demoVehStatusList?ouId=$ouId&location=$locationParam")
//                Log.d("URLCHECK->", url.toString())
//                val connection    = url.openConnection() as HttpURLConnection
//                connection.requestMethod = "GET"
//                val response      = connection.inputStream.bufferedReader().readText()
//                val json          = JSONObject(response)
//                val dataArray     = json.getJSONArray("obj")
//
//                runOnUiThread {
//                    // ── Clear again (removes loading row) and render fresh data ──
//                    tableLayout.removeAllViews()
//                    addTableHeader()
//
//                    if (dataArray.length() == 0) {
//                        showEmptyState()
//                    } else {
//                        addTableData(dataArray)
//                    }
//                    resetButton()
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                runOnUiThread {
//                    tableLayout.removeAllViews()
//                    showErrorState()
//                    resetButton()
//                }
//            }
//        }
//    }
//
//    // ── Empty / Error states ─────────────────────────────────────────────────
//
//    private fun showEmptyState() {
//        val row = buildStateRow("🚗  No vehicles found for this location.", "#9A9A9F")
//        tableLayout.addView(row)
//    }
//
//    private fun showErrorState() {
//        val row = buildStateRow("⚠️  Failed to load data. Please try again.", "#C2410C")
//        tableLayout.addView(row)
//    }
//
//    private fun buildStateRow(message: String, textColor: String): LinearLayout {
//        val row = LinearLayout(this).apply {
//            layoutParams = LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//            gravity   = Gravity.CENTER
//            setPadding(0, 50, 0, 50)
//            setBackgroundColor(Color.WHITE)
//        }
//        val tv = TextView(this).apply {
//            text     = message
//            textSize = 13f
//            setTextColor(Color.parseColor(textColor))
//            gravity  = Gravity.CENTER
//        }
//        row.addView(tv)
//        return row
//    }
//
//    // ── Table building ───────────────────────────────────────────────────────
//    private fun addTableHeader() {
//        tableLayout.addView(buildHeaderRow())
//    }
//
//    private fun addTableData(dataArray: JSONArray) {
//        for (i in 0 until dataArray.length()) {
//            val obj         = dataArray.getJSONObject(i)
//            val srNo        = (i + 1).toString()
//            val instanceNo  = obj.optString("INSTANCE_NUMBER").nullSafe()
//            val modelDesc   = obj.optString("MODEL_DESC").nullSafe()
//            val status      = obj.optString("STATUS").nullSafe()
//            val outTime     = formatDateTime(obj.optString("OUT_TIME").nullSafe())
//            val executive   = obj.optString("CREATED_BY").nullSafe()
//            val exeName     = obj.optString("ATTRIBUTE3").nullSafe()
//            val custName    = obj.optString("CUST_NAME").nullSafe()
//            val custAddress = obj.optString("CUST_ADDRESS").nullSafe()
//
//            val row = buildDataRow(
//                values   = listOf(srNo, instanceNo,modelDesc, status, outTime, executive,exeName, custName, custAddress),
//                rowIndex = i,
//                status   = status
//            )
//            tableLayout.addView(row)
//
//            // Divider
//            val divider = android.view.View(this).apply {
//                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1)
//                setBackgroundColor(COLOR_DIVIDER)
//            }
//            tableLayout.addView(divider)
//        }
//    }
//
//    // ── Row builders ─────────────────────────────────────────────────────────
//
//    private fun buildHeaderRow(): LinearLayout {
//        val row = LinearLayout(this).apply {
//            layoutParams = LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//            orientation = LinearLayout.HORIZONTAL
//            setBackgroundColor(COLOR_HEADER_BG)
//        }
//        columnHeaders.forEachIndexed { index, title ->
//            val weight = if (index < columnWeights.size) columnWeights[index] else 1f
//            val tv = TextView(this).apply {
//                text          = title
//                textSize      = 10.5f
//                typeface      = Typeface.DEFAULT_BOLD
//                gravity       = Gravity.CENTER
//                setTextColor(if (index == 1 || index == 2) COLOR_HEADER_ACCENT else COLOR_HEADER_TEXT)
//                setPadding(10, 16, 10, 16)
//                letterSpacing = 0.05f
//                layoutParams  = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, weight)
//            }
//            row.addView(tv)
//            if (index < columnHeaders.size - 1) {
//                val sep = android.view.View(this).apply {
//                    layoutParams = LinearLayout.LayoutParams(1, ViewGroup.LayoutParams.MATCH_PARENT)
//                    setBackgroundColor(Color.parseColor("#2E2E30"))
//                }
//                row.addView(sep)
//            }
//        }
//        return row
//    }
//
//    private fun buildDataRow(values: List<String>, rowIndex: Int, status: String): LinearLayout {
//        val bgColor = if (rowIndex % 2 == 0) COLOR_ROW_ODD else COLOR_ROW_EVEN
//        val row = LinearLayout(this).apply {
//            layoutParams = LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//            orientation = LinearLayout.HORIZONTAL
//            setBackgroundColor(bgColor)
//        }
//        values.forEachIndexed { index, text ->
//            val weight = if (index < columnWeights.size) columnWeights[index] else 1f
//            when (index) {
//                0 -> row.addView(buildCell(text, weight).apply {
//                    setTextColor(COLOR_SR_TEXT)
//                    typeface = Typeface.DEFAULT_BOLD
//                    textSize = 12f
//                    setBackgroundColor(Color.parseColor("#F9FAFB"))
//                })
//                1 -> row.addView(buildCell(text, weight).apply {
//                    setTextColor(COLOR_TEXT_PRIMARY)
//                    typeface = Typeface.DEFAULT_BOLD
//                    textSize = 10.5f
//                })
//                2 -> {
//                    val (chipBg, chipText) = getStatusColors(text)
//                    val wrapper = LinearLayout(this).apply {
//                        layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, weight)
//                        gravity = Gravity.CENTER
//                        setBackgroundColor(bgColor)
//                    }
//                    val chip = TextView(this).apply {
//                        this.text = text
//                        textSize  = 9f
//                        typeface  = Typeface.DEFAULT_BOLD
//                        gravity   = Gravity.CENTER
//                        setTextColor(chipText)
//                        setBackgroundColor(chipBg)
//                        setPadding(10, 6, 10, 6)
//                    }
//                    wrapper.addView(chip)
//                    row.addView(wrapper)
//                }
//                3 -> row.addView(buildCell(text, weight).apply {
//                    setTextColor(COLOR_TEXT_SECONDARY)
//                    textSize = 9.5f
//                })
//                else -> row.addView(buildCell(text, weight).apply {
//                    setTextColor(COLOR_TEXT_PRIMARY)
//                    textSize = 10f
//                })
//            }
//            if (index < values.size - 1) {
//                val sep = android.view.View(this).apply {
//                    layoutParams = LinearLayout.LayoutParams(1, ViewGroup.LayoutParams.MATCH_PARENT)
//                    setBackgroundColor(COLOR_DIVIDER)
//                }
//                row.addView(sep)
//            }
//        }
//        return row
//    }
//
//    private fun buildCell(text: String, weight: Float): TextView {
//        return TextView(this).apply {
//            this.text = text
//            textSize  = 10f
//            gravity   = Gravity.CENTER
//            setPadding(10, 14, 10, 14)
//            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, weight)
//        }
//    }
//
//    // ── Helpers ──────────────────────────────────────────────────────────────
//
//    private fun getStatusColors(status: String): Pair<Int, Int> = when {
//        status.contains("OUT", ignoreCase = true)     -> STATUS_OUT_DEMO_BG to STATUS_OUT_DEMO_TEXT
//        status.contains("AVAIL", ignoreCase = true)   -> STATUS_AVAIL_BG    to STATUS_AVAIL_TEXT
//        status.contains("BOOK", ignoreCase = true)    -> STATUS_BOOKED_BG   to STATUS_BOOKED_TEXT
//        status.contains("SERVICE", ignoreCase = true) -> STATUS_SERVICE_BG  to STATUS_SERVICE_TEXT
//        else                                           -> STATUS_DEFAULT_BG  to STATUS_DEFAULT_TEXT
//    }
//
//    private fun String?.nullSafe(): String =
//        if (this.isNullOrBlank() || this.equals("null", ignoreCase = true)) "—" else this
//
//    /** "2025-06-18T10:17:03.000+0000" → "18 Jun\n10:17" */
//    private fun formatDateTime(raw: String): String {
//        if (raw == "—" || raw.isBlank()) return "—"
//        return try {
//            val datePart = raw.substringBefore("T")
//            val timePart = raw.substringAfter("T").take(5)
//            val (_, month, day) = datePart.split("-")
//            val months = arrayOf("", "Jan", "Feb", "Mar", "Apr", "May",
//                "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
//            "$day ${months[month.toInt()]}\n$timePart"
//        } catch (e: Exception) {
//            raw.take(16)
//        }
//    }
//}











/////////////////////////////////////////////////////////////////

package com.example.apinew

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class WorkshopAvailableDemoVehicle : AppCompatActivity() {

    private lateinit var login_name: String
    private lateinit var deptName: String
    private var ouId: Int = 0
    private var locId: Int = 0
    private lateinit var location_name: String
    private lateinit var attribute1: String
    private lateinit var attribute2: String
    private lateinit var location: String
    private lateinit var username: TextView
    private lateinit var locationName: TextView
    private lateinit var tableLayout: LinearLayout
    private lateinit var citySpinner: Spinner
    private lateinit var refreshButton: TextView
    private lateinit var cityTextView: TextView
    private lateinit var labelView: View
    private lateinit var spinnerLableView : View

    // Location actually used for the last fetched list (matters for SALESSUPERADMIN,
    // where the spinner-selected city can differ from the logged-in user's own location_name).
    private var currentLocationParam: String = ""

    // ── Column definitions ──────────────────────────────────────────────────
    // "SELECT" sits right after VEHICLE NO (not at the far end) so users can pick the
    // right row without scrolling across the whole table to reach the action button.
    private val columnHeaders = listOf("SR", "VEHICLE NO", "SELECT", "MODEL","STATUS", "OUT TIME", "CREATED BY","EXECUTIVE NAME", "CUST NAME", "ADDRESS")
    private val columnWeights = floatArrayOf(0.45f, 1.1f, 0.9f, 1.15f, 1.1f, 1.0f, 1.1f, 1.3f, 1.0f, 1.3f)

    // ── Palette (ZERO blue) ─────────────────────────────────────────────────
    private val COLOR_HEADER_BG      = Color.parseColor("#1C1C1E")
    private val COLOR_HEADER_TEXT    = Color.parseColor("#FFFFFF")
    private val COLOR_HEADER_ACCENT  = Color.parseColor("#22C55E")
    private val COLOR_ROW_ODD        = Color.parseColor("#FFFFFF")
    private val COLOR_ROW_EVEN       = Color.parseColor("#F8FAFB")
    private val COLOR_TEXT_PRIMARY   = Color.parseColor("#1C1C1E")
    private val COLOR_TEXT_SECONDARY = Color.parseColor("#6B7280")
    private val COLOR_SR_TEXT        = Color.parseColor("#374151")
    private val COLOR_DIVIDER        = Color.parseColor("#E5E7EB")
    private val STATUS_OUT_DEMO_BG   = Color.parseColor("#FFF7ED")
    private val STATUS_OUT_DEMO_TEXT = Color.parseColor("#C2410C")
    private val STATUS_AVAIL_BG      = Color.parseColor("#F0FDF4")
    private val STATUS_AVAIL_TEXT    = Color.parseColor("#15803D")
    private val STATUS_BOOKED_BG     = Color.parseColor("#FEF2F2")
    private val STATUS_BOOKED_TEXT   = Color.parseColor("#B91C1C")
    private val STATUS_SERVICE_BG    = Color.parseColor("#F5F3FF")
    private val STATUS_SERVICE_TEXT  = Color.parseColor("#7C3AED")
    private val STATUS_DEFAULT_BG    = Color.parseColor("#F3F4F6")
    private val STATUS_DEFAULT_TEXT  = Color.parseColor("#374151")

    // ── Lifecycle ────────────────────────────────────────────────────────────
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workshop_available_demo_vehicle)

        locId         = intent.getIntExtra("locId", 0)
        ouId          = intent.getIntExtra("ouId", 0)
        deptName      = intent.getStringExtra("deptName") ?: ""
        location_name = intent.getStringExtra("location_name") ?: ""
        login_name    = intent.getStringExtra("login_name") ?: ""
        location      = intent.getStringExtra("location") ?: ""
        attribute1    = intent.getStringExtra("attribute1") ?: ""
        attribute2    = intent.getStringExtra("attribute2") ?: ""

        username      = findViewById(R.id.username)
        locationName  = findViewById(R.id.locIdTxt)
        tableLayout   = findViewById(R.id.tableLayout)
        citySpinner   = findViewById(R.id.citySpinner)
        refreshButton = findViewById(R.id.refreshButton)
        cityTextView  = findViewById(R.id.cityTextView)
        labelView = findViewById(R.id.labelView)
        spinnerLableView = findViewById(R.id.spinnerLableView)

        username.text     = login_name
        locationName.text = location_name

        // Show spinner only for SUPERADMIN; others use location_name directly
        fetchCityData()

        if (deptName == "SALESSUPERADMIN") {
            citySpinner.visibility = View.VISIBLE
            cityTextView.visibility = View.VISIBLE
            spinnerLableView.visibility=View.VISIBLE
            labelView.visibility=View.VISIBLE
            citySpinner.isEnabled  = true
            citySpinner.alpha      = 1f
        } else {
            citySpinner.visibility = View.GONE
            cityTextView.visibility = View.GONE
            spinnerLableView.visibility=View.GONE
            labelView.visibility=View.GONE
            citySpinner.isEnabled  = false
        }

        refreshButton.setOnClickListener {
            // Button press feedback
            it.alpha = 0.75f
            it.postDelayed({ it.alpha = 1f }, 180)

            // ── KEY FIX: Clear table immediately, show loading, then fetch ──
            clearTableAndShowLoading()
            fetchDemoVehicleStatus()
        }
    }

    // ── Table clear + loading placeholder ───────────────────────────────────

    /**
     * Clears all previous rows and shows a "Loading…" spinner row
     * so the user immediately sees feedback when they tap the button.
     */
    private fun clearTableAndShowLoading() {
        tableLayout.removeAllViews()   // ← wipes all previous data

        // Header still shows while loading
        addTableHeader()

        // Loading row
        val loadingRow = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(0, 40, 0, 40)
            setBackgroundColor(Color.WHITE)
        }
        val loadingText = TextView(this).apply {
            text      = "⏳ Fetching vehicle status…"
            textSize  = 13f
            setTextColor(Color.parseColor("#9A9A9F"))
            gravity   = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        loadingRow.addView(loadingText)
        tableLayout.addView(loadingRow)

        // Disable button while loading
        refreshButton.isClickable = false
        refreshButton.text        = "⏳ LOADING…"
        refreshButton.alpha       = 0.7f
    }

    /** Re-enables the button after data loads */
    private fun resetButton() {
        refreshButton.isClickable = true
        refreshButton.text        = "⟳   CHECK AVAILABILITY"
        refreshButton.alpha       = 1f
    }

    // ── Network — City Spinner ───────────────────────────────────────────────
    private fun fetchCityData() {
        val client  = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.ERP_APP_URL}/demoVehicleTrans/locationDetails?ouId=$ouId")
            .build()
        Log.d("URL->",request.toString())

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                Log.d("jsonData",jsonData.toString())
                Log.d("response",response.toString())
                jsonData?.let {
                    val cities = parseCities(it)
                    runOnUiThread {
                        // Custom adapter — dark popup background, green accent text
                        val adapter = object : ArrayAdapter<String>(
                            this@WorkshopAvailableDemoVehicle,
                            android.R.layout.simple_spinner_item,
                            cities
                        ) {
                            override fun getView(position: Int, convertView: android.view.View?, parent: ViewGroup): android.view.View {
                                val view = super.getView(position, convertView, parent)
                                (view as? TextView)?.apply {
                                    setTextColor(Color.parseColor("#1C1C1E"))
                                    textSize = 13f
                                    typeface = Typeface.DEFAULT_BOLD
                                    setPadding(4, 0, 0, 0)
                                }
                                return view
                            }

                            override fun getDropDownView(position: Int, convertView: android.view.View?, parent: ViewGroup): android.view.View {
                                val view = super.getDropDownView(position, convertView, parent)
                                (view as? TextView)?.apply {
                                    // Highlight first "Select City" item differently
                                    if (position == 0) {
                                        setTextColor(Color.parseColor("#9A9A9F"))
                                        typeface = Typeface.DEFAULT
                                    } else {
                                        setTextColor(Color.parseColor("#22C55E"))
                                        typeface = Typeface.DEFAULT_BOLD
                                    }
                                    textSize = 13f
                                    setPadding(24, 18, 24, 18)
                                    setBackgroundColor(Color.parseColor("#1C1C1E"))
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
//                val code = city.getString("LOC_ID")
                val desc = city.getString("LOCATION")
//                cities.add("$code-$desc")
                cities.add("$desc")
            }
            cities
        } catch (e: JSONException) {
            e.printStackTrace()
            cities
        }
    }

    // ── Network — Vehicle Status ─────────────────────────────────────────────
    private fun fetchDemoVehicleStatus() {
        // SUPERADMIN picks city from spinner; everyone else uses their own location_name
        val locationParam = if (deptName == "SALESSUPERADMIN") {
            val selectedCity = citySpinner.selectedItem?.toString() ?: ""
            if (selectedCity != "Select City" && selectedCity.isNotBlank())
                selectedCity.trim()   // ← no more split("-"), spinner item IS the city name
            else ""
        } else {
            location_name
        }

        // Remember which location this list actually belongs to, so the SELECT
        // action redirects with the correct location (important for SALESSUPERADMIN).
        currentLocationParam = locationParam

        thread {
            try {
                val url = URL("${ApiFile.ERP_APP_URL}/demoVehicleTrans/demoVehStatusList?ouId=$ouId&location=$locationParam&loginName=$login_name")
                Log.d("URLCHECK->", url.toString())
                val connection    = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                val response      = connection.inputStream.bufferedReader().readText()
                val json          = JSONObject(response)
                val dataArray     = json.getJSONArray("obj")

                runOnUiThread {
                    // ── Clear again (removes loading row) and render fresh data ──
                    tableLayout.removeAllViews()
                    addTableHeader()

                    if (dataArray.length() == 0) {
                        showEmptyState()
                    } else {
                        addTableData(dataArray)
                    }
                    resetButton()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    tableLayout.removeAllViews()
                    showErrorState()
                    resetButton()
                }
            }
        }
    }

    // ── Empty / Error states ─────────────────────────────────────────────────

    private fun showEmptyState() {
        val row = buildStateRow("🚗  No vehicles found for this location.", "#9A9A9F")
        tableLayout.addView(row)
    }

    private fun showErrorState() {
        val row = buildStateRow("⚠️  Failed to load data. Please try again.", "#C2410C")
        tableLayout.addView(row)
    }

    private fun buildStateRow(message: String, textColor: String): LinearLayout {
        val row = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            gravity   = Gravity.CENTER
            setPadding(0, 50, 0, 50)
            setBackgroundColor(Color.WHITE)
        }
        val tv = TextView(this).apply {
            text     = message
            textSize = 13f
            setTextColor(Color.parseColor(textColor))
            gravity  = Gravity.CENTER
        }
        row.addView(tv)
        return row
    }

    // ── Table building ───────────────────────────────────────────────────────
    private fun addTableHeader() {
        tableLayout.addView(buildHeaderRow())
    }

    private fun addTableData(dataArray: JSONArray) {
        for (i in 0 until dataArray.length()) {
            val obj         = dataArray.getJSONObject(i)
            val srNo        = (i + 1).toString()
            val instanceNo  = obj.optString("INSTANCE_NUMBER").nullSafe()
            val modelDesc   = obj.optString("MODEL_DESC").nullSafe()
            val status      = obj.optString("STATUS").nullSafe()
            val outTime     = formatDateTime(obj.optString("OUT_TIME").nullSafe())
            val executive   = obj.optString("CREATED_BY").nullSafe()
            val exeName     = obj.optString("ATTRIBUTE3").nullSafe()
            val custName    = obj.optString("CUST_NAME").nullSafe()
            val custAddress = obj.optString("CUST_ADDRESS").nullSafe()

            val row = buildDataRow(
                values   = listOf(srNo, instanceNo,modelDesc, status, outTime, executive,exeName, custName, custAddress),
                rowIndex = i,
                status   = status
            )
            tableLayout.addView(row)

            // Divider
            val divider = android.view.View(this).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1)
                setBackgroundColor(COLOR_DIVIDER)
            }
            tableLayout.addView(divider)
        }
    }

    // ── Row builders ─────────────────────────────────────────────────────────

    private fun buildHeaderRow(): LinearLayout {
        val row = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(COLOR_HEADER_BG)
        }
        columnHeaders.forEachIndexed { index, title ->
            val weight = if (index < columnWeights.size) columnWeights[index] else 1f
            val tv = TextView(this).apply {
                text          = title
                textSize      = 10.5f
                typeface      = Typeface.DEFAULT_BOLD
                gravity       = Gravity.CENTER
                setTextColor(if (index == 1 || index == 2) COLOR_HEADER_ACCENT else COLOR_HEADER_TEXT)
                setPadding(10, 16, 10, 16)
                letterSpacing = 0.05f
                layoutParams  = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, weight)
            }
            row.addView(tv)
            if (index < columnHeaders.size - 1) {
                val sep = android.view.View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(1, ViewGroup.LayoutParams.MATCH_PARENT)
                    setBackgroundColor(Color.parseColor("#2E2E30"))
                }
                row.addView(sep)
            }
        }
        return row
    }

    private fun buildDataRow(values: List<String>, rowIndex: Int, status: String): LinearLayout {
        val bgColor = if (rowIndex % 2 == 0) COLOR_ROW_ODD else COLOR_ROW_EVEN
        val row = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(bgColor)
        }
        val vehicleNo = values.getOrNull(1) ?: ""

        values.forEachIndexed { index, text ->
            // The SELECT column is injected right after VEHICLE NO (index 1) but isn't
            // part of `values`, so every column from MODEL onward is shifted by one
            // position when looking up its weight/header.
            val displayIndex = if (index <= 1) index else index + 1
            val weight = if (displayIndex < columnWeights.size) columnWeights[displayIndex] else 1f
            when (index) {
                0 -> row.addView(buildCell(text, weight).apply {
                    setTextColor(COLOR_SR_TEXT)
                    typeface = Typeface.DEFAULT_BOLD
                    textSize = 12f
                    setBackgroundColor(Color.parseColor("#F9FAFB"))
                })
                1 -> row.addView(buildCell(text, weight).apply {
                    setTextColor(COLOR_TEXT_PRIMARY)
                    typeface = Typeface.DEFAULT_BOLD
                    textSize = 10.5f
                })
                2 -> {
                    val (chipBg, chipText) = getStatusColors(text)
                    val wrapper = LinearLayout(this).apply {
                        layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, weight)
                        gravity = Gravity.CENTER
                        setBackgroundColor(bgColor)
                    }
                    val chip = TextView(this).apply {
                        this.text = text
                        textSize  = 9f
                        typeface  = Typeface.DEFAULT_BOLD
                        gravity   = Gravity.CENTER
                        setTextColor(chipText)
                        setBackgroundColor(chipBg)
                        setPadding(10, 6, 10, 6)
                    }
                    wrapper.addView(chip)
                    row.addView(wrapper)
                }
                3 -> row.addView(buildCell(text, weight).apply {
                    setTextColor(COLOR_TEXT_SECONDARY)
                    textSize = 9.5f
                })
                else -> row.addView(buildCell(text, weight).apply {
                    setTextColor(COLOR_TEXT_PRIMARY)
                    textSize = 10f
                })
            }
            row.addView(buildDivider())

            // Right after VEHICLE NO, drop in the SELECT button — keeping it next to
            // the vehicle number means the user never has to scroll to line them up.
            if (index == 1) {
                val actionWeight = if (2 < columnWeights.size) columnWeights[2] else 1f
                row.addView(buildActionCell(vehicleNo, status, actionWeight, bgColor))
                row.addView(buildDivider())
            }
        }

        return row
    }

    /** Thin vertical divider used between table cells. */
    private fun buildDivider(): android.view.View {
        return android.view.View(this).apply {
            layoutParams = LinearLayout.LayoutParams(1, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundColor(COLOR_DIVIDER)
        }
    }

    /** SELECT button shown only for AVAILABLE vehicles; a "—" placeholder otherwise. */
    private fun buildActionCell(vehicleNo: String, status: String, weight: Float, bgColor: Int): LinearLayout {
        val wrapper = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, weight)
            gravity = Gravity.CENTER
            setBackgroundColor(bgColor)
        }

        if (status.contains("AVAIL", ignoreCase = true) && vehicleNo.isNotBlank() && vehicleNo != "—") {
            val selectBtn = TextView(this).apply {
                text     = "SELECT"
                textSize = 9.5f
                typeface = Typeface.DEFAULT_BOLD
                gravity  = Gravity.CENTER
                setTextColor(Color.WHITE)
                setBackgroundColor(COLOR_HEADER_ACCENT)
                setPadding(16, 10, 16, 10)
                isClickable = true
                isFocusable = true
                setOnClickListener { view ->
                    view.alpha = 0.7f
                    view.postDelayed({ view.alpha = 1f }, 150)
                    openVehicleOutScreen(vehicleNo)
                }
            }
            wrapper.addView(selectBtn)
        } else {
            val placeholder = TextView(this).apply {
                text = "—"
                textSize = 10f
                setTextColor(COLOR_TEXT_SECONDARY)
                gravity = Gravity.CENTER
            }
            wrapper.addView(placeholder)
        }

        return wrapper
    }

    private fun buildCell(text: String, weight: Float): TextView {
        return TextView(this).apply {
            this.text = text
            textSize  = 10f
            gravity   = Gravity.CENTER
            setPadding(10, 14, 10, 14)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, weight)
        }
    }

    // ── Navigation ───────────────────────────────────────────────────────────

    /**
     * Redirects to WorkshopDemoVehicle for the given vehicle number and asks it to
     * automatically run the "Vehicle Out" (detailsForVehicleOut) flow, instead of
     * requiring the user to retype the vehicle number on the next screen.
     */
    private fun openVehicleOutScreen(vehicleNo: String) {
        val effectiveLocation = if (currentLocationParam.isNotBlank()) currentLocationParam else location_name

        val intent = Intent(this, WorkshopDemoVehicle::class.java).apply {
            putExtra("locId", locId)
            putExtra("ouId", ouId)
            putExtra("deptName", deptName)
            putExtra("location_name", effectiveLocation)
            putExtra("login_name", login_name)
            putExtra("location", location)
            putExtra("attribute1", attribute1)
            putExtra("PRESELECTED_VEHICLE_NO", vehicleNo)
            putExtra("AUTO_TRIGGER_OUT", true)
        }
        startActivity(intent)
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private fun getStatusColors(status: String): Pair<Int, Int> = when {
        status.contains("OUT", ignoreCase = true)     -> STATUS_OUT_DEMO_BG to STATUS_OUT_DEMO_TEXT
        status.contains("AVAIL", ignoreCase = true)   -> STATUS_AVAIL_BG    to STATUS_AVAIL_TEXT
        status.contains("BOOK", ignoreCase = true)    -> STATUS_BOOKED_BG   to STATUS_BOOKED_TEXT
        status.contains("SERVICE", ignoreCase = true) -> STATUS_SERVICE_BG  to STATUS_SERVICE_TEXT
        else                                           -> STATUS_DEFAULT_BG  to STATUS_DEFAULT_TEXT
    }

    private fun String?.nullSafe(): String =
        if (this.isNullOrBlank() || this.equals("null", ignoreCase = true)) "—" else this

    /** "2025-06-18T10:17:03.000+0000" → "18 Jun\n10:17" */
    private fun formatDateTime(raw: String): String {
        if (raw == "—" || raw.isBlank()) return "—"
        return try {
            val datePart = raw.substringBefore("T")
            val timePart = raw.substringAfter("T").take(5)
            val (_, month, day) = datePart.split("-")
            val months = arrayOf("", "Jan", "Feb", "Mar", "Apr", "May",
                "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
            "$day ${months[month.toInt()]}\n$timePart"
        } catch (e: Exception) {
            raw.take(16)
        }
    }
}