package com.example.apinew

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputFilter
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

class WorkshopWashingGoaHistory : AppCompatActivity() {

    private var ouId: Int = 0
    private var locId: Int = 0
    private lateinit var login_name: String
    private lateinit var attribute1: String
    private lateinit var location: String
    private lateinit var tableLayout: TableLayout
    private lateinit var location_name: String
    private lateinit var stktrfIntrBtn: TextView
    private lateinit var textView13: TextView
    private lateinit var textViewStatus: EditText
    private lateinit var chassisBtn:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workshop_washing_history_goa)
        location_name = intent.getStringExtra("location_name") ?: ""
        ouId = intent.getIntExtra("ouId", 0)
        attribute1 = intent.getStringExtra("attribute1") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""
        location = intent.getStringExtra("location") ?: ""
        location_name = intent.getStringExtra("location_name") ?: ""
        tableLayout = findViewById(R.id.transferTable)
        stktrfIntrBtn = findViewById(R.id.stktrfIntrBtn)
        textView13 = findViewById(R.id.textView13)
        textViewStatus = findViewById(R.id.textViewStatus)
        chassisBtn = findViewById(R.id.chassisBtn)

        stktrfIntrBtn.setOnClickListener {
            fetchVehicleHistory()
        }

        chassisBtn.setOnClickListener { fetchVehicleHistoryByChassisNumber() }

        val alphaNumericFilter = InputFilter { source, _, _, _, _, _ ->
            if (source.all { it.isLetterOrDigit() }) null else ""
        }

        val lengthFilter = InputFilter.LengthFilter(10)


        val textViewStatus = findViewById<EditText>(R.id.textViewStatus)
        textViewStatus.filters = arrayOf(alphaNumericFilter,lengthFilter)

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
            textView.setBackgroundColor(Color.parseColor("#FFD700"))
            textView.setBackgroundResource(R.drawable.table_cell_header_border_washing)
            textView.setTextColor(Color.BLUE)
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
            setupTableHeader(arrayOf("SR NO","VEHICLE NO","CHASSIS NO","MODEL","SERVICE ADVISOR","WASH SUPER VISOR","LOC ID","LOCATION",
                "IN TIME","BODY WASH","FULL WASH","DRY BODY WASH","DRY WASH FULL","STATUS",
                "OUT TIME"))
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
            vehicle.LOCATION,
            vehicle.IN_TIME,
            vehicle.BODY_WASH,
            vehicle.FULL_WASH,
            vehicle.DRY_BODY_WASH,
            vehicle.DRY_WASH_FULL,
            vehicle.STATUS,
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


    private fun populateTableForChassisSearch(vehicleList: List<chassisValues>) {
        tableLayout.removeAllViews()
        if (vehicleList.isEmpty()) {
            tableLayout.visibility = View.GONE
        } else {
            tableLayout.visibility = View.VISIBLE
            setupTableHeader(arrayOf("SR NO","CHASSIS NO","VIN","MODEL","SERVICE ADVISOR","WASH SUPER VISOR","LOC ID","1ST STAGE IN TIME",
                "AIR BLOW STATION","UNDERBODY STATION","ENGINE ROOM STATION","1ST STAGE OUT TIME","2ND STAGE IN TIME","LOOSE ITEM STATION","VEHICLE INTERIOR STATION",
                "3 RD STAGE IN TIME","VEHICLE EXTERIOR STATION","GLASS POLISH STATION","3RD STAGE OUT TIME"))
            vehicleList.forEachIndexed { index, vehicle ->
                addDataRow2(index + 1, vehicle)
            }
        }
    }

    private fun addDataRow2(id: Int, vehicle: chassisValues) {
        val tableRow = TableRow(this)
        tableRow.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )

        val values = arrayOf(
            id.toString(),
            vehicle.CHASSIS_NO,
            vehicle.VIN,
            vehicle.MODEL,
            vehicle.SERVICE_ADVISOR,
            vehicle.WASHING_SUPERVISOR,
            vehicle.LOC_ID,
            vehicle.FS_IN_TIME,
            vehicle.AIR_BLOW_STN,
            vehicle.UNDERBODY_STN,
            vehicle.ENGINE_ROOM_STN,
            vehicle.FS_OUT_TIME,
            vehicle.SS_IN_TIME,
            vehicle.LOOSE_ITEMS_STN,
            vehicle.VEH_INTERIOR_STN,
            vehicle.DS_IN_TIME,
            vehicle.VEH_EXTERIOR_STN,
            vehicle.GLASS_POLISH_STN,
            vehicle.DS_OUT_TIME
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


    private fun fetchVehicleHistory() {
        val client = OkHttpClient()
        val vehNo=textViewStatus.text.toString()
        if(vehNo.isEmpty()){
            Toast.makeText(this@WorkshopWashingGoaHistory, "Please enter vehicle number...", Toast.LENGTH_SHORT).show()
            return
        }
        val url = "${ApiFile.APP_URL}/washingRegisterGa/vehWashHistoryByRegNoGa?regNo=$vehNo"
//        val url=WorkshopWashingUrlManager.getWashingVehicleHistory(vehNo)

        val request = Request.Builder()
            .url(url)
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    val jsonObject = JSONObject(responseBody)
                    val jsonArray = jsonObject.getJSONArray("obj")
                    val vehicleList = mutableListOf<RegPending>()
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        val vehicle = RegPending(
                            CHASSIS_NO = item.optString("CHASSIS_NO"),
                            REG_NO = item.optString("REG_NO"),
                            LOC_ID = item.optString("LOC_ID"),
                            MODEL = item.optString("MODEL"),
                            SERVICE_ADVISOR = item.optString("SERVICE_ADVISOR"),
                            WASHING_SUPERVISOR = item.optString("WASHING_SUPERVISOR"),
                            IN_TIME = formatDateTime(item.optString("IN_TIME")),
                            OUT_TIME = formatDateTime(item.optString("OUT_TIME")),
                            BODY_WASH = item.optString("BODY_WASH"),
                            DRY_BODY_WASH = item.optString("DRY_BODY_WASH"),
                            DRY_WASH_FULL = item.optString("DRY_WASH_FULL"),
                            FULL_WASH = item.optString("FULL_WASH"),
                            LOCATION = item.optString("LOCATION"),
                            UPDATED_BY = item.optString("UPDATED_BY"),
                            CREATED_BY = item.optString("CREATED_BY"),
                            STATUS = item.optString("STATUS"),
                            OU_ID = item.optString("OU_ID")
                        )
                        vehicleList.add(vehicle)
                    }

                    runOnUiThread {
                        if(vehicleList.size<=0){
                            Toast.makeText(this@WorkshopWashingGoaHistory, "This vehicle has not any history", Toast.LENGTH_LONG).show()
                            return@runOnUiThread
                        }
                        Toast.makeText(this@WorkshopWashingGoaHistory, "Details found successfully for Vehicle No.$vehNo", Toast.LENGTH_LONG).show()
                        populateTable(vehicleList)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@WorkshopWashingGoaHistory, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@WorkshopWashingGoaHistory, "Failed to fetch data due to exception: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun fetchVehicleHistoryByChassisNumber() {
        val client = OkHttpClient()
        val chassisNo=textViewStatus.text.toString()
        if(chassisNo.isEmpty()){
            Toast.makeText(this@WorkshopWashingGoaHistory, "Please enter chassis number...", Toast.LENGTH_SHORT).show()
            return
        }
        val url = "${ApiFile.APP_URL}/washingRegisterGa/vehWashHistoryByChassisNoGa?chassisNo=$chassisNo"
//        val url=WorkshopWashingUrlManager.getWashingVehicleHistoryByChassisNumber(chassisNo,ou)

        val request = Request.Builder()
            .url(url)
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    val jsonObject = JSONObject(responseBody)
                    val jsonArray = jsonObject.getJSONArray("obj")
                    val vehicleList = mutableListOf<RegPending>()
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        val vehicle = RegPending(
                            CHASSIS_NO = item.optString("CHASSIS_NO"),
                            REG_NO = item.optString("REG_NO"),
                            LOC_ID = item.optString("LOC_ID"),
                            MODEL = item.optString("MODEL"),
                            SERVICE_ADVISOR = item.optString("SERVICE_ADVISOR"),
                            WASHING_SUPERVISOR = item.optString("WASHING_SUPERVISOR"),
                            IN_TIME = formatDateTime(item.optString("IN_TIME")),
                            OUT_TIME = formatDateTime(item.optString("OUT_TIME")),
                            BODY_WASH = item.optString("BODY_WASH"),
                            DRY_BODY_WASH = item.optString("DRY_BODY_WASH"),
                            DRY_WASH_FULL = item.optString("DRY_WASH_FULL"),
                            FULL_WASH = item.optString("FULL_WASH"),
                            LOCATION = item.optString("LOCATION"),
                            UPDATED_BY = item.optString("UPDATED_BY"),
                            CREATED_BY = item.optString("CREATED_BY"),
                            STATUS = item.optString("STATUS"),
                            OU_ID = item.optString("OU_ID")
                        )
                        vehicleList.add(vehicle)
                    }

                    runOnUiThread {
                        if(vehicleList.size<=0){
                            Toast.makeText(this@WorkshopWashingGoaHistory, "This vehicle has not any history", Toast.LENGTH_LONG).show()
                            return@runOnUiThread
                        }
                        Toast.makeText(this@WorkshopWashingGoaHistory, "Details found successfully for Vehicle No.$chassisNo", Toast.LENGTH_LONG).show()
                        populateTable(vehicleList)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@WorkshopWashingGoaHistory, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@WorkshopWashingGoaHistory, "Failed to fetch data due to exception: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    data class RegPending(
        val REG_NO:String,
        val OU_ID:String,
        val WASHING_SUPERVISOR:String,
        val IN_TIME: String,
        val FULL_WASH:String,
        val DRY_BODY_WASH:String,
        val CREATED_BY: String,
        val LOC_ID:String,
        val LOCATION:String,
        val SERVICE_ADVISOR:String,
        val UPDATED_BY:String,
        val OUT_TIME:String,
        val DRY_WASH_FULL:String,
        val STATUS:String,
        val MODEL:String,
        val BODY_WASH:String,
        val CHASSIS_NO:String
    )

    data class chassisValues(
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
        val OUT_TIME:String,
        val AIR_BLOW_STN:String,
        val UNDERBODY_STN:String,
        val ENGINE_ROOM_STN:String,
        val LOOSE_ITEMS_STN:String,
        val VEH_INTERIOR_STN:String,
        val VEH_EXTERIOR_STN:String,
        val GLASS_POLISH_STN:String,
        val DS_OUT_TIME:String,
        val VIN:String
    )
}