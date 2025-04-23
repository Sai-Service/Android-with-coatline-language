package com.example.apinew

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale

class ChassisActivity : Activity() {

    private lateinit var logoutButton: Button
    private lateinit var fetchChassisDataButton: Button
    private lateinit var fetchVehicleDataButton:Button
    private lateinit var chassisNoEditText: EditText
    private lateinit var VEHICLE_NO:EditText
    private lateinit var backButton: Button
    private lateinit var camera: Button
    private lateinit var vehImage: ImageView
    private var ouId: Int = 0
    private lateinit var refreshButton: Button
    private lateinit var homePage: ImageButton
    private lateinit var location_name:String
    private lateinit var login_name: String
    private lateinit var vinNo:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chassis)

        ouId = intent.getIntExtra("ouId", 0)
        location_name = intent.getStringExtra("location_name") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""
        logoutButton = findViewById(R.id.logoutButton)
        fetchChassisDataButton = findViewById(R.id.fetchChassisDataButton)
        chassisNoEditText = findViewById(R.id.CHASSIS_NO)
        backButton = findViewById(R.id.backButton)
        camera = findViewById(R.id.camera)
        vehImage = findViewById(R.id.VEH_IMAGE)
        homePage=findViewById(R.id.homePage)
        VEHICLE_NO=findViewById(R.id.VEHICLE_NO)
        fetchVehicleDataButton=findViewById(R.id.fetchVehicleDataButton)
//        downloadImageButton = findViewById(R.id.downloadImageButton)


        logoutButton.setOnClickListener {
            logout()
        }


        backButton.setOnClickListener {
            back()
        }

        camera.setOnClickListener {
            rd_to_cam()
        }

        homePage.setOnClickListener {
            backToHome()
        }

        fetchChassisDataButton.setOnClickListener {
            val chassisNo = chassisNoEditText.text.toString()
            if (chassisNo.isNotEmpty()) {
                fetchChassisData(chassisNo)
            } else {
                Toast.makeText(this, "Please enter a Chassis Number", Toast.LENGTH_SHORT).show()
            }
        }


        fetchVehicleDataButton.setOnClickListener {
            val vehicleNo = VEHICLE_NO.text.toString()
            if (vehicleNo.isNotEmpty()) {
                fetchChassisData(vehicleNo)
            } else {
                Toast.makeText(this, "Please enter a Vehicle Number", Toast.LENGTH_SHORT).show()
            }
        }

        val chassisNoFromIntent = intent.getStringExtra("CHASSIS_NO")
        if (chassisNoFromIntent != null) {
            chassisNoEditText.setText(chassisNoFromIntent)
            fetchChassisData(chassisNoFromIntent)
        }


        refreshButton = findViewById(R.id.refreshButton)
        refreshButton.setOnClickListener {
            resetFields()
        }
    }

    private fun resetFields() {
        chassisNoEditText.setText("")
        VEHICLE_NO.setText("")
        findViewById<EditText>(R.id.VARIANT_DESC).setText("")
        findViewById<EditText>(R.id.MODEL_DESC).setText("")
        findViewById<EditText>(R.id.VIN).setText("")
        findViewById<TextView>(R.id.DEALER_LOCATION).text = ""
        findViewById<EditText>(R.id.COLOUR).setText("")
        findViewById<EditText>(R.id.ENGINE_NO).setText("")
        findViewById<EditText>(R.id.VEH_STATUS).setText("")
        findViewById<TextView>(R.id.REMARKS1).text = ""
        findViewById<EditText>(R.id.FUEL_DESC).setText("")
        findViewById<TextView>(R.id.MUL_INV_NO).text=""
        findViewById<EditText>(R.id.MUL_INV_DT).setText("")
        findViewById<EditText>(R.id.SOBNO).setText("")
        findViewById<EditText>(R.id.GRN_NO).setText("")
        findViewById<EditText>(R.id.GRN_DATE).setText("")
        findViewById<EditText>(R.id.LOCATION).setText("")
        findViewById<EditText>(R.id.STATUS).setText("")
        findViewById<EditText>(R.id.AGEING).setText("")
        findViewById<ImageView>(R.id.VEH_IMAGE).setImageBitmap(null)
        chassisNoEditText.isEnabled = true
        VEHICLE_NO.isEnabled = true
        camera.visibility = View.GONE
    }

    private fun formatDateTime(dateTime: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
            val outputDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val outputTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(dateTime)
            val formattedDate = date?.let { outputDateFormat.format(it) }
            val formattedTime = date?.let { outputTimeFormat.format(it) }
            "$formattedDate $formattedTime"
        } catch (e: Exception) {
            dateTime
        }
    }

    // Function to fetch data from API by Chassis No.
    private fun fetchChassisData(chassisNo: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.APP_URL}/fndcom/stockDetailsByChassis?chassis_no=$chassisNo")
            .build()
        camera.visibility = View.VISIBLE

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    Log.d("Data", it)
                    val stockArray = jsonObject.getJSONArray("obj")

                    if (stockArray.length() > 1) {
                        // If multiple VINs are present, show LOV
                        val vinList = ArrayList<String>()
                        for (i in 0 until stockArray.length()) {
                            vinList.add(stockArray.getJSONObject(i).getString("VIN"))
                        }
                        runOnUiThread {
                            showVinSelectionDialog(vinList, chassisNo)
                        }
                    } else if (stockArray.length() == 1) {
                        val stockItem = stockArray.getJSONObject(0)
                        val chassisData = parseChassisData(stockItem)
                        runOnUiThread {
                            populateFields(chassisData)
//                            adjustCameraVisibility(chassisData.VEH_STATUS)
                        }
                    } else {
                        runOnUiThread { handleNoDataFound(chassisNo) }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread { handleNoDataFound(chassisNo) }
            }
        }
    }

    private fun showVinSelectionDialog(vinList: List<String>, chassisNo: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select VIN")
        builder.setItems(vinList.toTypedArray()) { _, which ->
            val selectedVin = vinList[which]
            fetchDetailsByVin(selectedVin)
        }
        builder.show()
    }


    private fun fetchDetailsByVin(vin: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.APP_URL}/fndcom/stockDetailsByVin?vin=$vin")
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    val stockItem = jsonObject.getJSONArray("obj").getJSONObject(0)
                    val chassisData = parseChassisData(stockItem)

                    runOnUiThread {
                        populateFields(chassisData)
//                        adjustCameraVisibility(chassisData.VEH_STATUS)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread { Toast.makeText(this@ChassisActivity, "Details not found for VIN $vin", Toast.LENGTH_SHORT).show() }
            }
        }
    }

    private fun parseChassisData(stockItem: JSONObject): ChassisData {
        return ChassisData(
            CHASSIS_NO = stockItem.getString("CHASSIS_NO"),
            VARIANT_DESC = stockItem.getString("VARIANT_DESC"),
            MODEL_DESC = stockItem.getString("MODEL_DESC"),
            VIN = stockItem.getString("VIN"),
            DEALER_LOCATION = stockItem.getString("DEALER_LOCATION"),
            COLOUR = stockItem.getString("COLOUR"),
            ENGINE_NO = stockItem.getString("ENGINE_NO"),
            VEH_STATUS = stockItem.getString("VEH_STATUS"),
            FUEL_DESC = stockItem.getString("FUEL_DESC"),
            MUL_INV_NO = stockItem.getString("MUL_INV_NO"),
            MUL_INV_DT = formatDateTime(stockItem.getString("MUL_INV_DT")),
            GRN_NO = stockItem.getString("GRN_NO"),
            GRN_DATE = formatDateTime(stockItem.getString("GRN_DATE")),
            LOCATION = stockItem.getString("LOCATION"),
            ALLOTMENT_DT = formatDateTime(stockItem.getString("ALLOTMENT_DT")),
            ALLOTMENT_NO = stockItem.getString("ALLOTMENT_NO"),
            VEH_IMAGE = stockItem.optString("veh_image", ""),
            REMARKS1 = stockItem.optString("REMARKS1", ""),
            SOBNO = stockItem.optString("SOBNO", ""),
            VEH_IMAGE_NEW = stockItem.optString("VEH_IMAGE_NEW", ""),
            STATUS = stockItem.optString("STATUS", ""),
            AGEING=stockItem.optString("AGEING","")
        )
    }

//    private fun adjustCameraVisibility(vehStatus: String) {
//        camera.visibility = if (vehStatus == "DELIVERED") View.GONE else View.VISIBLE
//    }

    private fun handleNoDataFound(chassisNo: String) {
        camera.visibility = View.GONE
        Toast.makeText(this, "$chassisNo Chassis number not found", Toast.LENGTH_SHORT).show()
        chassisNoEditText.apply {
            setText(chassisNo)
            isEnabled = true
        }
    }


    private fun populateFields(chassisData: ChassisData) {
        findViewById<EditText>(R.id.CHASSIS_NO).apply {
            setText(chassisData.CHASSIS_NO)
            isEnabled = false
        }
        findViewById<EditText>(R.id.CHASSIS_NO).setText(chassisData.CHASSIS_NO)
        findViewById<EditText>(R.id.VIN).setText(chassisData.VIN)
        vinNo=chassisData.VIN
        Log.d("vinNo---> CHeck",vinNo)
        findViewById<TextView>(R.id.VARIANT_DESC).text = chassisData.VARIANT_DESC
        findViewById<EditText>(R.id.MODEL_DESC).setText(chassisData.MODEL_DESC)
        findViewById<TextView>(R.id.DEALER_LOCATION).text = chassisData.DEALER_LOCATION
        findViewById<EditText>(R.id.COLOUR).setText(chassisData.COLOUR)
        findViewById<EditText>(R.id.ENGINE_NO).setText(chassisData.ENGINE_NO)
        findViewById<EditText>(R.id.VEH_STATUS).setText(chassisData.VEH_STATUS)
        findViewById<EditText>(R.id.STATUS).setText(chassisData.STATUS)
        findViewById<TextView>(R.id.REMARKS1).text = chassisData.REMARKS1
        findViewById<EditText>(R.id.FUEL_DESC).setText(chassisData.FUEL_DESC)
        findViewById<TextView>(R.id.MUL_INV_NO).text=chassisData.MUL_INV_NO
        findViewById<EditText>(R.id.MUL_INV_DT).setText(chassisData.MUL_INV_DT)
        findViewById<EditText>(R.id.GRN_NO).setText(chassisData.GRN_NO)
        findViewById<EditText>(R.id.GRN_DATE).setText(chassisData.GRN_DATE)
        findViewById<EditText>(R.id.LOCATION).setText(chassisData.LOCATION)
        findViewById<EditText>(R.id.GRN_DATE).setText(chassisData.GRN_DATE)
        findViewById<EditText>(R.id.SOBNO).setText(chassisData.SOBNO)
        findViewById<EditText>(R.id.AGEING).setText(chassisData.AGEING)
        val imageUrl = "${ApiFile.APP_URL}/invstock/Download?vin=${chassisData.VIN}"
        Log.d("imageUrl---++++>>",imageUrl)
        Picasso.get().load(imageUrl).into(vehImage)
    }

    private fun logout() {
        val intent = Intent(this@ChassisActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun rd_to_cam() {
        val vinNumber = vinNo
        val chassisNumber=chassisNoEditText.text.toString()
        if (vinNumber.isNotEmpty()) {
            val intent = Intent(this, UploadImage::class.java)
            intent.putExtra("VIN", vinNo)
            intent.putExtra("login_name", login_name)
            intent.putExtra("CHASSIS_NO",chassisNumber)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Please enter a Chassis Number", Toast.LENGTH_SHORT).show()
        }
    }

    private fun backToHome() {
        finish()
    }


    private fun back() {
        finish()
    }
}

data class ChassisData(
    val VIN: String,
    val CHASSIS_NO: String,
    val ENGINE_NO: String,
    val MODEL_DESC: String,
    val VARIANT_DESC: String,
    val FUEL_DESC: String,
    val COLOUR: String,
    val VEH_STATUS: String,
    val GRN_NO: String,
    val GRN_DATE: String,
    val ALLOTMENT_NO: String,
    val ALLOTMENT_DT: String,
    val SOBNO: String,
    val MUL_INV_NO: String,
    val MUL_INV_DT: String,
    val DEALER_LOCATION: String,
    val REMARKS1: String,
    val LOCATION: String,
    val VEH_IMAGE: String,
    val VEH_IMAGE_NEW:String,
    val STATUS:String,
    val AGEING:String
//    val DMS_LOC:String
)





