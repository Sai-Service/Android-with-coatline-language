package com.example.apinew

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.media.Image
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors
import kotlin.math.max
import kotlin.math.min

class FloorVehicleTracking : AppCompatActivity() {

    private lateinit var login_name: String
    private lateinit var deptName: String
    private var ouId: Int = 0
    private var locId: Int = 0
    private lateinit var location_name: String
    private lateinit var username:TextView
    private lateinit var locIdTxt:TextView
    private lateinit var deptIntent:TextView

    private lateinit var floorHistory:TextView
    private lateinit var washIn:TextView
    private lateinit var washOut:TextView
    private lateinit var captureVehNumber:View
    private lateinit var captureRegNoCamera:ImageButton
    private lateinit var vehNoEnterLL:View
    private lateinit var enterVehNumber:EditText
    private lateinit var vehWashButtonIn:ImageButton
    private lateinit var vehWashButtonOut:ImageButton
    private lateinit var regNoDetails:TextView
    private lateinit var technician:TextView
    private lateinit var technicianName:EditText
    private lateinit var carRepair:TextView
    private lateinit var carRepairDetails:EditText
    private lateinit var labours:TextView
    private lateinit var laboursCount:EditText
    private lateinit var submitButton:TextView
    private lateinit var submitButtonOut:TextView
    private lateinit var resetFields:TextView
    private lateinit var remarks:TextView
    private lateinit var remarksEnter:EditText


    private lateinit var bestResult2:String
    private lateinit var regNo:String
    private lateinit var engineNo:String
    private lateinit var vinNo:String
    private lateinit var chassisNo:String
    private lateinit var trfId:String


    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        private const val CAMERA_REQUEST_CODE = 101
        private const val CAMERA_REQUEST_CODE_2 = 103
    }
    private val recognizer = TextRecognition.getClient(
        TextRecognizerOptions.Builder().setExecutor(
            Executors.newSingleThreadExecutor()
        ).build()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_floor_vehicle_tracking)

        locId = intent.getIntExtra("locId", 0)
        ouId = intent.getIntExtra("ouId", 0)
        deptName = intent.getStringExtra("deptName") ?: ""
        location_name = intent.getStringExtra("location_name") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""


        username=findViewById(R.id.username)
        locIdTxt=findViewById(R.id.locIdTxt)
        deptIntent=findViewById(R.id.deptIntent)

        username.text=login_name
        locIdTxt.text= location_name
        deptIntent.text=deptName


        floorHistory=findViewById(R.id.floorHistory)
        washIn=findViewById(R.id.washIn)
        washOut=findViewById(R.id.washOut)
        captureVehNumber=findViewById(R.id.captureVehNumber)
        captureRegNoCamera=findViewById(R.id.captureRegNoCamera)
        vehNoEnterLL=findViewById(R.id.vehNoEnterLL)
        enterVehNumber=findViewById(R.id.enterVehNumber)
        vehWashButtonIn=findViewById(R.id.vehWashButtonIn)
        vehWashButtonOut=findViewById(R.id.vehWashButtonOut)
        regNoDetails=findViewById(R.id.regNoDetails)
        technician=findViewById(R.id.technician)
        technicianName=findViewById(R.id.technicianName)
        carRepair=findViewById(R.id.carRepair)
        carRepairDetails=findViewById(R.id.carRepairDetails)
        labours=findViewById(R.id.labours)
        laboursCount=findViewById(R.id.laboursCount)
        submitButton=findViewById(R.id.submitButton)
        submitButtonOut=findViewById(R.id.submitButtonOut)
        resetFields=findViewById(R.id.resetFields)
        remarks=findViewById(R.id.remarks)
        remarksEnter=findViewById(R.id.remarksEnter)


        regNoDetails.visibility=View.GONE
        technician.visibility=View.GONE
        technicianName.visibility=View.GONE
        carRepair.visibility=View.GONE
        carRepairDetails.visibility=View.GONE
        labours.visibility=View.GONE
        laboursCount.visibility=View.GONE
        submitButton.visibility=View.GONE
        submitButtonOut.visibility=View.GONE
        resetFields.visibility=View.GONE
        remarks.visibility=View.GONE
        remarksEnter.visibility=View.GONE

        captureVehNumber.visibility=View.GONE
        vehNoEnterLL.visibility=View.GONE


        captureRegNoCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openCamera(CAMERA_REQUEST_CODE_2)
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            }
        }

        washIn.setOnClickListener {
            vehNoEnterLL.visibility=View.VISIBLE
            captureVehNumber.visibility=View.VISIBLE
            vehWashButtonIn.visibility=View.VISIBLE
            vehWashButtonOut.visibility=View.GONE
        }

        washOut.setOnClickListener {
            vehNoEnterLL.visibility=View.VISIBLE
            captureVehNumber.visibility=View.VISIBLE
            vehWashButtonIn.visibility=View.GONE
            vehWashButtonOut.visibility=View.VISIBLE
        }

        vehWashButtonIn.setOnClickListener {
            detailsForVehicleInFirstTime()
        }

        vehWashButtonOut.setOnClickListener {
            detailsForVehicleOut()
        }

        resetFields.setOnClickListener {
            resetFields()
        }

        submitButton.setOnClickListener {
            vehicleIn()
        }
        submitButtonOut.setOnClickListener {
            vehicleOut()
        }
        floorHistory.setOnClickListener {
            floorVehHistory()
        }

        val blockSpecialCharFilter = InputFilter { source, _, _, _, _, _ ->
            val pattern = Regex("^[a-zA-Z0-9]+$")
            if (source.isEmpty() || source.matches(pattern)) {
                source
            } else {
                ""
            }
        }

        enterVehNumber.filters=arrayOf(blockSpecialCharFilter, InputFilter.AllCaps())

    }

    private fun floorVehHistory() {
        val intent = Intent(this@FloorVehicleTracking, FloorVehicleHistory::class.java)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name",location_name)
        intent.putExtra("deptName",deptName)
        startActivity(intent)
    }
    private fun openCamera(requestCode: Int) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            IntentIntegrator.REQUEST_CODE -> {
                val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
                if (result != null) {
                    if (result.contents == null) {
                        Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
                    } else {
                        val vin = result.contents
                    }
                }
            }
            CAMERA_REQUEST_CODE, CAMERA_REQUEST_CODE_2 -> {
                if (resultCode == Activity.RESULT_OK) {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    when (requestCode) {
                        CAMERA_REQUEST_CODE -> {
                            processImageWithMultipleAttempts(imageBitmap, enterVehNumber)
                        }
                        CAMERA_REQUEST_CODE_2 -> {
                            processImageWithMultipleAttempts(imageBitmap, enterVehNumber)
                        }
                    }
                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun processImageWithMultipleAttempts(originalBitmap: Bitmap, resultTextView: TextView) {
        val attempts = listOf(
            { preprocessImage(originalBitmap) },
            { preprocessImage(resizeImage(originalBitmap)) },
            { resizeImage(originalBitmap) }
        )

        val results = mutableListOf<String>()

        attempts.forEachIndexed { index, preprocessor ->
            val processedBitmap = preprocessor()
            val image = InputImage.fromBitmap(processedBitmap, 0)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    results.add(visionText.text)
                    if (index == attempts.lastIndex) {
                        displayBestResult(results, resultTextView)
                    }
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    if (index == attempts.lastIndex && results.isEmpty()) {
                        Toast.makeText(this, "Text recognition failed", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun displayBestResult(results: List<String>, resultTextView: TextView) {
        bestResult2 = results.maxByOrNull { it.length }
            ?.replace(" ", "")
            ?.replace(".", "")
            ?.replace("IND","")
            ?.replace("IN0","")
            ?.replace("UND","")
            ?.replace("UN0","")
            ?.replace("1N0","")
            ?.replace("|","")
            ?.replace("-","")
            ?.replace(",","")
            ?.replace("/","")
            ?.replace("$","")
            ?.replace("o","")
            ?.replace(")","")
            ?.replace("(","")
            ?.replace("NO","")
            ?: ""

        Toast.makeText(this, "Result:$bestResult2", Toast.LENGTH_SHORT).show()

        val regexVehicleNo  = Regex("^[A-Z]{2}\\d{2}[A-Z]{2}\\d{4}$")
        val regexVehicleNo2 = Regex("^[A-Z]{2}\\d{2}[A-Z]\\d{4}$")
        val regexVehicleNo3 = Regex("^[A-Z]{2}\\d{2}[A-Z]{3}\\d{4}$")

        var modifiedString = bestResult2
        modifiedString = when (bestResult2.length) {
            9 -> modifiedString.mapIndexed { index, char ->
                when {
                    (index == 2 || index == 3 || index >= 5) && char == 'O' -> '0'
                    (index == 2 || index == 3 || index >= 5) && char == 'Z' -> '4'
                    (index == 2 || index == 3 || index >= 5) && char == 'S' -> '5'
                    (index == 4) && char == '0' -> 'D'
                    (index == 0) && char == 'N' -> 'M'//Added on 16-05-2025
                    else -> char
                }
            }.joinToString("")

            10 -> modifiedString.mapIndexed { index, char ->
                when {
                    (index == 2 || index == 3 || index >= 6) && char == 'O' -> '0'
                    (index == 2 || index == 3 || index >= 6) && char == 'Z' -> '4'
                    (index == 2 || index == 3 || index >= 6) && char == 'S' -> '5'
                    (index == 4 || index == 5) && char == '0' -> 'D'
                    (index == 0) && char == 'N' -> 'M'//Added on 16-05-2025
                    else -> char
                }
            }.joinToString("")

            11 -> modifiedString.mapIndexed { index, char ->
                when {
                    (index == 2 || index == 3 || index >= 7) && char == 'O' -> '0'
                    (index == 2 || index == 3 || index >= 7) && char == 'Z' -> '4'
                    (index == 2 || index == 3 || index >= 7) && char == 'S' -> '5'
                    (index == 4 || index == 5 || index == 6) && char == '0' -> 'D'
                    (index == 0) && char == 'N' -> 'M'//Added on 16-05-2025
                    else -> char
                }
            }.joinToString("")

            else -> modifiedString
        }

        modifiedString = modifiedString.trim()

        when {
            regexVehicleNo.matches(modifiedString) -> {
                resultTextView.text = modifiedString
                enterVehNumber.setText(modifiedString)
//                Toast.makeText(this, "Text recognized: $modifiedString", Toast.LENGTH_SHORT).show()
            }
            regexVehicleNo2.matches(modifiedString) -> {
                resultTextView.text = modifiedString
                enterVehNumber.setText(modifiedString)
//                Toast.makeText(this, "Text recognized: $modifiedString", Toast.LENGTH_SHORT).show()
            }
            regexVehicleNo3.matches(modifiedString) -> {
                resultTextView.text = modifiedString
                enterVehNumber.setText(modifiedString)
//                Toast.makeText(this, "Text recognized: $modifiedString", Toast.LENGTH_SHORT).show()
            }
            else -> {
                runOnUiThread {
                    Toast.makeText(this, "Cant able to read Vehicle Number", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun preprocessImage(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val processedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = bitmap.getPixel(x, y)
                val red = (pixel shr 16) and 0xFF
                val green = (pixel shr 8) and 0xFF
                val blue = pixel and 0xFF
                var gray = (0.299 * red + 0.587 * green + 0.114 * blue).toInt()

                gray = if (gray > 128) min(255, gray + 30) else max(0, gray - 30)

                processedBitmap.setPixel(x, y, Color.rgb(gray, gray, gray))
            }
        }

        return processedBitmap
    }

    private fun resizeImage(bitmap: Bitmap, targetWidth: Int = 1000): Bitmap {
        val aspectRatio = bitmap.width.toDouble() / bitmap.height.toDouble()
        val targetHeight = (targetWidth / aspectRatio).toInt()
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }

    private fun populateFieldsAfterInSearch(){
        regNoDetails.visibility=View.VISIBLE
        technician.visibility=View.VISIBLE
        technicianName.visibility=View.VISIBLE
        carRepair.visibility=View.VISIBLE
        carRepairDetails.visibility=View.VISIBLE
        labours.visibility=View.VISIBLE
        laboursCount.visibility=View.VISIBLE
        submitButton.visibility=View.VISIBLE
        resetFields.visibility=View.VISIBLE
        remarks.visibility=View.VISIBLE
        remarksEnter.visibility=View.VISIBLE
    }

    private fun populateFieldsAfterOutSearch(){
        regNoDetails.visibility=View.VISIBLE
        submitButtonOut.visibility=View.VISIBLE
        resetFields.visibility=View.VISIBLE
        remarks.visibility=View.VISIBLE
        remarksEnter.visibility=View.VISIBLE
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

    private fun detailsForVehicleInFirstTime() {
        val client = OkHttpClient()
        val vehNo = enterVehNumber.text.toString()
        if(vehNo.isEmpty()){
            Toast.makeText(this@FloorVehicleTracking,"Please enter vehicle number.",Toast.LENGTH_SHORT).show()
            return
        }
        val url = ApiFile.APP_URL + "/service/wsVehDetForTestDriveIn?regNo=$vehNo"

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
                        regNo = stockItem.optString("regNo"),
                        chassisNo = stockItem.optString("chassisNo"),
                        engineNo = stockItem.optString("engineNo"),
                        vin = stockItem.optString("vin"),
                        technicianName=stockItem.optString("technicianName"),
                        location = stockItem.optString("location"),
                        remarks =stockItem.optString("remarks"),
                        status = stockItem.optString("status"),
                        trfId =stockItem.optString("trfId"),
                        //Masters Table Data
                        ACCOUNT_NUMBER = stockItem.optString("ACCOUNT_NUMBER"),
                        ADDRESS = stockItem.optString("ADDRESS"),
                        CUST_NAME = stockItem.optString("CUST_NAME"),
                        EMAIL_ADDRESS = stockItem.optString("EMAIL_ADDRESS"),
                        ENGINE_NO = stockItem.optString("ENGINE_NO"),
                        INSTANCE_NUMBER = stockItem.optString("INSTANCE_NUMBER"),
                        PRIMARY_PHONE_NUMBER = stockItem.optString("PRIMARY_PHONE_NUMBER"),
                        REGISTRATION_DATE = stockItem.optString("REGISTRATION_DATE"),
                        CHASSIS_NO = stockItem.optString("CHASSIS_NO"),
                        VIN = stockItem.optString("VIN"),
                        //Out after vehicle in from location
                        inTime = formatDateTime(stockItem.optString("inTime")),
                        REG_NO = stockItem.optString("REG_NO"),
                        REMARKS = stockItem.optString("REMARKS"),
                        LOCATION = stockItem.optString("LOCATION"),
                        SERVICE_ADVISOR = stockItem.optString("SERVICE_ADVISOR"),
                        attribute1 = stockItem.optString("attribute1"),
                        attribute2 = stockItem.optString("attribute2")
                    )

                    val responseMessage = jsonObject.getString("message")

                    when (responseMessage) {
                        "Details Found Successfully in master table" -> { //3
                            runOnUiThread {
                                populateFieldsDuringInFromMasterVehicle(jcData3)
                                populateFieldsAfterInSearch()
                                Toast.makeText(
                                    this@FloorVehicleTracking,
                                    "Details Found Successfully in master table for Vehicle No: $vehNo",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        else -> {
                            runOnUiThread {
                                Toast.makeText(
                                    this@FloorVehicleTracking,
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
                        this@FloorVehicleTracking,
                        "Failed to fetch details for vehicle No: $vehNo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun detailsForVehicleOut() {
        val client = OkHttpClient()
        val vehNo = enterVehNumber.text.toString()
        if(vehNo.isEmpty()){
            Toast.makeText(this@FloorVehicleTracking,"Please enter vehicle number.",Toast.LENGTH_SHORT).show()
            return
        }
        val url = ApiFile.APP_URL + "/VehicleTrack/VehicleInAndOut?regNo=$vehNo"

        val request = Request.Builder()
            .url(url)
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val jsonObject = JSONObject(it)

//                    val stockItem = jsonObject.getJSONArray("obj").getJSONObject(0)
                    val stockItem = jsonObject.getJSONObject("obj")


                    val jcData3 = allData(
                        regNo = stockItem.optString("regNo"),
                        chassisNo = stockItem.optString("chassisNo"),
                        engineNo = stockItem.optString("engineNo"),
                        vin = stockItem.optString("vin"),
                        technicianName=stockItem.optString("technicianName"),
                        location = stockItem.optString("location"),
                        remarks =stockItem.optString("remarks"),
                        status = stockItem.optString("status"),
                        trfId = stockItem.optString("trfId"),
                        //Masters Table Data
                        ACCOUNT_NUMBER = stockItem.optString("ACCOUNT_NUMBER"),
                        ADDRESS = stockItem.optString("ADDRESS"),
                        CUST_NAME = stockItem.optString("CUST_NAME"),
                        EMAIL_ADDRESS = stockItem.optString("EMAIL_ADDRESS"),
                        ENGINE_NO = stockItem.optString("ENGINE_NO"),
                        INSTANCE_NUMBER = stockItem.optString("INSTANCE_NUMBER"),
                        PRIMARY_PHONE_NUMBER = stockItem.optString("PRIMARY_PHONE_NUMBER"),
                        REGISTRATION_DATE = stockItem.optString("REGISTRATION_DATE"),
                        VIN=stockItem.optString("VIN"),
                        //Out after vehicle in from location
                        inTime = formatDateTime(stockItem.optString("inTime")),
                        REG_NO = stockItem.optString("REG_NO"),
                        REMARKS = stockItem.optString("REMARKS"),
                        LOCATION = stockItem.optString("LOCATION"),
                        //In after test Drive
                        SERVICE_ADVISOR = stockItem.optString("SERVICE_ADVISOR"),
                        attribute1 = stockItem.optString("attribute1"),
                        attribute2 = stockItem.optString("attribute2"),
                        CHASSIS_NO = stockItem.optString("CHASSIS_NO"),
                    )

                    val responseMessage = jsonObject.getString("message")

                    when (responseMessage) {
                        "Details Found Successfully In Test Track Table" -> {
                            runOnUiThread {
                                populateFieldsAfterVehicleOutForFirstTime(jcData3)
                                populateFieldsAfterOutSearch()
                                Log.d("jsonData",jsonData)
                                Log.d("jsonObject",jsonObject.toString())
                                Toast.makeText(
                                    this@FloorVehicleTracking,
                                    "Details found for Vehicle No: $vehNo",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        "Vehicle Is Not IN" -> {
                            runOnUiThread {
                                Toast.makeText(
                                    this@FloorVehicleTracking,
                                    "Vehicle Is Not IN",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        else -> {
                            runOnUiThread {
                                Toast.makeText(
                                    this@FloorVehicleTracking,
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
                        this@FloorVehicleTracking,
                        "Failed to fetch details for vehicle No: $vehNo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun resetFields() {
        val tableLayout = findViewById<TableLayout>(R.id.tableLayout2)
        tableLayout.removeAllViews()
        enterVehNumber.setText("")
        captureVehNumber.visibility=View.GONE
        vehNoEnterLL.visibility=View.GONE
        regNoDetails.visibility=View.GONE
        technician.visibility=View.GONE
        technicianName.setText("")
        technicianName.visibility=View.GONE
        carRepair.visibility=View.GONE
        carRepairDetails.visibility=View.GONE
        carRepairDetails.setText("")
        labours.visibility=View.GONE
        laboursCount.visibility=View.GONE
        laboursCount.setText("")
        submitButton.visibility=View.GONE
        submitButtonOut.visibility=View.GONE
        resetFields.visibility=View.GONE
        remarks.visibility=View.GONE
        remarksEnter.visibility=View.GONE
        remarksEnter.setText("")
        regNo=""
        chassisNo=""
        engineNo=""
        vinNo=""
        trfId=""
    }

    private fun vehicleIn() {
        val technicianName=technicianName.text.toString()
        val remarks=carRepairDetails.text.toString()
        val labours=laboursCount.text.toString()
        val remarksField=remarksEnter.text.toString()

        val url = ApiFile.APP_URL + "/VehicleTrack/VehicleIn/"
        val jsonObject = JSONObject().apply {
            put("regNo", regNo)
            if (::vinNo.isInitialized) {
                if(vinNo.isNotEmpty() ) {
                    put("vin", vinNo)
                }
            }
            if (::chassisNo.isInitialized) {
                if(chassisNo.isNotEmpty() ) {
                    put("chassisNo", chassisNo)
                }
            }
            if (::engineNo.isInitialized) {
                if(engineNo.isNotEmpty() ) {
                    put("engineNo", engineNo)
                }
            }
            put("location", location_name)
            put("ou",ouId)
            put("createdBy",login_name)
            put("updatedBy",login_name)
            put("dept",deptName)
            put("technicianName",technicianName)
            put("authorisedBy",login_name)
            put("remarks",remarks)
            put("locCode",locId)
            put("attribute1",labours)
            put("attribute2",remarksField)
        }

        val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val client = OkHttpClient()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseCode = response.code
                val responseBody = response.body?.string()

                runOnUiThread {
                    if (responseBody != null) {
                        val jsonResponse = JSONObject(responseBody)
                        val message = jsonResponse.optString("message", "")

                        when {
                            message.contains("Vehicle is already In at Floor.", ignoreCase = true) -> {
                                Toast.makeText(
                                    this@FloorVehicleTracking,
                                    "Vehicle is already In at Floor.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            responseCode == 200 -> {
                                Toast.makeText(
                                    this@FloorVehicleTracking,
                                    "Vehicle in successfully!!!",
                                    Toast.LENGTH_LONG
                                ).show()
                                resetFields()
                            }
                            else -> {
                                Toast.makeText(
                                    this@FloorVehicleTracking,
                                    "Failed to save data. Error code: $responseCode",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@FloorVehicleTracking,
                            "No response from server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@FloorVehicleTracking,
                        "Error saving data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun vehicleOut() {
        val remarksField=remarksEnter.text.toString()
        var transferId=trfId
        val url = ApiFile.APP_URL + "/VehicleTrack/VehicleOUT/$transferId"
        val jsonObject = JSONObject().apply {
            put("updatedBy",login_name)
            put("attribute2",remarksField)
        }

        val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .put(requestBody)
            .build()

        val client = OkHttpClient()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseCode = response.code
                val responseBody = response.body?.string()

                runOnUiThread {
                    if (responseBody != null) {
                        val jsonResponse = JSONObject(responseBody)
                        val message = jsonResponse.optString("message", "")

                        when {
                            message.contains("Vehicle is already In at Floor.", ignoreCase = true) -> {
                                Toast.makeText(
                                    this@FloorVehicleTracking,
                                    "Vehicle is already In at Floor.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            responseCode == 200 -> {
                                Toast.makeText(
                                    this@FloorVehicleTracking,
                                    "Vehicle in successfully!!!",
                                    Toast.LENGTH_LONG
                                ).show()
                                resetFields()
                                transferId=""
                            }
                            else -> {
                                Toast.makeText(
                                    this@FloorVehicleTracking,
                                    "Failed to save data. Error code: $responseCode",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@FloorVehicleTracking,
                            "No response from server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@FloorVehicleTracking,
                        "Error saving data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }




    private fun populateFieldsDuringInFromMasterVehicle(jcData:allData) {
        val table = findViewById<TableLayout>(R.id.tableLayout2)
        table.removeAllViews()

        val detailsMap = mutableMapOf(
            "REG NO" to jcData.INSTANCE_NUMBER,
            "VIN" to jcData.VIN,
            "CHASSIS NO" to jcData.CHASSIS_NO,
            "ENGINE NO" to jcData.ENGINE_NO,
            "CUST NAME" to jcData.CUST_NAME
        )

        regNo=jcData.INSTANCE_NUMBER
        engineNo=jcData.ENGINE_NO
        vinNo=jcData.VIN
        chassisNo=jcData.CHASSIS_NO

        for ((label, value) in detailsMap) {
            val row = LayoutInflater.from(this).inflate(R.layout.table_row_gate_pass, null) as TableRow
            val labelTextView = row.findViewById<TextView>(R.id.label)
            val valueTextView = row.findViewById<TextView>(R.id.value)

            labelTextView.text = label
            valueTextView.text = value

            table.addView(row)
        }

        if (table.childCount > 0) {
            val lastRow = table.getChildAt(table.childCount - 1) as TableRow
            lastRow.findViewById<View>(R.id.labelDivider).visibility = View.GONE
            lastRow.findViewById<View>(R.id.valueDivider).visibility = View.GONE
        }
    }


    private fun populateFieldsAfterVehicleOutForFirstTime(jcData: allData) {
        val table = findViewById<TableLayout>(R.id.tableLayout2)
        table.removeAllViews()

        val detailsMap = mutableMapOf(
            "REG NO" to jcData.regNo,
            "VIN" to jcData.vin,
            "CHASSIS NO" to jcData.chassisNo,
            "ENGINE NO" to jcData.engineNo,
            "LOCATION" to jcData.location,
            "IN TIME" to jcData.inTime,
            "TECHNICIAN" to jcData.technicianName,
            "LABOURS" to jcData.attribute1,
            "REMARKS" to jcData.attribute2,
            "TRF ID" to jcData.trfId
        )

        trfId=jcData.trfId

        for ((label, value) in detailsMap) {
            val row = LayoutInflater.from(this).inflate(R.layout.table_row_gate_pass, null) as TableRow
            val labelTextView = row.findViewById<TextView>(R.id.label)
            val valueTextView = row.findViewById<TextView>(R.id.value)

            labelTextView.text = label
            valueTextView.text = value

            table.addView(row)
        }

        if (table.childCount > 0) {
            val lastRow = table.getChildAt(table.childCount - 1) as TableRow
            lastRow.findViewById<View>(R.id.labelDivider).visibility = View.GONE
            lastRow.findViewById<View>(R.id.valueDivider).visibility = View.GONE
        }
    }

    data class allData(
        //Vehicle In first time from stock table....
        val regNo:String,
        val chassisNo:String,
        val engineNo:String,
        val vin:String,
        val technicianName:String,
        val location:String,
        val remarks:String,
        val status:String,
        val trfId:String,

        //Vehicle In first time from masters table....
        val ENGINE_NO: String,
        val ADDRESS: String,
        val INSTANCE_NUMBER:String ,
        val REGISTRATION_DATE:String,
        val EMAIL_ADDRESS:String,
        val ACCOUNT_NUMBER:String,
        val CUST_NAME:String,
        val PRIMARY_PHONE_NUMBER: String,
        val VIN:String,
        val CHASSIS_NO:String,

        val REMARKS:String,
        val inTime:String,
        val REG_NO:String,
        val LOCATION:String,


        val SERVICE_ADVISOR:String,
        val attribute1:String,
        val attribute2:String

    )
}




