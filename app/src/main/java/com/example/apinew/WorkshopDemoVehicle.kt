package com.example.apinew

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.google.android.datatransport.cct.StringMerger
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
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors
import kotlin.math.max
import kotlin.math.min

class WorkshopDemoVehicle : AppCompatActivity() {
    private lateinit var login_name: String
    private lateinit var deptName: String
    private var ouId: Int = 0
    private var locId: Int = 0
    private lateinit var location_name: String
    private lateinit var attribute1:String
    private lateinit var location:String
    private lateinit var username:TextView
    private lateinit var locIdTxt:TextView
    private lateinit var deptIntent:TextView
    private lateinit var vehHistoryLL:View
    //    private lateinit var newVehOutLL:View
    private lateinit var newVehLL:View
    private lateinit var captureVehNumberIn:View
    //    private lateinit var captureVehNumberOut:View
//    private lateinit var newVehOutEditText:EditText
    private lateinit var newVehEditText:EditText
    private lateinit var newVehInButton:ImageButton
    private lateinit var forTestDriveOut:TextView
    private lateinit var forTestDriveIn:TextView
    private lateinit var forNewVehicleIn:TextView
    private lateinit var kmTxt:TextView
    private lateinit var currentKMSField:EditText
    private lateinit var regNoDetails:TextView
    private lateinit var remarksTxt:TextView
    private lateinit var remarksField:EditText
    private lateinit var newVehicleInPremises:TextView
    private lateinit var newVehicleOutPremises:TextView
    private lateinit var refreshButton:TextView
    private lateinit var refresh:TextView
    private lateinit var forNewVehicleOut:TextView
    private lateinit var newVehOutButton:ImageButton
    private lateinit var regNo:String
    private lateinit var jobCardNo:String
    private lateinit var chassisNo:String
    private lateinit var engineNo:String
    private lateinit var vinNo:String
    private lateinit var testDriveNo:String
    private lateinit var custName:String
    private lateinit var fuelDesc:String
    private lateinit var modelDesc:String
    private lateinit var variantDesc:String
    private lateinit var outKm:String
    private lateinit var inKmNewVeh:String
    private lateinit var vehicleHistory:TextView
    private lateinit var captureToKm:ImageButton
    private var clickedPlaceholder: ImageView? = null
    private var photoUri: Uri? = null
    private var photoFile: File? = null
    private lateinit var captureRegNoCameraIn:ImageButton
    //    private lateinit var captureRegNoCameraOut:ImageButton
    private lateinit var bestResult2:String
    private lateinit var attribute5:String
    private lateinit var gateNumber:String
    private lateinit var gateType:String

    private lateinit var custNameTxt:TextView
    private lateinit var custNameEditText:EditText
    private lateinit var custContactTxt:TextView
    private lateinit var custContactEditText:EditText
    private lateinit var custAddressTxt:TextView
    private lateinit var custAddressEditText:EditText
    private lateinit var gateTypeLov:Spinner
    private lateinit var gateTypeTxtView:TextView
    private lateinit var gatePassNo:TextView
    private lateinit var gatePassNoNote:TextView

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
        setContentView(R.layout.activity_workshop_demo_vehicle)

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
//        driverTxt=findViewById(R.id.driverTxt)
//        driverNameField=findViewById(R.id.driverNameField)
        remarksTxt=findViewById(R.id.remarksTxt)
        remarksField=findViewById(R.id.remarksField)
        vehicleHistory=findViewById(R.id.vehicleHistory)
        captureToKm=findViewById(R.id.captureToKm)
        vehHistoryLL=findViewById(R.id.vehHistoryLL)
        forNewVehicleIn=findViewById(R.id.forNewVehicleIn)
        newVehLL=findViewById(R.id.newVehLL)
        newVehEditText=findViewById(R.id.newVehEditText)
        newVehInButton=findViewById(R.id.newVehInButton)
        newVehicleInPremises=findViewById(R.id.newVehicleInPremises)
        forNewVehicleOut=findViewById(R.id.forNewVehicleOut)
        newVehicleOutPremises=findViewById(R.id.newVehicleOutPremises)
        refreshButton=findViewById(R.id.refreshButton)
        refresh=findViewById(R.id.refresh)
        captureVehNumberIn=findViewById(R.id.captureVehNumberIn)
        captureRegNoCameraIn=findViewById(R.id.captureRegNoCameraIn)
//        captureVehNumberOut=findViewById(R.id.captureVehNumberOut)
//        captureRegNoCameraOut=findViewById(R.id.captureRegNoCameraOut)
//        newVehOutLL=findViewById(R.id.newVehOutLL)
//        newVehOutEditText=findViewById(R.id.newVehOutEditText)
        newVehOutButton=findViewById(R.id.newVehOutButton)


        custNameTxt=findViewById(R.id.custNameTxt)
        custNameEditText=findViewById(R.id.custNameEditText)
        custContactTxt=findViewById(R.id.custContactTxt)
        custContactEditText=findViewById(R.id.custContactEditText)
        custAddressTxt=findViewById(R.id.custAddressTxt)
        custAddressEditText=findViewById(R.id.custAddressEditText)
        gateTypeLov=findViewById(R.id.gateTypeLov)
        gateTypeTxtView=findViewById(R.id.gateTypeTxtView)
        gatePassNo=findViewById(R.id.gatePassNo)
        gatePassNoNote=findViewById(R.id.gatePassNoNote)


        kmTxt.visibility=View.GONE
        currentKMSField.visibility=View.GONE
        captureToKm.visibility=View.GONE
        regNoDetails.visibility=View.GONE
//        driverTxt.visibility=View.GONE
//        driverNameField.visibility=View.GONE
        remarksTxt.visibility=View.GONE
        remarksField.visibility=View.GONE
        newVehicleInPremises.visibility=View.GONE
        newVehicleOutPremises.visibility=View.GONE
        refreshButton.visibility=View.GONE
        refresh.visibility=View.GONE

        custNameTxt.visibility=View.GONE
        custNameEditText.visibility=View.GONE
        custContactTxt.visibility=View.GONE
        custContactEditText.visibility=View.GONE
        custAddressTxt.visibility=View.GONE
        custAddressEditText.visibility=View.GONE
        gateTypeLov.visibility=View.GONE
        gateTypeTxtView.visibility=View.GONE
        gatePassNo.visibility=View.GONE
        gatePassNoNote.visibility=View.GONE

        locId = intent.getIntExtra("locId", 0)
        ouId = intent.getIntExtra("ouId", 0)
        deptName = intent.getStringExtra("deptName") ?: ""
        location_name = intent.getStringExtra("location_name") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""
        location=intent.getStringExtra("location") ?:""
        attribute1=intent.getStringExtra("attribute1") ?:""



        username.text=login_name
        locIdTxt.text= location_name
        deptIntent.text=deptName
        newVehLL.visibility=View.GONE
        captureVehNumberIn.visibility=View.GONE


        forNewVehicleIn.setOnClickListener {
            newVehLL.visibility=View.VISIBLE
//            captureVehNumberIn.visibility=View.VISIBLE
            newVehEditText.setText("")
            newVehInButton.visibility=View.VISIBLE
            newVehOutButton.visibility=View.GONE
        }

        forNewVehicleOut.setOnClickListener {
            newVehLL.visibility=View.VISIBLE
//            captureVehNumberIn.visibility=View.VISIBLE
            newVehEditText.setText("")
            newVehInButton.visibility=View.GONE
            newVehOutButton.visibility=View.VISIBLE
        }

        newVehInButton.setOnClickListener { detailsForVehicleInFirstTime() }

        newVehOutButton.setOnClickListener { detailsForVehicleOut() }

        newVehicleInPremises.setOnClickListener { vehicleIn() }

        newVehicleOutPremises.setOnClickListener { vehicleOut() }

        refreshButton.setOnClickListener { resetFields() }

        refresh.setOnClickListener { resetFieldsAfterGatePassNo() }

        captureToKm.setOnClickListener {
            clickedPlaceholder = captureToKm
            openCamera()
        }

        fetchGateNo()



//        captureRegNoCameraIn.setOnClickListener {
//            clickedPlaceholder = captureRegNoCameraIn
//            openCamera(CAMERA_REQUEST_CODE_2)
//        }


        captureRegNoCameraIn.setOnClickListener {
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
//        captureRegNoCameraOut.setOnClickListener {
//            clickedPlaceholder = captureRegNoCameraOut
//            openCamera(CAMERA_REQUEST_CODE_3)
//        }

        vehicleHistory.setOnClickListener { workShopTestDriveVehHistory()  }

    }

    private fun workShopTestDriveVehHistory() {
        val intent = Intent(this@WorkshopDemoVehicle, WorkshopTestDriveVehicleHistory::class.java)
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
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    if (photoFile != null && photoFile!!.exists()) {
                        val bitmap = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                        if (bitmap != null) {
                            if (clickedPlaceholder === captureToKm) {
                                processImageForText(bitmap)
                            }
                        } else {
                            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Image capture failed", Toast.LENGTH_SHORT).show()
                    }
                }
                CAMERA_REQUEST_CODE_2, CAMERA_REQUEST_CODE_3 -> {
                    val imageBitmap = data?.extras?.get("data") as? Bitmap
                    if (imageBitmap != null) {
                        processImageWithMultipleAttempts(imageBitmap, newVehEditText)
                    } else {
                        Toast.makeText(this, "Image capture failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }




    private fun processImageWithMultipleAttempts(originalBitmap: Bitmap, resultTextView: EditText) {
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

    private fun displayBestResult(results: List<String>, resultTextView: EditText) {
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
                    (index == 4 || index == 5 || index == 6) && char == '0' -> 'D'
                    else -> char
                }
            }.joinToString("")

            else -> modifiedString
        }

        modifiedString = modifiedString.trim()
        Log.d("FinalModifiedString", modifiedString)

        when {
            regexVehicleNo.matches(modifiedString) -> {
                resultTextView.setText(modifiedString)
                newVehEditText.setText(modifiedString)
                Log.d("TextRecognition", "Best result: $modifiedString")
                Toast.makeText(this, "Text recognized: $modifiedString", Toast.LENGTH_SHORT).show()
            }
            regexVehicleNo2.matches(modifiedString) -> {
                resultTextView.setText(modifiedString)
                newVehEditText.setText(modifiedString)
                Log.d("TextRecognition", "Best result: $modifiedString")
                Toast.makeText(this, "Text recognized: $modifiedString", Toast.LENGTH_SHORT).show()
            }
            regexVehicleNo3.matches(modifiedString) -> {
                resultTextView.setText(modifiedString)
                newVehEditText.setText(modifiedString)
                Log.d("TextRecognition", "Best result: $modifiedString")
                Toast.makeText(this, "Text recognized: $modifiedString", Toast.LENGTH_SHORT).show()
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

//    private fun fetchGateNo() {
//        val client = OkHttpClient()
//        val request = Request.Builder()
//            .url("${ApiFile.APP_URL}/gateTypeMaster/gateDetailsByLocId?locId=$locId")
//            .build()
//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                val response = client.newCall(request).execute()
//                Log.d("Gate No->",response.toString())
//                val jsonData = response.body?.string()
//                jsonData?.let {
//                    val cities = parseGateNo(it)
//                    runOnUiThread {
//                        val adapter = ArrayAdapter(this@WorkshopDemoVehicle, android.R.layout.simple_spinner_item, cities
//                        )
//                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                        gateTypeLov.adapter = adapter
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }



    private fun fetchGateNo() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.APP_URL}/gateTypeMaster/gateDetailsByLocId?locId=$locId")
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                Log.d("Gate No->", response.toString())
                val jsonData = response.body?.string()
                jsonData?.let {
                    val cities = parseGateNo(it)
                    runOnUiThread {
                        val adapter = ArrayAdapter(this@WorkshopDemoVehicle, android.R.layout.simple_spinner_item, cities)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        gateTypeLov.adapter = adapter

                        gateTypeLov.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                if (position > 0) {  // Check if a valid item is selected (not the "Select Gate Number" option)
                                    val selectedGate = parentView?.getItemAtPosition(position) as String
                                    // Split the selected string into gateNo and gateType
                                    val parts = selectedGate.split("-")
                                    if (parts.size == 2) {
                                        gateNumber = parts[0]
                                        gateType = parts[1]
                                        Log.d("Selected Gate", "Gate Number: $gateNumber, Gate Type: $gateType")
                                    }
                                }
                            }

                            override fun onNothingSelected(parentView: AdapterView<*>?) {
                                // Handle case where no item is selected
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun parseGateNo(jsonData: String): List<String> {
        val cities = mutableListOf<String>()
        try {
            val jsonObject = JSONObject(jsonData)
            val jsonArray = jsonObject.getJSONArray("obj")
            cities.add("Select Gate Number")
            for (i in 0 until jsonArray.length()) {
                val city = jsonArray.getJSONObject(i)
                val gateTpe = city.getString("GATE_TYPE")
                val gateNo = city.getString("GATE_NO")
                gateNumber=city.getString("GATE_NO")
                gateType=city.getString("GATE_TYPE")
                cities.add("$gateNo-$gateTpe")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return cities
    }


    private fun populateFieldsAfterInSearch() {
        kmTxt.visibility=View.VISIBLE
        currentKMSField.visibility=View.VISIBLE
        captureToKm.visibility=View.VISIBLE
        regNoDetails.visibility=View.VISIBLE
        newVehicleInPremises.visibility=View.VISIBLE
        remarksTxt.visibility=View.VISIBLE
        remarksField.visibility=View.VISIBLE
        refreshButton.visibility=View.VISIBLE
        gateTypeTxtView.visibility=View.VISIBLE
        gateTypeLov.visibility=View.VISIBLE
    }

    private fun populateFieldsAfterOutSearch() {
        kmTxt.visibility=View.VISIBLE
        currentKMSField.visibility=View.VISIBLE
        captureToKm.visibility=View.VISIBLE
        regNoDetails.visibility=View.VISIBLE
        newVehicleOutPremises.visibility=View.VISIBLE
        remarksTxt.visibility=View.VISIBLE
        remarksField.visibility=View.VISIBLE
//        driverTxt.visibility=View.VISIBLE
//        driverNameField.visibility=View.VISIBLE
        refreshButton.visibility=View.VISIBLE
        custNameTxt.visibility=View.VISIBLE
        custNameEditText.visibility=View.VISIBLE
        custContactTxt.visibility=View.VISIBLE
        custContactEditText.visibility=View.VISIBLE
        custAddressTxt.visibility=View.VISIBLE
        custAddressEditText.visibility=View.VISIBLE
        gateTypeLov.visibility=View.VISIBLE
        gateTypeTxtView.visibility=View.VISIBLE
    }

    private fun detailsForVehicleInFirstTime() {
        val client = OkHttpClient()
        val vehNo = newVehEditText.text.toString()
        if(vehNo.isEmpty()){
            Toast.makeText(this@WorkshopDemoVehicle,"Please enter the vehicle number",Toast.LENGTH_SHORT).show()
            return
        }
        val url = ApiFile.APP_URL + "/demoVehicleTrans/getDemoVehInDetailsByRegNo?regNo=$vehNo"

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
                        CHASSIS_NO = stockItem.optString("CHASSIS_NO"),
                        VIN = stockItem.optString("VIN"),
                        MODEL_DESC = stockItem.optString("MODEL_DESC"),
                        ENGINE_NO = stockItem.optString("ENGINE_NO"),
                        FUEL_DESC = stockItem.optString("FUEL_DESC"),
                        VARIANT_DESC = stockItem.optString("VARIANT_DESC"),
                        CUST_NAME = stockItem.optString("CUST_NAME"),
                        CUST_ADDRESS = stockItem.optString("CUST_ADDRESS"),
                        CUST_CONTACT_NO = stockItem.optString("CUST_CONTACT_NO"),
                        LOCATION = stockItem.optString("LOCATION"),
                        OUT_TIME = stockItem.optString("OUT_TIME"),
                        OUT_KM = stockItem.optString("OUT_KM"),
                        REMARKS = stockItem.optString("REMARKS"),
                        VEHICLE_NO = stockItem.optString("VEHICLE_NO"),
                        REG_NO = stockItem.optString("REG_NO")
                    )

                    val responseMessage = jsonObject.getString("message")

                    when (responseMessage) {
                        "Details Found Successfully" -> {
                            runOnUiThread {
                                populateFieldsIn(jcData3)
                                populateFieldsAfterInSearch()
                                fetchGateNo()
                                Toast.makeText(
                                    this@WorkshopDemoVehicle,
                                    "Details Found Successfully for vehicle no.: $vehNo",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        else -> {
                            runOnUiThread {
                                Toast.makeText(
                                    this@WorkshopDemoVehicle,
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
                        this@WorkshopDemoVehicle,
                        "Failed to fetch details for vehicle No: $vehNo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

//    private fun detailsForVehicleOut() {
//        val client = OkHttpClient()
//        val vehNo = newVehEditText.text.toString()
//        if(vehNo.isEmpty()){
//            Toast.makeText(this@WorkshopDemoVehicle,"Please enter the vehicle number",Toast.LENGTH_SHORT).show()
//            return
//        }
//        val url = ApiFile.APP_URL + "/demoVehicleTrans/getDemoVehByRegNo?regNo=$vehNo"
//
//        Log.d("URL:", url)
//
//        val request = Request.Builder()
//            .url(url)
//            .build()
//
//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                val response = client.newCall(request).execute()
//                val jsonData = response.body?.string()
//                jsonData?.let {
//                    val jsonObject = JSONObject(it)
//                    Log.d("Data", jsonObject.toString())
//
//                    val stockItem = jsonObject.getJSONArray("obj").getJSONObject(0)
//
//                    val jcData3 = allData(
//                        CHASSIS_NO = stockItem.optString("CHASSIS_NO"),
//                        VIN = stockItem.optString("VIN"),
//                        MODEL_DESC = stockItem.optString("MODEL_DESC"),
//                        ENGINE_NO = stockItem.optString("ENGINE_NO"),
//                        FUEL_DESC = stockItem.optString("FUEL_DESC"),
//                        VARIANT_DESC = stockItem.optString("VARIANT_DESC"),
//                        CUST_NAME = stockItem.optString("CUST_NAME"),
//                        CUST_ADDRESS = stockItem.optString("CUST_ADDRESS"),
//                        CUST_CONTACT_NO = stockItem.optString("CUST_CONTACT_NO"),
//                        LOCATION = stockItem.optString("LOCATION"),
//                        OUT_TIME = stockItem.optString("OUT_TIME"),
//                        OUT_KM = stockItem.optString("OUT_KM"),
//                        REMARKS = stockItem.optString("REMARKS"),
//                        VEHICLE_NO = stockItem.optString("VEHICLE_NO")
//                    )
//
//                    val responseMessage = jsonObject.getString("message")
//
//                    when (responseMessage) {
//                        "Details Found Successfully" -> {
//                            runOnUiThread {
//                                populateFieldsOut(jcData3)
//                                populateFieldsAfterOutSearch()
//                                Toast.makeText(
//                                    this@WorkshopDemoVehicle,
//                                    "Details Found Successfully for vehicle no.: $vehNo",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                        else -> {
//                            runOnUiThread {
//                                Toast.makeText(
//                                    this@WorkshopDemoVehicle,
//                                    "Unexpected response for Vehicle No: $vehNo",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                runOnUiThread {
//                    Toast.makeText(
//                        this@WorkshopDemoVehicle,
//                        "Failed to fetch details for vehicle No: $vehNo",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//    }


    private fun detailsForVehicleOut() {
        val client = OkHttpClient()
        val vehNo = newVehEditText.text.toString()

        if (vehNo.isEmpty()) {
            Toast.makeText(
                this@WorkshopDemoVehicle,
                "Please enter the vehicle number",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val url = ApiFile.APP_URL + "/demoVehicleTrans/getDemoVehByRegNo?regNo=$vehNo"
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

                    val statusCode = jsonObject.getInt("code")
                    val responseMessage = jsonObject.getString("message")

                    runOnUiThread {
                        when (statusCode) {
                            200 -> {
                                val stockItem = jsonObject.getJSONArray("obj").getJSONObject(0)

                                val jcData3 = allData(
                                    CHASSIS_NO = stockItem.optString("CHASSIS_NO"),
                                    VIN = stockItem.optString("VIN"),
                                    MODEL_DESC = stockItem.optString("MODEL_DESC"),
                                    ENGINE_NO = stockItem.optString("ENGINE_NO"),
                                    FUEL_DESC = stockItem.optString("FUEL_DESC"),
                                    VARIANT_DESC = stockItem.optString("VARIANT_DESC"),
                                    CUST_NAME = stockItem.optString("CUST_NAME"),
                                    CUST_ADDRESS = stockItem.optString("CUST_ADDRESS"),
                                    CUST_CONTACT_NO = stockItem.optString("CUST_CONTACT_NO"),
                                    LOCATION = stockItem.optString("LOCATION"),
                                    OUT_TIME = stockItem.optString("OUT_TIME"),
                                    OUT_KM = stockItem.optString("OUT_KM"),
                                    REMARKS = stockItem.optString("REMARKS"),
                                    VEHICLE_NO = stockItem.optString("VEHICLE_NO"),
                                    REG_NO = stockItem.optString("REG_NO")
                                )

                                populateFieldsOut(jcData3)
                                populateFieldsAfterOutSearch()

                                Toast.makeText(
                                    this@WorkshopDemoVehicle,
                                    "Details Found Successfully for vehicle no.: $vehNo",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            400 -> {
                                Toast.makeText(
                                    this@WorkshopDemoVehicle,
                                    responseMessage,
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            else -> {
                                Toast.makeText(
                                    this@WorkshopDemoVehicle,
                                    "Unexpected response: $responseMessage",
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
                        this@WorkshopDemoVehicle,
                        "Failed to fetch details for vehicle No: $vehNo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun vehicleOut() {
        val currentKms = currentKMSField.text.toString()
        val remarks = remarksField.text.toString()
        val gateNo=gateNumber
        val gateType=gateType
        val customerName=custNameEditText.text.toString()
        val customerAddress=custAddressEditText.text.toString()
        val customerContact=custContactEditText.text.toString()

        if (customerName.isEmpty()) {
            Toast.makeText(this, "Enter Customer name to proceed.", Toast.LENGTH_SHORT).show()
            return
        }
        if (customerContact.isEmpty()) {
            Toast.makeText(this, "Enter Customer contact no. to proceed.", Toast.LENGTH_SHORT).show()
            return
        }
        if (customerAddress.isEmpty()) {
            Toast.makeText(this, "Enter Customer address to proceed.", Toast.LENGTH_SHORT).show()
            return
        }

        if(gateNo.isEmpty() &&gateType.isEmpty()){
            Toast.makeText(this,"Please select the Gate.",Toast.LENGTH_SHORT).show()
            return
        }

        if(gateTypeLov.selectedItem.toString()=="Select Gate Number"){
            Toast.makeText(this,"Please select the Gate.",Toast.LENGTH_SHORT).show()
            return
        }

        if (currentKMSField.text.toString().isEmpty()) {
            Toast.makeText(this, "Current Kilometers are required", Toast.LENGTH_SHORT).show()
            return
        }


        val url = ApiFile.APP_URL + "/demoVehicleTrans/demoVehOutProceed/"
        val jsonObject = JSONObject().apply {

            if (::regNo.isInitialized) {
                if(regNo.isNotEmpty() ) {
                    put("regNo", regNo)
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
            if (::engineNo.isInitialized) {
                if(engineNo.isNotEmpty() ) {
                    put("engineNo", engineNo)
                }
            }
            if (::modelDesc.isInitialized) {
                if(modelDesc.isNotEmpty() ) {
                    put("modelDesc", modelDesc)
                }
            }
            if (::variantDesc.isInitialized) {
                if(variantDesc.isNotEmpty() ) {
                    put("variantDesc", variantDesc)
                }
            }
            if (::fuelDesc.isInitialized) {
                if(fuelDesc.isNotEmpty() ) {
                    put("fuelDesc", fuelDesc)
                }
            }
            if(remarks.isNotEmpty()){
                put("remarks",remarks)
            } else {
                put("remarks","-")
            }
            put("custName",customerName)
            put("custContactNo",customerContact)
            put("custAddress",customerAddress)
            put("locId", locId.toString())
            put("ouId", ouId.toString())
            put("createdBy", login_name)
            put("location", location_name)
            put("attribute1",gateNo)
            put("attribute2",gateType)
            put("updatedBy", login_name)
            put("outKm",currentKms)
            put("fromLocation",location)
            put("loginName",login_name)
            put("authorisedBy",attribute1)
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
                            message.contains("Vehicle Already Out For Demo", ignoreCase = true) -> {
                                Toast.makeText(
                                    this@WorkshopDemoVehicle,
                                    "Vehicle Already Out For Demo",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            message.contains("Out km value is less than previous In km", ignoreCase = true) -> {
                                Toast.makeText(
                                    this@WorkshopDemoVehicle,
                                    "Out km value is less than previous In km",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            responseCode == 200 -> {
//                                val gatePassNoString = jsonResponse.optString("gatePassNo", "")
//                                Log.d("gatePassNoString",gatePassNoString)
//                                gatePassNo.text = gatePassNoString
//                                gatePassNo.visibility=View.VISIBLE
                                val obj = jsonResponse.optJSONObject("obj")
                                val gatePassNoString = obj?.optString("gatePassNo", "") ?: ""
                                Log.d("gatePassNoString", gatePassNoString)
                                gatePassNo.text = "Gate Pass No:- $gatePassNoString"
                                gatePassNo.visibility = View.VISIBLE
                                gatePassNoNote.visibility=View.VISIBLE
                                refresh.visibility=View.VISIBLE
                                Toast.makeText(
                                    this@WorkshopDemoVehicle,
                                    "Vehicle out successfully!!!",
                                    Toast.LENGTH_LONG
                                ).show()
                                resetFields()
                            }
                            else -> {
                                Toast.makeText(
                                    this@WorkshopDemoVehicle,
                                    "Failed to save data. Error code: $responseCode",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@WorkshopDemoVehicle,
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
                        this@WorkshopDemoVehicle,
                        "Error saving data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    //Post Data For vehicle out at location for test drive
    private fun vehicleIn() {
        val currentKms=currentKMSField.text.toString()
        val currentKilometersInt=currentKms.toInt()
        val remarks=remarksField.text.toString()
        val outKmInt=outKm.toInt()
        val gateNo=gateNumber
        val gateType=gateType

        if(currentKMSField.text.toString().isEmpty()){
            Toast.makeText(this,"Please enter the Current Kilometers",Toast.LENGTH_SHORT).show()
            return
        }

        if(currentKilometersInt<outKmInt){
            Toast.makeText(this,"Current kilometers must be greater than out kilometers.",Toast.LENGTH_SHORT).show()
            return
        }

        if(gateNo.isEmpty() &&gateType.isEmpty()){
            Toast.makeText(this,"Please select the Gate.",Toast.LENGTH_SHORT).show()
            return
        }

        if(gateTypeLov.selectedItem.toString()=="Select Gate Number"){
            Toast.makeText(this,"Please select the Gate.",Toast.LENGTH_SHORT).show()
            return
        }

        val url = "${ApiFile.APP_URL}/demoVehicleTrans/demoVehInUpdate"
        val json = JSONObject().apply {
            if (::chassisNo.isInitialized) {
                if(chassisNo.isNotEmpty() ) {
                    put("chassisNo", chassisNo)
                }
            }
            if (::regNo.isInitialized) {
                if(regNo.isNotEmpty() ) {
                    put("regNo", regNo)
                }
            }
            if(remarks.isNotEmpty()){
                put("remarks",remarks)
            } else {
                put("remarks","-")
            }
            put("updatedBy",login_name)
            put("inKm",currentKms)
            put("attribute1",gateNo)
            put("attribute2",gateType)

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
                    Toast.makeText(this@WorkshopDemoVehicle, "Failed to update vehicle", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val responseBody = it.body?.string() ?: ""

                    runOnUiThread {
                        when {
                            responseBody.contains("Demo Vehicle Already In", ignoreCase = true) -> {
                                Toast.makeText(
                                    this@WorkshopDemoVehicle,
                                    "Demo Vehicle Already In",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            it.isSuccessful -> {
                                Toast.makeText(
                                    this@WorkshopDemoVehicle,
                                    "Vehicle in successfully!",
                                    Toast.LENGTH_LONG
                                ).show()
                                resetFields()
                                newVehEditText.setText("")
                                newVehLL.visibility=View.GONE
                            }
                            else -> {
                                Toast.makeText(
                                    this@WorkshopDemoVehicle,
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
            "VIN" to jcData.VIN,
            "CHASSIS_NO" to jcData.CHASSIS_NO,
        )

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


    private fun populateFieldsOut(jcData:allData) {
        val table = findViewById<TableLayout>(R.id.tableLayout2)
        table.removeAllViews()

        val detailsMap = mutableMapOf(
            "VEHICLE NO" to jcData.VEHICLE_NO,
            "CHASSIS NO" to jcData.CHASSIS_NO,
            "VIN" to jcData.VIN,
            "ENGINE NO" to jcData.ENGINE_NO,
            "MODEL" to jcData.MODEL_DESC,
            "FUEL DESC" to jcData.FUEL_DESC,
            "VARIANT" to jcData.VARIANT_DESC,
        )

        engineNo=jcData.ENGINE_NO
        chassisNo=jcData.CHASSIS_NO
        modelDesc=jcData.MODEL_DESC
        variantDesc=jcData.VARIANT_DESC
        fuelDesc=jcData.FUEL_DESC
        vinNo=jcData.VIN
        regNo=jcData.VEHICLE_NO


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


    private fun populateFieldsIn(jcData:allData) {
        val table = findViewById<TableLayout>(R.id.tableLayout2)
        table.removeAllViews()

        val detailsMap = mutableMapOf(
            "CHASSIS NO" to jcData.CHASSIS_NO,
            "VIN" to jcData.VIN,
            "ENGINE NO" to jcData.ENGINE_NO,
            "MODEL" to jcData.MODEL_DESC,
            "FUEL DESC" to jcData.FUEL_DESC,
            "VARIANT" to jcData.VARIANT_DESC,
            "CUSTOMER NAME" to jcData.CUST_NAME,
            "CUSTOMER CONTACT" to jcData.CUST_CONTACT_NO,
            "CUSTOMER ADDRESS" to jcData.CUST_ADDRESS,
            "OUT KM" to jcData.OUT_KM,
            "OUT TIME" to jcData.OUT_TIME,
            "LOCATION" to jcData.LOCATION
        )

        engineNo=jcData.ENGINE_NO
        chassisNo=jcData.CHASSIS_NO
        modelDesc=jcData.MODEL_DESC
        variantDesc=jcData.VARIANT_DESC
        fuelDesc=jcData.FUEL_DESC
        vinNo=jcData.VIN
        outKm=jcData.OUT_KM
        regNo=jcData.REG_NO


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
//        driverTxt.visibility=View.GONE
//        driverNameField.visibility=View.GONE
//        driverNameField.setText("")
        captureVehNumberIn.visibility=View.GONE
        remarksTxt.visibility=View.GONE
        remarksField.visibility=View.GONE
        remarksField.setText("")
        gateTypeLov.visibility=View.GONE
        gateTypeTxtView.visibility=View.GONE
        newVehicleOutPremises.visibility=View.GONE
//        newVehLL.visibility=View.GONE
//        newVehEditText.setText("")
        newVehicleInPremises.visibility=View.GONE
        refreshButton.visibility=View.GONE
        gateTypeLov.setSelection(0)
        regNo=""
        jobCardNo=""
        chassisNo=""
        engineNo=""
        vinNo=""
        testDriveNo=""
        custName=""
        outKm=""
        inKmNewVeh=""
        attribute5=""
        gateNumber=""
        gateType=""
        custNameTxt.visibility=View.GONE
        custNameEditText.visibility=View.GONE
        custContactTxt.visibility=View.GONE
        custContactEditText.visibility=View.GONE
        custAddressTxt.visibility=View.GONE
        custAddressEditText.visibility=View.GONE
        custNameEditText.setText("")
        custContactEditText.setText("")
        custAddressEditText.setText("")
    }



    private fun resetFieldsAfterGatePassNo(){
        newVehEditText.setText("")
        newVehLL.visibility=View.GONE
        gatePassNo.visibility=View.GONE
        gatePassNo.text=""
        refresh.visibility=View.GONE
        gatePassNoNote.visibility=View.GONE
    }

    data class allData(
        val VIN:String,
        val VARIANT_DESC:String,
        val CHASSIS_NO:String,
        val MODEL_DESC:String,
        val ENGINE_NO:String,
        val FUEL_DESC:String,
        val CUST_ADDRESS:String,
        val CUST_NAME:String,
        val OUT_KM:String,
        val CUST_CONTACT_NO:String,
        val REMARKS:String,
        val OUT_TIME:String,
        val LOCATION:String,
        val VEHICLE_NO:String,
        val REG_NO:String
    )
}



