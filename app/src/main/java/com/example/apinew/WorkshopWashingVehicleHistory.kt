package com.example.apinew

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
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
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class WorkshopWashingVehicleHistory : AppCompatActivity() {

    private var ouId: Int = 0
    private var locId: Int = 0
    private lateinit var login_name: String
    private lateinit var attribute1: String
    private lateinit var location: String
    private lateinit var tableLayout: TableLayout
    private lateinit var location_name: String
    private lateinit var intransitBtn: Button
    private lateinit var stktrfIntrBtn: TextView
    private lateinit var textView13: TextView
    private lateinit var textViewStatus: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workshop_washing_vehicle_history)
        location_name = intent.getStringExtra("location_name") ?: ""
        ouId = intent.getIntExtra("ouId", 0)
        attribute1 = intent.getStringExtra("attribute1") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""
        location = intent.getStringExtra("location") ?: ""
        location_name = intent.getStringExtra("location_name") ?: ""
        tableLayout = findViewById(R.id.transferTable)
        intransitBtn = findViewById(R.id.intransitBtn)
        stktrfIntrBtn = findViewById(R.id.stktrfIntrBtn)
        textView13 = findViewById(R.id.textView13)
        textViewStatus = findViewById(R.id.textViewStatus)

        stktrfIntrBtn.setOnClickListener {
            fetchTransferListData()
        }

    }

    private fun setupTableHeader(headers: Array<String>) {
        val tableHeader = TableRow(this)
        tableHeader.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )

        headers.forEach { header ->
            val textView = TextView(this)
            textView.text = header
            textView.setPadding(16, 16, 16, 16)
            textView.setTypeface(null, Typeface.BOLD)
            textView.setBackgroundColor(Color.parseColor("#83BEF8"))
            tableHeader.addView(textView)
        }

        tableLayout.addView(tableHeader)
    }

    private fun populateTable(vehicleList: List<RegPending>) {
        tableLayout.removeAllViews()
        if (vehicleList.isEmpty()) {
            tableLayout.visibility = View.GONE
        } else {
            tableLayout.visibility = View.VISIBLE
            setupTableHeader(arrayOf("ID","VEHICLE NO","CHASSIS NO","MODEL","SERVICE ADVISOR","WASH SUPER VISOR","LOC ID","IN TIME","OUT TIME"))
            vehicleList.forEachIndexed { index, vehicle ->
                addDataRow(index + 1, vehicle)
            }
        }
    }

    private fun addDataRow(id: Int, vehicle: RegPending) {
        val tableRow = TableRow(this)
        tableRow.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )

        val values = arrayOf(
            id.toString(),
            vehicle.REG_NO,
            vehicle.CHASSIS_NO,
            vehicle.MODEL,
            vehicle.SERVICE_ADVISOR,
            vehicle.WASHING_SUPERVISOR,
            vehicle.LOC_ID,
            vehicle.IN_TIME,
            vehicle.OUT_TIME
        )
        values.forEach { value ->
            val textView = TextView(this)
            textView.text = value
            textView.setPadding(16, 16, 16, 16)
            textView.setBackgroundResource(R.drawable.table_cell_border)
            tableRow.addView(textView)
        }
        tableLayout.addView(tableRow)
    }

    private fun formatDateTime(dateTime: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
//            val inputFormat = SimpleDateFormat("dd-MM-yyyy'T'HH:mm", Locale.getDefault())
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


    private fun fetchTransferListData() {
        val client = OkHttpClient()
        val vehNo=textViewStatus.text.toString()
        if(vehNo.isEmpty()){
            Toast.makeText(this@WorkshopWashingVehicleHistory, "Please enter vehicle number...", Toast.LENGTH_SHORT).show()
            return
        }
        val url = "${ApiFile.APP_URL}/vehWashingReport/vehWashHistoryByRegNo?regNo=$vehNo"

        Log.d("FetchData", "Request URL: $url")

        val request = Request.Builder()
            .url(url)
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                Log.d("FetchDataResponse", "Response: $responseBody")

                if (response.isSuccessful && responseBody != null) {
                    val jsonObject = JSONObject(responseBody)
                    val jsonArray = jsonObject.getJSONArray("obj")
                    val vehicleList = mutableListOf<RegPending>()
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        val vehicle = RegPending(
                            CHASSIS_NO = item.optString("CHASSIS_NO"),
                            REG_NO = item.optString("REG_NO"),
                            DS_IN_TIME = item.optString("DS_IN_TIME"),
                            FS_IN_TIME = item.optString("FS_IN_TIME"),
                            FS_OUT_TIME = item.optString("FS_OUT_TIME"),
                            LOC_ID = item.optString("LOC_ID"),
                            MODEL = item.optString("MODEL"),
                            PROMISED_TIME = item.optString("PROMISED_TIME"),
                            SERVICE_ADVISOR = item.optString("SERVICE_ADVISOR"),
                            SS_IN_TIME = item.optString("SS_IN_TIME"),
                            SS_OUT_TIME = item.optString("SS_OUT_TIME"),
                            WASHING_SUPERVISOR = item.optString("WASHING_SUPERVISOR"),
                            IN_TIME = formatDateTime(item.optString("IN_TIME")),
                            OUT_TIME = formatDateTime(item.optString("OUT_TIME"))
                        )
                        vehicleList.add(vehicle)
                    }
                    Log.d("FetchDataResponse", "Parsed Vehicles: ${vehicleList.size}")

                    runOnUiThread {
                        if(vehicleList.size<=0){
                            Toast.makeText(this@WorkshopWashingVehicleHistory, "This vehicle has not any transfer history", Toast.LENGTH_LONG).show()
                        }
                        Toast.makeText(this@WorkshopWashingVehicleHistory, "Details found successfully for Vehicle No.$vehNo", Toast.LENGTH_LONG).show()
                        populateTable(vehicleList)
                    }
                } else {
                    Log.e("FetchDataError", "Failed to fetch data: ${response.code}")
                    runOnUiThread {
                        Toast.makeText(this@WorkshopWashingVehicleHistory, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("FetchDataException", "Exception: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@WorkshopWashingVehicleHistory, "Failed to fetch data due to exception: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    data class RegPending(
        val REG_NO:String,
        val CHASSIS_NO:String,
        val MODEL:String,
        val SERVICE_ADVISOR:String,
        val WASHING_SUPERVISOR:String,
        val PROMISED_TIME:String,
        val LOC_ID:String,
        val FS_IN_TIME:String,
        val FS_OUT_TIME:String,
        val SS_IN_TIME:String,
        val SS_OUT_TIME:String,
        val DS_IN_TIME:String,
        val IN_TIME:String,
        val OUT_TIME:String
    )
}





