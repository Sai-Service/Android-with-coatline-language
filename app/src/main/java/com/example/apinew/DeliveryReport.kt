package com.example.apinew

import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.apinew.ApiFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.max

class DeliveryReport : AppCompatActivity() {

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
    private lateinit var fromDatePicker: Button
    private lateinit var toDatePicker: Button
    private var fromDate: String = ""
    private var toDate: String = ""
    private lateinit var TextProgressBar: TextView
    private lateinit var deptName: String
    private lateinit var rowCountTextView: TextView
    private lateinit var toDateLabel: TextView
    private lateinit var fromDateLabel: TextView

    // Reusable OkHttpClient with longer timeouts for image downloads
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_countwisenew)

        rowCountTextView = findViewById(R.id.rowCountTextView)
        ouId = intent.getIntExtra("ouId", 0)
        locId = intent.getIntExtra("locId", 0)
        attribute1 = intent.getStringExtra("attribute1") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""
        location = intent.getStringExtra("location") ?: ""
        deptName = intent.getStringExtra("deptName") ?: ""
        rowCountTextView.visibility = View.GONE

        fromDatePicker = findViewById(R.id.fromDatePicker)
        toDatePicker = findViewById(R.id.toDatePicker)
        fromDateLabel = findViewById(R.id.fromDateLabel)
        toDateLabel = findViewById(R.id.toDateLabel)
        fetchData = findViewById(R.id.fetchData)
        progressBar = findViewById(R.id.progressBar)
        tableLayout = findViewById(R.id.tableLayout)
        headerTableLayout = findViewById(R.id.headerTableLayout)
        headerHorizontalScrollView = findViewById(R.id.headerHorizontalScrollView)
        dataHorizontalScrollView = findViewById(R.id.dataHorizontalScrollView)
        TextProgressBar = findViewById(R.id.TextProgressBar)
        TextProgressBar.visibility = View.GONE

        val calendar = Calendar.getInstance()

        val dateSetListener = { textView: TextView, isFromDate: Boolean ->
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selectedDate = String.format(
                        Locale.getDefault(),
                        "%02d-%s-%d",
                        dayOfMonth,
                        SimpleDateFormat("MMM", Locale.getDefault()).format(
                            Date(year - 1900, month, dayOfMonth)
                        ),
                        year
                    )
                    textView.text = selectedDate
                    if (isFromDate) fromDate = selectedDate else toDate = selectedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        fromDatePicker.setOnClickListener { dateSetListener(fromDateLabel, true) }
        toDatePicker.setOnClickListener { dateSetListener(toDateLabel, false) }

        headerHorizontalScrollView.setOnScrollChangeListener { _, scrollX, _, _, _ ->
            dataHorizontalScrollView.scrollTo(scrollX, dataHorizontalScrollView.scrollY)
        }
        dataHorizontalScrollView.setOnScrollChangeListener { _, scrollX, _, _, _ ->
            headerHorizontalScrollView.scrollTo(scrollX, headerHorizontalScrollView.scrollY)
        }

        fetchData.setOnClickListener {
            if (fromDate.isEmpty() || toDate.isEmpty()) {
                Toast.makeText(this, "Please select both dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            rowCountTextView.visibility = View.GONE
            tableLayout.removeAllViews()
            headerTableLayout.removeAllViews()
            fetchCountWise()
        }
    }

    // ─────────────────────────────────────────────────────────────
    // DATE HELPER
    // ─────────────────────────────────────────────────────────────

    private fun getIncrementedDate(date: String): String {
        return try {
            val inputFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
            val cal = Calendar.getInstance()
            cal.time = inputFormat.parse(date)!!
            cal.add(Calendar.DATE, 1)
            inputFormat.format(cal.time)
        } catch (e: Exception) {
            date
        }
    }

    // ─────────────────────────────────────────────────────────────
    // FETCH DATA
    // ─────────────────────────────────────────────────────────────

    private fun fetchCountWise() {
        rowCountTextView.visibility = View.GONE
        tableLayout.removeAllViews()
        headerTableLayout.removeAllViews()

        val incrementedToDate = getIncrementedDate(toDate)
        val url = ApiFile.DELIVERY_APP_URL +
                "/vehDelvTrans/getTransactionsByDays?driverLocId=$locId&fromDate=$fromDate&toDate=$incrementedToDate"

        Log.d("URL-->", url)
        progressBar.visibility = View.VISIBLE
        TextProgressBar.visibility = View.VISIBLE

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = httpClient.newCall(Request.Builder().url(url).build()).execute()
                val jsonData = response.body?.string()

                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    val jsonArray = jsonObject.getJSONArray("obj")

                    // Each row: 16 text columns + 1 image path (index 16)
                    val summaryDataList = mutableListOf<List<String>>()

                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        val data = listOf(
                            item.optString("SR_NO"),
                            item.optString("VEHICLE_NO"),
                            item.optString("INVOICE_NO"),
                            item.optString("TRANSACTION_NO"),
                            item.optString("PAYMENT_TRANS_NO"),
                            item.optString("CUST_NAME"),
                            item.optString("CUST_CONTACT_NO"),
                            item.optString("OTP_SEND_NUMBER"),
                            item.optString("DRIVER_NAME"),
                            item.optString("DRIVER_LOC_ID"),
                            item.optString("DRIVER_CONTACT_NO"),
                            item.optString("PAYMENT_TYPE"),
                            item.optString("RECEIPT_METHOD_ID"),
                            item.optString("AMOUNT_DUE_REMAINING"),
                            item.optString("AMOUNT_PAID"),
                            item.optString("AMOUNT_PENDING"),
                            item.optString("PAYMENT_IMAGE")   // index 16 — image path
                        )
                        summaryDataList.add(data)
                    }

                    runOnUiThread {
                        updateTableView(summaryDataList)
                        progressBar.visibility = View.GONE
                        TextProgressBar.visibility = View.GONE
                        rowCountTextView.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    TextProgressBar.visibility = View.GONE
                    Toast.makeText(this@DeliveryReport, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // BUILD TABLE
    // ─────────────────────────────────────────────────────────────

    private fun updateTableView(stockItems: List<List<String>>) {
        tableLayout.removeAllViews()
        headerTableLayout.removeAllViews()
        rowCountTextView.text = ""

        val headers = listOf(
            "SR.NO.", "VEH NO", "INVOICE NO", "TRANSACTION NO", "PAYMENT TRANS NO",
            "CUST NAME", "CUST CONTACT", "OTP SENT ON", "DRIVER NAME", "DRIVER LOC ID",
            "DRIVER CONTACT", "PAYMENT TYPE", "RECEIPT METHOD ID",
            "AMOUNT DUE", "AMOUNT PAID", "AMOUNT PENDING",
            "PAYMENT PROOF"   // header for image column
        )

        val textViewPadding = 24 * 2
        val maxWidths = MutableList(headers.size) { 0 }

        // Measure header widths
        for ((index, header) in headers.withIndex()) {
            val tv = createTextView(header, true)
            tv.measure(0, 0)
            maxWidths[index] = tv.measuredWidth + textViewPadding
        }

        // Measure data cell widths (text columns only; image column fixed width)
        for (row in stockItems) {
            for ((index, data) in row.withIndex()) {
                if (index == 16) {
                    // Image column: fixed 120dp regardless of path length
                    maxWidths[index] = max(maxWidths[index], (120 * resources.displayMetrics.density).toInt())
                } else {
                    val tv = createTextView(data, false)
                    tv.measure(0, 0)
                    maxWidths[index] = max(maxWidths[index], tv.measuredWidth + textViewPadding)
                }
            }
        }

        // Header row
        val headerRow = TableRow(this)
        for ((index, header) in headers.withIndex()) {
            val tv = createTextView(header, true)
            tv.layoutParams = TableRow.LayoutParams(maxWidths[index], TableRow.LayoutParams.WRAP_CONTENT)
            headerRow.addView(tv)
        }
        headerTableLayout.addView(headerRow)

        // Data rows
        for (item in stockItems) {
            val dataRow = TableRow(this)

            for ((index, data) in item.withIndex()) {
                if (index == 16) {
                    // ── IMAGE COLUMN ──────────────────────────────────────
                    val cellView: View
                    val imagePath = data.trim()

                    if (imagePath.isEmpty() || imagePath == "-") {
                        // No image — show plain "N/A" text
                        val tv = createTextView("N/A", false)
                        tv.gravity = Gravity.CENTER
                        tv.layoutParams = TableRow.LayoutParams(
                            maxWidths[index],
                            TableRow.LayoutParams.WRAP_CONTENT
                        )
                        cellView = tv
                    } else {
                        // Has image — show eye icon button
                        val btn = ImageButton(this)
                        btn.setImageDrawable(
                            androidx.core.content.ContextCompat.getDrawable(
                                this, android.R.drawable.ic_menu_view
                            )
                        )
                        btn.setColorFilter(Color.parseColor("#205295"))
                        btn.setBackgroundColor(Color.TRANSPARENT)
                        btn.setPadding(16, 8, 16, 8)
                        btn.layoutParams = TableRow.LayoutParams(
                            maxWidths[index],
                            TableRow.LayoutParams.WRAP_CONTENT
                        )
                        btn.setOnClickListener {
                            downloadAndShowImage(imagePath)
                        }
                        cellView = btn
                    }
                    dataRow.addView(cellView)

                } else {
                    // ── TEXT COLUMN ───────────────────────────────────────
                    val tv = createTextView(data, false)
                    tv.layoutParams = TableRow.LayoutParams(
                        maxWidths[index],
                        TableRow.LayoutParams.WRAP_CONTENT
                    )
                    dataRow.addView(tv)
                }
            }
            tableLayout.addView(dataRow)
        }

        rowCountTextView.text = "Total Rows: ${stockItems.size}"
    }

    // ─────────────────────────────────────────────────────────────
    // DOWNLOAD IMAGE & SHOW IN DIALOG
    // ─────────────────────────────────────────────────────────────

    private fun downloadAndShowImage(imagePath: String) {
        // Show loading dialog while downloading
        val loadingDialog = AlertDialog.Builder(this)
            .setMessage("Loading payment proof...")
            .setCancelable(false)
            .create()
        loadingDialog.show()

        val encodedPath = java.net.URLEncoder.encode(imagePath, "UTF-8")
        val imageUrl = "${ApiFile.DELIVERY_APP_URL}/vehDelvTrans/downloadPayProof?path=$encodedPath"
        Log.d("ImageDownload", "Downloading: $imageUrl")

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = httpClient.newCall(
                    Request.Builder().url(imageUrl).build()
                ).execute()

                val bytes = response.body?.bytes()

                runOnUiThread {
                    loadingDialog.dismiss()

                    if (bytes != null && bytes.isNotEmpty()) {
                        val bitmap: Bitmap? = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                        if (bitmap != null) {
                            showImageDialog(bitmap)
                        } else {
                            Toast.makeText(
                                this@DeliveryReport,
                                "Could not decode image.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@DeliveryReport,
                            "Image not found on server.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("ImageDownload", "Error: ${e.message}")
                runOnUiThread {
                    loadingDialog.dismiss()
                    Toast.makeText(
                        this@DeliveryReport,
                        "Failed to load image: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun showImageDialog(bitmap: Bitmap) {
        val imageView = ImageView(this).apply {
            setImageBitmap(bitmap)
            scaleType = ImageView.ScaleType.FIT_CENTER
            adjustViewBounds = true
            setPadding(8, 8, 8, 8)
        }

        AlertDialog.Builder(this)
            .setTitle("Payment Proof")
            .setView(imageView)
            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    // ─────────────────────────────────────────────────────────────
    // CREATE TEXT VIEW HELPER
    // ─────────────────────────────────────────────────────────────

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