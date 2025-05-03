//package com.example.saivehicledelivery
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.app.Activity
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.net.Uri
//import android.os.Bundle
//import android.provider.MediaStore
//import android.util.Log
//import android.view.View
//import android.widget.AdapterView
//import android.widget.ArrayAdapter
//import android.widget.Button
//import android.widget.EditText
//import android.widget.ImageButton
//import android.widget.ImageView
//import android.widget.Spinner
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.core.content.FileProvider
//import com.example.apinew.ApiFile
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.launch
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.RequestBody.Companion.toRequestBody
//import org.json.JSONObject
//import java.io.File
//import java.text.SimpleDateFormat
//import java.util.Calendar
//import java.util.Locale
//import com.google.zxing.integration.android.IntentIntegrator
//import okhttp3.Call
//import okhttp3.Callback
//import okhttp3.MultipartBody
//import okhttp3.RequestBody
//import okhttp3.Response
//import org.json.JSONArray
//import java.io.IOException
//
//
//class VehicleDelivery : AppCompatActivity() {
//   private lateinit var invoiceNumberField:EditText
//   private lateinit var searchButton:Button
//   private lateinit var vehicleNumberTxtView:TextView
//    private lateinit var nameTxtView:TextView
//    private lateinit var transactionNoTxtView:TextView
//    private lateinit var transactionNoDateTxtView:TextView
//    private lateinit var contactNoTxtView:EditText
//    private lateinit var addressTxtView:TextView
//    private lateinit var amountRemainingTxtView:TextView
//    private lateinit var amountPaidTxtField:EditText
//    private lateinit var modelTextView:TextView
//   private lateinit var sendOTP:Button
//   private lateinit var otpInputField:EditText
//   private lateinit var verifyOTPButton:Button
//   private lateinit var upiInputTextField:EditText
//   private lateinit var detailsSubmit:Button
//   private lateinit var logoImageView:ImageView
//   private lateinit var username:TextView
//   private lateinit var locIdTxt:TextView
//    private lateinit var login_name: String
//    private lateinit var location_name:String
//    private lateinit var userName:String
//    private lateinit var userContact:String
//    private lateinit var location:String
//    private lateinit var deptName :String
//    private lateinit var logoutBtn:ImageView
//    private var ouId: Int = 0
//    private var locId:Int=0
//    private lateinit var imageView:ImageView
//    private lateinit var captureImage:ImageButton
//    private lateinit var deleteImage:ImageButton
//    private var photoUri: Uri? = null
//    private var photoFile: File? = null
//    private lateinit var imageText:TextView
//    private lateinit var upiText:TextView
//    private lateinit var invNumber:String
//    private lateinit var invoiceNumberTxtView:TextView
//    private lateinit var paymentIdSpinner:Spinner
//    private lateinit var paymentModeSpinner:Spinner
//    private lateinit var paymentType2:String
//    private lateinit var clearFields:Button
//    private lateinit var serviceExecutive:TextView
//    private lateinit var invoiceDataLL:View
//    private lateinit var amountRemaining:String
//    private lateinit var partyId:TextView
//    private lateinit var transactionDate:String
//    private lateinit var gatePassId:String
//    private lateinit var cityConverter:String
//
//
//
//    @SuppressLint("ClickableViewAccessibility")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_vehicle_delivery)
//
//        login_name = intent.getStringExtra("login_name") ?: ""
//        location = intent.getStringExtra("location") ?: ""
//        location_name = intent.getStringExtra("location_name") ?: ""
//        deptName = intent.getStringExtra("deptName") ?: ""
//        locId = intent.getIntExtra("locId", 0)
//        ouId = intent.getIntExtra("ouId", 0)
//        userContact=intent.getStringExtra("userContact") ?:""
//        userName=intent.getStringExtra("userName") ?:""
//
//
//        vehicleNumberTxtView=findViewById(R.id.vehicleNumberTxtView)
//        nameTxtView=findViewById(R.id.nameTxtView)
//        transactionNoTxtView=findViewById(R.id.transactionNoTxtView)
//        transactionNoDateTxtView=findViewById(R.id.transactionNoDateTxtView)
//        serviceExecutive=findViewById(R.id.serviceExecutive)
//        contactNoTxtView=findViewById(R.id.contactNoTxtView)
//        modelTextView=findViewById(R.id.modelTextView)
//        addressTxtView=findViewById(R.id.addressTxtView)
//        amountRemainingTxtView=findViewById(R.id.amountRemainingTxtView)
//        amountPaidTxtField=findViewById(R.id.amountPaidTxtField)
//        invoiceNumberTxtView=findViewById(R.id.invoiceNumberTxtView)
//        paymentIdSpinner=findViewById(R.id.paymentIdSpinner)
//        imageView=findViewById(R.id.imageView)
//        captureImage=findViewById(R.id.captureImage)
//        deleteImage=findViewById(R.id.deleteImage)
//        imageText=findViewById(R.id.imageText)
//        upiText=findViewById(R.id.upiText)
//        clearFields=findViewById(R.id.clearFields)
//        partyId=findViewById(R.id.partyId)
//
//        invoiceDataLL=findViewById(R.id.invoiceDataLL)
//        invoiceDataLL.visibility=View.GONE
//
//
//        invoiceNumberField=findViewById(R.id.invoiceNumberField)
//        searchButton=findViewById(R.id.searchButton)
//        paymentModeSpinner=findViewById(R.id.paymentModeSpinner)
//        sendOTP=findViewById(R.id.sendOTP)
//        otpInputField=findViewById(R.id.otpInputField)
//        verifyOTPButton=findViewById(R.id.verifyOTPButton)
//        upiInputTextField=findViewById(R.id.upiInputTextField)
//        detailsSubmit=findViewById(R.id.detailsSubmit)
//        logoImageView=findViewById(R.id.logoImageView)
//        logoutBtn=findViewById(R.id.logoutBtn)
//        username=findViewById(R.id.username)
//        locIdTxt=findViewById(R.id.locIdTxt)
//        locIdTxt.text = "$location_name"
//        username.text = "$login_name"
//
//        contactNoTxtView.isEnabled=false
//
//        detailsSubmit.setOnClickListener {
//            saveInvoiceDetails()
//        }
//
//        logoutBtn.setOnClickListener{
//            logout()
//        }
//
//        searchButton.setOnClickListener {
//            fetchInvData()
//        }
//
//        sendOTP.isEnabled=false
//        verifyOTPButton.isEnabled=false
//        paymentIdSpinner.isEnabled=false
//        paymentModeSpinner.isEnabled=false
//
//
//
//        captureImage.setOnClickListener {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
//            } else {
//                openCamera()
//            }
//        }
//
//        deleteImage.setOnClickListener {
//            imageView.setImageURI(null)
//            photoUri = null
//            photoFile = null
//        }
//
//        sendOTP.setOnClickListener {
//            generateOTP()
//        }
//
//        verifyOTPButton.setOnClickListener {
//            validateOTP()
//        }
//
//        clearFields.setOnClickListener {
//            resetFields()
//        }
//
//    }
//
//    private fun fetchPaymentModes() {
//        val client = OkHttpClient()
//        val request = Request.Builder()
//            .url("${ApiFile.APP_URL}/vehDelvTrans/getByPaymentType?paymentType=CASH_RECEIPT_TYPE")
//            .build()
//
//        client.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                e.printStackTrace()
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                if (response.isSuccessful) {
//                    val responseBody = response.body?.string()
//                    if (!responseBody.isNullOrEmpty()) {
//                        val jsonResponse = JSONObject(responseBody)
//                        val paymentModes = parsePaymentModes(jsonResponse)
//
//                        runOnUiThread {
//                            setupPaymentModeSpinner(paymentModes)
//                            Log.d("URL->1",request.toString())
//                            Log.d("URL->2",client.toString())
//                        }
//                    }
//                }
//            }
//        })
//    }
//
//    private fun parsePaymentModes(jsonResponse: JSONObject): List<String> {
//        val paymentList = mutableListOf("Select Payment Mode")
//        val objArray: JSONArray = jsonResponse.getJSONArray("obj")
//
//        for (i in 0 until objArray.length()) {
//            val item = objArray.getJSONObject(i)
//            val description = item.getString("DESCRIPTION")
//            val paymentType = item.getString("PAYMENT_TYPE")
//            paymentList.add("$description-$paymentType")
//            paymentType2=paymentType
////            paymentList.add(description)
//        }
//
//        return paymentList
//    }
//
//    private fun setupPaymentModeSpinner(paymentModes: List<String>) {
//        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, paymentModes) {
//            override fun isEnabled(position: Int): Boolean {
//                return position != 0
//            }
//
//            override fun getDropDownView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
//                val view = super.getDropDownView(position, convertView, parent)
//                val textView = view as TextView
//                textView.setTextColor(
//                    if (position == 0) android.graphics.Color.GRAY
//                    else android.graphics.Color.BLACK
//                )
//                return view
//            }
//        }
//
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        paymentModeSpinner.adapter = adapter
//
//        paymentModeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                if (position != 0) {
//                    val selectedItem = paymentModeSpinner.selectedItem.toString()
//                    val selectedPaymentType = selectedItem.split("-")[1]
//                    paymentType2 = selectedPaymentType
//                    fetchPaymentIdSpinnerData(selectedPaymentType)
//
//                    val selectedItem2= paymentModeSpinner.selectedItem?.toString()?.trim()
//                    if (selectedItem2.equals("Cash-CASH", ignoreCase = true)) {
//                        upiText.visibility = View.GONE
//                        upiInputTextField.visibility = View.GONE
//                    } else {
//                        upiText.visibility = View.VISIBLE
//                        upiInputTextField.visibility = View.VISIBLE
//                    }
//
//                }
//                updateSendOTPButtonState()
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                updateSendOTPButtonState()
//            }
//        }
//    }
//
//    private fun fetchPaymentIdSpinnerData(description: String) {
//        val location = location
//        val department = deptName
//        val url = "${ApiFile.APP_URL}/vehDelvTrans/getByReceiptMethod?paymentType=$description&location=$location&department=$department&ouId=$ouId"
//
//        val client = OkHttpClient()
//        val request = Request.Builder().url(url).build()
//        Log.d("url-fetchPaymentIdSpinnerData",url)
//
//
//        client.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                e.printStackTrace()
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                if (response.isSuccessful) {
//                    val responseBody = response.body?.string()
//                    if (!responseBody.isNullOrEmpty()) {
//                        val jsonResponse = JSONObject(responseBody)
//                        val paymentIds = parsePaymentIdData(jsonResponse)
//
//                        runOnUiThread {
//                            populatePaymentIdSpinner(paymentIds)
//                        }
//                    }
//                }
//            }
//        })
//    }
//
//    private fun parsePaymentIdData(jsonResponse: JSONObject): List<String> {
//        val paymentIdList = mutableListOf("Select Payment ID")
//        val objArray: JSONArray = jsonResponse.getJSONArray("obj")
//
//        for (i in 0 until objArray.length()) {
//            val item = objArray.getJSONObject(i)
//            val name = item.getString("NAME")
//            paymentIdList.add(name)
//        }
//
//        return paymentIdList
//    }
//
//    private fun populatePaymentIdSpinner(paymentIds: List<String>) {
//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, paymentIds)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        paymentIdSpinner.adapter = adapter
//
//
//        paymentIdSpinner.setSelection(0)
//
//        paymentIdSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                updateSendOTPButtonState()
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                updateSendOTPButtonState()
//            }
//        }
//    }
//
//
//    private fun updateSendOTPButtonState() {
//        val isPaymentModeSelected = paymentModeSpinner.selectedItemPosition != 0
//        val isPaymentIdSelected = paymentIdSpinner.selectedItemPosition != 0
//        sendOTP.isEnabled = isPaymentModeSelected && isPaymentIdSelected
//
//        val buttonColor = if (sendOTP.isEnabled) {
//            ContextCompat.getColor(this, R.color.teal_700)
//        } else {
//            ContextCompat.getColor(this, R.color.secondary_text_color)
//        }
//        sendOTP.setBackgroundColor(buttonColor)
//    }
//
//
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
//        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
//            imageView.setImageURI(photoUri)
////            btnDelete.visibility = Button.VISIBLE
////            btnUpload.visibility = Button.VISIBLE
//        } else {
//            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
//            if (result != null) {
//                if (result.contents == null) {
//                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
//                } else {
//                    Toast.makeText(this, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
//                }
//            } else {
//                super.onActivityResult(requestCode, resultCode, data)
//            }
//        }
//    }
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
//    private fun fetchInvData() {
//        val invNo = invoiceNumberField.text.toString()
//        val client = OkHttpClient()
//        val url = ApiFile.APP_URL + "/vehDelvTrans/getByGatePassId?attribute1=$invNo&location=$location"
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
//
//                jsonData?.let {
//                    val jsonObject = JSONObject(it)
//                    Log.d("Response Data", jsonObject.toString())
//
//                    val code = jsonObject.getInt("code")
//                    val message = jsonObject.getString("message")
//
//                    runOnUiThread {
//                        when (code) {
//                            400 -> {
//                                Toast.makeText(
//                                    this@VehicleDelivery,
//                                    message,
//                                    Toast.LENGTH_LONG
//                                ).show()
//                            }
//                            200 -> {
//                                val objArray = jsonObject.optJSONArray("obj")
//                                if (objArray != null && objArray.length() > 0) {
//                                    val stockItem = objArray.getJSONObject(0)
//
////                                    val partyId = if (stockItem.has("PARTY_ID")) {
////                                        stockItem.optInt("PARTY_ID", 0)
////                                    } else {
////                                        stockItem.optInt("PARTYID", 0)
////                                    }
//
//
//                                    val invData = InvoiceNoData(
//                                        VEHICLE_NO = stockItem.optString("VEHICLE_NO"),
//                                        TRANS_REF_NUM = stockItem.optString("TRANS_REF_NUM"),
//                                        CUSTOMER_ADDRESS = stockItem.optString("CUSTOMER_ADDRESS"),
//                                        CUSTOMER_NAME = stockItem.optString("CUSTOMER_NAME"),
//                                        DATE_OF_DELIVERY = stockItem.optString("DATE_OF_DELIVERY"),
//                                        CONTACT_NO = stockItem.optString("CONTACT_NO"),
//                                        AMOUNT_DUE_REMAINING = stockItem.getInt("AMOUNT_DUE_REMAINING"),
//                                        INVOICE_NO = stockItem.optString("INVOICE_NO"),
//                                        EXECUTIVE = stockItem.optString("EXECUTIVE"),
//                                        GATE_PASS_ID = stockItem.optString("GATE_PASS_ID"),
//                                        SERVICE_LOCATION = stockItem.optString("SERVICE_LOCATION"),
//                                        MODEL = stockItem.optString("MODEL"),
//                                        PARTY_ID = stockItem.getInt("PARTY_ID")
//                                    )
//
//                                    populateFields(invData)
//                                    Toast.makeText(
//                                        this@VehicleDelivery,
//                                        "Details found Successfully for Gate Pass No: $invNo",
//                                        Toast.LENGTH_LONG
//                                    ).show()
//
//                                    fetchPaymentModes()
//
//                                    verifyOTPButton.setBackgroundColor(
//                                        ContextCompat.getColor(this@VehicleDelivery, R.color.teal_700)
//                                    )
//                                    verifyOTPButton.isEnabled = true
//                                    paymentIdSpinner.isEnabled = true
//                                    paymentModeSpinner.isEnabled = true
//                                } else {
//                                    Toast.makeText(
//                                        this@VehicleDelivery,
//                                        "No details found for Gate Pass No: $invNo",
//                                        Toast.LENGTH_LONG
//                                    ).show()
//                                }
//                            }
//                            else -> {
//                                Toast.makeText(
//                                    this@VehicleDelivery,
//                                    "Unexpected response: $message",
//                                    Toast.LENGTH_LONG
//                                ).show()
//                            }
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                runOnUiThread {
//                    Toast.makeText(
//                        this@VehicleDelivery,
//                        "Failed to fetch details for Gate Pass No: $invNo. Error: ${e.message}",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//            }
//        }
//    }
//
//
//
//    private fun saveInvoiceDetails() {
//        val invNo=invNumber
//        val otp=otpInputField.text.toString()
//        val payTransNumber=upiInputTextField.text.toString()
//        val custContactNo=contactNoTxtView.text.toString()
//        val remainingAmount=amountRemainingTxtView.text.toString().split(": ")[1]
//        val remainAmt=remainingAmount.toFloat()
//        val amtPaidByUser=amountPaidTxtField.text.toString()
//        val amtToBePaid=amtPaidByUser.toFloat()
//        if (amtToBePaid>remainAmt){
//            Toast.makeText(this@VehicleDelivery,
//                "Entered amount is greater than remaining amount.",
//                Toast.LENGTH_LONG
//            ).show()
//            return
//        }
//        val otp2=otp.toInt()
//        val client = OkHttpClient()
//        val url = ApiFile.APP_URL + "/vehDelvTrans/delvPaymentComplete"
//        val jsonObject = JSONObject()
//
//        val currentDateTime = Calendar.getInstance()
//        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
//        val formattedDate = formatter.format(currentDateTime.time)
//        Log.d("formattedDate", formattedDate)
//
//        currentDateTime.add(Calendar.DAY_OF_MONTH, 1)
//        val formatterForEndDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
//
//        jsonObject.put("vehicleNo", vehicleNumberTxtView.text.toString().split(": ")[1])
//        jsonObject.put("custName", nameTxtView.text.toString().split(": ")[1])
//        jsonObject.put("custAddress", addressTxtView.text.toString().split(": ")[1])
//        jsonObject.put("custContactNo",custContactNo)
//        jsonObject.put("transactionNo", transactionNoTxtView.text.toString().split(": ")[1])
//        jsonObject.put("transactionDate",transactionDate)
//        jsonObject.put("amountDueRemaining", remainAmt)
//        jsonObject.put("invoiceNo", invNo)
//        jsonObject.put("paymentType",paymentType2)
//        jsonObject.put("receiptMethodId",paymentIdSpinner.selectedItem.toString())
//        jsonObject.put("otp",otp2)
//        jsonObject.put("paymentImage","-")
//        jsonObject.put("paymentTransactionNo",payTransNumber)
//        jsonObject.put("amountPaid",amtToBePaid)
//        jsonObject.put("ouId",ouId)
//        jsonObject.put("department",deptName)
//        jsonObject.put("driverName","$userName-$login_name")
//        jsonObject.put("driverLocationName",location_name)
//        jsonObject.put("partyId",partyId.text.toString().split(": ")[1])
//        jsonObject.put("attribute1",gatePassId)
//        jsonObject.put("driverContactNo",userContact)
//        jsonObject.put("attribute2","")
//        jsonObject.put("attribute3","")
//        jsonObject.put("attribute4","")
//        jsonObject.put("attribute5","")
//        jsonObject.put("driverLocId",locId)
//
//
//        Log.d("jsonObject", jsonObject.toString())
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
//                            message.contains("Payment Already Done For This Invoice", ignoreCase = true) -> {
//                                Toast.makeText(
//                                    this@VehicleDelivery,
//                                    "Payment Already Done For This Invoice",
//                                    Toast.LENGTH_LONG
//                                ).show()
//                                resetFields()
//                            }
//                            responseCode == 200 -> {
//                                Toast.makeText(
//                                    this@VehicleDelivery,
//                                    "Data saved successfully",
//                                    Toast.LENGTH_LONG
//                                ).show()
//                                paymentSuccessMessage()
//                                resetFields()
//                            }
//                            else -> {
//                                Toast.makeText(
//                                    this@VehicleDelivery,
//                                    "Failed to save data. Error code: $responseCode",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                    } else {
//                        Toast.makeText(
//                            this@VehicleDelivery,
//                            "Data not available",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Log.e("SaveData", "Error: ${e.message}")
//                runOnUiThread {
//                    Toast.makeText(
//                        this@VehicleDelivery,
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
//    private fun paymentSuccessMessage() {
//        val invNo=invNumber
//        val otp=otpInputField.text.toString()
//        val payTransNumber=upiInputTextField.text.toString()
//        val custContactNo=contactNoTxtView.text.toString()
//        val remainingAmount=amountRemainingTxtView.text.toString().split(": ")[1]
//        val remainAmt=remainingAmount.toInt()
//        val amtPaidByUser=amountPaidTxtField.text.toString()
//        val amtToBePaid=amtPaidByUser.toInt()
//        if(ouId==104){
//            cityConverter="Mumbai"
//        }
//        if(ouId==81){
//            cityConverter="Pune"
//        }
//        if(ouId==105){
//            cityConverter="Kolhapur"
//        }
//        if(ouId==106){
//            cityConverter="Goa"
//        }
//        if(ouId==107){
//            cityConverter="Cochin"
//        }
//        if(ouId==108){
//            cityConverter="Hyderabad"
//        }
//        val otp2=otp.toInt()
//        val client = OkHttpClient()
//        val url = ApiFile.APP_URL + "/vehDelvTrans/sendPaymentSuccessSms"
//        val jsonObject = JSONObject()
//
//
//        val currentDateTime = Calendar.getInstance()
//        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
//        val formattedDate = formatter.format(currentDateTime.time)
//        Log.d("formattedDate", formattedDate)
//
//        currentDateTime.add(Calendar.DAY_OF_MONTH, 1)
//        val formatterForEndDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
//
//        jsonObject.put("trxNumber",gatePassId)
//        jsonObject.put("instanceNumber", vehicleNumberTxtView.text.toString().split(": ")[1])
//        jsonObject.put("partyName", nameTxtView.text.toString().split(": ")[1])
//        jsonObject.put("accountType",paymentIdSpinner.selectedItem.toString())
//        jsonObject.put("mobileNo",custContactNo)
//        jsonObject.put("orgId",ouId)
//        jsonObject.put("amount",amtToBePaid)
//        jsonObject.put("method",paymentType2)
//        jsonObject.put("city",cityConverter)
//
//        Log.d("jsonObject", jsonObject.toString())
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
//                            message.contains("Payment Already Done For This Invoice", ignoreCase = true) -> {
//                                Toast.makeText(
//                                    this@VehicleDelivery,
//                                    "Payment Already Done For This Invoice",
//                                    Toast.LENGTH_LONG
//                                ).show()
//                                resetFields()
//                            }
//                            responseCode == 200 -> {
//                                Toast.makeText(
//                                    this@VehicleDelivery,
//                                    "Data saved successfully",
//                                    Toast.LENGTH_LONG
//                                ).show()
//                                resetFields()
//                            }
//                            else -> {
//                                Toast.makeText(
//                                    this@VehicleDelivery,
//                                    "Failed to save data. Error code: $responseCode",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                    } else {
//                        Toast.makeText(
//                            this@VehicleDelivery,
//                            "No response from server",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Log.e("SaveData", "Error: ${e.message}")
//                runOnUiThread {
//                    Toast.makeText(
//                        this@VehicleDelivery,
//                        "Error saving data: ${e.message}",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//    }
//
//
//    private fun generateOTP() {
//        val invNo = invNumber
//        val contactNo=contactNoTxtView.text.toString()
//
//        if (invNo.isEmpty()) {
//            Toast.makeText(this@VehicleDelivery, "Invoice Number cannot be empty", Toast.LENGTH_SHORT).show()
//            return
//        }
//        val client = OkHttpClient()
//        val url = "${ApiFile.APP_URL}/vehDelvOtp/generateDelvOtp?invoiceNo=$invNo&mobileNo=$contactNo"
//
//        Log.d("generateOTP", "Request URL: $url")
//
//        val request = Request.Builder()
//            .url(url)
//            .post(RequestBody.create(null, byteArrayOf()))
//            .build()
//
//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                val response = client.newCall(request).execute()
//                val responseCode = response.code
//                val responseBody = response.body?.string()
//
//                Log.d("generateOTP", "Response Code: $responseCode")
//                Log.d("generateOTP", "Response Body: $responseBody")
//
//                runOnUiThread {
//                    if (responseBody != null) {
//                        try {
//                            val jsonResponse = JSONObject(responseBody)
//                            val message = jsonResponse.optString("message", "")
//
//                            when {
//                                message.contains("Payment Already Done For This Invoice", ignoreCase = true) -> {
//                                    Toast.makeText(
//                                        this@VehicleDelivery,
//                                        "Payment Already Done For This Invoice",
//                                        Toast.LENGTH_LONG
//                                    ).show()
//                                }
//                                responseCode == 200 -> {
//                                    val otpCode = jsonResponse.optString("data", "")
//                                    Toast.makeText(
//                                        this@VehicleDelivery,
//                                        "OTP Generated and sent successfully.\nOTP will be valid for only 2 minutes only.",
//                                        Toast.LENGTH_LONG
//                                    ).show()
//                                }
//                                else -> {
//                                    Toast.makeText(
//                                        this@VehicleDelivery,
//                                        "Failed to generate OTP. Error code: $responseCode",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                }
//                            }
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                            Toast.makeText(
//                                this@VehicleDelivery,
//                                "Error processing the response: ${e.message}",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    } else {
//                        Toast.makeText(
//                            this@VehicleDelivery,
//                            "No response from server",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Log.e("generateOTP", "Error: ${e.message}")
//                runOnUiThread {
//                    Toast.makeText(
//                        this@VehicleDelivery,
//                        "Error generating OTP: ${e.message}",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//    }
//
//
//    private fun validateOTP() {
//        val otp = otpInputField.text.toString()
//        val inv=invNumber
//
//        if (otp.isEmpty()) {
//            Toast.makeText(this@VehicleDelivery, "OTP cannot be empty", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val client = OkHttpClient()
//        val url = "${ApiFile.APP_URL}/vehDelvOtp/validateDelvOtp?invoiceNo=$inv&otp=$otp&status=SEND"
//
//        Log.d("generateOTP", "Request URL: $url")
//
//        val request = Request.Builder()
//            .url(url)
//            .post(RequestBody.create(null, byteArrayOf()))
//            .build()
//
//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                val response = client.newCall(request).execute()
//                val responseCode = response.code
//                val responseBody = response.body?.string()
//
//                Log.d("generateOTP", "Response Code: $responseCode")
//                Log.d("generateOTP", "Response Body: $responseBody")
//
//                runOnUiThread {
//                    if (responseBody != null) {
//                        try {
//                            val jsonResponse = JSONObject(responseBody)
//                            val message = jsonResponse.optString("message", "")
//                            val code = jsonResponse.optInt("code", -1) // Extracting the code field
//
//                            when {
//                                code == 400 -> {
//                                    Toast.makeText(this@VehicleDelivery, "OTP IS INVALID OR EXPIRED" ,Toast.LENGTH_LONG).show()
//                                }
//                                code == 200 -> {
//                                    Toast.makeText(
//                                        this@VehicleDelivery,
//                                        "Validation successful",
//                                        Toast.LENGTH_LONG
//                                    ).show()
//                                    detailsSubmit.visibility=View.VISIBLE
//                                }
//                                else -> {
//                                    Toast.makeText(
//                                        this@VehicleDelivery,
//                                        "Unexpected response: $message",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                }
//                            }
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                            Toast.makeText(
//                                this@VehicleDelivery,
//                                "Error processing the response: ${e.message}",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    } else {
//                        Toast.makeText(
//                            this@VehicleDelivery,
//                            "No response from server",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Log.e("generateOTP", "Error: ${e.message}")
//                runOnUiThread {
//                    Toast.makeText(
//                        this@VehicleDelivery,
//                        "Error generating OTP: ${e.message}",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//    }
//
//
//    private fun populateFields(InvoiceNoData:InvoiceNoData) {
//        invoiceNumberTxtView.text = "Invoice No.: ${InvoiceNoData.INVOICE_NO}"
//        vehicleNumberTxtView.text = "Vehicle No.: ${InvoiceNoData.VEHICLE_NO}"
//        addressTxtView.text = "Address: ${InvoiceNoData.CUSTOMER_ADDRESS}"
//        nameTxtView.text = "Name: ${InvoiceNoData.CUSTOMER_NAME}"
//        contactNoTxtView.setText(InvoiceNoData.CONTACT_NO)
//        transactionNoTxtView.text = "Transaction Ref No.: ${InvoiceNoData.TRANS_REF_NUM}"
//        transactionNoDateTxtView.text = "Delivery Date.: ${formatDateTime(InvoiceNoData.DATE_OF_DELIVERY)}"
//        transactionDate=InvoiceNoData.DATE_OF_DELIVERY
//        amountRemainingTxtView.text="Balance Amount: ${InvoiceNoData.AMOUNT_DUE_REMAINING}"
//        serviceExecutive.text="Service Executive: ${InvoiceNoData.EXECUTIVE}"
//        modelTextView.text="Model: ${InvoiceNoData.MODEL}"
//        partyId.text="Customer Id: ${InvoiceNoData.PARTY_ID}"
//        val amountPaid=InvoiceNoData.AMOUNT_DUE_REMAINING
//        if(amountPaid!=null) {
//            amountPaidTxtField.setText("$amountPaid")
//        }
//        invNumber=InvoiceNoData.INVOICE_NO
//        vehicleNumberTxtView.visibility=View.VISIBLE
//        modelTextView.visibility=View.VISIBLE
//        addressTxtView.visibility=View.VISIBLE
//        partyId.visibility=View.VISIBLE
//        nameTxtView.visibility=View.VISIBLE
//        contactNoTxtView.visibility=View.VISIBLE
//        transactionNoTxtView.visibility=View.VISIBLE
//        transactionNoDateTxtView.visibility=View.VISIBLE
//        invoiceNumberTxtView.visibility=View.VISIBLE
//        amountRemainingTxtView.visibility=View.VISIBLE
//        serviceExecutive.visibility=View.VISIBLE
//        amountRemaining= InvoiceNoData.AMOUNT_DUE_REMAINING.toString()
//
//        gatePassId=InvoiceNoData.GATE_PASS_ID
//
//        val pendingAmount=amountRemaining.toFloat()
////        val pendingAmount=1000
//
//        Log.d("pendingAmount-->",pendingAmount.toString())
//        if(pendingAmount<=0){
//            invoiceDataLL.visibility=View.GONE
//        } else {
//            invoiceDataLL.visibility=View.VISIBLE
//            upiInputTextField.visibility=View.GONE
//            upiText.visibility=View.GONE
//        }
//    }
//
//    private fun resetFields(){
//        invoiceNumberField.setText("")
//        vehicleNumberTxtView.text=""
//        vehicleNumberTxtView.visibility=View.GONE
//        addressTxtView.visibility=View.GONE
//        nameTxtView.visibility=View.GONE
//        contactNoTxtView.visibility=View.GONE
//        transactionNoTxtView.visibility=View.GONE
//        transactionNoDateTxtView.visibility=View.GONE
//        amountRemainingTxtView.visibility=View.GONE
//        invoiceNumberTxtView.visibility=View.GONE
//        serviceExecutive.visibility=View.GONE
//        modelTextView.visibility=View.GONE
//        serviceExecutive.text=""
//        partyId.visibility=View.GONE
//        partyId.text=""
//        invoiceNumberTxtView.text=""
//        modelTextView.text=""
//        addressTxtView.text=""
//        nameTxtView.text=""
//        contactNoTxtView.setText("")
//        transactionNoTxtView.text=""
//        transactionNoDateTxtView.text=""
//        amountRemainingTxtView.text=""
//        otpInputField.setText("")
//        amountPaidTxtField.setText("")
//        upiInputTextField.setText("")
//        detailsSubmit.visibility=View.GONE
//        sendOTP.isEnabled=false
//        sendOTP.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
//        verifyOTPButton.isEnabled=false
//        verifyOTPButton.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
//        paymentIdSpinner.isEnabled=false
//        paymentModeSpinner.isEnabled=false
//        resetSpinner()
//        resetSpinner2()
//        invoiceDataLL.visibility=View.GONE
//    }
//
//    fun resetSpinner() {
//        val adapter = paymentModeSpinner.adapter as ArrayAdapter<String>
//        adapter.clear()
//        adapter.addAll(emptyList())
//        adapter.notifyDataSetChanged()
//        paymentModeSpinner.setSelection(0)
//    }
//
//    fun resetSpinner2() {
//        val adapter2 = paymentIdSpinner.adapter as? ArrayAdapter<String>
//        if (adapter2 != null) {
//            adapter2.clear()
//            adapter2.addAll(emptyList())
//            adapter2.notifyDataSetChanged()
//        }
//        paymentIdSpinner.setSelection(0)
//    }
//
//
//
//    private fun logout(){
//        val intent=Intent(this,MainActivity::class.java)
//        startActivity(intent)
//        finish()
//    }
//
//    data class InvoiceNoData(
//        val VEHICLE_NO:String,
//        val TRANS_REF_NUM:String,
//        val CUSTOMER_ADDRESS:String,
//        val CUSTOMER_NAME:String,
//        val DATE_OF_DELIVERY:String,
//        val CONTACT_NO:String,
//        val AMOUNT_DUE_REMAINING: Int,
//        val INVOICE_NO:String,
//        val EXECUTIVE:String,
//        val GATE_PASS_ID:String,
//        val SERVICE_LOCATION:String,
//        val MODEL:String,
//        val PARTY_ID:Int
//    )
//
//}








////////////////////////////////////////////////////////////////////////////////////////////////////


package com.example.saivehicledelivery

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.apinew.ApiFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.google.zxing.integration.android.IntentIntegrator
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException


class VehicleDelivery : AppCompatActivity() {
    private lateinit var invoiceNumberField:EditText
    private lateinit var searchButton:Button
    private lateinit var vehicleNumberTxtView:TextView
    private lateinit var nameTxtView:TextView
    private lateinit var transactionNoTxtView:TextView
    private lateinit var transactionNoDateTxtView:TextView
    private lateinit var contactNoTxtView:EditText
    private lateinit var addressTxtView:TextView
    private lateinit var amountRemainingTxtView:TextView
    private lateinit var amountPaidTxtField:EditText
    private lateinit var modelTextView:TextView
    private lateinit var sendOTP:Button
    private lateinit var otpInputField:EditText
    private lateinit var verifyOTPButton:Button
    private lateinit var upiInputTextField:EditText
    private lateinit var detailsSubmit:Button
    private lateinit var logoImageView:ImageView
    private lateinit var username:TextView
    private lateinit var locIdTxt:TextView
    private lateinit var login_name: String
    private lateinit var location_name:String
    private lateinit var userName:String
    private lateinit var userContact:String
    private lateinit var location:String
    private lateinit var deptName :String
    private lateinit var logoutBtn:ImageView
    private var ouId: Int = 0
    private var locId:Int=0
    private lateinit var imageView:ImageView
    private lateinit var captureImage:ImageButton
    private lateinit var deleteImage:ImageButton
    private var photoUri: Uri? = null
    private var photoFile: File? = null
    private lateinit var imageText:TextView
    private lateinit var upiText:TextView
    private lateinit var invNumber:String
    private lateinit var invoiceNumberTxtView:TextView
    private lateinit var paymentIdSpinner:Spinner
    private lateinit var paymentModeSpinner:Spinner
    private lateinit var paymentType2:String
    private lateinit var clearFields:Button
    private lateinit var serviceExecutive:TextView
    private lateinit var invoiceDataLL:View
    private lateinit var amountRemaining:String
    private lateinit var partyId:TextView
    private lateinit var transactionDate:String
    private lateinit var gatePassId:String
    private lateinit var cityConverter:String



    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_delivery)

        login_name = intent.getStringExtra("login_name") ?: ""
        location = intent.getStringExtra("location") ?: ""
        location_name = intent.getStringExtra("location_name") ?: ""
        deptName = intent.getStringExtra("deptName") ?: ""
        locId = intent.getIntExtra("locId", 0)
        ouId = intent.getIntExtra("ouId", 0)
        userContact=intent.getStringExtra("userContact") ?:""
        userName=intent.getStringExtra("userName") ?:""


        vehicleNumberTxtView=findViewById(R.id.vehicleNumberTxtView)
        nameTxtView=findViewById(R.id.nameTxtView)
        transactionNoTxtView=findViewById(R.id.transactionNoTxtView)
        transactionNoDateTxtView=findViewById(R.id.transactionNoDateTxtView)
        serviceExecutive=findViewById(R.id.serviceExecutive)
        contactNoTxtView=findViewById(R.id.contactNoTxtView)
        modelTextView=findViewById(R.id.modelTextView)
        addressTxtView=findViewById(R.id.addressTxtView)
        amountRemainingTxtView=findViewById(R.id.amountRemainingTxtView)
        amountPaidTxtField=findViewById(R.id.amountPaidTxtField)
        invoiceNumberTxtView=findViewById(R.id.invoiceNumberTxtView)
        paymentIdSpinner=findViewById(R.id.paymentIdSpinner)
        imageView=findViewById(R.id.imageView)
        captureImage=findViewById(R.id.captureImage)
        deleteImage=findViewById(R.id.deleteImage)
        imageText=findViewById(R.id.imageText)
        upiText=findViewById(R.id.upiText)
        clearFields=findViewById(R.id.clearFields)
        partyId=findViewById(R.id.partyId)

        invoiceDataLL=findViewById(R.id.invoiceDataLL)
        invoiceDataLL.visibility=View.GONE


        invoiceNumberField=findViewById(R.id.invoiceNumberField)
        searchButton=findViewById(R.id.searchButton)
        paymentModeSpinner=findViewById(R.id.paymentModeSpinner)
        sendOTP=findViewById(R.id.sendOTP)
        otpInputField=findViewById(R.id.otpInputField)
        verifyOTPButton=findViewById(R.id.verifyOTPButton)
        upiInputTextField=findViewById(R.id.upiInputTextField)
        detailsSubmit=findViewById(R.id.detailsSubmit)
        logoImageView=findViewById(R.id.logoImageView)
        logoutBtn=findViewById(R.id.logoutBtn)
        username=findViewById(R.id.username)
        locIdTxt=findViewById(R.id.locIdTxt)
        locIdTxt.text = "$location_name"
        username.text = "$login_name"

//        contactNoTxtView.isEnabled=false

        detailsSubmit.setOnClickListener {
            saveInvoiceDetails()
        }

        logoutBtn.setOnClickListener{
            logout()
        }

        searchButton.setOnClickListener {
            fetchInvData()
        }

        sendOTP.isEnabled=false
        verifyOTPButton.isEnabled=false
        paymentIdSpinner.isEnabled=false
        paymentModeSpinner.isEnabled=false



        captureImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
            } else {
                openCamera()
            }
        }

        deleteImage.setOnClickListener {
            imageView.setImageURI(null)
            photoUri = null
            photoFile = null
        }

        sendOTP.setOnClickListener {
            generateOTP()
        }

        verifyOTPButton.setOnClickListener {
            validateOTP()
        }

        clearFields.setOnClickListener {
            resetFields()
        }

    }

    private fun fetchPaymentModes() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.APP_URL}/vehDelvTrans/getByPaymentType?paymentType=CASH_RECEIPT_TYPE")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val jsonResponse = JSONObject(responseBody)
                        val paymentModes = parsePaymentModes(jsonResponse)

                        runOnUiThread {
                            setupPaymentModeSpinner(paymentModes)
                            Log.d("URL->1",request.toString())
                            Log.d("URL->2",client.toString())
                        }
                    }
                }
            }
        })
    }

    private fun parsePaymentModes(jsonResponse: JSONObject): List<String> {
        val paymentList = mutableListOf("Select Payment Mode")
        val objArray: JSONArray = jsonResponse.getJSONArray("obj")

        for (i in 0 until objArray.length()) {
            val item = objArray.getJSONObject(i)
            val description = item.getString("DESCRIPTION")
            val paymentType = item.getString("PAYMENT_TYPE")
            paymentList.add("$description-$paymentType")
            paymentType2=paymentType
//            paymentList.add(description)
        }

        return paymentList
    }

    private fun setupPaymentModeSpinner(paymentModes: List<String>) {
        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, paymentModes) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            override fun getDropDownView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view as TextView
                textView.setTextColor(
                    if (position == 0) android.graphics.Color.GRAY
                    else android.graphics.Color.BLACK
                )
                return view
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        paymentModeSpinner.adapter = adapter

        paymentModeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position != 0) {
                    val selectedItem = paymentModeSpinner.selectedItem.toString()
                    val selectedPaymentType = selectedItem.split("-")[1]
                    paymentType2 = selectedPaymentType
                    fetchPaymentIdSpinnerData(selectedPaymentType)

                    val selectedItem2= paymentModeSpinner.selectedItem?.toString()?.trim()
                    if (selectedItem2.equals("Cash-CASH", ignoreCase = true)||selectedItem2.equals("Credit Card-CREDIT_CARD", ignoreCase = true)) {
                        upiText.visibility = View.GONE
                        upiInputTextField.visibility = View.GONE
                    } else {
                        upiText.visibility = View.VISIBLE
                        upiInputTextField.visibility = View.VISIBLE
                    }

                }
                updateSendOTPButtonState()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                updateSendOTPButtonState()
            }
        }
    }

    private fun fetchPaymentIdSpinnerData(description: String) {
        val location = location
        val department = deptName
        val url = "${ApiFile.APP_URL}/vehDelvTrans/getByReceiptMethod?paymentType=$description&location=$location&department=$department&ouId=$ouId"

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
                        val paymentIds = parsePaymentIdData(jsonResponse)
                        runOnUiThread {
                            populatePaymentIdSpinner(paymentIds)
                        }
                    }
                }
            }
        })
    }

    private fun parsePaymentIdData(jsonResponse: JSONObject): List<String> {
        val paymentIdList = mutableListOf("Select Payment ID")
        val objArray: JSONArray = jsonResponse.getJSONArray("obj")

        for (i in 0 until objArray.length()) {
            val item = objArray.getJSONObject(i)
            val name = item.getString("NAME")
            paymentIdList.add(name)
        }

        return paymentIdList
    }

    private fun populatePaymentIdSpinner(paymentIds: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, paymentIds)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        paymentIdSpinner.adapter = adapter


        paymentIdSpinner.setSelection(0)

        paymentIdSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateSendOTPButtonState()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                updateSendOTPButtonState()
            }
        }
    }


    private fun updateSendOTPButtonState() {
        val isPaymentModeSelected = paymentModeSpinner.selectedItemPosition != 0
        val isPaymentIdSelected = paymentIdSpinner.selectedItemPosition != 0
        sendOTP.isEnabled = isPaymentModeSelected && isPaymentIdSelected

        val buttonColor = if (sendOTP.isEnabled) {
            ContextCompat.getColor(this, R.color.teal_700)
        } else {
            ContextCompat.getColor(this, R.color.secondary_text_color)
        }
        sendOTP.setBackgroundColor(buttonColor)
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


    private fun createImageFile(): File? {
        val storageDir: File? = externalCacheDir
        return File.createTempFile("JPEG_${System.currentTimeMillis()}_", ".jpg", storageDir).apply {
            photoFile = this
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            imageView.setImageURI(photoUri)
//            btnDelete.visibility = Button.VISIBLE
//            btnUpload.visibility = Button.VISIBLE
        } else {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun formatDateTime(dateTime: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
            val outputDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val outputTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(dateTime)
            val formattedDate = date?.let { outputDateFormat.format(it) }
            val formattedTime = date?.let { outputTimeFormat.format(it) }
            "$formattedDate $formattedTime"
        } catch (e: Exception) {
            dateTime
        }
    }

    private fun fetchInvData() {
        val invNo = invoiceNumberField.text.toString()
        val client = OkHttpClient()
        val url = ApiFile.APP_URL + "/vehDelvTrans/getByGatePassId?attribute1=$invNo&location=$location"
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
                    Log.d("Response Data", jsonObject.toString())

                    val code = jsonObject.getInt("code")
                    val message = jsonObject.getString("message")

                    runOnUiThread {
                        when (code) {
                            400 -> {
                                Toast.makeText(
                                    this@VehicleDelivery,
                                    message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            200 -> {
                                val objArray = jsonObject.optJSONArray("obj")
                                if (objArray != null && objArray.length() > 0) {
                                    val stockItem = objArray.getJSONObject(0)

//                                    val partyId = if (stockItem.has("PARTY_ID")) {
//                                        stockItem.optInt("PARTY_ID", 0)
//                                    } else {
//                                        stockItem.optInt("PARTYID", 0)
//                                    }


                                    val invData = InvoiceNoData(
                                        VEHICLE_NO = stockItem.optString("VEHICLE_NO"),
                                        TRANS_REF_NUM = stockItem.optString("TRANS_REF_NUM"),
                                        CUSTOMER_ADDRESS = stockItem.optString("CUSTOMER_ADDRESS"),
                                        CUSTOMER_NAME = stockItem.optString("CUSTOMER_NAME"),
                                        DATE_OF_DELIVERY = stockItem.optString("DATE_OF_DELIVERY"),
                                        CONTACT_NO = stockItem.optString("CONTACT_NO"),
                                        AMOUNT_DUE_REMAINING = stockItem.getInt("AMOUNT_DUE_REMAINING"),
                                        INVOICE_NO = stockItem.optString("INVOICE_NO"),
                                        EXECUTIVE = stockItem.optString("EXECUTIVE"),
                                        GATE_PASS_ID = stockItem.optString("GATE_PASS_ID"),
                                        SERVICE_LOCATION = stockItem.optString("SERVICE_LOCATION"),
                                        MODEL = stockItem.optString("MODEL"),
                                        PARTY_ID = stockItem.getInt("PARTY_ID")
                                    )

                                    populateFields(invData)
                                    Toast.makeText(
                                        this@VehicleDelivery,
                                        "Details found Successfully for Gate Pass No: $invNo",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    fetchPaymentModes()

                                    verifyOTPButton.setBackgroundColor(
                                        ContextCompat.getColor(this@VehicleDelivery, R.color.teal_700)
                                    )
                                    verifyOTPButton.isEnabled = true
                                    paymentIdSpinner.isEnabled = true
                                    paymentModeSpinner.isEnabled = true
                                } else {
                                    Toast.makeText(
                                        this@VehicleDelivery,
                                        "No details found for Gate Pass No: $invNo",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                            else -> {
                                Toast.makeText(
                                    this@VehicleDelivery,
                                    "Unexpected response: $message",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@VehicleDelivery,
                        "Failed to fetch details for Gate Pass No: $invNo. Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }


    private fun saveInvoiceDetails() {
        val invNo=invNumber
        val otp=otpInputField.text.toString()
        val payTransNumber=upiInputTextField.text.toString()
        val custContactNo=contactNoTxtView.text.toString()
        val remainingAmount=amountRemainingTxtView.text.toString().split(": ")[1]
        val remainAmt=remainingAmount.toFloat()
        val amtPaidByUser=amountPaidTxtField.text.toString()
        val amtToBePaid=amtPaidByUser.toFloat()
        if (amtToBePaid>remainAmt){
            Toast.makeText(this@VehicleDelivery,
                "Entered amount is greater than remaining amount.",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        val otp2=otp.toInt()
        val client = OkHttpClient()
        val url = ApiFile.APP_URL + "/vehDelvTrans/delvPaymentComplete"
        val jsonObject = JSONObject()

        val currentDateTime = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        val formattedDate = formatter.format(currentDateTime.time)
        Log.d("formattedDate", formattedDate)

        currentDateTime.add(Calendar.DAY_OF_MONTH, 1)
        jsonObject.put("invoiceNo", invNo)
        jsonObject.put("location", location)
        jsonObject.put("paymentType",paymentType2)
        jsonObject.put("receiptMethodId",paymentIdSpinner.selectedItem.toString())
        jsonObject.put("otp",otp2)
        jsonObject.put("paymentImage","-")
        jsonObject.put("paymentTransactionNo",payTransNumber)
        jsonObject.put("amountPaid",amtToBePaid)
        jsonObject.put("ouId",ouId)
        jsonObject.put("department",deptName)
        jsonObject.put("driverName","$userName-$login_name")
        jsonObject.put("driverLocationName",location_name)
        jsonObject.put("partyId",partyId.text.toString().split(": ")[1])
        jsonObject.put("attribute1",gatePassId)
        jsonObject.put("driverContactNo",userContact)
        jsonObject.put("attribute2","")
        jsonObject.put("attribute3","")
        jsonObject.put("attribute4","")
        jsonObject.put("attribute5","")
        jsonObject.put("driverLocId",locId)
//        jsonObject.put("custContactNo",custContactNo)


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
                            message.contains("Payment Already Done For This Invoice", ignoreCase = true) -> {
                                Toast.makeText(
                                    this@VehicleDelivery,
                                    "Payment Already Done For This Invoice",
                                    Toast.LENGTH_LONG
                                ).show()
                                resetFields()
                            }
                            responseCode == 200 -> {
                                detailsSubmit.isEnabled=false
                                Toast.makeText(
                                    this@VehicleDelivery,
                                    "Data saved successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                                paymentSuccessMessage()
                                resetFields()
                            }
                            else -> {
                                Toast.makeText(
                                    this@VehicleDelivery,
                                    "Failed to save data. Error code: $responseCode",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@VehicleDelivery,
                            "Data not available",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("SaveData", "Error: ${e.message}")
                runOnUiThread {
                    Toast.makeText(
                        this@VehicleDelivery,
                        "Error saving data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }



    private fun paymentSuccessMessage() {
        val otp=otpInputField.text.toString()
        val custContactNo=contactNoTxtView.text.toString()
        val amtPaidByUser=amountPaidTxtField.text.toString()
        val amtToBePaid=amtPaidByUser.toInt()
        if(ouId==104){
            cityConverter="Mumbai"
        }
        if(ouId==81){
            cityConverter="Pune"
        }
        if(ouId==105){
            cityConverter="Kolhapur"
        }
        if(ouId==106){
            cityConverter="Goa"
        }
        if(ouId==107){
            cityConverter="Cochin"
        }
        if(ouId==108){
            cityConverter="Hyderabad"
        }
        val otp2=otp.toInt()
        val client = OkHttpClient()
        val url = ApiFile.APP_URL + "/vehDelvTrans/sendPaymentSuccessSms"
        val jsonObject = JSONObject()


        val currentDateTime = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        val formattedDate = formatter.format(currentDateTime.time)
        Log.d("formattedDate", formattedDate)

        currentDateTime.add(Calendar.DAY_OF_MONTH, 1)
        jsonObject.put("trxNumber",gatePassId)
        jsonObject.put("instanceNumber", vehicleNumberTxtView.text.toString().split(": ")[1])
        jsonObject.put("partyName", nameTxtView.text.toString().split(": ")[1])
        jsonObject.put("accountType",paymentIdSpinner.selectedItem.toString())
        jsonObject.put("mobileNo",custContactNo)
        jsonObject.put("orgId",ouId)
        jsonObject.put("amount",amtToBePaid)
        jsonObject.put("method",paymentType2)
        jsonObject.put("city",cityConverter)

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
                            message.contains("Payment Already Done For This Invoice", ignoreCase = true) -> {
                                Toast.makeText(
                                    this@VehicleDelivery,
                                    "Payment Already Done For This Invoice",
                                    Toast.LENGTH_LONG
                                ).show()
                                resetFields()
                            }
                            responseCode == 200 -> {
                                Toast.makeText(
                                    this@VehicleDelivery,
                                    "Data saved successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                                resetFields()
                            }
                            else -> {
                                Toast.makeText(
                                    this@VehicleDelivery,
                                    "Failed to save data. Error code: $responseCode",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@VehicleDelivery,
                            "No response from server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("SaveData", "Error: ${e.message}")
                runOnUiThread {
                    Toast.makeText(
                        this@VehicleDelivery,
                        "Error saving data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun generateOTP() {
        val invNo = invNumber
        val contactNo=contactNoTxtView.text.toString()

        if (invNo.isEmpty()) {
            Toast.makeText(this@VehicleDelivery, "Invoice Number cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        val client = OkHttpClient()
        val url = "${ApiFile.APP_URL}/vehDelvOtp/generateDelvOtp?invoiceNo=$invNo&mobileNo=$contactNo"

        Log.d("generateOTP", "Request URL: $url")

        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create(null, byteArrayOf()))
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseCode = response.code
                val responseBody = response.body?.string()

                Log.d("generateOTP", "Response Code: $responseCode")
                Log.d("generateOTP", "Response Body: $responseBody")

                runOnUiThread {
                    if (responseBody != null) {
                        try {
                            val jsonResponse = JSONObject(responseBody)
                            val message = jsonResponse.optString("message", "")

                            when {
                                message.contains("Payment Already Done For This Invoice", ignoreCase = true) -> {
                                    Toast.makeText(
                                        this@VehicleDelivery,
                                        "Payment Already Done For This Invoice",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                responseCode == 200 -> {
                                    val otpCode = jsonResponse.optString("data", "")
                                    Toast.makeText(
                                        this@VehicleDelivery,
                                        "OTP Generated and sent successfully.\nOTP will be valid for only 2 minutes only.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                else -> {
                                    Toast.makeText(
                                        this@VehicleDelivery,
                                        "Failed to generate OTP. Error code: $responseCode",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(
                                this@VehicleDelivery,
                                "Error processing the response: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@VehicleDelivery,
                            "No response from server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("generateOTP", "Error: ${e.message}")
                runOnUiThread {
                    Toast.makeText(
                        this@VehicleDelivery,
                        "Error generating OTP: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun validateOTP() {
        val otp = otpInputField.text.toString()
        val inv=invNumber

        if (otp.isEmpty()) {
            Toast.makeText(this@VehicleDelivery, "OTP cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val client = OkHttpClient()
        val url = "${ApiFile.APP_URL}/vehDelvOtp/validateDelvOtp?invoiceNo=$inv&otp=$otp&status=SEND"

        Log.d("generateOTP", "Request URL: $url")

        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create(null, byteArrayOf()))
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseCode = response.code
                val responseBody = response.body?.string()

                Log.d("generateOTP", "Response Code: $responseCode")
                Log.d("generateOTP", "Response Body: $responseBody")

                runOnUiThread {
                    if (responseBody != null) {
                        try {
                            val jsonResponse = JSONObject(responseBody)
                            val message = jsonResponse.optString("message", "")
                            val code = jsonResponse.optInt("code", -1) // Extracting the code field

                            when {
                                code == 400 -> {
                                    Toast.makeText(this@VehicleDelivery, "OTP IS INVALID OR EXPIRED" ,Toast.LENGTH_LONG).show()
                                }
                                code == 200 -> {
                                    Toast.makeText(
                                        this@VehicleDelivery,
                                        "Validation successful",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    detailsSubmit.visibility=View.VISIBLE
                                }
                                else -> {
                                    Toast.makeText(
                                        this@VehicleDelivery,
                                        "Unexpected response: $message",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(
                                this@VehicleDelivery,
                                "Error processing the response: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@VehicleDelivery,
                            "No response from server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("generateOTP", "Error: ${e.message}")
                runOnUiThread {
                    Toast.makeText(
                        this@VehicleDelivery,
                        "Error generating OTP: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun populateFields(InvoiceNoData:InvoiceNoData) {
        invoiceNumberTxtView.text = "Invoice No.: ${InvoiceNoData.INVOICE_NO}"
        vehicleNumberTxtView.text = "Vehicle No.: ${InvoiceNoData.VEHICLE_NO}"
        addressTxtView.text = "Address: ${InvoiceNoData.CUSTOMER_ADDRESS}"
        nameTxtView.text = "Name: ${InvoiceNoData.CUSTOMER_NAME}"
        contactNoTxtView.setText(InvoiceNoData.CONTACT_NO)
        transactionNoTxtView.text = "Transaction Ref No.: ${InvoiceNoData.TRANS_REF_NUM}"
        transactionNoDateTxtView.text = "Delivery Date.: ${formatDateTime(InvoiceNoData.DATE_OF_DELIVERY)}"
        transactionDate=InvoiceNoData.DATE_OF_DELIVERY
        amountRemainingTxtView.text="Balance Amount: ${InvoiceNoData.AMOUNT_DUE_REMAINING}"
        serviceExecutive.text="Service Executive: ${InvoiceNoData.EXECUTIVE}"
        modelTextView.text="Model: ${InvoiceNoData.MODEL}"
        partyId.text="Customer Id: ${InvoiceNoData.PARTY_ID}"
        val amountPaid=InvoiceNoData.AMOUNT_DUE_REMAINING
        if(amountPaid!=null) {
            amountPaidTxtField.setText("$amountPaid")
        }
        invNumber=InvoiceNoData.INVOICE_NO
        vehicleNumberTxtView.visibility=View.VISIBLE
        modelTextView.visibility=View.VISIBLE
        addressTxtView.visibility=View.VISIBLE
        partyId.visibility=View.VISIBLE
        nameTxtView.visibility=View.VISIBLE
        contactNoTxtView.visibility=View.VISIBLE
        transactionNoTxtView.visibility=View.VISIBLE
        transactionNoDateTxtView.visibility=View.VISIBLE
        invoiceNumberTxtView.visibility=View.VISIBLE
        amountRemainingTxtView.visibility=View.VISIBLE
        serviceExecutive.visibility=View.VISIBLE
        amountRemaining= InvoiceNoData.AMOUNT_DUE_REMAINING.toString()

        gatePassId=InvoiceNoData.GATE_PASS_ID

        val pendingAmount=amountRemaining.toFloat()
//        val pendingAmount=1000

        Log.d("pendingAmount-->",pendingAmount.toString())
        if(pendingAmount<=0){
            invoiceDataLL.visibility=View.GONE
        } else {
            invoiceDataLL.visibility=View.VISIBLE
            upiInputTextField.visibility=View.GONE
            upiText.visibility=View.GONE
        }
    }

    private fun resetFields(){
        invoiceNumberField.setText("")
        vehicleNumberTxtView.text=""
        vehicleNumberTxtView.visibility=View.GONE
        addressTxtView.visibility=View.GONE
        nameTxtView.visibility=View.GONE
        contactNoTxtView.visibility=View.GONE
        transactionNoTxtView.visibility=View.GONE
        transactionNoDateTxtView.visibility=View.GONE
        amountRemainingTxtView.visibility=View.GONE
        invoiceNumberTxtView.visibility=View.GONE
        serviceExecutive.visibility=View.GONE
        modelTextView.visibility=View.GONE
        serviceExecutive.text=""
        partyId.visibility=View.GONE
        partyId.text=""
        invoiceNumberTxtView.text=""
        modelTextView.text=""
        addressTxtView.text=""
        nameTxtView.text=""
        contactNoTxtView.setText("")
        transactionNoTxtView.text=""
        transactionNoDateTxtView.text=""
        amountRemainingTxtView.text=""
        otpInputField.setText("")
        amountPaidTxtField.setText("")
        upiInputTextField.setText("")
        detailsSubmit.visibility=View.GONE
        sendOTP.isEnabled=false
        sendOTP.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
        verifyOTPButton.isEnabled=false
        verifyOTPButton.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
        paymentIdSpinner.isEnabled=false
        paymentModeSpinner.isEnabled=false
        resetSpinner()
        resetSpinner2()
        invoiceDataLL.visibility=View.GONE
        detailsSubmit.isEnabled=true
    }

    fun resetSpinner() {
        val adapter = paymentModeSpinner.adapter as ArrayAdapter<String>
        adapter.clear()
        adapter.addAll(emptyList())
        adapter.notifyDataSetChanged()
        paymentModeSpinner.setSelection(0)
    }

    fun resetSpinner2() {
        val adapter2 = paymentIdSpinner.adapter as? ArrayAdapter<String>
        if (adapter2 != null) {
            adapter2.clear()
            adapter2.addAll(emptyList())
            adapter2.notifyDataSetChanged()
        }
        paymentIdSpinner.setSelection(0)
    }



    private fun logout(){
        val intent=Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    data class InvoiceNoData(
        val VEHICLE_NO:String,
        val TRANS_REF_NUM:String,
        val CUSTOMER_ADDRESS:String,
        val CUSTOMER_NAME:String,
        val DATE_OF_DELIVERY:String,
        val CONTACT_NO:String,
        val AMOUNT_DUE_REMAINING: Int,
        val INVOICE_NO:String,
        val EXECUTIVE:String,
        val GATE_PASS_ID:String,
        val SERVICE_LOCATION:String,
        val MODEL:String,
        val PARTY_ID:Int
    )

}