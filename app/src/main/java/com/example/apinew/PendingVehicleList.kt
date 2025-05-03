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

//class PendingVehicleList : AppCompatActivity() {
//
//    private var ouId: Int = 0
//    private var locId: Int = 0
//    private lateinit var login_name: String
//    private lateinit var attribute1: String
//    private lateinit var location: String
//    private lateinit var vehicleIn: Button
////        private lateinit var progressBar: ProgressBar
//    private lateinit var tableLayout: TableLayout
//    private lateinit var  location_name:String
//    private lateinit var intransitBtn:Button
//    private lateinit var stktrfIntrBtn:Button
////    private lateinit var toKms: EditText
//
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_pending_vehicle)
//        location_name = intent.getStringExtra("location_name")?:""
//        Log.d("locId",location_name.toString())
//
//
//        ouId = intent.getIntExtra("ouId", 0)
//        attribute1 = intent.getStringExtra("attribute1") ?: ""
//        login_name = intent.getStringExtra("login_name") ?: ""
//        location = intent.getStringExtra("location") ?: ""
//        location_name = intent.getStringExtra("location_name") ?: ""
//        vehicleIn=findViewById(R.id.vehicleIn)
//        tableLayout=findViewById(R.id.transferTable)
//        intransitBtn=findViewById(R.id.intransitBtn)
//        stktrfIntrBtn=findViewById(R.id.stktrfIntrBtn)
//
//        intransitBtn.setOnClickListener{
//            fetchTransferListData()
//        }
//
//        stktrfIntrBtn.setOnClickListener{
//            fetchTransferListDataIntransit()
//        }
//
////        toKm= findViewById<EditText>(R.id.toKm).setText("")
////        toKms=findViewById(R.id.toKms)
////        val toKm= toKms.toString()
////        toKm.setText("")
//
//
////        vehicleIn.setOnClickListener{
////            fetchTransferListData()
////        }
//
//    }
//
//    private fun setupTableHeader() {
//        val tableHeader = TableRow(this)
//        tableHeader.layoutParams = TableRow.LayoutParams(
//            TableRow.LayoutParams.MATCH_PARENT,
//            TableRow.LayoutParams.WRAP_CONTENT
//        )
//
//        val headers = arrayOf("ID","STOCK TRF NO", "VIN", "VEH STATUS", "TRANSFERRED BY","FROM LOCATION","FROM KM","TO KM","STOCK IN")
//        headers.forEach { header ->
//            val textView = TextView(this)
//            textView.text = header
//            textView.setPadding(16, 16, 16, 16)
//            textView.setTypeface(null, Typeface.BOLD)
//            textView.setBackgroundColor(Color.parseColor("#CCCCCC"))
//            tableHeader.addView(textView)
////            toKms.visibility=View.VISIBLE
//        }
//
//        tableLayout.addView(tableHeader)
//    }
//
//
//    private fun populateTable(vehicleList: List<CameraActivity.Vehicle2>) {
//        tableLayout.removeAllViews()
//        if (vehicleList.isEmpty()) {
//            tableLayout.visibility = View.GONE
//        } else {
//            tableLayout.visibility = View.VISIBLE
//            setupTableHeader()
//            vehicleList.forEachIndexed { index, vehicle ->
//                addDataRow(index + 1, vehicle)
//            }
//        }
//    }
//
////    private fun addDataRow(id: Int, vehicle2: CameraActivity.Vehicle2) {
////        val tableRow = TableRow(this)
////        tableRow.layoutParams = TableRow.LayoutParams(
////            TableRow.LayoutParams.MATCH_PARENT,
////            TableRow.LayoutParams.WRAP_CONTENT
////        )
////
////        val values = arrayOf(id.toString(),vehicle2.STOCK_TRF_NO, vehicle2.VIN, vehicle2.VEHSTATUS, vehicle2.TRANSFERRED_BY,vehicle2.LOCATION,vehicle2.FRMKM)
////        values.forEach { value ->
////            val textView = TextView(this)
////            textView.text = value.toString()
////            textView.setPadding(16, 16, 16, 16)
////            textView.setBackgroundResource(R.drawable.table_cell_border)
////            tableRow.addView(textView)
////        }
////
////        val inButton = Button(this)
////        inButton.text = "In"
////        val tokms=toKms.text.toString()
////        Log.d("toKm-----",toKms.toString())
////        inButton.setOnClickListener {
////            updateVehicleStatus(vehicle2.VIN, login_name,vehicle2.STOCK_TRF_NO, tokms)
////        }
////        tableRow.addView(inButton)
////
////        tableLayout.addView(tableRow)
////    }
//
//
//    private fun addDataRow(id: Int, vehicle2: CameraActivity.Vehicle2) {
//        val tableRow = TableRow(this)
//        tableRow.layoutParams = TableRow.LayoutParams(
//            TableRow.LayoutParams.MATCH_PARENT,
//            TableRow.LayoutParams.WRAP_CONTENT
//        )
//
//        val values = arrayOf(id.toString(), vehicle2.STOCK_TRF_NO, vehicle2.VIN, vehicle2.VEHSTATUS, vehicle2.TRANSFERRED_BY, vehicle2.LOCATION, vehicle2.FRMKM)
//        values.forEach { value ->
//            val textView = TextView(this)
//            textView.text = value.toString()
//            textView.setPadding(16, 16, 16, 16)
//            textView.setBackgroundResource(R.drawable.table_cell_border)
//            tableRow.addView(textView)
//        }
//
//        val toKmsEditText = EditText(this)
//        toKmsEditText.setPadding(16, 16, 16, 16)
//        toKmsEditText.setBackgroundResource(R.drawable.table_cell_border)
//        tableRow.addView(toKmsEditText)
//        val inButton = Button(this)
//        inButton.text = "In"
//        inButton.setOnClickListener {
//            val tokms = toKmsEditText.text.toString()
//            if(vehicle2.FRMKM<tokms){
//                updateVehicleStatus(vehicle2.VIN, login_name, vehicle2.STOCK_TRF_NO, tokms)
//            } else{
//                Toast.makeText(this@PendingVehicleList, "To Km must be greater than From Km", Toast.LENGTH_SHORT).show()
//            }
//            Log.d("toKm-----", tokms)
//        }
//        tableRow.addView(inButton)
//
//        tableLayout.addView(tableRow)
//    }
//
//
//    private fun updateVehicleStatus(vin: String, login_name: String,STOCK_TRF_NO :String,toKm:String) {
//        val url = "${ApiFile.APP_URL}/qrcode/updateVehicle"
//        val json = JSONObject().apply {
//            put("vin", vin)
//            put("receivedBy", login_name)
//            put("stkTrfNo",STOCK_TRF_NO )
//            put("toKm", toKm)
//
////            put("veh_status", veh_status)
//        }
//        Log.d("Login:",login_name)
////        Log.d("Login:",veh_status)
//        Log.d("URL:",url)
//
//        val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
//        Log.d("URL FOR UPADATE:",json.toString())
//        val request = Request.Builder()
//            .url(url)
//            .put(requestBody)
//            .addHeader("Content-Type", "application/json")
//            .build()
//
//        val client = OkHttpClient()
//
//        client.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                e.printStackTrace()
//                runOnUiThread {
//                    Toast.makeText(this@PendingVehicleList, "Failed to update vehicle status", Toast.LENGTH_SHORT).show()
//                }
//            }
//            override fun onResponse(call: Call, response: Response) {
//                response.use {
//                    if (it.isSuccessful) {
//                        runOnUiThread {
//                            Toast.makeText(this@PendingVehicleList, "Vehicle status updated successfully", Toast.LENGTH_SHORT).show()
////                            updateTableRow(tableRow, vehStatus, login_name)
////                            Toast.makeText(this@CameraActivity, "Veh status:$veh_status", Toast.LENGTH_SHORT).show()
//                            Toast.makeText(this@PendingVehicleList, "Login Name:$login_name", Toast.LENGTH_SHORT).show()
//                        }
//                        fetchTransferListData()
//                    } else {
//                        val responseBody = it.body?.string() ?: ""
//                        val errorMessage = if (responseBody.contains("Invalid VIN")) {
//                            "Invalid VIN"
//                        } else {
//                            "Unexpected code ${it.code}"
//                        }
//                        runOnUiThread {
//                            Toast.makeText(this@PendingVehicleList, errorMessage, Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
//            }
//        })
//    }
//
//    private fun fetchTransferListData() {
//        val client = OkHttpClient()
//        val transType ="Stock Transfer In-Transit"
//        val url = "${ApiFile.APP_URL}/qrcode/transferList?to_location=$location_name&veh_status=$transType"
//
//        Log.d("FetchData", "Request URL: $url")
//
//        val request = Request.Builder()
//            .url(url)
//            .build()
//
//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                val response = client.newCall(request).execute()
//                val responseBody = response.body?.string()
//                Log.d("FetchDataResponse", "Response: $responseBody")
//                if (response.isSuccessful && responseBody != null) {
//                    val jsonObject = JSONObject(responseBody)
//                    val jsonArray = jsonObject.getJSONArray("obj")
//                    val vehicleList = mutableListOf<CameraActivity.Vehicle2>()
//                    for (i in 0 until jsonArray.length()) {
//                        val item = jsonArray.getJSONObject(i)
//                        val vehicle = CameraActivity.Vehicle2(
//                            LOCATION = item.getString("LOCATION"),
//                            VEHSTATUS = item.getString("VEH_STATUS"),
//                            TRANSFERRED_BY = item.getString("TRANSFERRED_BY"),
////                           REASONCODE = item.getString("REASONCODE"),
//                            VIN = item.getString("VIN"),
//                            FROM_LOCATION= item.getString("FROM_LOCATION").toString(),
//                            FRMKM=item.getString("FRMKM"),
//                            STOCK_TRF_NO=item.getString("STOCK_TRF_NO")
//                        )
//                        vehicleList.add(vehicle)
//                        val fromKm=vehicle.FRMKM
//                    }
//                    Log.d("FetchDataResponse", "Parsed Vehicles: ${vehicleList.size}")
//                    runOnUiThread {
//                        populateTable(vehicleList)
//                    }
//                } else {
//                    Log.e("FetchDataError", "Failed to fetch data: ${response.code}")
//                    runOnUiThread {
//                        Toast.makeText(this@PendingVehicleList, "Failed to fetch data", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            } catch (e: IOException) {
//                e.printStackTrace()
//                Log.e("FetchDataException", "Exception: ${e.message}")
//                runOnUiThread {
//                    Toast.makeText(this@PendingVehicleList, "Failed to fetch data due to exception: ${e.message}", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
//
//}





class PendingVehicleList : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending_vehicle)
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
            textViewStatus.visibility=View.VISIBLE
            fetchTransferListData()
        }

        intransitBtn.setOnClickListener {
            textViewStatus.text = "Intransit"
            textViewStatus.visibility=View.VISIBLE
            fetchTransferListDataIntransit()
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

    private fun populateTable(vehicleList: List<CameraActivity.Vehicle2>) {
        tableLayout.removeAllViews()
        if (vehicleList.isEmpty()) {
            tableLayout.visibility = View.GONE
        } else {
            tableLayout.visibility = View.VISIBLE
            setupTableHeader(arrayOf("ID", "STOCK TRF NO", "VIN","CHASSIS NO", "VEH STATUS", "TRANSFERRED BY","FROM LOCATION","MODEL DESC","DRIVER","FROM KM", "TO KM", "STOCK IN"))
            vehicleList.forEachIndexed { index, vehicle ->
                addDataRow(index + 1, vehicle)
            }
        }
    }

    private fun populateTableForIntransit(vehicleList: List<vin_data>) {
        tableLayout.removeAllViews()
        if (vehicleList.isEmpty()) {
            tableLayout.visibility = View.GONE
        } else {
            tableLayout.visibility = View.VISIBLE
            setupTableHeader(arrayOf("ID", "VIN", "CHASSIS NO", "FUEL DESC", "MODEL DESC", "VARIANT DESC", "COLOUR", "STOCK IN"))
            vehicleList.forEachIndexed { index, vehicle ->
                addDataRowForIntransit(index + 1, vehicle)
            }
        }
    }

    private fun addDataRow(id: Int, vehicle2: CameraActivity.Vehicle2) {
        val tableRow = TableRow(this)
        tableRow.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )

        val values = arrayOf(id.toString(), vehicle2.STOCK_TRF_NO, vehicle2.VIN,vehicle2.CHASSIS_NO, vehicle2.VEHSTATUS, vehicle2.TRANSFERRED_BY, vehicle2.FROM_LOCATION,vehicle2.MODEL_DESC,vehicle2.DRIVER_NAME,vehicle2.FRMKM)
        values.forEach { value ->
            val textView = TextView(this)
            textView.text = value.toString()
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
                    val frmKmValue = vehicle2.FRMKM.toFloat()
                    val toKmValue = tokms.toFloat()

                    if (frmKmValue < toKmValue) {
                        updateVehicleStatus(vehicle2.VIN, login_name, vehicle2.STOCK_TRF_NO, tokms)
                    } else {
                        Toast.makeText(this@PendingVehicleList, "To Km must be greater than From Km", Toast.LENGTH_SHORT).show()
                    }
                    Log.d("toKm-----", tokms)
                } catch (e: NumberFormatException) {
                    Toast.makeText(this@PendingVehicleList, "Please enter valid numeric values", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@PendingVehicleList, "Please enter a value for To Km", Toast.LENGTH_SHORT).show()
            }
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

    private fun updateVehicleStatus(vin: String, login_name: String, STOCK_TRF_NO: String, toKm: String) {
        val url = "${ApiFile.APP_URL}/qrcode/updateVehicle"
        val json = JSONObject().apply {
            put("vin", vin)
            put("receivedBy", login_name)
            put("stkTrfNo", STOCK_TRF_NO)
            put("toKm", toKm)
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
                    Toast.makeText(this@PendingVehicleList, "Failed to update vehicle status", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(this@PendingVehicleList, "Vehicle status updated successfully", Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(this@PendingVehicleList, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }

    private fun updateVehicleStatus_1(vin: String, location_name: String) {
        val url = "${ApiFile.APP_URL}/qrcode/updateVehicleIntransit?vin=$vin&location=$location_name"
        val json = JSONObject().apply {
            put("vin", vin)
            put("location", location_name)
        }
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
                    Toast.makeText(this@PendingVehicleList, "Failed to update vehicle status", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(this@PendingVehicleList, "Vehicle status updated successfully", Toast.LENGTH_SHORT).show()
                        }
                        fetchTransferListDataIntransit()
                    } else {
                        val responseBody = it.body?.string() ?: ""
                        val errorMessage = if (responseBody.contains("Invalid VIN")) {
                            "Invalid VIN"
                        } else {
                            "Unexpected code ${it.code}"
                        }
                        runOnUiThread {
                            Toast.makeText(this@PendingVehicleList, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }

    private fun fetchTransferListData() {
        val client = OkHttpClient()
        val transType = "Stock Transfer In-Transit"
        val url = "${ApiFile.APP_URL}/qrcode/transferList?to_location=$location_name&veh_status=$transType"

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
                    val vehicleList = mutableListOf<CameraActivity.Vehicle2>()
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        val vehicle = CameraActivity.Vehicle2(
                            LOCATION = item.getString("LOCATION"),
                            VEHSTATUS = item.getString("VEH_STATUS"),
                            TRANSFERRED_BY = item.getString("TRANSFERRED_BY"),
                            VIN = item.getString("VIN"),
                            FROM_LOCATION = item.getString("FROM_LOCATION").toString(),
                            FRMKM = item.getString("FRMKM"),
                            STOCK_TRF_NO = item.getString("STOCK_TRF_NO"),
                            CHASSIS_NO=item.getString("CHASSIS_NO"),
                            MODEL_DESC=item.getString("MODEL_DESC"),
                            DRIVER_NAME=item.getString("DRIVER_NAME")
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
                        Toast.makeText(this@PendingVehicleList, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("FetchDataException", "Exception: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@PendingVehicleList, "Failed to fetch data due to exception: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchTransferListDataIntransit() {
        val client = OkHttpClient()
        val transType = "In-Transit"
        val url = "${ApiFile.APP_URL}/qrcode/transferListIntransit?to_location=$location_name&veh_status=$transType"

        Log.d("FetchDataIntransit", "Request URL: $url")

        val request = Request.Builder()
            .url(url)
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                Log.d("FetchDataIntransitResponse", "Response: $responseBody")
                if (response.isSuccessful && responseBody != null) {
                    val jsonObject = JSONObject(responseBody)
                    val jsonArray = jsonObject.getJSONArray("obj")
                    val vehicleList = mutableListOf<vin_data>()
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        val vehicle = vin_data(
                            VIN = item.getString("VIN"),
                            CHASSIS_NO = item.getString("CHASSIS_NO"),
                            FUEL_DESC = item.getString("FUEL_DESC"),
                            MODEL_DESC = item.getString("MODEL_DESC"),
                            VARIANT_DESC = item.getString("VARIANT_DESC"),
                            COLOUR = item.getString("COLOUR"),
                        )
                        vehicleList.add(vehicle)
                        vin_no = vehicle.VIN
                    }
                    Log.d("FetchDataIntransitResponse", "Parsed Vehicles: ${vehicleList.size}")
                    runOnUiThread {
                        populateTableForIntransit(vehicleList)
                    }
                } else {
                    Log.e("FetchDataIntransitError", "Failed to fetch data: ${response.code}")
                    runOnUiThread {
                        Toast.makeText(this@PendingVehicleList, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("FetchDataIntransitException", "Exception: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@PendingVehicleList, "Failed to fetch data due to exception: ${e.message}", Toast.LENGTH_SHORT).show()
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
}





