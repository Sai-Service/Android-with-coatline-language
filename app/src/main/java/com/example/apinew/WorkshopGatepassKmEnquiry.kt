//////package com.example.apinew
//////
//////import android.content.Intent
//////import android.graphics.Bitmap
//////import android.graphics.BitmapFactory
//////import android.net.Uri
//////import android.os.Bundle
//////import android.provider.MediaStore
//////import android.util.Log
//////import android.view.LayoutInflater
//////import android.view.View
//////import android.widget.Button
//////import android.widget.EditText
//////import android.widget.ImageButton
//////import android.widget.ImageView
//////import android.widget.Spinner
//////import android.widget.TableLayout
//////import android.widget.TableRow
//////import android.widget.TextView
//////import android.widget.Toast
//////import androidx.appcompat.app.AppCompatActivity
//////import androidx.core.content.FileProvider
//////import com.google.mlkit.vision.common.InputImage
//////import com.google.mlkit.vision.text.TextRecognition
//////import com.google.mlkit.vision.text.latin.TextRecognizerOptions
//////import kotlinx.coroutines.Dispatchers
//////import kotlinx.coroutines.GlobalScope
//////import kotlinx.coroutines.launch
//////import okhttp3.Call
//////import okhttp3.Callback
//////import okhttp3.MediaType.Companion.toMediaType
//////import okhttp3.MediaType.Companion.toMediaTypeOrNull
//////import okhttp3.MultipartBody
//////import okhttp3.OkHttpClient
//////import okhttp3.Request
//////import okhttp3.RequestBody
//////import okhttp3.RequestBody.Companion.toRequestBody
//////import okhttp3.Response
//////import org.json.JSONObject
//////import java.io.File
//////import java.io.IOException
//////import java.text.SimpleDateFormat
//////import java.util.Locale
//////
//////class WorkshopGatepassKmEnquiry : AppCompatActivity() {
//////    private lateinit var login_name: String
//////    private lateinit var deptName: String
//////    private var ouId: Int = 0
//////    private var locId: Int = 0
//////    private lateinit var location_name: String
//////    private lateinit var username:TextView
//////    private lateinit var locIdTxt:TextView
//////    private lateinit var deptIntent:TextView
//////    private lateinit var vehNoLLForOut: View
//////    private lateinit var vehNoLLForIn:View
//////    private lateinit var vehHistoryLL:View
//////    private lateinit var newVehOutLL:View
//////    private lateinit var newVehLL:View
//////    private lateinit var newVehOutEditText:EditText
//////    private lateinit var newVehEditText:EditText
//////    private lateinit var newVehInButton:ImageButton
//////    private lateinit var forTestDriveOut:TextView
//////    private lateinit var forTestDriveIn:TextView
//////    private lateinit var forNewVehicleIn:TextView
//////    private lateinit var vehNoInputFieldOut:EditText
//////    private lateinit var searchVehButtonOut:ImageButton
//////    private lateinit var vehNoInputFieldIn:EditText
//////    private lateinit var searchVehButtonIn:ImageButton
//////    private lateinit var kmTxt:TextView
//////    private lateinit var currentKMSField:EditText
//////    private lateinit var regNoDetails:TextView
//////    private lateinit var driverTxt:TextView
//////    private lateinit var driverNameField:EditText
//////    private lateinit var remarksTxt:TextView
//////    private lateinit var remarksField:EditText
//////    private lateinit var vehicleOutForTestDrive:Button
//////    private lateinit var vehicleInAfterTestDrive:Button
//////    private lateinit var newVehicleInPremises:Button
//////    private lateinit var newVehicleOutPremises:Button
//////    private lateinit var refreshButton:Button
//////    private lateinit var forNewVehicleOut:TextView
//////    private lateinit var newVehOutButton:ImageButton
//////    private lateinit var regNo:String
//////    private lateinit var jobCardNo:String
//////    private lateinit var chassisNo:String
//////    private lateinit var engineNo:String
//////    private lateinit var vinNo:String
//////    private lateinit var testDriveNo:String
//////    private lateinit var custName:String
//////    private lateinit var outKm:String
//////    private lateinit var inKmNewVeh:String
//////    private lateinit var vehicleHistory:TextView
//////    private lateinit var captureToKm:ImageButton
//////    private var clickedPlaceholder: ImageView? = null
//////    private var photoUri: Uri? = null
//////    private var photoFile: File? = null
//////
//////
//////
//////    override fun onCreate(savedInstanceState: Bundle?) {
//////        super.onCreate(savedInstanceState)
//////        setContentView(R.layout.activity_workshop_gatepass_km_enquiry)
//////
//////        username=findViewById(R.id.username)
//////        locIdTxt=findViewById(R.id.locIdTxt)
//////        deptIntent=findViewById(R.id.deptIntent)
//////
//////        vehNoLLForOut=findViewById(R.id.vehNoLLForOut)
//////        vehNoLLForIn=findViewById(R.id.vehNoLLForIn)
//////        forTestDriveOut=findViewById(R.id.forTestDriveOut)
//////        forTestDriveIn=findViewById(R.id.forTestDriveIn)
//////        vehNoInputFieldOut=findViewById(R.id.vehNoInputFieldOut)
//////        searchVehButtonOut=findViewById(R.id.searchVehButtonOut)
//////        vehNoInputFieldIn=findViewById(R.id.vehNoInputFieldIn)
//////        searchVehButtonIn=findViewById(R.id.searchVehButtonIn)
//////        kmTxt=findViewById(R.id.kmTxt)
//////        currentKMSField=findViewById(R.id.currentKMSField)
//////        val tableLayout = findViewById<TableLayout>(R.id.tableLayout2)
//////        tableLayout.removeAllViews()
//////        regNoDetails=findViewById(R.id.regNoDetails)
//////        driverTxt=findViewById(R.id.driverTxt)
//////        driverNameField=findViewById(R.id.driverNameField)
//////        remarksTxt=findViewById(R.id.remarksTxt)
//////        remarksField=findViewById(R.id.remarksField)
//////        vehicleOutForTestDrive=findViewById(R.id.vehicleOutForTestDrive)
//////        vehicleInAfterTestDrive=findViewById(R.id.vehicleInAfterTestDrive)
//////        vehicleHistory=findViewById(R.id.vehicleHistory)
//////        captureToKm=findViewById(R.id.captureToKm)
//////        vehHistoryLL=findViewById(R.id.vehHistoryLL)
//////        forNewVehicleIn=findViewById(R.id.forNewVehicleIn)
//////        newVehLL=findViewById(R.id.newVehLL)
//////        newVehEditText=findViewById(R.id.newVehEditText)
//////        newVehInButton=findViewById(R.id.newVehInButton)
//////        newVehicleInPremises=findViewById(R.id.newVehicleInPremises)
//////        forNewVehicleOut=findViewById(R.id.forNewVehicleOut)
//////        newVehOutLL=findViewById(R.id.newVehOutLL)
//////        newVehOutEditText=findViewById(R.id.newVehOutEditText)
//////        newVehOutButton=findViewById(R.id.newVehOutButton)
//////        newVehicleOutPremises=findViewById(R.id.newVehicleOutPremises)
//////        refreshButton=findViewById(R.id.refreshButton)
//////
//////
//////        kmTxt.visibility=View.GONE
//////        currentKMSField.visibility=View.GONE
//////        captureToKm.visibility=View.GONE
//////        regNoDetails.visibility=View.GONE
//////        driverTxt.visibility=View.GONE
//////        driverNameField.visibility=View.GONE
//////        remarksTxt.visibility=View.GONE
//////        remarksField.visibility=View.GONE
//////        vehicleOutForTestDrive.visibility=View.GONE
//////        vehicleInAfterTestDrive.visibility=View.GONE
//////        newVehicleInPremises.visibility=View.GONE
//////        newVehicleOutPremises.visibility=View.GONE
//////        refreshButton.visibility=View.GONE
//////
//////
//////
//////        locId = intent.getIntExtra("locId", 0)
//////        ouId = intent.getIntExtra("ouId", 0)
//////        deptName = intent.getStringExtra("deptName") ?: ""
//////        location_name = intent.getStringExtra("location_name") ?: ""
//////        login_name = intent.getStringExtra("login_name") ?: ""
//////
//////
//////        username.text=login_name
//////        locIdTxt.text= location_name
//////        deptIntent.text=deptName
//////        vehNoLLForOut.visibility=View.GONE
//////        vehNoLLForIn.visibility=View.GONE
//////        newVehLL.visibility=View.GONE
//////        newVehOutLL.visibility=View.GONE
//////
//////
//////        forTestDriveOut.setOnClickListener {
//////            vehNoLLForIn.visibility=View.GONE
//////            newVehLL.visibility=View.GONE
//////            vehNoLLForOut.visibility=View.VISIBLE
//////            newVehOutLL.visibility=View.GONE
//////            vehNoInputFieldIn.setText("")
//////            newVehEditText.setText("")
//////            newVehOutEditText.setText("")
//////        }
//////
//////        forTestDriveIn.setOnClickListener {
//////            newVehLL.visibility=View.GONE
//////            vehNoLLForIn.visibility=View.VISIBLE
//////            vehNoLLForOut.visibility=View.GONE
//////            newVehOutLL.visibility=View.GONE
//////            vehNoInputFieldOut.setText("")
//////            newVehEditText.setText("")
//////            newVehOutEditText.setText("")
//////        }
//////
//////        forNewVehicleIn.setOnClickListener {
//////            newVehLL.visibility=View.VISIBLE
//////            vehNoLLForIn.visibility=View.GONE
//////            vehNoLLForOut.visibility=View.GONE
//////            newVehOutLL.visibility=View.GONE
//////            vehNoInputFieldOut.setText("")
//////            vehNoInputFieldIn.setText("")
//////            newVehOutEditText.setText("")
//////        }
//////
//////        forNewVehicleOut.setOnClickListener {
//////            newVehOutLL.visibility=View.VISIBLE
//////            newVehLL.visibility=View.GONE
//////            vehNoLLForIn.visibility=View.GONE
//////            vehNoLLForOut.visibility=View.GONE
//////            vehNoInputFieldOut.setText("")
//////            vehNoInputFieldIn.setText("")
//////            vehNoInputFieldOut.setText("")
//////        }
//////
////////        searchVehButtonOut.setOnClickListener { fetchVehDataOutTime() }
////////        searchVehButtonIn.setOnClickListener { fetchVehDataInTime() }
//////
////////        vehicleOutForTestDrive.setOnClickListener { vehicleOut() }
////////        vehicleInAfterTestDrive.setOnClickListener { updateOutVehicleIn() }
//////
//////        vehicleHistory.setOnClickListener { workShopTestDriveVehHistory()  }
//////
//////        newVehInButton.setOnClickListener { fetchVehDataNewOut()  }
//////
//////        newVehicleInPremises.setOnClickListener { newVehicleOutForTestDrive() }
//////
//////        newVehOutButton.setOnClickListener { fetchVehDataNewVehicleIn() }
//////
//////        newVehicleOutPremises.setOnClickListener { newVehicleInAfterTestDrive() }
//////
//////        refreshButton.setOnClickListener { resetFields() }
//////
//////        captureToKm.setOnClickListener {
//////            clickedPlaceholder = captureToKm
//////            openCamera()
//////        }
//////
//////    }
//////
//////    private fun openCamera() {
//////        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//////        val photoFile = createImageFile()
//////        photoFile?.also {
//////            photoUri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", it)
//////            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
//////            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//////            startActivityForResult(takePictureIntent, 101)
//////        }
//////    }
//////
//////    private fun createImageFile(): File? {
//////        val storageDir: File? = externalCacheDir
//////        return File.createTempFile("JPEG_${System.currentTimeMillis()}_", ".jpg", storageDir).apply {
//////            photoFile = this
//////        }
//////    }
//////
//////    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//////        super.onActivityResult(requestCode, resultCode, data)
//////        if (requestCode == 101 && resultCode == RESULT_OK) {
//////            val bitmap = BitmapFactory.decodeFile(photoFile?.absolutePath)
//////            if (clickedPlaceholder == captureToKm) {
//////                processImageForText(bitmap)
//////            }
////////            else {
////////                clickedPlaceholder?.setImageBitmap(bitmap)
////////            }
//////        }
//////    }
//////    private fun processImageForText(bitmap: Bitmap) {
//////        val image = InputImage.fromBitmap(bitmap, 0)
//////        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
//////
//////        recognizer.process(image)
//////            .addOnSuccessListener { visionText ->
//////                handleExtractedText(visionText)
//////            }
//////            .addOnFailureListener { e ->
//////                Toast.makeText(this, "Failed to extract text: ${e.message}", Toast.LENGTH_SHORT).show()
//////            }
//////    }
//////
//////    private fun handleExtractedText(result:com.google.mlkit.vision.text.Text) {
//////        val recognizedText = result.text
//////        if (recognizedText.isNotEmpty()) {
//////            val regex = Regex("(\\d+)\\s*(?=km)", RegexOption.IGNORE_CASE)
//////            val matchResult = regex.find(recognizedText)
//////
//////            if (matchResult != null) {
//////                val numericText = matchResult.value.trim()
//////                currentKMSField.setText(numericText)
//////            } else {
//////                Toast.makeText(this, "No valid reading found", Toast.LENGTH_SHORT).show()
//////            }
//////        } else {
//////            Toast.makeText(this, "No text found", Toast.LENGTH_SHORT).show()
//////        }
//////    }
//////
//////
//////    private fun populateFieldsForNewVehicle() {
//////        kmTxt.visibility=View.VISIBLE
//////        currentKMSField.visibility=View.VISIBLE
//////        captureToKm.visibility=View.VISIBLE
//////        regNoDetails.visibility=View.VISIBLE
//////        newVehicleInPremises.visibility=View.VISIBLE
//////        remarksTxt.visibility=View.VISIBLE
//////        remarksField.visibility=View.VISIBLE
//////        driverTxt.visibility=View.VISIBLE
//////        driverNameField.visibility=View.VISIBLE
//////    }
//////
//////    private fun populateFieldsForNewVehicleOut() {
//////        kmTxt.visibility=View.VISIBLE
//////        currentKMSField.visibility=View.VISIBLE
//////        captureToKm.visibility=View.VISIBLE
//////        regNoDetails.visibility=View.VISIBLE
//////        newVehicleOutPremises.visibility=View.VISIBLE
//////        remarksTxt.visibility=View.VISIBLE
//////        remarksField.visibility=View.VISIBLE
//////    }
//////
//////    private fun fetchVehDataNewOut() {
//////        val client = OkHttpClient()
//////        val vehNo = newVehEditText.text.toString()
//////        val url = ApiFile.APP_URL + "/service/wsVehDetForTestDriveOut?regNo=$vehNo"
//////
//////        Log.d("URL:", url)
//////
//////        val request = Request.Builder()
//////            .url(url)
//////            .build()
//////
//////        GlobalScope.launch(Dispatchers.IO) {
//////            try {
//////                val response = client.newCall(request).execute()
//////                val jsonData = response.body?.string()
//////                jsonData?.let {
//////                    val jsonObject = JSONObject(it)
//////                    Log.d("Data", jsonObject.toString())
//////
//////                    val stockItem = jsonObject.getJSONArray("obj").getJSONObject(0)
//////
//////                    val jcData3 = newVehDetails(
//////                        JOBCARDNO=stockItem.optString("JOBCARDNO"),
//////                        DEPT = stockItem.optString("DEPT"),
//////                        ENGINENO = stockItem.optString("ENGINENO"),
//////                        CHASSIS_NO = stockItem.optString("CHASSIS_NO"),
//////                        REGNO = stockItem.optString("REGNO"),
//////                        CUSTNAME = stockItem.optString("CUSTNAME"),
//////                        VIN = stockItem.optString("VIN"),
//////                        VARIANT_CODE = stockItem.optString("VARIANT_CODE"),
//////                        CONTACTNO = stockItem.optString("CONTACTNO"),
//////                        MODEL_DESC = stockItem.optString("MODEL_DESC"),
//////                        ERPACCTNO = stockItem.optString("ERPACCTNO"),
//////                        ACCOUNT_NUMBER = stockItem.optString("ACCOUNT_NUMBER"),
//////                        ADDRESS = stockItem.optString("ADDRESS"),
//////                        CUST_NAME = stockItem.optString("CUST_NAME"),
//////                        EMAIL_ADDRESS = stockItem.optString("EMAIL_ADDRESS"),
//////                        INSTANCE_NUMBER = stockItem.optString("INSTANCE_NUMBER"),
//////                        PRIMARY_PHONE_NUMBER = stockItem.optString("PRIMARY_PHONE_NUMBER"),
//////                        REGISTRATION_DATE = stockItem.optString("REGISTRATION_DATE")
//////                    )
//////
//////                    val responseMessage = jsonObject.getString("message")
//////
//////                    when (responseMessage) {
//////                        "Details Found Successfully in Service Stock" -> {
//////                            runOnUiThread {
//////                                popFieldStock(jcData3)
//////                                populateFieldsForNewVehicle()
//////                                Toast.makeText(
//////                                    this@WorkshopGatepassKmEnquiry,
//////                                    "Details found in Service Stock for Vehicle No: $vehNo",
//////                                    Toast.LENGTH_SHORT
//////                                ).show()
//////                            }
//////                        }
//////                        "Details Found Successfully in master table" -> {
//////                            runOnUiThread {
//////                                popFieldMasters(jcData3)
//////                                populateFieldsForNewVehicle()
//////                                Toast.makeText(
//////                                    this@WorkshopGatepassKmEnquiry,
//////                                    "Details found in master table for Vehicle No: $vehNo",
//////                                    Toast.LENGTH_SHORT
//////                                ).show()
//////                            }
//////                        }
//////                        "New Vehicle" -> {
//////                            runOnUiThread {
//////                                popFieldNew(jcData3)
//////                                populateFieldsForNewVehicle()
//////                                Toast.makeText(
//////                                    this@WorkshopGatepassKmEnquiry,
//////                                    "New Vehicle details for Vehicle No: $vehNo",
//////                                    Toast.LENGTH_SHORT
//////                                ).show()
//////                            }
//////                        }
//////                        else -> {
//////                            runOnUiThread {
//////                                Toast.makeText(
//////                                    this@WorkshopGatepassKmEnquiry,
//////                                    "Unexpected response for Vehicle No: $vehNo",
//////                                    Toast.LENGTH_SHORT
//////                                ).show()
//////                            }
//////                        }
//////                    }
//////                }
//////            } catch (e: Exception) {
//////                e.printStackTrace()
//////                runOnUiThread {
//////                    Toast.makeText(
//////                        this@WorkshopGatepassKmEnquiry,
//////                        "Failed to fetch details for vehicle No: $vehNo",
//////                        Toast.LENGTH_SHORT
//////                    ).show()
//////                }
//////            }
//////        }
//////    }
//////
//////
//////    private fun fetchVehDataNewVehicleIn() {
//////        val client = OkHttpClient()
//////        val vehNo=newVehOutEditText.text.toString()
//////        val url =ApiFile.APP_URL+"/service/wsVehDetForTestDriveIn?regNo=$vehNo"
//////
//////        Log.d("URL:", url)
//////
//////        val request = Request.Builder()
//////            .url(url)
//////            .build()
//////
//////        GlobalScope.launch(Dispatchers.IO) {
//////            try {
//////                val response = client.newCall(request).execute()
//////                val jsonData = response.body?.string()
//////                jsonData?.let {
//////                    val jsonObject = JSONObject(it)
//////                    Log.d("Data", jsonObject.toString())
//////                    val stockItem = jsonObject.getJSONArray("obj").getJSONObject(0)
//////
//////                    val jcData4 = newVehDetailsOut(
//////                        LOCATION = stockItem.optString("LOCATION"),
//////                        REG_NO = stockItem.optString("REG_NO"),
//////                        OUT_KM = stockItem.optString("OUT_KM"),
//////                        DRIVER_NAME = stockItem.optString("DRIVER_NAME"),
//////                        OUT_TIME = formatDateTime(stockItem.optString("OUT_TIME")),
//////                        TEST_DRIVE_NO=stockItem.optString("TEST_DRIVE_NO"),
//////                        CHASSIS_NO = stockItem.optString("CHASSIS_NO"),
//////                        DEPT = stockItem.optString("DEPT"),
//////                        ENGINE_NO = stockItem.optString("ENGINE_NO"),
//////                        JOBCARDNO = stockItem.optString("JOBCARDNO"),
//////                        VIN = stockItem.optString("VIN")
//////                        )
//////                    runOnUiThread {
//////                        Toast.makeText(
//////                            this@WorkshopGatepassKmEnquiry,
//////                            "Details found Successfully \n" +
//////                                    " for Vehicle No: $vehNo",
//////                            Toast.LENGTH_SHORT
//////                        ).show()
//////                        populateFields6(jcData4)
//////                        populateFieldsForNewVehicleOut()
//////                        refreshButton.visibility=View.VISIBLE
//////                    }
//////                }
//////            } catch (e: Exception) {
//////                e.printStackTrace()
//////                runOnUiThread {
//////                    Toast.makeText(
//////                        this@WorkshopGatepassKmEnquiry,
//////                        "Failed to fetch details for vehicle No: $vehNo",
//////                        Toast.LENGTH_SHORT
//////                    ).show()
//////                }
//////            }
//////        }
//////    }
//////
//////
//////    private fun formatDateTime(dateTime: String): String {
//////        return try {
//////            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
//////            val outputDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
//////            val outputTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
//////            val date = inputFormat.parse(dateTime)
//////            val formattedDate = date?.let { outputDateFormat.format(it) }
//////            val formattedTime = date?.let { outputTimeFormat.format(it) }
//////            "$formattedDate $formattedTime"
//////        } catch (e: Exception) {
//////            dateTime
//////        }
//////    }
//////
//////
//////    private fun newVehicleOutForTestDrive() {
//////        val driverName = driverNameField.text.toString()
//////        val currentKms = currentKMSField.text.toString()
//////        val remarks = remarksField.text.toString()
//////
//////        if (currentKMSField.text.toString().isEmpty()) {
//////            Toast.makeText(this, "Current Kilometers are required", Toast.LENGTH_SHORT).show()
//////            return
//////        }
//////
//////        if (driverName.isEmpty()) {
//////            Toast.makeText(this, "Driver Name is required", Toast.LENGTH_SHORT).show()
//////            return
//////        }
//////
//////        if (remarks.isEmpty()) {
//////            Toast.makeText(this, "Remarks required", Toast.LENGTH_SHORT).show()
//////            return
//////        }
//////
//////        val url = ApiFile.APP_URL + "/service/wsVehTdOut/"
//////        val jsonObject = JSONObject().apply {
//////            put("regNo", regNo)
////////            put("vin", "-")
//////            if (::vinNo.isInitialized) {
//////                if(vinNo.isNotEmpty() ) {
//////                    put("vin", vinNo)
//////                }
//////            }
////////            put("chassisNo", "-")
//////            if (::chassisNo.isInitialized) {
//////                if(chassisNo.isNotEmpty() ) {
//////                    put("chassisNo", chassisNo)
//////                }
//////            }
////////            put("jobCardNo", "")
//////            if (::jobCardNo.isInitialized) {
//////                if(jobCardNo.isNotEmpty() ) {
//////                    put("jobCardNo", jobCardNo)
//////                }
//////            }
//////
////////            put("engineNo", "-")
//////            if (::engineNo.isInitialized) {
//////                if(engineNo.isNotEmpty() ) {
//////                    put("engineNo", engineNo)
//////                }
//////            }
//////            put("locCode", locId.toString())
//////            put("driverName", driverName)
//////            put("outKm", currentKms)
//////            put("ou", ouId.toString())
//////            put("dept", deptName)
//////            put("remarks", remarks)
//////            put("createdBy", login_name)
//////            put("location", location_name)
//////            put("authorisedBy", login_name)
//////            put("updatedBy", login_name)
//////            if (::custName.isInitialized) {
//////                if(custName.isNotEmpty() ) {
//////                    put("attribute1", custName)
//////                }
//////            }
////////            put("attribute1", custName)
//////        }
//////
//////        val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
//////
//////        val request = Request.Builder()
//////            .url(url)
//////            .post(requestBody)
//////            .build()
//////
//////        val client = OkHttpClient()
//////
//////        GlobalScope.launch(Dispatchers.IO) {
//////            try {
//////                val response = client.newCall(request).execute()
//////                val responseCode = response.code
//////                val responseBody = response.body?.string()
//////
//////                Log.d("newVehicleIn", "Response Code: $responseCode")
//////                Log.d("newVehicleIn", "Response Body: $responseBody")
//////
//////                runOnUiThread {
//////                    if (responseBody != null) {
//////                        val jsonResponse = JSONObject(responseBody)
//////                        val message = jsonResponse.optString("message", "")
//////
//////                        when {
//////                            message.contains("Vehicle is still in premises, cannot move out for test drive", ignoreCase = true) -> {
//////                                Toast.makeText(
//////                                    this@WorkshopGatepassKmEnquiry,
//////                                    "Vehicle is already out for test drive...!!!",
//////                                    Toast.LENGTH_LONG
//////                                ).show()
//////                            }
//////                            responseCode == 200 -> {
//////                                Toast.makeText(
//////                                    this@WorkshopGatepassKmEnquiry,
//////                                    "Vehicle out for test drive...!!!",
//////                                    Toast.LENGTH_LONG
//////                                ).show()
//////                                resetFields()
//////                            }
//////                            else -> {
//////                                Toast.makeText(
//////                                    this@WorkshopGatepassKmEnquiry,
//////                                    "Failed to save data. Error code: $responseCode",
//////                                    Toast.LENGTH_SHORT
//////                                ).show()
//////                            }
//////                        }
//////                    } else {
//////                        Toast.makeText(
//////                            this@WorkshopGatepassKmEnquiry,
//////                            "No response from server",
//////                            Toast.LENGTH_SHORT
//////                        ).show()
//////                    }
//////                }
//////            } catch (e: Exception) {
//////                e.printStackTrace()
//////                Log.e("newVehicleIn", "Error: ${e.message}")
//////                runOnUiThread {
//////                    Toast.makeText(
//////                        this@WorkshopGatepassKmEnquiry,
//////                        "Error saving data: ${e.message}",
//////                        Toast.LENGTH_SHORT
//////                    ).show()
//////                }
//////            }
//////        }
//////    }
//////
//////
//////
//////    private fun newVehicleInAfterTestDrive() {
//////        val currentKms=currentKMSField.text.toString()
//////        val currentKms2=currentKMSField.text.toString()
//////        val inKms=inKmNewVeh.toInt()
//////        val remarks=remarksField.text.toString()
//////
//////        if(currentKMSField.text.toString().isEmpty()){
//////            Toast.makeText(this,"Please enter the Current Kilometers",Toast.LENGTH_SHORT).show()
//////            return
//////        }
//////
//////        if(currentKms2.isEmpty()){
//////            Toast.makeText(this,"Please enter the Current Kilometers",Toast.LENGTH_SHORT).show()
//////            return
//////        }
//////
//////        if(currentKms.toInt()<=inKms){
//////            Toast.makeText(this@WorkshopGatepassKmEnquiry,"Current Kilometers must be greater than out Kilometers....",Toast.LENGTH_SHORT).show()
//////            return
//////        }
//////
//////        val url = "${ApiFile.APP_URL}/service/wsVehTdIn"
//////        val json = JSONObject().apply {
//////            put("updatedBy", login_name)
//////            put("testDriveNo",testDriveNo)
//////            put("inKm", currentKms)
////////            put("jobCardNo","")
//////            if (::jobCardNo.isInitialized) {
//////                if(jobCardNo.isNotEmpty() ) {
//////                    put("jobCardNo", jobCardNo)
//////                }
//////            }
//////            put("remarks",remarks)
//////
//////        }
//////        Log.d("URL:", url)
//////
//////        val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
//////        Log.d("URL FOR UPDATE:", json.toString())
//////        val request = Request.Builder()
//////            .url(url)
//////            .put(requestBody)
//////            .addHeader("Content-Type", "application/json")
//////            .build()
//////
//////        val client = OkHttpClient()
//////
//////        client.newCall(request).enqueue(object : Callback {
//////            override fun onFailure(call: Call, e: IOException) {
//////                e.printStackTrace()
//////                runOnUiThread {
//////                    Toast.makeText(this@WorkshopGatepassKmEnquiry, "Failed to update vehicle", Toast.LENGTH_SHORT).show()
//////                }
//////            }
//////            override fun onResponse(call: Call, response: Response) {
//////                response.use {
//////                    if (it.isSuccessful) {
//////                        runOnUiThread {
//////                            Toast.makeText(this@WorkshopGatepassKmEnquiry, "Vehicle In after test drive!!!", Toast.LENGTH_SHORT).show()
//////                            resetFields()
//////                        }
//////                    } else {
//////                        val responseBody = it.body?.string() ?: ""
//////                        val errorMessage = if (responseBody.contains("Invalid VIN")) {
//////                            "Invalid VIN"
//////                        } else {
//////                            "Unexpected code ${it.code}"
//////                        }
//////                        runOnUiThread {
//////                            Toast.makeText(this@WorkshopGatepassKmEnquiry, errorMessage, Toast.LENGTH_SHORT).show()
//////                        }
//////                    }
//////                }
//////            }
//////        })
//////    }
//////
//////
//////    private fun popFieldStock(jcData:newVehDetails) {
//////        val table = findViewById<TableLayout>(R.id.tableLayout2)
//////        table.removeAllViews()
//////
//////        val detailsMap = mutableMapOf(
//////            "VEH NO." to jcData.REGNO,
//////            "JOBCARDNO" to jcData.JOBCARDNO,
//////            "VIN" to jcData.VIN,
//////            "CHASSIS NO" to jcData.CHASSIS_NO,
//////            "ENGINE NO" to jcData.ENGINENO,
//////            "MODEL" to jcData.MODEL_DESC,
//////            "VARIANT" to jcData.VARIANT_CODE,
//////            "ERACCNO" to jcData.ERPACCTNO,
//////            "CUST NAME" to jcData.CUSTNAME,
//////            "CONTACT" to jcData.CONTACTNO,
//////        )
//////        regNo=jcData.REGNO
//////        custName=jcData.CUSTNAME
//////        chassisNo=jcData.CHASSIS_NO
//////        vinNo=jcData.VIN
//////        engineNo=jcData.ENGINENO
//////        jobCardNo=jcData.JOBCARDNO
//////        for ((label, value) in detailsMap) {
//////            val row = LayoutInflater.from(this).inflate(R.layout.table_row_gate_pass, null) as TableRow
//////            val labelTextView = row.findViewById<TextView>(R.id.label)
//////            val valueTextView = row.findViewById<TextView>(R.id.value)
//////
//////            labelTextView.text = label
//////            valueTextView.text = value
//////
//////            table.addView(row)
//////        }
//////
//////        if (table.childCount > 0) {
//////            val lastRow = table.getChildAt(table.childCount - 1) as TableRow
//////            lastRow.findViewById<View>(R.id.labelDivider).visibility = View.GONE
//////            lastRow.findViewById<View>(R.id.valueDivider).visibility = View.GONE
//////        }
//////    }
//////
//////    private fun popFieldMasters(jcData:newVehDetails) {
//////        val table = findViewById<TableLayout>(R.id.tableLayout2)
//////        table.removeAllViews()
//////
//////        val detailsMap = mutableMapOf(
//////            "VEH NO." to jcData.INSTANCE_NUMBER,
//////            "ACCOUNT NUMBER" to jcData.ACCOUNT_NUMBER,
//////            "CUST NAME" to jcData.CUST_NAME,
//////            "EMAIL ADDRESS" to jcData.EMAIL_ADDRESS,
//////            "PRIMARY PHONE NUMBER" to jcData.PRIMARY_PHONE_NUMBER,
//////            "REGISTRATION DATE" to jcData.REGISTRATION_DATE
//////        )
//////
//////        regNo=jcData.INSTANCE_NUMBER
//////        custName=jcData.CUST_NAME
//////
//////        for ((label, value) in detailsMap) {
//////            val row = LayoutInflater.from(this).inflate(R.layout.table_row_gate_pass, null) as TableRow
//////            val labelTextView = row.findViewById<TextView>(R.id.label)
//////            val valueTextView = row.findViewById<TextView>(R.id.value)
//////
//////            labelTextView.text = label
//////            valueTextView.text = value
//////
//////            table.addView(row)
//////        }
//////
//////        if (table.childCount > 0) {
//////            val lastRow = table.getChildAt(table.childCount - 1) as TableRow
//////            lastRow.findViewById<View>(R.id.labelDivider).visibility = View.GONE
//////            lastRow.findViewById<View>(R.id.valueDivider).visibility = View.GONE
//////        }
//////    }
//////
//////
//////    private fun popFieldNew(jcData:newVehDetails) {
//////        val table = findViewById<TableLayout>(R.id.tableLayout2)
//////        table.removeAllViews()
////////        val vehNo = newVehEditText.text.toString()
//////
//////
//////        val detailsMap = mutableMapOf(
//////            "VEH NO." to jcData.REGNO,
//////            "MESSAGE" to "This is a new Vehicle!!"
//////        )
//////        regNo=jcData.REGNO
//////
//////        for ((label, value) in detailsMap) {
//////            val row = LayoutInflater.from(this).inflate(R.layout.table_row_gate_pass, null) as TableRow
//////            val labelTextView = row.findViewById<TextView>(R.id.label)
//////            val valueTextView = row.findViewById<TextView>(R.id.value)
//////
//////            labelTextView.text = label
//////            valueTextView.text = value
//////
//////            table.addView(row)
//////        }
//////
//////        if (table.childCount > 0) {
//////            val lastRow = table.getChildAt(table.childCount - 1) as TableRow
//////            lastRow.findViewById<View>(R.id.labelDivider).visibility = View.GONE
//////            lastRow.findViewById<View>(R.id.valueDivider).visibility = View.GONE
//////        }
//////    }
//////
//////
//////    private fun populateFields6(jcData:newVehDetailsOut) {
//////        val table = findViewById<TableLayout>(R.id.tableLayout2)
//////        table.removeAllViews()
//////
//////        val detailsMap = mutableMapOf(
//////            "VEH NO." to jcData.REG_NO,
//////            "JOBCARDNO" to jcData.JOBCARDNO,
//////            "VIN" to jcData.VIN,
//////            "CHASSIS NO" to jcData.CHASSIS_NO,
//////            "ENGINE NO" to jcData.ENGINE_NO,
//////            "LOCATION" to jcData.LOCATION,
//////            "OUT KM" to jcData.OUT_KM,
//////            "OUT TIME" to jcData.OUT_TIME,
//////            "DRIVER" to jcData.DRIVER_NAME
//////        )
//////        regNo=jcData.REG_NO
//////        testDriveNo=jcData.TEST_DRIVE_NO
//////        inKmNewVeh=jcData.OUT_KM
//////        jobCardNo=jcData.JOBCARDNO
//////
//////        for ((label, value) in detailsMap) {
//////            val row = LayoutInflater.from(this).inflate(R.layout.table_row_gate_pass, null) as TableRow
//////            val labelTextView = row.findViewById<TextView>(R.id.label)
//////            val valueTextView = row.findViewById<TextView>(R.id.value)
//////
//////            labelTextView.text = label
//////            valueTextView.text = value
//////
//////            table.addView(row)
//////        }
//////
//////        if (table.childCount > 0) {
//////            val lastRow = table.getChildAt(table.childCount - 1) as TableRow
//////            lastRow.findViewById<View>(R.id.labelDivider).visibility = View.GONE
//////            lastRow.findViewById<View>(R.id.valueDivider).visibility = View.GONE
//////        }
//////    }
//////
//////
//////    private fun resetFields(){
//////        val tableLayout = findViewById<TableLayout>(R.id.tableLayout2)
//////        tableLayout.removeAllViews()
//////        vehNoInputFieldOut.setText("")
//////        vehNoInputFieldIn.setText("")
//////        kmTxt.visibility=View.GONE
//////        currentKMSField.visibility=View.GONE
//////        captureToKm.visibility=View.GONE
//////        currentKMSField.setText("")
//////        regNoDetails.visibility=View.GONE
//////        driverTxt.visibility=View.GONE
//////        driverNameField.visibility=View.GONE
//////        driverNameField.setText("")
//////        remarksTxt.visibility=View.GONE
//////        remarksField.visibility=View.GONE
//////        remarksField.setText("")
//////        vehicleOutForTestDrive.visibility=View.GONE
//////        vehicleInAfterTestDrive.visibility=View.GONE
//////        vehNoLLForIn.visibility=View.GONE
//////        vehNoLLForOut.visibility=View.GONE
//////        newVehOutEditText.setText("")
//////        newVehOutLL.visibility=View.GONE
//////        newVehicleOutPremises.visibility=View.GONE
//////        newVehLL.visibility=View.GONE
//////        newVehEditText.setText("")
//////        newVehicleInPremises.visibility=View.GONE
//////        refreshButton.visibility=View.GONE
//////        regNo=""
//////        jobCardNo=""
//////        chassisNo=""
//////        engineNo=""
//////        vinNo=""
//////        testDriveNo=""
//////        custName=""
//////        outKm=""
//////        inKmNewVeh=""
//////    }
//////
//////    private fun workShopTestDriveVehHistory() {
//////        val intent = Intent(this@WorkshopGatepassKmEnquiry, WorkshopTestDriveVehicleHistory::class.java)
//////        intent.putExtra("login_name", login_name)
//////        intent.putExtra("ouId", ouId)
//////        intent.putExtra("locId", locId)
//////        intent.putExtra("location_name",location_name)
//////        intent.putExtra("deptName",deptName)
//////        startActivity(intent)
//////    }
//////
//////
//////    data class newVehDetails(
//////        val DEPT:String,
//////        val ENGINENO:String,
//////        val CHASSIS_NO:String,
//////        val REGNO:String,
//////        val CUSTNAME:String,
//////        val VIN:String,
//////        val VARIANT_CODE:String,
//////        val CONTACTNO:String,
//////        val MODEL_DESC:String,
//////        val ERPACCTNO:String,
//////        val INSTANCE_NUMBER :String,
//////        val EMAIL_ADDRESS:String,
//////        val ADDRESS:String,
//////        val ACCOUNT_NUMBER:String,
//////        val CUST_NAME:String,
//////        val REGISTRATION_DATE:String,
//////        val PRIMARY_PHONE_NUMBER:String,
//////        val JOBCARDNO:String
//////    )
//////
//////    data class newVehDetailsOut(
//////        val LOCATION:String,
//////        val REG_NO:String,
//////        val VIN:String,
//////        val CHASSIS_NO:String,
//////        val DEPT:String,
//////        val OUT_KM:String,
//////        val DRIVER_NAME:String,
//////        val OUT_TIME:String,
//////        val TEST_DRIVE_NO:String,
//////        val ENGINE_NO:String,
//////        val JOBCARDNO:String
//////    )
//////
//////}
//////
//////
//////
////
////
////
////
////
////
////
////
////
////
////
////
////
////
////
//////////////////////////////////////////////////////////////////////////////////////////////////
////
////
////
////
//package com.example.apinew
//
//import android.content.Intent
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.net.Uri
//import android.os.Bundle
//import android.provider.MediaStore
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.widget.Button
//import android.widget.EditText
//import android.widget.ImageButton
//import android.widget.ImageView
//import android.widget.Spinner
//import android.widget.TableLayout
//import android.widget.TableRow
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.FileProvider
//import com.google.mlkit.vision.common.InputImage
//import com.google.mlkit.vision.text.TextRecognition
//import com.google.mlkit.vision.text.latin.TextRecognizerOptions
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.launch
//import okhttp3.Call
//import okhttp3.Callback
//import okhttp3.MediaType.Companion.toMediaType
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.MultipartBody
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.RequestBody
//import okhttp3.RequestBody.Companion.toRequestBody
//import okhttp3.Response
//import org.json.JSONObject
//import java.io.File
//import java.io.IOException
//import java.text.SimpleDateFormat
//import java.util.Locale
//
//class WorkshopGatepassKmEnquiry : AppCompatActivity() {
//    private lateinit var login_name: String
//    private lateinit var deptName: String
//    private var ouId: Int = 0
//    private var locId: Int = 0
//    private lateinit var location_name: String
//    private lateinit var username:TextView
//    private lateinit var locIdTxt:TextView
//    private lateinit var deptIntent:TextView
//    private lateinit var vehHistoryLL:View
//    private lateinit var newVehOutLL:View
//    private lateinit var newVehLL:View
//    private lateinit var newVehOutEditText:EditText
//    private lateinit var newVehEditText:EditText
//    private lateinit var newVehInButton:ImageButton
//    private lateinit var forTestDriveOut:TextView
//    private lateinit var forTestDriveIn:TextView
//    private lateinit var forNewVehicleIn:TextView
//    private lateinit var kmTxt:TextView
//    private lateinit var currentKMSField:EditText
//    private lateinit var regNoDetails:TextView
//    private lateinit var driverTxt:TextView
//    private lateinit var driverNameField:EditText
//    private lateinit var remarksTxt:TextView
//    private lateinit var remarksField:EditText
//    private lateinit var vehicleOutForTestDrive:Button
//    private lateinit var vehicleInAfterTestDrive:Button
//    private lateinit var newVehicleInPremises:Button
//    private lateinit var newVehicleOutPremises:Button
//    private lateinit var refreshButton:Button
//    private lateinit var forNewVehicleOut:TextView
//    private lateinit var newVehOutButton:ImageButton
//    private lateinit var regNo:String
//    private lateinit var jobCardNo:String
//    private lateinit var chassisNo:String
//    private lateinit var engineNo:String
//    private lateinit var vinNo:String
//    private lateinit var testDriveNo:String
//    private lateinit var custName:String
//    private lateinit var outKm:String
//    private lateinit var inKmNewVeh:String
//    private lateinit var vehicleHistory:TextView
//    private lateinit var captureToKm:ImageButton
//    private var clickedPlaceholder: ImageView? = null
//    private var photoUri: Uri? = null
//    private var photoFile: File? = null
//
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_workshop_gatepass_km_enquiry)
//
//        username=findViewById(R.id.username)
//        locIdTxt=findViewById(R.id.locIdTxt)
//        deptIntent=findViewById(R.id.deptIntent)
//        forTestDriveOut=findViewById(R.id.forTestDriveOut)
//        forTestDriveIn=findViewById(R.id.forTestDriveIn)
//        kmTxt=findViewById(R.id.kmTxt)
//        currentKMSField=findViewById(R.id.currentKMSField)
//        val tableLayout = findViewById<TableLayout>(R.id.tableLayout2)
//        tableLayout.removeAllViews()
//        regNoDetails=findViewById(R.id.regNoDetails)
//        driverTxt=findViewById(R.id.driverTxt)
//        driverNameField=findViewById(R.id.driverNameField)
//        remarksTxt=findViewById(R.id.remarksTxt)
//        remarksField=findViewById(R.id.remarksField)
//        vehicleHistory=findViewById(R.id.vehicleHistory)
//        captureToKm=findViewById(R.id.captureToKm)
//        vehHistoryLL=findViewById(R.id.vehHistoryLL)
//        forNewVehicleIn=findViewById(R.id.forNewVehicleIn)
//        newVehLL=findViewById(R.id.newVehLL)
//        newVehEditText=findViewById(R.id.newVehEditText)
//        newVehInButton=findViewById(R.id.newVehInButton)
//        newVehicleInPremises=findViewById(R.id.newVehicleInPremises)
//        forNewVehicleOut=findViewById(R.id.forNewVehicleOut)
//        newVehOutLL=findViewById(R.id.newVehOutLL)
//        newVehOutEditText=findViewById(R.id.newVehOutEditText)
//        newVehOutButton=findViewById(R.id.newVehOutButton)
//        newVehicleOutPremises=findViewById(R.id.newVehicleOutPremises)
//        refreshButton=findViewById(R.id.refreshButton)
//
//
//        kmTxt.visibility=View.GONE
//        currentKMSField.visibility=View.GONE
//        captureToKm.visibility=View.GONE
//        regNoDetails.visibility=View.GONE
//        driverTxt.visibility=View.GONE
//        driverNameField.visibility=View.GONE
//        remarksTxt.visibility=View.GONE
//        remarksField.visibility=View.GONE
//        vehicleOutForTestDrive.visibility=View.GONE
//        vehicleInAfterTestDrive.visibility=View.GONE
//        newVehicleInPremises.visibility=View.GONE
//        newVehicleOutPremises.visibility=View.GONE
//        refreshButton.visibility=View.GONE
//
//
//
//        locId = intent.getIntExtra("locId", 0)
//        ouId = intent.getIntExtra("ouId", 0)
//        deptName = intent.getStringExtra("deptName") ?: ""
//        location_name = intent.getStringExtra("location_name") ?: ""
//        login_name = intent.getStringExtra("login_name") ?: ""
//
//
//        username.text=login_name
//        locIdTxt.text= location_name
//        deptIntent.text=deptName
//        newVehLL.visibility=View.GONE
//        newVehOutLL.visibility=View.GONE
//
//
//        forTestDriveOut.setOnClickListener {
//            newVehLL.visibility=View.GONE
//            newVehOutLL.visibility=View.GONE
//            newVehEditText.setText("")
//            newVehOutEditText.setText("")
//        }
//
//        forTestDriveIn.setOnClickListener {
//            newVehLL.visibility=View.GONE
//            newVehOutLL.visibility=View.GONE
//            newVehEditText.setText("")
//            newVehOutEditText.setText("")
//        }
//
//        forNewVehicleIn.setOnClickListener {
//            newVehLL.visibility=View.VISIBLE
//            newVehOutLL.visibility=View.GONE
//            newVehOutEditText.setText("")
//        }
//
//        forNewVehicleOut.setOnClickListener {
//            newVehOutLL.visibility=View.VISIBLE
//            newVehLL.visibility=View.GONE
//        }
//
////        searchVehButtonOut.setOnClickListener { fetchVehDataOutTime() }
////        searchVehButtonIn.setOnClickListener { fetchVehDataInTime() }
//
////        vehicleOutForTestDrive.setOnClickListener { vehicleOut() }
////        vehicleInAfterTestDrive.setOnClickListener { updateOutVehicleIn() }
//
//        vehicleHistory.setOnClickListener { workShopTestDriveVehHistory()  }
//
//        newVehInButton.setOnClickListener { fetchVehDataNewOut()  }
//
////        newVehicleInPremises.setOnClickListener { newVehicleOutForTestDrive() }
//
//        newVehicleInPremises.setOnClickListener { newVehicleInAfterTestDrive() }
//
//
//        newVehOutButton.setOnClickListener {
////            fetchVehDataNewVehicleIn()
//            fetchVehDataNewIn()
//        }
//
////        newVehicleOutPremises.setOnClickListener { newVehicleInAfterTestDrive() }
//
//        newVehicleOutPremises.setOnClickListener { newVehicleOutForTestDrive() }
//
//
//        refreshButton.setOnClickListener { resetFields() }
//
//        captureToKm.setOnClickListener {
//            clickedPlaceholder = captureToKm
//            openCamera()
//        }
//
//    }
//
//    private fun openCamera() {
//        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        val photoFile = createImageFile()
//        photoFile?.also {
//            photoUri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", it)
//            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
//            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            startActivityForResult(takePictureIntent, 101)
//        }
//    }
//
//    private fun createImageFile(): File? {
//        val storageDir: File? = externalCacheDir
//        return File.createTempFile("JPEG_${System.currentTimeMillis()}_", ".jpg", storageDir).apply {
//            photoFile = this
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 101 && resultCode == RESULT_OK) {
//            val bitmap = BitmapFactory.decodeFile(photoFile?.absolutePath)
//            if (clickedPlaceholder == captureToKm) {
//                processImageForText(bitmap)
//            }
////            else {
////                clickedPlaceholder?.setImageBitmap(bitmap)
////            }
//        }
//    }
//    private fun processImageForText(bitmap: Bitmap) {
//        val image = InputImage.fromBitmap(bitmap, 0)
//        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
//
//        recognizer.process(image)
//            .addOnSuccessListener { visionText ->
//                handleExtractedText(visionText)
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(this, "Failed to extract text: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    private fun handleExtractedText(result:com.google.mlkit.vision.text.Text) {
//        val recognizedText = result.text
//        if (recognizedText.isNotEmpty()) {
//            val regex = Regex("(\\d+)\\s*(?=km)", RegexOption.IGNORE_CASE)
//            val matchResult = regex.find(recognizedText)
//
//            if (matchResult != null) {
//                val numericText = matchResult.value.trim()
//                currentKMSField.setText(numericText)
//            } else {
//                Toast.makeText(this, "No valid reading found", Toast.LENGTH_SHORT).show()
//            }
//        } else {
//            Toast.makeText(this, "No text found", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//
//    private fun populateFieldsForNewVehicle() {
//        kmTxt.visibility=View.VISIBLE
//        currentKMSField.visibility=View.VISIBLE
//        captureToKm.visibility=View.VISIBLE
//        regNoDetails.visibility=View.VISIBLE
//        newVehicleInPremises.visibility=View.VISIBLE
//        remarksTxt.visibility=View.VISIBLE
//        remarksField.visibility=View.VISIBLE
//        driverTxt.visibility=View.VISIBLE
//        driverNameField.visibility=View.VISIBLE
//    }
//
//    private fun populateFieldsForNewVehicleOut() {
//        kmTxt.visibility=View.VISIBLE
//        currentKMSField.visibility=View.VISIBLE
//        captureToKm.visibility=View.VISIBLE
//        regNoDetails.visibility=View.VISIBLE
//        newVehicleOutPremises.visibility=View.VISIBLE
//        remarksTxt.visibility=View.VISIBLE
//        remarksField.visibility=View.VISIBLE
//    }
//
//    private fun fetchVehDataNewOut() {
//        val client = OkHttpClient()
//        val vehNo = newVehEditText.text.toString()
//        val url = ApiFile.APP_URL + "/service/wsVehDetForTestDriveOut?regNo=$vehNo"
//
//        Log.d("URL:", url)
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
//                    Log.d("Data", jsonObject.toString())
//
//                    val stockItem = jsonObject.getJSONArray("obj").getJSONObject(0)
//
//                    val jcData3 = allData(
//                        JOBCARDNO=stockItem.optString("JOBCARDNO"),
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
//                        ACCOUNT_NUMBER = stockItem.optString("ACCOUNT_NUMBER"),
//                        ADDRESS = stockItem.optString("ADDRESS"),
//                        CUST_NAME = stockItem.optString("CUST_NAME"),
//                        EMAIL_ADDRESS = stockItem.optString("EMAIL_ADDRESS"),
//                        INSTANCE_NUMBER = stockItem.optString("INSTANCE_NUMBER"),
//                        PRIMARY_PHONE_NUMBER = stockItem.optString("PRIMARY_PHONE_NUMBER"),
//                        REGISTRATION_DATE = stockItem.optString("REGISTRATION_DATE"),
//                        LOCATION = stockItem.optString("LOCATION"),
//                        REG_NO = stockItem.optString("REG_NO"),
//                        OUT_KM = stockItem.optString("OUT_KM"),
//                        DRIVER_NAME = stockItem.optString("DRIVER_NAME"),
//                        OUT_TIME = formatDateTime(stockItem.optString("OUT_TIME")),
//                        TEST_DRIVE_NO=stockItem.optString("TEST_DRIVE_NO"),
//                        ENGINE_NO = stockItem.optString("ENGINE_NO"),
//
//                        )
//
//                    val responseMessage = jsonObject.getString("message")
//
//                    when (responseMessage) {
//                        "Details Found Successfully in Service Stock" -> {
//                            runOnUiThread {
//                                popFieldStock(jcData3)
//                                populateFieldsForNewVehicle()
//                                Toast.makeText(
//                                    this@WorkshopGatepassKmEnquiry,
//                                    "Details found in Service Stock for Vehicle No: $vehNo",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                        "Details Found Successfully in master table" -> {
//                            runOnUiThread {
//                                popFieldMasters(jcData3)
//                                populateFieldsForNewVehicle()
//                                Toast.makeText(
//                                    this@WorkshopGatepassKmEnquiry,
//                                    "Details found in master table for Vehicle No: $vehNo",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                        "New Vehicle" -> {
//                            runOnUiThread {
//                                popFieldNew(jcData3)
//                                populateFieldsForNewVehicle()
//                                Toast.makeText(
//                                    this@WorkshopGatepassKmEnquiry,
//                                    "New Vehicle details for Vehicle No: $vehNo",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                        else -> {
//                            runOnUiThread {
//                                Toast.makeText(
//                                    this@WorkshopGatepassKmEnquiry,
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
//                        this@WorkshopGatepassKmEnquiry,
//                        "Failed to fetch details for vehicle No: $vehNo",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//    }
//
//
//    private fun fetchVehDataNewIn() {
//        val client = OkHttpClient()
////        val vehNo = newVehEditText.text.toString()
//        val vehNo=newVehOutEditText.text.toString()
//        val url = ApiFile.APP_URL + "/service/wsVehDetForTestDriveIn?regNo=$vehNo"
//
//        Log.d("URL:", url)
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
//                    Log.d("Data", jsonObject.toString())
//
//                    val stockItem = jsonObject.getJSONArray("obj").getJSONObject(0)
//
//                    val jcData4 = allData(
//                        LOCATION = stockItem.optString("LOCATION"),
//                        REG_NO = stockItem.optString("REG_NO"),
//                        OUT_KM = stockItem.optString("OUT_KM"),
//                        DRIVER_NAME = stockItem.optString("DRIVER_NAME"),
//                        OUT_TIME = formatDateTime(stockItem.optString("OUT_TIME")),
//                        TEST_DRIVE_NO=stockItem.optString("TEST_DRIVE_NO"),
//                        CHASSIS_NO = stockItem.optString("CHASSIS_NO"),
//                        DEPT = stockItem.optString("DEPT"),
//                        ENGINE_NO = stockItem.optString("ENGINE_NO"),
//                        JOBCARDNO = stockItem.optString("JOBCARDNO"),
//                        VIN = stockItem.optString("VIN"),
//                        ENGINENO = stockItem.optString("ENGINENO"),
//                        REGNO = stockItem.optString("REGNO"),
//                        CUSTNAME = stockItem.optString("CUSTNAME"),
//                        VARIANT_CODE = stockItem.optString("VARIANT_CODE"),
//                        CONTACTNO = stockItem.optString("CONTACTNO"),
//                        MODEL_DESC = stockItem.optString("MODEL_DESC"),
//                        ERPACCTNO = stockItem.optString("ERPACCTNO"),
//                        ACCOUNT_NUMBER = stockItem.optString("ACCOUNT_NUMBER"),
//                        ADDRESS = stockItem.optString("ADDRESS"),
//                        CUST_NAME = stockItem.optString("CUST_NAME"),
//                        EMAIL_ADDRESS = stockItem.optString("EMAIL_ADDRESS"),
//                        INSTANCE_NUMBER = stockItem.optString("INSTANCE_NUMBER"),
//                        PRIMARY_PHONE_NUMBER = stockItem.optString("PRIMARY_PHONE_NUMBER"),
//                        REGISTRATION_DATE = stockItem.optString("REGISTRATION_DATE")
//                    )
//                    val responseMessage = jsonObject.getString("message")
//
//                    when (responseMessage) {
//                        "Details Found Successfully in Service Stock" -> {
//                            runOnUiThread {
//                                popFieldStock(jcData4)
//                                populateFieldsForNewVehicle()
//                                Toast.makeText(
//                                    this@WorkshopGatepassKmEnquiry,
//                                    "Details found in Service Stock for Vehicle No: $vehNo",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                        "Details Found Successfully in master table" -> {
//                            runOnUiThread {
//                                popFieldMasters(jcData4)
//                                populateFieldsForNewVehicle()
//                                Toast.makeText(
//                                    this@WorkshopGatepassKmEnquiry,
//                                    "Details found in master table for Vehicle No: $vehNo",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                        "New Vehicle" -> {
//                            runOnUiThread {
//                                popFieldNew(jcData4)
//                                populateFieldsForNewVehicle()
//                                Toast.makeText(
//                                    this@WorkshopGatepassKmEnquiry,
//                                    "New Vehicle details for Vehicle No: $vehNo",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                        else -> {
//                            runOnUiThread {
//                                Toast.makeText(
//                                    this@WorkshopGatepassKmEnquiry,
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
//                        this@WorkshopGatepassKmEnquiry,
//                        "Failed to fetch details for vehicle No: $vehNo",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//    }
//
//
//    private fun fetchVehDataNewVehicleIn() {
//        val client = OkHttpClient()
//        val vehNo=newVehOutEditText.text.toString()
//        val url =ApiFile.APP_URL+"/service/wsVehDetForTestDriveIn?regNo=$vehNo"
//
//        Log.d("URL:", url)
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
//                    Log.d("Data", jsonObject.toString())
//                    val stockItem = jsonObject.getJSONArray("obj").getJSONObject(0)
//
//                    val jcData4 = newVehDetailsOut(
//                        LOCATION = stockItem.optString("LOCATION"),
//                        REG_NO = stockItem.optString("REG_NO"),
//                        OUT_KM = stockItem.optString("OUT_KM"),
//                        DRIVER_NAME = stockItem.optString("DRIVER_NAME"),
//                        OUT_TIME = formatDateTime(stockItem.optString("OUT_TIME")),
//                        TEST_DRIVE_NO=stockItem.optString("TEST_DRIVE_NO"),
//                        CHASSIS_NO = stockItem.optString("CHASSIS_NO"),
//                        DEPT = stockItem.optString("DEPT"),
//                        ENGINE_NO = stockItem.optString("ENGINE_NO"),
//                        JOBCARDNO = stockItem.optString("JOBCARDNO"),
//                        VIN = stockItem.optString("VIN")
//                    )
//                    runOnUiThread {
//                        Toast.makeText(
//                            this@WorkshopGatepassKmEnquiry,
//                            "Details found Successfully \n" +
//                                    " for Vehicle No: $vehNo",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        populateFields6(jcData4)
//                        populateFieldsForNewVehicleOut()
//                        refreshButton.visibility=View.VISIBLE
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                runOnUiThread {
//                    Toast.makeText(
//                        this@WorkshopGatepassKmEnquiry,
//                        "Failed to fetch details for vehicle No: $vehNo",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//    }
//
//
//    private fun formatDateTime(dateTime: String): String {
//        return try {
//            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
//            val outputDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
//            val outputTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
//            val date = inputFormat.parse(dateTime)
//            val formattedDate = date?.let { outputDateFormat.format(it) }
//            val formattedTime = date?.let { outputTimeFormat.format(it) }
//            "$formattedDate $formattedTime"
//        } catch (e: Exception) {
//            dateTime
//        }
//    }
//
//
//    private fun newVehicleOutForTestDrive() {
//        val driverName = driverNameField.text.toString()
//        val currentKms = currentKMSField.text.toString()
//        val remarks = remarksField.text.toString()
//
//        if (currentKMSField.text.toString().isEmpty()) {
//            Toast.makeText(this, "Current Kilometers are required", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        if (driverName.isEmpty()) {
//            Toast.makeText(this, "Driver Name is required", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        if (remarks.isEmpty()) {
//            Toast.makeText(this, "Remarks required", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val url = ApiFile.APP_URL + "/service/wsVehTdIn/"
//        val jsonObject = JSONObject().apply {
//            put("regNo", regNo)
////            put("vin", "-")
//            if (::vinNo.isInitialized) {
//                if(vinNo.isNotEmpty() ) {
//                    put("vin", vinNo)
//                }
//            }
////            put("chassisNo", "-")
//            if (::chassisNo.isInitialized) {
//                if(chassisNo.isNotEmpty() ) {
//                    put("chassisNo", chassisNo)
//                }
//            }
////            put("jobCardNo", "")
//            if (::jobCardNo.isInitialized) {
//                if(jobCardNo.isNotEmpty() ) {
//                    put("jobCardNo", jobCardNo)
//                }
//            }
//
////            put("engineNo", "-")
//            if (::engineNo.isInitialized) {
//                if(engineNo.isNotEmpty() ) {
//                    put("engineNo", engineNo)
//                }
//            }
//            put("locCode", locId.toString())
//            put("driverName", driverName)
//            put("inKm", currentKms)
//            put("ou", ouId.toString())
//            put("dept", deptName)
//            put("remarks", remarks)
//            put("createdBy", login_name)
//            put("location", location_name)
//            put("authorisedBy", login_name)
//            put("updatedBy", login_name)
//            if (::custName.isInitialized) {
//                if(custName.isNotEmpty() ) {
//                    put("attribute1", custName)
//                }
//            }
////            put("attribute1", custName)
//        }
//
//        val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
//
//        val request = Request.Builder()
//            .url(url)
//            .put(requestBody)
//            .build()
//
//        val client = OkHttpClient()
//
//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                val response = client.newCall(request).execute()
//                val responseCode = response.code
//                val responseBody = response.body?.string()
//
//                Log.d("newVehicleIn", "Response Code: $responseCode")
//                Log.d("newVehicleIn", "Response Body: $responseBody")
//
//                runOnUiThread {
//                    if (responseBody != null) {
//                        val jsonResponse = JSONObject(responseBody)
//                        val message = jsonResponse.optString("message", "")
//
//                        when {
//                            message.contains("Vehicle is still in premises, cannot move out for test drive", ignoreCase = true) -> {
//                                Toast.makeText(
//                                    this@WorkshopGatepassKmEnquiry,
//                                    "Vehicle is already out for test drive...!!!",
//                                    Toast.LENGTH_LONG
//                                ).show()
//                            }
//                            responseCode == 200 -> {
//                                Toast.makeText(
//                                    this@WorkshopGatepassKmEnquiry,
//                                    "Vehicle out for test drive...!!!",
//                                    Toast.LENGTH_LONG
//                                ).show()
//                                resetFields()
//                            }
//                            else -> {
//                                Toast.makeText(
//                                    this@WorkshopGatepassKmEnquiry,
//                                    "Failed to save data. Error code: $responseCode",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                    } else {
//                        Toast.makeText(
//                            this@WorkshopGatepassKmEnquiry,
//                            "No response from server",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Log.e("newVehicleIn", "Error: ${e.message}")
//                runOnUiThread {
//                    Toast.makeText(
//                        this@WorkshopGatepassKmEnquiry,
//                        "Error saving data: ${e.message}",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//    }
//
//
//
//    private fun newVehicleInAfterTestDrive() {
//        val currentKms=currentKMSField.text.toString()
//        val currentKms2=currentKMSField.text.toString()
//        val inKms=inKmNewVeh.toInt()
//        val remarks=remarksField.text.toString()
//
//        if(currentKMSField.text.toString().isEmpty()){
//            Toast.makeText(this,"Please enter the Current Kilometers",Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        if(currentKms2.isEmpty()){
//            Toast.makeText(this,"Please enter the Current Kilometers",Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        if(currentKms.toInt()<=inKms){
//            Toast.makeText(this@WorkshopGatepassKmEnquiry,"Current Kilometers must be greater than out Kilometers....",Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val url = "${ApiFile.APP_URL}/service/wsVehTdOut"
//        val json = JSONObject().apply {
//            put("updatedBy", login_name)
//            put("testDriveNo",testDriveNo)
//            put("outKm", currentKms)
////            put("jobCardNo","")
//            if (::jobCardNo.isInitialized) {
//                if(jobCardNo.isNotEmpty() ) {
//                    put("jobCardNo", jobCardNo)
//                }
//            }
//
//            if (::inKmNewVeh.isInitialized) {
//                if(inKmNewVeh.isNotEmpty() ) {
//                    put("inKm", inKmNewVeh)
//                }
//            }
//
//            if (::engineNo.isInitialized) {
//                if(engineNo.isNotEmpty() ) {
//                    put("engineNo", engineNo)
//                }
//            }
//            if (::vinNo.isInitialized) {
//                if(vinNo.isNotEmpty() ) {
//                    put("vin", vinNo)
//                }
//            }
//
//            put("remarks",remarks)
//
//        }
//        Log.d("URL:", url)
//
//        val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
//        Log.d("URL FOR UPDATE:", json.toString())
//        val request = Request.Builder()
//            .url(url)
//            .post(requestBody)
//            .addHeader("Content-Type", "application/json")
//            .build()
//
//        val client = OkHttpClient()
//
//        client.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                e.printStackTrace()
//                runOnUiThread {
//                    Toast.makeText(this@WorkshopGatepassKmEnquiry, "Failed to update vehicle", Toast.LENGTH_SHORT).show()
//                }
//            }
//            override fun onResponse(call: Call, response: Response) {
//                response.use {
//                    if (it.isSuccessful) {
//                        runOnUiThread {
//                            Toast.makeText(this@WorkshopGatepassKmEnquiry, "Vehicle In after test drive!!!", Toast.LENGTH_SHORT).show()
//                            resetFields()
//                        }
//                    } else {
//                        val responseBody = it.body?.string() ?: ""
//                        val errorMessage = if (responseBody.contains("Invalid VIN")) {
//                            "Invalid VIN"
//                        } else {
//                            "Unexpected code ${it.code}"
//                        }
//                        runOnUiThread {
//                            Toast.makeText(this@WorkshopGatepassKmEnquiry, errorMessage, Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
//            }
//        })
//    }
//
//
//    private fun popFieldStock(jcData:allData) {
//        val table = findViewById<TableLayout>(R.id.tableLayout2)
//        table.removeAllViews()
//
//        val detailsMap = mutableMapOf(
//            "VEH NO." to jcData.REGNO,
//            "JOBCARDNO" to jcData.JOBCARDNO,
//            "VIN" to jcData.VIN,
//            "CHASSIS NO" to jcData.CHASSIS_NO,
//            "ENGINE NO" to jcData.ENGINENO,
//            "MODEL" to jcData.MODEL_DESC,
//            "VARIANT" to jcData.VARIANT_CODE,
//            "ERACCNO" to jcData.ERPACCTNO,
//            "CUST NAME" to jcData.CUSTNAME,
//            "CONTACT" to jcData.CONTACTNO,
//        )
//        regNo=jcData.REGNO
//        custName=jcData.CUSTNAME
//        chassisNo=jcData.CHASSIS_NO
//        vinNo=jcData.VIN
//        engineNo=jcData.ENGINENO
//        jobCardNo=jcData.JOBCARDNO
//        for ((label, value) in detailsMap) {
//            val row = LayoutInflater.from(this).inflate(R.layout.table_row_gate_pass, null) as TableRow
//            val labelTextView = row.findViewById<TextView>(R.id.label)
//            val valueTextView = row.findViewById<TextView>(R.id.value)
//
//            labelTextView.text = label
//            valueTextView.text = value
//
//            table.addView(row)
//        }
//
//        if (table.childCount > 0) {
//            val lastRow = table.getChildAt(table.childCount - 1) as TableRow
//            lastRow.findViewById<View>(R.id.labelDivider).visibility = View.GONE
//            lastRow.findViewById<View>(R.id.valueDivider).visibility = View.GONE
//        }
//    }
//
//    private fun popFieldMasters(jcData:allData) {
//        val table = findViewById<TableLayout>(R.id.tableLayout2)
//        table.removeAllViews()
//
//        val detailsMap = mutableMapOf(
//            "VEH NO." to jcData.INSTANCE_NUMBER,
//            "ACCOUNT NUMBER" to jcData.ACCOUNT_NUMBER,
//            "CUST NAME" to jcData.CUST_NAME,
//            "EMAIL ADDRESS" to jcData.EMAIL_ADDRESS,
//            "PRIMARY PHONE NUMBER" to jcData.PRIMARY_PHONE_NUMBER,
//            "REGISTRATION DATE" to jcData.REGISTRATION_DATE
//        )
//
//        regNo=jcData.INSTANCE_NUMBER
//        custName=jcData.CUST_NAME
//
//        for ((label, value) in detailsMap) {
//            val row = LayoutInflater.from(this).inflate(R.layout.table_row_gate_pass, null) as TableRow
//            val labelTextView = row.findViewById<TextView>(R.id.label)
//            val valueTextView = row.findViewById<TextView>(R.id.value)
//
//            labelTextView.text = label
//            valueTextView.text = value
//
//            table.addView(row)
//        }
//
//        if (table.childCount > 0) {
//            val lastRow = table.getChildAt(table.childCount - 1) as TableRow
//            lastRow.findViewById<View>(R.id.labelDivider).visibility = View.GONE
//            lastRow.findViewById<View>(R.id.valueDivider).visibility = View.GONE
//        }
//    }
//
//
//    private fun popFieldNew(jcData:allData) {
//        val table = findViewById<TableLayout>(R.id.tableLayout2)
//        table.removeAllViews()
////        val vehNo = newVehEditText.text.toString()
//
//
//        val detailsMap = mutableMapOf(
//            "VEH NO." to jcData.REGNO,
//            "MESSAGE" to "This is a new Vehicle!!"
//        )
//        regNo=jcData.REGNO
//
//        for ((label, value) in detailsMap) {
//            val row = LayoutInflater.from(this).inflate(R.layout.table_row_gate_pass, null) as TableRow
//            val labelTextView = row.findViewById<TextView>(R.id.label)
//            val valueTextView = row.findViewById<TextView>(R.id.value)
//
//            labelTextView.text = label
//            valueTextView.text = value
//
//            table.addView(row)
//        }
//
//        if (table.childCount > 0) {
//            val lastRow = table.getChildAt(table.childCount - 1) as TableRow
//            lastRow.findViewById<View>(R.id.labelDivider).visibility = View.GONE
//            lastRow.findViewById<View>(R.id.valueDivider).visibility = View.GONE
//        }
//    }
//
//
//    private fun populateFields6(jcData:newVehDetailsOut) {
//        val table = findViewById<TableLayout>(R.id.tableLayout2)
//        table.removeAllViews()
//
//        val detailsMap = mutableMapOf(
//            "VEH NO." to jcData.REG_NO,
//            "JOBCARDNO" to jcData.JOBCARDNO,
//            "VIN" to jcData.VIN,
//            "CHASSIS NO" to jcData.CHASSIS_NO,
//            "ENGINE NO" to jcData.ENGINE_NO,
//            "LOCATION" to jcData.LOCATION,
//            "OUT KM" to jcData.OUT_KM,
//            "OUT TIME" to jcData.OUT_TIME,
//            "DRIVER" to jcData.DRIVER_NAME
//        )
//        regNo=jcData.REG_NO
//        testDriveNo=jcData.TEST_DRIVE_NO
//        inKmNewVeh=jcData.OUT_KM
//        jobCardNo=jcData.JOBCARDNO
//
//        for ((label, value) in detailsMap) {
//            val row = LayoutInflater.from(this).inflate(R.layout.table_row_gate_pass, null) as TableRow
//            val labelTextView = row.findViewById<TextView>(R.id.label)
//            val valueTextView = row.findViewById<TextView>(R.id.value)
//
//            labelTextView.text = label
//            valueTextView.text = value
//
//            table.addView(row)
//        }
//
//        if (table.childCount > 0) {
//            val lastRow = table.getChildAt(table.childCount - 1) as TableRow
//            lastRow.findViewById<View>(R.id.labelDivider).visibility = View.GONE
//            lastRow.findViewById<View>(R.id.valueDivider).visibility = View.GONE
//        }
//    }
//
//
//    private fun resetFields(){
//        val tableLayout = findViewById<TableLayout>(R.id.tableLayout2)
//        tableLayout.removeAllViews()
//        kmTxt.visibility=View.GONE
//        currentKMSField.visibility=View.GONE
//        captureToKm.visibility=View.GONE
//        currentKMSField.setText("")
//        regNoDetails.visibility=View.GONE
//        driverTxt.visibility=View.GONE
//        driverNameField.visibility=View.GONE
//        driverNameField.setText("")
//        remarksTxt.visibility=View.GONE
//        remarksField.visibility=View.GONE
//        remarksField.setText("")
//        vehicleOutForTestDrive.visibility=View.GONE
//        vehicleInAfterTestDrive.visibility=View.GONE
//        newVehOutEditText.setText("")
//        newVehOutLL.visibility=View.GONE
//        newVehicleOutPremises.visibility=View.GONE
//        newVehLL.visibility=View.GONE
//        newVehEditText.setText("")
//        newVehicleInPremises.visibility=View.GONE
//        refreshButton.visibility=View.GONE
//        regNo=""
//        jobCardNo=""
//        chassisNo=""
//        engineNo=""
//        vinNo=""
//        testDriveNo=""
//        custName=""
//        outKm=""
//        inKmNewVeh=""
//    }
//
//    private fun workShopTestDriveVehHistory() {
//        val intent = Intent(this@WorkshopGatepassKmEnquiry, WorkshopTestDriveVehicleHistory::class.java)
//        intent.putExtra("login_name", login_name)
//        intent.putExtra("ouId", ouId)
//        intent.putExtra("locId", locId)
//        intent.putExtra("location_name",location_name)
//        intent.putExtra("deptName",deptName)
//        startActivity(intent)
//    }
//
//
//    data class newVehDetails(
//        val DEPT:String,
//        val ENGINENO:String,
//        val CHASSIS_NO:String,
//        val REGNO:String,
//        val CUSTNAME:String,
//        val VIN:String,
//        val VARIANT_CODE:String,
//        val CONTACTNO:String,
//        val MODEL_DESC:String,
//        val ERPACCTNO:String,
//        val INSTANCE_NUMBER :String,
//        val EMAIL_ADDRESS:String,
//        val ADDRESS:String,
//        val ACCOUNT_NUMBER:String,
//        val CUST_NAME:String,
//        val REGISTRATION_DATE:String,
//        val PRIMARY_PHONE_NUMBER:String,
//        val JOBCARDNO:String
//    )
//
//    data class newVehDetailsOut(
//        val LOCATION:String,
//        val REG_NO:String,
//        val VIN:String,
//        val CHASSIS_NO:String,
//        val DEPT:String,
//        val OUT_KM:String,
//        val DRIVER_NAME:String,
//        val OUT_TIME:String,
//        val TEST_DRIVE_NO:String,
//        val ENGINE_NO:String,
//        val JOBCARDNO:String,
//
//        )
//
//    data class allData(
//        val LOCATION:String,
//        val REG_NO:String,
//        val OUT_KM:String,
//        val DRIVER_NAME:String,
//        val OUT_TIME:String,
//        val TEST_DRIVE_NO:String,
//        val ENGINE_NO:String,
//        val DEPT:String,
//        val ENGINENO:String,
//        val CHASSIS_NO:String,
//        val REGNO:String,
//        val CUSTNAME:String,
//        val VIN:String,
//        val VARIANT_CODE:String,
//        val CONTACTNO:String,
//        val MODEL_DESC:String,
//        val ERPACCTNO:String,
//        val INSTANCE_NUMBER :String,
//        val EMAIL_ADDRESS:String,
//        val ADDRESS:String,
//        val ACCOUNT_NUMBER:String,
//        val CUST_NAME:String,
//        val REGISTRATION_DATE:String,
//        val PRIMARY_PHONE_NUMBER:String,
//        val JOBCARDNO:String
//    )
//
//}
////
////
////
