package com.example.apinew

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors
import kotlin.math.max
import kotlin.math.min

class WorkshopParkingInOutTrack : AppCompatActivity() {
    private lateinit var login_name: String
    private lateinit var deptName: String
    private var ouId: Int = 0
    private var locId: Int = 0
    private lateinit var location_name: String
    private lateinit var username:TextView
    private lateinit var locIdTxt:TextView
    private lateinit var deptIntent:TextView
    private lateinit var newVehEditText:EditText

    private lateinit var newVehInButton: ImageButton
    private lateinit var forNewVehicleOut:TextView
    private lateinit var refreshBtn:TextView
    private lateinit var regNoDetails:TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workshop_parking_in_out_track)

        username=findViewById(R.id.username)
        locIdTxt=findViewById(R.id.locIdTxt)
        deptIntent=findViewById(R.id.deptIntent)


        val tableLayout = findViewById<TableLayout>(R.id.tableLayout2)
        tableLayout.removeAllViews()

        locId = intent.getIntExtra("locId", 0)
        ouId = intent.getIntExtra("ouId", 0)
        deptName = intent.getStringExtra("deptName") ?: ""
        location_name = intent.getStringExtra("location_name") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""


        username.text=login_name
        locIdTxt.text= location_name
        deptIntent.text=deptName

        newVehInButton=findViewById(R.id.newVehInButton)
        newVehEditText=findViewById(R.id.newVehEditText)
        forNewVehicleOut=findViewById(R.id.forNewVehicleOut)
        refreshBtn=findViewById(R.id.refreshBtn)
        regNoDetails=findViewById(R.id.regNoDetails)

        refreshBtn.visibility=View.GONE
        regNoDetails.visibility=View.GONE

        refreshBtn.setOnClickListener { resetFields() }

        val noSpaceFilter = InputFilter { source, _, _, _, _, _ ->
            if (source.any { it.isWhitespace() }) "" else null
        }

        val blockSpecialCharFilter = InputFilter { source, _, _, _, _, _ ->
            val pattern = Regex("^[a-zA-Z0-9]+$")
            if (source.isEmpty() || source.matches(pattern)) {
                source
            } else {
                ""
            }
        }


        newVehEditText.filters = arrayOf(noSpaceFilter, blockSpecialCharFilter, InputFilter.AllCaps())


        newVehInButton.setOnClickListener {
            detailsForVehicleInFirstTime()
        }

    }


    private fun detailsForVehicleInFirstTime() {
        val client = OkHttpClient()
        val vehNo = newVehEditText.text.toString()
        if(vehNo.isEmpty()){
            Toast.makeText(this@WorkshopParkingInOutTrack,"Please enter vehicle number.",Toast.LENGTH_SHORT).show()
            return
        }
        val url = ApiFile.APP_URL + "/tdParking/parkingHistory?regNo=$vehNo"

        val request = Request.Builder()
            .url(url)
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val jsonObject = JSONObject(it)

                    val stockItem = jsonObject.getJSONArray("obj").getJSONObject(0)

                    val jcData3 = allData(
                        CHASSIS_NO = stockItem.optString("CHASSIS_NO",""),
                        REG_NO = stockItem.optString("REG_NO",""),
                        CREATION_DATE = stockItem.optString("CREATION_DATE",""),
                        CREATED_BY = stockItem.optString("CREATED_BY",""),
                        IN_KM = stockItem.optString("IN_KM",""),
                        IN_TIME = stockItem.optString("IN_TIME",""),
                        LOCATION = stockItem.optString("LOCATION",""),
                        OU = stockItem.optString("OU",""),
                        OUT_KM = stockItem.optString("OUT_KM",""),
                        OUT_TIME = stockItem.optString("OUT_TIME",""),
                        REMARKS = stockItem.optString("REMARKS",""),
                        TEST_DRIVE_NO = stockItem.optString("TEST_DRIVE_NO",""),
                        UPDATION_DATE = stockItem.optString("UPDATION_DATE",""),
                        UPDATED_BY = stockItem.optString("UPDATED_BY",""),
                        DESCRIPTION = stockItem.optString("DESCRIPTION",""),
                        GATE_TYPE = stockItem.optString("GATE_TYPE",""),
                        GATE_NO = stockItem.optString("GATE_NO",""),
                        REASON = stockItem.optString("REASON",""),
                        DRIVER_IN = stockItem.optString("DRIVER_IN",""),
                        DRIVER_OUT = stockItem.optString("DRIVER_OUT",""),
                        DEPT = stockItem.optString("DEPT",""),
                        PARKING_REASON = stockItem.optString("PARKING_REASON"),
                        PARKING_DESC = stockItem.optString("PARKING_DESC"),
                        VIN = stockItem.optString("VIN"),
                        STATUS = stockItem.optString("STATUS"),
                        SERVICE_ADVISOR = stockItem.optString("SERVICE_ADVISOR"),
                        MODEL_DESC = stockItem.optString("MODEL_DESC")
                        )

                    val responseMessage = jsonObject.getString("message")

                    when (responseMessage) {
                        "Details Found Successfully" -> { //1
                            runOnUiThread {
                                populateFieldsAfterVehicleInAfterTestDrive(jcData3)
                                refreshBtn.visibility=View.VISIBLE
                                regNoDetails.visibility=View.VISIBLE
                                Toast.makeText(
                                    this@WorkshopParkingInOutTrack,
                                    "Details found in Test Drive Table for Vehicle No: $vehNo",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        else -> {
                            runOnUiThread {
                                Toast.makeText(
                                    this@WorkshopParkingInOutTrack,
                                    "Unexpected response for Vehicle No: $vehNo",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@WorkshopParkingInOutTrack,
                        "Failed to fetch details for vehicle No: $vehNo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


//    private fun populateFieldsAfterVehicleInAfterTestDrive(jcData:allData) {
//        val table = findViewById<TableLayout>(R.id.tableLayout2)
//        table.removeAllViews()
//
//        val detailsMap = mutableMapOf(
//            "REGNO" to jcData.REG_NO,
//            "JOBCARDNO" to jcData.JOBCARDNO,
//            "CHASSIS NO" to jcData.CHASSIS_NO,
//            "LOCATION" to jcData.LOCATION,
//            "IN KM" to jcData.IN_KM,
//            "IN TIME" to jcData.IN_TIME,
//            "OUT KM" to jcData.OUT_KM,
//            "OUT TIME" to jcData.OUT_TIME,
//            "GATE NO" to jcData.GATE_NO,
//            "GATE TYPE" to jcData.GATE_TYPE,
//            "REASON" to jcData.REASON,
//            "PARK DESC" to jcData.DESCRIPTION,
//            "REMARKS" to jcData.REMARKS,
//            "DRIVER NAME" to jcData.DRIVER_NAME
//        )
//
//
//        for ((label, value) in detailsMap) {
//            if (value!= "-" && value!="0") {
//                val row = LayoutInflater.from(this).inflate(R.layout.table_row_gate_pass, null) as TableRow
//                val labelTextView = row.findViewById<TextView>(R.id.label)
//                val valueTextView = row.findViewById<TextView>(R.id.value)
//
//                labelTextView.text = label
//                valueTextView.text = value
//
//                table.addView(row)
//            }
//        }
//
//        if (table.childCount > 0) {
//            val lastRow = table.getChildAt(table.childCount - 1) as TableRow
//            lastRow.findViewById<View>(R.id.labelDivider).visibility = View.GONE
//            lastRow.findViewById<View>(R.id.valueDivider).visibility = View.GONE
//        }
//    }


    private fun populateFieldsAfterVehicleInAfterTestDrive(jcData: allData) {
        val table = findViewById<TableLayout>(R.id.tableLayout2)
        table.removeAllViews()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val inTime = try { dateFormat.parse(jcData.IN_TIME) } catch (e: Exception) { null }
        val outTime = try { dateFormat.parse(jcData.OUT_TIME) } catch (e: Exception) { null }

//        val hideReasonAndDesc = inTime != null && outTime != null && outTime.time < inTime.time

        val detailsMap = mutableMapOf(
            "REGNO" to jcData.REG_NO,
            "CHASSIS NO" to jcData.CHASSIS_NO,
            "LOCATION" to jcData.LOCATION,
            "DEPT" to jcData.DEPT,
            "MODEL" to jcData.MODEL_DESC,
            "SERVICE ADV" to jcData.SERVICE_ADVISOR,
            "IN KM" to jcData.IN_KM,
            "IN TIME" to jcData.IN_TIME,
            "OUT KM" to jcData.OUT_KM,
            "OUT TIME" to jcData.OUT_TIME,
            "VEH OUT REASON" to jcData.PARKING_DESC,
            "GATE NO" to jcData.GATE_NO,
            "GATE TYPE" to jcData.GATE_TYPE,
            "REMARKS" to jcData.REMARKS,
            "DRIVER NAME-IN" to jcData.DRIVER_IN,
            "DRIVER NAME-OUT" to jcData.DRIVER_OUT,
            "VEHICLE STATUS" to jcData.STATUS

        )

//        if (!hideReasonAndDesc) {
//            detailsMap["REASON"] = jcData.REASON
//            detailsMap["PARK DESC"] = jcData.DESCRIPTION
//        }

        for ((label, value) in detailsMap) {
            if (value != "-" && value != "0") {
                val row = LayoutInflater.from(this).inflate(R.layout.table_row_gate_pass, null) as TableRow
                val labelTextView = row.findViewById<TextView>(R.id.label)
                val valueTextView = row.findViewById<TextView>(R.id.value)

                labelTextView.text = label
                valueTextView.text = value

                table.addView(row)
            }
        }

        if (table.childCount > 0) {
            val lastRow = table.getChildAt(table.childCount - 1) as TableRow
            lastRow.findViewById<View>(R.id.labelDivider).visibility = View.GONE
            lastRow.findViewById<View>(R.id.valueDivider).visibility = View.GONE
        }
    }


    private fun resetFields() {
        val tableLayout = findViewById<TableLayout>(R.id.tableLayout2)
        tableLayout.removeAllViews()
        refreshBtn.visibility=View.GONE
        regNoDetails.visibility=View.GONE
        newVehEditText.setText("")
    }

    data class allData(
        val IN_TIME:String,
        val OUT_KM:String,
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
        val OUT_TIME:String,
        val REASON:String,
        val GATE_NO:String,
        val GATE_TYPE:String,
        val DESCRIPTION:String,
        val DRIVER_IN:String,
        val DRIVER_OUT:String,
        val DEPT:String,
        val VIN:String,
        val PARKING_REASON:String,
        val PARKING_DESC:String,
        val STATUS:String,
        val SERVICE_ADVISOR:String,
        val MODEL_DESC:String

    )
}




