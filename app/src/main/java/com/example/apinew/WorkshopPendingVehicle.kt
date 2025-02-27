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
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class WorkshopPendingVehicle : AppCompatActivity() {

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
    private lateinit var textViewStatus: TextView
    private lateinit var deptName:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workshop_pending_vehicle)
        location_name = intent.getStringExtra("location_name") ?: ""
        Log.d("location-name", location_name)

        ouId = intent.getIntExtra("ouId", 0)
        attribute1 = intent.getStringExtra("attribute1") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""
        location = intent.getStringExtra("location") ?: ""
        location_name = intent.getStringExtra("location_name") ?: ""
        deptName = intent.getStringExtra("deptName") ?: ""
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
            setupTableHeader(arrayOf("ID","JOB CARD NO","STOCK TRF NO", "VIN", "CHASSIS NO","VEH NO.", "VEH STATUS", "TRANSFERRED BY", "FROM LOCATION", "DRIVER", "FROM KM","VIEW IMAGES","VEHICLE IN"))
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
            vehicle.JOBCARDNO,
            vehicle.STOCK_TRF_NO,
            vehicle.VIN,
            vehicle.CHASSISNO,
            vehicle.REG_NO,
            vehicle.VEH_STATUS,
            vehicle.TRANSFERRED_BY,
            vehicle.FROM_LOCATION,
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

        val inButton = Button(this)
        inButton.text = "VIEW IMAGES"

        val vehicleIn=Button(this)
        vehicleIn.text="IN"

        val stkTrfNumber = vehicle.STOCK_TRF_NO
        val regNumber = vehicle.REG_NO

        inButton.setOnClickListener {
            val intent = Intent(this@WorkshopPendingVehicle, WorkshopViewTrfRecImages::class.java)
            intent.putExtra("attribute1", attribute1)
            intent.putExtra("REG_NO",regNumber)
            intent.putExtra("STOCK_TRF_NO",stkTrfNumber)
            startActivity(intent)
        }

        vehicleIn.setOnClickListener {
            val intent = Intent(this@WorkshopPendingVehicle, WorkShopStockReceiveWithImages::class.java)
            intent.putExtra("REG_NO",regNumber)
            intent.putExtra("STOCK_TRF_NO",stkTrfNumber)
            intent.putExtra("attribute1", attribute1)
            intent.putExtra("login_name", login_name)
            intent.putExtra("ouId", ouId)
            intent.putExtra("location", location)
            intent.putExtra("locId", locId)
            intent.putExtra("location_name", location_name)
            intent.putExtra("deptName", deptName)
            startActivity(intent)
        }
        tableRow.addView(inButton)
        tableRow.addView(vehicleIn)

        tableLayout.addView(tableRow)
    }

    private fun fetchTransferListData() {
        val client = OkHttpClient()
        val location=location_name
        val url = "${ApiFile.APP_URL}/service/srTransferList?to_location=$location&dept=$deptName"

        Log.d("FetchData", "Request URL: $url")
        Log.d("location---Pending", "location: $location")

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
                            CHASSISNO = item.getString("CHASSISNO"),
                            DRIVER_NAME = item.getString("DRIVER_NAME"),
                            FROMKM = item.getInt("FROMKM"),
                            STOCK_TRF_DATE = item.getString("STOCK_TRF_DATE"),
                            VARIANT = item.getString("VARIANT"),
                            VEH_STATUS = item.getString("VEH_STATUS"),
                            REG_NO = item.getString("REG_NO"),
                            JOBCARDNO = item.getString("JOBCARDNO"),
                            DEPT=item.getString("DEPT")
                        )
                        vehicleList.add(vehicle)
                    }
                    Log.d("FetchDataResponse", "Parsed Vehicles: ${vehicleList.size}")

                    runOnUiThread {
                        if(vehicleList.size<=0){
                            Toast.makeText(this@WorkshopPendingVehicle, "No Pending vehicles present at $location_name", Toast.LENGTH_LONG).show()
                        }
                        populateTable(vehicleList)
                    }
                } else {
                    Log.e("FetchDataError", "Failed to fetch data: ${response.code}")
                    runOnUiThread {
                        Toast.makeText(this@WorkshopPendingVehicle, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("FetchDataException", "Exception: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@WorkshopPendingVehicle, "Failed to fetch data due to exception: ${e.message}", Toast.LENGTH_SHORT).show()
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
        val CHASSISNO: String,
        val VIN:String,
        val JOBCARDNO:String,
        val FROM_LOCATION:String,
        val STOCK_TRF_NO:String,
        val STOCK_TRF_DATE:String,
        val TRANSFERRED_BY:String,
        val VARIANT:String,
        val VEH_STATUS:String,
        val DRIVER_NAME:String,
        val FROMKM:Int,
        val REG_NO:String,
        val DEPT:String
    )
}





