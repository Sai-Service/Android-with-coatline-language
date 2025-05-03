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
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
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
import java.util.concurrent.Executors
import kotlin.math.max
import kotlin.math.min

class TrueValueStockTaking : AppCompatActivity() {

    private lateinit var scanButton: Button
    private lateinit var saveButton: Button
    private lateinit var VinQr: EditText
    private lateinit var VinDetails: TextView
    private lateinit var variantCdTextView: TextView
    private lateinit var dmsInvNoTextView: TextView
    private lateinit var chassisNumberTextView: TextView
    private lateinit var modelCodeTextView: TextView
    //    private lateinit var engineNumberTextView: TextView
    private lateinit var engine2:TextView
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
    //    private lateinit var VinNumber:EditText
    private lateinit var variant_code:TextView
    private lateinit var Physical_Location:TextView
    private lateinit var qr_result_textview:TextView
    private lateinit var refreshButton:Button
    private lateinit var fetchChassisDataButton:Button
    private lateinit var findByChassis:ImageButton
    private lateinit var chassis_no:EditText
    private  lateinit var findBatchNameFn:Button
    private lateinit var vintypeSpinner:Spinner
    private lateinit var fetchVinData2:ImageButton
    private var selectedBatchName: String? = null
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
    private lateinit var trValChassis: TextView
    private lateinit var trValVin: TextView
    private lateinit var trValVariantDesc: TextView
    private lateinit var trValFuelDesc: TextView
    private lateinit var trValModelDesc: TextView
    private lateinit var trValEngineNo: TextView
    private lateinit var trValVehStatus: TextView
    private lateinit var DRIVER_NAME:TextView
    private lateinit var STOCK_TRF_NO:TextView

    private lateinit var VIN:TextView
    private lateinit var CHASSIS_NUM:TextView
    private lateinit var VARIANT_CD:TextView
    private lateinit var FUEL_DESC:TextView
    private lateinit var MODEL_CD:TextView
    private lateinit var ENGINE_NO:TextView
    private lateinit var LOCATION:TextView

    private lateinit var inStkTfNo:String
    private lateinit var inRegNo:String
    private lateinit var toKmEditText:EditText
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

    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.O)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_true_value_stock_taking)
        scanButton = findViewById(R.id.scan_button)
        save_button = findViewById(R.id.save_button)
        VinQr = findViewById(R.id.vin_qr_edittext)
        VinDetails = findViewById(R.id.VinDetails)
//        variantCdTextView = findViewById(R.id.variant_cd_textview)
//        dmsInvNoTextView = findViewById(R.id.dms_inv_no_textview)
//        chassisNumberTextView = findViewById(R.id.chassis_number_textview)
//        modelCodeTextView = findViewById(R.id.model_code_textview)
        batchEditText = findViewById(R.id.batchEditText)
        multipleBatchNameSpinner = findViewById(R.id.multipleBatchNameSpinner)
        multipleBatchNameSpinner.visibility = View.GONE
        batchName = findViewById(R.id.batchNameLabel)
        addBtn = findViewById(R.id.addBtn)
        usernameText = findViewById(R.id.usernameText)
        locationText = findViewById(R.id.locationText)
        viewReportsButton = findViewById(R.id.viewReportsButton)
        homepage = findViewById(R.id.homepage)
//        variant_code = findViewById(R.id.variant_code)
//        Physical_Location = findViewById(R.id.Physical_Location)
        locId = intent.getIntExtra("locId", 0)
        qr_result_textview = findViewById(R.id.qr_result_textview)
        ouId = intent.getIntExtra("ouId", 0)
        deptName = intent.getStringExtra("deptName") ?: ""
        location_name = intent.getStringExtra("location_name") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""
        VinDetails.visibility = View.GONE
        save_button.visibility = View.GONE
//        engine2 = findViewById(R.id.engine2)
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

        trValVin=findViewById(R.id.trValVin)
        trValChassis=findViewById(R.id.trValChassis)
        trValEngineNo=findViewById(R.id.trValEngineNo)
        trValVehStatus=findViewById(R.id.trValVehStatus)
        trValFuelDesc=findViewById(R.id.trValFuelDesc)
        trValModelDesc=findViewById(R.id.trValModelDesc)
        trValVariantDesc=findViewById(R.id.trValVariantDesc)
        DRIVER_NAME=findViewById(R.id.DRIVER_NAME)
        STOCK_TRF_NO=findViewById(R.id.STOCK_TRF_NO)
        captureVinNo=findViewById(R.id.captureVinNo)
        enterVehNo=findViewById(R.id.enterVehNo)
        captureVehNo=findViewById(R.id.captureVehNo)
        vehNoEditText=findViewById(R.id.vehNoEditText)
        fetchVehNoData=findViewById(R.id.fetchVehNoData)
        VIN = findViewById(R.id.VIN)
        VARIANT_CD = findViewById(R.id.VARIANT_CD)
        FUEL_DESC = findViewById(R.id.FUEL_DESC)
        MODEL_CD = findViewById(R.id.MODEL_CD)
        LOCATION=findViewById(R.id.LOCATION)
        CHASSIS_NUM=findViewById(R.id.CHASSIS_NUM)
        ENGINE_NO=findViewById(R.id.ENGINE_NO)


        fetchVinNoTextView=findViewById(R.id.fetchVinNoTextView)
        fetchVehNoTextView=findViewById(R.id.fetchVehNoTextView)

        chassis_no.visibility = View.GONE
        findByChassis.visibility = View.GONE


        findBybatchNameOpenStatus()
        findBybatchNameStatus()



        CHASSIS_NUM.visibility = View.GONE
        VIN.visibility = View.GONE
        VARIANT_CD.visibility = View.GONE
        FUEL_DESC.visibility = View.GONE
        MODEL_CD.visibility = View.GONE
        ENGINE_NO.visibility = View.GONE
        LOCATION.visibility=View.GONE
        DRIVER_NAME.visibility=View.GONE
        STOCK_TRF_NO.visibility=View.GONE
        trValChassis.visibility=View.GONE
        trValVin.visibility=View.GONE
        trValFuelDesc.visibility=View.GONE
        trValModelDesc.visibility=View.GONE
        trValVehStatus.visibility=View.GONE
        trValEngineNo.visibility=View.GONE
        trValVariantDesc.visibility=View.GONE



        val currentDate: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val currentMonth: String = SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(Date())


        usernameText.text = "$login_name"
        locationText.text = "$location_name"

        batchEditText.setText("$location_name-$currentDate")

        batchEditText.isEnabled = false

        addBtn.setOnClickListener {
            scanButton.visibility = View.VISIBLE
            VinDetails.visibility = View.VISIBLE
            save_button.visibility = View.VISIBLE
        }

        viewReportsButton.setOnClickListener {
            stockReports()
        }

        fetchVehNoTextView.setOnClickListener {
            fetchRegNoDataByCamera()
        }

        homepage.setOnClickListener {
            backToHome()
        }

        refreshButton = findViewById(R.id.refreshButton)
        refreshButton.setOnClickListener {
            resetFields()
        }

        findByChassis.setOnClickListener {
            fetchChassisData()
        }

        fetchVehNoData.setOnClickListener {
            fetchRegNoData()
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
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
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
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),
                   CAMERA_PERMISSION_CODE
                )
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
            val selectedBatchName = multipleBatchNameSpinner.selectedItem?.toString()
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

        fetchBatchData()

        fetchVinData2.setOnClickListener {
            val vin = vintypeSpinner.selectedItem.toString()
            fetchVinData2(vin)
//            findBybatchNameStatus()
        }

        save_button.setOnClickListener {
            val regNo=LOCATION.text.toString().split(": ")[1]
            val selectedBatchName = multipleBatchNameSpinner.selectedItem?.toString() ?: ""
            val batchName = batchEditText.text.toString()
            when {
                selectedBatchName == "Select Batch Name" && batchName.isEmpty()-> {
                    Toast.makeText(this,"Please select a Batch Name first", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
                selectedBatchName.isNotEmpty() && selectedBatchName != "Select Batch Name"-> {
                    if (regNo.isNotEmpty()) {
                        saveVinData(regNo, selectedBatchName)
                    } else {
                        Toast.makeText(this, "Vehicle no is empty", Toast.LENGTH_SHORT).show()
                    }
                }

                else -> {
//                    val batchName = batchEditText.text.toString()
                    if (regNo.isNotEmpty()) {
                        saveVinData(regNo, batchName)
                    } else {
                        Toast.makeText(this, "Vehicle or Batch Name is empty", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

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
                        fetchVinData(vin)
                    }
                }
            }
            CAMERA_REQUEST_CODE,CAMERA_REQUEST_CODE_2 -> {
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
//
    private fun displayBestResult(results: List<String>, resultTextView: TextView) {
//        bestResult2 = results.maxByOrNull { it.length } ?: ""
        bestResult2 = results.maxByOrNull { it.length }?.replace(" ", "") ?: ""
        runOnUiThread {
            resultTextView.text = bestResult2
            vehNoTextView.text = bestResult2
            vinNoTextView.text=bestResult2
            Toast.makeText(this, "Text recognized: $bestResult2", Toast.LENGTH_SHORT).show()
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

    private fun findBybatchNameOpenStatus() {
        val LocationName = "$location_name"
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.APP_URL}/tvAccounts/findTvBatchNameStatus?location=$LocationName")
            .build()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                val jsonObject = JSONObject(jsonData.toString())
                val jsonArray = jsonObject.getJSONArray("obj")
                val batStatus = jsonArray.getJSONObject(0)
                val status = batStatus.getString("BATCHSTATUS")
                runOnUiThread {
                    if (status.equals("open", ignoreCase = true)) {
                        multipleBatchNameSpinner.visibility = View.VISIBLE
                        batchEditText.visibility = View.INVISIBLE
                        fetchBatchData()
                    } else if (status.equals("Closed", ignoreCase = true)) {
                        findBybatchNameStatus()
                        save_button.visibility = View.INVISIBLE
                    }
                    else if (jsonArray.length()==0){
                        multipleBatchNameSpinner.visibility = View.INVISIBLE
                        batchEditText.visibility = View.VISIBLE
                        fetchBatchData()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    //
                }
            }
        }
    }

    private fun findBybatchNameStatus() {
        val currentDate: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val batchName = ("$location_name-$currentDate")
        val LocationName = "$location_name"
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.APP_URL}/tvAccounts/findBatchNameStatus?batchName=$batchName&location=$LocationName")
            .build()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()

                val jsonObject = JSONObject(jsonData.toString())
                val jsonArray = jsonObject.getJSONArray("obj")
                val batStatus = jsonArray.getJSONObject(0)
                val batchName = batStatus.getString("batchName")
                val batchStatus = batStatus.getString("batchStatus")

                if (jsonArray.length() == 0){
                    batchEditText.visibility=View.VISIBLE
                    multipleBatchNameSpinner.visibility= View.INVISIBLE
                } else{
                    batchEditText.visibility= View.GONE
                    multipleBatchNameSpinner.visibility=View.VISIBLE
                    fetchBatchData()
                }
                if(batchName==batchEditText.text.toString()&&batchStatus.toString()=="Closed"){
                    save_button.visibility=View.INVISIBLE
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun fetchBatchData() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.APP_URL}/tvAccounts/tvBatchNameOpen?location=${location_name}")
            .build()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val batchCodeList = parseCities(it)
                    runOnUiThread {
                        val adapter = ArrayAdapter(
                            this@TrueValueStockTaking,
                            android.R.layout.simple_spinner_item,
                            batchCodeList
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        multipleBatchNameSpinner.adapter = adapter
                        if (batchCodeList.size > 1) {
                            runOnUiThread {
                                multipleBatchNameSpinner.visibility = View.VISIBLE
                                batchEditText.visibility = View.GONE
                            }
                        } else {
                            runOnUiThread {
                                multipleBatchNameSpinner.visibility = View.GONE
                                batchEditText.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun parseCities(jsonData: String): List<String> {
        val batchCodeList = mutableListOf<String>()
        try {
            val jsonObject = JSONObject(jsonData)
            val jsonArray = jsonObject.getJSONArray("obj")
            batchCodeList.add("Select Batch Name")
            for (i in 0 until jsonArray.length()) {
                val batchList = jsonArray.getJSONObject(i)
                val code = batchList.getString("BATCHNAME")
                batchCodeList.add("$code")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return batchCodeList
    }


    private fun resetFields() {
        chassis_no.setText("")
        vintypeSpinner.visibility=View.GONE
        qr_result_textview.text=""
        VinDetails.visibility=View.INVISIBLE
        VinDetails.text=""
        findByChassis.visibility=View.INVISIBLE
        chassis_no.visibility=View.GONE
        resetVintypeSpinner()
        save_button.visibility=View.INVISIBLE
        vintypeSpinner.setSelection(0)
        fetchVinData2.visibility=View.INVISIBLE


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
        DRIVER_NAME.visibility=View.GONE
        DRIVER_NAME.text=""
        STOCK_TRF_NO.visibility=View.GONE
        STOCK_TRF_NO.text=""
        CHASSIS_NUM.visibility = View.GONE
        CHASSIS_NUM.text=""
        VIN.visibility = View.GONE
        VIN.text=""
        VARIANT_CD.visibility = View.GONE
        VARIANT_CD.text=""
        FUEL_DESC.visibility = View.GONE
        FUEL_DESC.text=""
        MODEL_CD.visibility = View.GONE
        MODEL_CD.text=""
        ENGINE_NO.visibility = View.GONE
        ENGINE_NO.text=""
        LOCATION.visibility=View.GONE
        LOCATION.text=""
        trValChassis.visibility=View.GONE
        trValChassis.text=""
        trValVin.visibility=View.GONE
        trValVin.text=""
        trValFuelDesc.visibility=View.GONE
        trValFuelDesc.text=""
        trValModelDesc.visibility=View.GONE
        trValModelDesc.text=""
        trValVehStatus.visibility=View.GONE
        trValVehStatus.text=""
        trValEngineNo.visibility=View.GONE
        trValEngineNo.text=""
        trValVariantDesc.visibility=View.GONE
        trValVariantDesc.text=""
    }

    private fun fetchChassisData() {
        val client = OkHttpClient()
        val chassis=chassis_no.text.toString()
        val url =ApiFile.APP_URL+"/tvAccounts/tvAccDetailsByChassisNo?chassisNo=$chassis&ouId=$ouId"


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

                    val RegData =regNoDataStkTake(
                        CHASSIS_NO = stockItem.getString("CHASSIS_NO"),
                        VARIANT_DESC = stockItem.getString("VARIANT_DESC"),
                        MODEL_DESC = stockItem.getString("MODEL_DESC"),
                        VIN = stockItem.getString("VIN"),
                        ENGINE_NO = stockItem.getString("ENGINE_NO"),
                        VARIANT_CODE = stockItem.getString("VARIANT_CODE"),
                        VEH_STATUS = stockItem.getString("VEH_STATUS"),
                        REG_NO = stockItem.getString("REG_NO"),
                        LOCATION = stockItem.getString("LOCATION"),
                        OPERATING_UNIT = stockItem.getInt("OPERATING_UNIT"),
                        STATUS = stockItem.getString("STATUS"),
                        FUEL_DESC=stockItem.getString("FUEL_DESC"),
                        COLOUR=stockItem.getString("COLOUR")

                    )
                    runOnUiThread {
                            populateFields(RegData)
                            qr_result_textview.visibility = View.VISIBLE
                            VinDetails.visibility = View.VISIBLE
                            refreshButton.visibility = View.VISIBLE
                            Toast.makeText(
                                this@TrueValueStockTaking,
                                "Details found Successfully \n for chassis: $chassis",
                                Toast.LENGTH_LONG
                            ).show()
                        VinDetails.visibility = View.VISIBLE
                        save_button.visibility = View.VISIBLE
                        CHASSIS_NUM.visibility = View.GONE
                        VIN.visibility = View.GONE
                        VARIANT_CD.visibility = View.GONE
                        FUEL_DESC.visibility = View.GONE
                        MODEL_CD.visibility = View.GONE
                        ENGINE_NO.visibility = View.GONE
                        LOCATION.visibility = View.VISIBLE
                        DRIVER_NAME.visibility = View.GONE
                        STOCK_TRF_NO.visibility = View.GONE
                        trValChassis.visibility = View.VISIBLE
                        trValVin.visibility = View.VISIBLE
                        trValFuelDesc.visibility = View.GONE
                        trValModelDesc.visibility = View.VISIBLE
                        trValVehStatus.visibility = View.VISIBLE
                        trValEngineNo.visibility = View.VISIBLE
                        trValVariantDesc.visibility = View.VISIBLE

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@TrueValueStockTaking,
                        "Failed to fetch details for chassis: $chassis",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun fetchRegNoDataByCamera() {
        val client = OkHttpClient()
//        val vehicleNo2=vehNoTextView.toString()
        val url =ApiFile.APP_URL+"/tvAccounts/tvAccDetailsByRegNo?regNo=$bestResult2"

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

                    val RegData = regNoDataStkTake(
                        CHASSIS_NO = stockItem.getString("CHASSIS_NO"),
                        VARIANT_DESC = stockItem.getString("VARIANT_DESC"),
                        MODEL_DESC = stockItem.getString("MODEL_DESC"),
                        VIN = stockItem.getString("VIN"),
                        ENGINE_NO = stockItem.getString("ENGINE_NO"),
                        VARIANT_CODE = stockItem.getString("VARIANT_CODE"),
                        VEH_STATUS = stockItem.getString("VEH_STATUS"),
                        REG_NO = stockItem.getString("REG_NO"),
                        LOCATION = stockItem.getString("LOCATION"),
                        OPERATING_UNIT = stockItem.getInt("OPERATING_UNIT"),
                        STATUS = stockItem.getString("STATUS"),
                        FUEL_DESC=stockItem.getString("FUEL_DESC"),
                        COLOUR=stockItem.getString("COLOUR")

                    )
                    runOnUiThread {
                            populateFields(RegData)
                            qr_result_textview.visibility = View.VISIBLE
                            VinDetails.visibility = View.VISIBLE
                            refreshButton.visibility = View.VISIBLE
                            Toast.makeText(
                                this@TrueValueStockTaking,
                                "Details found Successfully \n for vehicle no.: $bestResult2",
                                Toast.LENGTH_LONG
                            ).show()
                            VinDetails.visibility = View.VISIBLE
                            save_button.visibility = View.VISIBLE
                            CHASSIS_NUM.visibility = View.GONE
                            VIN.visibility = View.GONE
                            VARIANT_CD.visibility = View.GONE
                            FUEL_DESC.visibility = View.GONE
                            MODEL_CD.visibility = View.GONE
                            ENGINE_NO.visibility = View.GONE
                            LOCATION.visibility = View.VISIBLE
                            DRIVER_NAME.visibility = View.GONE
                            STOCK_TRF_NO.visibility = View.GONE
                            trValChassis.visibility = View.VISIBLE
                            trValVin.visibility = View.VISIBLE
                        trValFuelDesc.visibility = View.GONE
                        trValModelDesc.visibility = View.VISIBLE
                            trValVehStatus.visibility = View.VISIBLE
                            trValEngineNo.visibility = View.VISIBLE
                            trValVariantDesc.visibility = View.VISIBLE

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@TrueValueStockTaking,
                        "Failed to fetch details for vehicle no.: $bestResult2",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun fetchRegNoData() {
        val client = OkHttpClient()
        val vehicleNo=vehNoEditText.text.toString()
        val url =ApiFile.APP_URL+"/tvAccounts/tvAccDetailsByRegNo?regNo=$vehicleNo"

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

                    val RegData = regNoDataStkTake(
                        CHASSIS_NO = stockItem.getString("CHASSIS_NO"),
                        VARIANT_DESC = stockItem.getString("VARIANT_DESC"),
                        MODEL_DESC = stockItem.getString("MODEL_DESC"),
                        VIN = stockItem.getString("VIN"),
                        ENGINE_NO = stockItem.getString("ENGINE_NO"),
                        VARIANT_CODE = stockItem.getString("VARIANT_CODE"),
                        VEH_STATUS = stockItem.getString("VEH_STATUS"),
                        REG_NO = stockItem.getString("REG_NO"),
                        LOCATION = stockItem.getString("LOCATION"),
                        OPERATING_UNIT = stockItem.getInt("OPERATING_UNIT"),
                        STATUS = stockItem.getString("STATUS"),
                        FUEL_DESC=stockItem.getString("FUEL_DESC"),
                        COLOUR=stockItem.getString("COLOUR")
                    )
                    runOnUiThread {
                            populateFields(RegData)
                            qr_result_textview.visibility = View.VISIBLE
                            VinDetails.visibility = View.VISIBLE
                            refreshButton.visibility = View.VISIBLE
                            Toast.makeText(
                                this@TrueValueStockTaking,
                                "Details found Successfully \n for vehicle no.: $vehicleNo",
                                Toast.LENGTH_LONG
                            ).show()
                        VinDetails.visibility = View.VISIBLE
                        save_button.visibility = View.VISIBLE
                        CHASSIS_NUM.visibility = View.GONE
                        VIN.visibility = View.GONE
                        VARIANT_CD.visibility = View.GONE
                        FUEL_DESC.visibility = View.GONE
                        MODEL_CD.visibility = View.GONE
                        ENGINE_NO.visibility = View.GONE
                        LOCATION.visibility = View.VISIBLE
                        DRIVER_NAME.visibility = View.GONE
                        STOCK_TRF_NO.visibility = View.GONE
                        trValChassis.visibility = View.VISIBLE
                        trValVin.visibility = View.VISIBLE
                        trValFuelDesc.visibility = View.GONE
                        trValModelDesc.visibility = View.VISIBLE
                        trValVehStatus.visibility = View.VISIBLE
                        trValEngineNo.visibility = View.VISIBLE
                        trValVariantDesc.visibility = View.VISIBLE

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@TrueValueStockTaking,
                        "Failed to fetch details for vehicle no.: $vehicleNo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun resetVintypeSpinner() {
        val vintypeItems = listOf("Select Batch Name")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, vintypeItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        vintypeSpinner.adapter = adapter
        vintypeSpinner.setSelection(0)
    }

    private fun parseVindata(objArray: String): List<String> {
        val parseVindataList = mutableListOf<String>()
        try {
            val jsonObject = JSONObject(objArray)
            val jsonArray = jsonObject.getJSONArray("obj")
            parseVindataList.add("Select Vin")
            for (i in 0 until jsonArray.length()) {
                val parseVindataLst = jsonArray.getJSONObject(i)
                val vin = parseVindataLst.getString("VIN")
                parseVindataList.add(vin)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return parseVindataList
    }


    private fun fetchVinData(vin: String) {
        val client = OkHttpClient()
        val url =ApiFile.APP_URL+"/qrcode/detailsByVin?vin=$vin"
//        val url =ApiFile.APP_URL+"/qrcode/detailsByVin?vin=MBJTYKK1SRE122674"

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

                    val vinData = vinData(
                        FUEL_DESC = stockItem.getString("FUEL_DESC"),
                        COLOUR = stockItem.getString("COLOUR"),
                        CHASSIS_NUM = stockItem.getString("CHASSIS_NUM"),
                        MODEL_CD = stockItem.getString("MODEL_CD"),
                        VIN = stockItem.getString("VIN"),
                        VARIANT_CD=stockItem.getString("VARIANT_CD"),
                        LOCATION  =stockItem.getString("LOCATION"),
                        ENGINE_NO=stockItem.getString("ENGINE_NO")
                    )
                    runOnUiThread {
//                        populateFields(vinData)
                        qr_result_textview.visibility=View.VISIBLE
                        VinDetails.visibility = View.VISIBLE
                        refreshButton.visibility=View.VISIBLE
                        Toast.makeText(
                            this@TrueValueStockTaking,
                            "Details found Successfully \n for VIN: $vin",
                            Toast.LENGTH_LONG
                        ).show()
                        VinDetails.visibility=View.VISIBLE
                        save_button.visibility=View.VISIBLE
                        variantCdTextView.visibility=View.VISIBLE
                        dmsInvNoTextView.visibility=View.VISIBLE
                        chassisNumberTextView.visibility=View.VISIBLE
                        modelCodeTextView.visibility=View.VISIBLE
                        variant_code.visibility=View.VISIBLE
                        Physical_Location.visibility=View.VISIBLE
                        engine2.visibility=View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@TrueValueStockTaking,
                        "Failed to fetch details for VIN: $vin",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun fetchVinData2(vin: String) {
        val client = OkHttpClient()
        val vin2=vintypeSpinner.selectedItem.toString()
//        val url = ApiFile.APP_URL+"/accounts/vehDetailsByVin?vin=MBJTYKL1SRE240101"
        val url =ApiFile.APP_URL+"/qrcode/detailsByVin?vin=$vin2"
//        val url =ApiFile.APP_URL+"/qrcode/detailsByVin?vin=MBJTYKK1SRE122674"
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

                    val vinData = vinData(
                        FUEL_DESC = stockItem.getString("FUEL_DESC"),
                        COLOUR = stockItem.getString("COLOUR"),
                        CHASSIS_NUM = stockItem.getString("CHASSIS_NUM"),
                        MODEL_CD = stockItem.getString("MODEL_CD"),
                        VIN = stockItem.getString("VIN"),
                        VARIANT_CD=stockItem.getString("VARIANT_CD"),
                        LOCATION  =stockItem.getString("LOCATION"),
                        ENGINE_NO=stockItem.getString("ENGINE_NO")
                    )


                    runOnUiThread {
//                        populateFields(vinData)
                        VinDetails.visibility = View.VISIBLE
                        refreshButton.visibility=View.VISIBLE
                        Toast.makeText(
                            this@TrueValueStockTaking,
                            "Details found Successfully \n for VIN: $vin",
                            Toast.LENGTH_LONG
                        ).show()
                        VinDetails.visibility=View.VISIBLE
                        save_button.visibility=View.VISIBLE
                        variantCdTextView.visibility=View.VISIBLE
                        dmsInvNoTextView.visibility=View.VISIBLE
                        chassisNumberTextView.visibility=View.VISIBLE
                        modelCodeTextView.visibility=View.VISIBLE
                        variant_code.visibility=View.VISIBLE
                        Physical_Location.visibility=View.VISIBLE
                        engine2.visibility=View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@TrueValueStockTaking,
                        "Failed to fetch details for VIN: $vin",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun stockReports() {
        val intent = Intent(this, TrueValueStockReports::class.java)
        intent.putExtra("login_name", login_name)
        intent.putExtra("locId", locId)
        startActivity(intent)
    }

    private fun populateFields(RegData:regNoDataStkTake) {
        LOCATION.text="VEHICLE NO.: ${RegData.REG_NO}"
        trValChassis.text = "CHASSIS NO.: ${RegData.CHASSIS_NO}"
        trValVin.text = "VIN: ${RegData.VIN}"
        trValVariantDesc.text = "FUEL DESC: ${RegData.FUEL_DESC}"
        trValModelDesc.text="MODEL DESC: ${RegData.MODEL_DESC}"
        trValEngineNo.text="ENGINE NO: ${RegData.ENGINE_NO}"
        trValVehStatus.text="COLOUR: ${RegData.COLOUR}"

    }

    private fun saveVinData(regNo: String, batchName: String) {

        val client = OkHttpClient()
        val url = ApiFile.APP_URL + "/tvAccounts/tvBatchName"
        val jsonObject = JSONObject()

        val currentDateTime = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        val formattedDate = formatter.format(currentDateTime.time)

        currentDateTime.add(Calendar.DAY_OF_MONTH, 1)
        val formatterForEndDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        val batchCodeEndDate = formatterForEndDate.format(currentDateTime.time)

        jsonObject.put("regNo", regNo)
        jsonObject.put("chassis_no", trValChassis.text.toString().split(": ")[1])
        jsonObject.put("vin", trValVin.text.toString().split(": ")[1])
        jsonObject.put("fuel_desc", trValVariantDesc.text.toString().split(": ")[1])
        jsonObject.put("model_desc", trValModelDesc.text.toString().split(": ")[1])
        jsonObject.put("engin_no", trValEngineNo.text.toString().split(": ")[1])
        jsonObject.put("colour", trValVehStatus.text.toString().split(": ")[1])
        jsonObject.put("batchName", batchName)
        jsonObject.put("locationId", locId)
        jsonObject.put("locationName", locationText.text.toString())
        jsonObject.put("createdBy", login_name)
        jsonObject.put("batchStatus", "open")
//        jsonObject.put("creationDate", formattedDate)
        jsonObject.put("batchCreationDate", formattedDate)
        jsonObject.put("batchCodeEndDate", batchCodeEndDate)
        jsonObject.put("updatedBy", login_name)
//        jsonObject.put("updationDate", formattedDate)

        val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseCode = response.code
                val responseBody = response.body?.string()

                runOnUiThread {
                    if (responseBody != null) {
                        val jsonObject = JSONObject(responseBody)
                        val message = jsonObject.optString("message", "")

                        when {
                            message.contains("Registration No already exists for this batchName", ignoreCase = true) -> {
                                Toast.makeText(
                                    this@TrueValueStockTaking,
                                    "Stock taking is already completed for this vehicle",
                                    Toast.LENGTH_LONG
                                ).show()
                                clearFields()
                            }
                            responseCode == 200 -> {
                                Toast.makeText(
                                    this@TrueValueStockTaking,
                                    "Data saved successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                                clearFields()
                            }
                            else -> {
                                Toast.makeText(
                                    this@TrueValueStockTaking,
                                    "Failed to save data. Error code: $responseCode",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@TrueValueStockTaking,
                            "No response from server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@TrueValueStockTaking,
                        "Error saving data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun clearFields() {
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
        DRIVER_NAME.visibility=View.GONE
        DRIVER_NAME.text=""
        STOCK_TRF_NO.visibility=View.GONE
        STOCK_TRF_NO.text=""
        CHASSIS_NUM.visibility = View.GONE
        CHASSIS_NUM.text=""
        VIN.visibility = View.GONE
        VIN.text=""
        VARIANT_CD.visibility = View.GONE
        VARIANT_CD.text=""
        FUEL_DESC.visibility = View.GONE
        FUEL_DESC.text=""
        MODEL_CD.visibility = View.GONE
        MODEL_CD.text=""
        ENGINE_NO.visibility = View.GONE
        ENGINE_NO.text=""
        LOCATION.visibility=View.GONE
        LOCATION.text=""
        trValChassis.visibility=View.GONE
        trValChassis.text=""
        trValVin.visibility=View.GONE
        trValVin.text=""
        trValFuelDesc.visibility=View.GONE
        trValFuelDesc.text=""
        trValModelDesc.visibility=View.GONE
        trValModelDesc.text=""
        trValVehStatus.visibility=View.GONE
        trValVehStatus.text=""
        trValEngineNo.visibility=View.GONE
        trValEngineNo.text=""
        trValVariantDesc.visibility=View.GONE
        trValVariantDesc.text=""
        save_button.visibility=View.INVISIBLE
    }

    private fun backToHome() {
        finish()
    }

    data class chassis_data(
        val VIN: String,
        val LOCATION:String
    )

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

    data class regNoDataStkTake(
        val CHASSIS_NO:String,
        val VEH_STATUS:String,
        val REG_NO:String,
        val VIN:String,
        val MODEL_DESC:String,
        val ENGINE_NO:String,
        val OPERATING_UNIT:Int,
        val VARIANT_DESC:String,
        val VARIANT_CODE:String,
        val STATUS:String,
        val LOCATION:String,
        val FUEL_DESC:String,
        val COLOUR:String
    )
}
