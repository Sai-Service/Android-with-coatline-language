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
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import android.util.Log
import android.view.View
import android.widget.AdapterView
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
import java.io.File
import java.text.ParseException
import java.util.TimeZone
import java.util.concurrent.Executors
import kotlin.math.max
import kotlin.math.min

class TrueValueStockTaking2 : AppCompatActivity() {

    private lateinit var scanButton: Button
    private lateinit var VinQr: EditText
    private lateinit var VinDetails: TextView
    private lateinit var batchEditText: EditText
    private lateinit var multipleBatchNameSpinner: Spinner
    private lateinit var batchName: TextView
    private lateinit var addBtn: Button
    private lateinit var save_button: Button
    private lateinit var viewReportsButton: Button
    private lateinit var login_name: String
    private lateinit var deptName: String
    private lateinit var location_name: String
    private lateinit var usernameText: TextView
    private lateinit var locationText: TextView
    private lateinit var homepage: ImageButton
    private var ouId: Int = 0
    private var locId: Int = 0
    private lateinit var qr_result_textview: TextView
    private lateinit var refreshButton: Button
    private lateinit var fetchChassisDataButton: Button
    private lateinit var findByChassis: ImageButton
    private lateinit var chassis_no: EditText
    private lateinit var vintypeSpinner: Spinner
    private lateinit var fetchVinData2: ImageButton
    private lateinit var captureVinNo: Button
    private lateinit var enterVehNo: Button
    private lateinit var captureVehNo: Button
    private lateinit var vehNoEditText: EditText
    private lateinit var fetchVehNoData: ImageButton
    private lateinit var vehNoTextView: TextView
    private lateinit var vinNoTextView: EditText
    private val recognizer = TextRecognition.getClient(
        TextRecognizerOptions.Builder().setExecutor(
            Executors.newSingleThreadExecutor()
        ).build()
    )
    private lateinit var fetchVinNoTextView: ImageButton
    private lateinit var fetchVehNoTextView: ImageButton
    private lateinit var bestResult2: String
    private lateinit var trValChassis: TextView
    private lateinit var trValVin: TextView
    private lateinit var trValVariantDesc: TextView
    private lateinit var trValFuelDesc: TextView
    private lateinit var trValModelDesc: TextView
    private lateinit var trValEngineNo: TextView
    private lateinit var trValVehStatus: TextView
    private lateinit var DRIVER_NAME: TextView
    private lateinit var STOCK_TRF_NO: TextView

    private lateinit var VIN: TextView
    private lateinit var CHASSIS_NUM: TextView
    private lateinit var VARIANT_CD: TextView
    private lateinit var FUEL_DESC: TextView
    private lateinit var MODEL_CD: TextView
    private lateinit var ENGINE_NO: TextView
    private lateinit var LOCATION: TextView
    private lateinit var save_button2: Button
    private lateinit var checkBatchStatus: String
    private lateinit var cutOffDateSpinner:Spinner
    private lateinit var batchStatus:String
    private lateinit var jsonBatchStatus:String

    private lateinit var scanVinQrButton: Button



    private lateinit var toCutOffDateBatchName:String

    private lateinit var currentPhotoPath: String
    private val REQUEST_IMAGE_CAPTURE_VEH = 200
    private lateinit var currentVinPhotoPath: String


    private var fetchMethod: String = "MANUAL" // default
    private var qrScanPurpose: String = "VIN" // "BATCH" or "VIN"

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        private const val CAMERA_REQUEST_CODE = 101      // VIN camera (unchanged)
        private const val CAMERA_REQUEST_CODE_2 = 103    // kept for reference but replaced below
        private const val REQUEST_VIN_CAPTURE = 300
        private val VIN_SCANNER_REQUEST = 500

        private val VIN_REGEX = Regex("^[A-HJ-NPR-Z0-9]{17}$")
        private val VIN_CHAR_MAP = mapOf('I' to '1', 'O' to '0', 'Q' to '0')
    }

    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.O)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_true_value_stock_taking)
        scanButton = findViewById(R.id.scanVinQrButton)
        save_button = findViewById(R.id.save_button)
        save_button2 = findViewById(R.id.save_button2)
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

        cutOffDateSpinner=findViewById(R.id.cutOffDateSpinner)


        vehNoTextView = findViewById(R.id.vehNoTextView)
        vinNoTextView = findViewById(R.id.vinNoTextView)

        trValVin = findViewById(R.id.trValVin)
        trValChassis = findViewById(R.id.trValChassis)
        trValEngineNo = findViewById(R.id.trValEngineNo)
        trValVehStatus = findViewById(R.id.trValVehStatus)
        trValFuelDesc = findViewById(R.id.trValFuelDesc)
        trValModelDesc = findViewById(R.id.trValModelDesc)
        trValVariantDesc = findViewById(R.id.trValVariantDesc)
        DRIVER_NAME = findViewById(R.id.DRIVER_NAME)
        STOCK_TRF_NO = findViewById(R.id.STOCK_TRF_NO)
        captureVinNo = findViewById(R.id.captureVinNo)
        enterVehNo = findViewById(R.id.enterVehNo)
        captureVehNo = findViewById(R.id.captureVehNo)
        vehNoEditText = findViewById(R.id.vehNoEditText)
        fetchVehNoData = findViewById(R.id.fetchVehNoData)
        VIN = findViewById(R.id.VIN)
        VARIANT_CD = findViewById(R.id.VARIANT_CD)
        FUEL_DESC = findViewById(R.id.FUEL_DESC)
        MODEL_CD = findViewById(R.id.MODEL_CD)
        LOCATION = findViewById(R.id.LOCATION)
        CHASSIS_NUM = findViewById(R.id.CHASSIS_NUM)
        ENGINE_NO = findViewById(R.id.ENGINE_NO)


        fetchVinNoTextView = findViewById(R.id.fetchVinNoTextView)
        fetchVehNoTextView = findViewById(R.id.fetchVehNoTextView)

        chassis_no.visibility = View.GONE
        findByChassis.visibility = View.GONE


        findBybatchNameOpenStatus()

        findByChassis.setOnClickListener {
            fetchChassisData()
            VinDetails.text = "Details By Chassis Number"
        }

        fetchVehNoData.setOnClickListener {
            fetchRegNoData()
            save_button.visibility = View.INVISIBLE
            save_button2.visibility = View.VISIBLE
            VinDetails.text = "Details By Vehicle Number"
        }


        fetchVinNoTextView.setOnClickListener {
            fetchVinNoData()
            VinDetails.text = "Details By Vin Number"
        }

        fetchVehNoTextView.setOnClickListener {
            fetchRegNoDataByCamera()
            VinDetails.text = "Details By Vehicle Number"
        }

        scanVinQrButton = findViewById(R.id.scanVinQrButton)

//        captureVinNo.setOnClickListener {
//            displayBestResult()
//        }

//        captureVinNo.setOnClickListener {
//            vinNoTextView.visibility = View.VISIBLE
//            vehNoTextView.visibility = View.GONE
//            fetchVinNoTextView.visibility = View.VISIBLE
//            refreshButton.visibility = View.VISIBLE
//            save_button.visibility = View.INVISIBLE
//            save_button2.visibility = View.INVISIBLE
//            findByChassis.visibility = View.GONE
//            chassis_no.visibility = View.GONE
//            fetchVehNoTextView.visibility=View.GONE
//
//            if (ContextCompat.checkSelfPermission(
//                    this,
//                    Manifest.permission.CAMERA
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                openCamera(CAMERA_REQUEST_CODE)
//            } else {
//                ActivityCompat.requestPermissions(
//                    this, arrayOf(Manifest.permission.CAMERA),
//                    CAMERA_PERMISSION_CODE
//                )
//            }
//        }

//        captureVinNo.setOnClickListener {
//            vinNoTextView.visibility = View.VISIBLE
//            vehNoTextView.visibility = View.GONE
//            fetchVinNoTextView.visibility = View.VISIBLE
//            refreshButton.visibility = View.VISIBLE
//            save_button.visibility = View.INVISIBLE
//            save_button2.visibility = View.INVISIBLE
//            findByChassis.visibility = View.GONE
//            chassis_no.visibility = View.GONE
//            fetchVehNoTextView.visibility = View.GONE
//
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//                == PackageManager.PERMISSION_GRANTED
//            ) {
//                dispatchVinPictureIntent()   // FIXED: was openCamera(CAMERA_REQUEST_CODE)
//            } else {
//                ActivityCompat.requestPermissions(
//                    this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE
//                )
//            }
//        }


        captureVinNo.setOnClickListener {
            vinNoTextView.visibility = View.VISIBLE
            vehNoTextView.visibility = View.GONE
            fetchVinNoTextView.visibility = View.VISIBLE
            refreshButton.visibility = View.VISIBLE
            save_button.visibility = View.INVISIBLE
            save_button2.visibility = View.INVISIBLE
            findByChassis.visibility = View.GONE
            chassis_no.visibility = View.GONE
            fetchVehNoTextView.visibility = View.GONE

            // Launch live scanner instead of photo capture
            val intent = Intent(this, VinScannerActivity::class.java)
            startActivityForResult(intent, VIN_SCANNER_REQUEST)
        }



        captureVehNo.setOnClickListener {
//            checkBatchCutoff()
            chassis_no.visibility = View.GONE
            findByChassis.visibility = View.GONE
            vehNoEditText.visibility = View.GONE
            fetchVehNoData.visibility = View.GONE
            vehNoTextView.visibility = View.VISIBLE
            fetchVehNoTextView.visibility = View.VISIBLE
            refreshButton.visibility = View.VISIBLE
            save_button.visibility = View.INVISIBLE
            save_button2.visibility = View.INVISIBLE
            vinNoTextView.visibility = View.GONE
            fetchVinNoTextView.visibility = View.GONE

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ) {
                dispatchTakePictureIntent()   // <-- CHANGED from openCamera(CAMERA_REQUEST_CODE_2)
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE
                )
            }
        }

        enterVehNo.setOnClickListener {
//            checkBatchCutoff()
            vehNoEditText.visibility = View.VISIBLE
            fetchVehNoData.visibility = View.VISIBLE
            refreshButton.visibility = View.VISIBLE
            chassis_no.visibility = View.GONE
            findByChassis.visibility = View.GONE
            vehNoTextView.visibility = View.GONE
            fetchVehNoTextView.visibility = View.GONE
            save_button.visibility = View.INVISIBLE
            save_button2.visibility = View.INVISIBLE
            vinNoTextView.visibility = View.GONE
            fetchVinNoTextView.visibility = View.GONE
        }

        fetchChassisDataButton.setOnClickListener {
            runOnUiThread {
                chassis_no.visibility = View.VISIBLE
                findByChassis.visibility = View.VISIBLE
                vehNoEditText.visibility = View.GONE
                fetchVehNoData.visibility = View.GONE
                vehNoTextView.visibility = View.GONE
                fetchVehNoTextView.visibility = View.GONE
                refreshButton.visibility = View.VISIBLE
                save_button.visibility = View.INVISIBLE
                save_button2.visibility = View.INVISIBLE
                vinNoTextView.visibility = View.GONE
                fetchVinNoTextView.visibility = View.GONE
            }
        }

        CHASSIS_NUM.visibility = View.GONE
        VIN.visibility = View.GONE
        VARIANT_CD.visibility = View.GONE
        FUEL_DESC.visibility = View.GONE
        MODEL_CD.visibility = View.GONE
        ENGINE_NO.visibility = View.GONE
        LOCATION.visibility = View.GONE
        DRIVER_NAME.visibility = View.GONE
        STOCK_TRF_NO.visibility = View.GONE
        trValChassis.visibility = View.GONE
        trValVin.visibility = View.GONE
        trValFuelDesc.visibility = View.GONE
        trValModelDesc.visibility = View.GONE
        trValVehStatus.visibility = View.GONE
        trValEngineNo.visibility = View.GONE
        trValVariantDesc.visibility = View.GONE


        val currentDate: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val currentMonth: String = SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(Date())


        usernameText.text = "$login_name"
        locationText.text = "$location_name"

//        batchEditText.setText("$location_name-$currentDate")
//        batchEditText.setText("218-BORIVALI WEST-15-09-2024")

        batchEditText.isEnabled = false

        addBtn.setOnClickListener {
            scanButton.visibility = View.VISIBLE
            VinDetails.visibility = View.VISIBLE
            save_button.visibility = View.VISIBLE
        }

        viewReportsButton.setOnClickListener {
            stockReports()
        }

        homepage.setOnClickListener {
            backToHome()
        }

        refreshButton = findViewById(R.id.refreshButton)
        refreshButton.setOnClickListener {
            resetFields()
        }

        fetchChassisDataButton.setOnClickListener {
            runOnUiThread {
//                checkBatchCutoff()
                chassis_no.visibility = View.VISIBLE
                findByChassis.visibility = View.VISIBLE
            }
        }

//        fetchCutOffDates()

//        Handler(Looper.getMainLooper()).postDelayed({
//            checkBatchCutoff()
//        }, 1000)

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

        save_button.setOnClickListener {
            val regNo = LOCATION.text.toString().split(": ")[1]
            val selectedBatchName = multipleBatchNameSpinner.selectedItem?.toString() ?: ""

            when {
                selectedBatchName == "Select Batch Name" -> {
                    Toast.makeText(this, "Please select a Batch Name first", Toast.LENGTH_SHORT)
                        .show()
                }

                selectedBatchName.isNotEmpty() -> {
                    if (regNo.isNotEmpty()) {
                        saveVinData(regNo, selectedBatchName)
                    } else {
                        Toast.makeText(this, "VIN is empty", Toast.LENGTH_SHORT).show()
                    }
                }

                else -> {
                    val batchName = batchEditText.text.toString()
                    if (regNo.isNotEmpty()) {
                        saveVinData(regNo, batchName)
                    } else {
                        Toast.makeText(this, "VIN or Batch Name is empty", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }


        save_button2.setOnClickListener{
            val regNo = LOCATION.text.toString().split(": ")[1]

            val selectedBatchName = multipleBatchNameSpinner.selectedItem?.toString() ?: ""

            when {
                selectedBatchName == "Select Batch Name" -> {
                    Toast.makeText(this, "Please select a Batch Name first", Toast.LENGTH_SHORT)
                        .show()
                }

                selectedBatchName.isNotEmpty() -> {
                    if (regNo.isNotEmpty()) {
                        saveVinData2(regNo, selectedBatchName)
                    } else {
                        Toast.makeText(this, "VIN is empty", Toast.LENGTH_SHORT).show()
                    }
                }

                else -> {
                    val batchName = batchEditText.text.toString()
                    if (regNo.isNotEmpty()) {
                        saveVinData2(regNo, batchName)
                    } else {
                        Toast.makeText(this, "VIN or Batch Name is empty", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        scanVinQrButton.setOnClickListener {
            qrScanPurpose = "VIN"

            // Reset UI similar to captureVinNo flow
            vinNoTextView.visibility = View.VISIBLE
            vehNoTextView.visibility = View.GONE
            fetchVinNoTextView.visibility = View.VISIBLE
            refreshButton.visibility = View.VISIBLE
            save_button.visibility = View.INVISIBLE
            save_button2.visibility = View.INVISIBLE
            findByChassis.visibility = View.GONE
            chassis_no.visibility = View.GONE
            fetchVehNoTextView.visibility = View.GONE

            val integrator = IntentIntegrator(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
            integrator.setPrompt("Scan VIN QR Code or Barcode")
            integrator.setCameraId(0)
            integrator.setBeepEnabled(true)
            integrator.setBarcodeImageEnabled(true)
            integrator.setOrientationLocked(false)
            integrator.initiateScan()
        }
    }


    private fun dispatchVinPictureIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            val photoFile: File? = try {
                createVinImageFile()
            } catch (ex: IOException) {
                null
            }
            if (photoFile != null) {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    "${applicationContext.packageName}.provider",
                    photoFile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(intent, REQUEST_VIN_CAPTURE)
            } else {
                Toast.makeText(this, "Could not create image file", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun createVinImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "VIN_${timeStamp}_", ".jpg", storageDir
        ).apply { currentVinPhotoPath = absolutePath }
    }


    private fun fetchCutOffDates() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.ERP_APP_URL}/tvAccounts/tvBatchCutofDate?ou=$ouId")
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()

                val jsonObject = JSONObject(jsonData.toString())
                val jsonArray = jsonObject.getJSONArray("obj")
                val batchDates = mutableListOf<String>()

                for (i in 0 until jsonArray.length()) {
                    val batch = jsonArray.getJSONObject(i)
                    val date = batch.getString("BATCH_CREATION_DATE")
                    batchDates.add(date)
                }

                runOnUiThread {
                    val cutOffDateSpinner: Spinner = findViewById(R.id.cutOffDateSpinner)
                    val fetchChassisDataButton: Button = findViewById(R.id.fetchChassisDataButton)
                    val scanButton: Button = findViewById(R.id.scanVinQrButton)

                    if (batchDates.isEmpty()) {
                        Toast.makeText(this@TrueValueStockTaking2, "Stock not uploaded for this Month", Toast.LENGTH_SHORT).show()
                        captureVehNo.isEnabled = false
                        enterVehNo.isEnabled = false
                        fetchChassisDataButton.isEnabled = false
                        scanVinQrButton.isEnabled = false

                        return@runOnUiThread
                    }

                    val spinnerItems = mutableListOf("Select Cut Off Date").apply { addAll(batchDates) }

                    val adapter = ArrayAdapter(
                        this@TrueValueStockTaking2,
                        android.R.layout.simple_spinner_item,
                        spinnerItems
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    cutOffDateSpinner.adapter = adapter

                    fetchChassisDataButton.isEnabled = false
                    scanButton.isEnabled = false

                    cutOffDateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                            if (parent.getItemAtPosition(position) == "Select Cut Off Date") {
                                captureVehNo.isEnabled = false
                                enterVehNo.isEnabled = false
                                captureVinNo.isEnabled = false
                                fetchChassisDataButton.isEnabled = false
                                scanVinQrButton.isEnabled = false
                                batchEditText.setText("$location_name")
                            } else {
                                val spinnerDate=cutOffDateSpinner.selectedItem.toString()
                                captureVehNo.isEnabled = true
                                enterVehNo.isEnabled = true
                                captureVinNo.isEnabled = true
                                scanVinQrButton.isEnabled = true
                                fetchChassisDataButton.isEnabled = true
                                batchEditText.setText("$location_name-$spinnerDate")

                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            captureVehNo.isEnabled = false
                            enterVehNo.isEnabled = false
                            fetchChassisDataButton.isEnabled = false
                            scanVinQrButton.isEnabled = false
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@TrueValueStockTaking2, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun findBybatchNameOpenStatus() {
        val LocationName = "$location_name"
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.ERP_APP_URL}/tvAccounts/findTvBatchNameStatus?location=$LocationName")
            .build()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                val jsonObject = JSONObject(jsonData.toString())
                val jsonArray = jsonObject.getJSONArray("obj")
                jsonBatchStatus=jsonArray.length().toString()

                if (jsonArray.length() == 0) {
                    runOnUiThread {
                        findBybatchNameStatus()
                        fetchCutOffDates()
                        save_button.visibility = View.INVISIBLE
                        save_button2.visibility = View.INVISIBLE
                        cutOffDateSpinner.visibility=View.VISIBLE
                    }
                    return@launch
                }

                val batStatus = jsonArray.getJSONObject(0)
                val status = batStatus.getString("BATCHSTATUS")
                checkBatchStatus = batStatus.getString("BATCHSTATUS")
                batchStatus=status

                runOnUiThread {
                    if (checkBatchStatus.equals("open", ignoreCase = true)) {
                        multipleBatchNameSpinner.visibility = View.VISIBLE
                        batchEditText.visibility = View.INVISIBLE
                        cutOffDateSpinner.visibility=View.GONE
                        fetchBatchData()
                    } else if (checkBatchStatus.equals("Closed", ignoreCase = true)) {
                        findBybatchNameStatus()
                        fetchCutOffDates()
                        save_button.visibility = View.INVISIBLE
                        save_button2.visibility = View.INVISIBLE
                        cutOffDateSpinner.visibility=View.VISIBLE
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
        val LocationName = location_name
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.ERP_APP_URL}/tvAccounts/findBatchNameStatus?batchName=$batchName&location=$LocationName")
            .build()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                val jsonObject = JSONObject(jsonData.toString())
                val jsonArray = jsonObject.getJSONArray("obj")
                val batStatus = jsonArray.getJSONObject(0)
                val batchName2 = batStatus.getString("batchName")
                val batchStatus = batStatus.getString("batchStatus")

                if (jsonArray.length() == 0){
                    batchEditText.visibility=View.VISIBLE
                    multipleBatchNameSpinner.visibility= View.INVISIBLE
                } else{
                    batchEditText.visibility= View.GONE
                    multipleBatchNameSpinner.visibility=View.VISIBLE
                    fetchBatchData()
                }
                if(batchName.toString()==batchEditText.text.toString()&&batchStatus.toString()=="Closed"){
                    save_button.visibility=View.INVISIBLE
                    save_button2.visibility=View.INVISIBLE

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
            .url("${ApiFile.ERP_APP_URL}/tvAccounts/tvBatchNameOpen?location=${location_name}")
            .build()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val batchCodeList = parseCities(it)
                    runOnUiThread {
                        val adapter = ArrayAdapter(
                            this@TrueValueStockTaking2,
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
                toCutOffDateBatchName=code
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return batchCodeList
    }

    private fun openCamera(requestCode: Int) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, requestCode)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {

//            IntentIntegrator.REQUEST_CODE -> {
//                val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
//                if (result != null) {
//                    if (result.contents == null) {
//                        Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
//                    } else {
//                        val vin = result.contents
//                        qr_result_textview.visibility = View.VISIBLE
//                        qr_result_textview.text = vin
//                        VinQr.setText(vin)
//                    }
//                }
//            }

            IntentIntegrator.REQUEST_CODE -> {
                val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
                if (result != null) {
                    if (result.contents == null) {
                        Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
                    } else {
                        val scannedValue = result.contents.trim()

                        if (qrScanPurpose == "VIN") {
                            // Only the VIN is expected in this QR/barcode
                            fetchMethod = "Vin QR"
                            vinNoTextView.setText(scannedValue)
                            vinNoTextView.visibility = View.VISIBLE
                            vinNoTextView.isEnabled = true
                            vinNoTextView.isFocusableInTouchMode = true

                            if (VIN_REGEX.matches(scannedValue) && scannedValue.startsWith("M")) {
                                Toast.makeText(this, "✓ VIN: $scannedValue", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(
                                    this,
                                    "⚠ Scanned value doesn't look like a valid VIN, please verify",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            VinDetails.text = "Details By Vin Number"
                            fetchVinNoData()   // <-- calls tvAccDetailsByVin and populates fields

                            qrScanPurpose = "VINBYQR" // reset for next use
                        } else {
                            // existing batch QR flow — unchanged
                            val vin = scannedValue
                            qr_result_textview.visibility = View.VISIBLE
                            qr_result_textview.text = vin
                            VinQr.setText(vin)
                        }
                    }
                }
            }


            REQUEST_VIN_CAPTURE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val file = File(currentVinPhotoPath)
                    if (file.exists()) {
                        showVinLoadingState()
                        Thread {
                            val bitmap = decodeSampledBitmap(file.absolutePath, 1920, 1080)
                            if (bitmap != null) {
                                processVinImage(bitmap) { vin ->
                                    hideVinLoadingState()
                                    runOnUiThread {
                                        if (vin != null) {
                                            // Show in editable field — user can correct if wrong
                                            vinNoTextView.setText(vin)
                                            vinNoTextView.visibility = View.VISIBLE
                                            vinNoTextView.isEnabled = true
                                            vinNoTextView.isFocusableInTouchMode = true

                                            if (VIN_REGEX.matches(vin) && vin.startsWith("M")) {
                                                Toast.makeText(
                                                    this,
                                                    "✓ VIN: $vin\nTap search icon to fetch details",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    this,
                                                    "⚠ Best guess: $vin\nPlease verify and correct if needed",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        } else {
                                            // Even if null — show empty editable field
                                            vinNoTextView.setText("")
                                            vinNoTextView.visibility = View.VISIBLE
                                            vinNoTextView.isEnabled = true
                                            vinNoTextView.isFocusableInTouchMode = true
                                            Toast.makeText(
                                                this,
                                                "Could not read VIN. Please type manually.",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                }
                            } else {
                                runOnUiThread {
                                    hideVinLoadingState()
                                    Toast.makeText(this, "Could not load image", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }.start()
                    } else {
                        Toast.makeText(this, "Image file not found", Toast.LENGTH_SHORT).show()
                    }
                }
            }

//            VIN_SCANNER_REQUEST -> {
//                if (resultCode == Activity.RESULT_OK) {
//                    val vin = data?.getStringExtra(VinScannerActivity.EXTRA_VIN)
//                    if (vin != null) {
//                        vinNoTextView.setText(vin)
//                        vinNoTextView.visibility = View.VISIBLE
//                        vinNoTextView.isEnabled = true
//                        vinNoTextView.isFocusableInTouchMode = true
//                        if (VIN_REGEX.matches(vin) && vin[0] == 'M') {
//                            Toast.makeText(this, "✓ VIN: $vin", Toast.LENGTH_LONG).show()
//                        } else {
//                            Toast.makeText(this, "⚠ Best guess: $vin — please verify", Toast.LENGTH_LONG).show()
//                        }
//                    }
//                }
//            }


            VIN_SCANNER_REQUEST -> {
                if (resultCode == Activity.RESULT_OK) {
                    val vin = data?.getStringExtra(VinScannerActivity.EXTRA_VIN)
                    if (vin != null) {
                        
                        vinNoTextView.setText(vin)
                        vinNoTextView.visibility = View.VISIBLE
                        vinNoTextView.isEnabled = true
                        vinNoTextView.isFocusableInTouchMode = true
                        if (VIN_REGEX.matches(vin) && vin[0] == 'M') {
                            Toast.makeText(this, "✓ VIN: $vin", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "⚠ Best guess: $vin — please verify", Toast.LENGTH_LONG).show()
                        }
                        VinDetails.text = "Details By Vin Number"
                        fetchVinNoData()   // <-- fetch and populate details, same as QR-scan flow
                    }
                }
            }

            // Vehicle number — now uses full-res file + background decoding (no black screen)
            REQUEST_IMAGE_CAPTURE_VEH -> {
                if (resultCode == Activity.RESULT_OK) {
                    val file = File(currentPhotoPath)
                    if (file.exists()) {
                        showVehLoadingState()
                        Thread {
                            val bitmap = decodeSampledBitmap(file.absolutePath, 1920, 1080)
                            runOnUiThread {
                                if (bitmap != null) {
                                    processVehicleNumberImage(bitmap, vehNoTextView)
                                    hideVehLoadingState()
                                } else {
                                    hideVehLoadingState()
                                    Toast.makeText(this, "Image file not found", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }.start()
                    } else {
                        Toast.makeText(this, "Image file not found", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }


    // ---------- Background bitmap decoding — prevents black screen ----------

    /**
     * Decodes a large image file with downsampling so it never blocks the UI.
     * reqWidth/reqHeight = max dimensions you need — OCR doesn't need 4K images.
     */
    private fun decodeSampledBitmap(filePath: String, reqWidth: Int, reqHeight: Int): Bitmap? {
        return try {
            // First pass — just read dimensions, don't allocate pixels
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(filePath, options)

            // Calculate sample size
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

            // Second pass — decode with sampling
            options.inJustDecodeBounds = false
            BitmapFactory.decodeFile(filePath, options)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while (
                halfHeight / inSampleSize >= reqHeight &&
                halfWidth / inSampleSize >= reqWidth
            ) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

// ---------- Loading state UI ----------

    private fun showVinLoadingState() {
        vinNoTextView.setText("Reading VIN...")
        vinNoTextView.visibility = View.VISIBLE
        fetchVinNoTextView.isEnabled = false
        captureVinNo.isEnabled = false
    }

    private fun hideVinLoadingState() {
        fetchVinNoTextView.isEnabled = true
        captureVinNo.isEnabled = true
    }

    private fun showVehLoadingState() {
        vehNoTextView.text = "Reading plate..."
        vehNoTextView.visibility = View.VISIBLE
        fetchVehNoTextView.isEnabled = false
        captureVehNo.isEnabled = false
    }

    private fun hideVehLoadingState() {
        fetchVehNoTextView.isEnabled = true
        captureVehNo.isEnabled = true
    }

    // ---------- Full-resolution camera dispatch ----------

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                null
            }
            if (photoFile != null) {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    "${applicationContext.packageName}.provider",
                    photoFile
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_VEH)
            } else {
                Toast.makeText(this, "Could not create image file", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", ".jpg", storageDir
        ).apply { currentPhotoPath = absolutePath }
    }

    // ---------- Vehicle Number OCR Pipeline (full pipeline from WorkshopWashingModule) ----------

    private fun processVehicleNumberImage(originalBitmap: Bitmap, resultTextView: TextView) {
        val bitmapsToTry = mutableListOf<Bitmap>()
        bitmapsToTry.add(originalBitmap)
        bitmapsToTry.add(resizeKeepAspect(originalBitmap, 1000))
        bitmapsToTry.add(adaptiveThreshold(resizeKeepAspect(originalBitmap, 1000)))

        val angles = listOf(-6f, -3f, 0f, 3f, 6f)
        val initialList = bitmapsToTry.toList()
        for (bm in initialList) {
            for (ang in angles) {
                bitmapsToTry.add(rotateBitmap(bm, ang))
            }
        }

        var foundValid = false

        fun processNext(index: Int) {
            if (index >= bitmapsToTry.size) {
                if (!foundValid) {
                    runOnUiThread {
                        Toast.makeText(this, "Cant able to read Vehicle Number", Toast.LENGTH_SHORT).show()
                    }
                }
                return
            }
            val image = InputImage.fromBitmap(bitmapsToTry[index], 0)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val candidates = extractPlateCandidates(visionText)
                    for (cand in candidates) {
                        val corrected = correctByPosition(cand)
                        val cleaned = corrected.replace("\\s+".toRegex(), "")
                        val regexVehicleNo  = Regex("^[A-Z]{2}\\d{2}[A-Z]{2}\\d{4}$")
                        val regexVehicleNo2 = Regex("^[A-Z]{2}\\d{2}[A-Z]\\d{4}$")
                        val regexVehicleNo3 = Regex("^[A-Z]{2}\\d{2}[A-Z]{3}\\d{4}$")
                        val regexVehicleNo4 = Regex("^\\d{2}\\s?[A-Z]{2}\\s?\\d{4}\\s?[A-Z]$")

                        if (regexVehicleNo.matches(cleaned) ||
                            regexVehicleNo2.matches(cleaned) ||
                            regexVehicleNo3.matches(cleaned) ||
                            regexVehicleNo4.matches(cleaned)) {

                            runOnUiThread {
                                resultTextView.text = cleaned
                                vehNoTextView.text = cleaned
                                Toast.makeText(this, "Result: $cleaned", Toast.LENGTH_SHORT).show()
                                // Auto-fetch details for the recognized plate, same as tapping the search icon
                                fetchRegNoDataByCamera()
                            }
                            foundValid = true
                            return@addOnSuccessListener
                        }
                    }
                    if (!foundValid) processNext(index + 1)
                }
                .addOnFailureListener {
                    if (!foundValid) processNext(index + 1)
                }
        }
        processNext(0)
    }

// ---------- Helpers ----------

    private fun rotateBitmap(src: Bitmap, angle: Float): Bitmap {
        val matrix = android.graphics.Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
    }

    private fun adaptiveThreshold(bitmap: Bitmap): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        val out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(w * h)
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h)
        val gray = IntArray(w * h)
        for (i in pixels.indices) {
            val p = pixels[i]
            val r = (p shr 16) and 0xFF
            val g = (p shr 8) and 0xFF
            val b = p and 0xFF
            gray[i] = ((0.299 * r + 0.587 * g + 0.114 * b).toInt())
        }
        val outGray = IntArray(w * h)
        val radius = 3
        for (y in 0 until h) {
            for (x in 0 until w) {
                var sum = 0
                var count = 0
                val y0 = maxOf(0, y - radius)
                val y1 = minOf(h - 1, y + radius)
                val x0 = maxOf(0, x - radius)
                val x1 = minOf(w - 1, x + radius)
                for (yy in y0..y1) {
                    val base = yy * w
                    for (xx in x0..x1) {
                        sum += gray[base + xx]
                        count++
                    }
                }
                val mean = if (count > 0) sum / count else gray[y * w + x]
                val value = if (gray[y * w + x] > mean - 10) 255 else 0
                outGray[y * w + x] = value
            }
        }
        for (i in outGray.indices) {
            val v = outGray[i]
            out.setPixel(i % w, i / w, Color.rgb(v, v, v))
        }
        return out
    }

    private fun resizeKeepAspect(bitmap: Bitmap, targetWidth: Int): Bitmap {
        val aspect = bitmap.width.toDouble() / bitmap.height.toDouble()
        val targetHeight = (targetWidth / aspect).toInt()
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }

    private fun extractPlateCandidates(visionText: com.google.mlkit.vision.text.Text): List<String> {
        val candidateRegex = Regex("[A-Z0-9\\s]{8,12}")
        val results = mutableListOf<String>()
        for (block in visionText.textBlocks) {
            for (line in block.lines) {
                val lineText = line.text
                    .replace("[^A-Za-z0-9\\s]".toRegex(), "")
                    .uppercase(Locale.US)
                candidateRegex.findAll(lineText).forEach {
                    val token = it.value.replace("\\s+".toRegex(), "")
                    if (token.length >= 8) results.add(token)
                }
                for (elem in line.elements) {
                    val e = elem.text
                        .replace("[^A-Za-z0-9]".toRegex(), "")
                        .uppercase(Locale.US)
                    if (e.length in 6..12) {
                        if (candidateRegex.matches(e) || e.length >= 8)
                            results.add(e.replace("\\s+".toRegex(), ""))
                    }
                }
            }
        }
        val whole = visionText.text
            .replace("[^A-Za-z0-9\\s]".toRegex(), "")
            .replace("\\s+".toRegex(), "")
            .uppercase(Locale.US)
        if (whole.length in 8..12) results.add(whole)
        return results.distinct()
    }

    private fun correctByPosition(candidate: String): String {
        val s = candidate.uppercase(Locale.US).trim()
        val regexVehicleNo  = Regex("^[A-Z]{2}\\d{2}[A-Z]{2}\\d{4}$")
        val regexVehicleNo2 = Regex("^[A-Z]{2}\\d{2}[A-Z]\\d{4}$")
        val regexVehicleNo3 = Regex("^[A-Z]{2}\\d{2}[A-Z]{3}\\d{4}$")
        val regexVehicleNo4 = Regex("^\\d{2}\\s?[A-Z]{2}\\s?\\d{4}\\s?[A-Z]$")

        fun applyMask(original: String, digitMask: BooleanArray): String {
            val arr = original.toCharArray()
            for (i in arr.indices) {
                if (i >= digitMask.size) break
                if (digitMask[i]) {
                    when (arr[i]) {
                        'O', 'Q', 'D' -> arr[i] = '0'
                        'I', 'L'      -> arr[i] = '1'
                        'S'           -> arr[i] = '5'
                        'Z'           -> arr[i] = '2'
                        'B'           -> arr[i] = '8'
                    }
                } else {
                    if (!arr[i].isLetter()) {
                        when (arr[i]) {
                            '0' -> arr[i] = 'O'
                            '1' -> arr[i] = 'I'
                            '8' -> arr[i] = 'B'
                        }
                    }
                }
            }
            return String(arr)
        }

        val possibleCorrections = mutableListOf<String>()
        val len = s.length

        if (len == 10) {
            val mask = BooleanArray(len)
            mask[2] = true; mask[3] = true
            mask[6] = true; mask[7] = true; mask[8] = true; mask[9] = true
            possibleCorrections.add(applyMask(s, mask))
        }
        if (len == 9) {
            val mask = BooleanArray(len)
            mask[2] = true; mask[3] = true
            for (i in 5 until len) mask[i] = true
            possibleCorrections.add(applyMask(s, mask))
        }
        if (len == 11) {
            val mask = BooleanArray(len)
            mask[2] = true; mask[3] = true
            for (i in 7 until len) mask[i] = true
            possibleCorrections.add(applyMask(s, mask))
        }
        if (len in 8..12) {
            val mask = BooleanArray(len)
            if (len >= 6) {
                for (i in (len - 4) until len) mask[i] = true
                if (len > 3) { mask[2] = true; mask[3] = true }
            }
            possibleCorrections.add(applyMask(s, mask))
        }

        val allTests = possibleCorrections.distinct()
        for (test in allTests) {
            val cleaned = test.replace("\\s+".toRegex(), "")
            if (regexVehicleNo.matches(cleaned)  ||
                regexVehicleNo2.matches(cleaned) ||
                regexVehicleNo3.matches(cleaned) ||
                regexVehicleNo4.matches(cleaned)) {
                return cleaned
            }
        }
        return s.replace("\\s+".toRegex(), "")
    }


    private fun processVinImage(bitmap: Bitmap, onResult: (String?) -> Unit) {

        // Fast variants first — ordered by likelihood of success
        // Stop as soon as we get a confident VIN (starts MA3 + regex match)
        val variants = mutableListOf<Pair<String, Bitmap>>()

        // 1. Cropped plate region — most likely to work, try first
        val plateCrop = cropBitmap(bitmap, 0.50f, 0.90f, 0f, 1f)
        val scaledPlate = Bitmap.createScaledBitmap(plateCrop, plateCrop.width * 2, plateCrop.height * 2, true)

        variants.add("plate_scaled" to scaledPlate)
        variants.add("plate_gray_scaled" to toGrayscaleSimple(scaledPlate))
        variants.add("plate_contrast_scaled" to enhanceContrast(scaledPlate))
        variants.add("plate_raw" to plateCrop)
        variants.add("plate_gray" to toGrayscaleSimple(plateCrop))

        // 2. Full image fallbacks — only used if plate crop fails
        variants.add("full_raw" to bitmap)
        variants.add("full_gray" to toGrayscaleSimple(bitmap))
        variants.add("full_contrast" to enhanceContrast(bitmap))

        // Run variants one by one, stop early on confident match
        processVariantSequentially(variants, 0, mutableListOf(), onResult)
    }

    private fun processVariantSequentially(
        variants: List<Pair<String, Bitmap>>,
        index: Int,
        collectedTexts: MutableList<String>,
        onResult: (String?) -> Unit
    ) {
        if (index >= variants.size) {
            // All variants exhausted — return best from collected
            val best = findBestVinFromAllResults(collectedTexts)
            onResult(best)
            return
        }

        val (name, bmp) = variants[index]
        val image = InputImage.fromBitmap(bmp, 0)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val raw = visionText.text
                Log.d("VIN_OCR", "[$name] Got: '$raw'")
                collectedTexts.add(raw)

                // Check for confident result immediately
                val quickCheck = findBestVinFromAllResults(listOf(raw))
                if (quickCheck != null && quickCheck.startsWith("MA3") && VIN_REGEX.matches(quickCheck)) {
                    Log.d("VIN_OCR", "[$name] Early exit! Confident VIN: $quickCheck")
                    onResult(quickCheck)
                    return@addOnSuccessListener  // Stop processing more variants
                }

                // Also check accumulated results for MA3 match
                if (index >= 2) {  // After at least 3 variants tried
                    val accumulated = findBestVinFromAllResults(collectedTexts)
                    if (accumulated != null && accumulated.startsWith("MA3") && VIN_REGEX.matches(accumulated)) {
                        Log.d("VIN_OCR", "Accumulated confident VIN after $index variants: $accumulated")
                        onResult(accumulated)
                        return@addOnSuccessListener
                    }
                }

                // Not confident yet — try next variant
                processVariantSequentially(variants, index + 1, collectedTexts, onResult)
            }
            .addOnFailureListener {
                Log.d("VIN_OCR", "[$name] Failed")
                processVariantSequentially(variants, index + 1, collectedTexts, onResult)
            }
    }

    /**
     * Crops a bitmap by percentage of width/height.
     * topFraction=0.4, bottomFraction=1.0 means take from 40% to 100% of height.
     */
    private fun cropBitmap(
        bitmap: Bitmap,
        topFraction: Float,
        bottomFraction: Float,
        leftFraction: Float,
        rightFraction: Float
    ): Bitmap {
        val top = (bitmap.height * topFraction).toInt().coerceIn(0, bitmap.height)
        val bottom = (bitmap.height * bottomFraction).toInt().coerceIn(0, bitmap.height)
        val left = (bitmap.width * leftFraction).toInt().coerceIn(0, bitmap.width)
        val right = (bitmap.width * rightFraction).toInt().coerceIn(0, bitmap.width)
        val h = (bottom - top).coerceAtLeast(1)
        val w = (right - left).coerceAtLeast(1)
        return Bitmap.createBitmap(bitmap, left, top, w, h)
    }

    private fun toGrayscaleSimple(bitmap: Bitmap): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        val out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        for (y in 0 until h) {
            for (x in 0 until w) {
                val p = bitmap.getPixel(x, y)
                val r = (p shr 16) and 0xFF
                val g = (p shr 8) and 0xFF
                val b = p and 0xFF
                val gray = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
                out.setPixel(x, y, Color.rgb(gray, gray, gray))
            }
        }
        return out
    }

    private fun enhanceContrast(bitmap: Bitmap): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        val out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        for (y in 0 until h) {
            for (x in 0 until w) {
                val p = bitmap.getPixel(x, y)
                val r = (p shr 16) and 0xFF
                val g = (p shr 8) and 0xFF
                val b = p and 0xFF
                val gray = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
                // High contrast: push towards black or white
                val enhanced = if (gray > 128) minOf(255, gray + 60) else maxOf(0, gray - 60)
                out.setPixel(x, y, Color.rgb(enhanced, enhanced, enhanced))
            }
        }
        return out
    }

    private fun findBestVinFromAllResults(allTexts: List<String>): String? {
        val candidates = mutableListOf<String>()

        for (raw in allTexts) {
            val denoised = raw.uppercase(Locale.US)
                .replace("MARUTI", "")
                .replace("SUZUKI", "")
                .replace("INDIA", "")
                .replace("LTD", "")
                .replace("ENGINE", "")
                .replace("NO.", "")
                .replace(Regex("[^A-Z0-9]"), "")

            Log.d("VIN_BLOB", "Denoised: '$denoised'")

            // Strategy 1: Find M anchor — take 17 chars from each M
            var searchStart = 0
            while (searchStart < denoised.length) {
                val mIndex = denoised.indexOf('M', searchStart)
                if (mIndex < 0) break
                if (denoised.length >= mIndex + 17) {
                    val candidate = denoised.substring(mIndex, mIndex + 17)
                    if (isLikelyVin(candidate)) {
                        candidates.add(0, correctVinChars(candidate))
                        Log.d("VIN_M_ANCHOR", "M-anchored: '$candidate'")
                    }
                }
                searchStart = mIndex + 1
            }

            // Strategy 2: OCR dropped the M — check if prepending M gives a valid VIN
            // This handles: "A3EADE1S00137478" → "MA3EADE1S00137478"
            // Trigger: blob doesn't contain M but has 16-17 alphanumeric chars
            // that look like the rest of a VIN
            if (!denoised.contains('M') || candidates.isEmpty()) {
                // Try prepending M to every position that gives 17 chars
                val stripped = denoised.replace(Regex("[^A-Z0-9]"), "")

                // If we have 16 chars — M was probably dropped at the start
                if (stripped.length == 16) {
                    val withM = "M$stripped"
                    if (isLikelyVin(withM)) {
                        candidates.add(0, correctVinChars(withM))
                        Log.d("VIN_PREPEND_M", "Prepended M: '$withM'")
                    }
                }

                // If we have 17+ chars — M replaced by another char at position 0
                // The ⊗ before M sometimes causes OCR to read first char wrong
                if (stripped.length >= 17) {
                    // Try replacing first char with M
                    val firstReplaced = "M" + stripped.substring(1, 17)
                    if (isLikelyVin(firstReplaced)) {
                        candidates.add(correctVinChars(firstReplaced))
                        Log.d("VIN_FIRST_REPLACE", "First char → M: '$firstReplaced'")
                    }
                    // Try inserting M at position 0 and take 17
                    val inserted = "M" + stripped.substring(0, 16)
                    if (isLikelyVin(inserted)) {
                        candidates.add(correctVinChars(inserted))
                        Log.d("VIN_INSERT_M", "Inserted M: '$inserted'")
                    }
                }
            }

            // Strategy 3: Line by line
            raw.split("\n").map { it.trim() }.filter { it.length >= 8 }.forEach { line ->
                val upper = line.uppercase(Locale.US)
                if (upper.contains("MARUTI") || upper.contains("SUZUKI") ||
                    upper.contains("INDIA") || upper.contains("LTD") ||
                    upper.contains("ENGINE")) return@forEach

                val cleaned = upper.replace(Regex("[^A-Z0-9]"), "")
                Log.d("VIN_LINE", "Line cleaned: '$cleaned'")

                // M anchor on line
                var s = 0
                while (s < cleaned.length) {
                    val mi = cleaned.indexOf('M', s)
                    if (mi < 0) break
                    if (cleaned.length >= mi + 17) {
                        val candidate = cleaned.substring(mi, mi + 17)
                        if (isLikelyVin(candidate)) {
                            candidates.add(0, correctVinChars(candidate))
                        }
                    }
                    s = mi + 1
                }

                // M dropped — try prepend/replace on line
                if (!cleaned.contains('M') && cleaned.length in 15..18) {
                    // Replace first char with M
                    if (cleaned.length >= 16) {
                        val firstReplaced = "M" + cleaned.substring(1, 17)
                        if (isLikelyVin(firstReplaced)) {
                            candidates.add(0, correctVinChars(firstReplaced))
                            Log.d("VIN_LINE_M_REPLACE", "Line first→M: '$firstReplaced'")
                        }
                    }
                    // Prepend M
                    if (cleaned.length == 16) {
                        val withM = "M$cleaned"
                        if (isLikelyVin(withM)) {
                            candidates.add(0, correctVinChars(withM))
                            Log.d("VIN_LINE_M_PREPEND", "Line prepend M: '$withM'")
                        }
                    }
                }
            }
        }

        val unique = candidates.filter { it.length == 17 }.distinct()
        Log.d("VIN_CANDIDATES", "Candidates: $unique")

        // Priority 1: M + full regex
        unique.firstOrNull { it.startsWith("M") && VIN_REGEX.matches(it) }?.let {
            Log.d("VIN_RESULT", "Perfect: $it"); return it
        }

        // Priority 2: M + high score
        unique.filter { it.startsWith("M") }
            .maxByOrNull { scoreVin(it) }?.let {
                Log.d("VIN_RESULT", "M scored: $it"); return it
            }

        // Priority 3: any candidate
        return unique.maxByOrNull { scoreVin(it) }
    }

    /**
     * Checks if a 17-char string is likely a VIN and NOT a word/noise.
     *
     * Rules:
     * 1. Must be exactly 17 chars
     * 2. Must NOT contain consecutive letters that spell common words
     * 3. Positions 9-16 (VIS) should be mostly digits — real VINs have digits here
     * 4. Must not be all letters (that would be a word, not a VIN)
     * 5. Digit count in last 6 positions should be >= 4 (serial number)
     */
    private fun isLikelyVin(candidate: String): Boolean {
        if (candidate.length != 17) return false

        // Must have a good mix of letters AND digits — pure letters = word, not VIN
        val digitCount = candidate.count { it.isDigit() }
        val letterCount = candidate.count { it.isLetter() }

        // VIN always has digits — if no digits at all, it's noise
        if (digitCount == 0) return false

        // If all digits, also not a VIN
        if (letterCount == 0) return false

        // Last 6 chars (serial) should have at least 4 digits
        val last6 = candidate.substring(11)
        val digitsInLast6 = last6.count { it.isDigit() }
        if (digitsInLast6 < 3) return false

        // No invalid VIN chars (I, O, Q are banned in real VINs)
        // But we check AFTER correction so this is less critical here

        // Common false-positive word patterns to reject
        // If first 6 chars are all letters — likely a word fragment, not WMI+VDS
        val first6AllLetters = candidate.substring(0, 6).all { it.isLetter() }
        if (first6AllLetters && digitCount < 4) return false

        return true
    }
    private fun correctVinChars(raw: String): String {
        if (raw.length != 17) return raw
        return raw.mapIndexed { index, char ->
            when {
                // Only these 3 are UNIVERSALLY banned in ALL VIN positions
                char == 'I' -> '1'
                char == 'O' -> '0'
                char == 'Q' -> '0'

                // ONLY fix obvious digit-misreads in serial positions 11-16
                // These are almost always digits in Maruti VINs
                index in 11..16 -> when (char) {
                    'S' -> '5'
                    'G' -> '6'
                    'B' -> '8'
                    'Z' -> '2'
                    'D' -> '0'
                    'L' -> '1'
                    'T' -> '7'
                    else -> char  // Leave everything else as-is
                }

                // DO NOT touch positions 0-10 except I/O/Q above
                // S, A, E, D etc. are valid letters in WMI and VDS sections
                else -> char
            }
        }.joinToString("")
    }

    private fun scoreVin(vin: String): Int {
        if (vin.length != 17) return 0
        var score = 0
        if (vin.startsWith("M")) score += 20  // Maruti WMI bonus
        if (vin.startsWith("MA3")) score += 30 // Maruti WMI exact
        if (VIN_REGEX.matches(vin)) score += 50
        // Digits in last 6 positions (serial) are expected
        for (i in 11..16) if (vin[i].isDigit()) score += 3
        // Valid VIN chars throughout
        val validChars = "ABCDEFGHJKLMNPRSTUVWXYZ0123456789"
        score += vin.count { it in validChars } * 2
        return score
    }
    private fun findVinInOcrOutput(visionText: com.google.mlkit.vision.text.Text): String? {

        // Step 1: Try each LINE as-is first
        // The VIN is on one line in the image — trust ML Kit's line detection
        for (block in visionText.textBlocks) {
            for (line in block.lines) {
                // Remove ONLY the ⊗ decorator and spaces — touch nothing else
                val raw = line.text
                    .replace("⊗", "")
                    .replace("*", "")
                    .replace("×", "")
                    .replace(" ", "")
                    .trim()
                    .uppercase(Locale.US)

                Log.d("VIN_CANDIDATE", "Line candidate: '$raw' (len=${raw.length})")

                // If it's exactly 17 chars — this is our VIN
                if (raw.length == 17 && VIN_REGEX.matches(raw)) {
                    Log.d("VIN_FOUND", "Exact match from line: $raw")
                    return raw
                }

                // If it's 17 chars but fails regex — still return it,
                // OCR read it exactly as it saw it
                if (raw.length == 17) {
                    Log.d("VIN_FOUND", "17-char from line (no regex): $raw")
                    return raw
                }
            }
        }

        // Step 2: Concatenate all elements in order (OCR split the VIN across words)
        for (block in visionText.textBlocks) {
            for (line in block.lines) {
                val combined = line.elements.joinToString("") { elem ->
                    elem.text
                        .replace("⊗", "")
                        .replace("*", "")
                        .replace("×", "")
                        .uppercase(Locale.US)
                        .trim()
                }
                Log.d("VIN_COMBINED", "Combined elements: '$combined' (len=${combined.length})")

                if (combined.length == 17 && VIN_REGEX.matches(combined)) return combined
                if (combined.length == 17) return combined
            }
        }

        // Step 3: Full blob — last resort, just take 17 chars from wherever they appear
        val fullText = visionText.text
            .replace(Regex("[^A-Za-z0-9]"), "")
            .uppercase(Locale.US)

        Log.d("VIN_BLOB", "Full blob: '$fullText' (len=${fullText.length})")

        // Find the longest alphanumeric sequence that is 17 chars
        val match = Regex("[A-Z0-9]{17}").find(fullText)
        if (match != null) {
            Log.d("VIN_BLOB_MATCH", "Blob match: '${match.value}'")
            return match.value
        }

        return null
    }

    private fun pickBestVin(ocrResults: List<String>): String? {
        val candidates = mutableListOf<String>()

        for (raw in ocrResults) {
            // Step 1: clean the raw OCR text — remove decorators like ⊗ × * etc.
            val cleaned = raw
                .replace(Regex("[^A-Za-z0-9]"), " ")   // keep only alphanumeric + spaces
                .replace(Regex("\\s+"), " ")
                .trim()
                .uppercase()

            // Step 2: build token list from words and also concatenated chunks
            val tokens = cleaned.split(" ").filter { it.isNotEmpty() }

            // Try each word individually (sometimes OCR splits VIN into segments)
            for (token in tokens) {
                if (token.length in 14..20) {
                    // Slide 17-char window over the token
                    for (i in 0..(token.length - 17).coerceAtLeast(0)) {
                        if (i + 17 <= token.length) {
                            val chunk = token.substring(i, i + 17)
                            candidates.add(applyVinCorrections(chunk))
                        }
                    }
                    // Also try the token as-is if close to 17
                    if (token.length in 16..18) {
                        candidates.add(applyVinCorrections(token.take(17).padEnd(17, '0')))
                    }
                }
            }

            // Step 3: try concatenating adjacent tokens to rebuild the VIN
            // (OCR sometimes puts spaces in the middle of a VIN)
            for (windowSize in 2..tokens.size) {
                for (start in 0..(tokens.size - windowSize)) {
                    val combined = tokens.subList(start, start + windowSize).joinToString("")
                    if (combined.length in 14..20) {
                        for (i in 0..(combined.length - 17).coerceAtLeast(0)) {
                            if (i + 17 <= combined.length) {
                                val chunk = combined.substring(i, i + 17)
                                candidates.add(applyVinCorrections(chunk))
                            }
                        }
                    }
                }
            }

            // Step 4: also try the fully concatenated string (original approach as fallback)
            val fullyJoined = cleaned.replace(" ", "")
            if (fullyJoined.length >= 17) {
                for (i in 0..(fullyJoined.length - 17)) {
                    val chunk = fullyJoined.substring(i, i + 17)
                    candidates.add(applyVinCorrections(chunk))
                }
            }
        }

        val uniqueCandidates = candidates.filter { it.length == 17 }.distinct()

        // Priority 1: exact VIN regex match
        uniqueCandidates.firstOrNull { VIN_REGEX.matches(it) }?.let { return it }

        // Priority 2: score each candidate — prefer ones that match known VIN structure
        return uniqueCandidates.maxByOrNull { scoreVinCandidate(it) }
    }

    /**
     * Scores a 17-char candidate based on how well it matches VIN structural rules.
     * Higher = more likely to be a real VIN.
     */
    private fun scoreVinCandidate(vin: String): Int {
        if (vin.length != 17) return 0
        var score = 0

        // Valid VIN chars only (no I, O, Q)
        val validChars = "ABCDEFGHJKLMNPRSTUVWXYZ0123456789"
        score += vin.count { it in validChars } * 2

        // Positions 0-2: WMI — should be letters/digits, no special chars
        if (vin[0].isLetter()) score += 3
        if (vin[1].isLetter() || vin[1].isDigit()) score += 2
        if (vin[2].isLetter() || vin[2].isDigit()) score += 2

        // Positions 3-8: VDS — mixed letters and digits
        for (i in 3..8) {
            if (vin[i].isLetterOrDigit()) score += 1
        }

        // Position 9 (index 8): check digit — must be 0-9 or X
        if (vin[8].isDigit() || vin[8] == 'X') score += 5

        // Position 10 (index 9): model year — should be a letter or digit (not I,O,Q,U,Z,0)
        val validYearChars = "ABCDEFGHJKLMNPRSTUVWXY123456789"
        if (vin[9] in validYearChars) score += 3

        // Positions 12-17 (index 11-16): VIS sequence — typically digits
        for (i in 11..16) {
            if (vin[i].isDigit()) score += 2
            else score -= 1  // penalize letters in the serial number section
        }

        return score
    }

    /**
     * Improved position-aware VIN character corrections.
     * Based on actual VIN structure:
     *   [0-2]  WMI  - manufacturer identifier (letters/digits)
     *   [3-8]  VDS  - vehicle descriptor (letters/digits, mixed)
     *   [8]    Check digit (0-9 or X)
     *   [9]    Model year (letter or digit, specific set)
     *   [10]   Plant code (letter or digit)
     *   [11-16] Sequential serial number (digits)
     */
    private fun applyVinCorrections(raw: String): String {
        if (raw.length != 17) return raw
        return raw.mapIndexed { index, char ->
            when {
                // I, O, Q are NEVER valid in any VIN position
                char == 'I' -> '1'
                char == 'O' -> '0'
                char == 'Q' -> '0'

                // Position 8: check digit — digits or X only
                index == 8 -> when (char) {
                    'X' -> 'X'
                    'S' -> '5'
                    'B' -> '8'
                    'G' -> '6'
                    'Z' -> '2'
                    'D' -> '0'
                    else -> char
                }

                // Positions 11-16: VIS serial — almost always digits
                index in 11..16 -> when (char) {
                    'S' -> '5'
                    'G' -> '6'
                    'B' -> '8'
                    'Z' -> '2'
                    'A' -> '4'
                    'D' -> '0'
                    'L' -> '1'
                    'T' -> '7'
                    else -> char
                }

                // Position 9: model year — specific valid chars, never digit 0
                index == 9 -> when (char) {
                    '0' -> 'D'   // 0 is not valid model year, D is a valid year code
                    else -> char
                }

                // Positions 0-7, 10: WMI + VDS + plant — letters expected mostly
                // Fix digits that are clearly misread letters
                index in 0..7 || index == 10 -> when (char) {
                    // Only correct obvious digit→letter in letter-dominant positions
                    // Don't over-correct here as WMI/VDS can have digits
                    else -> char
                }

                else -> char
            }
        }.joinToString("")
    }

// ---------- NEW Image Preprocessing for Embossed/Chassis VINs ----------

    /**
     * Sharpening + high-contrast threshold — best for embossed metal VINs
     * where the text has very low contrast with the background.
     */
    private fun sharpenAndThreshold(bitmap: Bitmap): Bitmap {
        val w = bitmap.width
        val h = bitmap.height

        // Step 1: convert to grayscale
        val gray = IntArray(w * h)
        for (y in 0 until h) {
            for (x in 0 until w) {
                val p = bitmap.getPixel(x, y)
                val r = (p shr 16) and 0xFF
                val g = (p shr 8) and 0xFF
                val b = p and 0xFF
                gray[y * w + x] = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
            }
        }

        // Step 2: unsharp mask (sharpen) — enhances embossed edges
        val sharpened = IntArray(w * h)
        for (y in 1 until h - 1) {
            for (x in 1 until w - 1) {
                val center = gray[y * w + x]
                val top    = gray[(y - 1) * w + x]
                val bottom = gray[(y + 1) * w + x]
                val left   = gray[y * w + (x - 1)]
                val right  = gray[y * w + (x + 1)]
                // Laplacian sharpening kernel
                val sharp = (5 * center - top - bottom - left - right)
                sharpened[y * w + x] = sharp.coerceIn(0, 255)
            }
        }

        // Step 3: adaptive threshold with larger radius for embossed text
        val out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val radius = 15  // larger radius works better for embossed text
        val offset = -8  // slight negative offset to catch faint embossing

        for (y in 0 until h) {
            for (x in 0 until w) {
                var sum = 0
                var count = 0
                val y0 = maxOf(0, y - radius)
                val y1 = minOf(h - 1, y + radius)
                val x0 = maxOf(0, x - radius)
                val x1 = minOf(w - 1, x + radius)
                for (yy in y0..y1) {
                    for (xx in x0..x1) {
                        sum += sharpened[yy * w + xx]
                        count++
                    }
                }
                val mean = if (count > 0) sum / count else sharpened[y * w + x]
                val value = if (sharpened[y * w + x] > mean + offset) 255 else 0
                val idx = y * w + x
                out.setPixel(x, y, Color.rgb(value, value, value))
            }
        }
        return out
    }

    /**
     * Inverts a bitmap — sometimes OCR works better on dark-on-light vs light-on-dark.
     */
    private fun invertBitmap(bitmap: Bitmap): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        val out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        for (y in 0 until h) {
            for (x in 0 until w) {
                val p = bitmap.getPixel(x, y)
                val r = 255 - ((p shr 16) and 0xFF)
                val g = 255 - ((p shr 8) and 0xFF)
                val b = 255 - (p and 0xFF)
                out.setPixel(x, y, Color.rgb(r, g, b))
            }
        }
        return out
    }


    /**
     * Your existing vehicle-number logic, cleaned up and separated from VIN logic.
     */
    private fun displayBestVehicleNumber(results: List<String>, resultTextView: TextView) {
        val regexVehicleNo  = Regex("^[A-Z]{2}\\d{2}[A-Z]{2}\\d{4}$")
        val regexVehicleNo2 = Regex("^[A-Z]{2}\\d{2}[A-Z]\\d{4}$")
        val regexVehicleNo3 = Regex("^[A-Z]{2}\\d{2}[A-Z]{3}\\d{4}$")

        val raw = results.maxByOrNull { it.length } ?: return
        var cleaned = raw
            .replace(Regex("[\\s\\-./|,()$\"]"), "")
            .replace("IND", "").replace("IN0", "")
            .replace("UND", "").replace("UN0", "")
            .replace("1N0", "").replace("NO", "")
            .uppercase()
            .trim()

        // Position-aware correction for vehicle numbers
        cleaned = when (cleaned.length) {
            9  -> cleaned.mapIndexed { i, c ->
                if ((i == 2 || i == 3 || i >= 5) && c == 'O') '0'
                else if ((i == 4) && c == '0') 'D'
                else c
            }.joinToString("")
            10 -> cleaned.mapIndexed { i, c ->
                if ((i == 2 || i == 3 || i >= 6) && c == 'O') '0'
                else if ((i == 4 || i == 5) && c == '0') 'D'
                else c
            }.joinToString("")
            11 -> cleaned.mapIndexed { i, c ->
                if ((i == 2 || i == 3 || i >= 7) && c == 'O') '0'
                else if ((i in 4..6) && c == '0') 'D'
                else c
            }.joinToString("")
            else -> cleaned
        }

        when {
            regexVehicleNo.matches(cleaned) || regexVehicleNo2.matches(cleaned) || regexVehicleNo3.matches(cleaned) -> {
                resultTextView.text = cleaned
                vehNoTextView.text  = cleaned
//                vinNoTextView.text  = cleaned
                vinNoTextView.setText(cleaned)
                Toast.makeText(this, "Recognised: $cleaned", Toast.LENGTH_SHORT).show()
            }
            else -> Toast.makeText(this, "Could not recognise a valid plate. Got: $cleaned", Toast.LENGTH_LONG).show()
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
        save_button2.visibility=View.INVISIBLE

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
//        vinNoTextView.text=""
        vinNoTextView.setText("")
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


    private fun fetchChassisData() {
        val client = OkHttpClient()
        val chassis=chassis_no.text.toString()
        val url =ApiFile.ERP_APP_URL+"/tvAccounts/tvAccDetailsByChassisNo?chassisNo=$chassis&ouId=$ouId"

        val request = Request.Builder()
            .url(url)
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()

                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    val objArray = jsonObject.getJSONArray("obj")

                    if (objArray.length() == 0) {
                        runOnUiThread {
                            Toast.makeText(
                                this@TrueValueStockTaking2,
                                "No details found for vehicle no.: $chassis",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        return@let
                    }

                    val stockItem = objArray.getJSONObject(0)

                    // field names
                    val regData = regNoDataStkTake2(
                        CHASSIS_NO     = stockItem.optString("CHASSIS_NO", "-"),
                        VARIANT_DESC   = stockItem.optString("VARIANT_DESC", "-"),
                        MODEL_DESC     = stockItem.optString("MODEL_DESC", "-"),
                        VIN            = stockItem.optString("VIN", "-"),
                        ENGINE_NO      = stockItem.optString("ENGINE_NO", "-"),
                        VARIANT_CODE   = stockItem.optString("VARIANT", "-"),
                        VEH_STATUS     = stockItem.optString("VEH_STATUS", "-"),
                        REG_NO         = stockItem.optString("REG_NO", "-"),
                        LOCATION       = stockItem.optString("LOCATION", "-"),
                        OPERATING_UNIT = stockItem.optInt("OPERATING_UNITUID", 0),
                        STATUS         = stockItem.optString("STATUS", "-"),
                        FUEL_DESC      = stockItem.optString("FUEL_DESC", "-"),
                        COLOUR         = stockItem.optString("COLOUR", "-")
                    )
                    runOnUiThread {
                        populateFields(regData)
                        qr_result_textview.visibility = View.VISIBLE
                        VinDetails.visibility = View.VISIBLE
                        refreshButton.visibility = View.VISIBLE
                        Toast.makeText(
                            this@TrueValueStockTaking2,
                            "Details found Successfully \n for chassis: $chassis",
                            Toast.LENGTH_LONG
                        ).show()
                        VinDetails.visibility = View.VISIBLE
                        save_button.visibility = View.INVISIBLE
                        save_button2.visibility=View.VISIBLE
                        save_button2.visibility = View.VISIBLE
                        CHASSIS_NUM.visibility = View.GONE
                        VIN.visibility = View.GONE
                        VARIANT_CD.visibility = View.GONE
                        FUEL_DESC.visibility = View.GONE
                        MODEL_CD.visibility = View.GONE
                        ENGINE_NO.visibility = View.GONE
                        LOCATION.visibility = View.VISIBLE
                        DRIVER_NAME.visibility = View.VISIBLE
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
                        this@TrueValueStockTaking2,
                        "Failed to fetch details for chassis: $chassis",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun fetchRegNoDataByCamera() {
        fetchMethod = "Vehicle Number"
        val client = OkHttpClient()
        val vehicleNo2=vehNoTextView.text.toString()
        val url =ApiFile.ERP_APP_URL+"/tvAccounts/tvAccDetailsByRegNo?regNo=$vehicleNo2"

        val request = Request.Builder()
            .url(url)
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()

                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    val objArray = jsonObject.getJSONArray("obj")

                    if (objArray.length() == 0) {
                        runOnUiThread {
                            Toast.makeText(
                                this@TrueValueStockTaking2,
                                "No details found for vehicle no.: $vehicleNo2",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        return@let
                    }

                    val stockItem = objArray.getJSONObject(0)

                    // field names
                    val regData = regNoDataStkTake2(
                        CHASSIS_NO     = stockItem.optString("CHASSIS_NO", "-"),
                        VARIANT_DESC   = stockItem.optString("VARIANT_DESC", "-"),
                        MODEL_DESC     = stockItem.optString("MODEL_DESC", "-"),
                        VIN            = stockItem.optString("VIN", "-"),
                        ENGINE_NO      = stockItem.optString("ENGINE_NO", "-"),
                        VARIANT_CODE   = stockItem.optString("VARIANT", "-"),
                        VEH_STATUS     = stockItem.optString("VEH_STATUS", "-"),
                        REG_NO         = stockItem.optString("REG_NO", "-"),
                        LOCATION       = stockItem.optString("LOCATION", "-"),
                        OPERATING_UNIT = stockItem.optInt("OPERATING_UNITUID", 0),
                        STATUS         = stockItem.optString("STATUS", "-"),
                        FUEL_DESC      = stockItem.optString("FUEL_DESC", "-"),
                        COLOUR         = stockItem.optString("COLOUR", "-")
                    )
                    runOnUiThread {
                        populateFields(regData)
                        qr_result_textview.visibility = View.VISIBLE
                        VinDetails.visibility = View.VISIBLE
                        refreshButton.visibility = View.VISIBLE
                        Toast.makeText(
                            this@TrueValueStockTaking2,
                            "Details found Successfully \n for vehicle no.: $vehicleNo2",
                            Toast.LENGTH_LONG
                        ).show()
                        VinDetails.visibility = View.VISIBLE
                        save_button.visibility = View.VISIBLE
                        save_button2.visibility = View.INVISIBLE
                        CHASSIS_NUM.visibility = View.GONE
                        VIN.visibility = View.GONE
                        VARIANT_CD.visibility = View.GONE
                        FUEL_DESC.visibility = View.GONE
                        MODEL_CD.visibility = View.GONE
                        ENGINE_NO.visibility = View.GONE
                        LOCATION.visibility = View.VISIBLE
                        DRIVER_NAME.visibility = View.VISIBLE
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
                        this@TrueValueStockTaking2,
                        "Failed to fetch details for vehicle no.: $vehicleNo2",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun fetchRegNoData() {
        val client = OkHttpClient()
        val vehicleNo = vehNoEditText.text.toString()
        val url = ApiFile.ERP_APP_URL + "/tvAccounts/tvAccDetailsByRegNo?regNo=$vehicleNo"

        val request = Request.Builder()
            .url(url)
            .build()
        Log.d("URL->",request.toString())

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()

                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    val objArray = jsonObject.getJSONArray("obj")
                    Log.d("jsonObject->",jsonObject.toString())

                    if (objArray.length() == 0) {
                        runOnUiThread {
                            Toast.makeText(
                                this@TrueValueStockTaking2,
                                "No details found for vehicle no.: $vehicleNo",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        return@let
                    }

                    val stockItem = objArray.getJSONObject(0)

                    // field names
                    val regData = regNoDataStkTake2(
                        CHASSIS_NO     = stockItem.optString("CHASSIS_NO", "-"),
                        VARIANT_DESC   = stockItem.optString("VARIANT_DESC", "-"),
                        MODEL_DESC     = stockItem.optString("MODEL_DESC", "-"),
                        VIN            = stockItem.optString("VIN", "-"),
                        ENGINE_NO      = stockItem.optString("ENGINE_NO", "-"),
                        VARIANT_CODE   = stockItem.optString("VARIANT", "-"),
                        VEH_STATUS     = stockItem.optString("VEH_STATUS", "-"),
                        REG_NO         = stockItem.optString("REG_NO", "-"),
                        LOCATION       = stockItem.optString("LOCATION", "-"),
                        OPERATING_UNIT = stockItem.optInt("OPERATING_UNITUID", 0),
                        STATUS         = stockItem.optString("STATUS", "-"),
                        FUEL_DESC      = stockItem.optString("FUEL_DESC", "-"),
                        COLOUR         = stockItem.optString("COLOUR", "-")
                    )

                    runOnUiThread {
                        populateFields(regData)
                        qr_result_textview.visibility = View.VISIBLE
                        VinDetails.visibility = View.VISIBLE
                        refreshButton.visibility = View.VISIBLE
                        Toast.makeText(
                            this@TrueValueStockTaking2,
                            "Details found Successfully \n for vehicle no.: $vehicleNo",
                            Toast.LENGTH_LONG
                        ).show()
                        save_button.visibility = View.INVISIBLE
                        save_button2.visibility = View.VISIBLE
                        CHASSIS_NUM.visibility = View.GONE
                        VIN.visibility = View.GONE
                        VARIANT_CD.visibility = View.GONE
                        FUEL_DESC.visibility = View.GONE
                        MODEL_CD.visibility = View.GONE
                        ENGINE_NO.visibility = View.GONE
                        LOCATION.visibility = View.VISIBLE
                        DRIVER_NAME.visibility = View.VISIBLE
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
                        this@TrueValueStockTaking2,
                        "Failed to fetch details for vehicle no.: $vehicleNo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }



    private fun fetchVinNoData() {
        val client = OkHttpClient()
        val vinNo = vinNoTextView.text.toString()
        fetchMethod = "Vin"
        val url = ApiFile.ERP_APP_URL + "/tvAccounts/tvAccDetailsByVin?vin=$vinNo"

        val request = Request.Builder()
            .url(url)
            .build()
        Log.d("URL->",request.toString())

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()

                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    val objArray = jsonObject.getJSONArray("obj")
                    Log.d("jsonObject->",jsonObject.toString())

                    if (objArray.length() == 0) {
                        runOnUiThread {
                            Toast.makeText(
                                this@TrueValueStockTaking2,
                                "No details found for vin no.: $vinNo",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        return@let
                    }

                    val stockItem = objArray.getJSONObject(0)

                    // field names
                    val regData = regNoDataStkTake2(
                        CHASSIS_NO     = stockItem.optString("CHASSIS_NO", "-"),
                        VARIANT_DESC   = stockItem.optString("VARIANT_DESC", "-"),
                        MODEL_DESC     = stockItem.optString("MODEL_DESC", "-"),
                        VIN            = stockItem.optString("VIN", "-"),
                        ENGINE_NO      = stockItem.optString("ENGINE_NO", "-"),
                        VARIANT_CODE   = stockItem.optString("VARIANT", "-"),
                        VEH_STATUS     = stockItem.optString("VEH_STATUS", "-"),
                        REG_NO         = stockItem.optString("REG_NO", "-"),
                        LOCATION       = stockItem.optString("LOCATION", "-"),
                        OPERATING_UNIT = stockItem.optInt("OPERATING_UNITUID", 0),
                        STATUS         = stockItem.optString("STATUS", "-"),
                        FUEL_DESC      = stockItem.optString("FUEL_DESC", "-"),
                        COLOUR         = stockItem.optString("COLOUR", "-")
                    )

                    runOnUiThread {
                        populateFields(regData)
                        qr_result_textview.visibility = View.VISIBLE
                        VinDetails.visibility = View.VISIBLE
                        refreshButton.visibility = View.VISIBLE
                        Toast.makeText(
                            this@TrueValueStockTaking2,
                            "Details found Successfully \n for vin no.: $vinNo",
                            Toast.LENGTH_LONG
                        ).show()
                        save_button.visibility = View.VISIBLE
                        save_button2.visibility = View.INVISIBLE
                        CHASSIS_NUM.visibility = View.GONE
                        VIN.visibility = View.GONE
                        VARIANT_CD.visibility = View.GONE
                        FUEL_DESC.visibility = View.GONE
                        MODEL_CD.visibility = View.GONE
                        ENGINE_NO.visibility = View.GONE
                        LOCATION.visibility = View.VISIBLE
                        DRIVER_NAME.visibility = View.VISIBLE
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
                        this@TrueValueStockTaking2,
                        "Failed to fetch details for vin no.: $vinNo",
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

    private fun populateFields(regData:regNoDataStkTake2) {
        LOCATION.text="VEHICLE NO.: ${regData.REG_NO}"
        trValChassis.text = "CHASSIS NO.: ${regData.CHASSIS_NO}"
        trValVin.text = "VIN: ${regData.VIN}"
        trValVariantDesc.text = "FUEL DESC: ${regData.FUEL_DESC}"
        trValModelDesc.text="MODEL DESC: ${regData.MODEL_DESC}"
        trValEngineNo.text="ENGINE NO: ${regData.ENGINE_NO}"
        trValVehStatus.text="COLOUR: ${regData.COLOUR}"
        DRIVER_NAME.text="LOCATION: ${regData.LOCATION}"
    }

    private fun saveVinData(regNo: String, batchName: String) {

        var dateTimeToSend: String? = null

        if (::batchStatus.isInitialized && batchStatus.equals("Closed", ignoreCase = true)||jsonBatchStatus.toInt()==0) {
            val batchCreationDt = cutOffDateSpinner.selectedItem?.toString()
                ?: run {
                    Toast.makeText(this, "No date selected in the spinner.", Toast.LENGTH_SHORT).show()
                    return
                }

            val inputFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val date = try {
                inputFormatter.parse(batchCreationDt)
            } catch (e: ParseException) {
                e.printStackTrace()
                Toast.makeText(this, "Invalid date format in the spinner.", Toast.LENGTH_SHORT).show()
                return
            }

            val currentTime = Calendar.getInstance()
            currentTime.timeZone = TimeZone.getTimeZone("Asia/Kolkata")

            val combinedDateTime = Calendar.getInstance()
            combinedDateTime.time = date
            combinedDateTime.set(Calendar.HOUR_OF_DAY, currentTime.get(Calendar.HOUR_OF_DAY))
            combinedDateTime.set(Calendar.MINUTE, currentTime.get(Calendar.MINUTE))
            combinedDateTime.set(Calendar.SECOND, currentTime.get(Calendar.SECOND))
            combinedDateTime.set(Calendar.MILLISECOND, currentTime.get(Calendar.MILLISECOND))

            val formatter2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
            dateTimeToSend = formatter2.format(combinedDateTime.time)

        }

        val client = OkHttpClient()
        val url = ApiFile.ERP_APP_URL + "/tvAccounts/tvBatchNameScan"
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
//        jsonObject.put("createdBy", login_name)
        jsonObject.put("createdBy", login_name)
        jsonObject.put("scanBy", fetchMethod)
        jsonObject.put("batchStatus", "open")
//        if (batchStatus.equals("Closed", ignoreCase = true) && dateTimeToSend != null) {
//            jsonObject.put("batchCreationDate", dateTimeToSend)
//        }
        if(jsonBatchStatus.toInt()==0){
            jsonObject.put("batchCreationDate", dateTimeToSend)
        } else if (batchStatus == "Closed" && dateTimeToSend != null) {
            jsonObject.put("batchCreationDate", dateTimeToSend)
        }
        jsonObject.put("batchCodeEndDate", batchCodeEndDate)
        jsonObject.put("updatedBy", login_name)

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
                                    this@TrueValueStockTaking2,
                                    "Stock taking is already completed for this vehicle",
                                    Toast.LENGTH_LONG
                                ).show()
                                clearFields()
                            }
                            responseCode == 200 -> {
                                Toast.makeText(
                                    this@TrueValueStockTaking2,
                                    "Data saved successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                                clearFields()

                                if(jsonBatchStatus.toInt()==0||batchStatus=="Closed"){
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        findBybatchNameOpenStatus()
                                    }, 2000)
                                }
                            }
                            else -> {
                                Toast.makeText(
                                    this@TrueValueStockTaking2,
                                    "Failed to save data. Error code: $responseCode",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@TrueValueStockTaking2,
                            "No response from server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@TrueValueStockTaking2,
                        "Error saving data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun saveVinData2(regNo: String, batchName: String) {

        var dateTimeToSend: String? = null

        if (::batchStatus.isInitialized && batchStatus.equals("Closed", ignoreCase = true)||jsonBatchStatus.toInt()==0) {
            val batchCreationDt = cutOffDateSpinner.selectedItem?.toString()
                ?: run {
                    Toast.makeText(this, "No date selected in the spinner.", Toast.LENGTH_SHORT).show()
                    return
                }

            val inputFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val date = try {
                inputFormatter.parse(batchCreationDt)
            } catch (e: ParseException) {
                e.printStackTrace()
                Toast.makeText(this, "Invalid date format in the spinner.", Toast.LENGTH_SHORT).show()
                return
            }

            val currentTime = Calendar.getInstance()
            currentTime.timeZone = TimeZone.getTimeZone("Asia/Kolkata")

            val combinedDateTime = Calendar.getInstance()
            combinedDateTime.time = date
            combinedDateTime.set(Calendar.HOUR_OF_DAY, currentTime.get(Calendar.HOUR_OF_DAY))
            combinedDateTime.set(Calendar.MINUTE, currentTime.get(Calendar.MINUTE))
            combinedDateTime.set(Calendar.SECOND, currentTime.get(Calendar.SECOND))
            combinedDateTime.set(Calendar.MILLISECOND, currentTime.get(Calendar.MILLISECOND))

            val formatter2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
            dateTimeToSend = formatter2.format(combinedDateTime.time)

        }

        val client = OkHttpClient()
        val url = ApiFile.ERP_APP_URL + "/tvAccounts/tvBatchNameManual"
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
//        if (batchStatus.equals("Closed", ignoreCase = true) && dateTimeToSend != null) {
//            jsonObject.put("batchCreationDate", dateTimeToSend)
//        }
        if(jsonBatchStatus.toInt()==0){
            jsonObject.put("batchCreationDate", dateTimeToSend)
        } else if (batchStatus == "Closed" && dateTimeToSend != null) {
            jsonObject.put("batchCreationDate", dateTimeToSend)
        }
        jsonObject.put("batchCodeEndDate", batchCodeEndDate)
        jsonObject.put("updatedBy", login_name)

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
                                    this@TrueValueStockTaking2,
                                    "Stock taking is already completed for this vehicle",
                                    Toast.LENGTH_LONG
                                ).show()
                                clearFields()
                            }
                            responseCode == 200 -> {
                                Toast.makeText(
                                    this@TrueValueStockTaking2,
                                    "Data saved successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                                clearFields()

                                if(jsonBatchStatus.toInt()==0||batchStatus=="Closed"){
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        findBybatchNameOpenStatus()
                                    }, 2000)
                                }
                            }
                            else -> {
                                Toast.makeText(
                                    this@TrueValueStockTaking2,
                                    "Failed to save data. Error code: $responseCode",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@TrueValueStockTaking2,
                            "No response from server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@TrueValueStockTaking2,
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
//        vinNoTextView.text=""
        vinNoTextView.setText("")
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
        save_button2.visibility = View.INVISIBLE
        chassis_no.setText("")
        chassis_no.visibility=View.GONE
        findByChassis.visibility=View.GONE
    }

    private fun backToHome() {
        finish()
    }

    data class chassis_data(
        val VIN: String,
        val LOCATION:String
    )

    data class regNoDataStkTake2(
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
        val COLOUR:String,

        )


}
