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
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import android.widget.Filter
import kotlin.math.max
import kotlin.math.min

class WorkshopParkingModule : AppCompatActivity() {
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
    private lateinit var newVehEditText:AutoCompleteTextView
    private lateinit var newVehInButton:ImageButton
    private lateinit var forTestDriveOut:TextView
    private lateinit var forTestDriveIn:TextView
    private lateinit var forNewVehicleIn:TextView
    private lateinit var kmTxt:TextView
    private lateinit var currentKMSField:EditText
    private lateinit var regNoDetails:TextView
    private lateinit var driverTxt:TextView
    //    private lateinit var driverNameField:Spinner
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
    private lateinit var chassisNo:String
    private lateinit var engineNo:String
    private lateinit var vinNo:String
    private lateinit var testDriveNo:String
    private lateinit var custName:String
    private lateinit var outKm:String
    private lateinit var inKmNewVeh:String
    private lateinit var serviceAdvisor:String
    private lateinit var model:String
    private lateinit var outKmNewVeh:String
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
    private lateinit var departmentName:String
    private lateinit var departmentFromResponse:String


    private lateinit var selectedDeptTextView: TextView
    private lateinit var deptTextView:TextView
    private lateinit var deptTextView2:TextView
    private lateinit var driverNameAutoComplete: AutoCompleteTextView

    private lateinit var selectedDeptLL:View

    private lateinit var selectDeptMainLayout:View
    private lateinit var vehicleOperations:View
    private lateinit var vehicleInputFields:View
    private lateinit var regNoDetailsLL:View
    private lateinit var requiredDetailsLL:View
    private lateinit var buttonLL:View
    private lateinit var vehicleHistoryButtonLL:View
    private lateinit var deptTextView3:TextView
    private lateinit var deptTextView4:TextView
    private lateinit var deptTextView5:TextView
    private lateinit var deptTextView6:TextView


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
        setContentView(R.layout.activity_workshop_parking_module)

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
//        driverNameField=findViewById(R.id.driverNameField)
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
        selectedDeptLL=findViewById(R.id.selectedDeptLL)
        selectDeptMainLayout=findViewById(R.id.selectDeptMainLayout)
        vehicleOperations=findViewById(R.id.vehicleOperations)
        vehicleInputFields=findViewById(R.id.vehicleInputFields)
        regNoDetailsLL=findViewById(R.id.regNoDetailsLL)
        requiredDetailsLL=findViewById(R.id.requiredDetailsLL)
        buttonLL=findViewById(R.id.buttonLL)
        vehicleHistoryButtonLL=findViewById(R.id.vehicleHistoryButtonLL)

        selectedDeptLL.visibility=View.GONE

        deptTextView=findViewById(R.id.deptTextView)
        deptTextView2=findViewById(R.id.deptTextView2)
        deptTextView3=findViewById(R.id.deptTextView3)
        deptTextView4=findViewById(R.id.deptTextView4)
        deptTextView5=findViewById(R.id.deptTextView5)
        deptTextView6=findViewById(R.id.deptTextView6)


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

        driverNameAutoComplete=findViewById(R.id.driverNameAutoComplete)


        selectedDeptTextView = findViewById(R.id.selectedDeptTextView)

        deptTextView.setOnClickListener {
            selectedDeptLL.visibility=View.VISIBLE
            selectedDeptTextView.text="SERVICE"
            forNewVehicleOut.isEnabled = true
            forNewVehicleIn.isEnabled = true
            forNewVehicleIn.setBackgroundResource(R.drawable.gatepass_textview)
            forNewVehicleOut.setBackgroundResource(R.drawable.gatepass_textview)
            fetchDeptName()
            Handler(Looper.getMainLooper()).postDelayed({
                fetchDriverName()
            }, 2000)
        }

        deptTextView2.setOnClickListener {
            selectedDeptLL.visibility=View.VISIBLE
            selectedDeptTextView.text="BODYSHOP"
            forNewVehicleOut.isEnabled = true
            forNewVehicleIn.isEnabled = true
            forNewVehicleIn.setBackgroundResource(R.drawable.gatepass_textview)
            forNewVehicleOut.setBackgroundResource(R.drawable.gatepass_textview)
            fetchDeptName()
            Handler(Looper.getMainLooper()).postDelayed({
                fetchDriverName()
            }, 2000)
        }

        deptTextView3.setOnClickListener {
            selectedDeptLL.visibility=View.VISIBLE
            selectedDeptTextView.text="ARENA-SALES"
            forNewVehicleOut.isEnabled = true
            forNewVehicleIn.isEnabled = true
            forNewVehicleIn.setBackgroundResource(R.drawable.gatepass_textview)
            forNewVehicleOut.setBackgroundResource(R.drawable.gatepass_textview)
            fetchDeptName()
            Handler(Looper.getMainLooper()).postDelayed({
                fetchDriverName()
            }, 2000)
        }

        deptTextView4.setOnClickListener {
            selectedDeptLL.visibility=View.VISIBLE
            selectedDeptTextView.text="NEXA-SALES"
            forNewVehicleOut.isEnabled = true
            forNewVehicleIn.isEnabled = true
            forNewVehicleIn.setBackgroundResource(R.drawable.gatepass_textview)
            forNewVehicleOut.setBackgroundResource(R.drawable.gatepass_textview)
            fetchDeptName()
            Handler(Looper.getMainLooper()).postDelayed({
                fetchDriverName()
            }, 2000)
        }

        deptTextView5.setOnClickListener {
            selectedDeptLL.visibility=View.VISIBLE
            selectedDeptTextView.text="TRUE-VALUE"
            forNewVehicleOut.isEnabled = true
            forNewVehicleIn.isEnabled = true
            forNewVehicleIn.setBackgroundResource(R.drawable.gatepass_textview)
            forNewVehicleOut.setBackgroundResource(R.drawable.gatepass_textview)
            fetchDeptName()
            Handler(Looper.getMainLooper()).postDelayed({
                fetchDriverName()
            }, 2000)
        }

        deptTextView6.setOnClickListener {
            selectedDeptLL.visibility=View.VISIBLE
            selectedDeptTextView.text="OTHERS"
            forNewVehicleOut.isEnabled = true
            forNewVehicleIn.isEnabled = true
            forNewVehicleIn.setBackgroundResource(R.drawable.gatepass_textview)
            forNewVehicleOut.setBackgroundResource(R.drawable.gatepass_textview)
            fetchDeptName()
            Handler(Looper.getMainLooper()).postDelayed({
                fetchDriverName()
            }, 2000)
        }

        kmTxt.visibility=View.GONE
        currentKMSField.visibility=View.GONE
        captureToKm.visibility=View.GONE
        regNoDetails.visibility=View.GONE
        driverTxt.visibility=View.GONE
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
        driverNameAutoComplete.visibility=View.GONE


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

        if(deptName=="SERVICE-PARK" || deptName=="BODYSHOP-PARK"){
            vehicleHistoryButtonLL.visibility=View.VISIBLE
            selectDeptMainLayout.visibility=View.GONE
            vehicleOperations.visibility=View.GONE
            vehicleInputFields.visibility=View.GONE
            regNoDetailsLL.visibility=View.GONE
            requiredDetailsLL.visibility=View.GONE
            buttonLL.visibility=View.GONE
        }


        forNewVehicleIn.setOnClickListener {
            newVehLL.visibility=View.VISIBLE
            captureVehNumberIn.visibility=View.VISIBLE
            newVehEditText.setText("")
            physicallyOutVehEditText.setText("")
            physicallyOutLLTxt.visibility=View.GONE
            newVehInButton.visibility=View.VISIBLE
            newVehOutButton.visibility=View.GONE
            Toast.makeText(this@WorkshopParkingModule,"Vehicle In option selected",Toast.LENGTH_SHORT).show()
            fetchVehicleList()
        }

        forNewVehicleOut.setOnClickListener {
            newVehLL.visibility=View.VISIBLE
            captureVehNumberIn.visibility=View.VISIBLE
            newVehEditText.setText("")
            physicallyOutVehEditText.setText("")
            physicallyOutLLTxt.visibility=View.GONE
            newVehInButton.visibility=View.GONE
            newVehOutButton.visibility=View.VISIBLE
            Toast.makeText(this@WorkshopParkingModule,"Vehicle Out option selected",Toast.LENGTH_SHORT).show()
            fetchVehicleList()
        }

        newVehInButton.setOnClickListener { detailsForVehicleInFirstTime()  }

        newVehOutButton.setOnClickListener {
            detailsForVehicleOut()
            fetchParkLocations()
        }

        newVehicleInPremises.setOnClickListener { vehicleIn() }

        newVehicleOutPremises.setOnClickListener { vehicleOut() }

        refreshButton.setOnClickListener { resetFields() }



        physicallyOutButton.setOnClickListener {
            physicallyOutLLTxt.visibility=View.VISIBLE
            newVehLL.visibility=View.GONE
            newVehEditText.setText("")
            Toast.makeText(this@WorkshopParkingModule,"Vehicle Physically Out option selected",Toast.LENGTH_SHORT).show()
        }


        captureToKm.setOnClickListener {
            clickedPlaceholder = captureToKm
            openCamera()
        }

        fetchCityData()
        fetchGateNo()

        forNewVehicleOut.isEnabled = false
        forNewVehicleIn.isEnabled = false
        forNewVehicleIn.setBackgroundColor(ContextCompat.getColor(this, R.color.secondary_text_color))
        forNewVehicleOut.setBackgroundColor(ContextCompat.getColor(this, R.color.secondary_text_color))


        gateNoLov.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                fetchGateType()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

//        reasonCodeLov.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
//                if(reasonCodeLov.selectedItem.toString()=="LOCATION-TRANSFER PARKING"){
//                    transferLocationTxtView.visibility=View.VISIBLE
//                    transferLocationLov.visibility=View.VISIBLE
//                    fetchOrgIds()
//                    parkingEditText.visibility=View.GONE
//                    parkingEditTextTextView.visibility=View.GONE
//                } else if(reasonCodeLov.selectedItem.toString()=="PARKING"){
//                    parkingEditTextTextView.visibility=View.VISIBLE
//                    parkingEditText.visibility=View.VISIBLE
//                    transferLocationLov.visibility=View.GONE
//                    transferLocationTxtView.visibility=View.GONE
//                    attribute5=parkingEditText.text.toString()
//                } else if(reasonCodeLov.selectedItem.toString()=="TRIAL"){
//                    transferLocationTxtView.visibility=View.GONE
//                    transferLocationLov.visibility=View.GONE
//                    resetSpinner2()
//                }
//                else if(reasonCodeLov.selectedItem.toString()=="LOCATION-PARKING"){
//                    transferLocationTxtView.visibility=View.VISIBLE
//                    transferLocationLov.visibility=View.VISIBLE
//                    fetchParkLocations()
//                    parkingEditText.visibility=View.GONE
//                    parkingEditTextTextView.visibility=View.GONE
//                }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//            }
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

        vehicleHistory.setOnClickListener { workShopTestDriveVehHistory()  }

    }

    private fun populateDriverAutoComplete(driverNames: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, driverNames)

        driverNameAutoComplete.setAdapter(adapter)
        driverNameAutoComplete.threshold = 1

        driverNameAutoComplete.setOnItemClickListener { parent, view, position, id ->
            val selectedDriver = parent.getItemAtPosition(position).toString()
            Toast.makeText(this, "Selected: $selectedDriver", Toast.LENGTH_SHORT).show()
        }
    }

    private fun populateVehicleAutoComplete(vehicleNumbers: List<String>) {
        val adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            ArrayList(vehicleNumbers)
        ) {
            override fun getFilter(): Filter {
                return object : Filter() {
                    override fun performFiltering(constraint: CharSequence?): FilterResults {
                        val results = FilterResults()
                        val suggestions = ArrayList<String>()

                        if (!constraint.isNullOrEmpty()) {
                            val filterPattern = constraint.toString().lowercase()

                            for (item in vehicleNumbers) {
                                if (item.lowercase().contains(filterPattern)) {
                                    suggestions.add(item)
                                }
                            }
                        } else {
                            suggestions.addAll(vehicleNumbers)
                        }

                        results.values = suggestions
                        results.count = suggestions.size
                        return results
                    }

                    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                        clear()
                        if (results?.values is List<*>) {
                            addAll(results.values as List<String>)
                        }
                        notifyDataSetChanged()
                    }

                    override fun convertResultToString(resultValue: Any?): CharSequence {
                        return resultValue as CharSequence
                    }
                }
            }
        }

        newVehEditText.setAdapter(adapter)
        newVehEditText.threshold = 1

        newVehEditText.setOnItemClickListener { parent, view, position, id ->
            val selectedVehicle = parent.getItemAtPosition(position).toString()
            Toast.makeText(this, "Selected: $selectedVehicle", Toast.LENGTH_SHORT).show()
        }
    }


    private fun parsePaymentModes(jsonResponse: JSONObject): List<String> {
        val paymentList = mutableListOf("Select Department")

        val code = jsonResponse.optInt("code", 0)
        if (code != 200) {
            runOnUiThread {
                Toast.makeText(this, "API error: ${jsonResponse.optString("message")}", Toast.LENGTH_SHORT).show()
            }
            return paymentList
        }

        val obj = jsonResponse.opt("obj")
        if (obj is JSONArray) {
            for (i in 0 until obj.length()) {
                val item = obj.getJSONObject(i)
                val department = item.getString("DEPARTMENT")
                paymentList.add(department)
                departmentName = department
            }
        } else {
            Log.e("parsePaymentModes", "Expected JSONArray but got: $obj")
            runOnUiThread {
                Toast.makeText(this, "No departments found", Toast.LENGTH_SHORT).show()
            }
        }

        return paymentList
    }

    private fun fetchDeptName() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.APP_URL}/tdParking/getDepartment?ouId=$ouId&locId=$locId")
            .build()
        Log.d("URL -> ", request.toString())

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val jsonResponse = JSONObject(responseBody)
                        val departments = parsePaymentModes(jsonResponse)

                        runOnUiThread {
//                            showDepartmentDialog(departments)
                        }
                    }
                }
                forNewVehicleOut.isEnabled = false
                forNewVehicleIn.isEnabled = false
                forNewVehicleIn.setBackgroundColor(ContextCompat.getColor(this@WorkshopParkingModule, R.color.secondary_text_color))
                forNewVehicleOut.setBackgroundColor(ContextCompat.getColor(this@WorkshopParkingModule, R.color.secondary_text_color))

            }
        })
    }


    private fun fetchDriverName() {
        val department=selectedDeptTextView.text.toString()
        if(department=="Select Department"){
            Toast.makeText(this@WorkshopParkingModule,"Please select the department",Toast.LENGTH_SHORT).show()
        }

        val url = "${ApiFile.APP_URL}/tdParking/getDriverName?department=$department&locId=$locId&ouId=$ouId"

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        Log.d("url-fetchPaymentIdSpinnerData",url)


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val jsonResponse = JSONObject(responseBody)

                        val obj = jsonResponse.get("obj")
                        if (obj is JSONArray) {
                            if (obj.length() == 0) {
                                runOnUiThread {
                                    forNewVehicleOut.isEnabled = false
                                    forNewVehicleIn.isEnabled = false
                                    forNewVehicleIn.setBackgroundColor(ContextCompat.getColor(this@WorkshopParkingModule, R.color.secondary_text_color))
                                    forNewVehicleOut.setBackgroundColor(ContextCompat.getColor(this@WorkshopParkingModule, R.color.secondary_text_color))
                                    Toast.makeText(this@WorkshopParkingModule, "No data uploaded", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                val paymentIds = parsePaymentIdData(obj)
                                runOnUiThread {
                                    populateDriverAutoComplete(paymentIds)
                                    forNewVehicleOut.isEnabled = true
                                    forNewVehicleIn.isEnabled = true
                                    forNewVehicleIn.setBackgroundResource(R.drawable.gatepass_textview)
                                    forNewVehicleOut.setBackgroundResource(R.drawable.gatepass_textview)
                                }
                            }
                        } else if (obj is String) {
                            runOnUiThread {
                                Toast.makeText(this@WorkshopParkingModule, obj, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }


        })
    }

    private fun parsePaymentIdData(objArray: JSONArray): List<String> {
        val paymentIdList = mutableListOf("Select Driver Name")

        for (i in 0 until objArray.length()) {
            val item = objArray.getJSONObject(i)
            val name = item.getString("DRIVER_NAME")
            paymentIdList.add(name)
        }

        return paymentIdList
    }

    private fun fetchVehicleList() {
        val department=selectedDeptTextView.text.toString()
        if(department=="Select Department"){
            Toast.makeText(this@WorkshopParkingModule,"Please select the department",Toast.LENGTH_SHORT).show()
        }

        val url = "${ApiFile.APP_URL}/tdParking/vehInList?dept=$department&locId=$locId"

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        Log.d("url-fetchPaymentIdSpinnerData",url)

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val jsonResponse = JSONObject(responseBody)

                        val obj = jsonResponse.get("obj")
                        if (obj is JSONArray) {
                                val paymentIds = parseVehicleListData(obj)
                                runOnUiThread {
                                    populateVehicleAutoComplete(paymentIds)
                                }
                        } else if (obj is String) {
                            runOnUiThread {
                                Toast.makeText(this@WorkshopParkingModule, obj, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }


        })
    }

    private fun parseVehicleListData(objArray: JSONArray): List<String> {
        val paymentIdList = mutableListOf("Select Vehicle Number")

        for (i in 0 until objArray.length()) {
            val item = objArray.getJSONObject(i)
            val name = item.getString("REG_NO")
            paymentIdList.add(name)
        }

        return paymentIdList
    }

    private fun workShopTestDriveVehHistory() {
        val intent = Intent(this@WorkshopParkingModule, WorkshopParkingInOutTrack::class.java)
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
                        val adapter = ArrayAdapter(this@WorkshopParkingModule, android.R.layout.simple_spinner_item, cities
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
            .url("${ApiFile.APP_URL}/gateTypeMaster/gateTypeForParking?locId=$locId&attribute1=$login_name")
            .build()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val cities = parseGateNo(it)
                    runOnUiThread {
                        val adapter = ArrayAdapter(this@WorkshopParkingModule, android.R.layout.simple_spinner_item, cities
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
                .url("${ApiFile.APP_URL}/gateTypeMaster/gateTypeForParking?locId=$locId&attribute1=$login_name")
                .build()
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val response = client.newCall(request).execute()
                    val jsonData = response.body?.string()
                    jsonData?.let {
                        val cities = parseGateType(it)
                        runOnUiThread {
                            val adapter = ArrayAdapter(
                                this@WorkshopParkingModule, android.R.layout.simple_spinner_item, cities
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
                cities.add(gateTpe)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return cities
    }


    private fun parseOrgIds(jsonData: String): List<String> {
        val cities = mutableListOf<String>()
        try {
            val jsonObject = JSONObject(jsonData)
            val jsonArray = jsonObject.getJSONArray("obj")
            cities.add("Select Reason")
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


    private fun fetchParkLocations() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.APP_URL}/orgDef/getLocationPark?operating_unit=$locId")
            .build()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val cities = parseOrgIds(it)
                    runOnUiThread {
                        val adapter = ArrayAdapter(
                            this@WorkshopParkingModule, android.R.layout.simple_spinner_item, cities
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


    private fun populateFieldsAfterInSearch() {
        kmTxt.visibility=View.VISIBLE
        currentKMSField.visibility=View.VISIBLE
        captureToKm.visibility=View.VISIBLE
        regNoDetails.visibility=View.VISIBLE
        newVehicleInPremises.visibility=View.VISIBLE
        remarksTxt.visibility=View.VISIBLE
        remarksField.visibility=View.VISIBLE
        driverTxt.visibility=View.VISIBLE
        driverNameAutoComplete.visibility=View.VISIBLE
        refreshButton.visibility=View.VISIBLE
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
        refreshButton.visibility=View.VISIBLE
        reasonCodeLov.visibility=View.GONE
        reasonCodeTxtView.visibility=View.GONE
        gateNoLov.visibility=View.GONE
        gateNumberTxtView.visibility=View.GONE
        gateTypeTxtView.visibility=View.GONE
        gateTypeLov.visibility=View.GONE
        driverNameAutoComplete.visibility=View.VISIBLE
        transferLocationLov.visibility=View.VISIBLE
        transferLocationTxtView.visibility=View.VISIBLE
    }

    private fun populateFieldsAfterOutSearchFirstTime() {
        kmTxt.visibility=View.VISIBLE
        currentKMSField.visibility=View.VISIBLE
        captureToKm.visibility=View.VISIBLE
        regNoDetails.visibility=View.VISIBLE
        newVehicleOutPremises.visibility=View.VISIBLE
        remarksTxt.visibility=View.VISIBLE
        remarksField.visibility=View.VISIBLE
        driverTxt.visibility=View.VISIBLE
        refreshButton.visibility=View.VISIBLE
        reasonCodeLov.visibility=View.GONE
        reasonCodeTxtView.visibility=View.GONE
        gateNoLov.visibility=View.VISIBLE
        gateNumberTxtView.visibility=View.VISIBLE
        gateTypeTxtView.visibility=View.GONE
        gateTypeLov.visibility=View.GONE
        driverNameAutoComplete.visibility=View.VISIBLE
        transferLocationLov.visibility=View.VISIBLE
        transferLocationTxtView.visibility=View.VISIBLE
    }


//    private fun detailsForVehicleInFirstTime() {
//        fetchGateNo()
//        fetchDriverName()
//        val client = OkHttpClient()
//        val vehNo = newVehEditText.text.toString()
//        if(vehNo.isEmpty()){
//            Toast.makeText(this@WorkshopParkingModule,"Please enter vehicle number.",Toast.LENGTH_SHORT).show()
//            return
//        }
//        val url = ApiFile.APP_URL + "/tdParking/vehParkInDet?regNo=$vehNo"
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
//                    val jcData3 = allData(
//                        DEPT = stockItem.optString("DEPT"),
//                        ENGINENO = stockItem.optString("ENGINENO"),
//                        CHASSIS_NO = stockItem.optString("CHASSIS_NO"),
//                        REGNO = stockItem.optString("REGNO"),
//                        CUSTNAME = stockItem.optString("CUSTNAME"),
//                        VIN = stockItem.optString("VIN"),
//                        VARIANT_CODE = stockItem.optString("VARIANT_CODE"),
//                        CONTACTNO = stockItem.optString("CONTACTNO"),
//                        MODEL_DESC = stockItem.optString("MODEL_DESC"),
//                        ERPACCTNO = stockItem.optString("ERPACCTNO"),
//                        //Masters Table Data
//                        ACCOUNT_NUMBER = stockItem.optString("ACCOUNT_NUMBER"),
//                        ADDRESS = stockItem.optString("ADDRESS"),
//                        CUST_NAME = stockItem.optString("CUST_NAME"),
//                        EMAIL_ADDRESS = stockItem.optString("EMAIL_ADDRESS"),
//                        ENGINE_NO = stockItem.optString("ENGINE_NO"),
//                        INSTANCE_NUMBER = stockItem.optString("INSTANCE_NUMBER"),
//                        PRIMARY_PHONE_NUMBER = stockItem.optString("PRIMARY_PHONE_NUMBER"),
//                        REGISTRATION_DATE = stockItem.optString("REGISTRATION_DATE"),
//                        //Out after vehicle in from location
//                        IN_KM = stockItem.optString("IN_KM"),
//                        IN_TIME = stockItem.optString("IN_TIME"),
//                        REG_NO = stockItem.optString("REG_NO"),
//                        REMARKS = stockItem.optString("REMARKS"),
//                        TEST_DRIVE_NO = stockItem.optString("TEST_DRIVE_NO"),
//                        LOCATION = stockItem.optString("LOCATION"),
//                        //In after test Drive
//                        OUT_KM = stockItem.optString("OUT_KM"),
//                        OUT_TIME = stockItem.optString("OUT_TIME"),
//                        SERVICE_ADVISOR=stockItem.optString("SERVICE_ADVISOR"),
//                        DRIVER_IN=stockItem.optString("DRIVER_IN"),
//                        DRIVER_OUT =stockItem.optString("DRIVER_OUT"),
//                        VEHICLE_DESC = stockItem.optString("VEHICLE_DESC")
//                    )
//
//                    val responseMessage = jsonObject.getString("message")
//
//                    when (responseMessage) {
//                        "Details Found Successfully In Parking Table" -> { //1
//                            runOnUiThread {
//                                populateFieldsAfterVehicleInAfterTestDrive(jcData3)
//                                populateFieldsAfterInSearch()
//                                gateNumberTxtView.visibility=View.VISIBLE
//                                gateNoLov.visibility=View.VISIBLE
//                                Toast.makeText(
//                                    this@WorkshopParkingModule,
//                                    "Details found for Vehicle No: $vehNo",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                        "Details Found Successfully In Master Table" -> { //3
//                            runOnUiThread {
//                                populateFieldsDuringInFromMasterVehicle(jcData3)
//                                populateFieldsAfterInSearch()
//                                Toast.makeText(
//                                    this@WorkshopParkingModule,
//                                    "Details Found Successfully for Vehicle No: $vehNo",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                        "New Vehicle" -> { //4
//                            runOnUiThread {
//                                populateFieldsDuringInForNewVehicle(jcData3)
//                                populateFieldsAfterInSearch()
//                                Toast.makeText(
//                                    this@WorkshopParkingModule,
//                                    "New Vehicle details for Vehicle No: $vehNo",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                        else -> {
//                            runOnUiThread {
//                                Toast.makeText(
//                                    this@WorkshopParkingModule,
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
//                        this@WorkshopParkingModule,
//                        "Failed to fetch details for vehicle No: $vehNo",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//    }


    private fun detailsForVehicleInFirstTime() {
        fetchGateNo()
        fetchDriverName()

        val client = OkHttpClient()
        val vehNo = newVehEditText.text.toString()

        if (vehNo.isEmpty()) {
            Toast.makeText(this@WorkshopParkingModule, "Please enter vehicle number.", Toast.LENGTH_SHORT).show()
            return
        }

        val url = ApiFile.APP_URL + "/tdParking/vehParkInDet?regNo=$vehNo"

        val request = Request.Builder()
            .url(url)
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()

                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    val responseMessage = jsonObject.optString("message")

                    when (responseMessage) {
                        "Details Found Successfully In Parking Table",
                        "Details Found Successfully In Master Table",
                        "New Vehicle" -> {
                            val objArray = jsonObject.optJSONArray("obj")
                            if (objArray != null && objArray.length() > 0) {
                                val stockItem = objArray.getJSONObject(0)

                                val jcData3 = allData(
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
                                    ACCOUNT_NUMBER = stockItem.optString("ACCOUNT_NUMBER"),
                                    ADDRESS = stockItem.optString("ADDRESS"),
                                    CUST_NAME = stockItem.optString("CUST_NAME"),
                                    EMAIL_ADDRESS = stockItem.optString("EMAIL_ADDRESS"),
                                    ENGINE_NO = stockItem.optString("ENGINE_NO"),
                                    INSTANCE_NUMBER = stockItem.optString("INSTANCE_NUMBER"),
                                    PRIMARY_PHONE_NUMBER = stockItem.optString("PRIMARY_PHONE_NUMBER"),
                                    REGISTRATION_DATE = stockItem.optString("REGISTRATION_DATE"),
                                    IN_KM = stockItem.optString("IN_KM"),
                                    IN_TIME = stockItem.optString("IN_TIME"),
                                    REG_NO = stockItem.optString("REG_NO"),
                                    REMARKS = stockItem.optString("REMARKS"),
                                    TEST_DRIVE_NO = stockItem.optString("TEST_DRIVE_NO"),
                                    LOCATION = stockItem.optString("LOCATION"),
                                    OUT_KM = stockItem.optString("OUT_KM"),
                                    OUT_TIME = stockItem.optString("OUT_TIME"),
                                    SERVICE_ADVISOR = stockItem.optString("SERVICE_ADVISOR"),
                                    DRIVER_IN = stockItem.optString("DRIVER_IN"),
                                    DRIVER_OUT = stockItem.optString("DRIVER_OUT"),
                                    VEHICLE_DESC = stockItem.optString("VEHICLE_DESC")
                                )

                                runOnUiThread {
                                    when (responseMessage) {
                                        "Details Found Successfully In Parking Table" -> {
                                            populateFieldsAfterVehicleInAfterTestDrive(jcData3)
                                            populateFieldsAfterInSearch()
                                            gateNumberTxtView.visibility = View.VISIBLE
                                            gateNoLov.visibility = View.VISIBLE
                                        }

                                        "Details Found Successfully In Master Table" -> {
                                            populateFieldsDuringInFromMasterVehicle(jcData3)
                                            populateFieldsAfterInSearch()
                                        }

                                        "New Vehicle" -> {
                                            populateFieldsDuringInForNewVehicle(jcData3)
                                            populateFieldsAfterInSearch()
                                        }
                                    }

                                    Toast.makeText(
                                        this@WorkshopParkingModule,
                                        "$responseMessage for Vehicle No: $vehNo",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                runOnUiThread {
                                    Toast.makeText(
                                        this@WorkshopParkingModule,
                                        "No details found in response.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }

                        "Vehicle Already In" -> {
                            val regNo = jsonObject.optString("obj")
                            runOnUiThread {
                                Toast.makeText(
                                    this@WorkshopParkingModule,
                                    "Vehicle Already In: $regNo",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        else -> {
                            runOnUiThread {
                                Toast.makeText(
                                    this@WorkshopParkingModule,
                                    "Unexpected response: $responseMessage",
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
                        this@WorkshopParkingModule,
                        "Failed to fetch details for vehicle No: $vehNo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun detailsForVehicleOut() {
        fetchGateNo()
        fetchDriverName()
        val client = OkHttpClient()
        val vehNo = newVehEditText.text.toString()
        if(vehNo.isEmpty()){
            Toast.makeText(this@WorkshopParkingModule,"Please enter vehicle number.",Toast.LENGTH_SHORT).show()
            return
        }
        val url = ApiFile.APP_URL + "/tdParking/vehParkOutDet?regNo=$vehNo"

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
                        SERVICE_ADVISOR = stockItem.optString("SERVICE_ADVISOR"),
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
                        DRIVER_IN=stockItem.optString("DRIVER_IN"),
                        DRIVER_OUT =stockItem.optString("DRIVER_OUT"),
                        VEHICLE_DESC = stockItem.optString("VEHICLE_DESC")
                    )

                    val responseMessage = jsonObject.getString("message")

                    when (responseMessage) {
                        "Details Found Successfully In Parking Table" -> {
                            runOnUiThread {
                                populateFieldsAfterVehicleOutForFirstTime(jcData3)
                                populateFieldsAfterOutSearch()
                                Log.d("jsonData",jsonData)
                                Log.d("jsonObject",jsonObject.toString())

                                Toast.makeText(
                                    this@WorkshopParkingModule,
                                    "Details found in Test Drive Table for Vehicle No: $vehNo",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        "Details Found Successfully In Master Table" -> {
                            runOnUiThread {
                                populateFieldsDuringInFromMasterVehicle(jcData3)
                                populateFieldsAfterOutSearchFirstTime()
                                Log.d("jsonData",jsonData)
                                Log.d("jsonObject",jsonObject.toString())
                                Toast.makeText(
                                    this@WorkshopParkingModule,
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
                                    this@WorkshopParkingModule,
                                    "New Vehicle details for Vehicle No: $vehNo",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        else -> {
                            runOnUiThread {
                                Toast.makeText(
                                    this@WorkshopParkingModule,
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
                        this@WorkshopParkingModule,
                        "Failed to fetch details for vehicle No: $vehNo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    //Post Data For vehicle in at location and in after test drive
    private fun vehicleIn() {
        val driverName = driverNameAutoComplete.text.toString()
        val currentKms = currentKMSField.text.toString()
        val remarks = remarksField.text.toString()
        val department=selectedDeptTextView.text.toString()
        var gateNo=gateNumber
        var gateType=gateType
        Log.d("GateNumber",gateNo)
        Log.d("GateNumber",gateType)


        if(gateNoLov.selectedItem.toString()=="Select Gate Number"){
            gateNo="-"
            gateType="-"
        }

        if(currentKms.isEmpty()){
//            currentKms="0"
            Toast.makeText(this, "Please enter current kilometers", Toast.LENGTH_SHORT).show()
            return
        }

        if(driverName.isEmpty()){
            Toast.makeText(this, "Please enter driver name to proceed", Toast.LENGTH_SHORT).show()
            return
        }

        if (department=="Select Department") {
            Toast.makeText(this, "Please select department", Toast.LENGTH_SHORT).show()
            return
        }

        if (::departmentFromResponse.isInitialized) {
            if(departmentFromResponse.isNotEmpty() ) {
                if(departmentFromResponse!=selectedDeptTextView.text.toString()){
                    Toast.makeText(this,"Please select the proper department of vehicle",Toast.LENGTH_SHORT).show()
                    return
                }
            }
        }

        val url = ApiFile.APP_URL + "/tdParking/vehParkIn/"
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

            if (::engineNo.isInitialized) {
                if(engineNo.isNotEmpty() ) {
                    put("engineNo", engineNo)
                }
            }

            if (::outKmNewVeh.isInitialized) {
                if(outKmNewVeh.isNotEmpty()) {
                    val outKms=outKmNewVeh.toInt()
                    if(currentKms.toInt()<outKms){
                        Toast.makeText(this@WorkshopParkingModule,"Current KM's must be greater than Out KM.",Toast.LENGTH_SHORT).show()
                        return
                    }
                }
            }

            put("locId", locId.toString())
            put("driverIn", driverName)
            put("inKm", currentKms)
            put("ouId", ouId.toString())
            put("dept", department)
            put("createdBy", login_name)
            put("location", location_name)
            put("gateNo",gateNo)
            put("gateType",gateType)
            if(remarks.isEmpty()) {
                put("remarks", "-")
            } else {
                put("remarks", remarks)
            }
            put("updatedBy", login_name)
            if (::serviceAdvisor.isInitialized) {
                if(serviceAdvisor.isNotEmpty() ) {
                    put("attribute1", serviceAdvisor)
                }
            }
            if (::model.isInitialized) {
                if(model.isNotEmpty() ) {
                    put("attribute2", model)
                }
            }

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
                                    this@WorkshopParkingModule,
                                    "Test Drive Vehicle Already Received",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            message.contains("Cannot receive vehicle", ignoreCase = true) -> {
                                Toast.makeText(
                                    this@WorkshopParkingModule,
                                    "Vehicle Already In...",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            responseCode == 200 -> {
                                Toast.makeText(
                                    this@WorkshopParkingModule,
                                    "Vehicle in successfully!!!",
                                    Toast.LENGTH_LONG
                                ).show()
                                resetFields()
                            }
                            else -> {
                                Toast.makeText(
                                    this@WorkshopParkingModule,
                                    "Failed to save data. Error code: $responseCode",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@WorkshopParkingModule,
                            "No response from server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@WorkshopParkingModule,
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
        val remarks=remarksField.text.toString()
        val driverName=driverNameAutoComplete.text.toString()
        var reasonCode=reasonCodeLov.selectedItem.toString()
        val gateNoJson=gateNumber
        val gateTypeJson=gateType
        val departmentNameLov=selectedDeptTextView.text.toString()
        val outKms=currentKms.toInt()

        if(driverName.isEmpty()){
            Toast.makeText(this, "Please enter driver name to proceed", Toast.LENGTH_SHORT).show()
            return
        }

        if(transferLocationLov.selectedItem.toString()!="Select Transfer Location"){
            attribute5=transferLocationLov.selectedItem.toString()
        } else if (transferLocationLov.selectedItem.toString()=="Select Transfer Location"){
            Toast.makeText(this, "Please select Transfer location", Toast.LENGTH_SHORT).show()
            return
        }


        if(currentKms.isEmpty()){
            Toast.makeText(this, "Please enter current kilometers", Toast.LENGTH_SHORT).show()
            return
        }

        if(reasonCode=="Select Test Drive Reason"){
            reasonCode="-"
        }

        if(gateNoLov.selectedItem.toString()=="Select Gate Number"){
            gateNumber="-"
            gateType="-"
        }


        Log.d("parkingDesc",attribute5)
        Log.d("parkingReason",reasonCode)


        if (::departmentFromResponse.isInitialized) {
            if(departmentFromResponse.isNotEmpty() ) {
                if(departmentFromResponse!=selectedDeptTextView.text.toString()){
                    if(remarks.isEmpty()){
                        Toast.makeText(this,"YOU HAVE SELECTED DIFFERENT DEPARTMENT.\nPLEASE ENTER IN REMARKS",Toast.LENGTH_SHORT).show()
                        return
                    }
                }
            }
        }

        if (::inKmNewVeh.isInitialized) {
            if(inKmNewVeh.isNotEmpty()) {
                val inKms=inKmNewVeh.toInt()
                if(inKms>outKms){
                    Toast.makeText(this,"Current KM's must be greater than IN KM.",Toast.LENGTH_SHORT).show()
                    return
                }
            }
        }

        val url = "${ApiFile.APP_URL}/tdParking/vehParkOut"
        val json = JSONObject().apply {
            put("updatedBy", login_name)
            put("regNo",regNo)
            put("outKm", currentKms)
            put("dept", departmentNameLov)
            put("location",location_name)
            put("parkingReason",reasonCode)
            if (::gateNumber.isInitialized) {
                if(gateNumber.isNotEmpty() ) {
                    put("gateNo", gateNumber)
                }
            }
            if (::gateType.isInitialized) {
                if(gateType.isNotEmpty() ) {
                    put("gateType", gateType)
                }
            }
            if (::attribute5.isInitialized) {
                if(attribute5.isNotEmpty() ) {
                    put("parkingDesc", attribute5)
                } else {
                    put("parkingDesc", "-")
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

            if (::serviceAdvisor.isInitialized) {
                if(serviceAdvisor.isNotEmpty() ) {
                    put("attribute1", serviceAdvisor)
                }
            }
            if (::model.isInitialized) {
                if(model.isNotEmpty() ) {
                    put("attribute2", model)
                }
            }

            put("remarks",remarks)
            put("driverOut",driverName)
            put("createdBy",login_name)
            put("ouId",ouId)
            put("locId",locId)

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
                    Toast.makeText(this@WorkshopParkingModule, "Failed to update vehicle", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val responseBody = it.body?.string() ?: ""

                    runOnUiThread {
                        when {
                            responseBody.contains("Vehicle Already Out", ignoreCase = true) -> {
                                Toast.makeText(
                                    this@WorkshopParkingModule,
                                    "Vehicle Already Out.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            it.isSuccessful -> {
                                Toast.makeText(
                                    this@WorkshopParkingModule,
                                    "Vehicle out successfully!",
                                    Toast.LENGTH_LONG
                                ).show()
                                resetFields()
                            }
                            responseBody.contains("Invalid serial number format in testDriveNo.", ignoreCase = true) -> {
                                Toast.makeText(
                                    this@WorkshopParkingModule,
                                    "Invalid serial number format in testDriveNo.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            else -> {
                                Toast.makeText(
                                    this@WorkshopParkingModule,
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

    private fun populateFieldsDuringInFromMasterVehicle(jcData:allData) {
        val table = findViewById<TableLayout>(R.id.tableLayout2)
        table.removeAllViews()

        val detailsMap = mutableMapOf(
            "REG NO" to jcData.INSTANCE_NUMBER,
            "CHASSIS NO" to jcData.CHASSIS_NO,
            "ENGINE NO" to jcData.ENGINE_NO,
            "MODEL" to jcData.VEHICLE_DESC,
            "SERV ADV" to jcData.SERVICE_ADVISOR
        )

        regNo=jcData.INSTANCE_NUMBER
        engineNo=jcData.ENGINE_NO
        chassisNo=jcData.CHASSIS_NO
        custName=jcData.CUST_NAME
        vinNo=jcData.VIN
        model=jcData.VEHICLE_DESC
        serviceAdvisor=jcData.SERVICE_ADVISOR

        for ((label, value) in detailsMap) {
            if (value != "-" && value != "0") {
                val row = LayoutInflater.from(this).inflate(R.layout.table_row_gate_pass, null) as TableRow
                val labelTextView = row.findViewById<TextView>(R.id.label)
                val valueTextView = row.findViewById<TextView>(R.id.value)

                labelTextView.text = label
                valueTextView.text = value

                table.addView(row)
            }
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
            if (value != "-" && value != "0") {
                val row = LayoutInflater.from(this).inflate(R.layout.table_row_gate_pass, null) as TableRow
                val labelTextView = row.findViewById<TextView>(R.id.label)
                val valueTextView = row.findViewById<TextView>(R.id.value)

                labelTextView.text = label
                valueTextView.text = value

                table.addView(row)
            }
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
            "VIN" to jcData.VIN,
            "CHASSIS NO" to jcData.CHASSIS_NO,
            "ENGINE NO" to jcData.ENGINE_NO,
            "MODEL" to jcData.MODEL_DESC,
            "SERV ADV" to jcData.SERVICE_ADVISOR,
            "LOCATION" to jcData.LOCATION,
            "DEPARTMENT" to jcData.DEPT,
            "IN KM" to jcData.IN_KM,
            "IN TIME" to jcData.IN_TIME,
            "REMARKS" to jcData.REMARKS,
            "DRIVER" to jcData.DRIVER_IN
        )

        Log.d("VIN NUMBER---->",jcData.VIN)

        regNo=jcData.REG_NO
        inKmNewVeh=jcData.IN_KM
        engineNo=jcData.ENGINE_NO
        vinNo=jcData.VIN
        chassisNo=jcData.CHASSIS_NO
        departmentFromResponse=jcData.DEPT
        Log.d("IN - KILOMETERS---->",inKmNewVeh)


        for ((label, value) in detailsMap) {
            if (value != "-" && value != "0") {
                val row = LayoutInflater.from(this).inflate(R.layout.table_row_gate_pass, null) as TableRow
                val labelTextView = row.findViewById<TextView>(R.id.label)
                val valueTextView = row.findViewById<TextView>(R.id.value)

                labelTextView.text = label
                valueTextView.text = value

                table.addView(row)
            }
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
            "VIN" to jcData.VIN,
            "CHASSIS_NO" to jcData.CHASSIS_NO,
            "ENGINENO" to jcData.ENGINE_NO,
            "MODEL" to jcData.MODEL_DESC,
            "SERV ADV" to jcData.SERVICE_ADVISOR,
            "DEPT." to jcData.DEPT,
            "LOCATION" to jcData.LOCATION,
            "OUT KM" to jcData.OUT_KM,
            "OUT TIME" to jcData.OUT_TIME,
            "REMARKS" to jcData.REMARKS,
            "DRIVER" to jcData.DRIVER_OUT
        )
        Log.d("VIN NUMBER---->",jcData.VIN)

        regNo=jcData.REG_NO
        outKm=jcData.OUT_KM
        engineNo=jcData.ENGINE_NO
        vinNo=jcData.VIN
        chassisNo=jcData.CHASSIS_NO
        custName=jcData.CUST_NAME
        departmentFromResponse=jcData.DEPT
        model=jcData.MODEL_DESC
        serviceAdvisor=jcData.SERVICE_ADVISOR
        outKmNewVeh=jcData.OUT_KM



        for ((label, value) in detailsMap) {
            if (value != "-" && value != "0") {
                val row = LayoutInflater.from(this).inflate(R.layout.table_row_gate_pass, null) as TableRow
                val labelTextView = row.findViewById<TextView>(R.id.label)
                val valueTextView = row.findViewById<TextView>(R.id.value)

                labelTextView.text = label
                valueTextView.text = value

                table.addView(row)
            }
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
        driverNameAutoComplete.visibility=View.GONE
        driverNameAutoComplete.setText("")
        captureVehNumberIn.visibility=View.GONE
        remarksTxt.visibility=View.GONE
        remarksField.visibility=View.GONE
        remarksField.setText("")
        newVehicleOutPremises.visibility=View.GONE
        newVehLL.visibility=View.GONE
        newVehEditText.setText("")
        newVehicleInPremises.visibility=View.GONE
        refreshButton.visibility=View.GONE
        regNo=""
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
        selectedDeptTextView.text=""
        forNewVehicleOut.isEnabled = false
        forNewVehicleIn.isEnabled = false
        forNewVehicleIn.setBackgroundColor(ContextCompat.getColor(this, R.color.secondary_text_color))
        forNewVehicleOut.setBackgroundColor(ContextCompat.getColor(this, R.color.secondary_text_color))
        selectedDeptLL.visibility=View.GONE
        selectedDeptTextView.text=""
        departmentFromResponse=""
        departmentName=""
        model=""
        serviceAdvisor=""
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
        //Vehicle In first time from stock table...
        val DEPT: String,
        val CONTACTNO:String,
        val REGNO: String,
        val ERPACCTNO: String,
        val CUSTNAME:String ,
        val VARIANT_CODE:String ,
        val VIN:String ,
        val ENGINENO: String,
        val CHASSIS_NO: String,
        val MODEL_DESC: String,
        val DRIVER_IN:String,
        val DRIVER_OUT:String,

        //Vehicle In first time from masters table....
        val ENGINE_NO: String,
        val ADDRESS: String,
        val INSTANCE_NUMBER:String ,
        val REGISTRATION_DATE:String,
        val EMAIL_ADDRESS:String,
        val ACCOUNT_NUMBER:String,
        val CUST_NAME:String,
        val PRIMARY_PHONE_NUMBER: String,
        val SERVICE_ADVISOR:String,

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

        val VEHICLE_DESC:String
    )

}



