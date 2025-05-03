package com.example.apinew

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
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
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraActivity : AppCompatActivity() {

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
    private lateinit var orgText: TextView
    private lateinit var attribute1: String
    private lateinit var login_name: String
    private lateinit var  location_name:String
    private lateinit var deptName:String
    private lateinit var organizationSpinner: Spinner
    private lateinit var vehicleSpinner: Spinner
    private lateinit var logoutButton: ImageView
    private lateinit var postBtn: Button
    private lateinit var vehicleIn: Button
    private lateinit var fetchChassisDataButton: ImageButton
    private lateinit var Remarks: EditText
    private lateinit var detailsByVin:TextView
    private lateinit var VinDetails:TextView
    private lateinit var tableLayout: TableLayout
    private lateinit var chassis_no:EditText
    private lateinit var refreshButton:Button
    private lateinit var showDetailsButton:Button
    private lateinit var homePage: ImageButton
    private lateinit var vehText: TextView
    private lateinit var remarkText: TextView
    private  lateinit var currentKmsText: TextView
    private lateinit var currentKms: EditText
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var previewView: PreviewView
    private lateinit var vintypeSpinner:Spinner
    private lateinit var fetchVinData2:Button


    private val organizationMap = mutableMapOf<String, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        homePage=findViewById(R.id.homePage)
        vehicleSpinner = findViewById(R.id.vehicleSpinner)
        organizationSpinner = findViewById(R.id.organizationSpinner)
        location1 = findViewById(R.id.location1)
        ouId = intent.getIntExtra("ouId", 0)
        locId = intent.getIntExtra("locId", 0)
        Remarks = findViewById(R.id.Remarks)
        detailsByVin=findViewById(R.id.detailsByVin)
        showDetailsButton=findViewById(R.id.showDetailsButton)
        detailsByVin.visibility=View.GONE
        VinDetails=findViewById(R.id.VinDetails)
        vehText=findViewById(R.id.vehText)
        VinDetails.visibility = View.GONE
        qrResultTextView=findViewById(R.id.qrResultTextView)
        chassis_no=findViewById(R.id.chassis_no)
        vintypeSpinner = findViewById(R.id.vintypeSpinner)
        vintypeSpinner.setSelection(0)
        fetchChassisDataButton=findViewById(R.id.fetchChassisDataButton)
        tableLayout=findViewById(R.id.transferTable)
        remarkText=findViewById(R.id.remarkText)
        Remarks=findViewById(R.id.Remarks)
        currentKmsText=findViewById(R.id.currentKmsText)
        currentKms=findViewById(R.id.currentKms)
        tableLayout.visibility=View.GONE
        orgText=findViewById(R.id.orgText)
        postBtn=findViewById(R.id.postBtn)
        refreshButton=findViewById(R.id.refreshButton)
        previewView = findViewById(R.id.previewView)
        cameraExecutor = Executors.newSingleThreadExecutor()
        fetchVinData2=findViewById(R.id.fetchVinData2)


        postBtn.visibility=View.GONE
        refreshButton.visibility=View.GONE

        val vin = extractVinFromResult(qrResultTextView.text.toString())
        if (vin.isNotEmpty()) {
            VinQr.setText(vin)
        }

        fetchOrganizations()
        fetchData()

        attribute1 = intent.getStringExtra("attribute1") ?: ""
        location = intent.getStringExtra("location") ?: ""
        location_name = intent.getStringExtra("location_name") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""
        deptName = intent.getStringExtra("deptName") ?: ""
        logoutButton = findViewById(R.id.logoutButton)
        qrResultTextView = findViewById(R.id.qrResultTextView)
        attributeTextView = findViewById(R.id.attributeTextView)
        attributeTextView.text = "$login_name"
        location1.text = "$location_name"
        vehicleIn=findViewById(R.id.vehicleIn)


        fetchChassisDataButton.visibility = View.GONE
        chassis_no.visibility = View.GONE
        vintypeSpinner.visibility=View.GONE
        vehicleSpinner.visibility = View.GONE
        vehicleSpinner.visibility = View.GONE
        organizationSpinner.visibility = View.GONE
        vehText.visibility=View.GONE
        Remarks.visibility=View.GONE
        remarkText.visibility=View.GONE
        currentKmsText.visibility= View.GONE
        currentKms.visibility= View.GONE
        VinDetails.visibility=View.GONE
        qrResultTextView.visibility=View.GONE
        orgText.visibility=View.GONE

        showDetailsButton.setOnClickListener {
            toggleDetailsVisibility()
        }


        VinQr = findViewById(R.id.VinQr)

        fetchVinData2.setOnClickListener{
            fetchVinData2(vin)
        }

        fetchChassisDataButton.setOnClickListener {
            val chassisNo = chassis_no.text.toString()
            fetchChassisData(chassisNo)
            vehText.visibility=View.GONE
            vehicleSpinner.visibility=View.GONE
            remarkText.visibility=View.GONE
            Remarks.visibility=View.GONE
            currentKms.visibility=View.GONE
            currentKmsText.visibility=View.GONE
            vintypeSpinner.visibility=View.VISIBLE
        }

        logoutButton.setOnClickListener {
            logout()
        }

        homePage.setOnClickListener {
            backToHome()
        }

        vehicleIn.setOnClickListener{
            PendingVehicleList()
        }


        refreshButton = findViewById(R.id.refreshButton)
        refreshButton.setOnClickListener {
            resetFields()
        }


        val scanQrBtn: Button = findViewById(R.id.scanQrBtn)
        scanQrBtn.setOnClickListener {
            startQrScanner()
            chassis_no.visibility = View.VISIBLE
            vintypeSpinner.visibility=View.VISIBLE
            VinDetails.visibility=View.VISIBLE
            qrResultTextView.visibility=View.VISIBLE
            chassis_no.visibility=View.GONE
            vintypeSpinner.visibility=View.GONE
            fetchChassisDataButton.visibility=View.GONE
            fetchVinData2.visibility=View.GONE

        }

        VinQr.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.toString()?.let { vin ->

                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        postBtn = findViewById(R.id.postBtn)
        postBtn.setOnClickListener {
            postData()
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

        } else {
            fetchChassisDataButton.visibility = View.GONE
            fetchVinData2.visibility=View.GONE
            chassis_no.visibility = View.GONE
            vintypeSpinner.visibility=View.GONE
            VinDetails.visibility=View.GONE
            orgText.visibility=View.GONE
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
        Remarks.setText("")
        findViewById<TextView>(R.id.detailsByVin).text = ""
        findViewById<TextView>(R.id.qrResultTextView).text = ""
        findViewById<Spinner>(R.id.vehicleSpinner).setSelection(0)
        findViewById<Spinner>(R.id.organizationSpinner).setSelection(0)
        findViewById<TextView>(R.id.currentKms).setText("")
        findViewById<TextView>(R.id.Remarks).setText("")
        vintypeSpinner.setSelection(0)
        qrResultTextView.visibility=View.GONE
        fetchChassisDataButton.visibility = View.GONE
        fetchVinData2.visibility=View.GONE
        chassis_no.visibility = View.GONE
        vintypeSpinner.visibility=View.GONE
        vehicleSpinner.visibility = View.GONE
        vehicleSpinner.visibility = View.GONE
        organizationSpinner.visibility = View.GONE
        vehText.visibility=View.GONE
        Remarks.visibility=View.GONE
        remarkText.visibility=View.GONE
        currentKms.visibility=View.GONE
        currentKmsText.visibility=View.GONE
        VinDetails.visibility=View.GONE
        orgText.visibility=View.GONE
        postBtn.visibility=View.GONE
        fetchVinData2.visibility=View.GONE
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
                        val objArray = jsonObject.getJSONArray("obj")
                        if (objArray.length() > 0) {
                            val stockItem = objArray.getJSONObject(0)
                            jsonData?.let {
                                val parseVindataList = parseVindata(jsonData.toString())
                                runOnUiThread {
                                    val adapter = ArrayAdapter(
                                        this@CameraActivity,
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
                                if (chassisData.LOCATION != location_name) {
                                    Toast.makeText(
                                        this@CameraActivity,
                                        "Vehicle is not at $location_name",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    fetchVinData2.visibility=View.GONE
                                    vintypeSpinner.visibility=View.GONE
                                    fetchVinData2.visibility=View.GONE
                                }
                                else {
                                    qrResultTextView.text = chassisData.VIN
                                    VinQr.setText(qrResultTextView.text.toString())
                                    Toast.makeText(
                                        this@CameraActivity,
                                        "Details found Successfully \n for Chassis no. $chassis_no",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    vintypeSpinner.visibility=View.VISIBLE
                                    fetchVinData2.visibility=View.VISIBLE
                                    refreshButton.visibility = View.VISIBLE
                                    val vin = qrResultTextView.text.toString()
                                    VinQr.setText(chassisData.VIN)
                                }
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(
                                    this@CameraActivity,
                                    "No details found for Chassis no. $chassis_no",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (jsonException: JSONException) {
                        jsonException.printStackTrace()
                        runOnUiThread {
                            Toast.makeText(
                                this@CameraActivity,
                                "Failed to parse response",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this@CameraActivity,
                            "Failed to fetch data: $responseCode",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@CameraActivity,
                        "Error fetching data: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
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

    private fun postData() {
        val vin = qrResultTextView.text.toString()
        val reasonCode = vehicleSpinner.selectedItem.toString()
        val location = location1.text.toString()
        val created_by = attributeTextView.text.toString()
        val transferred_by = attributeTextView.text.toString()
        val organizationName = organizationSpinner.selectedItem.toString()
        val organization_id = organizationMap[organizationName] ?: 0
        val to_location = organization_id
        val remarks = Remarks.text.toString()
        val locIdValue = locId.toString()
        val lacation_name=organizationName
        val frmKm=currentKms.text.toString()


        if(locId==to_location){
            Toast.makeText(this, "Cannot transfer the vehicle to same location", Toast.LENGTH_LONG).show()
            return
        }

        if (vin.isEmpty()) {
            Toast.makeText(this, "VIN is required", Toast.LENGTH_SHORT).show()
            return
        }
        if (reasonCode.isEmpty() || reasonCode == "Choose Vehicle Status") {
            Toast.makeText(this, "Please select a valid reason code", Toast.LENGTH_SHORT).show()
            return
        }
        if (organization_id == 0 && reasonCode == "Stock Transfer") {
            Toast.makeText(this, "Please select an organization for Stock Transfer", Toast.LENGTH_SHORT).show()
            return
        }
        if (created_by.isEmpty()) {
            Toast.makeText(this, "Created By is required", Toast.LENGTH_SHORT).show()
            return
        }
        if (transferred_by.isEmpty()) {
            Toast.makeText(this, "Transferred By is required", Toast.LENGTH_SHORT).show()
            return
        }
        if (remarks.isEmpty()) {
            Toast.makeText(this, "Remarks are required", Toast.LENGTH_SHORT).show()
            return
        }

        if (frmKm.isEmpty()) {
            Toast.makeText(this, "frmKm is required", Toast.LENGTH_SHORT).show()
            return
        }

        val url = ApiFile.APP_URL+"/qrcode/vehTrackingByVin/"

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("vin", vin)
            .addFormDataPart("reasonCode", reasonCode)
            .addFormDataPart("location", location)
            .addFormDataPart("veh_status", "")
            .addFormDataPart("from_location", locIdValue)
            .addFormDataPart("to_location", to_location.toString())
            .addFormDataPart("organization_id", ouId.toString())
            .addFormDataPart("transferred_by", transferred_by)
            .addFormDataPart("remarks", remarks)
            .addFormDataPart("created_by", created_by)
            .addFormDataPart("frmKm", frmKm.toString())
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val client = OkHttpClient()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@CameraActivity, "Vehicle stock transferred to location $lacation_name!", Toast.LENGTH_LONG).show()
                        chassis_no.setText("")
                        Remarks.setText("")
                        findViewById<TextView>(R.id.detailsByVin).text = ""
                        findViewById<TextView>(R.id.qrResultTextView).text = ""
                        findViewById<Spinner>(R.id.vehicleSpinner).setSelection(0)
                        findViewById<Spinner>(R.id.organizationSpinner).setSelection(0)

                        qrResultTextView.visibility=View.GONE
                        fetchChassisDataButton.visibility = View.GONE
                        fetchVinData2.visibility=View.GONE
                        chassis_no.visibility = View.GONE
                        vehicleSpinner.visibility = View.GONE
                        vehicleSpinner.visibility = View.GONE
                        organizationSpinner.visibility = View.GONE
                        vehText.visibility=View.GONE
                        Remarks.visibility=View.GONE
                        currentKms.visibility=View.GONE
                        currentKmsText.visibility=View.GONE
                        remarkText.visibility=View.GONE
                        VinDetails.visibility=View.GONE
                        orgText.visibility=View.GONE
                        postBtn.visibility=View.GONE
                    } else {
                        Toast.makeText(this@CameraActivity, "Failed to post data: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@CameraActivity, "Failed to post data due to exception: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun populateFields(vin_data: vin_data) {
        val table = findViewById<TableLayout>(R.id.tableLayout2)
        table.removeAllViews()

        val detailsMap = mapOf(
            "VIN NO" to vin_data.VIN,
            "CHASSIS NO" to vin_data.CHASSIS_NUM,
            "ENGINE NO" to vin_data.ENGINE_NUM,
            "MODEL CODE" to vin_data.MODEL_CD,
            "VARIANT CODE" to vin_data.VARIANT_CD,
            "DMS INV NO" to vin_data.DMSINVNO,
            "SOB NO" to vin_data.TRANS_REF_NUM,
            "LOCATION" to vin_data.LOCATION,
            "VEH STATUS" to vin_data.VEH_STATUS,
            "STATUS" to vin_data.STATUS
        )

        for ((label, value) in detailsMap) {
            val row = LayoutInflater.from(this).inflate(R.layout.table_row, null) as TableRow
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

    private fun fetchVinData(vin: String) {
        val client = OkHttpClient()
        val url = ApiFile.APP_URL + "/qrcode/qrDetailsByVin?vin=$vin"

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
                        STATUS=stockItem.getString("STATUS")

                    )
                    runOnUiThread {
                            if (vinData.LOCATION != location_name) {
                                Toast.makeText(
                                    this@CameraActivity,
                                    "Vehicle is not at $location_name",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        else {
                            populateFields(vinData)
                            VinDetails.visibility = View.VISIBLE
                            detailsByVin.visibility = View.VISIBLE
                            refreshButton.visibility = View.VISIBLE
                                vintypeSpinner.visibility=View.GONE
                                fetchVinData2.visibility=View.GONE
                            postBtn.visibility = View.VISIBLE
                                orgText.visibility = View.VISIBLE
                                organizationSpinner.visibility = View.VISIBLE
                            refreshButton.visibility = View.VISIBLE
                            vehText.visibility=View.VISIBLE
                            vehicleSpinner.visibility=View.VISIBLE
                            remarkText.visibility=View.VISIBLE
                            Remarks.visibility=View.VISIBLE
                            currentKmsText.visibility=View.VISIBLE
                            currentKms.visibility=View.VISIBLE
                            Toast.makeText(
                                this@CameraActivity,
                                "Details found Successfully \n for VIN: $vin",
                                Toast.LENGTH_LONG
                            ).show()
                            updateSpinnerOptions(vinData.GATEPASSYN)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@CameraActivity,
                        "Failed to fetch details for VIN: $vin",
                        Toast.LENGTH_SHORT
                    ).show()
                    qrResultTextView.visibility=View.GONE
                    fetchChassisDataButton.visibility = View.GONE
                    fetchVinData2.visibility=View.GONE
                    chassis_no.visibility = View.GONE
                    vehicleSpinner.visibility = View.GONE
                    vehicleSpinner.visibility = View.GONE
                    organizationSpinner.visibility = View.GONE
                    vehText.visibility=View.GONE
                    Remarks.visibility=View.GONE
                    remarkText.visibility=View.GONE
                    currentKms.visibility=View.GONE
                    currentKmsText.visibility=View.GONE
                    VinDetails.visibility=View.GONE
                    orgText.visibility=View.GONE
                    postBtn.visibility=View.GONE
                }
            }
        }
    }

    private fun fetchVinData2(vin: String) {
        val client = OkHttpClient()
        val vin2=vintypeSpinner.selectedItem.toString()
        val url = ApiFile.APP_URL + "/qrcode/qrDetailsByVin?vin=$vin2"

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
                        STATUS=stockItem.getString("STATUS")
                    )
                    runOnUiThread {
                            if (vinData.LOCATION != location_name) {
                                Toast.makeText(
                                    this@CameraActivity,
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
                                postBtn.visibility = View.VISIBLE
                                orgText.visibility = View.VISIBLE
                                organizationSpinner.visibility = View.VISIBLE
                                vehText.visibility=View.VISIBLE
                                vehicleSpinner.visibility=View.VISIBLE
                                remarkText.visibility=View.VISIBLE
                                Remarks.visibility=View.VISIBLE
                                currentKmsText.visibility=View.VISIBLE
                                currentKms.visibility=View.VISIBLE
                            Toast.makeText(
                                this@CameraActivity,
                                "Details found Successfully \n for VIN: $vin2",
                                Toast.LENGTH_LONG
                            ).show()
                            updateSpinnerOptions(vinData.GATEPASSYN)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@CameraActivity,
                        "Failed to fetch details for VIN: $vin",
                        Toast.LENGTH_SHORT
                    ).show()
                    qrResultTextView.visibility=View.GONE
                    fetchChassisDataButton.visibility = View.GONE
                    fetchVinData2.visibility=View.GONE
                    chassis_no.visibility = View.GONE
                    vehicleSpinner.visibility = View.GONE
                    vehicleSpinner.visibility = View.GONE
                    organizationSpinner.visibility = View.GONE
                    vehText.visibility=View.GONE
                    Remarks.visibility=View.GONE
                    remarkText.visibility=View.GONE
                    currentKms.visibility=View.GONE
                    currentKmsText.visibility=View.GONE
                    VinDetails.visibility=View.GONE
                    orgText.visibility=View.GONE
                    postBtn.visibility=View.GONE
                }
            }
        }
    }

    private fun updateSpinnerOptions(gatePassYn: Int) {
        val spinnerItems = mutableListOf("Choose Vehicle Status")
        if (gatePassYn == 0) {
            spinnerItems.add("Stock Transfer")
        } else {
            spinnerItems.add("Vehicle Delivered")
        }

        val adapter = object : ArrayAdapter<String>(
            this@CameraActivity,
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
        vehicleSpinner.adapter = adapter
    }

    private fun fetchData() {
        val spinnerItems = mutableListOf("Choose Vehicle Status", "Stock Transfer", "Vehicle Delivered")

        val adapter = object : ArrayAdapter<String>(
            this@CameraActivity,
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
        vehicleSpinner.adapter = adapter

        vehicleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedVehicle = spinnerItems[position]

                if (selectedVehicle == "Stock Transfer") {
                    organizationSpinner.visibility = View.VISIBLE
                    orgText.visibility = View.VISIBLE
                } else {
                    organizationSpinner.visibility = View.GONE
                    orgText.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }


       private fun fetchOrganizations() {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("${ApiFile.APP_URL}/orgDef/getLocation?operating_unit=$ouId")
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    val gson = Gson()
                    val jsonObject = JSONObject(responseBody)
                    val organizations: List<Organization> = gson.fromJson(
                        jsonObject.getJSONArray("obj").toString(),
                        object : TypeToken<List<Organization>>() {}.type
                    )
                    runOnUiThread {
                        populateOrganizationSpinner(organizations)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@CameraActivity, "Failed to fetch organizations", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@CameraActivity, "Failed to fetch organizations due to exception: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun populateOrganizationSpinner(organizations: List<Organization>) {
        val spinnerItems = mutableListOf("Select Organization")
        spinnerItems.addAll(organizations.map { "${it.LOCID} - ${it.LOCATIONNAME}" })

        organizations.forEach { organization ->
            val key = "${organization.LOCID} - ${organization.LOCATIONNAME}"
            organizationMap[key] = organization.LOCID
        }

        val adapter = object : ArrayAdapter<String>(
            this,
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
        organizationSpinner.adapter = adapter

        organizationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun startQrScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("Scan a QR code/Barcode")
        integrator.setCameraId(0)
        integrator.setBeepEnabled(true)
        integrator.setBarcodeImageEnabled(true)
        integrator.setOrientationLocked(true)
        integrator.initiateScan()
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
                vehicleSpinner.visibility = View.GONE
                organizationSpinner.visibility = View.GONE
                vehText.visibility=View.GONE
                Remarks.visibility=View.GONE
                remarkText.visibility=View.GONE
                currentKms.visibility=View.GONE
                currentKmsText.visibility=View.GONE
                VinDetails.visibility=View.GONE
                orgText.visibility=View.GONE
                postBtn.visibility=View.GONE
            } else {
                val vin = result.contents
                qrResultTextView.visibility = View.GONE
                vintypeSpinner.visibility=View.GONE
                fetchVinData2.visibility=View.GONE
                qrResultTextView.text = vin
                VinQr.setText(vin)
                fetchVinData(vin)
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
        val STATUS :String
    )


    data class Vehicle2(
        val LOCATION: String,
        val CHASSIS_NO:String,
        val VEHSTATUS: String,
        val TRANSFERRED_BY: String,
        val MODEL_DESC:String,
        val VIN: String,
        val FROM_LOCATION:String,
        val FRMKM:String,
        val STOCK_TRF_NO:String,
        val DRIVER_NAME:String

    )

    data class chassis_data(
        val VIN: String,
        val LOCATION:String
    )

    data class City(
        val ATTRIBUTE1:String,
        val CMNDESC:String,

        )

}


