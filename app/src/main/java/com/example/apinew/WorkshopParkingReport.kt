package com.example.apinew

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.math.max
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class WorkshopParkingReport : AppCompatActivity() {

    private var ouId: Int = 0
    private var locId: Int = 0
    private lateinit var login_name: String
    private lateinit var emailId: String
    private lateinit var attribute1: String
    private lateinit var location: String
    private lateinit var fetchData: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tableLayout: TableLayout
    private lateinit var headerTableLayout: TableLayout
    private lateinit var headerHorizontalScrollView: HorizontalScrollView
    private lateinit var dataHorizontalScrollView: HorizontalScrollView
    private lateinit var TextProgressBar: TextView
    private lateinit var deptName: String
    private lateinit var citySpinner: Spinner
    private lateinit var citySpinnerTxtView: TextView
    private lateinit var rowCountTextView: TextView
    private lateinit var typeSpinner: Spinner
    private lateinit var fromDatePicker: Button
    private lateinit var toDatePicker: Button
    private var fromDate: String = ""
    private var toDate: String = ""
    private lateinit var fromDateLabel: TextView
    private lateinit var toDateLabel: TextView
    private lateinit var progressBarCardView: CardView
    private lateinit var logoutBtn: ImageView
    private lateinit var sendMailBtn: Button

    // Dashboard — sections of stat tiles built at runtime from whatever the API returns:
    //   1. Overall        -> TOTAL / JC OPENED / WITHOUT JC
    //   2. By Department  -> one tile per distinct DEPTALLOTED value
    //   3. By Vehicle Type-> one tile per distinct VEHTYPE value
    private lateinit var dashboardStatsContainer: LinearLayout

    // Rotating accent palette used to color-code each department tile; cycles if there
    // are more departments than colors, so it never breaks on new/unseen departments.
    private val dashboardPalette = listOf(
        Color.parseColor("#3F51B5"), // indigo
        Color.parseColor("#00897B"), // teal
        Color.parseColor("#D84315"), // deep orange
        Color.parseColor("#6A1B9A"), // purple
        Color.parseColor("#AD1457"), // pink
        Color.parseColor("#F9A825"), // amber
        Color.parseColor("#00838F"), // cyan
        Color.parseColor("#5D4037"), // brown
        Color.parseColor("#37474F")  // blue grey
    )

    // Separate palette for VEHTYPE tiles (TRUE VALUE / SERVICE / SALES / DEMO CAR / anything
    // new the API starts sending) so they read differently from the department tiles.
    private val vehTypePalette = listOf(
        Color.parseColor("#0277BD"), // light blue
        Color.parseColor("#EF6C00"), // orange
        Color.parseColor("#2E7D32"), // green
        Color.parseColor("#8E24AA"), // purple
        Color.parseColor("#C2185B"), // pink
        Color.parseColor("#00695C"), // teal
        Color.parseColor("#455A64"), // blue grey
        Color.parseColor("#795548")  // brown
    )

    // Fixed colours for the job-card status tiles — these two always mean the same thing.
    private val colorJcOpen = Color.parseColor("#2E7D32")
    private val colorJcPending = Color.parseColor("#C62828")

    // Job-card filter applied to the already-loaded data (no re-fetch)
    private enum class JcFilter { ALL, WITH_JC, WITHOUT_JC }

    private lateinit var jcFilterLayout: LinearLayout
    private lateinit var filterAllBtn: Button
    private lateinit var filterWithJcBtn: Button
    private lateinit var filterWithoutJcBtn: Button
    private var jcFilterMode = JcFilter.ALL

    // Pagination
    private lateinit var paginationLayout: LinearLayout
    private lateinit var prevPageBtn: Button
    private lateinit var nextPageBtn: Button
    private lateinit var pageIndicatorTextView: TextView

    private val PAGE_SIZE = 15
    private var currentPage = 0

    // masterTableData = everything the API returned; fullTableData = what the current
    // filter lets through (this is what pagination and rendering work off).
    private var masterTableData: List<List<String>> = emptyList()
    private var fullTableData: List<List<String>> = emptyList()
    private var columnWidths: List<Int> = emptyList()

    private val headers = listOf(
        "SR NO", "VEH NO", "CHASSIS NO", "ENGINE NO", "LOCATION", "OUT TIME", "IN TIME", "DEPT",
        "JC NO", "SER ADVISOR", "VEH TYPE"
    )

    // Position of "JC NO" in the row lists above — used by the filter
    private val COL_JC_NO = 8

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workshop_parking_report)

        ouId = intent.getIntExtra("ouId", 0)
        locId = intent.getIntExtra("locId", 0)
        attribute1 = intent.getStringExtra("attribute1") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""
        location = intent.getStringExtra("location") ?: ""
        deptName = intent.getStringExtra("deptName") ?: ""
        emailId = intent.getStringExtra("emailId") ?: ""
        rowCountTextView = findViewById(R.id.rowCountTextView)
        rowCountTextView.visibility = View.GONE

        fromDatePicker = findViewById(R.id.fromDatePicker)
        toDatePicker = findViewById(R.id.toDatePicker)
        fromDateLabel = findViewById(R.id.fromDateLabel)
        toDateLabel = findViewById(R.id.toDateLabel)
        progressBarCardView = findViewById(R.id.progressBarCardView)
        citySpinnerTxtView = findViewById(R.id.citySpinnerTxtView)
        logoutBtn = findViewById(R.id.logoutBtn)
        sendMailBtn = findViewById(R.id.sendMailBtn)
        progressBarCardView.visibility = View.GONE
        citySpinnerTxtView.visibility = View.GONE

        dashboardStatsContainer = findViewById(R.id.dashboardStatsContainer)
        buildDashboard(0)

        paginationLayout = findViewById(R.id.paginationLayout)
        prevPageBtn = findViewById(R.id.prevPageBtn)
        nextPageBtn = findViewById(R.id.nextPageBtn)
        pageIndicatorTextView = findViewById(R.id.pageIndicatorTextView)
        paginationLayout.visibility = View.GONE

        jcFilterLayout = findViewById(R.id.jcFilterLayout)
        filterAllBtn = findViewById(R.id.filterAllBtn)
        filterWithJcBtn = findViewById(R.id.filterWithJcBtn)
        filterWithoutJcBtn = findViewById(R.id.filterWithoutJcBtn)
        jcFilterLayout.visibility = View.GONE

        filterAllBtn.setOnClickListener { setJcFilter(JcFilter.ALL) }
        filterWithJcBtn.setOnClickListener { setJcFilter(JcFilter.WITH_JC) }
        filterWithoutJcBtn.setOnClickListener { setJcFilter(JcFilter.WITHOUT_JC) }
        styleFilterButtons()

        val calendar = Calendar.getInstance()

        val dateSetListener = { textView: TextView, isFromDate: Boolean ->
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selectedCalendar = Calendar.getInstance()
                    selectedCalendar.set(year, month, dayOfMonth)

                    val monthFormat = SimpleDateFormat("MMM", Locale.ENGLISH)
                    val selectedDate = String.format(
                        Locale.getDefault(),
                        "%02d-%s-%d",
                        dayOfMonth,
                        monthFormat.format(selectedCalendar.time),
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

        sendMailBtn.setOnClickListener {
            sendReport()
        }

        fetchData = findViewById(R.id.fetchData)
        progressBar = findViewById(R.id.progressBar)
        tableLayout = findViewById(R.id.tableLayout)
        citySpinner = findViewById(R.id.citySpinner)
        headerTableLayout = findViewById(R.id.headerTableLayout)
        headerHorizontalScrollView = findViewById(R.id.headerHorizontalScrollView)
        dataHorizontalScrollView = findViewById(R.id.dataHorizontalScrollView)
        TextProgressBar = findViewById(R.id.TextProgressBar)
        TextProgressBar.visibility = View.GONE
        typeSpinner = findViewById(R.id.typeSpinner)

        val typeOptions = mutableListOf("SELECT DEPARTMENT", "SERVICE", "BODYSHOP")
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

        if (deptName != "SUPERADMIN") {
            citySpinner.visibility = View.GONE
        }

        fetchData.setOnClickListener {
            rowCountTextView.visibility = View.GONE
            tableLayout.removeAllViews()
            headerTableLayout.removeAllViews()
            paginationLayout.visibility = View.GONE
            jcFilterLayout.visibility = View.GONE
            if (fromDate.isEmpty() || toDate.isEmpty()) {
                Toast.makeText(this, "Please select both dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            fetchUninvoice()
        }

        prevPageBtn.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                renderCurrentPage()
            }
        }

        nextPageBtn.setOnClickListener {
            val totalPages = totalPageCount()
            if (currentPage < totalPages - 1) {
                currentPage++
                renderCurrentPage()
            }
        }

        logoutBtn.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        SessionManager.clearSession(this)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Single source of truth for "does this vehicle have a job card opened".
     * Used both for the dashboard counts and for the table filter, so the two can
     * never disagree. Adjust the placeholder list here if the API sends something else
     * (e.g. "NA") for vehicles without a job card.
     */
    private fun hasJobCard(rawJcNo: String): Boolean {
        val jcNo = rawJcNo.trim()
        return jcNo.isNotEmpty() &&
                !jcNo.equals("null", true) &&
                jcNo != "0" &&
                jcNo != "-"
    }

    private fun fetchEmailList(callback: (List<String>) -> Unit) {
        val emailApiUrl = WorkshopWashingUrlManager.fetchEmailListForParking(ouId.toString(), locId.toString())
        val client = OkHttpClient()
        val request = Request.Builder().url(emailApiUrl).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@WorkshopParkingReport, "Failed to fetch emails: ${e.message}", Toast.LENGTH_SHORT).show()
                    callback(emptyList())
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val emails = mutableListOf<String>()
                try {
                    val jsonResponse = JSONObject(response.body?.string() ?: "")
                    val objArray = jsonResponse.optJSONArray("obj")
                    if (objArray != null) {
                        for (i in 0 until objArray.length()) {
                            val email = objArray.getJSONObject(i).optString("EMAIL_ID")
                            if (email.isNotEmpty()) {
                                emails.add(email)
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                runOnUiThread {
                    callback(emails)
                }
            }
        })
    }

    private fun sendReport() {
        if (fromDate.isEmpty() || toDate.isEmpty()) {
            Toast.makeText(this, "Please select both From Date and To Date", Toast.LENGTH_SHORT).show()
            return
        }

        progressBarCardView.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        TextProgressBar.text = "Email Sending in Progress...Please wait..."
        TextProgressBar.visibility = View.VISIBLE

        val inputFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH)

        val formattedFromDate = try {
            outputFormat.format(inputFormat.parse(fromDate)!!)
        } catch (e: Exception) {
            e.printStackTrace()
            progressBarCardView.visibility = View.GONE
            progressBar.visibility = View.GONE
            TextProgressBar.visibility = View.GONE
            Toast.makeText(this, "Invalid From Date format", Toast.LENGTH_SHORT).show()
            return
        }

        val formattedToDate = try {
            val date = inputFormat.parse(toDate)!!
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            outputFormat.format(calendar.time)
        } catch (e: Exception) {
            e.printStackTrace()
            progressBarCardView.visibility = View.GONE
            progressBar.visibility = View.GONE
            TextProgressBar.visibility = View.GONE
            Toast.makeText(this, "Invalid To Date format", Toast.LENGTH_SHORT).show()
            return
        }

        val url = WorkshopWashingUrlManager.postSendReportUrlForParking()
        val emailJsonArray = JSONArray()

        fetchEmailList { emailList ->
            for (email in emailList) {
                emailJsonArray.put(email)
            }

            val json = JSONObject().apply {
                put("ouId", ouId)
                put("locId", locId)
                put("fromDate", formattedFromDate)
                put("toDate", formattedToDate)
                put("recipients", emailJsonArray)
            }

            val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
            val request = Request.Builder().url(url).post(requestBody).build()
            val client = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .build()

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val response = client.newCall(request).execute()
                    val responseBodyStr = response.body?.string()

                    runOnUiThread {
                        progressBarCardView.visibility = View.GONE
                        progressBar.visibility = View.GONE
                        TextProgressBar.visibility = View.GONE
                        TextProgressBar.text = "Data is loading....."

                        if (response.isSuccessful && responseBodyStr != null) {
                            Log.d("ResponseBody", responseBodyStr)
                            val jsonResponse = JSONObject(responseBodyStr)
                            val message = jsonResponse.optString("message", "Report sent!")
                            Toast.makeText(this@WorkshopParkingReport, message, Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(
                                this@WorkshopParkingReport,
                                "Failed to send mail. Error code: ${response.code}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread {
                        progressBarCardView.visibility = View.GONE
                        progressBar.visibility = View.GONE
                        TextProgressBar.visibility = View.GONE
                        Toast.makeText(
                            this@WorkshopParkingReport,
                            "Error sending data: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun fetchCityData() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.ERP_APP_URL}/fndcom/cmnType?cmnType=City")
            .build()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val cities = parseCities(it)
                    runOnUiThread {
                        val adapter = ArrayAdapter(
                            this@WorkshopParkingReport,
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
        paginationLayout.visibility = View.GONE
        jcFilterLayout.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        TextProgressBar.visibility = View.VISIBLE
        progressBarCardView.visibility = View.VISIBLE

        // Reset dashboard and filter while a new fetch is in progress
        buildDashboard(0)
        jcFilterMode = JcFilter.ALL
        styleFilterButtons()

        val client = OkHttpClient()
        if (fromDate.isEmpty() || toDate.isEmpty()) {
            Toast.makeText(this, "Please select both From Date and To Date", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedCity = citySpinner.selectedItem.toString()
        val selectedCityCode = if (selectedCity != "Select City") {
            selectedCity.split("-")[0].trim()
        } else {
            ""
        }

        val incrementedToDate = getIncrementedDate(toDate)

        val url = if (deptName == "SUPERADMIN") {
            ApiFile.ERP_APP_URL + "/tdParking/parkingDetailsByOu?ouId=$selectedCityCode&locId=$locId&fromDate=$fromDate&toDate=$incrementedToDate"
        } else {
            ApiFile.ERP_APP_URL + "/tdParking/parkingDetailsByOu?ouId=$ouId&locId=$locId&fromDate=$fromDate&toDate=$incrementedToDate"
        }

        val request = Request.Builder()
            .url(url)
            .get()
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()

                if (!response.isSuccessful) {
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        progressBarCardView.visibility = View.GONE
                        TextProgressBar.visibility = View.GONE
                    }
                    return@launch
                }

                val jsonData = response.body?.string()

                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    if (jsonObject.has("obj")) {
                        val jsonArray = jsonObject.getJSONArray("obj")

                        val summaryDataList = mutableListOf<List<String>>()

                        // Every distinct DEPTALLOTED / VEHTYPE value is counted as it appears —
                        // no assumptions about which departments or vehicle types exist.
                        val deptCounts = LinkedHashMap<String, Int>()
                        val vehTypeCounts = LinkedHashMap<String, Int>()
                        var jcOpenCount = 0
                        var jcPendingCount = 0

                        for (i in 0 until jsonArray.length()) {
                            val stockItem = jsonArray.getJSONObject(i)

                            // ---- Department ----
                            val rawDept = stockItem.optString("DEPTALLOTED", "").trim()
                            val deptKey = if (rawDept.isEmpty() || rawDept.equals("null", true)) {
                                "UNASSIGNED"
                            } else {
                                rawDept.uppercase(Locale.getDefault())
                            }
                            deptCounts[deptKey] = (deptCounts[deptKey] ?: 0) + 1

                            // ---- Job card opened vs not ----
                            if (hasJobCard(stockItem.optString("JOB_CARD_NO", ""))) {
                                jcOpenCount++
                            } else {
                                jcPendingCount++
                            }

                            // ---- Vehicle type (TRUE VALUE / SERVICE / SALES / DEMO CAR / ...) ----
                            val rawType = stockItem.optString("VEHTYPE", "").trim()
                            val typeKey = if (rawType.isEmpty() || rawType.equals("null", true)) {
                                "NOT SPECIFIED"
                            } else {
                                rawType.uppercase(Locale.getDefault())
                            }
                            vehTypeCounts[typeKey] = (vehTypeCounts[typeKey] ?: 0) + 1

                            val data = listOf(
                                stockItem.optString("SR_NO", ""),
                                stockItem.optString("REG_NO", ""),
                                stockItem.optString("CHASSIS_NO", ""),
                                stockItem.optString("ENGINE_NO", ""),
                                stockItem.optString("LOCATION", ""),
                                stockItem.optString("OUT_TIME", ""),
                                stockItem.optString("IN_TIME", ""),
                                stockItem.optString("DEPTALLOTED", ""),
                                stockItem.optString("JOB_CARD_NO", ""),
                                stockItem.optString("SERVICE_ADVISOR", ""),
                                stockItem.optString("VEHTYPE", "")
                            )
                            summaryDataList.add(data)
                        }

                        val totalCount = jsonArray.length()

                        // Largest first so the most relevant tiles show up front
                        val sortedDeptCounts = LinkedHashMap<String, Int>()
                        deptCounts.entries.sortedByDescending { it.value }.forEach {
                            sortedDeptCounts[it.key] = it.value
                        }

                        val sortedVehTypeCounts = LinkedHashMap<String, Int>()
                        vehTypeCounts.entries.sortedByDescending { it.value }.forEach {
                            sortedVehTypeCounts[it.key] = it.value
                        }

                        runOnUiThread {
                            buildDashboard(
                                totalCount,
                                sortedDeptCounts,
                                jcOpenCount,
                                jcPendingCount,
                                sortedVehTypeCounts
                            )

                            updateTableView(summaryDataList)
                            progressBar.visibility = View.GONE
                            TextProgressBar.visibility = View.GONE
                            rowCountTextView.visibility = View.VISIBLE
                            progressBarCardView.visibility = View.GONE
                        }
                    } else {
                        runOnUiThread {
                            progressBar.visibility = View.GONE
                            progressBarCardView.visibility = View.GONE
                            TextProgressBar.visibility = View.GONE
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    progressBarCardView.visibility = View.GONE
                    TextProgressBar.visibility = View.GONE
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
            "$formattedDate " + "$formattedTime"
        } catch (e: Exception) {
            dateTime
        }
    }

    /**
     * Rebuilds the Fleet Overview from scratch, in three sections:
     *  1. Overall         — TOTAL, JC OPENED, WITHOUT JC
     *  2. By Department   — one tile per distinct DEPTALLOTED value
     *  3. By Vehicle Type — one tile per distinct VEHTYPE value
     *
     * [deptCounts] and [vehTypeCounts] are expected to already be ordered the way they
     * should display (e.g. largest count first). Nothing here is hardcoded to specific
     * department or vehicle-type names, so new values appearing in the API response are
     * picked up automatically.
     */
    private fun buildDashboard(
        total: Int,
        deptCounts: LinkedHashMap<String, Int> = LinkedHashMap(),
        jcOpenCount: Int = 0,
        jcPendingCount: Int = 0,
        vehTypeCounts: LinkedHashMap<String, Int> = LinkedHashMap()
    ) {
        dashboardStatsContainer.removeAllViews()

        // --- Section 1: overall + job card status ---
        val overviewTiles = listOf(
            createStatTile("TOTAL", total, Color.parseColor("#1A1A2E"), true),
            createStatTile("JC OPENED", jcOpenCount, colorJcOpen, false),
            createStatTile("WITHOUT JC", jcPendingCount, colorJcPending, false)
        )
        dashboardStatsContainer.addView(createStatSection("Overall", overviewTiles))

        // --- Section 2: departments ---
        if (deptCounts.isNotEmpty()) {
            val deptTiles = mutableListOf<View>()
            var colorIndex = 0
            for ((dept, count) in deptCounts) {
                val color = dashboardPalette[colorIndex % dashboardPalette.size]
                deptTiles.add(createStatTile(dept, count, color, false))
                colorIndex++
            }
            dashboardStatsContainer.addView(createStatSection("By Department", deptTiles))
        }

        // --- Section 3: vehicle types ---
        if (vehTypeCounts.isNotEmpty()) {
            val typeTiles = mutableListOf<View>()
            var colorIndex = 0
            for ((type, count) in vehTypeCounts) {
                val color = vehTypePalette[colorIndex % vehTypePalette.size]
                typeTiles.add(createStatTile(type, count, color, false))
                colorIndex++
            }
            dashboardStatsContainer.addView(createStatSection("By Vehicle Type", typeTiles))
        }
    }

    /** A labelled, horizontally scrollable row of stat tiles. */
    private fun createStatSection(title: String, tiles: List<View>): View {
        val d = resources.displayMetrics.density

        val section = LinearLayout(this)
        section.orientation = LinearLayout.VERTICAL
        val sectionParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        sectionParams.bottomMargin = (14 * d).toInt()
        section.layoutParams = sectionParams

        val label = TextView(this)
        label.text = title.uppercase(Locale.getDefault())
        label.textSize = 11f
        label.letterSpacing = 0.08f
        label.setTypeface(null, Typeface.BOLD)
        label.setTextColor(Color.parseColor("#9098B1"))
        label.setPadding((18 * d).toInt(), 0, (18 * d).toInt(), (8 * d).toInt())
        section.addView(label)

        val scroll = HorizontalScrollView(this)
        scroll.isHorizontalScrollBarEnabled = false
        scroll.clipToPadding = false
        scroll.setPadding((18 * d).toInt(), (3 * d).toInt(), (8 * d).toInt(), (5 * d).toInt())
        scroll.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        for (tile in tiles) {
            row.addView(tile)
        }

        scroll.addView(row)
        section.addView(scroll)
        return section
    }

    /** Builds a single modern stat tile: a colored accent strip + bold count + label. */
    private fun createStatTile(label: String, count: Int, accentColor: Int, isHighlighted: Boolean): View {
        val d = resources.displayMetrics.density

        val card = CardView(this)
        val cardParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        cardParams.marginEnd = (10 * d).toInt()
        card.layoutParams = cardParams
        // Long labels (WITHOUT JC, NOT SPECIFIED, ...) can grow past the base width
        card.minimumWidth = (112 * d).toInt()
        card.radius = 14 * d
        card.cardElevation = if (isHighlighted) 5 * d else 1.5f * d
        card.setCardBackgroundColor(if (isHighlighted) accentColor else Color.WHITE)
        card.useCompatPadding = false

        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (!isHighlighted) {
            val accentStrip = View(this)
            accentStrip.layoutParams =
                LinearLayout.LayoutParams((4 * d).toInt(), ViewGroup.LayoutParams.MATCH_PARENT)
            accentStrip.setBackgroundColor(accentColor)
            row.addView(accentStrip)
        }

        val textLayout = LinearLayout(this)
        textLayout.orientation = LinearLayout.VERTICAL
        val hPad = (14 * d).toInt()
        val vPad = (12 * d).toInt()
        textLayout.setPadding(hPad, vPad, hPad, vPad)
        textLayout.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val countText = TextView(this)
        countText.text = count.toString()
        countText.textSize = 21f
        countText.setTypeface(null, Typeface.BOLD)
        countText.setTextColor(if (isHighlighted) Color.WHITE else Color.parseColor("#1A1A2E"))

        val labelText = TextView(this)
        labelText.text = label
        labelText.textSize = 10.5f
        labelText.maxLines = 1
        labelText.ellipsize = TextUtils.TruncateAt.END
        labelText.letterSpacing = 0.04f
        labelText.setPadding(0, (2 * d).toInt(), 0, 0)
        labelText.setTextColor(if (isHighlighted) Color.parseColor("#C5CAE9") else accentColor)

        textLayout.addView(countText)
        textLayout.addView(labelText)
        row.addView(textLayout)
        card.addView(row)
        return card
    }

    /** Switches the job-card filter and re-renders from the already-loaded data. */
    private fun setJcFilter(mode: JcFilter) {
        if (jcFilterMode == mode) return
        jcFilterMode = mode
        applyJcFilter()
    }

    /**
     * Filters [masterTableData] by job-card status into [fullTableData], then repaints
     * page one. No network call — this works entirely on the data already on screen.
     */
    private fun applyJcFilter() {
        fullTableData = when (jcFilterMode) {
            JcFilter.ALL -> masterTableData
            JcFilter.WITH_JC -> masterTableData.filter { hasJobCard(it.getOrElse(COL_JC_NO) { "" }) }
            JcFilter.WITHOUT_JC -> masterTableData.filterNot { hasJobCard(it.getOrElse(COL_JC_NO) { "" }) }
        }
        currentPage = 0
        styleFilterButtons()
        renderCurrentPage()
    }

    /** Highlights the active filter button and dims the other two. */
    private fun styleFilterButtons() {
        val buttons = listOf(
            filterAllBtn to JcFilter.ALL,
            filterWithJcBtn to JcFilter.WITH_JC,
            filterWithoutJcBtn to JcFilter.WITHOUT_JC
        )
        for ((button, mode) in buttons) {
            val selected = mode == jcFilterMode
            button.alpha = if (selected) 1f else 0.45f
            button.setTypeface(null, if (selected) Typeface.BOLD else Typeface.NORMAL)
        }
    }

    private fun totalPageCount(): Int {
        val totalRows = fullTableData.size
        return if (totalRows == 0) 1 else ((totalRows - 1) / PAGE_SIZE) + 1
    }

    /**
     * Stores the full result set, builds the (single, reused) header row and
     * the column widths once, then applies the current filter and renders page one.
     */
    private fun updateTableView(stockItems: List<List<String>>) {
        masterTableData = stockItems
        currentPage = 0

        tableLayout.removeAllViews()
        headerTableLayout.removeAllViews()
        rowCountTextView.text = ""

        val textViewPadding = 24 * 2
        val widths = MutableList(headers.size) { 0 }

        for ((index, header) in headers.withIndex()) {
            val headerTextView = createTextView(header, true)
            headerTextView.measure(0, 0)
            widths[index] = headerTextView.measuredWidth + textViewPadding
        }

        // Measure against the full (unfiltered) dataset so column widths stay stable
        // across both pages and filter changes
        for (row in stockItems) {
            for ((index, data) in row.withIndex()) {
                val dataTextView = createTextView(data, false)
                dataTextView.measure(0, 0)
                widths[index] = max(widths[index], dataTextView.measuredWidth + textViewPadding)
            }
        }
        columnWidths = widths

        val headerRow = TableRow(this)
        headerRow.layoutParams = TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT,
            TableLayout.LayoutParams.WRAP_CONTENT
        )
        for ((index, header) in headers.withIndex()) {
            val headerText = createTextView(header, true)
            headerText.layoutParams =
                TableRow.LayoutParams(columnWidths[index], TableRow.LayoutParams.WRAP_CONTENT)
            headerRow.addView(headerText)
        }
        headerTableLayout.addView(headerRow)

        jcFilterLayout.visibility = if (stockItems.isEmpty()) View.GONE else View.VISIBLE

        applyJcFilter()
    }

    /**
     * Renders only the rows belonging to [currentPage] (PAGE_SIZE rows at a time),
     * so the table view never has to inflate the whole result set at once.
     */
    private fun renderCurrentPage() {
        tableLayout.removeAllViews()

        val totalRows = fullTableData.size
        val totalPages = totalPageCount()
        if (currentPage >= totalPages) currentPage = totalPages - 1
        if (currentPage < 0) currentPage = 0

        val startIndex = currentPage * PAGE_SIZE
        val endIndex = minOf(startIndex + PAGE_SIZE, totalRows)
        val pageItems = if (totalRows == 0) emptyList() else fullTableData.subList(startIndex, endIndex)

        for (item in pageItems) {
            val dataRow = TableRow(this)
            dataRow.layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
            )
            for ((index, data) in item.withIndex()) {
                val dataText = createTextView(data, false)
                dataText.layoutParams = TableRow.LayoutParams(
                    columnWidths.getOrElse(index) { TableRow.LayoutParams.WRAP_CONTENT },
                    TableRow.LayoutParams.WRAP_CONTENT
                )
                dataRow.addView(dataText)
            }
            tableLayout.addView(dataRow)
        }

        val filterNote = when (jcFilterMode) {
            JcFilter.ALL -> ""
            JcFilter.WITH_JC -> " (JC opened)"
            JcFilter.WITHOUT_JC -> " (without JC)"
        }

        rowCountTextView.text = when {
            masterTableData.isEmpty() -> "No records found"
            totalRows == 0 -> "No records match this filter"
            else -> "Showing ${startIndex + 1}-$endIndex of $totalRows records$filterNote"
        }

        pageIndicatorTextView.text = "Page ${currentPage + 1} of $totalPages"
        prevPageBtn.isEnabled = currentPage > 0
        nextPageBtn.isEnabled = currentPage < totalPages - 1
        prevPageBtn.alpha = if (prevPageBtn.isEnabled) 1f else 0.4f
        nextPageBtn.alpha = if (nextPageBtn.isEnabled) 1f else 0.4f

        paginationLayout.visibility = if (totalRows > PAGE_SIZE) View.VISIBLE else View.GONE

        // Snap scroll views back to the top-left when switching pages or filters
        dataHorizontalScrollView.scrollTo(0, 0)
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