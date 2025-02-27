package com.example.apinew

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
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
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.concurrent.Executors
import kotlin.math.max
import kotlin.math.min

class WorkShopGatepass2 : AppCompatActivity() {
    private lateinit var login_name: String
    private lateinit var deptName: String
    private var ouId: Int = 0
    private var locId: Int = 0
    private lateinit var location_name: String
    private lateinit var username:TextView
    private lateinit var locIdTxt:TextView
    private lateinit var deptIntent:TextView
    private lateinit var vehHistoryLL:View
    private lateinit var newVehOutLL:View
    private lateinit var newVehLL:View
    private lateinit var captureVehNumberIn:View
    private lateinit var captureVehNumberOut:View
    private lateinit var newVehOutEditText:EditText
    private lateinit var newVehEditText:EditText
    private lateinit var newVehInButton:ImageButton
    private lateinit var forTestDriveOut:TextView
    private lateinit var forTestDriveIn:TextView
    private lateinit var forNewVehicleIn:TextView
    private lateinit var kmTxt:TextView
    private lateinit var currentKMSField:EditText
    private lateinit var regNoDetails:TextView
    private lateinit var driverTxt:TextView
    private lateinit var driverNameField:EditText
    private lateinit var remarksTxt:TextView
    private lateinit var remarksField:EditText
    private lateinit var vehicleOutForTestDrive:Button
    private lateinit var vehicleInAfterTestDrive:Button
    private lateinit var newVehicleInPremises:Button
    private lateinit var newVehicleOutPremises:Button
    private lateinit var refreshButton:Button
    private lateinit var forNewVehicleOut:TextView
    private lateinit var newVehOutButton:ImageButton
    private lateinit var regNo:String
    private lateinit var jobCardNo:String
    private lateinit var chassisNo:String
    private lateinit var engineNo:String
    private lateinit var vinNo:String
    private lateinit var testDriveNo:String
    private lateinit var custName:String
    private lateinit var outKm:String
    private lateinit var inKmNewVeh:String
    private lateinit var vehicleHistory:TextView
    private lateinit var captureToKm:ImageButton
    private var clickedPlaceholder: ImageView? = null
    private var photoUri: Uri? = null
    private var photoFile: File? = null
    private lateinit var captureRegNoCameraIn:ImageButton
    private lateinit var captureRegNoCameraOut:ImageButton
    private lateinit var bestResult2:String

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        private const val CAMERA_REQUEST_CODE = 101
        private const val CAMERA_REQUEST_CODE_2 = 102
        private const val CAMERA_REQUEST_CODE_3 = 103
        private val recognizer = TextRecognition.getClient(TextRecognizerOptions.Builder().setExecutor(
            Executors.newSingleThreadExecutor()).build())
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workshop_gatepass_km_enquiry)

        username=findViewById(R.id.username)
        locIdTxt=findViewById(R.id.locIdTxt)
        deptIntent=findViewById(R.id.deptIntent)

        forTestDriveOut=findViewById(R.id.forTestDriveOut)
        forTestDriveIn=findViewById(R.id.forTestDriveIn)
        kmTxt=findViewById(R.id.kmTxt)
        currentKMSField=findViewById(R.id.currentKMSField)
        val tableLayout = findViewById<TableLayout>(R.id.tableLayout2)
        tableLayout.removeAllViews()
        regNoDetails=findViewById(R.id.regNoDetails)
        driverTxt=findViewById(R.id.driverTxt)
        driverNameField=findViewById(R.id.driverNameField)
        remarksTxt=findViewById(R.id.remarksTxt)
        remarksField=findViewById(R.id.remarksField)
        vehicleOutForTestDrive=findViewById(R.id.vehicleOutForTestDrive)
        vehicleInAfterTestDrive=findViewById(R.id.vehicleInAfterTestDrive)
        vehicleHistory=findViewById(R.id.vehicleHistory)
        captureToKm=findViewById(R.id.captureToKm)
        vehHistoryLL=findViewById(R.id.vehHistoryLL)
        forNewVehicleIn=findViewById(R.id.forNewVehicleIn)
        newVehLL=findViewById(R.id.newVehLL)
        newVehEditText=findViewById(R.id.newVehEditText)
        newVehInButton=findViewById(R.id.newVehInButton)
        newVehicleInPremises=findViewById(R.id.newVehicleInPremises)
        forNewVehicleOut=findViewById(R.id.forNewVehicleOut)
        newVehOutLL=findViewById(R.id.newVehOutLL)
        newVehOutEditText=findViewById(R.id.newVehOutEditText)
        newVehOutButton=findViewById(R.id.newVehOutButton)
        newVehicleOutPremises=findViewById(R.id.newVehicleOutPremises)
        refreshButton=findViewById(R.id.refreshButton)
        captureVehNumberIn=findViewById(R.id.captureVehNumberIn)
        captureRegNoCameraIn=findViewById(R.id.captureRegNoCameraIn)
        captureVehNumberOut=findViewById(R.id.captureVehNumberOut)
        captureRegNoCameraOut=findViewById(R.id.captureRegNoCameraOut)


        kmTxt.visibility=View.GONE
        currentKMSField.visibility=View.GONE
        captureToKm.visibility=View.GONE
        regNoDetails.visibility=View.GONE
        driverTxt.visibility=View.GONE
        driverNameField.visibility=View.GONE
        remarksTxt.visibility=View.GONE
        remarksField.visibility=View.GONE
        vehicleOutForTestDrive.visibility=View.GONE
        vehicleInAfterTestDrive.visibility=View.GONE
        newVehicleInPremises.visibility=View.GONE
        newVehicleOutPremises.visibility=View.GONE
        refreshButton.visibility=View.GONE



        locId = intent.getIntExtra("locId", 0)
        ouId = intent.getIntExtra("ouId", 0)
        deptName = intent.getStringExtra("deptName") ?: ""
        location_name = intent.getStringExtra("location_name") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""


        username.text=login_name
        locIdTxt.text= location_name
        deptIntent.text=deptName
        newVehLL.visibility=View.GONE
        newVehOutLL.visibility=View.GONE
        captureVehNumberIn.visibility=View.GONE
        captureVehNumberOut.visibility=View.GONE


        forNewVehicleIn.setOnClickListener {
            newVehLL.visibility=View.VISIBLE
            captureVehNumberIn.visibility=View.VISIBLE
            newVehOutLL.visibility=View.GONE
            newVehOutEditText.setText("")
            newVehEditText.setText("")
            captureVehNumberOut.visibility=View.GONE
        }

        forNewVehicleOut.setOnClickListener {
            newVehOutLL.visibility=View.VISIBLE
            captureVehNumberOut.visibility=View.VISIBLE
            captureVehNumberIn.visibility=View.GONE
            newVehLL.visibility=View.GONE
            newVehEditText.setText("")
            newVehOutEditText.setText("")
        }

        newVehInButton.setOnClickListener { detailsForVehicleInFirstTime() }

        newVehOutButton.setOnClickListener { detailsForVehicleOut() }

        newVehicleInPremises.setOnClickListener { vehicleIn() }

        newVehicleOutPremises.setOnClickListener { vehicleOut() }

        refreshButton.setOnClickListener { resetFields() }

        captureToKm.setOnClickListener {
            clickedPlaceholder = captureToKm
            openCamera()
        }


        captureRegNoCameraIn.setOnClickListener {
            clickedPlaceholder = captureRegNoCameraIn
            openCamera(CAMERA_REQUEST_CODE_2)
        }

        captureRegNoCameraOut.setOnClickListener {
            clickedPlaceholder = captureRegNoCameraOut
            openCamera(CAMERA_REQUEST_CODE_3)
        }


        vehicleHistory.setOnClickListener { workShopTestDriveVehHistory()  }

    }

    private fun workShopTestDriveVehHistory() {
        val intent = Intent(this@WorkShopGatepass2, WorkshopTestDriveVehicleHistory::class.java)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name",location_name)
        intent.putExtra("deptName",deptName)
        startActivity(intent)
    }



    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile = createImageFile()
        photoFile?.also {
            photoUri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", it)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivityForResult(takePictureIntent, 101)
        }
    }

    private fun openCamera(requestCode: Int) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile = createImageFile()
        photoFile?.also {
            photoUri =
                FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", it)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivityForResult(takePictureIntent, requestCode)
        }
    }

    private fun createImageFile(): File? {
        val storageDir: File? = externalCacheDir
        return File.createTempFile("JPEG_${System.currentTimeMillis()}_", ".jpg", storageDir).apply {
            photoFile = this
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 101 && resultCode == RESULT_OK) {
//            val bitmap = BitmapFactory.decodeFile(photoFile?.absolutePath)
//            if (clickedPlaceholder == captureToKm) {
//                processImageForText(bitmap)
//            }
//
//        }
//    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
//            val bitmap =
//                BitmapFactory.decodeFile(if (photoFile != null) photoFile!!.absolutePath else null)
            val bitmap = BitmapFactory.decodeFile(photoFile?.absolutePath)
            if (bitmap != null) {
                if (requestCode == CAMERA_REQUEST_CODE) {
                    if (clickedPlaceholder === captureToKm) {
                        processImageForText(bitmap)
                    }
                } else if (requestCode == CAMERA_REQUEST_CODE_2) {
                    // For captureRegNoCameraIn button
                    processImageWithMultipleAttempts(bitmap, newVehEditText)
                } else if (requestCode == CAMERA_REQUEST_CODE_3) {
                    // For captureRegNoCameraOut button
                    processImageWithMultipleAttempts(bitmap, newVehOutEditText)
                }
            }
        }
    }


    private fun processImageForText(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                handleExtractedText(visionText)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to extract text: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun handleExtractedText(result:com.google.mlkit.vision.text.Text) {
        val recognizedText = result.text
        if (recognizedText.isNotEmpty()) {
            val regex = Regex("(\\d+)\\s*(?=km)", RegexOption.IGNORE_CASE)
            val matchResult = regex.find(recognizedText)

            if (matchResult != null) {
                val numericText = matchResult.value.trim()
                currentKMSField.setText(numericText)
            } else {
                Toast.makeText(this, "No valid reading found", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No text found", Toast.LENGTH_SHORT).show()
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
                    else -> char
                }
            }.joinToString("")

            10 -> modifiedString.mapIndexed { index, char ->
                when {
                    (index == 2 || index == 3 || index >= 6) && char == 'O' -> '0'
                    (index == 2 || index == 3 || index >= 6) && char == 'Z' -> '4'
                    (index == 2 || index == 3 || index >= 6) && char == 'S' -> '5'
                    (index == 4 || index == 5) && char == '0' -> 'D'
                    else -> char
                }
            }.joinToString("")

            11 -> modifiedString.mapIndexed { index, char ->
                when {
                    (index == 2 || index == 3 || index >= 7) && char == 'O' -> '0'
                    (index == 2 || index == 3 || index >= 7) && char == 'Z' -> '4'
                    (index == 2 || index == 3 || index >= 7) && char == 'S' -> '5'
                    (index == 4 || index == 5|| index == 6) && char == '0' -> 'D'
                    else -> char
                }
            }.joinToString("")

            else -> modifiedString
        }

        modifiedString = modifiedString.trim()
        Log.d("FinalModifiedString", modifiedString)

        when {
            regexVehicleNo.matches(modifiedString) -> {
                resultTextView.text = modifiedString
                newVehEditText.setText(modifiedString)
                newVehOutEditText.setText(modifiedString)
                Log.d("TextRecognition", "Best result: $modifiedString")
                Toast.makeText(this, "Text recognized: $modifiedString", Toast.LENGTH_SHORT).show()
            }
            regexVehicleNo2.matches(modifiedString) -> {
                resultTextView.text = modifiedString
                newVehEditText.setText(modifiedString)
                newVehOutEditText.setText(modifiedString)
                Log.d("TextRecognition", "Best result: $modifiedString")
                Toast.makeText(this, "Text recognized: $modifiedString", Toast.LENGTH_SHORT).show()
            }
            regexVehicleNo3.matches(modifiedString) -> {
                resultTextView.text = modifiedString
                newVehEditText.setText(modifiedString)
                newVehOutEditText.setText(modifiedString)
                Log.d("TextRecognition", "Best result: $modifiedString")
                Toast.makeText(this, "Text recognized: $modifiedString", Toast.LENGTH_SHORT).show()
            }
            else -> {
                runOnUiThread {
                    Toast.makeText(this, "Invalid format", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    private fun preprocessImage(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val processedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Convert to grayscale and increase contrast
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


    private fun populateFieldsAfterInSearch() {
        kmTxt.visibility=View.VISIBLE
        currentKMSField.visibility=View.VISIBLE
        captureToKm.visibility=View.VISIBLE
        regNoDetails.visibility=View.VISIBLE
        newVehicleInPremises.visibility=View.VISIBLE
        remarksTxt.visibility=View.VISIBLE
        remarksField.visibility=View.VISIBLE
        driverTxt.visibility=View.VISIBLE
        driverNameField.visibility=View.VISIBLE
        refreshButton.visibility=View.VISIBLE
    }

    private fun populateFieldsAfterOutSearch() {
        kmTxt.visibility=View.VISIBLE
        currentKMSField.visibility=View.VISIBLE
        captureToKm.visibility=View.VISIBLE
        regNoDetails.visibility=View.VISIBLE
        newVehicleOutPremises.visibility=View.VISIBLE
        remarksTxt.visibility=View.VISIBLE
        remarksField.visibility=View.VISIBLE
        driverTxt.visibility=View.VISIBLE
        driverNameField.visibility=View.VISIBLE
        refreshButton.visibility=View.VISIBLE
    }

    private fun detailsForVehicleInFirstTime() {
        val client = OkHttpClient()
        val vehNo = newVehEditText.text.toString()
        val url = ApiFile.APP_URL + "/service/wsVehDetForTestDriveIn?regNo=$vehNo"

        Log.d("URL:", url)

        val request = Request.Builder()
            .url(url)
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    Log.d("Data", jsonObject.toString())

                    val stockItem = jsonObject.getJSONArray("obj").getJSONObject(0)

                    val jcData3 = allData(
                        JOBCARDNO = stockItem.optString("JOBCARDNO"),
                        DEPT = stockItem.optString("DEPT"),
                        ENGINENO = stockItem.optString("ENGINENO"),
                        CHASSIS_NO = stockItem.optString("CHASSIS_NO"),
                        REGNO = stockItem.optString("REGNO"),
                        CUSTNAME = stockItem.optString("CUSTNAME"),
                        VIN = stockItem.optString("VIN"),
                        VARIANT_CODE = stockItem.optString("VARIANT_CODE"),
                        CONTACTNO = stockItem.optString("CONTACTNO"),
                        MODEL_DESC = stockItem.optString("MODEL_DESC"),
                        ERPACCTNO = stockItem.optString("ERPACCTNO"),
                        //Masters Table Data
                        ACCOUNT_NUMBER = stockItem.optString("ACCOUNT_NUMBER"),
                        ADDRESS = stockItem.optString("ADDRESS"),
                        CUST_NAME = stockItem.optString("CUST_NAME"),
                        EMAIL_ADDRESS = stockItem.optString("EMAIL_ADDRESS"),
                        ENGINE_NO = stockItem.optString("ENGINE_NO"),
                        INSTANCE_NUMBER = stockItem.optString("INSTANCE_NUMBER"),
                        PRIMARY_PHONE_NUMBER = stockItem.optString("PRIMARY_PHONE_NUMBER"),
                        REGISTRATION_DATE = stockItem.optString("REGISTRATION_DATE"),
                        //Out after vehicle in from location
                        IN_KM = stockItem.optString("IN_KM"),
                        IN_TIME = stockItem.optString("IN_TIME"),
                        REG_NO = stockItem.optString("REG_NO"),
                        REMARKS = stockItem.optString("REMARKS"),
                        TEST_DRIVE_NO = stockItem.optString("TEST_DRIVE_NO"),
                        LOCATION = stockItem.optString("LOCATION"),
                        //In after test Drive
                        OUT_KM = stockItem.optString("OUT_KM"),
                        OUT_TIME = stockItem.optString("OUT_TIME")
                    )

                    val responseMessage = jsonObject.getString("message")

                    when (responseMessage) {
                        "Details Found Successfully In Test Drive Table" -> {
                            runOnUiThread {
                                populateFieldsAfterVehicleInAfterTestDrive(jcData3)
                                populateFieldsAfterInSearch()
                                Toast.makeText(
                                    this@WorkShopGatepass2,
                                    "Details found in Test Drive Table for Vehicle No: $vehNo",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        "Details Found Successfully in Service Stock" -> {
                            runOnUiThread {
                                populateFieldsDuringInStockVehicle(jcData3)
                                populateFieldsAfterInSearch()
                                Toast.makeText(
                                    this@WorkShopGatepass2,
                                    "Details found in Service Stock table for Vehicle No: $vehNo",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        "Details Found Successfully in master table" -> {
                            runOnUiThread {
                                populateFieldsDuringInFromMasterVehicle(jcData3)
                                populateFieldsAfterInSearch()
                                Toast.makeText(
                                    this@WorkShopGatepass2,
                                    "Details Found Successfully in master table for Vehicle No: $vehNo",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        "New Vehicle" -> {
                            runOnUiThread {
                                populateFieldsDuringInForNewVehicle(jcData3)
                                populateFieldsAfterInSearch()
                                Toast.makeText(
                                    this@WorkShopGatepass2,
                                    "New Vehicle details for Vehicle No: $vehNo",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        else -> {
                            runOnUiThread {
                                Toast.makeText(
                                    this@WorkShopGatepass2,
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
                        this@WorkShopGatepass2,
                        "Failed to fetch details for vehicle No: $vehNo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun detailsForVehicleOut() {
        val client = OkHttpClient()
        val vehNo = newVehOutEditText.text.toString()
        val url = ApiFile.APP_URL + "/service/wsVehDetForTestDriveOut?regNo=$vehNo"

        Log.d("URL:", url)

        val request = Request.Builder()
            .url(url)
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    Log.d("Data", jsonObject.toString())

                    val stockItem = jsonObject.getJSONArray("obj").getJSONObject(0)

                    val jcData3 = allData(
                        JOBCARDNO = stockItem.optString("JOBCARDNO"),
                        DEPT = stockItem.optString("DEPT"),
                        ENGINENO = stockItem.optString("ENGINENO"),
                        CHASSIS_NO = stockItem.optString("CHASSIS_NO"),
                        REGNO = stockItem.optString("REGNO"),
                        CUSTNAME = stockItem.optString("CUSTNAME"),
                        VIN = stockItem.optString("VIN"),
                        VARIANT_CODE = stockItem.optString("VARIANT_CODE"),
                        CONTACTNO = stockItem.optString("CONTACTNO"),
                        MODEL_DESC = stockItem.optString("MODEL_DESC"),
                        ERPACCTNO = stockItem.optString("ERPACCTNO"),
                        //Masters Table Data
                        ACCOUNT_NUMBER = stockItem.optString("ACCOUNT_NUMBER"),
                        ADDRESS = stockItem.optString("ADDRESS"),
                        CUST_NAME = stockItem.optString("CUST_NAME"),
                        EMAIL_ADDRESS = stockItem.optString("EMAIL_ADDRESS"),
                        ENGINE_NO = stockItem.optString("ENGINE_NO"),
                        INSTANCE_NUMBER = stockItem.optString("INSTANCE_NUMBER"),
                        PRIMARY_PHONE_NUMBER = stockItem.optString("PRIMARY_PHONE_NUMBER"),
                        REGISTRATION_DATE = stockItem.optString("REGISTRATION_DATE"),
                        //Out after vehicle in from location
                        IN_KM = stockItem.optString("IN_KM"),
                        IN_TIME = stockItem.optString("IN_TIME"),
                        REG_NO = stockItem.optString("REG_NO"),
                        REMARKS = stockItem.optString("REMARKS"),
                        TEST_DRIVE_NO = stockItem.optString("TEST_DRIVE_NO"),
                        LOCATION = stockItem.optString("LOCATION"),
                        //In after test Drive
                        OUT_KM = stockItem.optString("OUT_KM"),
                        OUT_TIME = stockItem.optString("OUT_TIME")
                    )

                    val responseMessage = jsonObject.getString("message")

                    when (responseMessage) {
                        "Details Found Successfully In Test Drive Table" -> {
                            runOnUiThread {
                                populateFieldsAfterVehicleOutForFirstTime(jcData3)
                                populateFieldsAfterOutSearch()
                                Toast.makeText(
                                    this@WorkShopGatepass2,
                                    "Details found in Test Drive Table for Vehicle No: $vehNo",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        "Details Found Successfully In Service Table" -> {
                            runOnUiThread {
                                populateFieldsDuringInStockVehicle(jcData3)
                                populateFieldsAfterOutSearch()
                                Toast.makeText(
                                    this@WorkShopGatepass2,
                                    "Details found in Service Stock table for Vehicle No: $vehNo",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        "Details Found Successfully in master table" -> {
                            runOnUiThread {
                                populateFieldsDuringInFromMasterVehicle(jcData3)
                                populateFieldsAfterOutSearch()
                                Toast.makeText(
                                    this@WorkShopGatepass2,
                                    "Details Found Successfully in master table for Vehicle No: $vehNo",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        "New Vehicle" -> {
                            runOnUiThread {
                                populateFieldsDuringInForNewVehicle(jcData3)
                                populateFieldsAfterOutSearch()
                                Toast.makeText(
                                    this@WorkShopGatepass2,
                                    "New Vehicle details for Vehicle No: $vehNo",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        else -> {
                            runOnUiThread {
                                Toast.makeText(
                                    this@WorkShopGatepass2,
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
                        this@WorkShopGatepass2,
                        "Failed to fetch details for vehicle No: $vehNo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

//Post Data For vehicle in at location and in after test drive
    private fun vehicleIn() {
        val driverName = driverNameField.text.toString()
        val currentKms = currentKMSField.text.toString()
        val remarks = remarksField.text.toString()

        if (currentKMSField.text.toString().isEmpty()) {
            Toast.makeText(this, "Current Kilometers are required", Toast.LENGTH_SHORT).show()
            return
        }

        if (driverName.isEmpty()) {
            Toast.makeText(this, "Driver Name is required", Toast.LENGTH_SHORT).show()
            return
        }

        if (remarks.isEmpty()) {
            Toast.makeText(this, "Remarks required", Toast.LENGTH_SHORT).show()
            return
        }


        if (::outKm.isInitialized) {
            if(outKm.isNotEmpty() ) {
                if(currentKms.toInt()<=outKm.toInt()){
                    Toast.makeText(this, "Current KM must be greater than Out KM", Toast.LENGTH_SHORT).show()
                    return
                }
            }
        }

        val url = ApiFile.APP_URL + "/service/wsVehTdIn/"
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
            if (::jobCardNo.isInitialized) {
                if(jobCardNo.isNotEmpty() ) {
                    put("jobCardNo", jobCardNo)
                }
            }
            if (::engineNo.isInitialized) {
                if(engineNo.isNotEmpty() ) {
                    put("engineNo", engineNo)
                }
            }
            put("locCode", locId.toString())
            put("driverName", driverName)
            put("inKm", currentKms)
            put("ou", ouId.toString())
            put("dept", deptName)
            put("remarks", remarks)
            put("createdBy", login_name)
            put("location", location_name)
            put("authorisedBy", login_name)
            put("updatedBy", login_name)
            if (::custName.isInitialized) {
                if(custName.isNotEmpty() ) {
                    put("attribute1", custName)
                }
            }
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

                Log.d("newVehicleIn", "Response Code: $responseCode")
                Log.d("newVehicleIn", "Response Body: $responseBody")

                runOnUiThread {
                    if (responseBody != null) {
                        val jsonResponse = JSONObject(responseBody)
                        val message = jsonResponse.optString("message", "")

                        when {
                            message.contains("Test Drive Vehicle Already Received", ignoreCase = true) -> {
                                Toast.makeText(
                                    this@WorkShopGatepass2,
                                    "Test Drive Vehicle Already Received",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            message.contains("Cannot receive vehicle", ignoreCase = true) -> {
                                Toast.makeText(
                                    this@WorkShopGatepass2,
                                    "Vehicle Already In...",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            responseCode == 200 -> {
                                Toast.makeText(
                                    this@WorkShopGatepass2,
                                    "Vehicle in successfully!!!",
                                    Toast.LENGTH_LONG
                                ).show()
                                resetFields()
                            }
                            else -> {
                                Toast.makeText(
                                    this@WorkShopGatepass2,
                                    "Failed to save data. Error code: $responseCode",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@WorkShopGatepass2,
                            "No response from server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("newVehicleIn", "Error: ${e.message}")
                runOnUiThread {
                    Toast.makeText(
                        this@WorkShopGatepass2,
                        "Error saving data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


//Post Data For vehicle out at location for test drive
    private fun vehicleOut() {
        val currentKms=currentKMSField.text.toString()
        val currentKilometersInt=currentKms.toInt()
        val remarks=remarksField.text.toString()
        val driverName=driverNameField.text.toString()

        if(currentKMSField.text.toString().isEmpty()){
            Toast.makeText(this,"Please enter the Current Kilometers",Toast.LENGTH_SHORT).show()
            return
        }

        if (::inKmNewVeh.isInitialized) {
            if(inKmNewVeh.isNotEmpty() ) {
               if(currentKilometersInt<inKmNewVeh.toInt()){
                   Toast.makeText(this,"Current Kilometers must be greater than previous km",Toast.LENGTH_SHORT).show()
                   return
               }
            }
        }

        val url = "${ApiFile.APP_URL}/service/wsVehTdOut"
        val json = JSONObject().apply {
            put("updatedBy", login_name)
            put("regNo",regNo)
            put("outKm", currentKms)
            put("dept",deptName)
            put("location",location_name)
            if (::jobCardNo.isInitialized) {
                if(jobCardNo.isNotEmpty() ) {
                    put("jobCardNo", jobCardNo)
                }
            }

            if (::inKmNewVeh.isInitialized) {
                if(inKmNewVeh.isNotEmpty() ) {
                    put("inKm", inKmNewVeh)
                }
            }

            if (::engineNo.isInitialized) {
                if(engineNo.isNotEmpty() ) {
                    put("engineNo", engineNo)
                }
            }
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

            put("remarks",remarks)
            put("driverName",driverName)
            put("createdBy",login_name)
            put("authorisedBy",login_name)
            put("ou",ouId)
            put("locCode",locId)

        }
        Log.d("URL:", url)

        val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        Log.d("URL FOR UPDATE:", json.toString())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@WorkShopGatepass2, "Failed to update vehicle", Toast.LENGTH_SHORT).show()
                }
            }
//            override fun onResponse(call: Call, response: Response) {
//                response.use {
//                    if (it.isSuccessful) {
//                        runOnUiThread {
//                            Toast.makeText(this@WorkShopGatepass2, "Vehicle out", Toast.LENGTH_SHORT).show()
//                            resetFields()
//                        }
//                    } else {
//                        val responseBody = it.body?.string() ?: ""
//                        val errorMessage = if (responseBody.contains("Invalid VIN")) {
//                            "Invalid VIN"
//                        } else {
//                            "Unexpected code ${it.code}"
//                        }
//                        runOnUiThread {
//                            Toast.makeText(this@WorkShopGatepass2, errorMessage, Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
//            }
        override fun onResponse(call: Call, response: Response) {
            response.use {
                val responseBody = it.body?.string() ?: ""

                runOnUiThread {
                    when {
                        responseBody.contains("Vehicle is already out, cannot move out for test drive. Receive the vehicle first.", ignoreCase = true) -> {
                            Toast.makeText(
                                this@WorkShopGatepass2,
                                "Vehicle is already out, cannot move out for test drive.\n Receive the vehicle first.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        it.isSuccessful -> {
                            Toast.makeText(
                                this@WorkShopGatepass2,
                                "Vehicle out successfully!",
                                Toast.LENGTH_LONG
                            ).show()
                            resetFields()
                        }
                        responseBody.contains("Invalid serial number format in testDriveNo.", ignoreCase = true) -> {
                            Toast.makeText(
                                this@WorkShopGatepass2,
                                "Invalid serial number format in testDriveNo.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        else -> {
                            Toast.makeText(
                                this@WorkShopGatepass2,
                                "Failed to update vehicle. Error code: ${it.code}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }

        })
    }


    private fun populateFieldsDuringInStockVehicle(jcData:allData) {
        val table = findViewById<TableLayout>(R.id.tableLayout2)
        table.removeAllViews()

        val detailsMap = mutableMapOf(
            "REGNO" to jcData.REGNO,
            "JOBCARDNO" to jcData.JOBCARDNO,
            "VIN" to jcData.VIN,
            "CHASSIS_NO" to jcData.CHASSIS_NO,
            "ENGINENO" to jcData.ENGINENO,
            "VARIANT_CODE" to jcData.VARIANT_CODE,
            "MODEL_DESC" to jcData.MODEL_DESC,
            "IN KM" to jcData.IN_KM,
            "DEPT." to jcData.DEPT,
            "CONTACTNO" to jcData.CONTACTNO,
            "ERPACCTNO" to jcData.ERPACCTNO,
            "CUSTNAME" to jcData.CUSTNAME
        )

        regNo=jcData.REGNO
        jobCardNo=jcData.JOBCARDNO
        engineNo=jcData.ENGINENO
        vinNo=jcData.VIN
        chassisNo=jcData.CHASSIS_NO
        custName=jcData.CUSTNAME
        inKmNewVeh=jcData.IN_KM


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


    private fun populateFieldsDuringInFromMasterVehicle(jcData:allData) {
        val table = findViewById<TableLayout>(R.id.tableLayout2)
        table.removeAllViews()

        val detailsMap = mutableMapOf(
            "REGNO" to jcData.INSTANCE_NUMBER,
            "CHASSIS_NO" to jcData.CHASSIS_NO,
            "ENGINENO" to jcData.ENGINE_NO,
            "ADDRESS" to jcData.ADDRESS,
            "REGISTRATION_DATE" to jcData.REGISTRATION_DATE,
            "EMAIL_ADDRESS" to jcData.EMAIL_ADDRESS,
            "ACCOUNT_NUMBER" to jcData.ACCOUNT_NUMBER,
            "CUST_NAME" to jcData.CUST_NAME,
            "PRIMARY_PHONE_NUMBER" to jcData.PRIMARY_PHONE_NUMBER
        )

        regNo=jcData.INSTANCE_NUMBER
        engineNo=jcData.ENGINE_NO
        chassisNo=jcData.CHASSIS_NO
        custName=jcData.CUST_NAME

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

    private fun populateFieldsDuringInForNewVehicle(jcData:allData) {
        val table = findViewById<TableLayout>(R.id.tableLayout2)
        table.removeAllViews()

        val detailsMap = mutableMapOf(
            "REGNO" to jcData.REGNO,
            "MESSAGE" to "This is a new vehicle"
        )
        regNo=jcData.REGNO

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

    private fun populateFieldsAfterVehicleOutForFirstTime(jcData:allData) {
        val table = findViewById<TableLayout>(R.id.tableLayout2)
        table.removeAllViews()

        val detailsMap = mutableMapOf(
            "REGNO" to jcData.REG_NO,
            "JOBCARDNO" to jcData.JOBCARDNO,
            "VIN" to jcData.VIN,
            "CHASSIS_NO" to jcData.CHASSIS_NO,
            "ENGINENO" to jcData.ENGINE_NO,
            "DEPT." to jcData.DEPT,
            "LOCATION" to jcData.LOCATION,
            "TEST DRIVE NO" to jcData.TEST_DRIVE_NO,
            "IN KM" to jcData.IN_KM,
            "IN TIME" to jcData.IN_TIME,
            "REMARKS" to jcData.REMARKS
        )

        regNo=jcData.REG_NO
        inKmNewVeh=jcData.IN_KM
        jobCardNo=jcData.JOBCARDNO
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

    private fun populateFieldsAfterVehicleInAfterTestDrive(jcData:allData) {
        val table = findViewById<TableLayout>(R.id.tableLayout2)
        table.removeAllViews()

        val detailsMap = mutableMapOf(
            "REGNO" to jcData.REG_NO,
            "JOBCARDNO" to jcData.JOBCARDNO,
            "VIN" to jcData.VIN,
            "CHASSIS_NO" to jcData.CHASSIS_NO,
            "ENGINENO" to jcData.ENGINE_NO,
            "DEPT." to jcData.DEPT,
            "LOCATION" to jcData.LOCATION,
            "CUSTNAME" to jcData.CUSTNAME,
            "TEST DRIVE NO" to jcData.TEST_DRIVE_NO,
            "OUT KM" to jcData.OUT_KM,
            "OUT TIME" to jcData.OUT_TIME,
            "REMARKS" to jcData.REMARKS
        )

        regNo=jcData.REG_NO
        outKm=jcData.OUT_KM
        jobCardNo=jcData.JOBCARDNO
        engineNo=jcData.ENGINE_NO
        vinNo=jcData.VIN
        chassisNo=jcData.CHASSIS_NO
        custName=jcData.CUST_NAME


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

    private fun resetFields(){
        val tableLayout = findViewById<TableLayout>(R.id.tableLayout2)
        tableLayout.removeAllViews()
        kmTxt.visibility=View.GONE
        currentKMSField.visibility=View.GONE
        captureToKm.visibility=View.GONE
        currentKMSField.setText("")
        regNoDetails.visibility=View.GONE
        driverTxt.visibility=View.GONE
        driverNameField.visibility=View.GONE
        driverNameField.setText("")
        captureVehNumberIn.visibility=View.GONE
        captureVehNumberOut.visibility=View.GONE
        remarksTxt.visibility=View.GONE
        remarksField.visibility=View.GONE
        remarksField.setText("")
        vehicleOutForTestDrive.visibility=View.GONE
        vehicleInAfterTestDrive.visibility=View.GONE
        newVehOutEditText.setText("")
        newVehOutLL.visibility=View.GONE
        newVehicleOutPremises.visibility=View.GONE
        newVehLL.visibility=View.GONE
        newVehEditText.setText("")
        newVehicleInPremises.visibility=View.GONE
        refreshButton.visibility=View.GONE
        regNo=""
        jobCardNo=""
        chassisNo=""
        engineNo=""
        vinNo=""
        testDriveNo=""
        custName=""
        outKm=""
        inKmNewVeh=""
    }

    data class allData(
        //Vehicle In first time from stock table....
        val DEPT: String,
        val CONTACTNO:String,
        val REGNO: String,
        val JOBCARDNO: String,
        val ERPACCTNO: String,
        val CUSTNAME:String ,
        val VARIANT_CODE:String ,
        val VIN:String ,
        val ENGINENO: String,
        val CHASSIS_NO: String,
        val MODEL_DESC: String,

        //Vehicle In first time from masters table....
       val ENGINE_NO: String,
        val ADDRESS: String,
        val INSTANCE_NUMBER:String ,
        val REGISTRATION_DATE:String,
        val EMAIL_ADDRESS:String,
        val ACCOUNT_NUMBER:String,
        val CUST_NAME:String,
        val PRIMARY_PHONE_NUMBER: String,

        //Vehicle out for test drive
        val IN_KM:String,
        val TEST_DRIVE_NO:String,
        val REMARKS:String,
        val IN_TIME:String,
        val REG_NO:String,
        val LOCATION:String,

        //In after test Drive
        val OUT_KM:String,
        val OUT_TIME:String
    )

}



