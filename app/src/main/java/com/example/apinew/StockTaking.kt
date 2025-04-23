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

        scanButton.setOnClickListener {
//            checkBatchCutoff()
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
            val vin = vintypeSpinner.selectedItem.toString()
            fetchVinData2(vin)
//            findBybatchNameStatus()
        }

        save_button.setOnClickListener {

            val vin = VinQr.text.toString()
//            val vin = vintypeSpinner.selectedItem.toString()
            val selectedBatchName = multipleBatchNameSpinner.selectedItem?.toString() ?: ""

            when {
                selectedBatchName == "Select Batch Name" -> {
                    Toast.makeText(this,"Please select a Batch Name first", Toast.LENGTH_SHORT)
                        .show()
                }

                selectedBatchName.isNotEmpty() -> {
                    if (vin.isNotEmpty()) {
                        saveVinData(vin, selectedBatchName)
                    } else {
                        Toast.makeText(this, "VIN is empty", Toast.LENGTH_SHORT).show()
                    }
                }

                else -> {
                    val batchName = batchEditText.text.toString()
                    if (vin.isNotEmpty()) {
                        saveVinData(vin, batchName)
                    } else {
                        Toast.makeText(this, "VIN or Batch Name is empty", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        save_button2.setOnClickListener {

//            val vin = VinQr.text.toString()
            val vin = vintypeSpinner.selectedItem.toString()
            val selectedBatchName = multipleBatchNameSpinner.selectedItem?.toString() ?: ""

            when {
                selectedBatchName == "Select Batch Name" -> {
                    Toast.makeText(this,"Please select a Batch Name first", Toast.LENGTH_SHORT)
                        .show()
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
                    if (vin.isNotEmpty()) {
                        saveVinData2(vin, batchName)
                    } else {
                        Toast.makeText(this, "VIN or Batch Name is empty", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
//        buttonChecker.setOnClickListener {
//            checkBatchCutoff()
//        }


    }




//    private fun checkBatchCutoff() {
//        val cutoffDate: String? = when {
//            ::toCutOffDateBatchName.isInitialized && toCutOffDateBatchName.isNotEmpty() -> parseCutoffDate(toCutOffDateBatchName)
//            !batchEditText.text.isNullOrEmpty() -> parseCutoffDate(batchEditText.text.toString())
//            else -> null
//        }
//
//        if (cutoffDate.isNullOrEmpty()) {
//            runOnUiThread {
//                Toast.makeText(this, "Invalid cutoff date", Toast.LENGTH_SHORT).show()
//            }
//            return
//        }
//
//        val client = OkHttpClient()
//        val apiUrl =
//            "${ApiFile.APP_URL}/accounts/batchCutofDate?ou=$ouId&cutOfDate=$cutoffDate"
//
//        val request = Request.Builder()
//            .url(apiUrl)
//            .build()
//
//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                val response = client.newCall(request).execute()
//                val responseBody = response.body?.string()
//
//                responseBody?.let {
//                    val jsonObject = JSONObject(it)
//                    val objArray = jsonObject.getJSONArray("obj")
//                    val count = objArray.getJSONObject(0).getInt("COUNT(*)")
//
//                    runOnUiThread {
//                        if (count > 0) {
//                            fetchChassisDataButton.isEnabled = true
//                            scanButton.isEnabled = true
//                            chassis_no.isEnabled =true
//                            findByChassis.isEnabled =true
//                            Log.d("count > 0",apiUrl)
//                        } else {
//                            fetchChassisDataButton.isEnabled = false
//                            scanButton.isEnabled = false
//                            chassis_no.isEnabled =false
//                            findByChassis.isEnabled =false
//                            Toast.makeText(
//                                this@StockTaking,
//                                "Stock is not uploaded for the\ncurrent date $cutoffDate in batch name !!!",
//                                Toast.LENGTH_LONG
//                            ).show()
//                            Log.d("count < 0",apiUrl)
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                runOnUiThread {
//                    Toast.makeText(
//                        this@StockTaking,
//                        "Error checking batch cutoff: ${e.message}",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    Log.d("count not fetch",apiUrl)
//
//                }
//            }
//        }
//    }
//
//
//
//    private fun parseCutoffDate(input: String): String? {
//        val regex = Regex("""\d{2}-\d{2}-\d{4}|\d-\d{2}-\d{4}""")
//        val match = regex.find(input)?.value ?: return null
//
//        return try {
//            val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(match)
//            SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(date)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }


    private fun fetchCutOffDates() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.APP_URL}/accounts/batchCutofDate?ou=$ouId")
            .build()

        Log.d("RequestCutOffCheck->", "fetchCutOffDatesCheck")


        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                Log.d("RequestCutOff->", request.toString())

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



//    private fun findBybatchNameOpenStatus(){
//        val LocationName = "$location_name"
//        val client = OkHttpClient()
//        val request = Request.Builder()
//            .url("${ApiFile.APP_URL}/accounts/findExBatchNameStatus?location=$LocationName")
//            .build()
//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                val response = client.newCall(request).execute()
//                val jsonData = response.body?.string()
//                Log.d("jsonData------", jsonData.toString())
//                val jsonObject = JSONObject(jsonData.toString())
//                val jsonArray = jsonObject.getJSONArray("obj")
//                Log.d("jsonDataCheck", jsonArray.toString())
////                val status =  jsonArray[0].toString()
//                val batStatus = jsonArray.getJSONObject(0)
//                val status = batStatus.getString("BATCHSTATUS")
//                Log.d("status------", status)
//                if (status.equals("open") ){
//                    Toast.makeText(this@StockTaking, "The batch is OPEN!", Toast.LENGTH_SHORT).show()
//                    Log.d("jsonDataCheckOpenStatus", jsonData.toString())
//                    multipleBatchNameSpinner.visibility=View.VISIBLE
//                    batchEditText.visibility= View.INVISIBLE
////                    findBybatchNameOpenStatus()
//                   fetchBatchData()
//                }
//                else if (status.equals("Closed") ){
//                    findBybatchNameStatus()
//                    save_button.visibility=View.INVISIBLE
//                    Toast.makeText(
//                        this@StockTaking,
//                        "The batch is closed!",
//                        Toast.LENGTH_SHORT
//                    ).show()
//
//                }
//            }
//            catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }

    private fun findBybatchNameOpenStatus() {
        val LocationName = "$location_name"
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.APP_URL}/accounts/findExBatchNameStatus?location=$LocationName")
            .build()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                Log.d("jsonData------", jsonData.toString())
                val jsonObject = JSONObject(jsonData.toString())
                val jsonArray = jsonObject.getJSONArray("obj")
                Log.d("jsonDataCheck", jsonArray.toString())
                Log.d("jsonArray Length->>>",jsonArray.length().toString())
                val arrayLn = jsonArray.length()
                jsonBatchStatus=arrayLn.toString()
                Log.d("arrayLn------", arrayLn.toString())

                if (jsonArray.length() == 0) {
                    runOnUiThread {
                        Log.d("jsonArray Empty", "The JSON array is empty.")
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
                batchStatus=status
                Log.d("status------", status)
                runOnUiThread {
                    if (status.equals("open", ignoreCase = true)) {
                        Log.d("jsonDataCheckOpenStatus", jsonData.toString())
                        multipleBatchNameSpinner.visibility = View.VISIBLE
                        batchEditText.visibility = View.INVISIBLE
                        cutOffDateSpinner.visibility=View.GONE
                        fetchBatchData()
                        Log.d("in if","in if-->cutoffdATE")
                    }
                    else if (status.equals("Closed", ignoreCase = true)) {
                        Log.d("in else","in else-->CUTOFFDATE")
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
        Log.d("LocationName----LocationName", LocationName)
        Log.d("batchName----batchName", batchName)
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.APP_URL}/accounts/findBatchNameStatus?batchName=${batchName}&location=$LocationName")
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

                if (jsonArray.length() === 0){
                    Log.d("jsonDataCheckIf", jsonData.toString())
                    batchEditText.visibility=View.VISIBLE
                    multipleBatchNameSpinner.visibility= View.INVISIBLE
                } else{
                    Log.d("jsonDataCheckelse", jsonData.toString())
                    batchEditText.visibility= View.GONE
                    multipleBatchNameSpinner.visibility=View.VISIBLE
                    fetchBatchData()
                }
                Log.d("nss---->", batchName.toString())
                Log.d("nss2--->", batchEditText.text.toString())
                if(batchName.toString()==batchEditText.text.toString()&&batchStatus.toString()=="Closed"){
                    save_button.visibility=View.INVISIBLE
                    save_button2.visibility=View.INVISIBLE
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
            .url("${ApiFile.APP_URL}/accounts/batchNameOpen?locationName=${location_name}")
            .build()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                Log.d("jsonDataBatchList", jsonData.toString())

                jsonData?.let {
                    val batchCodeList = parseCities(it)
                    Log.d("batchCodeList----428",batchCodeList.toString())
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
        Log.d("batchList Checking451----",batchCodeList.toString())
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


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

            .url("${ApiFile.APP_URL}/qrcode/qrDetailsByChassisBatch?chassisNo=$chassis_no&ouId=$ouId")
//            http://localhost:8081/qrcode/qrDetailsByChassisDelv?chassisNo=375203
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseCode = response.code
                val jsonData = response.body?.string()
                Log.d("Chassis_response--->",jsonData.toString())
                Log.d("deptName----------", deptName)
                if (responseCode == 200 && jsonData != null) {
                    try {
                        val jsonObject = JSONObject(jsonData)
                        Log.d("DatachassisData----", jsonData)
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
                                Log.d("Vin:", chassisData.VIN)
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
                    Log.d("Error", "Server returned non-200 response: $responseCode")
                    Log.d("Error", "Response: $jsonData")
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
        val vintypeItems = listOf("Select Batch Name")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, vintypeItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        vintypeSpinner.adapter = adapter
        vintypeSpinner.setSelection(0)
    }

    private fun parseVindata(objArray: String): List<String> {
        Log.d("Second Fn----",objArray)
        val parseVindataList = mutableListOf<String>()
        Log.d("parseVindataList---- After Call", parseVindataList.toString())
        try {
            val jsonObject = JSONObject(objArray)
            val jsonArray = jsonObject.getJSONArray("obj")
            Log.d("jsonArray----",jsonArray.toString())
            parseVindataList.add("Select Vin")
            for (i in 0 until jsonArray.length()) {
                val parseVindataLst = jsonArray.getJSONObject(i)
                val vin = parseVindataLst.getString("VIN")
                Log.d("vin----In For Loop", vin)
                parseVindataList.add(vin)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return parseVindataList
    }

    private fun fetchVehicleStatus(vin: String) {
        val url = "${ApiFile.APP_URL}/qrcode/vehStatusByVin?vin=$vin"

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
        val url =ApiFile.APP_URL+"/qrcode/detailsByVin?vin=$vin"
//        val url =ApiFile.APP_URL+"/qrcode/detailsByVin?vin=MBJTYKK1SRE122674"

        Log.d("URL:", url)

        val request = Request.Builder()
            .url(url)
            .build()
        Log.d("Vin no:", vin)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    Log.d("Data", jsonObject.toString())
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
//        val url = ApiFile.APP_URL+"/accounts/vehDetailsByVin?vin=MBJTYKL1SRE240101"
        val url =ApiFile.APP_URL+"/qrcode/detailsByVin?vin=$vin2"
//        val url =ApiFile.APP_URL+"/qrcode/detailsByVin?vin=MBJTYKK1SRE122674"
        Log.d("URL:", url)
        val request = Request.Builder()
            .url(url)
            .build()
        Log.d("Vin no:", vin)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    Log.d("Data", jsonObject.toString())

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

                    Log.d("fuelDesc",vinData.FUEL_DESC)
                    Log.d("Vin Data", vinData.toString())

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
        Log.d("BatchName", batchName)
        Log.d("vinIn", vin)

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

            Log.d("FinalDateTimeToSend", dateTimeToSend)
        }

        val client = OkHttpClient()
        val url = ApiFile.APP_URL + "/accounts/batchNameScan"
        val jsonObject = JSONObject()

        val currentDateTime = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        val formattedDate = formatter.format(currentDateTime.time)


        Log.d("formattedDate", formattedDate)

        currentDateTime.add(Calendar.DAY_OF_MONTH, 1)
        val formatterForEndDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        val batchCodeEndDate = formatterForEndDate.format(currentDateTime.time)
        Log.d("batchCodeEndDate", batchCodeEndDate)

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

    
    private fun saveVinData2(vin: String, batchName: String) {
        Log.d("BatchName", batchName)
        Log.d("vinIn", vin)

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

            Log.d("FinalDateTimeToSend", dateTimeToSend)
        }


        val client = OkHttpClient()
        val url = ApiFile.APP_URL + "/accounts/batchNameManual"
        val jsonObject = JSONObject()

        val currentDateTime = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        val formattedDate = formatter.format(currentDateTime.time)
        Log.d("formattedDate", formattedDate)

        currentDateTime.add(Calendar.DAY_OF_MONTH, 1)
        val formatterForEndDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        val batchCodeEndDate = formatterForEndDate.format(currentDateTime.time)
//        val batchCreationDt=cutOffDateSpinner.selectedItem.toString()
//        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
//
//        val dateTimeToSend = "$batchCreationDt $currentTime"

        Log.d("formattedDate", formattedDate)
//        Log.d("dateTimeToSend", dateTimeToSend)
        Log.d("batchCodeEndDate", batchCodeEndDate)

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
