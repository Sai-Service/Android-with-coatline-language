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
    private lateinit var driverTxt:TextView
    private lateinit var driverNameField:EditText
    private lateinit var remarksTxt:TextView
    private lateinit var remarksField:EditText
    private lateinit var vehicleOutForTestDrive:Button
    private lateinit var vehicleInAfterTestDrive:Button
    private lateinit var newVehicleInPremises:TextView
    private lateinit var newVehicleOutPremises:TextView
    private lateinit var refreshButton:TextView
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
//    private lateinit var captureRegNoCameraOut:ImageButton
    private lateinit var bestResult2:String
    private lateinit var reasonCodeLov:Spinner
    private lateinit var gateNoLov:Spinner
    private lateinit var reasonCodeTxtView:TextView
    private lateinit var gateNumberTxtView:TextView
    private lateinit var gateTypeLov:Spinner
    private lateinit var gateTypeTxtView:TextView
    private lateinit var transferLocationTxtView:TextView
    private lateinit var transferLocationLov:Spinner
    private lateinit var parkingEditText:EditText
    private lateinit var attribute5:String
    private lateinit var parkingEditTextTextView:TextView
    private lateinit var gateNumber:String
    private lateinit var gateType:String


    private lateinit var physicallyOutLL:View
    private lateinit var physicallyOutButton:TextView
    private lateinit var physicallyOutLLTxt:View
    private lateinit var physicallyOutVehEditText:EditText
    private lateinit var physicallyOutVehButton:ImageButton
    private lateinit var physicallyOutVehicleSave:TextView


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
        captureVehNumberIn=findViewById(R.id.captureVehNumberIn)
        captureRegNoCameraIn=findViewById(R.id.captureRegNoCameraIn)
        newVehOutButton=findViewById(R.id.newVehOutButton)
        physicallyOutVehEditText=findViewById(R.id.physicallyOutVehEditText)

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


        val newVehEditText = findViewById<EditText>(R.id.newVehEditText)
        newVehEditText.filters = arrayOf(noSpaceFilter, blockSpecialCharFilter, InputFilter.AllCaps())
//        newVehEditText.filters = arrayOf(noSpaceFilter)
//        newVehEditText.filters = arrayOf(blockSpecialCharFilter)

//        val physicallyOutVehEditText = findViewById<EditText>(R.id.physicallyOutVehEditText)
        physicallyOutVehEditText.filters = arrayOf(noSpaceFilter, blockSpecialCharFilter, InputFilter.AllCaps())
//        physicallyOutVehEditText.filters = arrayOf(noSpaceFilter)
//        physicallyOutVehEditText.filters = arrayOf(blockSpecialCharFilter)



        reasonCodeLov=findViewById(R.id.reasonCodeLov)
        gateNoLov=findViewById(R.id.gateNoLov)
        gateTypeLov=findViewById(R.id.gateTypeLov)
        gateTypeTxtView=findViewById(R.id.gateTypeTxtView)
        reasonCodeTxtView=findViewById(R.id.reasonCodeTxtView)
        gateNumberTxtView=findViewById(R.id.gateNumberTxtView)
        transferLocationTxtView=findViewById(R.id.transferLocationTxtView)
        transferLocationLov=findViewById(R.id.transferLocationLov)
        parkingEditText=findViewById(R.id.parkingEditText)
        parkingEditTextTextView=findViewById(R.id.parkingEditTextTextView)

        physicallyOutLL=findViewById(R.id.physicallyOutLL)
        physicallyOutButton=findViewById(R.id.physicallyOutButton)
        physicallyOutLLTxt=findViewById(R.id.physicallyOutLLTxt)
        physicallyOutVehButton=findViewById(R.id.physicallyOutVehButton)
        physicallyOutVehicleSave=findViewById(R.id.physicallyOutVehicleSave)

        kmTxt.visibility=View.GONE
        currentKMSField.visibility=View.GONE
        captureToKm.visibility=View.GONE
        regNoDetails.visibility=View.GONE
        driverTxt.visibility=View.GONE
        driverNameField.visibility=View.GONE
        remarksTxt.visibility=View.GONE
        remarksField.visibility=View.GONE
        newVehicleInPremises.visibility=View.GONE
        newVehicleOutPremises.visibility=View.GONE
        refreshButton.visibility=View.GONE
        reasonCodeLov.visibility=View.GONE
        gateNoLov.visibility=View.GONE
        gateTypeLov.visibility=View.GONE
        reasonCodeTxtView.visibility=View.GONE
        gateNumberTxtView.visibility=View.GONE
        gateTypeTxtView.visibility=View.GONE

        transferLocationTxtView.visibility=View.GONE
        transferLocationLov.visibility=View.GONE
        parkingEditText.visibility=View.GONE
        parkingEditTextTextView.visibility=View.GONE

        physicallyOutVehicleSave.visibility=View.GONE
        physicallyOutLLTxt.visibility=View.GONE


        locId = intent.getIntExtra("locId", 0)
        ouId = intent.getIntExtra("ouId", 0)
        deptName = intent.getStringExtra("deptName") ?: ""
        location_name = intent.getStringExtra("location_name") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""


        username.text=login_name
        locIdTxt.text= location_name
        deptIntent.text=deptName
        newVehLL.visibility=View.GONE
        captureVehNumberIn.visibility=View.GONE


        forNewVehicleIn.setOnClickListener {
            newVehLL.visibility=View.VISIBLE
            captureVehNumberIn.visibility=View.VISIBLE
            newVehEditText.setText("")
            physicallyOutVehEditText.setText("")
            physicallyOutLLTxt.visibility=View.GONE
            newVehInButton.visibility=View.VISIBLE
            newVehOutButton.visibility=View.GONE
            Toast.makeText(this@WorkShopGatepass2,"Vehicle In option selected",Toast.LENGTH_SHORT).show()
        }

        forNewVehicleOut.setOnClickListener {
            newVehLL.visibility=View.VISIBLE
            captureVehNumberIn.visibility=View.VISIBLE
            newVehEditText.setText("")
            physicallyOutVehEditText.setText("")
            physicallyOutLLTxt.visibility=View.GONE
            newVehInButton.visibility=View.GONE
            newVehOutButton.visibility=View.VISIBLE
            Toast.makeText(this@WorkShopGatepass2,"Vehicle Out option selected",Toast.LENGTH_SHORT).show()
        }

        newVehInButton.setOnClickListener { detailsForVehicleInFirstTime() }

        newVehOutButton.setOnClickListener { detailsForVehicleOut() }

        newVehicleInPremises.setOnClickListener { vehicleIn() }

        newVehicleOutPremises.setOnClickListener { vehicleOut() }

        refreshButton.setOnClickListener { resetFields() }

        physicallyOutVehButton.setOnClickListener { detailsForPhysicallyOutVehicle() }

        physicallyOutVehicleSave.setOnClickListener { vehiclePhysicallyOut() }

        physicallyOutButton.setOnClickListener {
            physicallyOutLLTxt.visibility=View.VISIBLE
            newVehLL.visibility=View.GONE
            newVehEditText.setText("")
            Toast.makeText(this@WorkShopGatepass2,"Vehicle Physically Out option selected",Toast.LENGTH_SHORT).show()
        }


        captureToKm.setOnClickListener {
            clickedPlaceholder = captureToKm
            openCamera()
        }

        fetchCityData()
        fetchGateNo()

        gateNoLov.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                fetchGateType()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        reasonCodeLov.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if(reasonCodeLov.selectedItem.toString()=="LOCATION-TRANSFER PARKING"){
                    transferLocationTxtView.visibility=View.VISIBLE
                    transferLocationLov.visibility=View.VISIBLE
                    fetchOrgIds()
                    parkingEditText.visibility=View.GONE
                    parkingEditTextTextView.visibility=View.GONE
                } else if(reasonCodeLov.selectedItem.toString()=="PARKING"){
                    parkingEditTextTextView.visibility=View.VISIBLE
                    parkingEditText.visibility=View.VISIBLE
                    transferLocationLov.visibility=View.GONE
                    transferLocationTxtView.visibility=View.GONE
                    attribute5=parkingEditText.text.toString()
                } else if(reasonCodeLov.selectedItem.toString()=="TRIAL"){
                    transferLocationTxtView.visibility=View.GONE
                    transferLocationLov.visibility=View.GONE
                    resetSpinner2()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }


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
                        processImageWithMultipleAttempts(imageBitmap,physicallyOutVehEditText)
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
                    (index == 0) && char == 'N' -> 'M'//Added on 16-05-2025
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
                resultTextView.setText(modifiedString)
                newVehEditText.setText(modifiedString)
                physicallyOutVehEditText.setText(modifiedString)
                Toast.makeText(this, "Text recognized: $modifiedString", Toast.LENGTH_SHORT).show()
            }
            regexVehicleNo2.matches(modifiedString) -> {
                resultTextView.setText(modifiedString)
                newVehEditText.setText(modifiedString)
                physicallyOutVehEditText.setText(modifiedString)
                Toast.makeText(this, "Text recognized: $modifiedString", Toast.LENGTH_SHORT).show()
            }
            regexVehicleNo3.matches(modifiedString) -> {
                resultTextView.setText(modifiedString)
                newVehEditText.setText(modifiedString)
                physicallyOutVehEditText.setText(modifiedString)
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

    private fun fetchCityData() {
        val cmnType="TESTDRIVE"
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.APP_URL}/fndcom/testDriveType?cmnType=$cmnType")
            .build()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val cities = parseCities(it)
                    runOnUiThread {
                        val adapter = ArrayAdapter(this@WorkShopGatepass2, android.R.layout.simple_spinner_item, cities
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        reasonCodeLov.adapter = adapter
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun parseCities(jsonData: String): List<String> {
        val cities = mutableListOf<String>()
        try {
            val jsonObject = JSONObject(jsonData)
            val jsonArray = jsonObject.getJSONArray("obj")
            cities.add("Select Test Drive Reason")
            for (i in 0 until jsonArray.length()) {
                val city = jsonArray.getJSONObject(i)
                val desc = city.getString("CMNDESC")
                cities.add(desc)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return cities
    }


    private fun fetchGateNo() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.APP_URL}/gateTypeMaster/gateDetailsByLocId?locId=$locId")
            .build()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val cities = parseGateNo(it)
                    runOnUiThread {
                        val adapter = ArrayAdapter(this@WorkShopGatepass2, android.R.layout.simple_spinner_item, cities
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        gateNoLov.adapter = adapter
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


    private fun fetchGateType() {
        val client = OkHttpClient()
        val gateNo = gateNoLov.selectedItem.toString()
        if (gateNo == "Select Gate Number") {
            Toast.makeText(this,"Please select the Gate Number.",Toast.LENGTH_SHORT).show()
            return
        } else {
        val request = Request.Builder()
            .url("${ApiFile.APP_URL}/gateTypeMaster/gateTypeByLocIdAndGateNo?locId=$locId&gateNo=$gateNo")
            .build()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val cities = parseGateType(it)
                    runOnUiThread {
                        val adapter = ArrayAdapter(
                            this@WorkShopGatepass2, android.R.layout.simple_spinner_item, cities
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        gateTypeLov.adapter = adapter
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    }

    private fun parseGateType(jsonData: String): List<String> {
        val cities = mutableListOf<String>()
        try {
            val jsonObject = JSONObject(jsonData)
            val jsonArray = jsonObject.getJSONArray("obj")
            cities.add("Select Gate Type")
            for (i in 0 until jsonArray.length()) {
                val city = jsonArray.getJSONObject(i)
                val gateTpe = city.getString("GATE_TYPE")
//                val gateNo = city.getString("GATE_NO")
                cities.add(gateTpe)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return cities
    }


    private fun fetchOrgIds() {
        val client = OkHttpClient()
            val request = Request.Builder()
                .url("${ApiFile.APP_URL}/orgDef/getLocation?operating_unit=$ouId")
                .build()
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val response = client.newCall(request).execute()
                    val jsonData = response.body?.string()
                    jsonData?.let {
                        val cities = parseOrgIds(it)
                        runOnUiThread {
                            val adapter = ArrayAdapter(
                                this@WorkShopGatepass2, android.R.layout.simple_spinner_item, cities
                            )
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            transferLocationLov.adapter = adapter
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

    }

    private fun parseOrgIds(jsonData: String): List<String> {
        val cities = mutableListOf<String>()
        try {
            val jsonObject = JSONObject(jsonData)
            val jsonArray = jsonObject.getJSONArray("obj")
            cities.add("Select Transfer Location")
            for (i in 0 until jsonArray.length()) {
                val city = jsonArray.getJSONObject(i)
                val location = city.getString("LOCATIONNAME")
                cities.add(location)
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
        driverTxt.visibility=View.VISIBLE
        driverNameField.visibility=View.VISIBLE
        refreshButton.visibility=View.VISIBLE
//        reasonCodeLov.visibility=View.VISIBLE
        gateNumberTxtView.visibility=View.VISIBLE
        gateNoLov.visibility=View.VISIBLE
        gateTypeLov.visibility=View.GONE
        gateTypeTxtView.visibility=View.GONE
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
        reasonCodeLov.visibility=View.VISIBLE
        gateNoLov.visibility=View.VISIBLE
        reasonCodeTxtView.visibility=View.VISIBLE
        gateNumberTxtView.visibility=View.VISIBLE
        gateTypeTxtView.visibility=View.GONE
        gateTypeLov.visibility=View.GONE
    }


    private fun populateFieldsForPhysicallyOutSearch() {
        kmTxt.visibility=View.VISIBLE
        currentKMSField.visibility=View.VISIBLE
        captureToKm.visibility=View.VISIBLE
        regNoDetails.visibility=View.VISIBLE
        physicallyOutVehicleSave.visibility=View.VISIBLE
        refreshButton.visibility=View.VISIBLE
    }

    private fun resetFieldsAfterPhysicallyOut(){
        physicallyOutLLTxt.visibility=View.GONE
        physicallyOutVehEditText.setText("")
        regNoDetails.visibility=View.GONE
        val tableLayout = findViewById<TableLayout>(R.id.tableLayout2)
        tableLayout.removeAllViews()
        kmTxt.visibility=View.GONE
        captureToKm.visibility=View.GONE
        currentKMSField.setText("")
        currentKMSField.visibility=View.GONE
        physicallyOutVehicleSave.visibility=View.GONE
        regNo=""
        testDriveNo=""
        refreshButton.visibility=View.GONE
    }

    private fun detailsForVehicleInFirstTime() {
        val client = OkHttpClient()
        val vehNo = newVehEditText.text.toString()
        if(vehNo.isEmpty()){
            Toast.makeText(this@WorkShopGatepass2,"Please enter vehicle number.",Toast.LENGTH_SHORT).show()
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
                        OUT_TIME = stockItem.optString("OUT_TIME"),
                        SERVICE_ADVISOR=stockItem.optString("SERVICE_ADVISOR")
                    )

                    val responseMessage = jsonObject.getString("message")

                    when (responseMessage) {
                        "Details Found Successfully In Test Drive Table" -> { //1
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
                        "Details Found Successfully in Service Stock" -> { //2
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
                        "Details Found Successfully in master table" -> { //3
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
                        "New Vehicle" -> { //4
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
        val vehNo = newVehEditText.text.toString()
        if(vehNo.isEmpty()){
            Toast.makeText(this@WorkShopGatepass2,"Please enter vehicle number.",Toast.LENGTH_SHORT).show()
            return
        }
        val url = ApiFile.APP_URL + "/service/wsVehDetForTestDriveOut?regNo=$vehNo"

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
                        OUT_TIME = stockItem.optString("OUT_TIME"),
                        SERVICE_ADVISOR=stockItem.optString("SERVICE_ADVISOR")
                    )

                    val responseMessage = jsonObject.getString("message")

                    when (responseMessage) {
                        "Details Found Successfully In Test Drive Table" -> {
                            runOnUiThread {
                                populateFieldsAfterVehicleOutForFirstTime(jcData3)
                                populateFieldsAfterOutSearch()
                                Log.d("jsonData",jsonData)
                                Log.d("jsonObject",jsonObject.toString())

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
                                Log.d("jsonData",jsonData)
                                Log.d("jsonObject",jsonObject.toString())
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
                                Log.d("jsonData",jsonData)
                                Log.d("jsonObject",jsonObject.toString())
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
                                Log.d("jsonData",jsonData)
                                Log.d("jsonObject",jsonObject.toString())
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


//    private fun detailsForPhysicallyOutVehicle() {
//        val client = OkHttpClient()
//        val vehNo = physicallyOutVehEditText.text.toString()
//        if(vehNo.isEmpty()){
//            Toast.makeText(this@WorkShopGatepass2,"Please enter vehicle number.",Toast.LENGTH_SHORT).show()
//            return
//        }
//        val url = ApiFile.APP_URL + "/service/wsVehDetTestDriveDelivered?regNo=$vehNo"
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
//
//                    val stockItem = jsonObject.getJSONArray("obj").getJSONObject(0)
//
//                    val jcData3 = physicallyOutData(
//                        REG_NO = stockItem.optString("REG_NO"),
//                        IN_TIME = stockItem.optString("IN_TIME"),
//                        IN_KM = stockItem.optString("IN_KM"),
//                        TEST_DRIVE_NO =stockItem.optString("TEST_DRIVE_NO")
//                    )
//                    val responseMessage = jsonObject.getString("message")
//
//                    when (responseMessage) {
//                        "Details Found Successfully" -> {
//                            runOnUiThread {
//                                populateFieldsForPhysicallyOutVehicle(jcData3)
//                               populateFieldsForPhysicallyOutSearch()
//                                Toast.makeText(
//                                    this@WorkShopGatepass2,
//                                    "Details Found Successfully for Vehicle No: $vehNo\nYou can out vehicle physically.",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                        "Details Not Found" -> {
//                            runOnUiThread {
//                                Toast.makeText(
//                                    this@WorkShopGatepass2,
//                                    "Vehicle is not delivered yet.",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                        else -> {
//                            runOnUiThread {
//                                Toast.makeText(
//                                    this@WorkShopGatepass2,
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
//                        this@WorkShopGatepass2,
//                        "Failed to fetch details for vehicle No: $vehNo",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//    }


    private fun detailsForPhysicallyOutVehicle() {
        val client = OkHttpClient()
        val vehNo = physicallyOutVehEditText.text.toString()
        if (vehNo.isEmpty()) {
            Toast.makeText(this@WorkShopGatepass2, "Please enter vehicle number.", Toast.LENGTH_SHORT).show()
            return
        }
        val url = ApiFile.APP_URL + "/service/wsVehDetTestDriveDelivered?regNo=$vehNo"

        val request = Request.Builder()
            .url(url)
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    val responseMessage = jsonObject.getString("message")

                    when (responseMessage) {
                        "Details Found Successfully" -> {
                            val obj = jsonObject.get("obj")
                            if (obj is JSONArray) {
                                val stockItem = obj.getJSONObject(0)

                                val jcData3 = physicallyOutData(
                                    REG_NO = stockItem.optString("REG_NO"),
                                    IN_TIME = stockItem.optString("IN_TIME"),
                                    IN_KM = stockItem.optString("IN_KM"),
                                    TEST_DRIVE_NO = stockItem.optString("TEST_DRIVE_NO")
                                )

                                runOnUiThread {
                                    populateFieldsForPhysicallyOutVehicle(jcData3)
                                    populateFieldsForPhysicallyOutSearch()
                                    Toast.makeText(
                                        this@WorkShopGatepass2,
                                        "Details Found Successfully for Vehicle No: $vehNo\nYou can out vehicle physically.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                runOnUiThread {
                                    Toast.makeText(
                                        this@WorkShopGatepass2,
                                        "Unexpected format received from server.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }

                        "Details Not Found" -> {
                            runOnUiThread {
                                Toast.makeText(
                                    this@WorkShopGatepass2,
                                    "Vehicle is not delivered yet.",
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
//        val gateNo=gateNoLov.selectedItem.toString()
        val gateNo=gateNumber
//        val gateType=gateTypeLov.selectedItem.toString()
        val gateType=gateType

    if(gateNo=="Select Gate Number"){
            Toast.makeText(this,"Please select the Gate Number.",Toast.LENGTH_SHORT).show()
            return
        }

        if(gateType=="Select Gate Type"){
            Toast.makeText(this,"Please select the Gate Type.",Toast.LENGTH_SHORT).show()
            return
        }

        if (currentKMSField.text.toString().isEmpty()) {
            Toast.makeText(this, "Current Kilometers are required", Toast.LENGTH_SHORT).show()
            return
        }

        if (driverName.isEmpty()) {
            Toast.makeText(this, "Driver Name is required", Toast.LENGTH_SHORT).show()
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
            put("createdBy", login_name)
            put("location", location_name)
            put("attribute3",gateNo)
            put("attribute4",gateType)
//            if(attribute5.isEmpty()) {
//                put("attribute5", "-")
//            } else {
//                put("attribute5", attribute5)
//            }
            if(remarks.isEmpty()) {
                put("remarks", "-")
            } else {
                put("remarks", remarks)
            }
            put("authorisedBy", login_name)
            put("updatedBy", login_name)
            if (::custName.isInitialized) {
                if(custName.isNotEmpty() ) {
                    put("attribute1", custName)
                }
            }
//            if (::attribute5.isInitialized) {
//                if(attribute5.isNotEmpty() ) {
//                    put("attribute5", attribute5)
//                } else {
//                    put("attribute5", "-")
//                }
//            }
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
        val reasonCode=reasonCodeLov.selectedItem.toString()

        val adapter = transferLocationLov.adapter
        if (adapter != null && adapter.count > 1) {
            if(reasonCode=="Select Transfer Location"){
                Toast.makeText(this, "Please select Transfer location", Toast.LENGTH_SHORT).show()
                return
            } else if(transferLocationLov.selectedItem.toString()!="Select Transfer Location"){
                attribute5=transferLocationLov.selectedItem.toString()
            }
        } else if (reasonCode=="PARKING"){
            attribute5=parkingEditText.text.toString()
        }
//        else if(reasonCode=="TRIAL"){
//            transferLocationTxtView.visibility=View.GONE
//            transferLocationLov.visibility=View.GONE
//            resetSpinner2()
//        }

        else {
            //
        }

//        if(attribute5=="Select Transfer Location"){
//            Toast.makeText(this, "Please select Transfer location", Toast.LENGTH_SHORT).show()
//            return
//        }

        if(reasonCode=="Select Test Drive Reason"){
                Toast.makeText(this,"Please select the reason of Test Drive.",Toast.LENGTH_SHORT).show()
                return
            }

        val gateNo=gateNoLov.selectedItem.toString()
        if(gateNo=="Select Gate Number"){
            Toast.makeText(this,"Please select the Gate Number.",Toast.LENGTH_SHORT).show()
            return
        }

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
            put("attribute2",reasonCode)
//            put("attribute3",gateNo)
            if (::gateNumber.isInitialized) {
                if(gateNumber.isNotEmpty() ) {
                    put("attribute3", gateNumber)
                }
            }
//            put("attribute4",gateType)
            if (::gateType.isInitialized) {
                if(gateType.isNotEmpty() ) {
                    put("attribute4", gateType)
                }
            }
            if (::attribute5.isInitialized) {
                if(attribute5.isNotEmpty() ) {
                    put("attribute5", attribute5)
                } else {
                    put("attribute5", "-")
                }
            }
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

        val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
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


    private fun vehiclePhysicallyOut() {
        val currentKms=currentKMSField.text.toString()
        val currentKmsInt=currentKms.toInt()

        if(currentKMSField.text.toString().isEmpty()){
            Toast.makeText(this,"Please enter the Current Kilometers",Toast.LENGTH_SHORT).show()
            return
        }

        val url = "${ApiFile.APP_URL}/service/wsVehTdDeliver"
        val json = JSONObject().apply {
            put("updatedBy", login_name)
            put("regNo",regNo)
            put("outKm", currentKmsInt)
            put("testDriveNo",testDriveNo)

        }

        val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
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
                    Toast.makeText(this@WorkShopGatepass2, "Failed to update vehicle", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val responseBody = it.body?.string() ?: ""

                    runOnUiThread {
                        when {
                            responseBody.contains("Out Km Must Be Greater Than Last In Km", ignoreCase = true) -> {
                                Toast.makeText(
                                    this@WorkShopGatepass2,
                                    "Out Km Must Be Greater Than Last In Km",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            it.isSuccessful -> {
                                Toast.makeText(
                                    this@WorkShopGatepass2,
                                    "Vehicle physically out successfully!",
                                    Toast.LENGTH_LONG
                                ).show()
                                resetFieldsAfterPhysicallyOut()
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
//            "DEPT." to jcData.DEPT,
//            "CONTACTNO" to jcData.CONTACTNO,
//            "ERPACCTNO" to jcData.ERPACCTNO,
//            "CUSTNAME" to jcData.CUSTNAME
        )

        Log.d("VIN NUMBER---->",jcData.VIN)

        driverNameField.setText(jcData.SERVICE_ADVISOR)
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
            "REG NO" to jcData.INSTANCE_NUMBER,
            "CHASSIS NO" to jcData.CHASSIS_NO,
            "ENGINE NO" to jcData.ENGINE_NO,
//            "ADDRESS" to jcData.ADDRESS,
//            "REGISTRATION_DATE" to jcData.REGISTRATION_DATE,
//            "EMAIL_ADDRESS" to jcData.EMAIL_ADDRESS,
//            "ACCOUNT_NUMBER" to jcData.ACCOUNT_NUMBER,
//            "DRIVER" to jcData.CUST_NAME,
//            "PRIMARY_PHONE_NUMBER" to jcData.PRIMARY_PHONE_NUMBER
        )

        regNo=jcData.INSTANCE_NUMBER
        engineNo=jcData.ENGINE_NO
        chassisNo=jcData.CHASSIS_NO
        custName=jcData.CUST_NAME
        driverNameField.setText(jcData.CUST_NAME)
        vinNo=jcData.VIN

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
            "REG NO" to jcData.REGNO,
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
            "REG NO" to jcData.REG_NO,
            "JOBCARD NO" to jcData.JOBCARDNO,
            "VIN" to jcData.VIN,
            "CHASSIS NO" to jcData.CHASSIS_NO,
            "ENGINE NO" to jcData.ENGINE_NO,
            "LOCATION" to jcData.LOCATION,
            "TEST DRIVE NO" to jcData.TEST_DRIVE_NO,
            "IN KM" to jcData.IN_KM,
            "IN TIME" to jcData.IN_TIME,
            "REMARKS" to jcData.REMARKS
        )

        Log.d("VIN NUMBER---->",jcData.VIN)

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
//            "DEPT." to jcData.DEPT,
            "LOCATION" to jcData.LOCATION,
//            "CUSTNAME" to jcData.CUSTNAME,
            "TEST DRIVE NO" to jcData.TEST_DRIVE_NO,
            "OUT KM" to jcData.OUT_KM,
            "OUT TIME" to jcData.OUT_TIME,
            "REMARKS" to jcData.REMARKS
        )
        Log.d("VIN NUMBER---->",jcData.VIN)

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


    private fun populateFieldsForPhysicallyOutVehicle(jcData:physicallyOutData) {
        val table = findViewById<TableLayout>(R.id.tableLayout2)
        table.removeAllViews()

        val detailsMap = mutableMapOf(
            "REG NO" to jcData.REG_NO,
            "FIRST IN KM" to jcData.IN_KM,
            "FIRST IN TIME" to jcData.IN_TIME
        )
        regNo=jcData.REG_NO
        testDriveNo=jcData.TEST_DRIVE_NO

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
//        captureVehNumberOut.visibility=View.GONE
        remarksTxt.visibility=View.GONE
        remarksField.visibility=View.GONE
        remarksField.setText("")
//        vehicleOutForTestDrive.visibility=View.GONE
//        vehicleInAfterTestDrive.visibility=View.GONE
//        newVehOutEditText.setText("")
//        newVehOutLL.visibility=View.GONE
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
        attribute5=""
        reasonCodeLov.setSelection(0)
        gateNoLov.setSelection(0)
//        gateTypeLov.setSelection(0)
        gateTypeLov.visibility=View.GONE
        reasonCodeLov.visibility=View.GONE
        gateNoLov.visibility=View.GONE
        reasonCodeTxtView.visibility=View.GONE
        gateNumberTxtView.visibility=View.GONE
        gateTypeTxtView.visibility=View.GONE
        transferLocationTxtView.visibility=View.GONE
        transferLocationLov.visibility=View.GONE
        transferLocationLov.setSelection(0)
        resetSpinner2()
        parkingEditText.setText("")
        parkingEditText.visibility=View.GONE
        parkingEditTextTextView.visibility=View.GONE
        gateNumber=""
        gateType=""
        physicallyOutLLTxt.visibility=View.GONE
        physicallyOutVehEditText.setText("")
        physicallyOutVehicleSave.visibility=View.GONE

    }

    fun resetSpinner2() {
        val adapter2 = transferLocationLov.adapter as? ArrayAdapter<String>
        if (adapter2 != null) {
            adapter2.clear()
            adapter2.addAll(emptyList())
            adapter2.notifyDataSetChanged()
        }
        transferLocationLov.setSelection(0)
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
        val OUT_TIME:String,

        val SERVICE_ADVISOR:String
    )

    data class physicallyOutData(
        val REG_NO:String,
        val IN_KM:String,
        val IN_TIME:String,
        val TEST_DRIVE_NO:String
    )

}



