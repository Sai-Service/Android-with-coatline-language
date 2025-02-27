package com.example.apinew

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
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.google.zxing.BarcodeFormat


class SecurityVehicleTrack : AppCompatActivity() {

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
    private lateinit var organizationSpinner: Spinner
    private lateinit var vehicleSpinner: Spinner
    private lateinit var logoutButton: ImageView
    private lateinit var postBtn: ImageButton
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
    private lateinit var postText:TextView
    private lateinit var checkbox:CheckBox
    private lateinit var delvYN:String
    private lateinit var tableLayout2: TableLayout
    private var VEH_STATUS: String? = null
    private lateinit var fetchVinData2:ImageButton
    private lateinit var vintypeSpinner:Spinner
    private lateinit var statusField:String
    private val organizationMap = mutableMapOf<String, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security_veh_track)
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
        fetchChassisDataButton=findViewById(R.id.fetchChassisDataButton)
        tableLayout=findViewById(R.id.transferTable)
        remarkText=findViewById(R.id.remarkText)
        Remarks=findViewById(R.id.Remarks)
        tableLayout.visibility=View.GONE
        orgText=findViewById(R.id.orgText)
        postBtn=findViewById(R.id.postBtn)
        refreshButton=findViewById(R.id.refreshButton)
        checkbox=findViewById(R.id.checkbox)
        tableLayout2=findViewById(R.id.tableLayout2)
        checkbox.visibility=View.GONE
        postText=findViewById(R.id.postText)
        postText.visibility=View.GONE
        postBtn.visibility=View.GONE
        refreshButton.visibility=View.GONE
        fetchVinData2=findViewById(R.id.fetchVinData2)
        fetchVinData2.visibility=View.GONE
        vintypeSpinner=findViewById(R.id.vintypeSpinner)
        vintypeSpinner.visibility=View.GONE

        val vin = extractVinFromResult(qrResultTextView.text.toString())
        if (vin.isNotEmpty()) {
            VinQr.setText(vin)
            fetchVinData(vin)
        }  //newly added


        attribute1 = intent.getStringExtra("attribute1") ?: ""
        location = intent.getStringExtra("location") ?: ""
        location_name = intent.getStringExtra("location_name") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""


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
        vehicleSpinner.visibility = View.GONE
        vehicleSpinner.visibility = View.GONE
        organizationSpinner.visibility = View.GONE
        vehText.visibility=View.GONE
        Remarks.visibility=View.GONE
        remarkText.visibility=View.GONE
        VinDetails.visibility=View.GONE
        qrResultTextView.visibility=View.GONE
        orgText.visibility=View.GONE


        showDetailsButton.setOnClickListener {
            toggleDetailsVisibility()
        }

        fetchVinData2.setOnClickListener {
            val vin1=vintypeSpinner.selectedItem.toString()
            fetchVinData2(vin1)
        }


        VinQr = findViewById(R.id.VinQr)
//            fetchVinData(VinQr.toString())


        fetchChassisDataButton.setOnClickListener {
            val chassisNo = chassis_no.text.toString()
            fetchChassisData(chassisNo)
        }

        logoutButton.setOnClickListener {
            logout()
        }

        homePage.setOnClickListener {
            backToHome()
        }

        vehicleIn.setOnClickListener{
            tableLayout.visibility=View.VISIBLE
        }


        refreshButton = findViewById(R.id.refreshButton)
        refreshButton.setOnClickListener {
            resetFields()
        }


        val scanQrBtn: Button = findViewById(R.id.scanQrBtn)
        scanQrBtn.setOnClickListener {
            startQrScanner()
            fetchChassisDataButton.visibility = View.VISIBLE
            chassis_no.visibility = View.VISIBLE
//            vehicleSpinner.visibility = View.VISIBLE
//            vehicleSpinner.visibility = View.VISIBLE
//            organizationSpinner.visibility = View.VISIBLE
//            vehText.visibility=View.VISIBLE
//            Remarks.visibility=View.VISIBLE
//            remarkText.visibility=View.VISIBLE
            VinDetails.visibility=View.VISIBLE
            qrResultTextView.visibility=View.INVISIBLE
//            orgText.visibility=View.VISIBLE
            chassis_no.visibility=View.GONE
            fetchChassisDataButton.visibility=View.GONE

        }


        VinQr.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.toString()?.let { vin ->
                    if (vin.isNotEmpty()) {
                    }
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
        val headers = arrayOf("ID", "VIN", "VEH STATUS", "TRANSFERRED BY", "REASONCODE", "InButton")
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
//            vehicleSpinner.visibility = View.VISIBLE
//            organizationSpinner.visibility = View.VISIBLE
//            vehText.visibility=View.VISIBLE
//            Remarks.visibility=View.VISIBLE
//            remarkText.visibility=View.VISIBLE
        } else {
            fetchChassisDataButton.visibility = View.GONE
            chassis_no.visibility = View.GONE
//            vehicleSpinner.visibility = View.GONE
//            vehicleSpinner.visibility = View.GONE
//            organizationSpinner.visibility = View.GONE
//            vehText.visibility=View.GONE
//            Remarks.visibility=View.GONE
//            remarkText.visibility=View.GONE
            VinDetails.visibility=View.GONE
            orgText.visibility=View.GONE
        }
    }


    private fun resetFields() {
        chassis_no.setText("")
        Remarks.setText("")
        findViewById<TextView>(R.id.detailsByVin).text = ""
        findViewById<TextView>(R.id.qrResultTextView).text = ""
        findViewById<Spinner>(R.id.vehicleSpinner).setSelection(0)
        findViewById<Spinner>(R.id.organizationSpinner).setSelection(0)
        findViewById<Spinner>(R.id.vintypeSpinner).setSelection(0)
        fetchVinData2.visibility=View.GONE
        vintypeSpinner.visibility=View.GONE
        qrResultTextView.visibility=View.GONE
        fetchChassisDataButton.visibility = View.GONE
        chassis_no.visibility = View.GONE
        vehicleSpinner.visibility = View.GONE
        vehicleSpinner.visibility = View.GONE
        organizationSpinner.visibility = View.GONE
        vehText.visibility=View.GONE
        Remarks.visibility=View.GONE
        remarkText.visibility=View.GONE
        VinDetails.visibility=View.GONE
        orgText.visibility=View.GONE
        checkbox.isChecked = false
        postBtn.visibility=View.GONE
        postText.visibility=View.GONE
        checkbox.visibility=View.GONE
        val tableLayout = findViewById<TableLayout>(R.id.tableLayout2)
        tableLayout.removeAllViews()
    }


    private fun fetchChassisData(chassis_no: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.APP_URL}/qrcode/qrDetailsByChassisDelv?chassisNo=$chassis_no&ouId=$ouId")
//            http://localhost:8081/qrcode/qrDetailsByChassisDelv?chassisNo=375203
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseCode = response.code
                val jsonData = response.body?.string()

                if (responseCode == 200 && jsonData != null) {
                    try {
                        val jsonObject = JSONObject(jsonData)
                        Log.d("DataBy Chassis No ---", jsonData)
                        val objArray = jsonObject.getJSONArray("obj")

                        if (objArray.length() > 0) {
                            val stockItem = objArray.getJSONObject(0)


                            jsonData?.let {
                                val parseVindataList = parseVindata(jsonData.toString())
                                runOnUiThread {
                                    val adapter = ArrayAdapter(
                                        this@SecurityVehicleTrack,
                                        android.R.layout.simple_spinner_item,
                                        parseVindataList
                                    )
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    vintypeSpinner.adapter = adapter
                                }
                            }

                            val chassisData = chassis_data(
                                VIN = stockItem.getString("VIN"),
                                LOCATION= stockItem.getString("LOCATION")
                            )
                            Log.d("Vin:", chassisData.VIN)
                            runOnUiThread {
                                if (chassisData.LOCATION != location_name) {
                                    Toast.makeText(
                                        this@SecurityVehicleTrack,
                                        "Vehicle is not at $location_name",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    VinDetails.visibility=View.GONE
                                } else {
                                    qrResultTextView.text = chassisData.VIN
                                    VinQr.setText(qrResultTextView.text.toString())
                                    qrResultTextView.visibility = View.INVISIBLE
                                    vintypeSpinner.visibility=View.VISIBLE
                                    fetchVinData2.visibility=View.VISIBLE
                                    Toast.makeText(
                                        this@SecurityVehicleTrack,
                                        "Details found Successfully \n for Chassis no. $chassis_no",
                                        Toast.LENGTH_SHORT
                                    ).show()
//                                postBtn.visibility=View.VISIBLE
                                    refreshButton.visibility = View.VISIBLE
//                                    fetchVinData(chassisData.VIN)
                                    val vin = qrResultTextView.text.toString()
                                    VinQr.setText(chassisData.VIN)
                                }
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(
                                    this@SecurityVehicleTrack,
                                    "No details found for Chassis no. $chassis_no",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (jsonException: JSONException) {
                        jsonException.printStackTrace()
                        runOnUiThread {
                            Toast.makeText(
                                this@SecurityVehicleTrack,
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
                            this@SecurityVehicleTrack,
                            "Failed to fetch data: $responseCode",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@SecurityVehicleTrack,
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

//    private fun postData() {
//        val vin = qrResultTextView.text.toString()
//        val reasonCode =  "null"
//        val location = location1.text.toString()
//        val created_by = attributeTextView.text.toString()
//        val transferred_by = attributeTextView.text.toString()
//        val organizationName = location1.text.toString()
//        val organization_id = locId.toString()
//        val to_location = locId.toString()
//        val remarks ="null"
//        val locIdValue = locId.toString()
//        val lacation_name = location1.text.toString()
//        val VEH_STATUS = VEH_STATUS ?: ""
//
//        Log.d("PostData", "VIN: $vin")
//        Log.d("PostData", "ReasonCode: $reasonCode")
//        Log.d("PostData", "Location: $location")
//        Log.d("PostData", "CreatedBy: $created_by")
//        Log.d("PostData", "TransferredBy: $transferred_by")
//        Log.d("PostData", "OrganizationName: $organizationName")
//        Log.d("PostData", "OrganizationID: $organization_id")
//        Log.d("PostData", "Remarks: $remarks")
//        Log.d("PostData", "LocIdValue: $locIdValue")
//
//
//        val url = ApiFile.APP_URL + "/SecDelv/vehTrackingBySecVin/"
//        val requestBody = MultipartBody.Builder()
//            .setType(MultipartBody.FORM)
//            .addFormDataPart("vin", vin)
//            .addFormDataPart("reasonCode", reasonCode)
//            .addFormDataPart("location", location)
//            .addFormDataPart("veh_status", "")
//            .addFormDataPart("from_location", locIdValue)
//            .addFormDataPart("to_location", to_location)
//            .addFormDataPart("organization_id", organization_id)
//            .addFormDataPart("transferred_by", transferred_by)
//            .addFormDataPart("remarks", remarks)
//            .addFormDataPart("created_by", created_by)
////            .addFormDataPart("checkboxValue", checkboxValue.toString())
////            .addFormDataPart("delvYN",delvYN)
//            .addFormDataPart("model_cd", VEH_STATUS)
//            .build()
//
//        val request = Request.Builder()
//            .url(url)
//            .post(requestBody)
//            .build()
//
//        val client = OkHttpClient()
//        Log.d("PostDataURL", "URL: $url")
//
//        Log.d("PostData", "VIN: $vin")
//        Log.d("PostData", "ReasonCode: $reasonCode")
//        Log.d("PostData", "OrganizationID: $organization_id")
//        Log.d("PostData", "CreatedBy: $created_by")
//        Log.d("PostData", "TransferredBy: $transferred_by")
//        Log.d("PostData", "Remarks: $remarks")
////        Log.d("PostData", "CheckboxValue: $checkboxValue")
//        Log.d("VEH_STATUS", "VEH_STATUS: $VEH_STATUS")
//
//
//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                val response = client.newCall(request).execute()
//                val responseBody = response.body?.string()
//
//                runOnUiThread {
//                    if (response.isSuccessful) {
//                        Toast.makeText(this@SecurityVehicleTrack, "Vehicle Delivered Successfully!", Toast.LENGTH_LONG).show()
//                        chassis_no.setText("")
//                        Remarks.setText("")
//                        findViewById<TextView>(R.id.detailsByVin).text = ""
//                        findViewById<TextView>(R.id.qrResultTextView).text = ""
//                        findViewById<Spinner>(R.id.vehicleSpinner).setSelection(0)
//                        findViewById<Spinner>(R.id.organizationSpinner).setSelection(0)
//                        qrResultTextView.visibility=View.GONE
//                        fetchChassisDataButton.visibility = View.GONE
//                        chassis_no.visibility = View.GONE
//                        vehicleSpinner.visibility = View.GONE
//                        vehicleSpinner.visibility = View.GONE
//                        organizationSpinner.visibility = View.GONE
//                        vehText.visibility=View.GONE
//                        Remarks.visibility=View.GONE
//                        remarkText.visibility=View.GONE
//                        VinDetails.visibility=View.GONE
//                        orgText.visibility=View.GONE
//                        checkbox.isChecked = false
//                        postBtn.visibility=View.GONE
//                        postText.visibility=View.GONE
//                        checkbox.visibility=View.GONE
//                        resetFields()
//                    } else {
//                        Toast.makeText(this@SecurityVehicleTrack, "Failed to post data: ${response.code}", Toast.LENGTH_SHORT).show()
//                        Log.e("PostDataError", "Failed to post data: ${response.code}, Response: $responseBody")
//                    }
//                }
//            } catch (e: IOException) {
//                e.printStackTrace()
//                runOnUiThread {
//                    Toast.makeText(this@SecurityVehicleTrack, "Failed to post data due to exception: ${e.message}", Toast.LENGTH_SHORT).show()
//                    Log.e("PostDataException", "Exception: ${e.message}")
//                }
//            }
//        }
//    }

    private fun postData() {
        val vin = qrResultTextView.text.toString()

        Log.d("PostData", "VIN: $vin")

        val url = "${ApiFile.APP_URL}/SecDelv/vehTrackingBySecVin?vin=$vin"

        val request = Request.Builder()
            .url(url)
            .put(RequestBody.create(null, ""))
            .build()

        val client = OkHttpClient()
        Log.d("PostDataURL", "URL: $url")

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@SecurityVehicleTrack, "Vehicle out for Delivery!", Toast.LENGTH_LONG).show()
                        chassis_no.setText("")
                        Remarks.setText("")
                        findViewById<TextView>(R.id.detailsByVin).text = ""
                        findViewById<TextView>(R.id.qrResultTextView).text = ""
                        findViewById<Spinner>(R.id.vehicleSpinner).setSelection(0)
                        findViewById<Spinner>(R.id.organizationSpinner).setSelection(0)
                        qrResultTextView.visibility=View.GONE
                        fetchChassisDataButton.visibility = View.GONE
                        chassis_no.visibility = View.GONE
                        vehicleSpinner.visibility = View.GONE
                        vehicleSpinner.visibility = View.GONE
                        organizationSpinner.visibility = View.GONE
                        vehText.visibility=View.GONE
                        Remarks.visibility=View.GONE
                        remarkText.visibility=View.GONE
                        VinDetails.visibility=View.GONE
                        orgText.visibility=View.GONE
                        checkbox.isChecked = false
                        postBtn.visibility=View.GONE
                        postText.visibility=View.GONE
                        checkbox.visibility=View.GONE
                        resetFields()
                    } else {
                        Toast.makeText(this@SecurityVehicleTrack, "Failed to update: ${response.code}", Toast.LENGTH_SHORT).show()
                        Log.e("PostDataError", "Failed to update data: ${response.code}, Response: $responseBody")
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@SecurityVehicleTrack, "Failed to update due to exception: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("PostDataException", "Exception: ${e.message}")
                }
            }
        }
    }


    private fun populateFields(vin_data: vin_data) {
        val table = findViewById<TableLayout>(R.id.tableLayout2)
        table.removeAllViews()
        val detailsMap = mapOf(
            "CUST NAME" to vin_data.CUSTOMER_NAME,
            "VIN NO" to vin_data.VIN,
            "CHASSIS NO" to vin_data.CHASSISNO,
            "ENGINE NO" to vin_data.ENGINENO,
            "MODEL DESC" to vin_data.MODELCD,
            "VARIANT DESC" to vin_data.VARIANT_CD,
            "COLOUR DESC" to vin_data.COLOR,
            "KEY NO" to vin_data.KEY_NO,
            "DMS INV NO" to vin_data.DMSINVNO,
            "SOB NO" to vin_data.SONO,
            "GATE PASS NO" to vin_data.GATEPASSYN,
            "DELIVERY DATE" to vin_data.DATE_OF_DELIVERY,
            "VEHICLE NO" to vin_data.VEHICLENO,
            "VEHICLE STATUS" to vin_data.VEHSTATUS,
            "STATUS" to vin_data.STATUS,
        )

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
        val url = ApiFile.APP_URL + "/qrcode/qrDetailsByVinDelv?vin=$vin"
//        http://localhost:8081/qrcode/qrDetailsByVinDelv?vin=MA3TFC62SPF246799
        Log.d("URL:", url)

        val request = Request.Builder()
            .url(url)
            .build()
        Log.d("Vin no:", vin)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                val responseCode = response.code
                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    Log.d("QR VIN Data-----", it)
                    val stockItem = jsonObject.getJSONArray("obj").getJSONObject(0)

                    val vinData = vin_data(
                        VARIANTCD = stockItem.getString("VARIANTCD"),
                        VARIANT_CD= stockItem.getString("VARIANT_CD"),
                        DMSINVNO = stockItem.getString("DMSINVNO"),
                        CHASSISNO = stockItem.getString("CHASSISNO"),
                        MODELCD = stockItem.getString("MODELCD"),
                        ENGINENO = stockItem.getString("ENGINENO"),
                        CUSTOMER_NAME = stockItem.getString("CUSTOMER_NAME"),
                        COLOR = stockItem.getString("COLOR"),
                        KEY_NO = stockItem.getString("KEY_NO"),
                        DATE_OF_DELIVERY = stockItem.getString("DATE_OF_DELIVERY"),
                        VEHICLENO = stockItem.getString("VEHICLENO"),
                        GATEPASSYN = stockItem.getInt("GATEPASSYN"),
                        SONO = stockItem.getString("SONO"),
                        VIN = stockItem.getString("VIN"),
                        VEHSTATUS= stockItem.getString("VEHSTATUS"),
                        STATUS = stockItem.getString("STATUS"),
                        LOCATION=stockItem.getString("LOCATION")
                    )
                    VEH_STATUS = vinData.VEHSTATUS
                    statusField=vinData.STATUS
                    Log.d("statusField",statusField)

                    runOnUiThread {
                        if (vinData.LOCATION != location_name) {
                            Toast.makeText(
                                this@SecurityVehicleTrack,
                                "Vehicle is not at $location_name",
                                Toast.LENGTH_SHORT
                            ).show()
                            VinDetails.visibility=View.GONE
                        } else {
                            if (responseCode == 200 && jsonData != null) {
                                populateFields(vinData)
                                VinDetails.visibility = View.VISIBLE
                                detailsByVin.visibility = View.VISIBLE
//                            postBtn.visibility = View.VISIBLE
                                refreshButton.visibility = View.VISIBLE
//                            postText.visibility=View.VISIBLE
                                Toast.makeText(
                                    this@SecurityVehicleTrack,
                                    "Details found Successfully \n for VIN: $vin",
                                    Toast.LENGTH_LONG
                                ).show()
                                val vin = qrResultTextView.text.toString()
                            }
                            if(statusField=="Ready For Delivery" && VEH_STATUS=="DELIVERED"){
                                postBtn.visibility=View.VISIBLE
                                postText.visibility=View.VISIBLE
                            } else{
                                postBtn.visibility=View.GONE
                                postText.visibility=View.GONE
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@SecurityVehicleTrack,
                        "Failed to fetch details for VIN: $vin",
                        Toast.LENGTH_SHORT
                    ).show()
                    VinDetails.visibility = View.GONE
                    chassis_no.visibility=View.GONE
                    fetchChassisDataButton.visibility=View.GONE
                }
            }
        }
    }

    private fun fetchVinData2(vin: String) {
        val client = OkHttpClient()
        val vin2=vintypeSpinner.selectedItem.toString()
        val url = ApiFile.APP_URL + "/qrcode/qrDetailsByVinDelv?vin=$vin2"
//        http://localhost:8081/qrcode/qrDetailsByVinDelv?vin=MA3TFC62SPF246799
        Log.d("URL:", url)

        val request = Request.Builder()
            .url(url)
            .build()
        Log.d("Vin no:", vin)
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                val responseCode = response.code
                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    Log.d("QR VIN Data-----", it)
                    val stockItem = jsonObject.getJSONArray("obj").getJSONObject(0)

                    val vinData = vin_data(
                        VARIANTCD = stockItem.getString("VARIANTCD"),
                        VARIANT_CD= stockItem.getString("VARIANT_CD"),
                        DMSINVNO = stockItem.getString("DMSINVNO"),
                        CHASSISNO = stockItem.getString("CHASSISNO"),
                        MODELCD = stockItem.getString("MODELCD"),
                        ENGINENO = stockItem.getString("ENGINENO"),
                        CUSTOMER_NAME = stockItem.getString("CUSTOMER_NAME"),
                        COLOR = stockItem.getString("COLOR"),
                        KEY_NO = stockItem.getString("KEY_NO"),
                        DATE_OF_DELIVERY = stockItem.getString("DATE_OF_DELIVERY"),
                        VEHICLENO = stockItem.getString("VEHICLENO"),
                        GATEPASSYN = stockItem.getInt("GATEPASSYN"),
                        SONO = stockItem.getString("SONO"),
                        VIN = stockItem.getString("VIN"),
                        VEHSTATUS= stockItem.getString("VEHSTATUS"),
                        STATUS = stockItem.getString("STATUS"),
                        LOCATION=stockItem.getString("LOCATION")
                    )
                    VEH_STATUS = vinData.VEHSTATUS
                    statusField=vinData.STATUS
                    Log.d("statusField",statusField)

                    runOnUiThread {
                        if (vinData.LOCATION != location_name) {
                            Toast.makeText(
                                this@SecurityVehicleTrack,
                                "Vehicle is not at $location_name",
                                Toast.LENGTH_SHORT
                            ).show()
                            VinDetails.visibility=View.GONE
                        } else {
                            if (responseCode == 200 && jsonData != null) {
                                populateFields(vinData)
                                VinDetails.visibility = View.VISIBLE
                                detailsByVin.visibility = View.VISIBLE
//                                postBtn.visibility = View.VISIBLE
                                refreshButton.visibility = View.VISIBLE
//                                postText.visibility=View.VISIBLE
                                Toast.makeText(
                                    this@SecurityVehicleTrack,
                                    "Details found Successfully \n for VIN: $vin",
                                    Toast.LENGTH_LONG
                                ).show()
                                val vin = qrResultTextView.text.toString()
                            }
                            if(statusField=="Ready For Delivery" && VEH_STATUS=="DELIVERED"){
                                postBtn.visibility=View.VISIBLE
                                postText.visibility=View.VISIBLE
                            } else{
                                postBtn.visibility=View.GONE
                                postText.visibility=View.GONE
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@SecurityVehicleTrack,
                        "Failed to get details for VIN: $vin",
                        Toast.LENGTH_SHORT
                    ).show()
                    VinDetails.visibility = View.GONE
                    chassis_no.visibility=View.GONE
                    fetchChassisDataButton.visibility=View.GONE
                    fetchVinData2.visibility=View.GONE
                    vintypeSpinner.visibility=View.GONE
                }
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
        refreshButton.visibility=View.VISIBLE
        qrResultTextView.visibility=View.GONE
        VinQr.visibility=View.GONE

        Handler(Looper.getMainLooper()).postDelayed({
//            Toast.makeText(this, "Could not scan.Please try again.", Toast.LENGTH_SHORT).show()
        }, 10000)
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
                postBtn.visibility=View.GONE
                checkbox.visibility=View.GONE
                VinDetails.visibility = View.GONE
            } else {
                val vin = result.contents
                qrResultTextView.visibility = View.INVISIBLE
//                postBtn.visibility=View.VISIBLE
//                postText.visibility=View.VISIBLE
                qrResultTextView.text = vin
                chassis_no.visibility=View.GONE
                fetchChassisDataButton.visibility=View.GONE
                VinQr.setText(vin)
                fetchVinData(vin)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    private fun logout() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
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
        val VARIANTCD: String,
        val DMSINVNO: String,
        val CHASSISNO: String,
        val MODELCD: String,
        val ENGINENO: String,
        val CUSTOMER_NAME: String,
        val VARIANT_CD: String,
        val COLOR: String,
        val KEY_NO: String,
        val DATE_OF_DELIVERY: String,
        val VEHICLENO: String,
        val SONO:String,
        val GATEPASSYN:Int,
        val VIN:String,
        val VEHSTATUS:String,
        val STATUS: String,
        val LOCATION: String
    )


    data class Vehicle2(
        val LOCATION: String,
        val VEHSTATUS: String,
        val TRANSFERRED_BY: String,
        val REASONCODE: String,
        val VIN: String
    )

    data class chassis_data(
        val VIN: String,
        val LOCATION:String
    )

}


