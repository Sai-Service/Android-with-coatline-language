package com.example.apinew

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import org.json.JSONObject
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import okhttp3.MediaType.Companion.toMediaType
import java.util.concurrent.Executors
import kotlin.math.max
import kotlin.math.min

class WorkshopStockTransfer : AppCompatActivity() {

    private lateinit var scanButton: Button
    private lateinit var tableLayout: TableLayout

    private lateinit var saveButton: Button
    private lateinit var VinQr: EditText
    private lateinit var VinDetails: TextView
    private lateinit var batchEditText: EditText
    private  lateinit var multipleBatchNameSpinner: Spinner
    private lateinit var batchName: TextView
    private lateinit var addBtn: Button
    private lateinit var save_button: Button
    private lateinit var viewReportsButton: Button
    private lateinit var login_name: String
    private  lateinit var deptName: String
    private lateinit var  location_name:String
    private lateinit var usernameText:TextView
    private lateinit var locationText:TextView
    private lateinit var homepage:ImageButton
    private var ouId: Int = 0
    private var locId:Int=0
    private lateinit var qr_result_textview:TextView
    private lateinit var refreshButton:Button
    private lateinit var fetchChassisDataButton:Button
    private lateinit var findByChassis:ImageButton
    private lateinit var chassis_no:EditText
    private lateinit var vintypeSpinner:Spinner
    private lateinit var fetchVinData2:ImageButton
    private lateinit var captureVinNo:Button
    private lateinit var enterVehNo:Button
    private lateinit var captureVehNo:Button
    private lateinit var vehNoEditText:EditText
    private lateinit var fetchVehNoData:ImageButton
    private lateinit var vehNoTextView:TextView
    private lateinit var vinNoTextView:TextView
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.Builder().setExecutor(Executors.newSingleThreadExecutor()).build())
    private lateinit var fetchVinNoTextView:ImageButton
    private lateinit var fetchVehNoTextView:ImageButton
    private lateinit var bestResult2:String
    private lateinit var DRIVER_NAME:TextView
    private lateinit var STOCK_TRF_NO:TextView
    private lateinit var inStkTfNo:String
    private lateinit var inRegNo:String
    private lateinit var toKmEditText:EditText
    private lateinit var pendingVehList:Button
    private lateinit var toKmText:TextView
    private lateinit var inFrmKm:String
    private lateinit var inToLoc:String
    private lateinit var inFromLoc:String
    private lateinit var inLocation:String

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        private const val CAMERA_REQUEST_CODE = 101
        private const val CAMERA_REQUEST_CODE_2 = 103
    }

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workshop_stock_transfer)
        toKmEditText=findViewById(R.id.toKmEditText)
        toKmText=findViewById(R.id.toKmText)
        pendingVehList=findViewById(R.id.pendingVehList)

        scanButton = findViewById(R.id.scan_button)
        save_button = findViewById(R.id.save_button)
        VinQr = findViewById(R.id.vin_qr_edittext)
        VinDetails = findViewById(R.id.VinDetails)
        batchEditText = findViewById(R.id.batchEditText)
        multipleBatchNameSpinner = findViewById(R.id.multipleBatchNameSpinner)
        multipleBatchNameSpinner.visibility = View.GONE
        batchName = findViewById(R.id.batchNameLabel)
        addBtn = findViewById(R.id.addBtn)
        usernameText = findViewById(R.id.usernameText)
        locationText = findViewById(R.id.locationText)
        viewReportsButton = findViewById(R.id.viewReportsButton)
        homepage = findViewById(R.id.homepage)
        locId = intent.getIntExtra("locId", 0)
        qr_result_textview = findViewById(R.id.qr_result_textview)
        ouId = intent.getIntExtra("ouId", 0)
        deptName = intent.getStringExtra("deptName") ?: ""
        location_name = intent.getStringExtra("location_name") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""
        VinDetails.visibility = View.GONE
        save_button.visibility = View.GONE
        refreshButton = findViewById(R.id.refreshButton)
        fetchChassisDataButton = findViewById(R.id.fetchChassisDataButton)
        chassis_no = findViewById(R.id.chassis_no)
        findByChassis = findViewById(R.id.findByChassis)
        vintypeSpinner = findViewById(R.id.vintypeSpinner)
        vintypeSpinner.visibility = View.GONE
        fetchVinData2 = findViewById(R.id.fetchVinData2)
        fetchVinData2.visibility = View.GONE
        vehNoTextView=findViewById(R.id.vehNoTextView)
        vinNoTextView=findViewById(R.id.vinNoTextView)

        captureVinNo=findViewById(R.id.captureVinNo)
        enterVehNo=findViewById(R.id.enterVehNo)
        captureVehNo=findViewById(R.id.captureVehNo)
        vehNoEditText=findViewById(R.id.vehNoEditText)
        fetchVehNoData=findViewById(R.id.fetchVehNoData)

        val tableLayout = findViewById<TableLayout>(R.id.tableLayout2)
        tableLayout.removeAllViews()

        fetchVinNoTextView=findViewById(R.id.fetchVinNoTextView)
        fetchVehNoTextView=findViewById(R.id.fetchVehNoTextView)

        fetchVehNoTextView.setOnClickListener {
            VinDetails.text="Details By Vehicle Number"
            fetchRegNoDataByCamera()
        }

        fetchVehNoData.setOnClickListener {
            fetchRegNoData()
            VinDetails.text="Details by Vehicle Number"
        }

        fetchVinNoTextView.setOnClickListener {
            fetchVinNoDataByCamera()
        }

        captureVinNo.setOnClickListener {
            vinNoTextView.visibility=View.VISIBLE
            vehNoTextView.visibility=View.GONE
            fetchVinNoTextView.visibility=View.VISIBLE
            refreshButton.visibility=View.VISIBLE
            save_button.visibility=View.INVISIBLE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera(CAMERA_REQUEST_CODE)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
            }
        }

        captureVehNo.setOnClickListener {
            chassis_no.visibility = View.GONE
            findByChassis.visibility = View.GONE
            vehNoEditText.visibility=View.GONE
            fetchVehNoData.visibility=View.GONE
            vehNoTextView.visibility=View.VISIBLE
            fetchVehNoTextView.visibility=View.VISIBLE
            refreshButton.visibility=View.VISIBLE
            save_button.visibility=View.INVISIBLE

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera(CAMERA_REQUEST_CODE_2)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
            }
        }

        enterVehNo.setOnClickListener{
            vehNoEditText.visibility=View.VISIBLE
            fetchVehNoData.visibility=View.VISIBLE
            refreshButton.visibility=View.VISIBLE
            chassis_no.visibility = View.GONE
            findByChassis.visibility = View.GONE
            vehNoTextView.visibility=View.GONE
            fetchVehNoTextView.visibility=View.GONE
            save_button.visibility=View.INVISIBLE

        }

        chassis_no.visibility = View.GONE
        findByChassis.visibility = View.GONE


        toKmEditText.visibility=View.GONE
        toKmText.visibility=View.GONE



        val currentDate: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val currentMonth: String = SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(Date())


        usernameText.text = "$login_name"
        locationText.text = "$location_name"

        batchEditText.setText("$location_name-$currentDate")
//        batchEditText.setText("Borivali(E)-29-07-2024")

        batchEditText.isEnabled = false

        addBtn.setOnClickListener {
            scanButton.visibility = View.VISIBLE
            VinDetails.visibility = View.VISIBLE
            save_button.visibility = View.VISIBLE
        }

        pendingVehList.setOnClickListener {
            stockReports()
        }

        homepage.setOnClickListener {
            backToHome()
        }

        save_button.setOnClickListener {
            postData()
        }

        refreshButton = findViewById(R.id.refreshButton)
        refreshButton.setOnClickListener {
            resetFields()
        }

        findByChassis.setOnClickListener {
            fetchChassisData()
            VinDetails.text="Details By Job Card Number"

        }

        fetchChassisDataButton.setOnClickListener {
            runOnUiThread {
                chassis_no.visibility = View.VISIBLE
                findByChassis.visibility = View.VISIBLE
                vehNoEditText.visibility=View.GONE
                fetchVehNoData.visibility=View.GONE
                vehNoTextView.visibility=View.GONE
                fetchVehNoTextView.visibility=View.GONE
                refreshButton.visibility=View.VISIBLE
                save_button.visibility=View.INVISIBLE
            }
        }

        scanButton.setOnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
            integrator.setPrompt("Scan a QR Code or Barcode")
            integrator.setCameraId(0)
            integrator.setBeepEnabled(true)
            integrator.setBarcodeImageEnabled(true)
            integrator.setOrientationLocked(false)
            integrator.initiateScan()
            Handler(Looper.getMainLooper()).postDelayed({
//                Toast.makeText(this, "Could not scan.Please try again.", Toast.LENGTH_SHORT).show()
            }, 10000)
        }

//        fetchVinData2.setOnClickListener {
//            val vin = vintypeSpinner.selectedItem.toString()
//            fetchVinData2(vin)
//        }

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
                        qr_result_textview.visibility = View.VISIBLE
                        qr_result_textview.text = vin
                        VinQr.setText(vin)
//                        fetchVinData(vin)
                    }
                }
            }
            CAMERA_REQUEST_CODE, CAMERA_REQUEST_CODE_2 -> {
                if (resultCode == Activity.RESULT_OK) {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    when (requestCode) {
                        CAMERA_REQUEST_CODE -> {
                            processImageWithMultipleAttempts(imageBitmap, vehNoTextView)
                        }
                        CAMERA_REQUEST_CODE_2 -> {
                            processImageWithMultipleAttempts(imageBitmap, vinNoTextView)
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

        when {
            regexVehicleNo.matches(modifiedString) -> {
                resultTextView.text = modifiedString
                vehNoTextView.text = modifiedString
                vinNoTextView.text = modifiedString
                Toast.makeText(this, "Text recognized: $modifiedString", Toast.LENGTH_SHORT).show()
            }
            regexVehicleNo2.matches(modifiedString) -> {
                resultTextView.text = modifiedString
                vehNoTextView.text = modifiedString
                vinNoTextView.text = modifiedString
                Toast.makeText(this, "Text recognized: $modifiedString", Toast.LENGTH_SHORT).show()
            }
            regexVehicleNo3.matches(modifiedString) -> {
                resultTextView.text = modifiedString
                vehNoTextView.text = modifiedString
                vinNoTextView.text = modifiedString
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




//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
//        if (result != null) {
//            if (result.contents == null) {
//                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
//            } else {
//                val vin = result.contents
//                qr_result_textview.visibility = View.VISIBLE
//                qr_result_textview.text = vin
//                VinQr.setText(vin)
//                fetchVinData(vin)
////                findBybatchNameStatus()
//            }
//        } else {
//            super.onActivityResult(requestCode, resultCode, data)
//        }
//    }


    private fun resetFields() {
        chassis_no.setText("")
        vintypeSpinner.visibility=View.GONE
        fetchVinNoTextView.visibility=View.GONE
        fetchVehNoTextView.visibility=View.GONE
        qr_result_textview.text=""
        VinDetails.visibility=View.INVISIBLE
        vehNoTextView.text=""
        vehNoTextView.visibility=View.GONE
        vinNoTextView.visibility=View.GONE
        vehNoEditText.visibility=View.GONE
        fetchVehNoData.visibility=View.GONE
        vehNoEditText.setText("")
        vinNoTextView.text=""
        VinDetails.text=""


        val tableLayout = findViewById<TableLayout>(R.id.tableLayout2)
        tableLayout.removeAllViews()

        toKmEditText.setText("")
        toKmEditText.visibility=View.GONE
        toKmText.visibility=View.GONE

        findByChassis.visibility=View.INVISIBLE
        chassis_no.visibility=View.GONE
        save_button.visibility=View.INVISIBLE
        vintypeSpinner.setSelection(0)
        fetchVinData2.visibility=View.INVISIBLE
    }

    private fun fetchChassisData() {
        val client = OkHttpClient()
        val chassis=chassis_no.text.toString()
        val url =ApiFile.APP_URL+"/service/srDetInByJobCardNo?jobCardNo=$chassis"

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

                    val RegData = regNoData(
                        CHASSISNO = stockItem.getString("CHASSISNO"),
                        JOBCARDNO = stockItem.getString("JOBCARDNO"),
                        COLOR = stockItem.getString("COLOR"),
                        VIN = stockItem.getString("VIN"),
                        REGNO = stockItem.getString("REGNO"),
                        PHYSICALLOCATION = stockItem.getString("PHYSICALLOCATION"),
                        FROMKM = stockItem.getInt("FROMKM"),
                        FROM_LOCATION = stockItem.getString("FROM_LOCATION"),
                        TO_LOCATION = stockItem.getString("TO_LOCATION"),
                        DRIVER_NAME = stockItem.getString("DRIVER_NAME"),
                        ENGINNO = stockItem.getString("ENGINNO"),
                        FUEL_DESC = stockItem.getString("FUEL_DESC"),
                        JOBCARDDATE = stockItem.getString("JOBCARDDATE"),
                        MODELDESC = stockItem.getString("MODELDESC"),
                        STOCK_TRF_NO = stockItem.getString("STOCK_TRF_NO"),
                        VARIANT = stockItem.getString("VARIANT"),
                        VEH_STATUS = stockItem.getString("VEH_STATUS")
                    )
                    runOnUiThread {
                        if (RegData.TO_LOCATION!= location_name) {
                            Toast.makeText(
                                this@WorkshopStockTransfer,
                                "Vehicle is not at $location_name",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        if (RegData.VEH_STATUS!="Stock Transfer In-Transit") {
                            Toast.makeText(
                                this@WorkshopStockTransfer,
                                "Vehicle is ${RegData.VEH_STATUS}",
                                Toast.LENGTH_SHORT
                            ).show()
                            save_button.visibility = View.INVISIBLE
                            VinDetails.visibility = View.VISIBLE
                            populateFields3(RegData)
                        }
                        else {
                            populateFields3(RegData)
                            qr_result_textview.visibility = View.VISIBLE
                            VinDetails.visibility = View.VISIBLE
                            refreshButton.visibility = View.VISIBLE
                            Toast.makeText(
                                this@WorkshopStockTransfer,
                                "Details found Successfully \n for Job Card No: $chassis",
                                Toast.LENGTH_LONG
                            ).show()
                            save_button.visibility = View.VISIBLE
                            toKmEditText.visibility = View.VISIBLE
                            toKmText.visibility = View.VISIBLE
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@WorkshopStockTransfer,
                        "Failed to fetch details for Job Card No: $chassis",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun fetchRegNoDataByCamera() {
        val client = OkHttpClient()
        val vehicleNo2=vehNoTextView.text.toString()
        val url =ApiFile.APP_URL+"/service/srDetInByRegNo?regNo=$vehicleNo2"

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

                    val RegData = regNoData(
                        CHASSISNO = stockItem.getString("CHASSISNO"),
                        JOBCARDNO = stockItem.getString("JOBCARDNO"),
                        COLOR = stockItem.getString("COLOR"),
                        VIN = stockItem.getString("VIN"),
                        REGNO = stockItem.getString("REGNO"),
                        PHYSICALLOCATION = stockItem.getString("PHYSICALLOCATION"),
                        FROMKM = stockItem.getInt("FROMKM"),
                        FROM_LOCATION = stockItem.getString("FROM_LOCATION"),
                        TO_LOCATION = stockItem.getString("TO_LOCATION"),
                        DRIVER_NAME = stockItem.getString("DRIVER_NAME"),
                        ENGINNO = stockItem.getString("ENGINNO"),
                        FUEL_DESC = stockItem.getString("FUEL_DESC"),
                        JOBCARDDATE = stockItem.getString("JOBCARDDATE"),
                        MODELDESC = stockItem.getString("MODELDESC"),
                        STOCK_TRF_NO = stockItem.getString("STOCK_TRF_NO"),
                        VARIANT = stockItem.getString("VARIANT"),
                        VEH_STATUS = stockItem.getString("VEH_STATUS")
                    )
                    runOnUiThread {
                        if (RegData.TO_LOCATION!= location_name) {
                            Toast.makeText(
                                this@WorkshopStockTransfer,
                                "Vehicle is not at $location_name",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        if (RegData.VEH_STATUS!="Stock Transfer In-Transit") {
                            Toast.makeText(
                                this@WorkshopStockTransfer,
                                "Vehicle is ${RegData.VEH_STATUS}",
                                Toast.LENGTH_SHORT
                            ).show()
                            save_button.visibility = View.INVISIBLE
                            VinDetails.visibility = View.VISIBLE
                            populateFields3(RegData)

                        }
                        else {
                            populateFields3(RegData)
                            qr_result_textview.visibility = View.VISIBLE
                            VinDetails.visibility = View.VISIBLE
                            refreshButton.visibility = View.VISIBLE
                            Toast.makeText(
                                this@WorkshopStockTransfer,
                                "Details found Successfully \n for vehicle no.: $bestResult2",
                                Toast.LENGTH_LONG
                            ).show()
                            save_button.visibility = View.VISIBLE
                            toKmEditText.visibility = View.VISIBLE
                            toKmText.visibility = View.VISIBLE
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@WorkshopStockTransfer,
                        "Failed to fetch details for vehicle no.: $bestResult2",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun fetchVinNoDataByCamera() {
        val client = OkHttpClient()
        val url =ApiFile.APP_URL+"/trueValue/tvDetailsByVin?vin=$bestResult2"

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

                    val RegData = regNoData(
                        CHASSISNO = stockItem.getString("CHASSISNO"),
                        JOBCARDNO = stockItem.getString("JOBCARDNO"),
                        COLOR = stockItem.getString("COLOR"),
                        VIN = stockItem.getString("VIN"),
                        REGNO = stockItem.getString("REGNO"),
                        PHYSICALLOCATION = stockItem.getString("PHYSICALLOCATION"),
                        FROMKM = stockItem.getInt("FROMKM"),
                        FROM_LOCATION = stockItem.getString("FROM_LOCATION"),
                        TO_LOCATION = stockItem.getString("TO_LOCATION"),
                        DRIVER_NAME = stockItem.getString("DRIVER_NAME"),
                        ENGINNO = stockItem.getString("ENGINNO"),
                        FUEL_DESC = stockItem.getString("FUEL_DESC"),
                        JOBCARDDATE = stockItem.getString("JOBCARDDATE"),
                        MODELDESC = stockItem.getString("MODELDESC"),
                        STOCK_TRF_NO = stockItem.getString("STOCK_TRF_NO"),
                        VARIANT = stockItem.getString("VARIANT"),
                        VEH_STATUS = stockItem.getString("VEH_STATUS")
                    )
                    runOnUiThread {
                        if (RegData.TO_LOCATION!= location_name) {
                            Toast.makeText(
                                this@WorkshopStockTransfer,
                                "Vehicle is not at $location_name",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        if (RegData.VEH_STATUS!="Stock Transfer In-Transit") {
                            Toast.makeText(
                                this@WorkshopStockTransfer,
                                "Vehicle is ${RegData.VEH_STATUS}",
                                Toast.LENGTH_SHORT
                            ).show()
                            save_button.visibility = View.INVISIBLE
                            populateFields3(RegData)
                        }
                        else{
                            populateFields3(RegData)
                            qr_result_textview.visibility = View.VISIBLE
                            VinDetails.visibility = View.VISIBLE
                            refreshButton.visibility = View.VISIBLE
                            Toast.makeText(
                                this@WorkshopStockTransfer,
                                "Details found Successfully \n for VIN: $bestResult2",
                                Toast.LENGTH_LONG
                            ).show()
                            toKmEditText.visibility = View.VISIBLE
                            toKmText.visibility = View.VISIBLE
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@WorkshopStockTransfer,
                        "Failed to fetch details for VIN: $bestResult2",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun fetchRegNoData() {
        val client = OkHttpClient()
        val vehicleNo=vehNoEditText.text.toString()
        val url =ApiFile.APP_URL+"/service/srDetInByRegNo?regNo=$vehicleNo"

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

                    val RegData = regNoData(
                       CHASSISNO = stockItem.getString("CHASSISNO"),
                        JOBCARDNO = stockItem.getString("JOBCARDNO"),
                        COLOR = stockItem.getString("COLOR"),
                        VIN = stockItem.getString("VIN"),
                        REGNO = stockItem.getString("REGNO"),
                        PHYSICALLOCATION = stockItem.getString("PHYSICALLOCATION"),
                        FROMKM = stockItem.getInt("FROMKM"),
                        FROM_LOCATION = stockItem.getString("FROM_LOCATION"),
                        TO_LOCATION = stockItem.getString("TO_LOCATION"),
                        DRIVER_NAME = stockItem.getString("DRIVER_NAME"),
                        ENGINNO = stockItem.getString("ENGINNO"),
                        FUEL_DESC = stockItem.getString("FUEL_DESC"),
                        JOBCARDDATE = stockItem.getString("JOBCARDDATE"),
                        MODELDESC = stockItem.getString("MODELDESC"),
                        STOCK_TRF_NO = stockItem.getString("STOCK_TRF_NO"),
                        VARIANT = stockItem.getString("VARIANT"),
                        VEH_STATUS = stockItem.getString("VEH_STATUS")
                    )
                    runOnUiThread {
                        if (RegData.TO_LOCATION!= location_name) {
                            Toast.makeText(
                                this@WorkshopStockTransfer,
                                "Vehicle is not at $location_name",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else if (RegData.VEH_STATUS!="Stock Transfer In-Transit") {
                            Toast.makeText(
                                this@WorkshopStockTransfer,
                                "Vehicle is ${RegData.VEH_STATUS}",
                                Toast.LENGTH_SHORT
                            ).show()
                            save_button.visibility = View.INVISIBLE
                            VinDetails.visibility = View.VISIBLE
                            populateFields3(RegData)
                        }
                        else {
                            populateFields3(RegData)
                            qr_result_textview.visibility = View.VISIBLE
                            VinDetails.visibility = View.VISIBLE
                            refreshButton.visibility = View.VISIBLE
                            Toast.makeText(
                                this@WorkshopStockTransfer,
                                "Details found Successfully \n for vehicle no.: $vehicleNo",
                                Toast.LENGTH_LONG
                            ).show()
                            save_button.visibility = View.VISIBLE
                            toKmEditText.visibility = View.VISIBLE
                            toKmText.visibility = View.VISIBLE
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@WorkshopStockTransfer,
                        "Failed to fetch details for vehicle no.: $vehicleNo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun postData() {
        val url = "${ApiFile.APP_URL}/service/srUpdateStockIn"
        val toKm=toKmEditText.text.toString()
        val json = JSONObject().apply {
            put("receivedBy", login_name)
            put("regNo",inRegNo)
            put("stockTrfNo",inStkTfNo)
            put("toKm",toKm)
            put("location",location_name)
            put("toLocation",location_name)
            put("updatedBy",login_name)
        }
        if (toKm.isEmpty()) {
            Toast.makeText(this, "To KM is required", Toast.LENGTH_SHORT).show()
            return
        }

        if (toKm.toFloat() <= inFrmKm.toFloat()) {
            Toast.makeText(this, "To KM must be greater than From KM", Toast.LENGTH_SHORT)
                .show()
            return
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
                    Toast.makeText(this@WorkshopStockTransfer, "Failed to update vehicle status", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(this@WorkshopStockTransfer, "Vehicle status updated successfully", Toast.LENGTH_SHORT).show()
                            resetFields()
                        }
                    } else {
                        val responseBody = it.body?.string() ?: ""
                        val errorMessage = if (responseBody.contains("Vehicle details not found")) {
                            "Vehicle details not found"
                        } else {
                            "Unexpected code ${it.code}"
                        }
                        runOnUiThread {
                            Toast.makeText(this@WorkshopStockTransfer, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }

    private fun stockReports() {
        val intent = Intent(this, WorkshopPendingVehicle::class.java)
        intent.putExtra("login_name", login_name)
        intent.putExtra("location_name", location_name)
        intent.putExtra("locId", locId)
        startActivity(intent)
    }

    private fun populateFields3(RegData: regNoData) {
        val table = findViewById<TableLayout>(R.id.tableLayout2)
        table.removeAllViews()
        inRegNo=RegData.REGNO
        inStkTfNo=RegData.STOCK_TRF_NO
        inFrmKm= RegData.FROMKM.toString()

        val detailsMap = mutableMapOf(
            "VEHICLE NO" to RegData.REGNO,
            "JOB CARD NO" to RegData.JOBCARDNO,
            "CHASSIS NO" to RegData.CHASSISNO,
            "VIN NO" to RegData.VIN,
            "VARIANT" to RegData.VARIANT,
            "FUEL DESC" to RegData.FUEL_DESC,
            "MODEL DESC" to RegData.MODELDESC,
            "ENGINE NO" to RegData.ENGINNO,
            "VEH STATUS" to RegData.VEH_STATUS,
            "FROM LOCATION" to RegData.FROM_LOCATION,
            "DRIVER" to RegData.DRIVER_NAME,
            "FROM KM" to RegData.FROMKM,
            "STK TRF NO" to RegData.STOCK_TRF_NO
        )


//        if (vin_data.VEH_STATUS != "Stock Transfer In-Transit") {
//            detailsMap["LOCATION"] = vin_data.LOCATION
//        }

        for ((label, value) in detailsMap) {
            val row = LayoutInflater.from(this).inflate(R.layout.table_row, null) as TableRow
            val labelTextView = row.findViewById<TextView>(R.id.label)
            val valueTextView = row.findViewById<TextView>(R.id.value)

            labelTextView.text = label
            valueTextView.text = value.toString()

            table.addView(row)
        }

        if (table.childCount > 0) {
            val lastRow = table.getChildAt(table.childCount - 1) as TableRow
            lastRow.findViewById<View>(R.id.labelDivider).visibility = View.GONE
            lastRow.findViewById<View>(R.id.valueDivider).visibility = View.GONE
        }
    }


    private fun backToHome() {
        finish()
    }


    data class vinData(
        val VIN:String,
        val FUEL_DESC: String,
        val COLOUR: String,
        val CHASSIS_NUM: String,
        val MODEL_CD: String,
        val VARIANT_CD:String,
        val LOCATION: String,
        val ENGINE_NO:String
    )

    data class regNoData(
        val JOBCARDNO:String,
        val REGNO:String,
        val CHASSISNO:String,
        val VIN:String,
        val MODELDESC:String,
        val ENGINNO:String,
        val COLOR:String,
        val VARIANT:String,
        val FUEL_DESC:String,
        val VEH_STATUS:String,
        val STOCK_TRF_NO:String,
        val FROM_LOCATION:String,
        val TO_LOCATION:String,
        val PHYSICALLOCATION:String,
        val FROMKM:Int,
        val DRIVER_NAME:String,
        val JOBCARDDATE:String


    )
}
