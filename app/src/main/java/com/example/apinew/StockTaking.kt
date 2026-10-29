package com.example.apinew

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.core.view.isNotEmpty
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
import java.text.ParseException
import java.util.TimeZone
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.io.File

class StockTaking : AppCompatActivity() {

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
    private lateinit var save_button2:Button
    private lateinit var toCutOffDateBatchName:String
    private lateinit var buttonChecker:Button
    private lateinit var cutOffDateSpinner:Spinner
    private lateinit var batchStatus:String
    private lateinit var jsonBatchStatus:String
//    private val VIN_REGEX = Regex("^[A-HJ-NPR-Z0-9]{17}$")
    private val BARCODE_REQUEST_CODE = 400
    private lateinit var currentBarcodePath: String
    private lateinit var scanner: GmsBarcodeScanner


    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.O)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_taking)

        cutOffDateSpinner=findViewById(R.id.cutOffDateSpinner)

        scanButton = findViewById(R.id.scan_button)
        save_button = findViewById(R.id.save_button)
        save_button2 = findViewById(R.id.save_button2)
        VinQr = findViewById(R.id.vin_qr_edittext)
        VinDetails = findViewById(R.id.VinDetails)
        variantCdTextView = findViewById(R.id.variant_cd_textview)
        dmsInvNoTextView = findViewById(R.id.dms_inv_no_textview)
        chassisNumberTextView = findViewById(R.id.chassis_number_textview)
        modelCodeTextView = findViewById(R.id.model_code_textview)
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
        locId = intent.getIntExtra("locId", 0)
        qr_result_textview = findViewById(R.id.qr_result_textview)
        ouId = intent.getIntExtra("ouId", 0)
        deptName = intent.getStringExtra("deptName") ?: ""
        location_name = intent.getStringExtra("location_name") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""
        VinDetails.visibility = View.GONE
        save_button.visibility = View.GONE
        save_button2.visibility = View.GONE

        engine2 = findViewById(R.id.engine2)
        refreshButton = findViewById(R.id.refreshButton)
        fetchChassisDataButton = findViewById(R.id.fetchChassisDataButton)
        chassis_no = findViewById(R.id.chassis_no)
        findByChassis = findViewById(R.id.findByChassis)
        vintypeSpinner = findViewById(R.id.vintypeSpinner)
        vintypeSpinner.visibility = View.GONE
        fetchVinData2 = findViewById(R.id.fetchVinData2)
        fetchVinData2.visibility = View.GONE
//        fetchVinData("MBJTYKK1SRE122674")
////        qr_result_textview.text="MBJTYKL1SRE240101"
//        VinQr.setText("MBJTYKK1SRE122674")
        chassis_no.visibility = View.GONE
        findByChassis.visibility = View.GONE
        variantCdTextView.visibility = View.GONE
        dmsInvNoTextView.visibility = View.GONE
        chassisNumberTextView.visibility = View.GONE
        modelCodeTextView.visibility = View.GONE
        variant_code.visibility = View.GONE
        Physical_Location.visibility = View.GONE
        engine2.visibility = View.GONE
        buttonChecker=findViewById(R.id.buttonChecker)
        findBybatchNameOpenStatus()
//        findBybatchNameStatus()

//        findBybatchNameStatus()

        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_CODE_128,
                Barcode.FORMAT_CODE_39,
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_DATA_MATRIX
            )
            .enableAutoZoom() // Very important for windshield stickers
            .build()

        scanner = GmsBarcodeScanning.getClient(this, options)


        val currentDate: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val currentMonth: String = SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(Date())

//        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
//        if (currentDay > 5) {
//            scanButton.visibility = View.GONE
//            fetchChassisDataButton.visibility = View.GONE
//        } else {
//            scanButton.visibility = View.VISIBLE
//            fetchChassisDataButton.visibility = View.VISIBLE
//        }

        usernameText.text = "$login_name"
        locationText.text = "$location_name"

//        batchEditText.setText("$location_name-$currentDate")

//        batchEditText.setText("Borivali(E)-01-11-2024")

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

        findByChassis.setOnClickListener {
            val chassisNo = chassis_no.text.toString()
            fetchChassisData(chassisNo)
//            findBybatchNameOpenStatus()
        }

        fetchChassisDataButton.setOnClickListener {
            runOnUiThread {
//                checkBatchCutoff()
                chassis_no.visibility = View.VISIBLE
                findByChassis.visibility = View.VISIBLE
            }
        }

//        Handler(Looper.getMainLooper()).postDelayed({
//            checkBatchCutoff()
//        }, 1000)


//        fetchCutOffDates()

//        scanButton.setOnClickListener {
//            val integrator = IntentIntegrator(this)
//            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
//            integrator.setPrompt("Scan a QR Code or Barcode")
//            integrator.setCameraId(0)
//            integrator.setBeepEnabled(true)
//            integrator.setBarcodeImageEnabled(true)
//            integrator.setOrientationLocked(false)
//            integrator.initiateScan()
//            Handler(Looper.getMainLooper()).postDelayed({
////                Toast.makeText(this, "Could not scan.Please try again.", Toast.LENGTH_SHORT).show()
//            }, 10000)
//        }


        scanButton.setOnClickListener {
            scanner.startScan()
                .addOnSuccessListener { barcode ->
                    val raw = barcode.rawValue ?: return@addOnSuccessListener
                    val vin = raw
                        .replace(Regex("[^A-Za-z0-9]"), "")
                        .uppercase(Locale.US)
                        .trim()

                    Log.d("BARCODE_GMS", "Raw: '$raw' → Cleaned: '$vin' (len=${vin.length})")

                    val finalVin = when {
                        vin.length == 17 && vin[0] == 'M' && VIN_REGEX.matches(vin) -> vin
                        vin.length == 17 -> vin
                        vin.length > 17 -> extractVinFromScan(vin)
                        else -> null
                    }

                    if (finalVin != null) {
                        qr_result_textview.visibility = View.VISIBLE
                        qr_result_textview.text = finalVin
                        VinQr.setText(finalVin)
                        Toast.makeText(this, "✓ VIN: $finalVin", Toast.LENGTH_SHORT).show()
                        fetchVinData(finalVin)
                    } else {
                        // Show what was scanned — let user verify
                        qr_result_textview.visibility = View.VISIBLE
                        qr_result_textview.text = vin
                        VinQr.setText(vin)
                        Toast.makeText(
                            this,
                            "Got: $vin — please verify",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                .addOnCanceledListener {
                    Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.e("BARCODE_GMS", "Failed: ${e.message}")
                    Toast.makeText(this, "Scan failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }




        fetchVinData2.setOnClickListener {
            val vin = vintypeSpinner.selectedItem.toString()
            fetchVinData2(vin)
        }

        save_button.setOnClickListener {

            val vin = VinQr.text.toString()
            val selectedBatchName = multipleBatchNameSpinner.selectedItem?.toString() ?: ""

            when {
                selectedBatchName == "Select Batch Name" -> {
                    Toast.makeText(this,"Please select a Batch Name first", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }

                selectedBatchName.isNotEmpty() -> {
                    if (vin.isNotEmpty()  && vin!="Select Vin") {
                        saveVinData(vin, selectedBatchName)
                    } else {
                        Toast.makeText(this, "VIN is empty", Toast.LENGTH_SHORT).show()
                    }
                }

                else -> {
                    val batchName = batchEditText.text.toString()
                    if (vin.isNotEmpty() && vin!="Select Vin") {
                        saveVinData(vin, batchName)
                    } else {
                        Toast.makeText(this, "VIN or Batch Name is empty", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        save_button2.setOnClickListener {

            val vin = vintypeSpinner.selectedItem.toString()
            val selectedBatchName = multipleBatchNameSpinner.selectedItem?.toString() ?: ""

            when {
                selectedBatchName == "Select Batch Name" -> {
                    Toast.makeText(this,"Please select a Batch Name first", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }

                selectedBatchName.isNotEmpty() -> {
                    if (vin.isNotEmpty() && vin!="Select Vin") {
                        saveVinData2(vin, selectedBatchName)
                    } else {
                        Toast.makeText(this, "VIN is empty or Select the Vin", Toast.LENGTH_SHORT).show()
                    }
                }

                else -> {
                    val batchName = batchEditText.text.toString()
                    if (vin.isNotEmpty() && vin!="Select Vin") {
                        saveVinData2(vin, batchName)
                    } else {
                        Toast.makeText(this, "VIN or Batch Name is empty", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }


    }

    private fun fetchCutOffDates() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.ERP_APP_URL}/accounts/batchCutofDate?ou=$ouId")
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
                    Log.d("BATCH_CREATION_DATE",date)
                    batchDates.add(date)
                }

                runOnUiThread {
                    val cutOffDateSpinner: Spinner = findViewById(R.id.cutOffDateSpinner)
                    val fetchChassisDataButton: Button = findViewById(R.id.fetchChassisDataButton)
                    val scanButton: Button = findViewById(R.id.scan_button)

                    if (batchDates.isEmpty()) {
                        Toast.makeText(this@StockTaking, "Stock not uploaded for this Month", Toast.LENGTH_SHORT).show()
                        fetchChassisDataButton.isEnabled = false
                        scanButton.isEnabled = false
                        return@runOnUiThread
                    }

                    val spinnerItems = mutableListOf("Select Cut Off Date").apply { addAll(batchDates) }

                    val adapter = ArrayAdapter(
                        this@StockTaking,
                        android.R.layout.simple_spinner_item,
                        spinnerItems
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    cutOffDateSpinner.adapter = adapter

                    fetchChassisDataButton.isEnabled = false
                    scanButton.isEnabled = false

                    cutOffDateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                            if (parent.getItemAtPosition(position) == "Select Cut Off Date" && batchDates.isNotEmpty()) {
                                fetchChassisDataButton.isEnabled = false
                                scanButton.isEnabled = false
                                batchEditText.setText("$location_name")
                            } else {
                                val spinnerDate=cutOffDateSpinner.selectedItem.toString()
                                fetchChassisDataButton.isEnabled = true
                                scanButton.isEnabled = true
                                batchEditText.setText("$location_name-$spinnerDate")
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            fetchChassisDataButton.isEnabled = false
                            scanButton.isEnabled = false
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@StockTaking, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun findBybatchNameOpenStatus() {
        val LocationName = "$location_name"
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.ERP_APP_URL}/accounts/findExBatchNameStatus?location=$LocationName")
            .build()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                val jsonObject = JSONObject(jsonData.toString())
                val jsonArray = jsonObject.getJSONArray("obj")
                val arrayLn = jsonArray.length()
                jsonBatchStatus=arrayLn.toString()

                if (jsonArray.length() == 0) {
                    runOnUiThread {
                        findBybatchNameStatus()
                        fetchCutOffDates()
                        save_button.visibility = View.INVISIBLE
                        save_button2.visibility = View.INVISIBLE
                        cutOffDateSpinner.visibility = View.VISIBLE
                    }
                    return@launch
                }

                val batStatus = jsonArray.getJSONObject(0)
                val status = batStatus.getString("BATCHSTATUS")
                Log.d("BATCHSTATUS",status)
                batchStatus=status
                runOnUiThread {
                    if (status.equals("open", ignoreCase = true)) {
                        multipleBatchNameSpinner.visibility = View.VISIBLE
                        batchEditText.visibility = View.INVISIBLE
                        cutOffDateSpinner.visibility=View.GONE
                        fetchBatchData()
                    }
                    else if (status.equals("Closed", ignoreCase = true)) {
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
//        val batchName = ("Borivali(E)-29-07-2024")
        val LocationName = "$location_name"
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.ERP_APP_URL}/accounts/findBatchNameStatus?batchName=${batchName}&location=$LocationName")
            .build()
        Log.d("URL-:",request.toString())
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()

                val jsonObject = JSONObject(jsonData.toString())
                val jsonArray = jsonObject.getJSONArray("obj")
                val batStatus = jsonArray.getJSONObject(0)
                val batchName = batStatus.getString("batchName")
                val batchStatus = batStatus.getString("batchStatus")
                Log.d("batchStatus--->",batchStatus)

                if (jsonArray.length() === 0){
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
            .url("${ApiFile.ERP_APP_URL}/accounts/batchNameOpen?locationName=${location_name}")
            .build()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()

                jsonData?.let {
                    val batchCodeList = parseCities(it)
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
//            }
//        } else {
//            super.onActivityResult(requestCode, resultCode, data)
//        }
//    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {

            BARCODE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val file = File(currentBarcodePath)
                    if (file.exists()) {
                        Toast.makeText(this, "Reading barcode...", Toast.LENGTH_SHORT).show()
                        Thread {
                            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                            if (bitmap != null) {
                                scanBarcodeWithMlKit(bitmap)
                            } else {
                                runOnUiThread {
                                    Toast.makeText(this, "Could not load image", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }.start()
                    }
                }
            }

            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun scanBarcodeWithMlKit(bitmap: Bitmap) {
        val scanner = BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                    Barcode.FORMAT_CODE_128,
                    Barcode.FORMAT_CODE_39,
                    Barcode.FORMAT_QR_CODE,
                    Barcode.FORMAT_DATA_MATRIX
                )
                .build()
        )

        val image = InputImage.fromBitmap(bitmap, 0)

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isEmpty()) {
                    // No barcode found — try OCR on the text directly as fallback
                    Log.d("BARCODE_MLKIT", "No barcode found — trying OCR fallback")
                    runOnUiThread {
                        Toast.makeText(
                            this,
                            "Barcode not detected. Try again closer\nor read text directly.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    return@addOnSuccessListener
                }

                // Got at least one barcode
                for (barcode in barcodes) {
                    val raw = barcode.rawValue ?: continue
                    val vin = raw
                        .replace(Regex("[^A-Za-z0-9]"), "")
                        .uppercase(Locale.US)
                        .trim()

                    Log.d("BARCODE_MLKIT", "Raw: '$raw' → Cleaned: '$vin' (len=${vin.length})")

                    // Extract VIN — M anchor only
                    val finalVin = when {
                        vin.length == 17 && vin[0] == 'M' && VIN_REGEX.matches(vin) -> vin
                        vin.length == 17 -> vin
                        vin.length > 17 -> extractVinFromScan(vin)
                        else -> null
                    }

                    if (finalVin != null) {
                        runOnUiThread {
                            qr_result_textview.visibility = View.VISIBLE
                            qr_result_textview.text = finalVin
                            VinQr.setText(finalVin)
                            Toast.makeText(this, "✓ VIN: $finalVin", Toast.LENGTH_SHORT).show()
                            fetchVinData(finalVin)
                        }
                        return@addOnSuccessListener
                    }
                }

                // Barcodes found but none gave a valid VIN
                val firstRaw = barcodes[0].rawValue ?: ""
                runOnUiThread {
                    VinQr.setText(firstRaw)
                    qr_result_textview.text = firstRaw
                    qr_result_textview.visibility = View.VISIBLE
                    Toast.makeText(
                        this,
                        "Got: $firstRaw — please verify",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("BARCODE_MLKIT", "Failed: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this, "Scan failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun extractVinFromScan(raw: String): String? {
        val cleaned = raw.replace(Regex("[^A-Z0-9]"), "")
        var i = 0
        while (i < cleaned.length) {
            val mi = cleaned.indexOf('M', i)
            if (mi < 0) break
            if (cleaned.length >= mi + 17) {
                val candidate = cleaned.substring(mi, mi + 17)
                if (VIN_REGEX.matches(candidate)) return candidate
            }
            i = mi + 1
        }
        // No regex match — return first M-anchored 17 chars
        val mIndex = cleaned.indexOf('M')
        if (mIndex >= 0 && cleaned.length >= mIndex + 17) {
            return cleaned.substring(mIndex, mIndex + 17)
        }
        return null
    }

    private val VIN_REGEX = Regex("^[A-HJ-NPR-Z0-9]{17}$")

    // M-anchor extraction from longer barcode scan results
//    private fun extractVinFromScan(raw: String): String? {
//        val cleaned = raw.replace(Regex("[^A-Z0-9]"), "")
//
//        // Find every M — take 17 chars, check if valid VIN
//        var i = 0
//        while (i < cleaned.length) {
//            val mi = cleaned.indexOf('M', i)
//            if (mi < 0) break
//            if (cleaned.length >= mi + 17) {
//                val candidate = cleaned.substring(mi, mi + 17)
//                if (VIN_REGEX.matches(candidate)) return candidate
//            }
//            i = mi + 1
//        }
//
//        // No perfect match — return first M-anchored 17 chars anyway
//        val mIndex = cleaned.indexOf('M')
//        if (mIndex >= 0 && cleaned.length >= mIndex + 17) {
//            return cleaned.substring(mIndex, mIndex + 17)
//        }
//
//        return null
//    }

    private fun resetFields() {
        chassis_no.setText("")
        vintypeSpinner.visibility=View.GONE
//        findViewById<TextView>(R.id.detailsByVin).text = ""
//        findViewById<TextView>(R.id.qrResultTextView).text = ""
//        findViewById<Spinner>(R.id.vehicleSpinner).setSelection(0)
//        findViewById<Spinner>(R.id.organizationSpinner).setSelection(0)
        qr_result_textview.text=""
//        fetchChassisDataButton.visibility = View.GONE
//        chassis_no.visibility = View.GONE
        VinDetails.visibility=View.INVISIBLE
        VinDetails.text=""
        variantCdTextView.text=""
        dmsInvNoTextView.text=""
        chassisNumberTextView.text=""
        findByChassis.visibility=View.INVISIBLE
        chassis_no.visibility=View.GONE
        modelCodeTextView.text=""
        variant_code.text=""
        Physical_Location.text=""
        engine2.text=""
        resetVintypeSpinner()
        variantCdTextView.visibility=View.INVISIBLE
        dmsInvNoTextView.visibility=View.INVISIBLE
        chassisNumberTextView.visibility=View.INVISIBLE
        modelCodeTextView.visibility=View.INVISIBLE
        variant_code.visibility=View.INVISIBLE
        Physical_Location.visibility=View.INVISIBLE
        engine2.visibility=View.INVISIBLE
        save_button.visibility=View.INVISIBLE
        save_button2.visibility=View.INVISIBLE

        vintypeSpinner.setSelection(0)
        fetchVinData2.visibility=View.INVISIBLE
    }

    private fun fetchChassisData(chassis_no: String) {
        val client = OkHttpClient()
        val request = Request.Builder()

            .url("${ApiFile.ERP_APP_URL}/qrcode/qrDetailsByChassisBatch?chassisNo=$chassis_no&ouId=$ouId")
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseCode = response.code
                val jsonData = response.body?.string()
                if (responseCode == 200 && jsonData != null) {
                    try {
                        val jsonObject = JSONObject(jsonData)
                        val objArray = jsonObject.getJSONArray("obj")

                        if (objArray.length() > 0) {
                            val stockItem = objArray.getJSONObject(0)
                            jsonData?.let {
                                val parseVindataList = parseVindata(jsonData.toString())
                                runOnUiThread {
                                    val adapter = ArrayAdapter(
                                        this@StockTaking,
                                        android.R.layout.simple_spinner_item,
                                        parseVindataList
                                    )
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    vintypeSpinner.adapter = adapter
                                }
                            }

                            val chassisData = chassis_data(
                                VIN = stockItem.getString("VIN"),
                                LOCATION=stockItem.getString("LOCATION")
                            )
                            runOnUiThread {
                                qr_result_textview.text = chassisData.VIN
                                qr_result_textview.visibility=View.GONE
                                VinQr.setText(qr_result_textview.text.toString())
                                refreshButton.visibility=View.GONE
                                vintypeSpinner.visibility=View.VISIBLE
                                fetchVinData2.visibility=View.VISIBLE
                                Toast.makeText(
                                    this@StockTaking,
                                    "Details found Successfully \n for Chassis no. $chassis_no",
                                    Toast.LENGTH_SHORT
                                ).show()
//                                    fetchVinData(chassisData.VIN)
                                val vin = qr_result_textview.text.toString()
                                VinQr.setText(chassisData.VIN)
                                fetchVehicleStatus(vin)
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(
                                    this@StockTaking,
                                    "No details found for Chassis no. $chassis_no",
                                    Toast.LENGTH_SHORT
                                ).show()
                                refreshButton.visibility=View.GONE
                            }
                        }
                    } catch (jsonException: JSONException) {
                        jsonException.printStackTrace()
                        runOnUiThread {
                            Toast.makeText(
                                this@StockTaking,
                                "Failed to parse response",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this@StockTaking,
                            "Failed to fetch data: $responseCode",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@StockTaking,
                        "Error fetching data: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun resetVintypeSpinner() {
        val vintypeItems = listOf("Select Vin")
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

    private fun fetchVehicleStatus(vin: String) {
        val url = "${ApiFile.ERP_APP_URL}/qrcode/vehStatusByVin?vin=$vin"

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@StockTaking, "Failed to fetch vehicle status", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    handleVehicleStatus(responseBody)
                } else {
                    runOnUiThread {
//                        Toast.makeText(this@StockTaking, "Error: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun handleVehicleStatus(status: String?) {
        runOnUiThread {
            if (status != null) {
                if (status.equals("INTRANSIT", ignoreCase = true)) {
//
                    Toast.makeText(this, "Vehicle is INTRANSIT", Toast.LENGTH_LONG).show()
                } else {
                    fetchChassisDataButton.visibility = View.VISIBLE
                    chassis_no.visibility = View.VISIBLE
                    VinDetails.visibility=View.VISIBLE
                }
            } else {
                Toast.makeText(this, "Error: Null status received", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun fetchVinData(vin: String) {
        val client = OkHttpClient()
        val url =ApiFile.ERP_APP_URL+"/qrcode/detailsByVin?vin=$vin"
//        val url =ApiFile.ERP_APP_URL+"/qrcode/detailsByVin?vin=MBJTYKK1SRE122674"


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
                        populateFields(vinData)
                        qr_result_textview.visibility=View.VISIBLE
                        VinDetails.visibility = View.VISIBLE
                        refreshButton.visibility=View.VISIBLE
                        Toast.makeText(
                            this@StockTaking,
                            "Details found Successfully \n for VIN: $vin",
                            Toast.LENGTH_LONG
                        ).show()
                        VinDetails.visibility=View.VISIBLE
                        save_button.visibility=View.VISIBLE
                        save_button2.visibility=View.INVISIBLE
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
                        this@StockTaking,
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
//        val url = ApiFile.ERP_APP_URL+"/accounts/vehDetailsByVin?vin=MBJTYKL1SRE240101"
        val url =ApiFile.ERP_APP_URL+"/qrcode/detailsByVin?vin=$vin2"
//        val url =ApiFile.ERP_APP_URL+"/qrcode/detailsByVin?vin=MBJTYKK1SRE122674"
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
                        populateFields(vinData)
                        VinDetails.visibility = View.VISIBLE
                        refreshButton.visibility=View.VISIBLE
                        Toast.makeText(
                            this@StockTaking,
                            "Details found Successfully \n for VIN: $vin",
                            Toast.LENGTH_LONG
                        ).show()
                        VinDetails.visibility=View.VISIBLE
                        save_button.visibility=View.INVISIBLE
                        save_button2.visibility=View.VISIBLE
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
                        this@StockTaking,
                        "Failed to fetch details for VIN: $vin",
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
        startActivity(intent)
    }

    private fun populateFields(vin_data: vinData) {
        variantCdTextView.text = "Chassis No.: ${vin_data.CHASSIS_NUM}"
        dmsInvNoTextView.text = "Fuel Desc: ${vin_data.FUEL_DESC}"
        chassisNumberTextView.text = "Model CD: ${vin_data.MODEL_CD}"
        modelCodeTextView.text = "Colour: ${vin_data.COLOUR}"
        variant_code.text="Variant Code: ${vin_data.VARIANT_CD}"
        Physical_Location.text="Physical Location: ${vin_data.LOCATION}"
        engine2.text="Engine Number: ${vin_data.ENGINE_NO}"
    }


    private fun saveVinData(vin: String, batchName: String) {

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
        val url = ApiFile.ERP_APP_URL + "/accounts/batchNameScan"
        val jsonObject = JSONObject()

        val currentDateTime = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        val formattedDate = formatter.format(currentDateTime.time)



        currentDateTime.add(Calendar.DAY_OF_MONTH, 1)
        val formatterForEndDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        val batchCodeEndDate = formatterForEndDate.format(currentDateTime.time)

        jsonObject.put("vin", vin)
        jsonObject.put("chassis_no", variantCdTextView.text.toString().split(": ")[1])
        jsonObject.put("model_desc", chassisNumberTextView.text.toString().split(": ")[1])
        jsonObject.put("colour", modelCodeTextView.text.toString().split(": ")[1])
        jsonObject.put("fuel_desc", dmsInvNoTextView.text.toString().split(": ")[1])
        jsonObject.put("engin_no", engine2.text.toString().split(": ")[1])
        jsonObject.put("batchName", batchName)
        jsonObject.put("locationId", locId)
        jsonObject.put("locationName", locationText.text.toString())
        jsonObject.put("createdBy", login_name)
        jsonObject.put("batchStatus", "open")
        jsonObject.put("creationDate", formattedDate)
//        if (batchStatus == "Closed" && dateTimecToSend != null) {
//            jsonObject.put("batchCreationDate", dateTimeToSend)
//        }
        if(jsonBatchStatus.toInt()==0){
            jsonObject.put("batchCreationDate", dateTimeToSend)
        } else if (batchStatus == "Closed" && dateTimeToSend != null) {
            jsonObject.put("batchCreationDate", dateTimeToSend)
        }
        jsonObject.put("batchCodeEndDate", batchCodeEndDate)
        jsonObject.put("updatedBy", login_name)
        jsonObject.put("updationDate", formattedDate)

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
                            message.contains("VIN already exists for this batchName", ignoreCase = true) -> {
                                Toast.makeText(
                                    this@StockTaking,
                                    "Stock taking is already completed for this vehicle",
                                    Toast.LENGTH_LONG
                                ).show()
                                clearFields()
                            }
                            responseCode == 200 -> {
                                Toast.makeText(
                                    this@StockTaking,
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


    private fun saveVinData2(vin: String, batchName: String) {

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
        val url = ApiFile.ERP_APP_URL + "/accounts/batchNameManual"
        val jsonObject = JSONObject()

        val currentDateTime = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        val formattedDate = formatter.format(currentDateTime.time)

        currentDateTime.add(Calendar.DAY_OF_MONTH, 1)
        val formatterForEndDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        val batchCodeEndDate = formatterForEndDate.format(currentDateTime.time)
//        val batchCreationDt=cutOffDateSpinner.selectedItem.toString()
//        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
//
//        val dateTimeToSend = "$batchCreationDt $currentTime"


        jsonObject.put("vin", vin)
        jsonObject.put("chassis_no", variantCdTextView.text.toString().split(": ")[1])
        jsonObject.put("model_desc", chassisNumberTextView.text.toString().split(": ")[1])
        jsonObject.put("colour", modelCodeTextView.text.toString().split(": ")[1])
        jsonObject.put("fuel_desc", dmsInvNoTextView.text.toString().split(": ")[1])
        jsonObject.put("engin_no", engine2.text.toString().split(": ")[1])
        jsonObject.put("batchName", batchName)
        jsonObject.put("locationId", locId)
        jsonObject.put("locationName", locationText.text.toString())
        jsonObject.put("createdBy", login_name)
        jsonObject.put("batchStatus", "open")
        jsonObject.put("creationDate", formattedDate)
//        jsonObject.put("batchCreationDate", dateTimeToSend)
        if(jsonBatchStatus.toInt()==0){
            jsonObject.put("batchCreationDate", dateTimeToSend)
        } else if (batchStatus == "Closed" && dateTimeToSend != null) {
            jsonObject.put("batchCreationDate", dateTimeToSend)
        }
        jsonObject.put("batchCodeEndDate", batchCodeEndDate)
        jsonObject.put("updatedBy", login_name)
        jsonObject.put("updationDate", formattedDate)

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
                            message.contains("VIN already exists for this batchName", ignoreCase = true) -> {
                                Toast.makeText(
                                    this@StockTaking,
                                    "Stock taking is already completed for this vehicle",
                                    Toast.LENGTH_LONG
                                ).show()
                                clearFields()
                            }
                            responseCode == 200 -> {
                                Toast.makeText(
                                    this@StockTaking,
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
        variantCdTextView.text = ""
        dmsInvNoTextView.text = ""
        chassisNumberTextView.text = ""
        modelCodeTextView.text = ""
        variant_code.text = ""
        engine2.text = ""
        VinQr.text.clear()
        chassis_no.setText("")
        qr_result_textview.text = ""
        resetVintypeSpinner()
        chassis_no.visibility=View.GONE
        vintypeSpinner.visibility=View.GONE
        findByChassis.visibility=View.GONE
        fetchVinData2.visibility=View.GONE
        refreshButton.visibility=View.GONE
        variantCdTextView.visibility=View.GONE
        dmsInvNoTextView.visibility=View.GONE
        chassisNumberTextView.visibility=View.GONE
        modelCodeTextView.visibility=View.GONE
        variant_code.visibility=View.GONE
        engine2.visibility=View.GONE
        Physical_Location.visibility=View.GONE
        save_button.visibility=View.GONE
        save_button2.visibility=View.GONE
        VinDetails.visibility=View.GONE
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

}
