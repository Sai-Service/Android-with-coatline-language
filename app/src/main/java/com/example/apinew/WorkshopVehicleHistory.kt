package com.example.apinew

import android.content.Intent
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
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class WorkshopVehicleHistory : AppCompatActivity() {

    private var ouId: Int = 0
    private var locId: Int = 0
    private lateinit var login_name: String
    private lateinit var attribute1: String
    private lateinit var regNumber: String
    private lateinit var stkTrfNumber: String
    private lateinit var location: String
    private lateinit var tableLayout: TableLayout
    private lateinit var location_name: String
    private lateinit var intransitBtn: Button
    private lateinit var stktrfIntrBtn: TextView
    private lateinit var vin_no: String
    private lateinit var textView13: TextView
    private lateinit var textViewStatus: EditText
    private lateinit var inRegNo:String
    private lateinit var inStkTfNo:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workshop_vehicle_history)
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
            setupTableHeader(arrayOf("ID","JOB CARD NO","STOCK TRF NO","CHASSIS NO","VEH NO", "FROM LOCATION","TRANSFERRED BY","TO LOCATION", "RECEIVED BY","RECEIVED DATE","DRIVER", "FROM KM","TO KM","VIEW IMAGES"))
            vehicleList.forEachIndexed { index, vehicle ->
                addDataRow(index + 1, vehicle)
            }
        }
    }


//    private fun populateTableForIntransit(vehicleList: List<vin_data>) {
//        tableLayout.removeAllViews()
//        if (vehicleList.isEmpty()) {
//            tableLayout.visibility = View.GONE
//        } else {
//            tableLayout.visibility = View.VISIBLE
//            setupTableHeader(arrayOf("ID", "VIN", "CHASSIS NO", "FUEL DESC", "MODEL DESC", "VARIANT DESC", "COLOUR", "STOCK IN"))
//            vehicleList.forEachIndexed { index, vehicle ->
//                addDataRowForIntransit(index + 1, vehicle)
//            }
//        }
//    }

//    private fun addDataRow(id: Int, vehicle: RegPending) {
//        val tableRow = TableRow(this)
//        tableRow.layoutParams = TableRow.LayoutParams(
//            TableRow.LayoutParams.MATCH_PARENT,
//            TableRow.LayoutParams.WRAP_CONTENT
//        )
//
//        val values = arrayOf(
//            id.toString(),
//            vehicle.JOBCARDNO,
//            vehicle.STOCK_TRF_NO,
//            vehicle.CHASSIS_NO,
//            vehicle.REG_NO,
//            vehicle.FROM_LOCATION,
//            vehicle.MADE_BY,
//            vehicle.TO_LOCATION,
//            vehicle.RECEIVED_BY,
//            vehicle.RECD_DATE,
//            vehicle.DRIVER_NAME,
//            vehicle.FROMKM.toString(),
//            vehicle.TO_KM.toString()
//        )
//        regNumber=vehicle.REG_NO
//        stkTrfNumber=vehicle.STOCK_TRF_NO
//        values.forEach { value ->
//            val textView = TextView(this)
//            textView.text = value
//            textView.setPadding(16, 16, 16, 16)
//            textView.setBackgroundResource(R.drawable.table_cell_border)
//            tableRow.addView(textView)
//        }
//
//        val toKmsEditText = EditText(this)
//        toKmsEditText.setPadding(16, 16, 16, 16)
//        toKmsEditText.setBackgroundColor(Color.WHITE)
////        tableRow.addView(toKmsEditText)
//
//        val inButton = Button(this)
//        inButton.text = "VIEW IMAGES"
//        inButton.setOnClickListener {
//            val intent = Intent(this@WorkshopVehicleHistory, WorkshopViewTrfRecImages::class.java)
//            intent.putExtra("attribute1", attribute1)
//            intent.putExtra("REG_NO",regNumber)
//            intent.putExtra("STOCK_TRF_NO",stkTrfNumber)
//            startActivity(intent)
//        }
//        tableRow.addView(inButton)
//
//        tableLayout.addView(tableRow)
//    }

    private fun addDataRow(id: Int, vehicle: RegPending) {
        val tableRow = TableRow(this)
        tableRow.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )

        val values = arrayOf(
            id.toString(),
            vehicle.JOBCARDNO,
            vehicle.STOCK_TRF_NO,
            vehicle.CHASSIS_NO,
            vehicle.REG_NO,
            vehicle.FROM_LOCATION,
            vehicle.MADE_BY,
            vehicle.TO_LOCATION,
            vehicle.RECEIVED_BY,
            vehicle.RECD_DATE,
            vehicle.DRIVER_NAME,
            vehicle.FROMKM.toString(),
            vehicle.TO_KM.toString()
        )

        values.forEach { value ->
            val textView = TextView(this)
            textView.text = value
            textView.setPadding(16, 16, 16, 16)
            textView.setBackgroundResource(R.drawable.table_cell_border)
            tableRow.addView(textView)
        }

        val inButton = Button(this)
        inButton.text = "VIEW IMAGES"

        val stkTrfNumber = vehicle.STOCK_TRF_NO
        val regNumber = vehicle.REG_NO

        inButton.setOnClickListener {
            val intent = Intent(this@WorkshopVehicleHistory, WorkshopViewTrfRecImages::class.java)
            intent.putExtra("attribute1", attribute1)
            intent.putExtra("REG_NO", regNumber)
            intent.putExtra("STOCK_TRF_NO", stkTrfNumber)
            startActivity(intent)
        }

        tableRow.addView(inButton)
        tableLayout.addView(tableRow)
    }



    private fun addDataRowForIntransit(id: Int, vehicle: vin_data) {
        val tableRow = TableRow(this)
        tableRow.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )

        val values = arrayOf(id.toString(), vehicle.VIN, vehicle.CHASSIS_NO, vehicle.FUEL_DESC, vehicle.MODEL_DESC, vehicle.VARIANT_DESC, vehicle.COLOUR)
        values.forEach { value ->
            val textView = TextView(this)
            textView.text = value.toString()
            textView.setPadding(16, 16, 16, 16)
            textView.setBackgroundResource(R.drawable.table_cell_border)
            tableRow.addView(textView)
        }

        val inButton2 = Button(this)
        inButton2.text = "IN"
        inButton2.setOnClickListener {
            updateVehicleStatus_1(vehicle.VIN, location_name)
        }
        tableRow.addView(inButton2)

        tableLayout.addView(tableRow)
    }

    private fun updateVehicleStatus_1(vin: String, location_name: String) {
        val url = "${ApiFile.APP_URL}/qrcode/updateVehicleIntransit?vin=$vin&location=$location_name"
        val json = JSONObject().apply {
            put("vin", vin)
            put("location", location_name)
        }

        val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(url)
            .put(requestBody)
            .addHeader("Content-Type", "application/json")
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@WorkshopVehicleHistory, "Failed to update vehicle status", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(this@WorkshopVehicleHistory, "Vehicle status updated successfully", Toast.LENGTH_SHORT).show()
                        }
//                        fetchTransferListDataIntransit()
                    } else {
                        val responseBody = it.body?.string() ?: ""
                        val errorMessage = if (responseBody.contains("Invalid VIN")) {
                            "Invalid VIN"
                        } else {
                            "Unexpected code ${it.code}"
                        }
                        runOnUiThread {
                            Toast.makeText(this@WorkshopVehicleHistory, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }

    private fun fetchTransferListData() {
        val client = OkHttpClient()
        val vehNo=textViewStatus.text.toString()
        if(vehNo.isEmpty()){
            Toast.makeText(this@WorkshopVehicleHistory, "Please enter vehicle number...", Toast.LENGTH_SHORT).show()
            return
        }
        val url = "${ApiFile.APP_URL}/service/wsVehTransHist?regNo=$vehNo"


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
                            FROM_LOCATION = item.getString("FROM_LOCATION"),
                            STOCK_TRF_NO = item.getString("STOCK_TRF_NO"),
                            CHASSIS_NO = item.getString("CHASSIS_NO"),
                            DRIVER_NAME = item.getString("DRIVER_NAME"),
                            FROMKM = item.getInt("FROM_KM"),
                            TO_KM=item.getInt("TO_KM"),
                            STOCK_TRF_DATE = item.getString("STOCK_TRF_DATE"),
                            REG_NO = item.getString("REG_NO"),
                            JOBCARDNO = item.getString("JOBCARDNO"),
                            TO_LOCATION=item.getString("TO_LOCATION"),
                            RECEIVED_BY=item.getString("RECEIVED_BY"),
                            RECD_DATE = item.getString("RECD_DATE"),
                            MADE_BY=item.getString("MADE_BY")
                        )
                        vehicleList.add(vehicle)
                    }

                    runOnUiThread {
                        if(vehicleList.size<=0){
                            Toast.makeText(this@WorkshopVehicleHistory, "This vehicle has not any transfer history", Toast.LENGTH_LONG).show()
                        }
                        populateTable(vehicleList)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@WorkshopVehicleHistory, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@WorkshopVehicleHistory, "Failed to fetch data due to exception: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    data class vin_data(
        val VIN: String,
        val CHASSIS_NO: String,
        val FUEL_DESC: String,
        val MODEL_DESC: String,
        val VARIANT_DESC: String,
        val COLOUR:String
    )

    data class RegPending(
        val CHASSIS_NO: String,
        val JOBCARDNO:String,
        val FROM_LOCATION:String,
        val STOCK_TRF_NO:String,
        val STOCK_TRF_DATE:String,
        val DRIVER_NAME:String,
        val FROMKM:Int,
        val REG_NO:String,
        val TO_LOCATION:String,
        val RECEIVED_BY:String,
        val RECD_DATE:String,
        val TO_KM:Int,
        val MADE_BY:String
    )
}





