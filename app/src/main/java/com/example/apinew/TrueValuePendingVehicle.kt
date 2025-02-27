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
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class TrueValuePendingVehicle : AppCompatActivity() {

    private var ouId: Int = 0
    private var locId: Int = 0
    private lateinit var login_name: String
    private lateinit var attribute1: String
    private lateinit var location: String
    private lateinit var tableLayout: TableLayout
    private lateinit var location_name: String
    private lateinit var intransitBtn: Button
    private lateinit var stktrfIntrBtn: Button
    private lateinit var vin_no: String
    private lateinit var textView13: TextView
    private lateinit var textViewStatus: TextView
    private lateinit var inRegNo:String
    private lateinit var inStkTfNo:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_true_value_pending_veh_list)
        location_name = intent.getStringExtra("location_name") ?: ""
        Log.d("locId", location_name.toString())

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
        textViewStatus.visibility=View.GONE

        stktrfIntrBtn.setOnClickListener {
            textViewStatus.text = "Stock Transfer Intransit"
            fetchTransferListData()
        }

        intransitBtn.setOnClickListener {
            textViewStatus.text = "Intransit"
            textViewStatus.visibility=View.VISIBLE
//            fetchTransferListDataIntransit()
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
            setupTableHeader(arrayOf("ID", "STOCK TRF NO", "VIN", "CHASSIS NO","VEH NO.", "VEH STATUS", "TRANSFERRED BY", "FROM LOCATION", "MODEL DESC", "DRIVER", "FROM KM", "TO KM", "STOCK IN"))
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
            vehicle.STOCK_TRF_NO,
            vehicle.VIN,
            vehicle.CHASSIS_NO,
            vehicle.REG_NO,
            vehicle.VEH_STATUS,
            vehicle.TRANSFERRED_BY,
            vehicle.FROM_LOCATION,
            vehicle.MODEL_DESC,
            vehicle.DRIVER_NAME,
            vehicle.FROMKM.toString()
        )
        values.forEach { value ->
            val textView = TextView(this)
            textView.text = value
            textView.setPadding(16, 16, 16, 16)
            textView.setBackgroundResource(R.drawable.table_cell_border)
            tableRow.addView(textView)
        }

        val toKmsEditText = EditText(this)
        toKmsEditText.setPadding(16, 16, 16, 16)
        toKmsEditText.setBackgroundColor(Color.WHITE)
        tableRow.addView(toKmsEditText)

        val inButton = Button(this)
        inButton.text = "IN"
        inButton.setOnClickListener {
            val tokms = toKmsEditText.text.toString()
            if (tokms.isNotEmpty()) {
                try {
                    val frmKmValue = vehicle.FROMKM.toFloat()
                    val toKmValue = tokms.toFloat()

                    if (frmKmValue < toKmValue) {
                        updateVehicleStatus(vehicle.REG_NO, login_name, vehicle.STOCK_TRF_NO,tokms)
                    } else {
                        Toast.makeText(this@TrueValuePendingVehicle, "To Km must be greater than From Km", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: NumberFormatException) {
                    Toast.makeText(this@TrueValuePendingVehicle, "Please enter valid numeric values", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@TrueValuePendingVehicle, "Please enter a value for To Km", Toast.LENGTH_SHORT).show()
            }
        }
        tableRow.addView(inButton)

        tableLayout.addView(tableRow)
    }


    private fun updateVehicleStatus(inRegNo: String, login_name: String, inStkTfNo: String, toKm: String) {
        val url = "${ApiFile.APP_URL}/trueValue/tvUpdateStockIn"
        val json = JSONObject().apply {
            put("receivedBy", login_name)
            put("regNo",inRegNo)
            put("stockTrfNo",inStkTfNo)
            put("toKm",toKm)
            put("location",location_name)
            put("toLocation",location_name)
        }
        Log.d("Login:", login_name)
        Log.d("URL:", url)

        val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        Log.d("URL FOR UPDATE:", json.toString())
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
                    Toast.makeText(this@TrueValuePendingVehicle, "Failed to update vehicle status", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(this@TrueValuePendingVehicle, "Vehicle status updated successfully", Toast.LENGTH_SHORT).show()
                        }
                        fetchTransferListData()
                    } else {
                        val responseBody = it.body?.string() ?: ""
                        val errorMessage = if (responseBody.contains("Invalid VIN")) {
                            "Invalid VIN"
                        } else {
                            "Unexpected code ${it.code}"
                        }
                        runOnUiThread {
                            Toast.makeText(this@TrueValuePendingVehicle, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }


    private fun fetchTransferListData() {
        val client = OkHttpClient()
        val location=location_name
        val url = "${ApiFile.APP_URL}/trueValue/tvTransferList?to_location=$location"

        Log.d("FetchData", "Request URL: $url")
        Log.d("location---PEnding", "location: $location")

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
                            TRANSFERRED_BY = item.getString("TRANSFERRED_BY"),
                            VIN = item.getString("VIN"),
                            FROM_LOCATION = item.getString("FROM_LOCATION"),
                            STOCK_TRF_NO = item.getString("STOCK_TRF_NO"),
                            CHASSIS_NO = item.getString("CHASSIS_NO"),
                            MODEL_DESC = item.getString("MODEL_DESC"),
                            DRIVER_NAME = item.getString("DRIVER_NAME"),
                            FROMKM = item.getInt("FROMKM"),
                            STOCK_TRF_DATE = item.getString("STOCK_TRF_DATE"),
                            VARIANT_CODE = item.getString("VARIANT_CODE"),
                            VEH_STATUS = item.getString("VEH_STATUS"),
                            REG_NO = item.getString("REG_NO")

                        )
                        vehicleList.add(vehicle)
                    }
                    Log.d("FetchDataResponse", "Parsed Vehicles: ${vehicleList.size}")

                    runOnUiThread {
                        populateTable(vehicleList)
                    }
                } else {
                    Log.e("FetchDataError", "Failed to fetch data: ${response.code}")
                    runOnUiThread {
                        Toast.makeText(this@TrueValuePendingVehicle, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("FetchDataException", "Exception: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@TrueValuePendingVehicle, "Failed to fetch data due to exception: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    data class RegPending(
        val CHASSIS_NO: String,
        val VIN:String,
        val MODEL_DESC:String,
        val FROM_LOCATION:String,
        val STOCK_TRF_NO:String,
        val STOCK_TRF_DATE:String,
        val TRANSFERRED_BY:String,
        val VARIANT_CODE:String,
        val VEH_STATUS:String,
        val DRIVER_NAME:String,
        val FROMKM:Int,
        val REG_NO:String
    )
}





