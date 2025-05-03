package com.example.apinew

import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import kotlin.math.max
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class WorkshopWashingReport : AppCompatActivity() {

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
    private lateinit var sendReportButton:Button
    private lateinit var emailId:String
    private lateinit var reportDownload:ImageButton



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workshop_washing_report)

        ouId = intent.getIntExtra("ouId", 0)
        locId = intent.getIntExtra("locId", 0)
        attribute1 = intent.getStringExtra("attribute1") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""
        location = intent.getStringExtra("location") ?: ""
        deptName = intent.getStringExtra("deptName") ?: ""
        emailId = intent.getStringExtra("emailId") ?: ""


        rowCountTextView = findViewById(R.id.rowCountTextView)
        rowCountTextView.visibility=View.GONE

        fromDatePicker=findViewById(R.id.fromDatePicker)
        toDatePicker=findViewById(R.id.toDatePicker)
        fromDateLabel=findViewById(R.id.fromDateLabel)
        toDateLabel=findViewById(R.id.toDateLabel)
        sendReportButton=findViewById(R.id.sendReportButton)
        reportDownload=findViewById(R.id.reportDownload)

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

        sendReportButton.setOnClickListener {
            sendReport()
        }

        reportDownload.setOnClickListener {
            downloadReport()
        }


//        if (deptName=="WM"){
//            sendReportButton.visibility=View.VISIBLE
//        } else {
//            sendReportButton.visibility=View.GONE
//        }

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
//            .url("${ApiFile.APP_URL}/fndcom/cmnType?cmnType=City")
            .url(WorkshopWashingUrlManager.getCityUrl())

            .build()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val cities = parseCities(it)
                    runOnUiThread {
                        val adapter = ArrayAdapter(
                            this@WorkshopWashingReport,
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

    private fun formatDateTime(dateTime: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
            val date = inputFormat.parse(dateTime)
            date?.let { outputFormat.format(it) } ?: dateTime
        } catch (e: Exception) {
            dateTime
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
            val incrementedToDate = getIncrementedDate(toDate)
            val url = if (deptName == "SUPERADMIN") {
//                ApiFile.APP_URL + "/vehWashingReport/vehWashingReportByOu?ouId=$selectedCityCode&locId=$locId&fromDate=$fromDate&toDate=$incrementedToDate"
                WorkshopWashingUrlManager.getWashingReportUrl(selectedCityCode.toInt(), locId, fromDate, incrementedToDate)
            } else {
//                ApiFile.APP_URL + "/vehWashingReport/vehWashingReportByOu?ouId=$ouId&locId=$locId&fromDate=$fromDate&toDate=$incrementedToDate"
                WorkshopWashingUrlManager.getWashingReportUrl(ouId, locId, fromDate, incrementedToDate)
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
                                    stockItem.optString("SR NO"),
                                    stockItem.getString("REG NO"),
                                    stockItem.optString("CHASSIS NO",""),
                                    stockItem.optString("MODEL", ""),
                                    stockItem.optString("SERVICE ADVISOR", ""),
                                    stockItem.optString("UPDATED BY", ""),
                                    formatDateTime(stockItem.optString("1ST STAGE IN TIME", "")),
                                    stockItem.optString("AIR BLOW-1ST STAGE", ""),
                                    stockItem.optString("ENGINE ROOM-1ST STAGE", ""),
                                    stockItem.optString("UNDER BODY-1ST STAGE", ""),
                                    formatDateTime(stockItem.optString("1ST STAGE OUT TIME", "")),
                                    formatDateTime(stockItem.optString("2ND STAGE IN TIME", "")),
                                    stockItem.optString("LOOSE ITEM-2ND STAGE", ""),
                                    stockItem.optString("VEH INTERIOR-2ND STAGE", ""),
                                    formatDateTime(stockItem.optString("2ND STAGE OUT TIME", "")),
                                    formatDateTime(stockItem.optString("3RD STAGE IN TIME", "")),
                                    stockItem.optString("VEH EXTERIOR-3RD STAGE", ""),
                                    stockItem.optString("GLASS POLISH-3RD STAGE", ""),
                                    formatDateTime(stockItem.optString("3RD STAGE OUT TIME", ""))
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
            "MODEL",
            "SERVICE ADVISOR",
            "WASH S.VISOR",
            "1ST STAGE IN TIME","AIR BLOW-1ST STAGE","ENGINE ROOM-1ST STAGE","UNDER BODY-1ST STAGE","1ST STAGE OUT TIME","2ND STAGE IN TIME","LOOSE ITEM-2ND STAGE","VEH INTERIOR-2ND STAGE","2ND STAGE OUT TIME",
            "3RD STAGE IN TIME","VEH EXTERIOR-3RD STAGE","GLASS POLISH-3RD STAGE","3RD STAGE OUT TIME"
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
                    TableRow.LayoutParams(maxWidths[index],TableRow.LayoutParams.WRAP_CONTENT)
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


    private fun fetchEmailList(callback: (List<String>) -> Unit) {
        val emailApiUrl = WorkshopWashingUrlManager.fetchEmailList(ouId.toString(),locId.toString())
        val client = OkHttpClient()
        val request = Request.Builder().url(emailApiUrl).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@WorkshopWashingReport, "Failed to fetch emails: ${e.message}", Toast.LENGTH_SHORT).show()
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

        progressBar.visibility = View.VISIBLE
        TextProgressBar.text = "Email Sending in Progress...Please wait..."
        TextProgressBar.visibility = View.VISIBLE

        val inputFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH)

        val formattedFromDate = try {
            outputFormat.format(inputFormat.parse(fromDate)!!)
        } catch (e: Exception) {
            e.printStackTrace()
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
            Toast.makeText(this, "Invalid To Date format", Toast.LENGTH_SHORT).show()
            return
        }

        val url = WorkshopWashingUrlManager.postSendReportUrl()
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
//            val client = OkHttpClient()
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
                        progressBar.visibility = View.GONE
                        TextProgressBar.visibility = View.GONE
                        TextProgressBar.text = "Data is loading....."

                        if (response.isSuccessful && responseBodyStr != null) {
                            Log.d("ResponseBody", responseBodyStr)
                            val jsonResponse = JSONObject(responseBodyStr)
                            val message = jsonResponse.optString("message", "Report sent!")

                            Toast.makeText(
                                this@WorkshopWashingReport,
                                message,
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                this@WorkshopWashingReport,
                                "Failed to send mail. Error code: ${response.code}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        TextProgressBar.visibility = View.GONE
                        Toast.makeText(
                            this@WorkshopWashingReport,
                            "Error sending data: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        }
    }


    private fun downloadReport() {
        if (fromDate.isEmpty() || toDate.isEmpty()) {
            Toast.makeText(this, "Please select both From Date and To Date", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE
        TextProgressBar.text = "Downloading report...Please wait..."
        TextProgressBar.visibility = View.VISIBLE

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1001
                )
                return
            }
        }

        val inputFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
        outputFormat.isLenient = false

        val formattedFromDate = try {
            val parsedDate = inputFormat.parse(fromDate)!!
            outputFormat.format(parsedDate).toUpperCase(Locale.ENGLISH)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Invalid From Date format", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE
            TextProgressBar.visibility = View.GONE
            return
        }

        val formattedToDate = try {
            val parsedDate = inputFormat.parse(toDate)!!
            outputFormat.format(parsedDate).toUpperCase(Locale.ENGLISH)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Invalid To Date format", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE
            TextProgressBar.visibility = View.GONE
            return
        }

        val downloadUrl = WorkshopWashingUrlManager.getVehWashingMainReportUrl(ouId, locId, formattedFromDate, formattedToDate)

        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url(downloadUrl)
            .build()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()

                if (!response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        progressBar.visibility = View.GONE
                        TextProgressBar.visibility = View.GONE
                        Toast.makeText(
                            this@WorkshopWashingReport,
                            "Download failed: ${response.code}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return@launch
                }

                val responseBody = response.body
                if (responseBody == null) {
                    withContext(Dispatchers.Main) {
                        progressBar.visibility = View.GONE
                        TextProgressBar.visibility = View.GONE
                        Toast.makeText(
                            this@WorkshopWashingReport,
                            "Empty response from server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return@launch
                }

                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = "VehWashingReport_${timeStamp}.xls"

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                        put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.ms-excel")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    }

                    val resolver = contentResolver
                    val uri: Uri? = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

                    if (uri != null) {
                        resolver.openOutputStream(uri).use { outputStream ->
                            if (outputStream != null) {
                                responseBody.byteStream().copyTo(outputStream)
                            }
                        }

                        withContext(Dispatchers.Main) {
                            progressBar.visibility = View.GONE
                            TextProgressBar.visibility = View.GONE
                            Toast.makeText(
                                this@WorkshopWashingReport,
                                "Report downloaded successfully to Downloads folder",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            progressBar.visibility = View.GONE
                            TextProgressBar.visibility = View.GONE
                            Toast.makeText(
                                this@WorkshopWashingReport,
                                "Failed to create file",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val file = File(downloadsDir, fileName)

                    FileOutputStream(file).use { outputStream ->
                        responseBody.byteStream().copyTo(outputStream)
                    }

                    withContext(Dispatchers.Main) {
                        progressBar.visibility = View.GONE
                        TextProgressBar.visibility = View.GONE
                        Toast.makeText(
                            this@WorkshopWashingReport,
                            "Report downloaded successfully to Downloads folder",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    TextProgressBar.visibility = View.GONE
                    Toast.makeText(
                        this@WorkshopWashingReport,
                        "Error downloading report: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


}
