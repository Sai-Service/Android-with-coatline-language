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

class WorkshopTestDriveVehicleHistory : AppCompatActivity() {

    private var ouId: Int = 0
    private var locId: Int = 0
    private lateinit var login_name: String
    private lateinit var attribute1: String
    private lateinit var location: String
    private lateinit var tableLayout: TableLayout
    private lateinit var location_name: String
    private lateinit var intransitBtn: Button
    private lateinit var stktrfIntrBtn: Button
    private lateinit var textView13: TextView
    private lateinit var textViewStatus: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workshop_test_drive_vehicle_history)
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
            textView.setBackgroundColor(Color.parseColor("#CCCCCC"))
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
            setupTableHeader(arrayOf("ID","VEHICLE NO","JOBCARD NO","CHASSIS NO","LOCATION","OUT KM","OUT TIME","IN KM","IN TIME"))
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
            vehicle.JOBCARDNO,
            vehicle.CHASSIS_NO,
            vehicle.LOCATION,
            vehicle.OUT_KM,
            vehicle.OUT_TIME,
            vehicle.IN_KM,
            vehicle.IN_TIME
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

    private fun fetchTransferListData() {
        val client = OkHttpClient()
        val vehNo=textViewStatus.text.toString()
        if(vehNo.isEmpty()){
            Toast.makeText(this@WorkshopTestDriveVehicleHistory, "Please enter vehicle number...", Toast.LENGTH_SHORT).show()
            return
        }
        val url = "${ApiFile.APP_URL}/service/wsVehTestDriveHistory?regNo=$vehNo"

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
                            CHASSIS_NO = item.getString("CHASSIS_NO"),
                            REG_NO = item.getString("REG_NO"),
                            JOBCARDNO = item.getString("JOBCARDNO"),
                            CREATION_DATE = item.getString("CREATION_DATE"),
                            CREATED_BY = item.getString("CREATED_BY"),
                            IN_KM = item.getString("IN_KM"),
                            IN_TIME = item.getString("IN_TIME"),
                            LOCATION = item.getString("LOCATION"),
                            OU = item.getString("OU"),
                            OUT_KM = item.getString("OUT_KM"),
                            OUT_TIME = item.getString("OUT_TIME"),
                            REMARKS = item.getString("REMARKS"),
                            TEST_DRIVE_NO = item.getString("TEST_DRIVE_NO"),
                            UPDATION_DATE = item.getString("UPDATION_DATE"),
                            UPDATED_BY = item.getString("UPDATED_BY")
                        )
                        vehicleList.add(vehicle)
                    }
                    Log.d("FetchDataResponse", "Parsed Vehicles: ${vehicleList.size}")

                    runOnUiThread {
                        if(vehicleList.size<=0){
                            Toast.makeText(this@WorkshopTestDriveVehicleHistory, "This vehicle has not any transfer history", Toast.LENGTH_LONG).show()
                        }
                        Toast.makeText(this@WorkshopTestDriveVehicleHistory, "Details found successfully for Vehicle No.$vehNo", Toast.LENGTH_LONG).show()
                        populateTable(vehicleList)
                    }
                } else {
                    Log.e("FetchDataError", "Failed to fetch data: ${response.code}")
                    runOnUiThread {
                        Toast.makeText(this@WorkshopTestDriveVehicleHistory, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("FetchDataException", "Exception: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@WorkshopTestDriveVehicleHistory, "Failed to fetch data due to exception: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    data class RegPending(
        val IN_TIME:String,
        val OUT_KM:String,
        val JOBCARDNO:String,
        val IN_KM:String,
        val CREATED_BY:String,
        val CREATION_DATE:String,
        val REMARKS:String,
        val UPDATED_BY:String,
        val UPDATION_DATE:String,
        val LOCATION:String,
        val TEST_DRIVE_NO:String,
        val REG_NO:String,
        val OU:String,
        val CHASSIS_NO:String,
        val OUT_TIME:String
    )
}





