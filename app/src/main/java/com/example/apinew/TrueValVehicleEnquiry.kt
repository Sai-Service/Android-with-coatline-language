package com.example.apinew

import android.app.Activity
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

class TrueValVehicleEnquiry : Activity() {

    private lateinit var logoutButton: Button
    private lateinit var fetchChassisDataButton: Button
    private lateinit var chassisNoEditText: EditText
    private lateinit var backButton: Button
    private lateinit var camera: Button
    private lateinit var vehImage: ImageView
    private var ouId: Int = 0
    private lateinit var refreshButton: Button
    private lateinit var homePage: ImageButton
    private lateinit var location_name:String
    private lateinit var login_name: String
    private lateinit var VIN:EditText
    private lateinit var fetchChassisDataButton2:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_true_val_enq)

        ouId = intent.getIntExtra("ouId", 0)
        location_name = intent.getStringExtra("location_name") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""
        logoutButton = findViewById(R.id.logoutButton)
        fetchChassisDataButton = findViewById(R.id.fetchChassisDataButton)
        fetchChassisDataButton2=findViewById(R.id.fetchChassisDataButton2)
        chassisNoEditText = findViewById(R.id.CHASSIS_NO)
        backButton = findViewById(R.id.backButton)
        camera = findViewById(R.id.camera)
        vehImage = findViewById(R.id.VEH_IMAGE)
        homePage=findViewById(R.id.homePage)
        VIN=findViewById(R.id.VIN)


        val chassisNoFromIntent = intent.getStringExtra("REG_NO")
        if (chassisNoFromIntent != null) {
            chassisNoEditText.setText(chassisNoFromIntent)
            fetchChassisData(chassisNoFromIntent)
        }


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

        fetchChassisDataButton2.setOnClickListener {
            fetchChassisData2()
        }

        fetchChassisDataButton.setOnClickListener {
            val vehicleNo = chassisNoEditText.text.toString()
            if (vehicleNo.isNotEmpty()) {
                fetchChassisData(vehicleNo)
            } else {
                Toast.makeText(this, "Please enter a Chassis Number", Toast.LENGTH_SHORT).show()
            }
        }


        refreshButton = findViewById(R.id.refreshButton)
        refreshButton.setOnClickListener {
            resetFields()
        }
    }
    private fun resetFields() {
        chassisNoEditText.setText("")
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
        findViewById<ImageView>(R.id.VEH_IMAGE).setImageBitmap(null)

        chassisNoEditText.isEnabled = true
        VIN.isEnabled= true
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


    private fun fetchChassisData(vehicleNo: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.APP_URL}/trueValue/tvDetailsByRegNo?regNo=$vehicleNo")
//            .url("http://10.0.2.2:8081/fndcom/stockDetailsByChassis?chassis_no=$chassisNo")
            .build()
        camera.visibility = View.VISIBLE

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    val stockItem = jsonObject.getJSONArray("obj").getJSONObject(0)
                    val chassisData = ChassisData2(
                        CHASSIS_NO = stockItem.getString("CHASSIS_NO"),
                        VARIANT_DESC = stockItem.getString("VARIANT_DESC"),
                        MODEL_DESC = stockItem.getString("MODEL_DESC"),
                        VIN = stockItem.getString("VIN"),
                        DEALER_LOCATION = stockItem.getString("DEALER_LOCATION"),
                        COLOUR = stockItem.getString("COLOUR"),
                        ENGINE_NO = stockItem.getString("ENGINE_NO"),
                        VEH_STATUS = stockItem.getString("VEH_STATUS"),
                        FUEL_DESC = stockItem.getString("FUEL_DESC"),
                        REG_NO = stockItem.getString("REG_NO"),
                        MFG_YEAR = stockItem.optString("MFG_YEAR",""),
                        LOCATION = stockItem.getString("LOCATION"),
                        BUYING_LOCATION = stockItem.getString("BUYING_LOCATION"),
                        COLOUR_CODE = stockItem.getString("COLOUR_CODE"),
                        OPERATING_UNIT = stockItem.getInt("OPERATING_UNIT"),
                        UPDATEDBY = stockItem.getString("UPDATEDBY"),
                        VARIANT_CODE = stockItem.getString("VARIANT_CODE"),
                        STATUS = stockItem.getString("STATUS"),
                        MANUFACTURER = stockItem.getString("MANUFACTURER")
                    )
                    runOnUiThread {
                        populateFields(chassisData)
                        camera.visibility = View.VISIBLE
                        Toast.makeText(this@TrueValVehicleEnquiry, "Details found Successfully \n for Vehicle no. $vehicleNo", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    camera.visibility = View.GONE
                    Toast.makeText(this@TrueValVehicleEnquiry, "$vehicleNo  number not found", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }


    private fun fetchChassisData2() {
        val chassisNo=VIN.text.toString()
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.APP_URL}/trueValue/tvDetailsByChassis?chassisNo=$chassisNo")
//            .url("http://10.0.2.2:8081/fndcom/stockDetailsByChassis?chassis_no=$chassisNo")
            .build()
        camera.visibility = View.VISIBLE

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    val stockItem = jsonObject.getJSONArray("obj").getJSONObject(0)
                    val chassisData = ChassisData2(
                        CHASSIS_NO = stockItem.getString("CHASSIS_NO"),
                        VARIANT_DESC = stockItem.getString("VARIANT_DESC"),
                        MODEL_DESC = stockItem.getString("MODEL_DESC"),
                        VIN = stockItem.getString("VIN"),
                        DEALER_LOCATION = stockItem.getString("DEALER_LOCATION"),
                        COLOUR = stockItem.getString("COLOUR"),
                        ENGINE_NO = stockItem.getString("ENGINE_NO"),
                        VEH_STATUS = stockItem.getString("VEH_STATUS"),
                        FUEL_DESC = stockItem.getString("FUEL_DESC"),
                        REG_NO = stockItem.getString("REG_NO"),
                        MFG_YEAR = stockItem.optString("MFG_YEAR",""),
                        LOCATION = stockItem.getString("LOCATION"),
                        BUYING_LOCATION = stockItem.getString("BUYING_LOCATION"),
                        COLOUR_CODE = stockItem.getString("COLOUR_CODE"),
                        OPERATING_UNIT = stockItem.getInt("OPERATING_UNIT"),
                        UPDATEDBY = stockItem.getString("UPDATEDBY"),
                        VARIANT_CODE = stockItem.getString("VARIANT_CODE"),
                        STATUS = stockItem.getString("STATUS"),
                        MANUFACTURER = stockItem.getString("MANUFACTURER")
                    )
                    runOnUiThread {
                        populateFields(chassisData)
                        camera.visibility = View.VISIBLE
                        Toast.makeText(this@TrueValVehicleEnquiry, "Details found Successfully \n for Vehicle no. $chassisNo", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    camera.visibility = View.GONE
                    Toast.makeText(this@TrueValVehicleEnquiry, "$chassisNo  number not found", Toast.LENGTH_SHORT).show()
//                    findViewById<EditText>(R.id.CHASSIS_NO).apply {
//                        setText(chassisNo)
//                        isEnabled = true
//                    }
                }
            }
        }
    }

    private fun populateFields(chassisData: ChassisData2) {
        findViewById<EditText>(R.id.CHASSIS_NO).apply {
            setText(chassisData.CHASSIS_NO)
            isEnabled = false
        }
        findViewById<EditText>(R.id.VIN).apply {
            setText(chassisData.REG_NO)
            isEnabled = false
        }
        findViewById<EditText>(R.id.CHASSIS_NO).setText(chassisData.REG_NO)
        findViewById<EditText>(R.id.VIN).setText(chassisData.CHASSIS_NO)
        findViewById<TextView>(R.id.VARIANT_DESC).text = chassisData.VARIANT_DESC
        findViewById<EditText>(R.id.MODEL_DESC).setText(chassisData.MFG_YEAR)
        findViewById<TextView>(R.id.DEALER_LOCATION).text = chassisData.OPERATING_UNIT.toString()
        findViewById<EditText>(R.id.COLOUR).setText(chassisData.COLOUR_CODE)
        findViewById<EditText>(R.id.ENGINE_NO).setText(chassisData.FUEL_DESC)
        findViewById<EditText>(R.id.VEH_STATUS).setText(chassisData.DEALER_LOCATION)
        findViewById<EditText>(R.id.STATUS).setText(chassisData.COLOUR)
        findViewById<TextView>(R.id.REMARKS1).text = chassisData.STATUS
        findViewById<EditText>(R.id.FUEL_DESC).setText(chassisData.ENGINE_NO)
        findViewById<TextView>(R.id.MUL_INV_NO).text=chassisData.MANUFACTURER
        findViewById<EditText>(R.id.MUL_INV_DT).setText(chassisData.UPDATEDBY)
        findViewById<EditText>(R.id.GRN_NO).setText(chassisData.MODEL_DESC)
        findViewById<EditText>(R.id.GRN_DATE).setText(chassisData.VIN)
        findViewById<EditText>(R.id.LOCATION).setText(chassisData.LOCATION)
        findViewById<EditText>(R.id.GRN_DATE).setText(chassisData.VARIANT_CODE)
        findViewById<EditText>(R.id.SOBNO).setText(chassisData.VEH_STATUS)
        val imageUrl = "${ApiFile.APP_URL}/tvImage/tvDownload?regNo=${chassisData.REG_NO}"
        Picasso.get().load(imageUrl).into(vehImage)
    }

    private fun logout() {
        val intent = Intent(this@TrueValVehicleEnquiry, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun rd_to_cam() {
        val vehicleNo = chassisNoEditText.text.toString()
        val chassisNo = VIN.text.toString()

        if (vehicleNo.isNotEmpty()|| chassisNo.isNotEmpty()) {
            val intent = Intent(this, TrueValueUploadImage::class.java)
            intent.putExtra("REG_NO", vehicleNo)
            intent.putExtra("CHASSIS_NO", chassisNo)
            intent.putExtra("login_name", login_name)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Please enter a Vehicle Number/Chassis Number", Toast.LENGTH_SHORT).show()
        }
    }

    private fun backToHome() {
        finish()
    }

    private fun back() {
        finish()
    }
}

data class ChassisData2(
    val CHASSIS_NO: String,
    val VARIANT_DESC: String,
    val REG_NO: String,
    val MFG_YEAR: String,
    val OPERATING_UNIT: Int,
    val COLOUR_CODE: String,
    val FUEL_DESC: String,
    val LOCATION: String,
    val COLOUR: String,
    val ENGINE_NO: String,
    val UPDATEDBY: String,
    val MODEL_DESC: String,
    val VIN: String,
    val VARIANT_CODE: String,
    val VEH_STATUS: String,
    val BUYING_LOCATION: String,
    val DEALER_LOCATION:String,
    val STATUS:String,
    val MANUFACTURER:String
//    val DMS_LOC:String
)




