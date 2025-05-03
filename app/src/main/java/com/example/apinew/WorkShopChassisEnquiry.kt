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

class WorkShopChassisEnquiry : Activity() {

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
        setContentView(R.layout.activity_workshop_chassis_enquiry)

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


        val chassisNoFromIntent = intent.getStringExtra("REGNO")
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
        findViewById<EditText>(R.id.JOB_C_DATE).setText("")
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
            .url("${ApiFile.APP_URL}/service/srDetailsByRegNo?regNo=$vehicleNo")
            .build()
        camera.visibility = View.VISIBLE

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    val stockItem = jsonObject.getJSONArray("obj").getJSONObject(0)
                    val chassisData = ChassisData3(
                        CHASSISNO = stockItem.getString("CHASSISNO"),
                        VARIANT = stockItem.getString("VARIANT"),
                        MODELDESC = stockItem.getString("MODELDESC"),
                        VIN = stockItem.getString("VIN"),
                        DMSCUSTID = stockItem.getString("DMSCUSTID"),
                        COLOR = stockItem.getString("COLOR"),
                        ENGINENO = stockItem.getString("ENGINENO"),
                        VEH_STATUS = stockItem.getString("VEH_STATUS"),
                        JOBCARDNO = stockItem.getString("JOBCARDNO"),
                        CONTACTNO = stockItem.getString("CONTACTNO"),
                        CUSTNAME = stockItem.getString("CUSTNAME"),
                        DMSLOCATION = stockItem.getString("DMSLOCATION"),
                        ERPACCTNO = stockItem.getString("ERPACCTNO"),
                        OUID = stockItem.getString("OUID"),
                        PHYSICALLOCATION = stockItem.getString("PHYSICALLOCATION"),
                        REGDATE = stockItem.getString("REGDATE"),
                        REGNO = stockItem.getString("REGNO"),
                        SERVICEADVISOR = stockItem.getString("SERVICEADVISOR"),
                        STATUS = stockItem.getString("STATUS"),
                        COLORDESC = stockItem.getString("COLORDESC"),
                        JOBCARDDATE=formatDateTime(stockItem.getString("JOBCARDDATE"))
                    )
                    runOnUiThread {
                        populateFields(chassisData)
                        camera.visibility = View.VISIBLE
                        Toast.makeText(this@WorkShopChassisEnquiry, "Details found Successfully \n for Vehicle no. $vehicleNo", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    camera.visibility = View.GONE
                    Toast.makeText(this@WorkShopChassisEnquiry, "$vehicleNo  number not found", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }


    private fun fetchChassisData2() {
        val chassisNo=VIN.text.toString()
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.APP_URL}/service/srDetailsByJobcardNo?jobCardNo=$chassisNo")
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
                    val chassisData = ChassisData3(
                        CHASSISNO = stockItem.getString("CHASSISNO"),
                        VARIANT = stockItem.getString("VARIANT"),
                        MODELDESC = stockItem.getString("MODELDESC"),
                        VIN = stockItem.getString("VIN"),
                        DMSCUSTID = stockItem.getString("DMSCUSTID"),
                        COLOR = stockItem.getString("COLOR"),
                        ENGINENO = stockItem.getString("ENGINENO"),
                        VEH_STATUS = stockItem.getString("VEH_STATUS"),
                        JOBCARDNO = stockItem.getString("JOBCARDNO"),
                        CONTACTNO = stockItem.getString("CONTACTNO"),
                        CUSTNAME = stockItem.getString("CUSTNAME"),
                        DMSLOCATION = stockItem.getString("DMSLOCATION"),
                        ERPACCTNO = stockItem.getString("ERPACCTNO"),
                        OUID = stockItem.getString("OUID"),
                        PHYSICALLOCATION = stockItem.getString("PHYSICALLOCATION"),
                        REGDATE = stockItem.getString("REGDATE"),
                        REGNO = stockItem.getString("REGNO"),
                        SERVICEADVISOR = stockItem.getString("SERVICEADVISOR"),
                        STATUS = stockItem.getString("STATUS"),
                        COLORDESC = stockItem.getString("COLORDESC"),
                        JOBCARDDATE=formatDateTime(stockItem.getString("JOBCARDDATE"))
                    )
                    runOnUiThread {
                        populateFields(chassisData)
                        camera.visibility = View.VISIBLE
                        Toast.makeText(this@WorkShopChassisEnquiry, "Details found Successfully \n for Job Card no. $chassisNo", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    camera.visibility = View.GONE
                    Toast.makeText(this@WorkShopChassisEnquiry, "$chassisNo  number not found", Toast.LENGTH_SHORT).show()
//                    findViewById<EditText>(R.id.CHASSIS_NO).apply {
//                        setText(chassisNo)
//                        isEnabled = true
//                    }
                }
            }
        }
    }

    private fun populateFields(chassisData: ChassisData3) {
        findViewById<EditText>(R.id.CHASSIS_NO).apply {
            setText(chassisData.JOBCARDNO)
            isEnabled = false
        }
        findViewById<EditText>(R.id.VIN).apply {
            setText(chassisData.JOBCARDNO)
            isEnabled = false
        }
        findViewById<EditText>(R.id.CHASSIS_NO).setText(chassisData.REGNO)
        findViewById<EditText>(R.id.VIN).setText(chassisData.JOBCARDNO)
        findViewById<TextView>(R.id.VARIANT_DESC).text = chassisData.CHASSISNO
        findViewById<EditText>(R.id.MODEL_DESC).setText(chassisData.VIN)
        findViewById<TextView>(R.id.DEALER_LOCATION).text = chassisData.SERVICEADVISOR
        findViewById<EditText>(R.id.FUEL_DESC).setText(chassisData.ENGINENO)
        findViewById<EditText>(R.id.COLOUR).setText(chassisData.COLOR)
        findViewById<EditText>(R.id.LOCATION).setText(chassisData.MODELDESC)
        findViewById<EditText>(R.id.GRN_DATE).setText(chassisData.VARIANT)
        findViewById<EditText>(R.id.ENGINE_NO).setText(chassisData.ERPACCTNO)
        findViewById<EditText>(R.id.VEH_STATUS).setText(chassisData.VEH_STATUS)
        findViewById<EditText>(R.id.STATUS).setText(chassisData.STATUS)
        findViewById<TextView>(R.id.REMARKS1).text = chassisData.COLORDESC
        findViewById<TextView>(R.id.MUL_INV_NO).text=chassisData.PHYSICALLOCATION
        findViewById<EditText>(R.id.MUL_INV_DT).setText(chassisData.CUSTNAME)
        findViewById<EditText>(R.id.GRN_DATE).setText(chassisData.DMSCUSTID)
        findViewById<EditText>(R.id.SOBNO).setText(chassisData.CONTACTNO)
        findViewById<EditText>(R.id.GRN_NO).setText(chassisData.DMSLOCATION)
        findViewById<EditText>(R.id.JOB_C_DATE).setText(chassisData.JOBCARDDATE)
        val imageUrl = "${ApiFile.APP_URL}/servImage/srDownload?regNo=${chassisData.REGNO}"
        Picasso.get().load(imageUrl).into(vehImage)
    }

    private fun logout() {
        val intent = Intent(this@WorkShopChassisEnquiry, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun rd_to_cam() {
        val vehicleNo = chassisNoEditText.text.toString()
        val chassisNo = VIN.text.toString()

        if (vehicleNo.isNotEmpty()|| chassisNo.isNotEmpty()) {
            val intent = Intent(this, WorkshopUploadImage::class.java)
            intent.putExtra("REGNO", vehicleNo)
            intent.putExtra("CHASSISNO", chassisNo)
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

data class ChassisData3(
    val CHASSISNO: String,
    val JOBCARDNO:String,
    val REGNO: String,
    val VIN: String,
    val SERVICEADVISOR:String,
    val STATUS:String,
    val VEH_STATUS:String,
    val ERPACCTNO:String,
    val PHYSICALLOCATION:String,
    val OUID:String,
    val CUSTNAME:String,
    val DMSCUSTID:String,
    val DMSLOCATION:String,
    val MODELDESC:String,
    val VARIANT:String,
    val ENGINENO:String,
    val REGDATE:String,
    val CONTACTNO:String,
    val COLOR:String,
    val COLORDESC:String,
    val JOBCARDDATE:String
)




