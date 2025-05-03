//package com.example.apinew
//
//import android.Manifest
//import java.net.URLEncoder
//import android.annotation.SuppressLint
//import android.app.Activity
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.graphics.Color
//import android.os.Build
//import android.os.Bundle
//import android.os.Handler
//import android.os.Looper
//import android.provider.MediaStore
//import androidx.annotation.RequiresApi
//import android.util.Log
//import android.view.View
//import android.widget.ArrayAdapter
//import android.widget.Button
//import android.widget.EditText
//import android.widget.ImageButton
//import android.widget.Spinner
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.core.text.isDigitsOnly
//import androidx.lifecycle.lifecycleScope
//import com.google.mlkit.vision.common.InputImage
//import com.google.mlkit.vision.text.TextRecognition
//import com.google.mlkit.vision.text.latin.TextRecognizerOptions
//import com.google.zxing.integration.android.IntentIntegrator
//import com.google.zxing.oned.rss.FinderPattern
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import okhttp3.Call
//import okhttp3.Callback
//import okhttp3.HttpUrl
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.RequestBody.Companion.toRequestBody
//import okhttp3.Response
//import org.json.JSONException
//import java.io.IOException
//import java.text.SimpleDateFormat
//import java.util.Calendar
//import java.util.Date
//import java.util.Locale
//import org.json.JSONObject
//import java.util.concurrent.Executors
//import kotlin.math.max
//import kotlin.math.min
//
//class StockTaking : AppCompatActivity() {
//
//    private lateinit var scanButton: Button
//    private lateinit var saveButton: Button
//    private lateinit var VinQr: EditText
//    private lateinit var VinDetails: TextView
//    private lateinit var variantCdTextView: TextView
//    private lateinit var dmsInvNoTextView: TextView
//    private lateinit var chassisNumberTextView: TextView
//    private lateinit var modelCodeTextView: TextView
//    private lateinit var engine2:TextView
//    private lateinit var batchEditText: EditText
//    private  lateinit var multipleBatchNameSpinner: Spinner
//    private lateinit var batchName: TextView
//    private lateinit var addBtn: Button
//    private lateinit var save_button: Button
//    private lateinit var save_button2:Button
//    private lateinit var viewReportsButton: Button
//    private lateinit var login_name: String
//    private  lateinit var deptName: String
//    private lateinit var  location_name:String
//    private lateinit var usernameText:TextView
//    private lateinit var locationText:TextView
//    private lateinit var homepage:ImageButton
//    private lateinit var ouId: String
//    private lateinit var locId: String
//    private lateinit var variant_code:TextView
//    private lateinit var Physical_Location:TextView
//    private lateinit var qr_result_textview:TextView
//    private lateinit var refreshButton:Button
//    private lateinit var fetchChassisDataButton:Button
//    private lateinit var findByprodSrl:ImageButton
//    private lateinit var prodSrlNo:EditText
//    private  lateinit var findBatchNameFn:Button
//    private lateinit var vintypeSpinner:Spinner
//    private lateinit var fetchVinData2:ImageButton
//    private var selectedBatchName: String? = null
//    private lateinit var locName:TextView
//    private lateinit var Department:TextView
//    private lateinit var inNoByMumCom:TextView
//    private val recognizer = TextRecognition.getClient(
//        TextRecognizerOptions.Builder().setExecutor(
//            Executors.newSingleThreadExecutor()
//        ).build()
//    )
//    private lateinit var purchaseInvNoPost:String
//    private lateinit var vehNoTextView:TextView
//    private lateinit var invNoTextView:TextView
//    private lateinit var remarks:TextView
//    private lateinit var fullTextView:TextView
//    private lateinit var assetNoTextView:TextView
//    private lateinit var fetchItemNoData:ImageButton
//    private lateinit var invNoEnter:EditText
//    private lateinit var assetNoEnter:EditText
//    private lateinit var assetNo:TextView
//    private lateinit var TableName:String
//    private lateinit var save_button3:Button
//    private lateinit var descItemName:EditText
//    private lateinit var descUsername:EditText
//    private lateinit var descDepartment:EditText
//
//    //added on 27-11-2024
//    private lateinit var itemNameEditText:EditText
//    private lateinit var itemNameEditTextFetch:ImageButton
//    private lateinit var fetchProdSrlBtn:Button
//    private lateinit var fetchItemNameBtn:Button
//
//    companion object {
//        private const val CAMERA_PERMISSION_CODE = 100
//        private const val CAMERA_REQUEST_CODE = 101
//        private const val CAMERA_REQUEST_CODE_2 = 103
//    }
//
//    @SuppressLint("MissingInflatedId")
//    @RequiresApi(Build.VERSION_CODES.O)
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_stock_taking)
//        scanButton = findViewById(R.id.scan_button)
//        save_button = findViewById(R.id.save_button)
//        save_button2= findViewById(R.id.save_button2)
//        VinQr = findViewById(R.id.vin_qr_edittext)
//        VinDetails = findViewById(R.id.VinDetails)
//        variantCdTextView = findViewById(R.id.variant_cd_textview)
//        dmsInvNoTextView = findViewById(R.id.dms_inv_no_textview)
//        chassisNumberTextView = findViewById(R.id.chassis_number_textview)
//        modelCodeTextView = findViewById(R.id.model_code_textview)
//        locName=findViewById(R.id.locName)
//        Department=findViewById(R.id.Department)
//        inNoByMumCom=findViewById(R.id.inNoByMumCom)
//        vehNoTextView=findViewById(R.id.vehNoTextView)
//        fullTextView=findViewById(R.id.fullTextView)
//        assetNoTextView=findViewById(R.id.assetNoTextView)
//        invNoTextView=findViewById(R.id.invNoTextView)
//        remarks=findViewById(R.id.remarks)
//        batchEditText = findViewById(R.id.batchEditText)
//        multipleBatchNameSpinner = findViewById(R.id.multipleBatchNameSpinner)
//        multipleBatchNameSpinner.visibility = View.GONE
//        batchName = findViewById(R.id.batchNameLabel)
//        addBtn = findViewById(R.id.addBtn)
//        usernameText = findViewById(R.id.usernameText)
//        locationText = findViewById(R.id.locationText)
//        viewReportsButton = findViewById(R.id.viewReportsButton)
//        homepage = findViewById(R.id.homepage)
//        variant_code = findViewById(R.id.variant_code)
//        Physical_Location = findViewById(R.id.Physical_Location)
//        qr_result_textview = findViewById(R.id.qr_result_textview)
//        ouId = intent.getStringExtra("ouId") ?: ""
//        locId = intent.getStringExtra("locId") ?: ""
//        Log.d("locId", locId.toString())
//        Log.d("ouId", ouId.toString())
//        deptName = intent.getStringExtra("deptName") ?: ""
//        location_name = intent.getStringExtra("locName") ?: ""
//        login_name = intent.getStringExtra("login_name") ?: ""
//        VinDetails.visibility = View.GONE
//        save_button.visibility = View.INVISIBLE
//        save_button2.visibility = View.INVISIBLE
//        engine2 = findViewById(R.id.engine2)
//        refreshButton = findViewById(R.id.refreshButton)
//        fetchChassisDataButton = findViewById(R.id.fetchChassisDataButton)
//        prodSrlNo = findViewById(R.id.prodSrlNo)
//        findByprodSrl = findViewById(R.id.findByprodSrl)
//        vintypeSpinner = findViewById(R.id.vintypeSpinner)
//        vintypeSpinner.visibility = View.GONE
//        fetchVinData2 = findViewById(R.id.fetchVinData2)
//        fetchVinData2.visibility = View.GONE
//        fetchItemNoData=findViewById(R.id.fetchItemNoData)
//        invNoEnter=findViewById(R.id.invNoEnter)
//        assetNoEnter=findViewById(R.id.assetNoEnter)
//        assetNo=findViewById(R.id.assetNo)
//        save_button3=findViewById(R.id.save_button3)
//        save_button3.visibility=View.INVISIBLE
//
//        descItemName=findViewById(R.id.descItemName)
//        descUsername=findViewById(R.id.descUsername)
//        descDepartment=findViewById(R.id.descDepartment)
//
//
//        itemNameEditText=findViewById(R.id.itemNameEditText)
//        itemNameEditTextFetch=findViewById(R.id.itemNameEditTextFetch)
//        fetchProdSrlBtn=findViewById(R.id.fetchProdSrlBtn)
//        fetchItemNameBtn=findViewById(R.id.fetchItemNameBtn)
//
//
//        fetchProdSrlBtn.setOnClickListener {
//            prodSrlNo.visibility = View.VISIBLE
//            findByprodSrl.visibility = View.VISIBLE
//            itemNameEditText.visibility=View.INVISIBLE
//            itemNameEditTextFetch.visibility=View.INVISIBLE
//        }
//
//        fetchItemNameBtn.setOnClickListener {
//            itemNameEditText.visibility=View.GONE
////            itemNameEditTextFetch.visibility=View.INVISIBLE
////            prodSrlNo.visibility = View.INVISIBLE
////            findByprodSrl.visibility = View.INVISIBLE
//
//            descItemName.visibility=View.GONE
//            descUsername.visibility=View.VISIBLE
//            descDepartment.visibility=View.GONE
//            save_button3.visibility=View.VISIBLE
//            refreshButton.visibility=View.VISIBLE
//        }
//
//        itemNameEditTextFetch.setOnClickListener {
//            fetchEnteredItemName()
//        }
//        findByprodSrl.setOnClickListener {
//            fetchAssetNoTxt()
////            fetchAssetNoDataFromSpinner()
//        }
//
//
////        prodSrlNo.visibility = View.VISIBLE
////        findByprodSrl.visibility = View.VISIBLE
//
//        prodSrlNo.visibility = View.GONE
//        findByprodSrl.visibility = View.GONE
//        itemNameEditText.visibility=View.GONE
//        itemNameEditTextFetch.visibility=View.GONE
//
//
//        variantCdTextView.visibility = View.GONE
//        dmsInvNoTextView.visibility = View.GONE
//        chassisNumberTextView.visibility = View.GONE
//        modelCodeTextView.visibility = View.GONE
//        variant_code.visibility = View.GONE
//        Physical_Location.visibility = View.GONE
//        engine2.visibility = View.GONE
//        locName.visibility=View.GONE
//        Department.visibility=View.GONE
//        vehNoTextView.visibility=View.GONE
//        invNoTextView.visibility=View.GONE
//        remarks.visibility=View.GONE
//        fullTextView.visibility=View.GONE
//        inNoByMumCom.visibility=View.GONE
//        assetNoTextView.visibility=View.GONE
//        fetchItemNoData.visibility=View.GONE
//        assetNo.visibility=View.GONE
//
//        fetchItemNoData.setOnClickListener {
//            fetchMumComData()
//        }
//
//        findBybatchNameOpenStatus()
//
//
//
//
//        val currentDate: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
//        val currentMonth: String = SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(Date())
//
//
//        usernameText.text = "$login_name"
//        locationText.text = "$location_name"
//
//        batchEditText.setText("$location_name-$currentDate")
////        batchEditText.setText("Borivali(E)-29-07-2024")
//
//        batchEditText.isEnabled = false
//
//        addBtn.setOnClickListener {
//            scanButton.visibility = View.VISIBLE
//            VinDetails.visibility = View.VISIBLE
//            save_button.visibility = View.VISIBLE
//        }
//
//        viewReportsButton.setOnClickListener {
//            stockReports()
//        }
//
//        homepage.setOnClickListener {
//            backToHome()
//        }
//
//        refreshButton = findViewById(R.id.refreshButton)
//        refreshButton.setOnClickListener {
//            resetFields()
//        }
//
//
//
//        fetchChassisDataButton.setOnClickListener {
//            fetchVinData2.visibility=View.VISIBLE
//            vehNoTextView.visibility=View.VISIBLE
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
//
//        scanButton.setOnClickListener {
//            val selectedBatchName = multipleBatchNameSpinner.selectedItem?.toString()
//            val integrator = IntentIntegrator(this)
//            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
//            integrator.setPrompt("Scan a QR Code or Barcode")
//            integrator.setCameraId(0)
//            integrator.setBeepEnabled(true)
//            integrator.setBarcodeImageEnabled(true)
//            integrator.setOrientationLocked(false)
//            integrator.initiateScan()
//            Log.d("currentDate", currentDate)
//            Handler(Looper.getMainLooper()).postDelayed({
////                Toast.makeText(this, "Could not scan.Please try again.", Toast.LENGTH_SHORT).show()
//            }, 10000)
//        }
//
//        fetchVinData2.setOnClickListener {
////            val itemCode = vehNoTextView.text.toString()
////            fetchVinData2(itemCode)
//            fetchAssetNoTxt()
//        }
//
//        save_button.setOnClickListener {
////            val vin = VinQr.text.toString()
//            val vin=dmsInvNoTextView.text.toString().split(": ")[1]
//            val selectedBatchName = multipleBatchNameSpinner.selectedItem?.toString() ?: ""
//            when {
//                selectedBatchName == "Select Batch Name" -> {
//                    Toast.makeText(this,"Please select a Batch Name first", Toast.LENGTH_SHORT)
//                        .show()
//                }
//
//                selectedBatchName.isNotEmpty() -> {
////                    if (vin.isNotEmpty()) {
//                    saveVinData(vin, selectedBatchName)
////                    }
////                else {
////                        Toast.makeText(this, "SR NO is empty", Toast.LENGTH_SHORT).show()
////                    }
//                }
//
//                else -> {
//                    val batchName = batchEditText.text.toString()
////                    if (vin.isNotEmpty()) {
//                    saveVinData(vin, batchName)
////                    }
////                    else {
////                        Toast.makeText(this, "SR NO or Batch Name is empty", Toast.LENGTH_SHORT)
////                            .show()
////                    }
//                }
//            }
//        }
//
//        save_button2.setOnClickListener {
//            val vin=dmsInvNoTextView.text.toString().split(": ")[1]
//            val selectedBatchName = multipleBatchNameSpinner.selectedItem?.toString() ?: ""
//            when {
//                selectedBatchName == "Select Batch Name" -> {
//                    Toast.makeText(this,"Please select a Batch Name first", Toast.LENGTH_SHORT)
//                        .show()
//                }
//
//                selectedBatchName.isNotEmpty() -> {
//                    if (vin.isNotEmpty()) {
//                        saveVinDataCamera(vin, selectedBatchName)
//                    } else {
//                        Toast.makeText(this, "SR NO is empty", Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//                else -> {
//                    val batchName = batchEditText.text.toString()
//                    if (vin.isNotEmpty()) {
//                        saveVinDataCamera(vin, batchName)
//                    } else {
//                        Toast.makeText(this, "SR NO or Batch Name is empty", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//                }
//            }
//        }
//
//        save_button3.setOnClickListener {
//            val itemName=descUsername.text.toString()
//            val selectedBatchName = multipleBatchNameSpinner.selectedItem?.toString() ?: ""
//            when {
//                selectedBatchName == "Select Batch Name" -> {
//                    Toast.makeText(this,"Please select a Batch Name first", Toast.LENGTH_SHORT)
//                        .show()
//                }
//
//                selectedBatchName.isNotEmpty() -> {
//                    if (itemName.isNotEmpty()) {
//                        saveDataDescriptionWise(itemName, selectedBatchName)
//                    } else {
//                        Toast.makeText(this, "Description or username is empty", Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//                else -> {
//                    val batchName = batchEditText.text.toString()
//                    if (itemName.isNotEmpty()) {
//                        saveDataDescriptionWise(itemName, batchName)
//                    } else {
//                        Toast.makeText(this, "Description or Batch Name is empty", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//                }
//            }
//        }
//    }
//
//    private fun findBybatchNameOpenStatus() {
//        val client = OkHttpClient()
//        val request = Request.Builder()
//            .url("${ApiFile.APP_URL}/faBatch/findExBatchNameStatus?locId=$locId")
//            .build()
//
//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                val response = client.newCall(request).execute()
//                val jsonData = response.body?.string()
//                val jsonObject = JSONObject(jsonData.toString())
//                val jsonArray = jsonObject.getJSONArray("obj")
//                val batStatus = jsonArray.getJSONObject(0)
//                val status = batStatus.getString("batchStatus")
//                Log.d("status-----BatchStatus",status)
//
//                runOnUiThread {
//                    if (status.equals("open", ignoreCase = true)) {
//                        Log.d("If Condition-----Open",status)
//                        multipleBatchNameSpinner.visibility = View.VISIBLE
//                        batchEditText.visibility = View.INVISIBLE
//                        fetchBatchData()
//                    }
//                    else if (status.equals("Closed", ignoreCase = true)) {
//                        Log.d("Else If Condition-----Open",status)
//                        batchEditText.visibility = View.VISIBLE
//                        multipleBatchNameSpinner.visibility = View.INVISIBLE
//                        findBybatchNameStatus()
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                runOnUiThread {
////                    Toast.makeText(this@StockTaking, "Failed to fetch batch status", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
//
//
//    private fun findBybatchNameStatus() {
//        val currentDate: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
//        val batchName = ("$location_name-$currentDate")
//
//        val LocationName = "$location_name"
//        Log.d("LocationName----LocationName", LocationName)
//        Log.d("batchName----batchName", batchName)
//        val client = OkHttpClient()
//        val request = Request.Builder()
//            .url("${ApiFile.APP_URL}/faBatch/findBatchNameStatus?batchName=${batchName}&locId=$locId")
//            .build()
//        Log.d("findBybatchNameStatus",request.toString())
//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                val response = client.newCall(request).execute()
//                val jsonData = response.body?.string()
//
//                Log.d("jsonData------", jsonData.toString())
//                val jsonObject = JSONObject(jsonData.toString())
//                val jsonArray = jsonObject.getJSONArray("obj")
//                val batStatus = jsonArray.getJSONObject(0)
//                val batchName = batStatus.getString("batchName")
//                val batchStatus = batStatus.getString("batchStatus")
//                Log.d("batchname",batchName)
//                Log.d("batchStatus",batchStatus)
//                Log.d("jsonDataCheck", jsonArray.toString())
//
//                if (jsonArray.length() == 0) {
//                    Log.d("jsonDataCheckIf", jsonData.toString())
//                    batchEditText.visibility=View.VISIBLE
//                    multipleBatchNameSpinner.visibility= View.INVISIBLE
//                } else {
//                    Log.d("jsonDataCheckelse", jsonData.toString())
//                    batchEditText.visibility= View.GONE
//                    multipleBatchNameSpinner.visibility=View.VISIBLE
//                    fetchBatchData()
//                }
//                Log.d("nss---->", batchName.toString())
//                Log.d("nss2--->", batchEditText.text.toString())
//                if(batchName.toString()==batchEditText.text.toString()&&batchStatus.toString()=="Closed"){
//                    save_button.visibility=View.INVISIBLE
//                    save_button2.visibility = View.INVISIBLE
//                    save_button2.visibility = View.INVISIBLE
//                    Log.d("nss---->", batchName.toString())
//                    Log.d("nss2.0--->", batchStatus.toString())
//                    Log.d("nss2--->", batchEditText.toString())
//                }
//            }
//            catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//
//    private fun fetchBatchData() {
//        val client = OkHttpClient()
//        val request = Request.Builder()
//            .url("${ApiFile.APP_URL}/faBatch/batchNameOpen?locId=${locId}")
//            .build()
//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                val response = client.newCall(request).execute()
//                val jsonData = response.body?.string()
//                Log.d("jsonDataBatchList", jsonData.toString())
//                jsonData?.let {
//                    val batchCodeList = parseCities(it)
//                    Log.d("batchCodeList-----LineNo---491",batchCodeList.toString())
//                    runOnUiThread {
//                        val adapter = ArrayAdapter(
//                            this@StockTaking,
//                            android.R.layout.simple_spinner_item,
//                            batchCodeList
//                        )
//                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                        multipleBatchNameSpinner.adapter = adapter
//                        if (batchCodeList.size > 1) {
//                            runOnUiThread {
//                                multipleBatchNameSpinner.visibility = View.VISIBLE
//                                batchEditText.visibility = View.GONE
//                            }
//                        } else {
//                            runOnUiThread {
//                                multipleBatchNameSpinner.visibility = View.GONE
//                                batchEditText.visibility = View.VISIBLE
//                            }
//                        }
//
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//
//
//
//    private fun parseCities(jsonData: String): List<String> {
//        val batchCodeList = mutableListOf<String>()
//        Log.d("List Checking---",mutableListOf<String>().toString())
//        Log.d("batchList Checking----",batchCodeList.toString())
//        try {
//            val jsonObject = JSONObject(jsonData)
//            val jsonArray = jsonObject.getJSONArray("obj")
//            batchCodeList.add("Select Batch Name")
//            for (i in 0 until jsonArray.length()) {
//                val batchList = jsonArray.getJSONObject(i)
//                val code = batchList.getString("batchName")
//                batchCodeList.add("$code")
//            }
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//        return batchCodeList
//    }
//
//
//
//    private fun openCamera(requestCode: Int) {
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        startActivityForResult(intent, requestCode)
//    }
//
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        when (requestCode) {
//            IntentIntegrator.REQUEST_CODE -> {
//                val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
//                if (result != null) {
//                    if (result.contents == null) {
//                        Toast.makeText(
//                            this@StockTaking,
//                            "No barcode data found",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    } else {
//                        val barcodeContent = result.contents
//
//                        val srNo = extractSerialNumber(barcodeContent)
//                        val invNo = extractInvNumber(barcodeContent)
////                        val assetNo = extractAssetNumber(barcodeContent)
//                        val assetNo = (barcodeContent)
//
//
//                        remarks.text = barcodeContent
//
//                        if (assetNo != null) {
//                            assetNoTextView.visibility = View.VISIBLE
//                            assetNoTextView.text = "ASSET NO.: $assetNo"
//                        } else {
//                            Toast.makeText(
//                                this@StockTaking,
//                                "ASSET NO not present",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//
//                        if (invNo != null) {
//                            invNoTextView.visibility = View.VISIBLE
//                            invNoTextView.text = "INVOICE NO.: $invNo"
//                        } else {
//                            Toast.makeText(
//                                this@StockTaking,
//                                "INVOICE NO not present",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//
//                        if (srNo != null) {
//                            qr_result_textview.visibility = View.VISIBLE
//                            invNoTextView.visibility = View.GONE
//                            qr_result_textview.text = srNo
//                            VinQr.setText(srNo)
//                        } else {
//                            Toast.makeText(
//                                this@StockTaking,
//                                "SR NO not present",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//
//                        // Priority search- Check assetNo first, then invNo, then srNo
//                        if (assetNo != null) {
//                            fetchVinData(
//                                assetNo,
//                                "",
//                                ""
//                            )
//                            Toast.makeText(
//                                this@StockTaking,
//                                "Search on asset No",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        } else if (invNo != null) {
//                            fetchVinData(
//                                "",
//                                invNo,
//                                ""
//                            )
//                            Toast.makeText(
//                                this@StockTaking,
//                                "Search on Inv No",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        } else if (srNo != null) {
//                            fetchVinData(
//                                "",
//                                "",
//                                srNo
//                            )
//                            Toast.makeText(
//                                this@StockTaking,
//                                "Search on sr No",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        } else {
//                            Toast.makeText(
//                                this@StockTaking,
//                                "SR NO, INV NO, and ASSET NO are not present",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
//                }
//            }
//
//            CAMERA_REQUEST_CODE, CAMERA_REQUEST_CODE_2 -> {
//                if (resultCode == Activity.RESULT_OK) {
//                    val imageBitmap = data?.extras?.get("data") as Bitmap
//                    when (requestCode) {
//                        CAMERA_REQUEST_CODE -> {
//                            vehNoTextView.visibility=View.VISIBLE
//                            processImageWithMultipleAttempts(imageBitmap, vehNoTextView)
////                            val itemCode = vehNoTextView.toString()
//                            val itemCode = vehNoTextView.text.toString()
//                            fetchVinData2(itemCode)
//
//                            Log.d("bestResult2", itemCode)
//                            if (itemCode.isNotEmpty()) {
//                                Toast.makeText(
//                                    this@StockTaking,
//                                    "ITEM CODE->$itemCode",
//                                    Toast.LENGTH_SHORT
//                                ).show()
////                                fetchVinData2(itemCode)
//                            } else {
//                                Toast.makeText(
//                                    this@StockTaking,
//                                    "Failed to extract a valid SR number",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                    }
//                }
//            }
//            else -> {
//                super.onActivityResult(requestCode, resultCode, data)
//            }
//        }
//    }
//
//    private fun extractInvNumber(barcodeContent: String): String? {
//        val invNoRegex = """(INV\s+NO|INVOICE\s+NO|INVOICE\s+NO.|Inv\s+No-)\s*([\w\/\-\.]+)""".toRegex()
//        val matchResult = invNoRegex.find(barcodeContent)
//        return matchResult?.groupValues?.get(2)
//    }
//
//    private fun extractSerialNumber(barcodeContent: String): String? {
//        val srNoRegex = """SR\s+NO\s+-\s+(\w+)""".toRegex()
//        val matchResult = srNoRegex.find(barcodeContent)
//        return matchResult?.groupValues?.get(1)
//    }
//
////    private fun extractAssetNumber(barcodeContent: String): String? {
////        val assetNoRegex = """ASSET\s+NO\s+(\d{5})|ASSET\s+NO.\s+(\d{5})|ASSET\s+NO-\s+(\d{5}|ASSET\s+NO:-\s+(\d{5})|ASSETNO\s+(\d{5})|\b(\d{5})\b)""".toRegex()
//////        val assetNoRegex = """""".toRegex()
////        val matchResult = assetNoRegex.find(barcodeContent)
////        return matchResult?.groupValues?.get(1)
////    }
//
//    fun extractAssetNumber(barcodeContent: String): String? {
//        val regex = Regex("\\b\\d{5}\\b")
//        return regex.find(barcodeContent)?.value
//    }
//
//
//    private fun processImageWithMultipleAttempts(originalBitmap: Bitmap, resultTextView: TextView) {
//        val attempts = listOf(
//            { preprocessImage(originalBitmap) },
//            { preprocessImage(resizeImage(originalBitmap)) },
//            { resizeImage(originalBitmap) }
//        )
//
//        val results = mutableListOf<String>()
//        var fullText = "" // Store only the first full text
//        var hasValidResult = false // Flag to stop after first valid result
//
//        attempts.forEachIndexed { index, preprocessor ->
//            if (hasValidResult) return@forEachIndexed // Stop if a valid result is found
//
//            val processedBitmap = preprocessor()
//            val image = InputImage.fromBitmap(processedBitmap, 0)
//
//            recognizer.process(image)
//                .addOnSuccessListener { visionText ->
//                    if (fullText.isEmpty()) {
//                        fullText = visionText.text // Store only the first full text
//                    }
//
//                    val extractedText = extractDesiredText(visionText.text)
//                    if (extractedText.isNotEmpty()) {
//                        results.add(extractedText)
//                        hasValidResult = true
//                    }
//
//
//                    if (index == attempts.lastIndex || hasValidResult) {
//                        displayBestResult(results, resultTextView)
//                        displayFullText(fullText)
//                    }
//                }
//                .addOnFailureListener { e ->
//                    e.printStackTrace()
//                    if (index == attempts.lastIndex && results.isEmpty()) {
//                        Toast.makeText(this, "Text recognition failed", Toast.LENGTH_SHORT).show()
//                    }
//                }
//        }
//    }
//
//
//    private fun displayFullText(fullText: String) {
//        runOnUiThread {
//            fullTextView.text = fullText
//        }
//    }
//
//
//    private fun extractDesiredText(text: String): String {
//
//        val processedText = text.mapIndexed { index, char ->
//            when {
//                text.length==11 && index in 7..10 && char == 'O' -> '0'
//                text.length==12 && index in 8..11 && char == 'O' -> '0'
//                else -> char
//            }
//        }.joinToString("")
//
//        val regexList = listOf(
////            Regex("MUM-\\w{3}\\d{4}"),
////            Regex("MUM-\\w{3}\\d{2}"),
////            Regex("MUM-\\w{3}00\\d{2}"),
////            Regex("MUM-\\w{3}-00\\d{2}"),
////            Regex("MUM-\\w{3}-\\d{4}"),
////            Regex("PUN-\\w{3}-\\d{4}"),
////            Regex("PUN-\\w{3}\\d{4}"),
////            Regex("PUN-\\w{3}00\\d{2}"),
////            Regex("PUN-\\w{3}-00\\d{2}"),
////            Regex("HYD-\\w{3}-\\d{4}"),
////            Regex("HYD-\\w{3}\\d{4}"),
////            Regex("HYD-\\w{3}00\\d{2}"),
////            Regex("HYD-\\w{3}-00\\d{2}"),
////            Regex("KOC-\\w{3}\\d{4}"),
////            Regex("KOC-\\w{3}-\\d{4}"),
////            Regex("KOC-\\w{3}-00\\d{2}"),
////            Regex("KOC-\\w{3}00\\d{2}"),
////            Regex("GOA-\\w{3}-\\d{4}"),
////            Regex("GOA-\\w{3}\\d{4}"),
////            Regex("GOA-\\w{3}-00\\d{2}"),
////            Regex("GOA-\\w{3}00\\d{2}"),
////            Regex("KLP-\\w{3}-\\d{4}"),
////            Regex("KLP-\\w{3}\\d{4}"),
////            Regex("KLP-\\w{3}-00\\d{2}"),
////            Regex("KLP-\\w{3}00\\d{2}")
//            Regex("\\d{5}"),
//            Regex("\\d{4}")
//
//
//        )
//
//        for (regex in regexList) {
//            val matchResult = regex.find(processedText)
//            if (matchResult != null) {
//                return matchResult.value
//            }
//        }
//        return ""
//    }
//
//
//
//    private fun displayBestResult(results: List<String>, resultTextView: TextView) {
//        val bestResult2 = results.firstOrNull { it.isNotEmpty() } ?: ""
//        vehNoTextView.text=bestResult2
//        runOnUiThread {
//            resultTextView.text = bestResult2
////            if (bestResult2.isNotEmpty()) {
//////                Toast.makeText(this, "Text recognized: $bestResult2", Toast.LENGTH_SHORT).show()
////            } else {
////                Toast.makeText(this, "Desired text format not found", Toast.LENGTH_SHORT).show()
////            }
//        }
//    }
//
//
//    private fun preprocessImage(bitmap: Bitmap): Bitmap {
//        val width = bitmap.width
//        val height = bitmap.height
//        val processedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//
//        for (y in 0 until height) {
//            for (x in 0 until width) {
//                val pixel = bitmap.getPixel(x, y)
//                val red = (pixel shr 16) and 0xFF
//                val green = (pixel shr 8) and 0xFF
//                val blue = pixel and 0xFF
//                var gray = (0.299 * red + 0.587 * green + 0.114 * blue).toInt()
//
//                gray = if (gray > 128) min(255, gray + 30) else max(0, gray - 30)
//
//                processedBitmap.setPixel(x, y, Color.rgb(gray, gray, gray))
//            }
//        }
//
//        return processedBitmap
//    }
//
//    private fun resizeImage(bitmap: Bitmap, targetWidth: Int = 1000): Bitmap {
//        val aspectRatio = bitmap.width.toDouble() / bitmap.height.toDouble()
//        val targetHeight = (targetWidth / aspectRatio).toInt()
//        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
//    }
//
//
//    private fun resetFields() {
//        itemNameEditText.visibility=View.GONE
//        itemNameEditTextFetch.visibility=View.GONE
//        prodSrlNo.visibility = View.GONE
//        findByprodSrl.visibility = View.GONE
//        itemNameEditText.setText("")
//        prodSrlNo.setText("")
//        inNoByMumCom.visibility=View.GONE
//        inNoByMumCom.text=""
//        assetNo.visibility=View.GONE
//        assetNo.text=""
//        assetNoTextView.visibility=View.GONE
//        assetNoTextView.text=""
//        invNoTextView.visibility=View.GONE
//        invNoTextView.text=""
//        fullTextView.visibility=View.GONE
//        fullTextView.text=""
//        remarks.visibility=View.GONE
//        remarks.text=""
//        prodSrlNo.setText("")
//        vintypeSpinner.visibility=View.GONE
//        vehNoTextView.text = ""
//        Department.text=""
//        locName.text=""
//        locName.visibility=View.GONE
//        Department.visibility=View.GONE
//        vehNoTextView.visibility=View.GONE
//        qr_result_textview.text=""
//        VinDetails.visibility=View.INVISIBLE
//        VinDetails.text=""
//        variantCdTextView.text=""
//        dmsInvNoTextView.text=""
//        chassisNumberTextView.text=""
//        findByprodSrl.visibility=View.INVISIBLE
//        prodSrlNo.visibility=View.GONE
//        modelCodeTextView.text=""
//        variant_code.text=""
//        Physical_Location.text=""
//        engine2.text=""
//        variantCdTextView.visibility=View.INVISIBLE
//        dmsInvNoTextView.visibility=View.INVISIBLE
//        chassisNumberTextView.visibility=View.INVISIBLE
//        modelCodeTextView.visibility=View.INVISIBLE
//        variant_code.visibility=View.INVISIBLE
//        Physical_Location.visibility=View.INVISIBLE
//        engine2.visibility=View.INVISIBLE
//        save_button.visibility=View.INVISIBLE
//        save_button2.visibility=View.INVISIBLE
//        save_button3.visibility=View.INVISIBLE
//        vintypeSpinner.setSelection(0)
//        fetchVinData2.visibility=View.INVISIBLE
//        fetchItemNoData.visibility=View.GONE
//        descItemName.setText("")
//        descUsername.setText("")
//        descDepartment.setText("")
//        descItemName.visibility=View.GONE
//        descUsername.visibility=View.GONE
//        descDepartment.visibility=View.GONE
//    }
//
//
//// For Request Parameter
////    private fun fetchVinData(srNo2: String) {
//////private fun fetchVinData() {
//////    val prodSrlNo = prodSrlNo.text.toString()
//////    val assetNo = assetNoEnter.text.toString()
//////    val invNo = invNoEnter.text.toString()
////
////    val client = OkHttpClient()
////
////    val urlBuilder = StringBuilder(ApiFile.APP_URL + "/faBatch/detailsByProdSrl?prodSrlNo=&assetNo=$srNo2&invNo=")
////
////    val url = urlBuilder.toString()
////    Log.d("URL:", url)
////
////    val request = Request.Builder().url(url).build()
////
////    GlobalScope.launch(Dispatchers.IO) {
////        try {
////            val response = client.newCall(request).execute()
////            val jsonData = response.body?.string()
////            jsonData?.let {
////                val jsonObject = JSONObject(it)
////                val stockItems = jsonObject.getJSONArray("obj")
////
////                if (stockItems.length() > 0) {
////                    val itemCodes = mutableListOf<String>()
////
////                    for (i in 0 until stockItems.length()) {
////                        val stockItem = stockItems.getJSONObject(i)
////
////                        val itemCode = if (stockItem.has("itemCode")) {
////                            stockItem.getString("itemCode")
////                        } else {
////                            stockItem.optString("assetNo", "N/A")
////                        }
////
////                        itemCodes.add(itemCode)
////                    }
////
////                    runOnUiThread {
////                        populateSpinnerWithMultipleItemCodes(itemCodes)
////                    }
////                } else {
////                    runOnUiThread {
////                        Toast.makeText(this@StockTaking, "No items found", Toast.LENGTH_SHORT).show()
////                    }
////                }
////            }
////        } catch (e: Exception) {
////            e.printStackTrace()
////            runOnUiThread {
////                Toast.makeText(this@StockTaking, "Failed to fetch details", Toast.LENGTH_SHORT).show()
////            }
////        }
////    }
////}
//
//
//
//
////// For Request Parameter
//
//
//
//    private fun fetchVinData(assetNo: String, invNo: String, prodSrlNo: String) {
//
//        val client = OkHttpClient()
//
//        val urlBuilder = StringBuilder(ApiFile.APP_URL + "/faBatch/detailsByProdSrl?")
//
//        if (assetNo.isNotEmpty()) {
//            urlBuilder.append("prodSrlNo=&assetNo=$assetNo&invNo=")
//        }
//        else if (invNo.isNotEmpty()) {
//            urlBuilder.append("prodSrlNo=&assetNo=&invNo=$invNo")
//        }
//        else if (prodSrlNo.isNotEmpty()) {
//            urlBuilder.append("prodSrlNo=$prodSrlNo&assetNo=&invNo=")
//        }
//        else {
//            Log.d("FetchVinData", "No parameters provided for the request")
//            return
//        }
//
//        val url = urlBuilder.toString()
//        Log.d("URL:", url)
//
//        val request = Request.Builder().url(url).build()
//
//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                val response = client.newCall(request).execute()
//                val jsonData = response.body?.string()
//                jsonData?.let {
//                    val jsonObject = JSONObject(it)
//                    val stockItems = jsonObject.getJSONArray("obj")
//
//                    if (stockItems.length() > 0) {
//                        val itemCodes = mutableListOf<String>()
//
//                        for (i in 0 until stockItems.length()) {
//                            val stockItem = stockItems.getJSONObject(i)
//
//                            val itemCode = if (stockItem.has("itemCode")) {
//                                stockItem.getString("itemCode")
//                            } else {
//                                stockItem.optString("assetNo", "N/A")
//                            }
//
//                            itemCodes.add(itemCode)
//                        }
//
//                        runOnUiThread {
//                            populateSpinnerWithMultipleItemCodes(itemCodes)
//                        }
//                    } else {
//                        runOnUiThread {
//                            Toast.makeText(this@StockTaking, "No items found", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                runOnUiThread {
//                    Toast.makeText(this@StockTaking, "Failed to fetch details", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
//
//
//    private fun fetchAssetNoDataFromSpinner() {
//        val assetNumber=prodSrlNo.text.toString()
//
//        val client = OkHttpClient()
//
//        val urlBuilder = ApiFile.APP_URL + "/faBatch/detailsByProdSrl?prodSrlNo=&assetNo=$assetNumber&invNo="
//
//        val url = urlBuilder
//        Log.d("URL:", url)
//
//        val request = Request.Builder().url(url).build()
//
//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                val response = client.newCall(request).execute()
//                val jsonData = response.body?.string()
//                jsonData?.let {
//                    val jsonObject = JSONObject(it)
//                    val stockItems = jsonObject.getJSONArray("obj")
//
//                    if (stockItems.length() > 0) {
//                        val itemCodes = mutableListOf<String>()
//
//                        for (i in 0 until stockItems.length()) {
//                            val stockItem = stockItems.getJSONObject(i)
//
//                            val itemCode = if (stockItem.has("itemCode")) {
//                                stockItem.getString("itemCode")
//                            } else {
//                                stockItem.optString("assetNo", "N/A")
//                            }
//
//                            itemCodes.add(itemCode)
//                        }
//
//                        runOnUiThread {
//                            populateSpinnerWithMultipleItemCodes(itemCodes)
//                        }
//                    } else {
//                        runOnUiThread {
//                            Toast.makeText(this@StockTaking, "No items found", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                runOnUiThread {
//                    Toast.makeText(this@StockTaking, "Failed to fetch details", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
//
//
//    private fun fetchAssetNoTxt() {
////        val prodSrlNoEditTxt=prodSrlNo.text.toString()
////        val prodSrlNoEditTxt=vehNoTextView.text.toString()
//        val prodSrlNoEditTxt=prodSrlNo.text.toString()
//        val client = OkHttpClient()
//        val url =ApiFile.APP_URL+"/faBatch/detailsByProdSrl?prodSrlNo=&assetNo=$prodSrlNoEditTxt&invNo="
//        Log.d("URL:", url)
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
//                    val vinData = itInventory(
//                        userDesigntn = stockItem.optString("userDesigntn","-"),
//                        locName = stockItem.optString("locName","-"),
//                        itemType = stockItem.optString("itemType","-"),
//                        itemTypeId = stockItem.optString("itemTypeId","-"),
//                        itemCode = stockItem.optString("itemCode","-"),
//                        userName = stockItem.optString("userName","-"),
//                        Department = stockItem.optString("Department","-"),
//                        city = stockItem.optString("city","-"),
//                        productserialNo = stockItem.optString("productserialNo","-"),
//                        itemsubType = stockItem.optString("itemsubType","-"),
//                        ouCity = stockItem.optString("ouCity","-"),
//                        location = stockItem.optString("location","-"),
//                        productMake = stockItem.optString("productMake","-"),
//                        dept = stockItem.optString("dept","-"),
//                        purchaseInvNo=stockItem.optString("purchaseInvNo","-"),
//                        assetNo=stockItem.optString("assetNo","-"),
//                        tableName=stockItem.optString("tableName","-")
//                    )
//                    runOnUiThread {
//                        populateFields(vinData)
//                        VinDetails.visibility = View.GONE
//                        refreshButton.visibility=View.VISIBLE
//                        Toast.makeText(
//                            this@StockTaking,
//                            "Details found Successfully \n for Asset No: $prodSrlNoEditTxt",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        VinDetails.visibility=View.VISIBLE
//                        vintypeSpinner.visibility=View.GONE
//                        VinDetails.text="Details by Asset Number"
//                        save_button.visibility=View.INVISIBLE
//                        save_button2.visibility=View.VISIBLE
//                        save_button3.visibility=View.INVISIBLE
//                        variantCdTextView.visibility=View.VISIBLE
//                        dmsInvNoTextView.visibility=View.VISIBLE
//                        chassisNumberTextView.visibility=View.VISIBLE
//                        modelCodeTextView.visibility=View.VISIBLE
//                        variant_code.visibility=View.VISIBLE
//                        Physical_Location.visibility=View.VISIBLE
//                        engine2.visibility=View.VISIBLE
//                        locName.visibility=View.VISIBLE
//                        Department.visibility=View.VISIBLE
//                        refreshButton.visibility=View.VISIBLE
//                        inNoByMumCom.visibility=View.VISIBLE
//                        assetNo.visibility=View.VISIBLE
//                        remarks.visibility=View.GONE
//                        fullTextView.visibility=View.GONE
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                runOnUiThread {
//                    Toast.makeText(
//                        this@StockTaking,
//                        "Failed to fetch details for Asset No: $prodSrlNoEditTxt",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//    }
//
//
//    private fun populateSpinnerWithMultipleItemCodes(itemCodes: List<String>) {
//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, itemCodes)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        vintypeSpinner.adapter = adapter
//        vintypeSpinner.visibility = View.VISIBLE
//        fetchItemNoData.visibility=View.VISIBLE
//    }
//
//    private fun fetchVinData2(srNo2: String) {
//        val client = OkHttpClient()
//        val itemCode = vehNoTextView.text.toString()
//        val url =ApiFile.APP_URL+"/faBatch/detailsByItemName?itemName=$itemCode"
//        Log.d("URL:", url)
//        val request = Request.Builder()
//            .url(url)
//            .build()
//        Log.d("bestResult2:", srNo2)
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
//                    val vinData = itInventory(
//                        userDesigntn = stockItem.getString("userDesigntn"),
//                        locName = stockItem.getString("locName"),
//                        itemType = stockItem.getString("itemType"),
//                        itemTypeId = stockItem.getString("itemTypeId"),
//                        itemCode = stockItem.getString("itemCode"),
//                        userName = stockItem.getString("userName"),
//                        Department = stockItem.getString("Department"),
//                        city = stockItem.getString("city"),
//                        productserialNo = stockItem.getString("productserialNo"),
//                        itemsubType = stockItem.getString("itemsubType"),
//                        ouCity = stockItem.getString("ouCity"),
//                        location = stockItem.getString("location"),
//                        productMake = stockItem.getString("productMake"),
//                        dept = stockItem.getString("dept"),
//                        purchaseInvNo=stockItem.getString("purchaseInvNo"),
//                        assetNo=stockItem.getString("assetNo"),
//                        tableName=stockItem.getString("tableName")
//                    )
//                    runOnUiThread {
//                        populateFields(vinData)
//                        VinDetails.visibility = View.GONE
//                        refreshButton.visibility=View.VISIBLE
//                        Toast.makeText(
//                            this@StockTaking,
//                            "Details found Successfully \n for item No: $itemCode",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        VinDetails.visibility=View.VISIBLE
//                        save_button.visibility=View.INVISIBLE
//                        save_button2.visibility=View.VISIBLE
//                        save_button3.visibility=View.INVISIBLE
//                        variantCdTextView.visibility=View.VISIBLE
//                        dmsInvNoTextView.visibility=View.VISIBLE
//                        chassisNumberTextView.visibility=View.VISIBLE
//                        modelCodeTextView.visibility=View.VISIBLE
//                        variant_code.visibility=View.VISIBLE
//                        Physical_Location.visibility=View.VISIBLE
//                        engine2.visibility=View.VISIBLE
//                        locName.visibility=View.VISIBLE
//                        Department.visibility=View.VISIBLE
//                        refreshButton.visibility=View.VISIBLE
//                        inNoByMumCom.visibility=View.VISIBLE
//                        assetNo.visibility=View.VISIBLE
//                        remarks.visibility=View.VISIBLE
//                        fullTextView.visibility=View.VISIBLE
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                runOnUiThread {
//                    Toast.makeText(
//                        this@StockTaking,
//                        "Failed to fetch details for SR NO: $itemCode",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//    }
//
//
//    private fun fetchEnteredItemName() {
//        val client = OkHttpClient()
//        val itemName = itemNameEditText.text.toString()
//        val url =ApiFile.APP_URL+"/faBatch/detailsByItemName?itemName=$itemName"
//        Log.d("URL:", url)
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
//                    val vinData = itInventory(
//                        userDesigntn = stockItem.getString("userDesigntn"),
//                        locName = stockItem.getString("locName"),
//                        itemType = stockItem.getString("itemType"),
//                        itemTypeId = stockItem.getString("itemTypeId"),
//                        itemCode = stockItem.getString("itemCode"),
//                        userName = stockItem.getString("userName"),
//                        Department = stockItem.getString("Department"),
//                        city = stockItem.getString("city"),
//                        productserialNo = stockItem.getString("productserialNo"),
//                        itemsubType = stockItem.getString("itemsubType"),
//                        ouCity = stockItem.getString("ouCity"),
//                        location = stockItem.getString("location"),
//                        productMake = stockItem.getString("productMake"),
//                        dept = stockItem.getString("dept"),
//                        purchaseInvNo=stockItem.getString("purchaseInvNo"),
//                        assetNo=stockItem.getString("assetNo"),
//                        tableName=stockItem.getString("tableName")
//                    )
//                    runOnUiThread {
//                        populateFields(vinData)
//                        VinDetails.visibility = View.GONE
//                        refreshButton.visibility=View.VISIBLE
//                        Toast.makeText(
//                            this@StockTaking,
//                            "Details found Successfully \n for item No: $itemName",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        VinDetails.visibility=View.VISIBLE
//                        save_button.visibility=View.INVISIBLE
//                        save_button2.visibility=View.VISIBLE
//                        save_button3.visibility=View.INVISIBLE
//                        variantCdTextView.visibility=View.VISIBLE
//                        dmsInvNoTextView.visibility=View.VISIBLE
//                        chassisNumberTextView.visibility=View.VISIBLE
//                        modelCodeTextView.visibility=View.VISIBLE
//                        variant_code.visibility=View.VISIBLE
//                        Physical_Location.visibility=View.VISIBLE
//                        engine2.visibility=View.VISIBLE
//                        locName.visibility=View.VISIBLE
//                        Department.visibility=View.VISIBLE
//                        refreshButton.visibility=View.VISIBLE
//                        inNoByMumCom.visibility=View.VISIBLE
//                        assetNo.visibility=View.VISIBLE
//                        remarks.visibility=View.GONE
//                        fullTextView.visibility=View.GONE
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                runOnUiThread {
//                    Toast.makeText(
//                        this@StockTaking,
//                        "Failed to fetch details for SR NO: $itemName",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//    }
//
//
//    private fun fetchMumComData() {
//        val itemCode=vintypeSpinner.selectedItem.toString()
//        val encodedItemCode = URLEncoder.encode(itemCode, "UTF-8")
//        val client = OkHttpClient()
//        val url =ApiFile.APP_URL+"/faBatch/detailsByMulValues?code=$encodedItemCode"
//
//        Log.d("URL:", url)
//
//        val request = Request.Builder()
//            .url(url)
//            .build()
//        Log.d("srNo:", itemCode)
//
//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                val response = client.newCall(request).execute()
//                val jsonData = response.body?.string()
//                jsonData?.let {
//                    val jsonObject = JSONObject(it)
//                    Log.d("Data", jsonObject.toString())
//                    val stockItem = jsonObject.getJSONArray("obj").getJSONObject(0)
//
//                    val vinData = itInventory(
//                        userDesigntn = stockItem.getString("userDesigntn"),
//                        locName = stockItem.getString("locName"),
//                        itemType = stockItem.getString("itemType"),
//                        itemTypeId = stockItem.getString("itemTypeId"),
//                        itemCode = stockItem.getString("itemCode"),
//                        userName = stockItem.getString("userName"),
//                        Department = stockItem.getString("Department"),
//                        city = stockItem.getString("city"),
//                        productserialNo = stockItem.getString("productserialNo"),
//                        itemsubType = stockItem.getString("itemsubType"),
//                        ouCity = stockItem.getString("ouCity"),
//                        location = stockItem.getString("location"),
//                        productMake = stockItem.getString("productMake"),
//                        dept = stockItem.getString("dept"),
//                        purchaseInvNo=stockItem.getString("purchaseInvNo"),
//                        assetNo=stockItem.getString("assetNo"),
//                        tableName=stockItem.getString("tableName")
//                    )
//                    runOnUiThread {
//                        populateFields(vinData)
//                        qr_result_textview.visibility=View.VISIBLE
//                        VinDetails.visibility = View.GONE
//                        refreshButton.visibility=View.VISIBLE
//                        Toast.makeText(
//                            this@StockTaking,
//                            "Details found Successfully \n for Item No.: $encodedItemCode",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        VinDetails.visibility=View.GONE
//                        save_button.visibility=View.VISIBLE
//                        save_button2.visibility=View.INVISIBLE
//                        save_button3.visibility=View.INVISIBLE
//                        variantCdTextView.visibility=View.VISIBLE
//                        dmsInvNoTextView.visibility=View.VISIBLE
//                        chassisNumberTextView.visibility=View.VISIBLE
//                        modelCodeTextView.visibility=View.VISIBLE
//                        variant_code.visibility=View.VISIBLE
//                        Physical_Location.visibility=View.VISIBLE
//                        engine2.visibility=View.VISIBLE
//                        locName.visibility=View.VISIBLE
//                        Department.visibility=View.VISIBLE
//                        refreshButton.visibility=View.VISIBLE
//                        inNoByMumCom.visibility=View.VISIBLE
//                        assetNo.visibility=View.VISIBLE
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                runOnUiThread {
//                    Toast.makeText(
//                        this@StockTaking,
//                        "Failed to fetch details for Item Code: $itemCode",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//    }
//
//    private fun stockReports() {
//        val intent = Intent(this, ViewReportsActivity::class.java)
//        intent.putExtra("login_name", login_name)
//        intent.putExtra("locId", locId)
//        startActivity(intent)
//    }
//
//    private fun populateFields(invData: itInventory) {
//        variantCdTextView.text = " USERNAME.: ${invData.userName}"
//        dmsInvNoTextView.text = "PRODUCT SR.NO.: ${invData.productserialNo}"
//        chassisNumberTextView.text = "ITEM CODE: ${invData.itemCode}"
//        modelCodeTextView.text = "ITEM TYPE: ${invData.itemType}"
//        variant_code.text="ITEM SUB TYPE: ${invData.itemsubType}"
//        Physical_Location.text="PRODUCT MAKE: ${invData.productMake}"
//        engine2.text="USER DESIGNATION: ${invData.userDesigntn}"
//        locName.text="LOCATION: ${invData.locName}"
//        Department.text="DEPARTMENT: ${invData.Department}"
//        inNoByMumCom.text="INV NO.: ${invData.purchaseInvNo}"
//        assetNo.text="ASSET NO.: ${invData.assetNo}"
//        TableName=invData.tableName
//        remarks.visibility = View.VISIBLE
//        fullTextView.visibility = View.VISIBLE
//        purchaseInvNoPost="${invData.itemTypeId}"
//    }
//
//    private fun saveVinData(vin: String, batchName: String) {
//        Log.d("BatchName", batchName)
//        Log.d("vinIn", vin)
//        val tableName=TableName
//        val assetId=chassisNumberTextView.text.toString().split(": ")[1]
////        val assetIdToInt=assetId.toInt()
//        val assetIdToIntOrString = if (assetId.isDigitsOnly()) {
//            assetId.toInt()
//        } else {
//            assetId
//        }
//        val remarks=remarks.text.toString()
//        val client = OkHttpClient()
//        val url = ApiFile.APP_URL + "/faBatch/faBatchNameScan"
//        val jsonObject = JSONObject()
//        val currentDateTime = Calendar.getInstance()
//        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
//        val formattedDate = formatter.format(currentDateTime.time)
//        Log.d("formattedDate", formattedDate)
//
//        currentDateTime.add(Calendar.DAY_OF_MONTH, 1)
//        val formatterForEndDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
//        val batchCodeEndDate = formatterForEndDate.format(currentDateTime.time)
//        Log.d("batchCodeEndDate", batchCodeEndDate)
//        Log.d("TableName--->.>",TableName)
//        jsonObject.put("userName", variantCdTextView.text.toString().split(": ")[1])
//        jsonObject.put("itemName", chassisNumberTextView.text.toString().split(": ")[1])
//        jsonObject.put("assetId",assetIdToIntOrString)
//        jsonObject.put("assetNo", assetNo.text.toString().split(": ")[1])
//        jsonObject.put("prodInvNo",inNoByMumCom.text.toString().split(": ")[1])
//        jsonObject.put("attribute5", tableName)
//        jsonObject.put("createdBy", login_name)
////        jsonObject.put("prodSrlNo",vin)
//        jsonObject.put("prodSrlNo", vin.ifEmpty { purchaseInvNoPost })
//        jsonObject.put("lastUpdatedBy", login_name)
//        jsonObject.put("batchEndDate", batchCodeEndDate)
//        jsonObject.put("locId", locId)
//        jsonObject.put("ouId", ouId)
//        jsonObject.put("insertedBy",login_name)
//        jsonObject.put("batchName", batchName)
//        jsonObject.put("batchStatus", "open")
//        jsonObject.put("batchCreationDate", formattedDate)
//        jsonObject.put("prodInvNo",inNoByMumCom.text.toString().split(": ")[1])
//        jsonObject.put("remarks",remarks)
//
//        Log.d("createdBy--->",login_name)
//        Log.d("prodInvNo--->",inNoByMumCom.text.toString().split(": ")[1])
//        Log.d("jsonObject", jsonObject.toString())
//
//        val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
//        Log.d("requestBody",requestBody.toString())
//
//
//        val request = Request.Builder()
//            .url(url)
//            .post(requestBody)
//            .build()
//
//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                val response = client.newCall(request).execute()
//                val responseCode = response.code
//                val responseBody = response.body?.string()
//
//                Log.d("SaveVinData", "Response Code: $responseCode")
//                Log.d("SaveVinData", "Response Body: $responseBody")
//
//                runOnUiThread {
//                    if (responseBody != null) {
//                        val jsonObject = JSONObject(responseBody)
//                        val message = jsonObject.optString("message", "")
//
//                        when {
//                            responseCode ==400 || message.contains("All assets against this asset ID have been stocked successfully.", ignoreCase = true) -> {
//                                Toast.makeText(
//                                    this@StockTaking,
//                                    "All assets against this asset ID have been stocked successfully.",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                clearFields()
//                            }
//                            responseCode == 200 -> {
//                                Toast.makeText(
//                                    this@StockTaking,
//                                    "Asset Stock Taken Successfully",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                clearFields()
//                            }
//                            else -> {
//                                Toast.makeText(
//                                    this@StockTaking,
//                                    "Failed to save data. Error code: $responseCode",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                    } else {
//                        Toast.makeText(
//                            this@StockTaking,
//                            "No response from server",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Log.e("SaveVinData", "Error: ${e.message}")
//                runOnUiThread {
//                    Toast.makeText(
//                        this@StockTaking,
//                        "Error saving data: ${e.message}",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//    }
//
//    private fun saveVinDataCamera(vin: String, batchName: String) {
//        Log.d("BatchName", batchName)
//        Log.d("vinIn", vin)
//        val assetId=chassisNumberTextView.text.toString().split(": ")[1]
////        val assetIdToInt=assetId.toInt()
//        val assetIdToIntOrString = if (assetId.isDigitsOnly()) {
//            assetId.toInt()
//        } else {
//            assetId
//        }
//        val tableName=TableName
//        val remarks=fullTextView.text.toString()
//
//        val client = OkHttpClient()
//        val url = ApiFile.APP_URL + "/faBatch/faBatchNameManual"
//        val jsonObject = JSONObject()
//
//        val currentDateTime = Calendar.getInstance()
//        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
//        val formattedDate = formatter.format(currentDateTime.time)
//        Log.d("formattedDate", formattedDate)
//
//        currentDateTime.add(Calendar.DAY_OF_MONTH, 1)
//        val formatterForEndDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
//        val batchCodeEndDate = formatterForEndDate.format(currentDateTime.time)
//        Log.d("batchCodeEndDate", batchCodeEndDate)
//        Log.d("TableName--->.>",TableName)
//
//        jsonObject.put("userName", variantCdTextView.text.toString().split(": ")[1])
//        jsonObject.put("itemName", chassisNumberTextView.text.toString().split(": ")[1])
//        jsonObject.put("assetId",assetIdToIntOrString)
//        jsonObject.put("assetNo", assetNo.text.toString().split(": ")[1])
//        jsonObject.put("prodInvNo",inNoByMumCom.text.toString().split(": ")[1])
//        jsonObject.put("attribute5", tableName)
//        jsonObject.put("createdBy", login_name)
////        jsonObject.put("prodSrlNo",vin)
//        jsonObject.put("prodSrlNo", vin.ifEmpty { purchaseInvNoPost })
//        jsonObject.put("lastUpdatedBy", login_name)
//        jsonObject.put("batchEndDate", batchCodeEndDate)
//        jsonObject.put("locId", locId)
//        jsonObject.put("ouId", ouId)
//        jsonObject.put("insertedBy",login_name)
//        jsonObject.put("batchName", batchName)
//        jsonObject.put("batchStatus", "open")
//        jsonObject.put("batchCreationDate", formattedDate)
//        jsonObject.put("prodInvNo",inNoByMumCom.text.toString().split(": ")[1])
//        jsonObject.put("remarks",remarks)
//
//        val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
//        Log.d("requestBody",requestBody.toString())
//
//        val request = Request.Builder()
//            .url(url)
//            .post(requestBody)
//            .build()
//
//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                val response = client.newCall(request).execute()
//                val responseCode = response.code
//                val responseBody = response.body?.string()
//
//                Log.d("SaveVinData", "Response Code: $responseCode")
//                Log.d("SaveVinData", "Response Body: $responseBody")
//
//                runOnUiThread {
//                    if (responseBody != null) {
//                        val jsonObject = JSONObject(responseBody)
//                        val message = jsonObject.optString("message", "")
//
//                        when {
//                            message.contains("All assets against this asset ID have been stocked successfully.", ignoreCase = true) -> {
//                                Toast.makeText(
//                                    this@StockTaking,
//                                    "All assets against this asset ID have been stocked successfully.",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                clearFields()
//                            }
//                            responseCode == 200 -> {
//                                Toast.makeText(
//                                    this@StockTaking,
//                                    "Asset Stock Taken Successfully",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                clearFields()
//                            }
//                            else -> {
//                                Toast.makeText(
//                                    this@StockTaking,
//                                    "Failed to save data. Error code: $responseCode",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                    } else {
//                        Toast.makeText(
//                            this@StockTaking,
//                            "No response from server",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Log.e("SaveVinData", "Error: ${e.message}")
//                runOnUiThread {
//                    Toast.makeText(
//                        this@StockTaking,
//                        "Error saving data: ${e.message}",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//    }
//
//    //For Description Post
//    private fun saveDataDescriptionWise(iteName: String, batchName: String) {
//
//        val client = OkHttpClient()
//        val url = ApiFile.APP_URL + "/faBatch/faBatchNameManual"
//        val jsonObject = JSONObject()
//
//        val currentDateTime = Calendar.getInstance()
//        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
//        val formattedDate = formatter.format(currentDateTime.time)
//        Log.d("formattedDate", formattedDate)
//
//        currentDateTime.add(Calendar.DAY_OF_MONTH, 1)
//        val formatterForEndDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
//        val batchCodeEndDate = formatterForEndDate.format(currentDateTime.time)
//        Log.d("batchCodeEndDate", batchCodeEndDate)
//
//        jsonObject.put("userName", iteName)
//        jsonObject.put("itemName","")
//        jsonObject.put("assetId","")
//        jsonObject.put("assetNo","Manual")
//        jsonObject.put("prodInvNo","-")
//        jsonObject.put("attribute5", "-")
//        jsonObject.put("createdBy", login_name)
//        jsonObject.put("prodSrlNo", "-")
//        jsonObject.put("lastUpdatedBy", login_name)
//        jsonObject.put("batchEndDate", batchCodeEndDate)
//        jsonObject.put("locId", locId)
//        jsonObject.put("ouId", ouId)
//        jsonObject.put("insertedBy",login_name)
//        jsonObject.put("batchName", batchName)
//        jsonObject.put("batchStatus", "open")
//        jsonObject.put("batchCreationDate", formattedDate)
////        jsonObject.put("prodInvNo","-")
//        jsonObject.put("remarks","-")
//
//        val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
//        Log.d("requestBody",requestBody.toString())
//
//        val request = Request.Builder()
//            .url(url)
//            .post(requestBody)
//            .build()
//
//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                val response = client.newCall(request).execute()
//                val responseCode = response.code
//                val responseBody = response.body?.string()
//
//                Log.d("SaveVinData", "Response Code: $responseCode")
//                Log.d("SaveVinData", "Response Body: $responseBody")
//
//                runOnUiThread {
//                    if (responseBody != null) {
//                        val jsonObject = JSONObject(responseBody)
//                        val message = jsonObject.optString("message", "")
//
//                        when {
//                            message.contains("All assets against this asset ID have been stocked successfully.", ignoreCase = true) -> {
//                                Toast.makeText(
//                                    this@StockTaking,
//                                    "All assets against this asset ID have been stocked successfully.",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                clearFields()
//                            }
//                            responseCode == 200 -> {
//                                Toast.makeText(
//                                    this@StockTaking,
//                                    "Asset Stock Taken Successfully",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                clearFields()
//                            }
//                            else -> {
//                                Toast.makeText(
//                                    this@StockTaking,
//                                    "Failed to save data. Error code: $responseCode",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                    } else {
//                        Toast.makeText(
//                            this@StockTaking,
//                            "No response from server",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Log.e("SaveVinData", "Error: ${e.message}")
//                runOnUiThread {
//                    Toast.makeText(
//                        this@StockTaking,
//                        "Error saving data: ${e.message}",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//    }
//
//    private fun clearFields() {
//        itemNameEditText.visibility=View.GONE
//        itemNameEditTextFetch.visibility=View.GONE
//        prodSrlNo.visibility = View.GONE
//        findByprodSrl.visibility = View.GONE
//        itemNameEditText.setText("")
//        prodSrlNo.setText("")
//        assetNoTextView.visibility=View.GONE
//        assetNoTextView.text=""
//        assetNo.visibility=View.GONE
//        assetNo.text=""
//        inNoByMumCom.visibility=View.GONE
//        inNoByMumCom.text=""
//        fullTextView.visibility=View.GONE
//        fullTextView.text=""
//        variantCdTextView.text = ""
//        vehNoTextView.text = ""
//        remarks.visibility=View.GONE
//        remarks.text=""
//        Department.text=""
//        locName.text=""
//        locName.visibility=View.GONE
//        invNoTextView.text=""
//        invNoTextView.visibility=View.GONE
//        Department.visibility=View.GONE
//        vehNoTextView.visibility=View.GONE
//        dmsInvNoTextView.text = ""
//        chassisNumberTextView.text = ""
//        modelCodeTextView.text = ""
//        variant_code.text = ""
//        engine2.text = ""
//        VinQr.text.clear()
//        prodSrlNo.setText("")
//        qr_result_textview.text = ""
//        prodSrlNo.visibility=View.GONE
//        vintypeSpinner.visibility=View.GONE
//        findByprodSrl.visibility=View.GONE
//        fetchVinData2.visibility=View.GONE
//        refreshButton.visibility=View.GONE
//        variantCdTextView.visibility=View.GONE
//        dmsInvNoTextView.visibility=View.GONE
//        chassisNumberTextView.visibility=View.GONE
//        modelCodeTextView.visibility=View.GONE
//        variant_code.visibility=View.GONE
//        engine2.visibility=View.GONE
//        Physical_Location.visibility=View.GONE
//        save_button.visibility=View.INVISIBLE
//        save_button2.visibility=View.INVISIBLE
//        save_button3.visibility=View.INVISIBLE
//        VinDetails.visibility=View.GONE
//        vehNoTextView.visibility=View.GONE
//        fetchItemNoData.visibility=View.GONE
//        descItemName.setText("")
//        descUsername.setText("")
//        descDepartment.setText("")
//        descItemName.visibility=View.GONE
//        descUsername.visibility=View.GONE
//        descDepartment.visibility=View.GONE
//    }
//
//    private fun backToHome() {
//        finish()
//    }
//
//    data class chassis_data(
//        val VIN: String,
//        val LOCATION:String
//    )
//
//    data class vinData(
//        val VIN:String,
//        val FUEL_DESC: String,
//        val COLOUR: String,
//        val CHASSIS_NUM: String,
//        val MODEL_CD: String,
//        val VARIANT_CD:String,
//        val LOCATION: String,
//        val ENGINE_NO:String
//    )
//
//    data class itInventory(
//        val userDesigntn:String,
//        val locName:String,
//        val itemTypeId:String,
//        val itemType:String,
//        val itemCode:String,
//        val userName:String,
//        val Department:String,
//        val city:String,
//        val productserialNo:String,
//        val itemsubType:String,
//        val ouCity:String,
//        val location:String,
//        val productMake:String,
//        val dept:String,
//        val purchaseInvNo:String,
//        val assetNo:String,
//        val tableName:String,
//    )
//}















/////////////////////////////////////////////////////////////////////////////



package com.example.apinew

import android.Manifest
import java.net.URLEncoder
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
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.oned.rss.FinderPattern
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
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

class StockTaking : AppCompatActivity() {

    private lateinit var scanButton: Button
    private lateinit var saveButton: Button
    private lateinit var VinQr: EditText
    private lateinit var VinDetails: TextView
    private lateinit var variantCdTextView: TextView
    private lateinit var dmsInvNoTextView: TextView
    private lateinit var chassisNumberTextView: TextView
    private lateinit var modelCodeTextView: TextView
    private lateinit var engine2:TextView
    private lateinit var batchEditText: EditText
    private  lateinit var multipleBatchNameSpinner: Spinner
    private lateinit var batchName: TextView
    private lateinit var addBtn: Button
    private lateinit var save_button: Button
    private lateinit var save_button2:Button
    private lateinit var viewReportsButton: Button
    private lateinit var login_name: String
    private  lateinit var deptName: String
    private lateinit var  location_name:String
    private lateinit var usernameText:TextView
    private lateinit var locationText:TextView
    private lateinit var homepage:ImageButton
    private lateinit var ouId: String
    private lateinit var locId: String
    private lateinit var role:String
    private lateinit var variant_code:TextView
    private lateinit var Physical_Location:TextView
    private lateinit var qr_result_textview:TextView
    private lateinit var refreshButton:Button
    private lateinit var fetchChassisDataButton:Button
    private lateinit var findByprodSrl:ImageButton
    private lateinit var prodSrlNo:EditText
    private  lateinit var findBatchNameFn:Button
    private lateinit var vintypeSpinner:Spinner
    private lateinit var fetchVinData2:ImageButton
    private var selectedBatchName: String? = null
    private lateinit var locName:TextView
    private lateinit var Department:TextView
    private lateinit var inNoByMumCom:TextView
    private val recognizer = TextRecognition.getClient(
        TextRecognizerOptions.Builder().setExecutor(
            Executors.newSingleThreadExecutor()
        ).build()
    )
    private lateinit var purchaseInvNoPost:String
    private lateinit var vehNoTextView:TextView
    private lateinit var invNoTextView:TextView
    private lateinit var remarks:EditText
    private lateinit var fullTextView:TextView
    private lateinit var assetNoTextView:TextView
    private lateinit var fetchItemNoData:ImageButton
    private lateinit var invNoEnter:EditText
    private lateinit var assetNoEnter:EditText
    private lateinit var assetNo:TextView
    private lateinit var TableName:String
    private lateinit var save_button3:Button
    private lateinit var descItemName:EditText
    private lateinit var descUsername:EditText
    private lateinit var descDepartment:EditText

    //added on 27-11-2024
    private lateinit var itemNameEditText:EditText
    private lateinit var itemNameEditTextFetch:ImageButton
    private lateinit var fetchProdSrlBtn:Button
    private lateinit var fetchItemNameBtn:Button

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        private const val CAMERA_REQUEST_CODE = 101
        private const val CAMERA_REQUEST_CODE_2 = 103
    }

    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.O)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_taking)
        scanButton = findViewById(R.id.scan_button)
        save_button = findViewById(R.id.save_button)
        save_button2= findViewById(R.id.save_button2)
        VinQr = findViewById(R.id.vin_qr_edittext)
        VinDetails = findViewById(R.id.VinDetails)
        variantCdTextView = findViewById(R.id.variant_cd_textview)
        dmsInvNoTextView = findViewById(R.id.dms_inv_no_textview)
        chassisNumberTextView = findViewById(R.id.chassis_number_textview)
        modelCodeTextView = findViewById(R.id.model_code_textview)
        locName=findViewById(R.id.locName)
        Department=findViewById(R.id.Department)
        inNoByMumCom=findViewById(R.id.inNoByMumCom)
        vehNoTextView=findViewById(R.id.vehNoTextView)
        fullTextView=findViewById(R.id.fullTextView)
        assetNoTextView=findViewById(R.id.assetNoTextView)
        invNoTextView=findViewById(R.id.invNoTextView)
        remarks=findViewById(R.id.remarks)
        batchEditText = findViewById(R.id.batchEditText)
        multipleBatchNameSpinner = findViewById(R.id.multipleBatchNameSpinner)
        multipleBatchNameSpinner.visibility = View.GONE
        batchName = findViewById(R.id.batchNameLabel)
        addBtn = findViewById(R.id.addBtn)
        usernameText = findViewById(R.id.usernameText)
        locationText = findViewById(R.id.locationText)
        viewReportsButton = findViewById(R.id.viewReportsButton)
        homepage = findViewById(R.id.homepage)
        variant_code = findViewById(R.id.variant_code)
        Physical_Location = findViewById(R.id.Physical_Location)
        qr_result_textview = findViewById(R.id.qr_result_textview)
        ouId = intent.getStringExtra("ouId") ?: ""
        locId = intent.getStringExtra("locId") ?: ""
        Log.d("locId", locId.toString())
        Log.d("ouId", ouId.toString())
        deptName = intent.getStringExtra("deptName") ?: ""
        location_name = intent.getStringExtra("locName") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""
        role = intent.getStringExtra("role") ?: ""
        VinDetails.visibility = View.GONE
        save_button.visibility = View.INVISIBLE
        save_button2.visibility = View.INVISIBLE
        engine2 = findViewById(R.id.engine2)
        refreshButton = findViewById(R.id.refreshButton)
        fetchChassisDataButton = findViewById(R.id.fetchChassisDataButton)
        prodSrlNo = findViewById(R.id.prodSrlNo)
        findByprodSrl = findViewById(R.id.findByprodSrl)
        vintypeSpinner = findViewById(R.id.vintypeSpinner)
        vintypeSpinner.visibility = View.GONE
        fetchVinData2 = findViewById(R.id.fetchVinData2)
        fetchVinData2.visibility = View.GONE
        fetchItemNoData=findViewById(R.id.fetchItemNoData)
        invNoEnter=findViewById(R.id.invNoEnter)
        assetNoEnter=findViewById(R.id.assetNoEnter)
        assetNo=findViewById(R.id.assetNo)
        save_button3=findViewById(R.id.save_button3)
        save_button3.visibility=View.INVISIBLE

        descItemName=findViewById(R.id.descItemName)
        descUsername=findViewById(R.id.descUsername)
        descDepartment=findViewById(R.id.descDepartment)


        itemNameEditText=findViewById(R.id.itemNameEditText)
        itemNameEditTextFetch=findViewById(R.id.itemNameEditTextFetch)
        fetchProdSrlBtn=findViewById(R.id.fetchProdSrlBtn)
        fetchItemNameBtn=findViewById(R.id.fetchItemNameBtn)


        fetchProdSrlBtn.setOnClickListener {
            prodSrlNo.visibility = View.VISIBLE
            findByprodSrl.visibility = View.VISIBLE
            itemNameEditText.visibility=View.INVISIBLE
            itemNameEditTextFetch.visibility=View.INVISIBLE
        }

        fetchItemNameBtn.setOnClickListener {
            itemNameEditText.visibility=View.GONE
//            itemNameEditTextFetch.visibility=View.INVISIBLE
//            prodSrlNo.visibility = View.INVISIBLE
//            findByprodSrl.visibility = View.INVISIBLE

            descItemName.visibility=View.GONE
            descUsername.visibility=View.VISIBLE
            descDepartment.visibility=View.GONE
            save_button3.visibility=View.VISIBLE
            refreshButton.visibility=View.VISIBLE
        }

        itemNameEditTextFetch.setOnClickListener {
            fetchEnteredItemName()
        }
        findByprodSrl.setOnClickListener {
//            fetchAssetNoTxt()
            fetchAssetNoDataFromSpinner()
        }


//        prodSrlNo.visibility = View.VISIBLE
//        findByprodSrl.visibility = View.VISIBLE

        prodSrlNo.visibility = View.GONE
        findByprodSrl.visibility = View.GONE
        itemNameEditText.visibility=View.GONE
        itemNameEditTextFetch.visibility=View.GONE


        variantCdTextView.visibility = View.GONE
        dmsInvNoTextView.visibility = View.GONE
        chassisNumberTextView.visibility = View.GONE
        modelCodeTextView.visibility = View.GONE
        variant_code.visibility = View.GONE
        Physical_Location.visibility = View.GONE
        engine2.visibility = View.GONE
        locName.visibility=View.GONE
        Department.visibility=View.GONE
        vehNoTextView.visibility=View.GONE
        invNoTextView.visibility=View.GONE
        remarks.visibility=View.GONE
        fullTextView.visibility=View.GONE
        inNoByMumCom.visibility=View.GONE
        assetNoTextView.visibility=View.GONE
        fetchItemNoData.visibility=View.GONE
        assetNo.visibility=View.GONE

        fetchItemNoData.setOnClickListener {
            fetchMumComData()
        }

        findBybatchNameOpenStatus()




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
            fetchVinData2.visibility=View.VISIBLE
            vehNoTextView.visibility=View.VISIBLE
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openCamera(CAMERA_REQUEST_CODE)
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
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
            Log.d("currentDate", currentDate)
            Handler(Looper.getMainLooper()).postDelayed({
//                Toast.makeText(this, "Could not scan.Please try again.", Toast.LENGTH_SHORT).show()
            }, 10000)
        }

        fetchVinData2.setOnClickListener {
//            val itemCode = vehNoTextView.text.toString()
//            fetchVinData2(itemCode)
            fetchAssetNoTxt()
        }

        save_button.setOnClickListener {
//            val vin = VinQr.text.toString()
            val vin=dmsInvNoTextView.text.toString().split(": ")[1]
            val selectedBatchName = multipleBatchNameSpinner.selectedItem?.toString() ?: ""
            when {
                selectedBatchName == "Select Batch Name" -> {
                    Toast.makeText(this,"Please select a Batch Name first", Toast.LENGTH_SHORT)
                        .show()
                }

                selectedBatchName.isNotEmpty() -> {
//                    if (vin.isNotEmpty()) {
                    saveVinData(vin, selectedBatchName)
//                    }
//                else {
//                        Toast.makeText(this, "SR NO is empty", Toast.LENGTH_SHORT).show()
//                    }
                }

                else -> {
                    val batchName = batchEditText.text.toString()
//                    if (vin.isNotEmpty()) {
                    saveVinData(vin, batchName)
//                    }
//                    else {
//                        Toast.makeText(this, "SR NO or Batch Name is empty", Toast.LENGTH_SHORT)
//                            .show()
//                    }
                }
            }
        }

        save_button2.setOnClickListener {
            val vin=dmsInvNoTextView.text.toString().split(": ")[1]
            val selectedBatchName = multipleBatchNameSpinner.selectedItem?.toString() ?: ""
            when {
                selectedBatchName == "Select Batch Name" -> {
                    Toast.makeText(this,"Please select a Batch Name first", Toast.LENGTH_SHORT)
                        .show()
                }

                selectedBatchName.isNotEmpty() -> {
                    if (vin.isNotEmpty()) {
                        saveVinDataCamera(vin, selectedBatchName)
                    } else {
                        Toast.makeText(this, "SR NO is empty", Toast.LENGTH_SHORT).show()
                    }
                }

                else -> {
                    val batchName = batchEditText.text.toString()
                    if (vin.isNotEmpty()) {
                        saveVinDataCamera(vin, batchName)
                    } else {
                        Toast.makeText(this, "SR NO or Batch Name is empty", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        save_button3.setOnClickListener {
            val itemName=descUsername.text.toString()
            val selectedBatchName = multipleBatchNameSpinner.selectedItem?.toString() ?: ""
            when {
                selectedBatchName == "Select Batch Name" -> {
                    Toast.makeText(this,"Please select a Batch Name first", Toast.LENGTH_SHORT)
                        .show()
                }

                selectedBatchName.isNotEmpty() -> {
                    if (itemName.isNotEmpty()) {
                        saveDataDescriptionWise(itemName, selectedBatchName)
                    } else {
                        Toast.makeText(this, "Description or username is empty", Toast.LENGTH_SHORT).show()
                    }
                }

                else -> {
                    val batchName = batchEditText.text.toString()
                    if (itemName.isNotEmpty()) {
                        saveDataDescriptionWise(itemName, batchName)
                    } else {
                        Toast.makeText(this, "Description or Batch Name is empty", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun findBybatchNameOpenStatus() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.APP_URL}/faBatch/findExBatchNameStatus?locId=$locId")
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                val jsonObject = JSONObject(jsonData.toString())
                val jsonArray = jsonObject.getJSONArray("obj")
                val batStatus = jsonArray.getJSONObject(0)
                val status = batStatus.getString("batchStatus")
                Log.d("status-----BatchStatus",status)

                runOnUiThread {
                    if (status.equals("open", ignoreCase = true)) {
                        Log.d("If Condition-----Open",status)
                        multipleBatchNameSpinner.visibility = View.VISIBLE
                        batchEditText.visibility = View.INVISIBLE
                        fetchBatchData()
                    }
                    else if (status.equals("Closed", ignoreCase = true)) {
                        Log.d("Else If Condition-----Open",status)
                        batchEditText.visibility = View.VISIBLE
                        multipleBatchNameSpinner.visibility = View.INVISIBLE
                        findBybatchNameStatus()
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


//    private fun findBybatchNameOpenStatus() {
//        val client = OkHttpClient()
//        val request = Request.Builder()
//            .url("${ApiFile.APP_URL}/faBatch/findExBatchNameStatus?locId=$locId")
//            .build()
//
//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                val response = client.newCall(request).execute()
//                val jsonData = response.body?.string()
//                val jsonObject = JSONObject(jsonData.toString())
//                val jsonArray = jsonObject.getJSONArray("obj")
//
//                var hasOpen = false
//                for (i in 0 until jsonArray.length()) {
//                    val batch = jsonArray.getJSONObject(i)
//                    val status = batch.getString("batchStatus")
//                    if (status.equals("open", ignoreCase = true)) {
//                        hasOpen = true
//                        break
//                    }
//                }
//
//                runOnUiThread {
//                    if (hasOpen) {
//                        Log.d("BatchStatus", "Found OPEN")
//                        multipleBatchNameSpinner.visibility = View.VISIBLE
//                        batchEditText.visibility = View.INVISIBLE
//                        fetchBatchData()
//                    } else {
//                        Log.d("BatchStatus", "Only CLOSED found")
//                        batchEditText.visibility = View.VISIBLE
//                        multipleBatchNameSpinner.visibility = View.INVISIBLE
//                        findBybatchNameStatus()
//                    }
//                }
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//                runOnUiThread {
//                    //
//                }
//            }
//        }
//    }



    private fun findBybatchNameStatus() {
        val currentDate: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val batchName = ("$location_name-$currentDate")

        val LocationName = "$location_name"
        Log.d("LocationName----LocationName", LocationName)
        Log.d("batchName----batchName", batchName)
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.APP_URL}/faBatch/findBatchNameStatus?batchName=${batchName}&locId=$locId")
            .build()
        Log.d("findBybatchNameStatus",request.toString())
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()

                Log.d("jsonData------", jsonData.toString())
                val jsonObject = JSONObject(jsonData.toString())
                val jsonArray = jsonObject.getJSONArray("obj")
                val batStatus = jsonArray.getJSONObject(0)
                val batchName = batStatus.getString("batchName")
                val batchStatus = batStatus.getString("batchStatus")
                Log.d("batchname",batchName)
                Log.d("batchStatus",batchStatus)
                Log.d("jsonDataCheck", jsonArray.toString())

                if (jsonArray.length() == 0) {
                    Log.d("jsonDataCheckIf", jsonData.toString())
                    batchEditText.visibility=View.VISIBLE
                    multipleBatchNameSpinner.visibility= View.INVISIBLE
                } else {
                    Log.d("jsonDataCheckelse", jsonData.toString())
                    batchEditText.visibility= View.GONE
                    multipleBatchNameSpinner.visibility=View.VISIBLE
                    fetchBatchData()
                }
                Log.d("nss---->", batchName.toString())
                Log.d("nss2--->", batchEditText.text.toString())
                if(batchName.toString()==batchEditText.text.toString()&&batchStatus.toString()=="Closed"){
                    save_button.visibility=View.INVISIBLE
                    save_button2.visibility = View.INVISIBLE
                    save_button2.visibility = View.INVISIBLE
                    Log.d("nss---->", batchName.toString())
                    Log.d("nss2.0--->", batchStatus.toString())
                    Log.d("nss2--->", batchEditText.toString())
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
            .url("${ApiFile.APP_URL}/faBatch/batchNameOpen?locId=${locId}")
            .build()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                Log.d("jsonDataBatchList", jsonData.toString())
                jsonData?.let {
                    val batchCodeList = parseCities(it)
                    Log.d("batchCodeList-----LineNo---491",batchCodeList.toString())
                    runOnUiThread {
                        val adapter = ArrayAdapter(
                            this@StockTaking,
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
        Log.d("List Checking---",mutableListOf<String>().toString())
        Log.d("batchList Checking----",batchCodeList.toString())
        try {
            val jsonObject = JSONObject(jsonData)
            val jsonArray = jsonObject.getJSONArray("obj")
            batchCodeList.add("Select Batch Name")
            for (i in 0 until jsonArray.length()) {
                val batchList = jsonArray.getJSONObject(i)
                val code = batchList.getString("batchName")
                batchCodeList.add("$code")
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
            IntentIntegrator.REQUEST_CODE -> {
                val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
                if (result != null) {
                    if (result.contents == null) {
                        Toast.makeText(
                            this@StockTaking,
                            "No barcode data found",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val barcodeContent = result.contents

                        val srNo = extractSerialNumber(barcodeContent)
                        val invNo = extractInvNumber(barcodeContent)
//                        val assetNo = extractAssetNumber(barcodeContent)
                        val assetNo = (barcodeContent)


//                        remarks.text = barcodeContent
//                        remarks.setText(barcodeContent)

                        if (assetNo != null) {
                            assetNoTextView.visibility = View.VISIBLE
                            assetNoTextView.text = "ASSET NO.: $assetNo"
                        } else {
                            Toast.makeText(
                                this@StockTaking,
                                "ASSET NO not present",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        if (invNo != null) {
                            invNoTextView.visibility = View.VISIBLE
                            invNoTextView.text = "INVOICE NO.: $invNo"
                        } else {
                            Toast.makeText(
                                this@StockTaking,
                                "INVOICE No not present",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        if (srNo != null) {
                            qr_result_textview.visibility = View.VISIBLE
                            invNoTextView.visibility = View.GONE
                            qr_result_textview.text = srNo
                            VinQr.setText(srNo)
                        } else {
                            Toast.makeText(
                                this@StockTaking,
                                "SR NO not present",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        // Priority search- Check assetNo first, then invNo, then srNo
                        if (assetNo != null) {
                            fetchVinData(
                                assetNo,
                                "",
                                ""
                            )
                            Toast.makeText(
                                this@StockTaking,
                                "Search on asset No",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (invNo != null) {
                            fetchVinData(
                                "",
                                invNo,
                                ""
                            )
                            Toast.makeText(
                                this@StockTaking,
                                "Search on Inv No",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (srNo != null) {
                            fetchVinData(
                                "",
                                "",
                                srNo
                            )
                            Toast.makeText(
                                this@StockTaking,
                                "Search on sr No",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@StockTaking,
                                "SR NO, INV NO, and ASSET NO are not present",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            CAMERA_REQUEST_CODE, CAMERA_REQUEST_CODE_2 -> {
                if (resultCode == Activity.RESULT_OK) {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    when (requestCode) {
                        CAMERA_REQUEST_CODE -> {
                            vehNoTextView.visibility=View.VISIBLE
                            processImageWithMultipleAttempts(imageBitmap, vehNoTextView)
//                            val itemCode = vehNoTextView.toString()
                            val itemCode = vehNoTextView.text.toString()
                            fetchVinData2(itemCode)

                            Log.d("bestResult2", itemCode)
                            if (itemCode.isNotEmpty()) {
                                Toast.makeText(
                                    this@StockTaking,
                                    "ITEM CODE->$itemCode",
                                    Toast.LENGTH_SHORT
                                ).show()
//                                fetchVinData2(itemCode)
                            } else {
                                Toast.makeText(
                                    this@StockTaking,
                                    "Failed to extract a valid SR number",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun extractInvNumber(barcodeContent: String): String? {
        val invNoRegex = """(INV\s+NO|INVOICE\s+NO|INVOICE\s+NO.|Inv\s+No-)\s*([\w\/\-\.]+)""".toRegex()
        val matchResult = invNoRegex.find(barcodeContent)
        return matchResult?.groupValues?.get(2)
    }

    private fun extractSerialNumber(barcodeContent: String): String? {
        val srNoRegex = """SR\s+NO\s+-\s+(\w+)""".toRegex()
        val matchResult = srNoRegex.find(barcodeContent)
        return matchResult?.groupValues?.get(1)
    }

//    private fun extractAssetNumber(barcodeContent: String): String? {
//        val assetNoRegex = """ASSET\s+NO\s+(\d{5})|ASSET\s+NO.\s+(\d{5})|ASSET\s+NO-\s+(\d{5}|ASSET\s+NO:-\s+(\d{5})|ASSETNO\s+(\d{5})|\b(\d{5})\b)""".toRegex()
////        val assetNoRegex = """""".toRegex()
//        val matchResult = assetNoRegex.find(barcodeContent)
//        return matchResult?.groupValues?.get(1)
//    }

    fun extractAssetNumber(barcodeContent: String): String? {
        val regex = Regex("\\b\\d{5}\\b")
        return regex.find(barcodeContent)?.value
    }


    private fun processImageWithMultipleAttempts(originalBitmap: Bitmap, resultTextView: TextView) {
        val attempts = listOf(
            { preprocessImage(originalBitmap) },
            { preprocessImage(resizeImage(originalBitmap)) },
            { resizeImage(originalBitmap) }
        )

        val results = mutableListOf<String>()
        var fullText = "" // Store only the first full text
        var hasValidResult = false // Flag to stop after first valid result

        attempts.forEachIndexed { index, preprocessor ->
            if (hasValidResult) return@forEachIndexed // Stop if a valid result is found

            val processedBitmap = preprocessor()
            val image = InputImage.fromBitmap(processedBitmap, 0)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    if (fullText.isEmpty()) {
                        fullText = visionText.text // Store only the first full text
                    }

                    val extractedText = extractDesiredText(visionText.text)
                    if (extractedText.isNotEmpty()) {
                        results.add(extractedText)
                        hasValidResult = true
                    }


                    if (index == attempts.lastIndex || hasValidResult) {
                        displayBestResult(results, resultTextView)
                        displayFullText(fullText)
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


    private fun displayFullText(fullText: String) {
        runOnUiThread {
            fullTextView.text = fullText
        }
    }


    private fun extractDesiredText(text: String): String {

        val processedText = text.mapIndexed { index, char ->
            when {
                text.length==11 && index in 7..10 && char == 'O' -> '0'
                text.length==12 && index in 8..11 && char == 'O' -> '0'
                else -> char
            }
        }.joinToString("")

        val regexList = listOf(
//            Regex("MUM-\\w{3}\\d{4}"),
//            Regex("MUM-\\w{3}\\d{2}"),
//            Regex("MUM-\\w{3}00\\d{2}"),
//            Regex("MUM-\\w{3}-00\\d{2}"),
//            Regex("MUM-\\w{3}-\\d{4}"),
//            Regex("PUN-\\w{3}-\\d{4}"),
//            Regex("PUN-\\w{3}\\d{4}"),
//            Regex("PUN-\\w{3}00\\d{2}"),
//            Regex("PUN-\\w{3}-00\\d{2}"),
//            Regex("HYD-\\w{3}-\\d{4}"),
//            Regex("HYD-\\w{3}\\d{4}"),
//            Regex("HYD-\\w{3}00\\d{2}"),
//            Regex("HYD-\\w{3}-00\\d{2}"),
//            Regex("KOC-\\w{3}\\d{4}"),
//            Regex("KOC-\\w{3}-\\d{4}"),
//            Regex("KOC-\\w{3}-00\\d{2}"),
//            Regex("KOC-\\w{3}00\\d{2}"),
//            Regex("GOA-\\w{3}-\\d{4}"),
//            Regex("GOA-\\w{3}\\d{4}"),
//            Regex("GOA-\\w{3}-00\\d{2}"),
//            Regex("GOA-\\w{3}00\\d{2}"),
//            Regex("KLP-\\w{3}-\\d{4}"),
//            Regex("KLP-\\w{3}\\d{4}"),
//            Regex("KLP-\\w{3}-00\\d{2}"),
//            Regex("KLP-\\w{3}00\\d{2}")
            Regex("\\d{5}"),
            Regex("\\d{4}")
        )

        for (regex in regexList) {
            val matchResult = regex.find(processedText)
            if (matchResult != null) {
                return matchResult.value
            }
        }
        return ""
    }



    private fun displayBestResult(results: List<String>, resultTextView: TextView) {
        val bestResult2 = results.firstOrNull { it.isNotEmpty() } ?: ""
        vehNoTextView.text=bestResult2
        runOnUiThread {
            resultTextView.text = bestResult2
//            if (bestResult2.isNotEmpty()) {
////                Toast.makeText(this, "Text recognized: $bestResult2", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this, "Desired text format not found", Toast.LENGTH_SHORT).show()
//            }
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
        itemNameEditText.visibility=View.GONE
        itemNameEditTextFetch.visibility=View.GONE
        prodSrlNo.visibility = View.GONE
        findByprodSrl.visibility = View.GONE
        itemNameEditText.setText("")
        prodSrlNo.setText("")
        inNoByMumCom.visibility=View.GONE
        inNoByMumCom.text=""
        assetNo.visibility=View.GONE
        assetNo.text=""
        assetNoTextView.visibility=View.GONE
        assetNoTextView.text=""
        invNoTextView.visibility=View.GONE
        invNoTextView.text=""
        fullTextView.visibility=View.GONE
        fullTextView.text=""
        remarks.visibility=View.GONE
        remarks.setText("")
        prodSrlNo.setText("")
        vintypeSpinner.visibility=View.GONE
        vehNoTextView.text = ""
        Department.text=""
        locName.text=""
        locName.visibility=View.GONE
        Department.visibility=View.GONE
        vehNoTextView.visibility=View.GONE
        qr_result_textview.text=""
        VinDetails.visibility=View.INVISIBLE
        VinDetails.text=""
        variantCdTextView.text=""
        dmsInvNoTextView.text=""
        chassisNumberTextView.text=""
        findByprodSrl.visibility=View.INVISIBLE
        prodSrlNo.visibility=View.GONE
        modelCodeTextView.text=""
        variant_code.text=""
        Physical_Location.text=""
        engine2.text=""
        variantCdTextView.visibility=View.INVISIBLE
        dmsInvNoTextView.visibility=View.INVISIBLE
        chassisNumberTextView.visibility=View.INVISIBLE
        modelCodeTextView.visibility=View.INVISIBLE
        variant_code.visibility=View.INVISIBLE
        Physical_Location.visibility=View.INVISIBLE
        engine2.visibility=View.INVISIBLE
        save_button.visibility=View.INVISIBLE
        save_button2.visibility=View.INVISIBLE
        save_button3.visibility=View.INVISIBLE
        vintypeSpinner.setSelection(0)
        fetchVinData2.visibility=View.INVISIBLE
        fetchItemNoData.visibility=View.GONE
        descItemName.setText("")
        descUsername.setText("")
        descDepartment.setText("")
        descItemName.visibility=View.GONE
        descUsername.visibility=View.GONE
        descDepartment.visibility=View.GONE
    }


    //Fetch Data After Scan Qr Code.
    private fun fetchVinData(assetNo: String, invNo: String, prodSrlNo: String) {

        val client = OkHttpClient()

        val urlBuilder = StringBuilder(ApiFile.APP_URL + "/faBatch/detailsByProdSrl?")

        if (assetNo.isNotEmpty()) {
            urlBuilder.append("prodSrlNo=&assetNo=$assetNo&invNo=&ouId=$ouId")
        }
        else if (invNo.isNotEmpty()) {
            urlBuilder.append("prodSrlNo=&assetNo=&invNo=$invNo")
        }
        else if (prodSrlNo.isNotEmpty()) {
            urlBuilder.append("prodSrlNo=$prodSrlNo&assetNo=&invNo=")
        }
        else {
            Log.d("FetchVinData", "No parameters provided for the request")
            return
        }

        val url = urlBuilder.toString()
        Log.d("URL:", url)

        val request = Request.Builder().url(url).build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    val stockItems = jsonObject.getJSONArray("obj")

                    if (stockItems.length() > 0) {
                        val itemCodes = mutableListOf<String>()

                        for (i in 0 until stockItems.length()) {
                            val stockItem = stockItems.getJSONObject(i)

                            val itemCode = if (stockItem.has("itemCode")) {
                                stockItem.getString("itemCode")
                            } else {
                                stockItem.optString("assetNo", "N/A")
                            }
                            itemCodes.add(itemCode)
                        }

                        runOnUiThread {
                            populateSpinnerWithMultipleItemCodes(itemCodes)
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@StockTaking, "No items found", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@StockTaking, "Failed to fetch details", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //Fetch MUM-COM number after entering the asset number in Spinner
    private fun fetchAssetNoDataFromSpinner() {
        val assetNumber=prodSrlNo.text.toString()

        val client = OkHttpClient()

        val urlBuilder = ApiFile.APP_URL + "/faBatch/detailsByProdSrl?prodSrlNo=&assetNo=$assetNumber&invNo=&ouId=$ouId"

        val url = urlBuilder
        Log.d("URL:", url)

        val request = Request.Builder().url(url).build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    val stockItems = jsonObject.getJSONArray("obj")

                    if (stockItems.length() > 0) {
                        val itemCodes = mutableListOf<String>()

                        for (i in 0 until stockItems.length()) {
                            val stockItem = stockItems.getJSONObject(i)

                            val itemCode = if (stockItem.has("itemCode")) {
                                stockItem.getString("itemCode")
                            }
                            else {
                                stockItem.optString("assetNo", "N/A")
                            }

                            itemCodes.add(itemCode)
                        }

                        runOnUiThread {
                            populateSpinnerWithMultipleItemCodes(itemCodes)
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@StockTaking, "No items found", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@StockTaking, "Failed to fetch details", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun fetchAssetNoTxt() {
        val prodSrlNoEditTxt=prodSrlNo.text.toString()
        val client = OkHttpClient()
        val url =ApiFile.APP_URL+"/faBatch/detailsByProdSrl?prodSrlNo=&assetNo=$prodSrlNoEditTxt&invNo=&ouId=$ouId"
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

                    val vinData = itInventory(
                        userDesigntn = stockItem.optString("userDesigntn","-"),
                        locName = stockItem.optString("locName","-"),
                        itemType = stockItem.optString("itemType","-"),
                        itemTypeId = stockItem.optString("itemTypeId","-"),
                        itemCode = stockItem.optString("itemCode","-"),
                        userName = stockItem.optString("userName","-"),
                        Department = stockItem.optString("Department","-"),
                        city = stockItem.optString("city","-"),
                        productserialNo = stockItem.optString("productserialNo","-"),
                        itemsubType = stockItem.optString("itemsubType","-"),
                        ouCity = stockItem.optString("ouCity","-"),
                        location = stockItem.optString("location","-"),
                        productMake = stockItem.optString("productMake","-"),
                        dept = stockItem.optString("dept","-"),
                        purchaseInvNo=stockItem.optString("purchaseInvNo","-"),
                        assetNo=stockItem.optString("assetNo","-"),
                        tableName=stockItem.optString("tableName","-")
                    )
                    runOnUiThread {
                        populateFields(vinData)
                        VinDetails.visibility = View.GONE
                        refreshButton.visibility=View.VISIBLE
                        Toast.makeText(
                            this@StockTaking,
                            "Details found Successfully \n for Asset No: $prodSrlNoEditTxt",
                            Toast.LENGTH_SHORT
                        ).show()
                        VinDetails.visibility=View.VISIBLE
                        vintypeSpinner.visibility=View.GONE
                        VinDetails.text="Details by Asset Number"
                        save_button.visibility=View.INVISIBLE
                        save_button2.visibility=View.VISIBLE
                        save_button3.visibility=View.INVISIBLE
                        variantCdTextView.visibility=View.VISIBLE
                        dmsInvNoTextView.visibility=View.VISIBLE
                        chassisNumberTextView.visibility=View.VISIBLE
                        modelCodeTextView.visibility=View.VISIBLE
                        variant_code.visibility=View.VISIBLE
                        Physical_Location.visibility=View.VISIBLE
                        engine2.visibility=View.VISIBLE
                        locName.visibility=View.VISIBLE
                        Department.visibility=View.VISIBLE
                        refreshButton.visibility=View.VISIBLE
                        inNoByMumCom.visibility=View.VISIBLE
                        assetNo.visibility=View.VISIBLE
                        remarks.visibility=View.GONE
                        fullTextView.visibility=View.GONE
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@StockTaking,
                        "Failed to fetch details for Asset No: $prodSrlNoEditTxt",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun populateSpinnerWithMultipleItemCodes(itemCodes: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, itemCodes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        vintypeSpinner.adapter = adapter
        vintypeSpinner.visibility = View.VISIBLE
        fetchItemNoData.visibility=View.VISIBLE
    }

    //Fetch Data After Capturing the asset No.
    private fun fetchVinData2(srNo2: String) {
        val client = OkHttpClient()
        val itemCode = vehNoTextView.text.toString()
        val url =ApiFile.APP_URL+"/faBatch/detailsByItemName?itemName=$itemCode"
        Log.d("URL:", url)
        val request = Request.Builder()
            .url(url)
            .build()
        Log.d("bestResult2:", srNo2)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    Log.d("Data", jsonObject.toString())

                    val stockItem = jsonObject.getJSONArray("obj").getJSONObject(0)

                    val vinData = itInventory(
                        userDesigntn = stockItem.getString("userDesigntn"),
                        locName = stockItem.getString("locName"),
                        itemType = stockItem.getString("itemType"),
                        itemTypeId = stockItem.getString("itemTypeId"),
                        itemCode = stockItem.getString("itemCode"),
                        userName = stockItem.getString("userName"),
                        Department = stockItem.getString("Department"),
                        city = stockItem.getString("city"),
                        productserialNo = stockItem.getString("productserialNo"),
                        itemsubType = stockItem.getString("itemsubType"),
                        ouCity = stockItem.getString("ouCity"),
                        location = stockItem.getString("location"),
                        productMake = stockItem.getString("productMake"),
                        dept = stockItem.getString("dept"),
                        purchaseInvNo=stockItem.getString("purchaseInvNo"),
                        assetNo=stockItem.getString("assetNo"),
                        tableName=stockItem.getString("tableName")
                    )
                    runOnUiThread {
                        populateFields(vinData)
                        VinDetails.visibility = View.GONE
                        refreshButton.visibility=View.VISIBLE
                        Toast.makeText(
                            this@StockTaking,
                            "Details found Successfully \n for item No: $itemCode",
                            Toast.LENGTH_SHORT
                        ).show()
                        VinDetails.visibility=View.VISIBLE
                        save_button.visibility=View.INVISIBLE
                        save_button2.visibility=View.VISIBLE
                        save_button3.visibility=View.INVISIBLE
                        variantCdTextView.visibility=View.VISIBLE
                        dmsInvNoTextView.visibility=View.VISIBLE
                        chassisNumberTextView.visibility=View.VISIBLE
                        modelCodeTextView.visibility=View.VISIBLE
                        variant_code.visibility=View.VISIBLE
                        Physical_Location.visibility=View.VISIBLE
                        engine2.visibility=View.VISIBLE
                        locName.visibility=View.VISIBLE
                        Department.visibility=View.VISIBLE
                        refreshButton.visibility=View.VISIBLE
                        inNoByMumCom.visibility=View.VISIBLE
                        assetNo.visibility=View.VISIBLE
                        remarks.visibility=View.VISIBLE
                        fullTextView.visibility=View.GONE
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@StockTaking,
                        "Failed to fetch details for SR NO: $itemCode",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun fetchEnteredItemName() {
        val client = OkHttpClient()
        val itemName = itemNameEditText.text.toString()
        val url =ApiFile.APP_URL+"/faBatch/detailsByItemName?itemName=$itemName"
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

                    val vinData = itInventory(
                        userDesigntn = stockItem.getString("userDesigntn"),
                        locName = stockItem.getString("locName"),
                        itemType = stockItem.getString("itemType"),
                        itemTypeId = stockItem.getString("itemTypeId"),
                        itemCode = stockItem.getString("itemCode"),
                        userName = stockItem.getString("userName"),
                        Department = stockItem.getString("Department"),
                        city = stockItem.getString("city"),
                        productserialNo = stockItem.getString("productserialNo"),
                        itemsubType = stockItem.getString("itemsubType"),
                        ouCity = stockItem.getString("ouCity"),
                        location = stockItem.getString("location"),
                        productMake = stockItem.getString("productMake"),
                        dept = stockItem.getString("dept"),
                        purchaseInvNo=stockItem.getString("purchaseInvNo"),
                        assetNo=stockItem.getString("assetNo"),
                        tableName=stockItem.getString("tableName")
                    )
                    runOnUiThread {
                        populateFields(vinData)
                        VinDetails.visibility = View.GONE
                        refreshButton.visibility=View.VISIBLE
                        Toast.makeText(
                            this@StockTaking,
                            "Details found Successfully \n for item No: $itemName",
                            Toast.LENGTH_SHORT
                        ).show()
                        VinDetails.visibility=View.VISIBLE
                        save_button.visibility=View.INVISIBLE
                        save_button2.visibility=View.VISIBLE
                        save_button3.visibility=View.INVISIBLE
                        variantCdTextView.visibility=View.VISIBLE
                        dmsInvNoTextView.visibility=View.VISIBLE
                        chassisNumberTextView.visibility=View.VISIBLE
                        modelCodeTextView.visibility=View.VISIBLE
                        variant_code.visibility=View.VISIBLE
                        Physical_Location.visibility=View.VISIBLE
                        engine2.visibility=View.VISIBLE
                        locName.visibility=View.VISIBLE
                        Department.visibility=View.VISIBLE
                        refreshButton.visibility=View.VISIBLE
                        inNoByMumCom.visibility=View.VISIBLE
                        assetNo.visibility=View.VISIBLE
                        remarks.visibility=View.GONE
                        fullTextView.visibility=View.GONE
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@StockTaking,
                        "Failed to fetch details for SR NO: $itemName",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    //Fetch the data on MUM-COM number after searching it by asset number.
    private fun fetchMumComData() {
        val itemCode=vintypeSpinner.selectedItem.toString()
        val encodedItemCode = URLEncoder.encode(itemCode, "UTF-8")
        val client = OkHttpClient()
        val url =ApiFile.APP_URL+"/faBatch/detailsByMulValues?code=$encodedItemCode"

        Log.d("URL:", url)

        val request = Request.Builder()
            .url(url)
            .build()
        Log.d("srNo:", itemCode)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    Log.d("Data", jsonObject.toString())
                    val stockItem = jsonObject.getJSONArray("obj").getJSONObject(0)

                    val vinData = itInventory(
                        userDesigntn = stockItem.getString("userDesigntn"),
                        locName = stockItem.getString("locName"),
                        itemType = stockItem.getString("itemType"),
                        itemTypeId = stockItem.getString("itemTypeId"),
                        itemCode = stockItem.getString("itemCode"),
                        userName = stockItem.getString("userName"),
                        Department = stockItem.getString("Department"),
                        city = stockItem.getString("city"),
                        productserialNo = stockItem.getString("productserialNo"),
                        itemsubType = stockItem.getString("itemsubType"),
                        ouCity = stockItem.getString("ouCity"),
                        location = stockItem.getString("location"),
                        productMake = stockItem.getString("productMake"),
                        dept = stockItem.getString("dept"),
                        purchaseInvNo=stockItem.getString("purchaseInvNo"),
                        assetNo=stockItem.getString("assetNo"),
                        tableName=stockItem.getString("tableName")
                    )
                    runOnUiThread {
                        populateFields(vinData)
                        qr_result_textview.visibility=View.VISIBLE
                        VinDetails.visibility = View.GONE
                        refreshButton.visibility=View.VISIBLE
                        Toast.makeText(
                            this@StockTaking,
                            "Details found Successfully \n for Item No.: $encodedItemCode",
                            Toast.LENGTH_SHORT
                        ).show()
                        VinDetails.visibility=View.GONE
                        save_button.visibility=View.VISIBLE
                        save_button2.visibility=View.INVISIBLE
                        save_button3.visibility=View.INVISIBLE
                        variantCdTextView.visibility=View.VISIBLE
                        dmsInvNoTextView.visibility=View.VISIBLE
                        chassisNumberTextView.visibility=View.VISIBLE
                        modelCodeTextView.visibility=View.VISIBLE
                        variant_code.visibility=View.VISIBLE
                        Physical_Location.visibility=View.VISIBLE
                        engine2.visibility=View.VISIBLE
                        locName.visibility=View.VISIBLE
                        Department.visibility=View.VISIBLE
                        refreshButton.visibility=View.VISIBLE
                        inNoByMumCom.visibility=View.VISIBLE
                        assetNo.visibility=View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@StockTaking,
                        "Failed to fetch details for Item Code: $itemCode",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun stockReports() {
        val intent = Intent(this, ViewReportsActivity::class.java)
        intent.putExtra("login_name", login_name)
        intent.putExtra("locId", locId)
        intent.putExtra("role",role)
        intent.putExtra("ouId", ouId)
        startActivity(intent)
    }

    private fun populateFields(invData: itInventory) {
        variantCdTextView.text = " USERNAME.: ${invData.userName}"
        dmsInvNoTextView.text = "PRODUCT SR.NO.: ${invData.productserialNo}"
        chassisNumberTextView.text = "ITEM CODE: ${invData.itemCode}"
        modelCodeTextView.text = "ITEM TYPE: ${invData.itemType}"
        variant_code.text="ITEM SUB TYPE: ${invData.itemsubType}"
        Physical_Location.text="PRODUCT MAKE: ${invData.productMake}"
        engine2.text="USER DESIGNATION: ${invData.userDesigntn}"
        locName.text="LOCATION: ${invData.locName}"
        Department.text="DEPARTMENT: ${invData.Department}"
        inNoByMumCom.text="INV NO.: ${invData.purchaseInvNo}"
        assetNo.text="ASSET NO.: ${invData.assetNo}"
        TableName=invData.tableName
        remarks.visibility = View.VISIBLE
        fullTextView.visibility = View.GONE
        purchaseInvNoPost="${invData.itemTypeId}"
    }

    private fun saveVinData(vin: String, batchName: String) {
        Log.d("BatchName", batchName)
        Log.d("vinIn", vin)
        val tableName=TableName
        val assetId=chassisNumberTextView.text.toString().split(": ")[1]
//        val assetIdToInt=assetId.toInt()
        val assetIdToIntOrString = if (assetId.isDigitsOnly()) {
            assetId.toInt()
        } else {
            assetId
        }
        val remarks=remarks.text.toString()
        val client = OkHttpClient()
        val url = ApiFile.APP_URL + "/faBatch/faBatchNameScan"
        val jsonObject = JSONObject()
        val currentDateTime = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        val formattedDate = formatter.format(currentDateTime.time)
        Log.d("formattedDate", formattedDate)

        currentDateTime.add(Calendar.DAY_OF_MONTH, 1)
        val formatterForEndDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        val batchCodeEndDate = formatterForEndDate.format(currentDateTime.time)
        Log.d("batchCodeEndDate", batchCodeEndDate)
        Log.d("TableName--->.>",TableName)
        jsonObject.put("userName", variantCdTextView.text.toString().split(": ")[1])
        jsonObject.put("itemName", chassisNumberTextView.text.toString().split(": ")[1])
        jsonObject.put("assetId",assetIdToIntOrString)
        jsonObject.put("assetNo", assetNo.text.toString().split(": ")[1])
        jsonObject.put("prodInvNo",inNoByMumCom.text.toString().split(": ")[1])
        jsonObject.put("attribute5", tableName)
        jsonObject.put("createdBy", login_name)
//        jsonObject.put("prodSrlNo",vin)
        jsonObject.put("prodSrlNo", vin.ifEmpty { purchaseInvNoPost })
        jsonObject.put("lastUpdatedBy", login_name)
        jsonObject.put("batchEndDate", batchCodeEndDate)
        jsonObject.put("locId", locId)
        jsonObject.put("ouId", ouId)
        jsonObject.put("insertedBy",login_name)
        jsonObject.put("batchName", batchName)
        jsonObject.put("batchStatus", "open")
        jsonObject.put("batchCreationDate", formattedDate)
        jsonObject.put("prodInvNo",inNoByMumCom.text.toString().split(": ")[1])
        jsonObject.put("remarks",remarks)

        Log.d("createdBy--->",login_name)
        Log.d("prodInvNo--->",inNoByMumCom.text.toString().split(": ")[1])
        Log.d("jsonObject", jsonObject.toString())

        val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
        Log.d("requestBody",requestBody.toString())


        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseCode = response.code
                val responseBody = response.body?.string()

                Log.d("SaveVinData", "Response Code: $responseCode")
                Log.d("SaveVinData", "Response Body: $responseBody")

                runOnUiThread {
                    if (responseBody != null) {
                        val jsonObject = JSONObject(responseBody)
                        val message = jsonObject.optString("message", "")

                        when {
                            responseCode == 400 || message.contains("All assets against this asset ID have been stocked successfully.", ignoreCase = true) -> {
                                Toast.makeText(
                                    this@StockTaking,
                                    "Entry already done for this asset.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                clearFields()
                            }
                            responseCode == 200 -> {
                                Toast.makeText(
                                    this@StockTaking,
                                    "Asset Stock Taken Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                clearFields()
                            }
                            else -> {
                                Toast.makeText(
                                    this@StockTaking,
                                    "Failed to save data. Error code: $responseCode",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@StockTaking,
                            "No response from server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("SaveVinData", "Error: ${e.message}")
                runOnUiThread {
                    Toast.makeText(
                        this@StockTaking,
                        "Error saving data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun saveVinDataCamera(vin: String, batchName: String) {
        Log.d("BatchName", batchName)
        Log.d("vinIn", vin)
        val assetId=chassisNumberTextView.text.toString().split(": ")[1]
//        val assetIdToInt=assetId.toInt()
        val assetIdToIntOrString = if (assetId.isDigitsOnly()) {
            assetId.toInt()
        } else {
            assetId
        }
        val tableName=TableName
//        val remarks=fullTextView.text.toString()
        val remarks=remarks.text.toString()


        val client = OkHttpClient()
        val url = ApiFile.APP_URL + "/faBatch/faBatchNameManual"
        val jsonObject = JSONObject()

        val currentDateTime = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        val formattedDate = formatter.format(currentDateTime.time)
        Log.d("formattedDate", formattedDate)

        currentDateTime.add(Calendar.DAY_OF_MONTH, 1)
        val formatterForEndDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        val batchCodeEndDate = formatterForEndDate.format(currentDateTime.time)
        Log.d("batchCodeEndDate", batchCodeEndDate)
        Log.d("TableName--->.>",TableName)

        jsonObject.put("userName", variantCdTextView.text.toString().split(": ")[1])
        jsonObject.put("itemName", chassisNumberTextView.text.toString().split(": ")[1])
        jsonObject.put("assetId",assetIdToIntOrString)
        jsonObject.put("assetNo", assetNo.text.toString().split(": ")[1])
        jsonObject.put("prodInvNo",inNoByMumCom.text.toString().split(": ")[1])
        jsonObject.put("attribute5", tableName)
        jsonObject.put("createdBy", login_name)
//        jsonObject.put("prodSrlNo",vin)
        jsonObject.put("prodSrlNo", vin.ifEmpty { purchaseInvNoPost })
        jsonObject.put("lastUpdatedBy", login_name)
        jsonObject.put("batchEndDate", batchCodeEndDate)
        jsonObject.put("locId", locId)
        jsonObject.put("ouId", ouId)
        jsonObject.put("insertedBy",login_name)
        jsonObject.put("batchName", batchName)
        jsonObject.put("batchStatus", "open")
        jsonObject.put("batchCreationDate", formattedDate)
        jsonObject.put("prodInvNo",inNoByMumCom.text.toString().split(": ")[1])
        jsonObject.put("remarks",remarks)

        val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
        Log.d("requestBody",requestBody.toString())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseCode = response.code
                val responseBody = response.body?.string()

                Log.d("SaveVinData", "Response Code: $responseCode")
                Log.d("SaveVinData", "Response Body: $responseBody")

                runOnUiThread {
                    if (responseBody != null) {
                        val jsonObject = JSONObject(responseBody)
                        val message = jsonObject.optString("message", "")

                        when {
                            message.contains("All assets against this asset ID have been stocked successfully.", ignoreCase = true) -> {
                                Toast.makeText(
                                    this@StockTaking,
                                    "Entry already done for this asset.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                clearFields()
                            }
                            responseCode == 200 -> {
                                Toast.makeText(
                                    this@StockTaking,
                                    "Asset Stock Taken Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                clearFields()
                            }
                            else -> {
                                Toast.makeText(
                                    this@StockTaking,
                                    "Failed to save data. Error code: $responseCode",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@StockTaking,
                            "No response from server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("SaveVinData", "Error: ${e.message}")
                runOnUiThread {
                    Toast.makeText(
                        this@StockTaking,
                        "Error saving data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    //For Description Post
    private fun saveDataDescriptionWise(iteName: String, batchName: String) {

        val client = OkHttpClient()
        val url = ApiFile.APP_URL + "/faBatch/faBatchNameManual"
        val jsonObject = JSONObject()
        val remarks=remarks.text.toString()


        val currentDateTime = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        val formattedDate = formatter.format(currentDateTime.time)
        Log.d("formattedDate", formattedDate)

        currentDateTime.add(Calendar.DAY_OF_MONTH, 1)
        val formatterForEndDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        val batchCodeEndDate = formatterForEndDate.format(currentDateTime.time)
        Log.d("batchCodeEndDate", batchCodeEndDate)

        jsonObject.put("userName", iteName)
        jsonObject.put("itemName","")
        jsonObject.put("assetId","")
        jsonObject.put("assetNo","Manual")
        jsonObject.put("prodInvNo","-")
        jsonObject.put("attribute5", "-")
        jsonObject.put("createdBy", login_name)
        jsonObject.put("prodSrlNo", "-")
        jsonObject.put("lastUpdatedBy", login_name)
        jsonObject.put("batchEndDate", batchCodeEndDate)
        jsonObject.put("locId", locId)
        jsonObject.put("ouId", ouId)
        jsonObject.put("insertedBy",login_name)
        jsonObject.put("batchName", batchName)
        jsonObject.put("batchStatus", "open")
        jsonObject.put("batchCreationDate", formattedDate)
//        jsonObject.put("prodInvNo","-")
        jsonObject.put("remarks",remarks)

        val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
        Log.d("requestBody",requestBody.toString())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseCode = response.code
                val responseBody = response.body?.string()

                Log.d("SaveVinData", "Response Code: $responseCode")
                Log.d("SaveVinData", "Response Body: $responseBody")

                runOnUiThread {
                    if (responseBody != null) {
                        val jsonObject = JSONObject(responseBody)
                        val message = jsonObject.optString("message", "")

                        when {
                            message.contains("All assets against this asset ID have been stocked successfully.", ignoreCase = true) -> {
                                Toast.makeText(
                                    this@StockTaking,
                                    "All assets against this asset ID have been stocked successfully.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                clearFields()
                            }
                            responseCode == 200 -> {
                                Toast.makeText(
                                    this@StockTaking,
                                    "Asset Stock Taken Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                clearFields()
                            }
                            else -> {
                                Toast.makeText(
                                    this@StockTaking,
                                    "Failed to save data. Error code: $responseCode",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@StockTaking,
                            "No response from server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("SaveVinData", "Error: ${e.message}")
                runOnUiThread {
                    Toast.makeText(
                        this@StockTaking,
                        "Error saving data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun clearFields() {
        itemNameEditText.visibility=View.GONE
        itemNameEditTextFetch.visibility=View.GONE
        prodSrlNo.visibility = View.GONE
        findByprodSrl.visibility = View.GONE
        itemNameEditText.setText("")
        prodSrlNo.setText("")
        assetNoTextView.visibility=View.GONE
        assetNoTextView.text=""
        assetNo.visibility=View.GONE
        assetNo.text=""
        inNoByMumCom.visibility=View.GONE
        inNoByMumCom.text=""
        fullTextView.visibility=View.GONE
        fullTextView.text=""
        variantCdTextView.text = ""
        vehNoTextView.text = ""
        remarks.visibility=View.GONE
        remarks.setText("")
        Department.text=""
        locName.text=""
        locName.visibility=View.GONE
        invNoTextView.text=""
        invNoTextView.visibility=View.GONE
        Department.visibility=View.GONE
        vehNoTextView.visibility=View.GONE
        dmsInvNoTextView.text = ""
        chassisNumberTextView.text = ""
        modelCodeTextView.text = ""
        variant_code.text = ""
        engine2.text = ""
        VinQr.text.clear()
        prodSrlNo.setText("")
        qr_result_textview.text = ""
        prodSrlNo.visibility=View.GONE
        vintypeSpinner.visibility=View.GONE
        findByprodSrl.visibility=View.GONE
        fetchVinData2.visibility=View.GONE
        refreshButton.visibility=View.GONE
        variantCdTextView.visibility=View.GONE
        dmsInvNoTextView.visibility=View.GONE
        chassisNumberTextView.visibility=View.GONE
        modelCodeTextView.visibility=View.GONE
        variant_code.visibility=View.GONE
        engine2.visibility=View.GONE
        Physical_Location.visibility=View.GONE
        save_button.visibility=View.INVISIBLE
        save_button2.visibility=View.INVISIBLE
        save_button3.visibility=View.INVISIBLE
        VinDetails.visibility=View.GONE
        vehNoTextView.visibility=View.GONE
        fetchItemNoData.visibility=View.GONE
        descItemName.setText("")
        descUsername.setText("")
        descDepartment.setText("")
        descItemName.visibility=View.GONE
        descUsername.visibility=View.GONE
        descDepartment.visibility=View.GONE
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

    data class itInventory(
        val userDesigntn:String,
        val locName:String,
        val itemTypeId:String,
        val itemType:String,
        val itemCode:String,
        val userName:String,
        val Department:String,
        val city:String,
        val productserialNo:String,
        val itemsubType:String,
        val ouCity:String,
        val location:String,
        val productMake:String,
        val dept:String,
        val purchaseInvNo:String,
        val assetNo:String,
        val tableName:String,
    )
}
