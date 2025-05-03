package com.example.apinew


//xml file used----- testing_xml.xml

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import com.example.apinew.ApiFile
import com.google.android.material.textfield.TextInputLayout
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class StockTransferNew : AppCompatActivity() {

    private lateinit var chassisTextView: EditText
    private var photoUri: Uri? = null
    private var photoFile: File? = null
    private var chassisNo: String? = null
    private lateinit var qrResultTextView: TextView
    private lateinit var VinQr: EditText
    private var ouId: Int = 0
    private var locId: Int = 0
    private lateinit var attributeTextView: TextView
    private lateinit var location1: TextView
    private lateinit var location: String
    private lateinit var attribute1: String
    private lateinit var login_name: String
    private lateinit var  location_name:String
    private lateinit var deptName:String
    private lateinit var logoutButton: ImageView
    private lateinit var postBtn: Button
    private lateinit var vehicleIn: Button
    private lateinit var fetchChassisDataButton: ImageButton
    private lateinit var detailsByVin:TextView
    private lateinit var VinDetails:TextView
    private lateinit var tableLayout: TableLayout
    private lateinit var chassis_no:EditText
    private lateinit var refreshButton:Button
    private lateinit var showDetailsButton:Button
    private lateinit var homePage: ImageView
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var vintypeSpinner:Spinner
    private lateinit var vehStatusSpinner: Spinner
    private lateinit var fetchVinData2:Button
    private lateinit var currentKmsText:TextView
    private lateinit var currentKms:EditText
    private lateinit var STKNO_1:String
    private lateinit var FRMKM2:String
    private lateinit var VIN1:String
    private lateinit var VEHSTATUS2:String
    private lateinit var currentKmsLayout:TextInputLayout




    private val organizationMap = mutableMapOf<String, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.testing_xml)
        homePage=findViewById(R.id.homePage)
        location1 = findViewById(R.id.location1)
        ouId = intent.getIntExtra("ouId", 0)
        locId = intent.getIntExtra("locId", 0)
        detailsByVin=findViewById(R.id.detailsByVin)
        showDetailsButton=findViewById(R.id.showDetailsButton)
        detailsByVin.visibility=View.GONE
        VinDetails=findViewById(R.id.VinDetails)
        VinDetails.visibility = View.GONE
        qrResultTextView=findViewById(R.id.qrResultTextView)
        chassis_no=findViewById(R.id.chassis_no)
        vintypeSpinner = findViewById(R.id.vintypeSpinner)
        vintypeSpinner.setSelection(0)
        fetchChassisDataButton=findViewById(R.id.fetchChassisDataButton)
        tableLayout=findViewById(R.id.transferTable)
        tableLayout.visibility=View.GONE
        postBtn=findViewById(R.id.postBtn)
        refreshButton=findViewById(R.id.refreshButton)
        cameraExecutor = Executors.newSingleThreadExecutor()
        fetchVinData2=findViewById(R.id.fetchVinData2)
        vehStatusSpinner=findViewById(R.id.vehStatusSpinner)
        vehStatusSpinner.visibility=View.GONE
        currentKmsText=findViewById(R.id.currentKmsText)
        currentKms=findViewById(R.id.currentKms)
        currentKmsText.visibility=View.GONE
        currentKms.visibility=View.GONE
        fetchVinData2.visibility=View.GONE
        currentKmsLayout=findViewById(R.id.currentKmsLayout)
        currentKmsLayout.visibility=View.GONE
        postBtn.visibility=View.GONE
        refreshButton.visibility=View.GONE

        val vin = extractVinFromResult(qrResultTextView.text.toString())
        if (vin.isNotEmpty()) {
            VinQr.setText(vin)
//            fetchVinData(vin)
//            fetchVehicleStatus(vin)
        }  //newly added

        fetchData()

        attribute1 = intent.getStringExtra("attribute1") ?: ""
        location = intent.getStringExtra("location") ?: ""
        location_name = intent.getStringExtra("location_name") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""
        deptName = intent.getStringExtra("deptName") ?: ""



//        organizationSpinner.visibility = View.GONE
//        orgText = findViewById(R.id.orgText)
//        orgText.visibility = View.GONE
//        qrResultTextView.visibility=View.GONE

        logoutButton = findViewById(R.id.logoutButton)
        qrResultTextView = findViewById(R.id.qrResultTextView)
        attributeTextView = findViewById(R.id.attributeTextView)
        attributeTextView.text = "$login_name"
        location1.text = "$location_name"
        vehicleIn=findViewById(R.id.vehicleIn)

        fetchChassisDataButton.visibility = View.GONE
        chassis_no.visibility = View.GONE
        vintypeSpinner.visibility=View.GONE
        VinDetails.visibility=View.GONE
        qrResultTextView.visibility=View.GONE




        showDetailsButton.setOnClickListener {
            toggleDetailsVisibility()
        }


        VinQr = findViewById(R.id.VinQr)
//            fetchVinData(VinQr.toString())

        fetchVinData2.setOnClickListener{
            fetchVinData2(vin)
        }



        fetchChassisDataButton.setOnClickListener {
            val chassisNo = chassis_no.text.toString()
            fetchChassisData(chassisNo)
            vintypeSpinner.visibility=View.VISIBLE
        }

        logoutButton.setOnClickListener {
            logout()
        }

        homePage.setOnClickListener {
            backToHome()
        }

        vehicleIn.setOnClickListener{
//            fetchTransferListData()
//            tableLayout.visibility=View.VISIBLE
            PendingVehicleList()
        }


        refreshButton = findViewById(R.id.refreshButton)
        refreshButton.setOnClickListener {
            resetFields()
        }


        val scanQrBtn: Button = findViewById(R.id.scanQrBtn)
        scanQrBtn.setOnClickListener {
            startQrScanner()
//            fetchChassisDataButton.visibility = View.VISIBLE
            chassis_no.visibility = View.VISIBLE
            vintypeSpinner.visibility=View.VISIBLE
//            vehicleSpinner.visibility = View.VISIBLE
//            organizationSpinner.visibility = View.VISIBLE
//            vehText.visibility=View.VISIBLE
//            Remarks.visibility=View.VISIBLE
//            remarkText.visibility=View.VISIBLE
//            currentKms.visibility=View.VISIBLE
//            currentKmsText.visibility=View.VISIBLE
            VinDetails.visibility=View.VISIBLE
            qrResultTextView.visibility=View.VISIBLE
//            orgText.visibility=View.VISIBLE
            chassis_no.visibility=View.GONE
            vintypeSpinner.visibility=View.GONE
            fetchChassisDataButton.visibility=View.GONE
            fetchVinData2.visibility=View.GONE

        }


        VinQr.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.toString()?.let { vin ->
                    if (vin.isNotEmpty()) {
//                        fetchVehicleStatus(vin)
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        postBtn = findViewById(R.id.postBtn)
        postBtn.setOnClickListener {
            val vinData=vin_data("VARIANT_CD","DMSINVNO","CHASSISNUM","MODEL_CD","ENGINE_NUM","TRANS_REF_NUM",0,VIN1,"LOCATION",VEHSTATUS2,vehStatusSpinner.selectedItem.toString(),STKNO_1,"FROM_LOCATION","FRMKM","TO_LOC")
            postData(vinData)
        }

        tableLayout = findViewById(R.id.transferTable)
        setupTableHeader()
    }

    private fun setupTableHeader() {
        val tableHeader = TableRow(this)
        tableHeader.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )

        val headers = arrayOf("ID", "VIN", "VEH_STATUS", "TRANSFERRED_BY", "REASONCODE","FROM_LOCATION","InButton")
        headers.forEach { header ->
            val textView = TextView(this)
            textView.text = header
            textView.setPadding(16, 16, 16, 16)
            textView.setTypeface(null, Typeface.BOLD)
            textView.setBackgroundColor(Color.parseColor("#CCCCCC"))
            tableHeader.addView(textView)
        }

        tableLayout.addView(tableHeader)
    }



    private fun toggleDetailsVisibility() {
        if (fetchChassisDataButton.visibility == View.GONE) {
            fetchChassisDataButton.visibility = View.VISIBLE
            chassis_no.visibility = View.VISIBLE
//            fetchVinData2.visibility=View.VISIBLE
//            vintypeSpinner.visibility=View.VISIBLE
//            vehicleSpinner.visibility = View.VISIBLE
//            organizationSpinner.visibility = View.VISIBLE
//            vehText.visibility=View.VISIBLE
//            Remarks.visibility=View.VISIBLE
//            remarkText.visibility=View.VISIBLE
//            VinDetails.visibility=View.VISIBLE


        } else {
            fetchChassisDataButton.visibility = View.GONE
            fetchVinData2.visibility=View.GONE
            chassis_no.visibility = View.GONE
            vintypeSpinner.visibility=View.GONE
//            vehicleSpinner.visibility = View.GONE
//            vehicleSpinner.visibility = View.GONE
//            organizationSpinner.visibility = View.GONE
//            vehText.visibility=View.GONE
//            Remarks.visibility=View.GONE
//            remarkText.visibility=View.GONE
            VinDetails.visibility=View.GONE
        }
    }

    private fun resetVintypeSpinner() {
        val vintypeItems = listOf("Select Vin")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, vintypeItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        vintypeSpinner.adapter = adapter
        vintypeSpinner.setSelection(0)
    }


    private fun resetFields() {
        chassis_no.setText("")
        findViewById<TextView>(R.id.detailsByVin).text = ""
        findViewById<TextView>(R.id.qrResultTextView).text = ""
        currentKms.setText("")
        findViewById<Spinner>(R.id.vehStatusSpinner).setSelection(0)
//        findViewById<Spinner>(R.id.organizationSpinner).setSelection(0)
//        findViewById<TextView>(R.id.currentKms).setText("")
//        findViewById<TextView>(R.id.Remarks).setText("")
        vintypeSpinner.setSelection(0)
        qrResultTextView.visibility=View.GONE
        fetchChassisDataButton.visibility = View.GONE
        fetchVinData2.visibility=View.GONE
        chassis_no.visibility = View.GONE
        vintypeSpinner.visibility=View.GONE
        VinDetails.visibility=View.GONE
        postBtn.visibility=View.GONE
        fetchVinData2.visibility=View.GONE
        vehStatusSpinner.visibility=View.GONE
        currentKmsText.visibility=View.GONE
        currentKms.visibility=View.GONE
        currentKmsLayout.visibility=View.GONE
        resetVintypeSpinner()
        val tableLayout = findViewById<TableLayout>(R.id.tableLayout2)
        tableLayout.removeAllViews()
    }

    private fun fetchChassisData(chassis_no: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.APP_URL}/qrcode/qrDetailsByChassis?chassisNo=$chassis_no&ouId=$ouId")
            .build()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseCode = response.code
                val jsonData = response.body?.string()

                if (responseCode == 200 && jsonData != null) {
                    try {
                        val jsonObject = JSONObject(jsonData)
                        Log.d("fetch 363 chassis Data-----", jsonData)
                        val objArray = jsonObject.getJSONArray("obj")
                        Log.d("chassis Array ----",objArray.toString())
                        if (objArray.length() > 0) {
                            val stockItem = objArray.getJSONObject(0)
                            Log.d("in If len----",objArray.toString())
                            jsonData?.let {
                                val parseVindataList = parseVindata(jsonData.toString())
                                runOnUiThread {
                                    val adapter = ArrayAdapter(
                                        this@StockTransferNew,
                                        android.R.layout.simple_spinner_item,
                                        parseVindataList
                                    )
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    vintypeSpinner.adapter = adapter
                                }
                            }
//                            val stockItem = objArray.getJSONObject(0)
                            val chassisData = chassis_data(
                                VIN = stockItem.getString("VIN"),
                                LOCATION=stockItem.getString("LOCATION")
                            )
                            Log.d("Vin:", chassisData.VIN)
                            runOnUiThread {
//                                if (chassisData.LOCATION != location_name) {
//                                    Toast.makeText(
//                                        this@StockTransferNew,
//                                        "Vehicle is not at $location_name",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                    fetchVinData2.visibility=View.GONE
//                                    vintypeSpinner.visibility=View.GONE
//                                    fetchVinData2.visibility=View.GONE
//                                }
//                                else {
                                    qrResultTextView.text = chassisData.VIN
                                    VinQr.setText(qrResultTextView.text.toString())
//                                qrResultTextView.visibility = View.VISIBLE
                                    Toast.makeText(
                                        this@StockTransferNew,
                                        "Details found Successfully \n for Chassis no. $chassis_no",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    vintypeSpinner.visibility=View.VISIBLE
                                    fetchVinData2.visibility=View.VISIBLE
//                                    postBtn.visibility = View.VISIBLE
                                    refreshButton.visibility = View.VISIBLE
//                                    vehText.visibility=View.VISIBLE
//                                    vehicleSpinner.visibility=View.VISIBLE
//                                    remarkText.visibility=View.VISIBLE
//                                    Remarks.visibility=View.VISIBLE
//                                    currentKmsText.visibility=View.VISIBLE
//                                    currentKms.visibility=View.VISIBLE
                                    val vin = qrResultTextView.text.toString()
//                                    fetchVinData(vin)
                                    VinQr.setText(chassisData.VIN)
//                                    fetchVehicleStatus(vin)
//                                }
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(
                                    this@StockTransferNew,
                                    "No details found for Chassis no. $chassis_no",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (jsonException: JSONException) {
                        jsonException.printStackTrace()
                        runOnUiThread {
                            Toast.makeText(
                                this@StockTransferNew,
                                "Failed to parse response",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    Log.d("Error", "Server returned non-200 response: $responseCode")
//                    Log.d("Error", "Response: $jsonData")
                    runOnUiThread {
                        Toast.makeText(
                            this@StockTransferNew,
                            "Failed to fetch data: $responseCode",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@StockTransferNew,
                        "Error fetching data: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
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

    private fun postData(vinData: vin_data) {
        val url = "${ApiFile.APP_URL}/qrcode/updateVehicleStockIN"
        val toKm=currentKms.text.toString()
        val selectedVehStatus = vehStatusSpinner.selectedItem.toString()
        val json = JSONObject().apply {
            put("vin", vinData.VIN)
            put("receivedBy", login_name)
            put("status",selectedVehStatus)
            put("stkTrfNo",vinData.STKNO)
            put("toKm", toKm)
            put("location", location_name)
            put("vehStatus",vinData.VEH_STATUS)
        }
        Log.d("vin_data.FRMKM", vinData.FRMKM)
        Log.d("vin_data.FRMKM222", FRMKM2)
        Log.d("vin_data.TOKM", toKm)
        Log.d("vinData.VIN:",vinData.VIN)
        Log.d("login_name:",login_name)
        Log.d("selectedVehStatus:",selectedVehStatus)
        Log.d("vinData.STKNO:",vinData.STKNO)
        Log.d("toKm:",toKm)
        Log.d("location_name:",location_name)
        Log.d("vinData.VEH_STATUS:",vinData.VEH_STATUS)



        if(vinData.VEH_STATUS!="In-Transit") {
           if (toKm.isEmpty()) {
               Toast.makeText(this, "To KM is required", Toast.LENGTH_SHORT).show()
               return
           }
       }

//        if(vinData.VEH_STATUS!="In-Transit") {
//            if (toKm.toFloat() < FRMKM2.toFloat()) {
//                Toast.makeText(this, "To KM must be greater than From KM", Toast.LENGTH_SHORT)
//                    .show()
//                return
//            }
//        }

        if(vinData.VEH_STATUS!="In-Transit") {
            if (toKm.toFloat() < FRMKM2.toFloat()) {
                Toast.makeText(this, "To KM must be greater than From KM", Toast.LENGTH_SHORT)
                    .show()
                return
            }
        }

        if (selectedVehStatus=="Choose Vehicle Status") {
            Toast.makeText(this, "Select Vehicle status first", Toast.LENGTH_SHORT).show()
            return
        }

        val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        Log.d("URL FOR UPADATE:",json.toString())
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
                    Toast.makeText(this@StockTransferNew, "Failed to update vehicle status", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(this@StockTransferNew, "Vehicle status updated successfully", Toast.LENGTH_SHORT).show()
                            resetFields()
                        }
                    } else {
                        val responseBody = it.body?.string() ?: ""
                        val errorMessage = if (responseBody.contains("Invalid VIN")) {
                            "Invalid VIN"
                        } else {
                            "Unexpected code ${it.code}"
                        }
                        runOnUiThread {
                            Toast.makeText(this@StockTransferNew, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }

    private fun populateFields(vin_data: vin_data) {
        val table = findViewById<TableLayout>(R.id.tableLayout2)
        table.removeAllViews()

        STKNO_1=vin_data.STKNO
        VIN1=vin_data.VIN
        VEHSTATUS2=vin_data.VEH_STATUS
        FRMKM2=vin_data.FRMKM
        val detailsMap = mutableMapOf(
            "VIN NO" to vin_data.VIN,
            "CHASSIS NO" to vin_data.CHASSIS_NUM,
            "ENGINE NO" to vin_data.ENGINE_NUM,
            "MODEL CODE" to vin_data.MODEL_CD,
            "VARIANT CODE" to vin_data.VARIANT_CD,
            "DMS INV NO" to vin_data.DMSINVNO,
            "SOB NO" to vin_data.TRANS_REF_NUM,
            "VEH STATUS" to vin_data.VEH_STATUS,
            "STATUS" to vin_data.STATUS,
            "STKNO" to vin_data.STKNO,
            "FROM LOC" to vin_data.FROM_LOCATION,
            "FROM KM" to vin_data.FRMKM
        )

        if (vin_data.VEH_STATUS != "Stock Transfer In-Transit") {
            detailsMap["LOCATION"] = vin_data.LOCATION
        }

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

    private fun fetchVinData(vin: String) {
        val client = OkHttpClient()
        val url = ApiFile.APP_URL + "/qrcode/qrDetailsByVin?vin=$vin"
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
                    Log.d("Data", it)
                    val stockItem = jsonObject.getJSONArray("obj").getJSONObject(0)
                    val vinData = vin_data(
                        VARIANT_CD = stockItem.getString("VARIANT_CD"),
                        DMSINVNO = stockItem.getString("DMSINVNO"),
                        CHASSIS_NUM = stockItem.getString("CHASSIS_NUM"),
                        MODEL_CD = stockItem.getString("MODEL_CD"),
                        ENGINE_NUM = stockItem.getString("ENGINE_NUM"),
                        GATEPASSYN = stockItem.getInt("GATEPASSYN"),
                        TRANS_REF_NUM = stockItem.getString("TRANS_REF_NUM"),
                        VIN = stockItem.getString("VIN"),
                        LOCATION = stockItem.getString("LOCATION"),
                        VEH_STATUS=stockItem.getString("VEH_STATUS"),
                        STATUS=stockItem.getString("STATUS"),
                        STKNO=stockItem.getString("STKNO"),
                        FROM_LOCATION=stockItem.getString("FROM_LOCATION"),
                        FRMKM =stockItem.getString("FRMKM"),
                        TO_LOC =stockItem.getString("TO_LOC")
                    )
                    runOnUiThread {
//                        if(deptName!="ACCOUNTS") {
                        Log.d("Vin LOcation------->",vinData.LOCATION)
                        Log.d("Vin FRMKMMMMM------->",vinData.FRMKM)

                        if (vinData.TO_LOC!= location_name && vinData.LOCATION!=location_name) {
                            Toast.makeText(
                                this@StockTransferNew,
                                "Vehicle is not at $location_name",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
//                        }
                        else {
                            populateFields(vinData)
                            VinDetails.visibility = View.VISIBLE
                            detailsByVin.visibility = View.VISIBLE
                            postBtn.visibility = View.VISIBLE
                            currentKmsText.visibility=View.VISIBLE
                            currentKms.visibility=View.VISIBLE
                            refreshButton.visibility = View.VISIBLE
                            vintypeSpinner.visibility=View.GONE
                            fetchVinData2.visibility=View.GONE
                            refreshButton.visibility = View.VISIBLE
                            if(vinData.VEH_STATUS=="In-Transit"){
                                postBtn.visibility = View.VISIBLE
                                currentKmsText.visibility=View.GONE
                                currentKms.visibility=View.GONE
                                vehStatusSpinner.visibility=View.VISIBLE
                                currentKmsLayout.visibility=View.GONE
                            } else if(vinData.VEH_STATUS=="Stock Transfer In-Transit"){
                                postBtn.visibility = View.VISIBLE
                                currentKmsText.visibility=View.VISIBLE
                                currentKms.visibility=View.VISIBLE
                                vehStatusSpinner.visibility=View.VISIBLE
                                currentKmsLayout.visibility=View.VISIBLE
                            }
                            else{
                                postBtn.visibility = View.GONE
                                currentKmsText.visibility=View.GONE
                                currentKms.visibility=View.GONE
                                vehStatusSpinner.visibility=View.GONE
                            }
                            Toast.makeText(
                                this@StockTransferNew,
                                "Details found Successfully \n for VIN: $vin",
                                Toast.LENGTH_LONG
                            ).show()
                            Log.d("Vin LOcation------->",vinData.LOCATION)
                            Log.d("Vin FRMKMMMMM------->",vinData.FRMKM)
                            val vin = qrResultTextView.text.toString()
//                            fetchVehicleStatus(vin)
                            updateSpinnerOptions(vinData)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@StockTransferNew,
                        "Failed to fetch details for VIN: $vin",
                        Toast.LENGTH_SHORT
                    ).show()
                    qrResultTextView.visibility=View.GONE
                    fetchChassisDataButton.visibility = View.GONE
                    fetchVinData2.visibility=View.GONE
                    chassis_no.visibility = View.GONE
                    postBtn.visibility=View.GONE
                }
            }
        }
    }

    private fun fetchVinData2(vin: String) {
        val client = OkHttpClient()
        val vin2=vintypeSpinner.selectedItem.toString()
        if (vin2=="Select Vin"){
            Toast.makeText(this@StockTransferNew, "Please select the vin number", Toast.LENGTH_SHORT).show()
        }
        Log.d("vin2--->",vin2)
        val url = ApiFile.APP_URL + "/qrcode/qrDetailsByVin?vin=$vin2"
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
                    Log.d("Data", it)
                    val stockItem = jsonObject.getJSONArray("obj").getJSONObject(0)

                    val vinData = vin_data(
                        VARIANT_CD = stockItem.getString("VARIANT_CD"),
                        DMSINVNO = stockItem.getString("DMSINVNO"),
                        CHASSIS_NUM = stockItem.getString("CHASSIS_NUM"),
                        MODEL_CD = stockItem.getString("MODEL_CD"),
                        ENGINE_NUM = stockItem.getString("ENGINE_NUM"),
                        GATEPASSYN = stockItem.getInt("GATEPASSYN"),
                        TRANS_REF_NUM = stockItem.getString("TRANS_REF_NUM"),
                        VIN = stockItem.getString("VIN"),
                        LOCATION = stockItem.getString("LOCATION"),
                        VEH_STATUS=stockItem.getString("VEH_STATUS"),
                        STATUS=stockItem.getString("STATUS"),
                        STKNO=stockItem.getString("STKNO"),
                        FROM_LOCATION=stockItem.getString("FROM_LOCATION"),
                        FRMKM =stockItem.getString("FRMKM"),
                        TO_LOC =stockItem.getString("TO_LOC")
                    )
                    runOnUiThread {
                        Log.d("Vin Location------->",vinData.LOCATION)
                        Log.d("TO Location------->",vinData.TO_LOC)
                        Log.d("Vin Location------->",location_name)

                        if (vinData.TO_LOC!= location_name && vinData.LOCATION!=location_name) {//vinData.LOCATION
                            Toast.makeText(
                                this@StockTransferNew,
                                "Vehicle is not at $location_name",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else {
                            populateFields(vinData)
                            VinDetails.visibility = View.VISIBLE
                            detailsByVin.visibility = View.VISIBLE
                            refreshButton.visibility = View.VISIBLE
                            vintypeSpinner.visibility=View.VISIBLE
                            fetchVinData2.visibility=View.VISIBLE
                            if(vinData.VEH_STATUS=="In-Transit"){
                                postBtn.visibility = View.VISIBLE
                                currentKmsText.visibility=View.GONE
                                currentKms.visibility=View.GONE
                                vehStatusSpinner.visibility=View.VISIBLE
                                currentKmsLayout.visibility=View.GONE
                            } else if(vinData.VEH_STATUS=="Stock Transfer In-Transit"){
                                postBtn.visibility = View.VISIBLE
                                currentKmsText.visibility=View.VISIBLE
                                currentKms.visibility=View.VISIBLE
                                vehStatusSpinner.visibility=View.VISIBLE
                                currentKmsLayout.visibility=View.VISIBLE
                            }
                            else{
                                postBtn.visibility = View.GONE
                                currentKmsText.visibility=View.GONE
                                currentKms.visibility=View.GONE
                                vehStatusSpinner.visibility=View.GONE
                            }
                            Toast.makeText(
                                this@StockTransferNew,
                                "Details found Successfully \n for VIN: $vin2",
                                Toast.LENGTH_LONG
                            ).show()
                            Log.d("Vin Location------->",vinData.LOCATION)
                            Log.d("Vin FRMKMMMMM------->",vinData.FRMKM)
                            val vin = qrResultTextView.text.toString()
//                            fetchVehicleStatus(vin)
                            updateSpinnerOptions(vinData)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@StockTransferNew,
                        "Failed to fetch details for VIN: $vin",
                        Toast.LENGTH_SHORT
                    ).show()
                    qrResultTextView.visibility=View.GONE
                    fetchChassisDataButton.visibility = View.GONE
                    fetchVinData2.visibility=View.GONE
                    chassis_no.visibility = View.GONE
                    postBtn.visibility=View.GONE
                }
            }
        }
    }

    private fun fetchData() {
        val spinnerItems = mutableListOf("Choose Vehicle Status")

        val adapter = object : ArrayAdapter<String>(
            this@StockTransferNew,
            android.R.layout.simple_spinner_item,
            spinnerItems
        ) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view as TextView

                if (position == 0) {
                    textView.setTextColor(ContextCompat.getColor(context, R.color.red))
                } else {
                    textView.setTextColor(ContextCompat.getColor(context, android.R.color.black))
                }

                return view
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        vehStatusSpinner.adapter = adapter

        vehStatusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            )
            {
//                val selectedVehicle = spinnerItems[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateSpinnerOptions(vinData: vin_data) {
        val spinnerItems = mutableListOf("Choose Vehicle Status")
        when (vinData.VEH_STATUS) {
            "In-Transit" -> spinnerItems.add("Vehicle Intransit - Stock In")
            "Stock Transfer In-Transit" -> spinnerItems.add("Stock Transfer Intransit - Stock In")
            else -> {
            }
        }
        val adapter = ArrayAdapter(
            this@StockTransferNew,
            android.R.layout.simple_spinner_item,
            spinnerItems
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        vehStatusSpinner.adapter = adapter
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
//                    Toast.makeText(this@StockTransferNew, "Failed to fetch vehicle status", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    handleVehicleStatus(responseBody)
                } else {
                    runOnUiThread {
                        Toast.makeText(this@StockTransferNew, "Error: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun handleVehicleStatus(status: String?) {
        runOnUiThread {
            if (status != null) {
                if (status.equals("INTRANSIT", ignoreCase = true)) {
                    postBtn.visibility=View.GONE
                    Toast.makeText(this, "Vehicle is Intransit", Toast.LENGTH_LONG).show()
                }
                else if(status.equals("DELIVERED", ignoreCase = true)){
                    Toast.makeText(this, "Vehicle is Delivered", Toast.LENGTH_LONG).show()
                }
                else {
                    fetchChassisDataButton.visibility = View.VISIBLE
                    fetchVinData2.visibility=View.VISIBLE
                    chassis_no.visibility = View.VISIBLE
                    VinDetails.visibility=View.VISIBLE
                    postBtn.visibility=View.VISIBLE
                }
            } else {
                Toast.makeText(this, "Error: Null status received", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startQrScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("Scan a QR Code or Barcode")
        integrator.setCameraId(0)
        integrator.setBeepEnabled(true)
        integrator.setBarcodeImageEnabled(true)
        integrator.setOrientationLocked(false)
        integrator.initiateScan()
        Handler(Looper.getMainLooper()).postDelayed({
            Toast.makeText(this, "Could not scan.Please try again.", Toast.LENGTH_SHORT).show()
        }, 10000)
//        postBtn.visibility=View.VISIBLE
        refreshButton.visibility=View.VISIBLE

    }

    private fun extractVinFromResult(result: String): String {
        val keyValuePairs = result.split(",")
        var vinNumber = ""
        for (pair in keyValuePairs) {
            val keyValue = pair.split(":")
            if (keyValue.size == 2) {
                val key = keyValue[0].trim()
                val value = keyValue[1].trim()
                if (key.equals("VIN", ignoreCase = true)) {
                    vinNumber = value
                    break
                }
            }
        }
        return vinNumber
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
                qrResultTextView.visibility=View.GONE
                fetchChassisDataButton.visibility = View.GONE
                fetchVinData2.visibility=View.GONE
                chassis_no.visibility = View.GONE
                VinDetails.visibility=View.GONE
                postBtn.visibility=View.GONE
            } else {
                val vin = result.contents
                qrResultTextView.visibility = View.GONE
                vintypeSpinner.visibility=View.GONE
                fetchVinData2.visibility=View.GONE
                qrResultTextView.text = vin
                VinQr.setText(vin)
                fetchVinData(vin)
//                fetchVehicleStatus(vin)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun logout() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun PendingVehicleList() {
        val intent = Intent(this, PendingVehicleList::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name",location_name)
        intent.putExtra("deptName",deptName)
        startActivity(intent)
    }


    private fun backToHome() {
        finish()
    }

    data class Organization(
        val LOCID: Int,
        val ORGANIZATION_NAME: String,
        val LOCATIONNAME:String
    )

    data class Vehicle(
        val CMNDESC: String
    )


    data class vin_data(
        val VARIANT_CD: String,
        val DMSINVNO: String,
        val CHASSIS_NUM: String,
        val MODEL_CD: String,
        val ENGINE_NUM: String,
        val TRANS_REF_NUM:String,
        val GATEPASSYN:Int,
        val VIN:String,
        val LOCATION:String,
        val VEH_STATUS :String,
        val STATUS :String,
        val STKNO:String,
        val FROM_LOCATION:String,
        val FRMKM:String,
        val TO_LOC:String
    )


//    data class Vehicle2(
//        val LOCATION: String,
//        val VEHSTATUS: String,
//        val TRANSFERRED_BY: String,
//        val REASONCODE: String,
//        val VIN: String,
//        val FROM_LOCATION:String,
//        val FRMKM:String,
//        val STOCK_TRF_NO:String
//    )

    data class chassis_data(
        val VIN: String,
        val LOCATION:String
    )

    data class City(
        val ATTRIBUTE1:String,
        val CMNDESC:String,

        )

}


