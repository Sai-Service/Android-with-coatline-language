package com.example.apinew


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
import androidx.appcompat.app.AlertDialog
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
import java.util.concurrent.TimeUnit
import android.os.Handler
import android.os.Looper
// ML Kit imports for OCR
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions


class VehicleDelivery : AppCompatActivity() {
    private lateinit var invoiceNumberField: EditText
    private lateinit var searchButton: Button
    private lateinit var vehicleNumberTxtView: TextView
    private lateinit var gatePassNumberTxtView: TextView
    private lateinit var nameTxtView: TextView
    private lateinit var transactionNoTxtView: TextView
    private lateinit var transactionNoDateTxtView: TextView
    // contactNoTxtView changed to TextView (non-editable)
    private lateinit var contactNoTxtView: TextView
    // New field for alternate mobile number
    private lateinit var alternateMobileField: EditText
    private lateinit var alternateMobileLayout: View
    private lateinit var addressTxtView: TextView
    private lateinit var amountRemainingTxtView: TextView
    private lateinit var amountPaidTxtField: EditText
    private lateinit var modelTextView: TextView
    private lateinit var sendOTP: Button
    private lateinit var otpInputField: EditText
    private lateinit var verifyOTPButton: Button
    private lateinit var upiInputTextField: EditText
    private lateinit var detailsSubmit: Button
    private lateinit var logoImageView: ImageView
    private lateinit var username: TextView
    private lateinit var locIdTxt: TextView
    private lateinit var login_name: String
    private lateinit var location_name: String
    private lateinit var userName: String
    private lateinit var userContact: String
    private lateinit var location: String
    private lateinit var deptName: String
    private lateinit var logoutBtn: ImageView
    private var ouId: Int = 0
    private var locId: Int = 0
    private lateinit var imageView: ImageView
    private lateinit var captureImage: ImageButton
    private lateinit var deleteImage: ImageButton
    private var photoUri: Uri? = null
    private var photoFile: File? = null
    private lateinit var imageText: TextView
    private lateinit var upiText: TextView
    private lateinit var invNumber: String
    private lateinit var invoiceNumberTxtView: TextView
    private lateinit var paymentIdSpinner: Spinner
    private lateinit var paymentModeSpinner: Spinner
    private lateinit var paymentType2: String
    private lateinit var clearFields: Button
    private lateinit var serviceExecutive: TextView
    private lateinit var invoiceDataLL: View
    private lateinit var amountRemaining: String
    private lateinit var partyId: TextView
    private lateinit var transactionDate: String
    private lateinit var gatePassId: String
    private lateinit var cityConverter: String
    private lateinit var customerPaymentSuccessMessageContactNo: String

    // UPI image section layout
    private lateinit var upiImageSection: View
    private lateinit var upiImageMandatoryNote: TextView

    private lateinit var forVehicleNo: Button
    private lateinit var forGatePassNo: Button
    private lateinit var noShowingTextView: TextView
    private lateinit var searchButton2: Button

    private var amountRefreshHandler: Handler? = null
    private var amountRefreshRunnable: Runnable? = null
    private var isRefreshing = false
    private lateinit var sendMsgToOGNo:String
    private val GALLERY_REQUEST_CODE = 102   // add next to your existing camera code 101

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
        userContact = intent.getStringExtra("userContact") ?: ""
        userName = intent.getStringExtra("userName") ?: ""

        vehicleNumberTxtView = findViewById(R.id.vehicleNumberTxtView)
        gatePassNumberTxtView = findViewById(R.id.gatePassNumberTxtView)
        nameTxtView = findViewById(R.id.nameTxtView)
        transactionNoTxtView = findViewById(R.id.transactionNoTxtView)
        transactionNoDateTxtView = findViewById(R.id.transactionNoDateTxtView)
        serviceExecutive = findViewById(R.id.serviceExecutive)
        // Now a TextView, not EditText
        contactNoTxtView = findViewById(R.id.contactNoTxtView)
        // New alternate mobile views
        alternateMobileField = findViewById(R.id.alternateMobileField)
        alternateMobileLayout = findViewById(R.id.alternateMobileLayout)
        modelTextView = findViewById(R.id.modelTextView)
        addressTxtView = findViewById(R.id.addressTxtView)
        amountRemainingTxtView = findViewById(R.id.amountRemainingTxtView)
        amountPaidTxtField = findViewById(R.id.amountPaidTxtField)
        invoiceNumberTxtView = findViewById(R.id.invoiceNumberTxtView)
        paymentIdSpinner = findViewById(R.id.paymentIdSpinner)
        imageView = findViewById(R.id.imageView)
        captureImage = findViewById(R.id.captureImage)
        deleteImage = findViewById(R.id.deleteImage)
        imageText = findViewById(R.id.imageText)
        upiText = findViewById(R.id.upiText)
        clearFields = findViewById(R.id.clearFields)
        partyId = findViewById(R.id.partyId)
        upiImageSection = findViewById(R.id.upiImageSection)
        upiImageMandatoryNote = findViewById(R.id.upiImageMandatoryNote)
        // Add this alongside captureImageBtn and deleteImageBtn wiring
        findViewById<Button>(R.id.uploadImageBtn).setOnClickListener { openGallery() }

        forVehicleNo = findViewById(R.id.forVehicleNo)
        forGatePassNo = findViewById(R.id.forGatePassNo)
        noShowingTextView = findViewById(R.id.noShowingTextView)
        searchButton2 = findViewById(R.id.searchButton2)

        invoiceDataLL = findViewById(R.id.invoiceDataLL)
        invoiceDataLL.visibility = View.GONE

        invoiceNumberField = findViewById(R.id.invoiceNumberField)
        searchButton = findViewById(R.id.searchButton)
        paymentModeSpinner = findViewById(R.id.paymentModeSpinner)
        sendOTP = findViewById(R.id.sendOTP)
        otpInputField = findViewById(R.id.otpInputField)
        verifyOTPButton = findViewById(R.id.verifyOTPButton)
        upiInputTextField = findViewById(R.id.upiInputTextField)
        detailsSubmit = findViewById(R.id.detailsSubmit)
        logoImageView = findViewById(R.id.logoImageView)
        logoutBtn = findViewById(R.id.logoutBtn)
        username = findViewById(R.id.username)
        locIdTxt = findViewById(R.id.locIdTxt)
        locIdTxt.text = "$location_name"
        username.text = "$login_name"

//        upiInputTextField.filters = arrayOf(android.text.InputFilter.LengthFilter(35))

//        upiInputTextField.filters = arrayOf(
//            android.text.InputFilter.LengthFilter(35),
//            android.text.InputFilter { source, _, _, _, _, _ ->
//                if (source.all { it.isLetterOrDigit() }) null else ""
//            }
//        )

        // Initial View Gone
        invoiceNumberField.visibility = View.GONE
        noShowingTextView.visibility = View.GONE
        searchButton.visibility = View.GONE
        clearFields.visibility = View.GONE
        searchButton2.visibility = View.GONE
        alternateMobileLayout.visibility = View.GONE
        upiImageSection.visibility = View.GONE

        forVehicleNo.setOnClickListener {
            invoiceNumberField.visibility = View.VISIBLE
            noShowingTextView.visibility = View.VISIBLE
            searchButton2.visibility = View.VISIBLE
            searchButton.visibility = View.GONE
            clearFields.visibility = View.VISIBLE
            noShowingTextView.text = "Vehicle Number"
            //////
            invoiceNumberField.setText("")
            invoiceNumberField.inputType = android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
            invoiceNumberField.filters = arrayOf(
                android.text.InputFilter.LengthFilter(10),
                android.text.InputFilter.AllCaps()
            )
            invoiceNumberField.hint = "Enter Vehicle Number (max 10)"
        }

        forGatePassNo.setOnClickListener {
            invoiceNumberField.visibility = View.VISIBLE
            noShowingTextView.visibility = View.VISIBLE
            searchButton.visibility = View.VISIBLE
            searchButton2.visibility = View.GONE
            clearFields.visibility = View.VISIBLE
            noShowingTextView.text = "Gate Pass Number"
            /////
            invoiceNumberField.setText("")
            invoiceNumberField.inputType = android.text.InputType.TYPE_CLASS_NUMBER
            invoiceNumberField.filters = arrayOf()
            invoiceNumberField.hint = "Enter Gate Pass Number"
        }

        detailsSubmit.setOnClickListener {
            // Validate UPI image if payment mode is UPI
//            val selectedPaymentMode = paymentModeSpinner.selectedItem?.toString() ?: ""
//            if (!selectedPaymentMode.equals("Cash-CASH", ignoreCase = true) &&
//                !selectedPaymentMode.equals("Credit Card-CREDIT_CARD", ignoreCase = true)
//            ) {
//                if (photoUri == null) {
//                    Toast.makeText(
//                        this,
//                        "Please capture the UPI payment screenshot before submitting.",
//                        Toast.LENGTH_LONG
//                    ).show()
//                    return@setOnClickListener
//                }
//            }
            detailsSubmit.isEnabled = false
            saveInvoiceDetails()
        }

        logoutBtn.setOnClickListener { logout() }

        searchButton.setOnClickListener { fetchInvData() }

        searchButton2.setOnClickListener { vehicleNoData() }

        sendOTP.isEnabled = false
        verifyOTPButton.isEnabled = false
        paymentIdSpinner.isEnabled = false
        paymentModeSpinner.isEnabled = false

        val cameraAction = {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
            } else {
                openCamera()
            }
        }

        val deleteAction = {
            imageView.setImageURI(null)
            imageView.setImageResource(R.drawable.imagehint)
            imageView.visibility = View.GONE
            photoUri = null
            photoFile = null
            upiInputTextField.setText("")
        }

        findViewById<Button>(R.id.captureImageBtn).setOnClickListener { cameraAction() }
        findViewById<Button>(R.id.uploadImageBtn).setOnClickListener { openGallery() }
        findViewById<Button>(R.id.deleteImageBtn).setOnClickListener { deleteAction() }

// Keep legacy hidden ImageButtons working too
        captureImage.setOnClickListener { cameraAction() }
        deleteImage.setOnClickListener { deleteAction() }



        sendOTP.setOnClickListener { generateOTP() }

        verifyOTPButton.setOnClickListener { validateOTP() }

        clearFields.setOnClickListener { resetFields() }
    }

    // -----------------------------------------------------------------------
    // PAYMENT MODES
    // -----------------------------------------------------------------------

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }
    private fun fetchPaymentModes() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.DELIVERY_APP_URL}/vehDelvTrans/getByPaymentType?paymentType=CASH_RECEIPT_TYPE")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { e.printStackTrace() }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val jsonResponse = JSONObject(responseBody)
                        val paymentModes = parsePaymentModes(jsonResponse)
                        runOnUiThread { setupPaymentModeSpinner(paymentModes) }
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
        }
        return paymentList
    }

    private fun setupPaymentModeSpinner(paymentModes: List<String>) {
        val adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            paymentModes
        ) {
            override fun isEnabled(position: Int) = position != 0

            override fun getDropDownView(
                position: Int,
                convertView: android.view.View?,
                parent: android.view.ViewGroup
            ): android.view.View {
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
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    val selectedItem = paymentModeSpinner.selectedItem.toString()
                    val selectedPaymentType = selectedItem.split("-")[1]
                    paymentType2 = selectedPaymentType
                    fetchPaymentIdSpinnerData(selectedPaymentType)

                    val isCashOrCard = selectedItem.equals("Cash-CASH", ignoreCase = true) ||
                            selectedItem.equals("Credit Card-CREDIT_CARD", ignoreCase = true)

                    if (isCashOrCard) {
                        upiText.visibility = View.GONE
                        upiInputTextField.visibility = View.GONE
                        // Hide UPI image section for Cash/Card
                        upiImageSection.visibility = View.GONE
                        photoUri = null
                        photoFile = null
                        imageView.setImageResource(R.drawable.imagehint)
                    } else {
                        upiText.visibility = View.VISIBLE
                        upiInputTextField.visibility = View.VISIBLE
                        // Show UPI image section — mandatory
                        upiImageSection.visibility = View.VISIBLE
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
        val url =
            "${ApiFile.DELIVERY_APP_URL}/vehDelvTrans/getByReceiptMethod?paymentType=$description&location=$location&department=$department&ouId=$ouId"
        Log.d("url-fetchPaymentIdSpinnerData", url)

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { e.printStackTrace() }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val jsonResponse = JSONObject(responseBody)
                        val paymentIds = parsePaymentIdData(jsonResponse)
                        runOnUiThread { populatePaymentIdSpinner(paymentIds) }
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
            paymentIdList.add(item.getString("NAME"))
        }
        return paymentIdList
    }

    private fun populatePaymentIdSpinner(paymentIds: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, paymentIds)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        paymentIdSpinner.adapter = adapter
        paymentIdSpinner.setSelection(0)

        paymentIdSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) { updateSendOTPButtonState() }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                updateSendOTPButtonState()
            }
        }
    }

    private fun updateSendOTPButtonState() {
        val isPaymentModeSelected = paymentModeSpinner.selectedItemPosition != 0
        val isPaymentIdSelected = paymentIdSpinner.selectedItemPosition != 0
        sendOTP.isEnabled = isPaymentModeSelected && isPaymentIdSelected

        val buttonColor = if (sendOTP.isEnabled)
            ContextCompat.getColor(this, R.color.teal_700)
        else
            ContextCompat.getColor(this, R.color.secondary_text_color)
        sendOTP.setBackgroundColor(buttonColor)
    }

    // -----------------------------------------------------------------------
    // CAMERA & OCR
    // -----------------------------------------------------------------------

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile = createImageFile()
        photoFile?.also {
            photoUri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.provider",
                it
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivityForResult(takePictureIntent, 101)
        }
    }

    private fun createImageFile(): File? {
        val storageDir: File? = externalCacheDir
        return File.createTempFile(
            "JPEG_${System.currentTimeMillis()}_",
            ".jpg",
            storageDir
        ).apply { photoFile = this }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
//            imageView.setImageURI(photoUri)
//            imageView.visibility = View.VISIBLE
//            // Auto-extract transaction ID via ML Kit OCR
//            photoUri?.let { uri ->
//                extractTransactionIdFromImage(uri)
//            }
//        } else {
//            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
//            if (result != null) {
//                if (result.contents == null) {
//                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
//                } else {
//                    Toast.makeText(this, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
//                }
//            }
//        }
//    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when {
            // ── Camera capture ──────────────────────────────────────────────
            requestCode == 101 && resultCode == Activity.RESULT_OK -> {
                imageView.setImageURI(photoUri)
                imageView.visibility = View.VISIBLE
                photoUri?.let { extractTransactionIdFromImage(it) }
            }

            // ── Gallery pick ────────────────────────────────────────────────
            requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK -> {
                val selectedUri = data?.data
                if (selectedUri != null) {
                    // Copy the gallery image into our cache file so the
                    // existing upload logic (photoFile) works unchanged
                    try {
                        val inputStream = contentResolver.openInputStream(selectedUri)
                        val cacheFile = createImageFile()   // sets photoFile
                        cacheFile?.outputStream()?.use { out ->
                            inputStream?.copyTo(out)
                        }
                        inputStream?.close()

                        // Point photoUri at the copied cache file so multipart
                        // upload sends the actual bytes
                        photoUri = FileProvider.getUriForFile(
                            this,
                            "${applicationContext.packageName}.provider",
                            photoFile!!
                        )

                        imageView.setImageURI(selectedUri)   // show original for preview
                        imageView.visibility = View.VISIBLE

                        // Run OCR on the selected image using its original URI
                        extractTransactionIdFromImage(selectedUri)

                    } catch (e: Exception) {
                        Log.e("Gallery", "Failed to copy gallery image: ${e.message}")
                        Toast.makeText(this, "Failed to load image. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // ── QR / barcode fallback ───────────────────────────────────────
            else -> {
                val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
                if (result != null && result.contents == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    /**
     * Uses ML Kit Text Recognition to scan the captured image and extract
     * a UPI/transaction reference number, then auto-fills the transaction field.
     *
     * Common UPI transaction ID patterns:
     *  - 12-digit numeric strings (UPI Ref / UTR)
     *  - Strings starting with T followed by digits (PhonePe)
     *  - Patterns labeled "UPI Ref", "Transaction ID", "UTR", "Ref No" etc.
     */
    /**
     * Validates that the captured image is a UPI payment screenshot by checking
     * OCR text for known UPI payment keywords. If the image does not look like
     * a payment screen, it is rejected and the user is prompted to retake.
     */
    private fun extractTransactionIdFromImage(uri: Uri) {
        try {
            val image = InputImage.fromFilePath(this, uri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val fullText = visionText.text
                    Log.d("OCR_TEXT", fullText)

                    // ── Step 1: Validate image is a UPI payment screenshot ──
                    if (!isValidUpiScreenshot(fullText)) {
                        Log.w("OCR_VALIDATE", "Image rejected — not a UPI payment screenshot")
                        runOnUiThread {
                            // Clear the captured image — force user to retake
                            imageView.setImageResource(R.drawable.imagehint)
                            imageView.visibility = View.GONE
                            photoUri = null
                            photoFile = null
                            upiInputTextField.setText("")

                            AlertDialog.Builder(this)
                                .setTitle("Invalid Image")
                                .setMessage(
                                    "The captured image does not appear to be a UPI payment screenshot.\n\n" +
                                            "Please capture the payment confirmation screen from your UPI app " +
                                            "(Google Pay, PhonePe, Paytm, BHIM, etc.) showing the transaction details."
                                )
                                .setPositiveButton("Retake") { dialog, _ ->
                                    dialog.dismiss()
                                    openCamera()
                                }
                                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                                .setCancelable(false)
                                .show()
                        }
                        return@addOnSuccessListener
                    }

                    // ── Step 2: Image is valid — extract transaction ID ──
                    val transactionId = parseTransactionId(fullText)
                    runOnUiThread {
                        if (!transactionId.isNullOrEmpty()) {
                            upiInputTextField.setText(transactionId)
                            Toast.makeText(
                                this,
                                "Transaction ID auto-filled: $transactionId\nPlease verify before submitting.",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                this,
                                "Payment screenshot accepted. Could not auto-detect Transaction ID — please enter manually.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("OCR_ERROR", "OCR failed: ${e.message}")
                    runOnUiThread {
                        Toast.makeText(
                            this,
                            "Image scan failed. Please enter Transaction ID manually.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } catch (e: Exception) {
            Log.e("OCR_ERROR", "Error creating image: ${e.message}")
        }
    }

    /**
     * Returns true if the OCR text from the image looks like a genuine UPI
     * payment confirmation screen.
     *
     * Validation rules (must satisfy at least ONE from each group):
     *
     * Group A — Payment success indicators:
     *   "paid", "payment successful", "success", "money sent", "transferred",
     *   "transaction successful", "approved", "debited", "payment done",
     *   "payment complete", "amount paid", "₹", "rs.", "inr"
     *
     * Group B — UPI / transaction reference indicators:
     *   "upi", "transaction id", "txn", "utr", "ref no", "reference",
     *   "gpay", "google pay", "phonepe", "paytm", "bhim", "payment"
     *
     * An image must pass BOTH groups to be accepted. This prevents random
     * screenshots (selfies, documents, QR codes without confirmation, etc.)
     * from being accepted.
     */
    private fun isValidUpiScreenshot(ocrText: String): Boolean {
        val lower = ocrText.lowercase()

        // ── Group A: Payment completion / success indicators ─────────────
        // Covers every major phrasing used by Indian UPI apps:
        // Google Pay → "Completed"
        // PhonePe    → "Payment Successful", "Sent to", "Sent Successfully"
        // Paytm      → "Payment Successful!", "Paid to"
        // BHIM       → "Transaction Successful", "Amount ₹"
        // SBI YONO   → "Transaction Successful", "Amount Transferred"
        // HDFC/ICICI → "Fund Transfer Successful", "Payment Successful"
        // Axis/Kotak → "Transaction Successful"
        // CRED       → "Payment done", "Transaction complete"
        // MobiKwik   → "Money Sent!"
        // Airtel     → "Money Sent Successfully"
        val groupA = listOf(
            "paid", "payment successful", "success", "money sent", "transferred",
            "transaction successful", "approved", "debited", "payment done",
            "payment complete", "amount paid", "₹", "rs.", "rs ", "inr", "sent",
            "completed", "complete", "done", "credited", "transaction complete",
            "payment received", "received", "sent to", "fund transfer",
            "transfer successful", "amount transferred", "sent successfully",
            "money sent successfully", "payment done", "transfer done"
        )

        // ── Group B: UPI / payment app / bank context indicators ──────────
        // Covers all UPI-enabled apps and banks in India.
        // Also covers UPI VPA suffixes (@ybl, @okaxis, etc.) and
        // "Powered by UPI" footer present on all NPCI-compliant screens.
        val groupB = listOf(
            // Generic UPI keywords
            "upi", "transaction id", "txn", "utr", "ref no", "reference",
            "vpa", "upi id", "upi ref", "upi transaction", "utr no",
            "utr number", "upi reference",
            // Payment apps
            "google pay", "gpay", "phonepe", "paytm", "bhim",
            "amazon pay", "whatsapp pay", "cred", "mobikwik", "freecharge",
            "airtel payments", "jio payments", "yes pay", "bob world",
            "pnb one", "yono", "imobile pay", "axis mobile", "kotak 811",
            // Banks
            "bank of india", "hdfc", "sbi", "icici", "axis bank", "kotak",
            "canara bank", "punjab national", "bank of baroda", "yes bank",
            "union bank", "indian bank", "central bank", "indian overseas",
            "federal bank", "idbi bank", "bandhan bank", "rbl bank",
            "indusind bank", "idfc first", "airtel bank", "jio bank",
            // UPI VPA suffixes (all NPCI approved handles)
            "@ybl", "@okaxis", "@okhdfcbank", "@okicici", "@oksbi",
            "@okbizaxis", "@ibl", "@axl", "@upi", "@axisbank",
            "@kotak", "@airtel", "@jio", "@yesbank", "@barodampay",
            "@cnrb", "@pnb", "@uboi", "@cbin", "@iob",
            // NPCI / infrastructure
            "powered by upi", "national payments", "npci",
            "unified payments"
        )

        val passesA = groupA.any { lower.contains(it) }
        val passesB = groupB.any { lower.contains(it) }

        Log.d("OCR_VALIDATE", "GroupA pass=$passesA, GroupB pass=$passesB")
        Log.d("OCR_VALIDATE", "OCR text (first 300 chars): ${ocrText.take(300)}")

        return passesA && passesB
    }

    /**
     * Parses OCR text to extract UPI transaction / UTR / reference numbers.
     *
     * Strategy:
     * 1. Find a label line (e.g. "UPI transaction ID"), then take the NEXT non-empty line as the value.
     *    This handles Google Pay / PhonePe where the ID is on the line BELOW the label.
     * 2. Prefer UPI transaction ID (12-digit numeric) over Google transaction ID (alphanumeric).
     * 3. Fallback patterns for other apps (Paytm, BHIM, etc.)
     *
     * Priority order:
     *   UPI transaction ID label → next line value   (Google Pay 12-digit)
     *   UTR / UPI Ref label      → next line value
     *   12-digit standalone numeric                   (universal UTR)
     *   PhonePe T + 11 digits
     *   Other alphanumeric >= 10 chars (NOT Google txn ID which starts "CICA")
     */
//    private fun parseTransactionId(text: String): String? {
//        val lines = text.lines().map { it.trim() }.filter { it.isNotEmpty() }
//
//        // ── Priority 1: Label-then-next-line strategy ──────────────────────
//        // Labels that indicate the NEXT line holds the UPI/UTR value.
//        // "UPI transaction ID" is highest priority (Google Pay).
//        // "Google transaction ID" is intentionally excluded — we don't want that.
//        val upiLabels = listOf(
//            "upi transaction id",
//            "upi ref no",
//            "upi ref",
//            "utr no",
//            "utr number",
//            "utr",
//            "transaction ref",
//            "transaction reference",
//            "txn id",
//            "txn ref",
//            "ref no",
//            "reference id",
//            "payment ref"
//        )
//
//        for (i in lines.indices) {
//            val lower = lines[i].lowercase()
//            for (label in upiLabels) {
//                if (lower == label || lower.startsWith(label)) {
//                    // Value is on the NEXT line
//                    val nextLine = lines.getOrNull(i + 1) ?: continue
//                    // Must be purely numeric (UTR) or alphanumeric, min 8 chars, no spaces
//                    if (nextLine.matches(Regex("[A-Za-z0-9]{8,}"))) {
//                        Log.d("OCR_MATCH", "Matched label '$label' → value '$nextLine'")
//                        return nextLine
//                    }
//                    // Sometimes the value is on the same line after a colon/space
//                    val afterColon = lower.substringAfter(label).replace(":", "").trim()
//                    if (afterColon.matches(Regex("[A-Za-z0-9]{8,}"))) {
//                        return afterColon
//                    }
//                }
//            }
//        }
//
//        // ── Priority 2: 12-digit standalone numeric (standard UTR) ─────────
//        // Matches exactly 12 consecutive digits — most common UPI transaction ID format.
//        val utrPattern = Regex("(?<![\\d])\\d{12}(?![\\d])")
//        val utrMatch = utrPattern.find(text)
//        if (utrMatch != null) {
//            Log.d("OCR_MATCH", "12-digit UTR match: ${utrMatch.value}")
//            return utrMatch.value
//        }
//
//        // ── Priority 3: PhonePe — T followed by exactly 11 digits ──────────
//        val phonepePattern = Regex("(?i)\\bT\\d{11}\\b")
//        val phonepeMatch = phonepePattern.find(text)
//        if (phonepeMatch != null) {
//            Log.d("OCR_MATCH", "PhonePe match: ${phonepeMatch.value}")
//            return phonepeMatch.value.uppercase()
//        }
//
//        // ── Priority 4: Paytm / BHIM — purely numeric 10–15 digits ─────────
//        val paytmPattern = Regex("(?<![\\d])\\d{10,15}(?![\\d])")
//        val paytmMatch = paytmPattern.find(text)
//        if (paytmMatch != null) {
//            Log.d("OCR_MATCH", "Numeric fallback: ${paytmMatch.value}")
//            return paytmMatch.value
//        }
//
//        // ── Priority 5: Alphanumeric >= 10 chars, exclude Google txn IDs ───
//        // Google transaction IDs start with "CICA" — skip those.
//        for (line in lines) {
//            if (line.matches(Regex("[A-Za-z0-9]{10,}")) && !line.startsWith("CICA", ignoreCase = true)) {
//                Log.d("OCR_MATCH", "Alphanumeric fallback: $line")
//                return line
//            }
//        }
//
//        Log.w("OCR_MATCH", "No transaction ID found in OCR text")
//        return null
//    }

//    private fun parseTransactionId(text: String): String? {
//        val lines = text.lines().map { it.trim() }.filter { it.isNotEmpty() }
//
//        // ── Blocklist: numbers that appear inside VPA/UPI handles ──────────
//        // e.g. "Vyapar.173239031382@hdfcbank" — the numeric part is an
//        // account/merchant ID, NOT a transaction reference. Strip these out
//        // before any pattern matching so they are never accidentally returned.
//        val vpaEmbeddedNumbers = mutableSetOf<String>()
//        val vpaRegex = Regex("""[A-Za-z0-9._%+\-]+@[A-Za-z0-9.\-]+""")
//        vpaRegex.findAll(text).forEach { match ->
//            // Extract any numeric substring of 8+ digits inside the VPA
//            Regex("""\d{8,}""").findAll(match.value).forEach { num ->
//                vpaEmbeddedNumbers.add(num.value)
//            }
//        }
//        // Also block phone numbers appearing as VPA prefix (e.g. 9049019178@apl)
//        Regex("""\d{10}@""").findAll(text).forEach { match ->
//            vpaEmbeddedNumbers.add(match.value.dropLast(1))
//        }
//        Log.d("OCR_BLOCK", "Blocked VPA numbers: $vpaEmbeddedNumbers")
//
//        // ── Priority 1: Label-then-next-line strategy ──────────────────────
//        // Extended to include Amazon Pay / bank reference labels
//        val upiLabels = listOf(
//            "upi transaction id",
//            "upi ref no",
//            "upi ref",
//            "utr no",
//            "utr number",
//            "utr",
//            "transaction ref",
//            "transaction reference",
//            "txn id",
//            "txn ref",
//            "ref no",
//            "reference id",
//            "payment ref",
//            // Amazon Pay specific
//            "bank reference id",
//            "bank ref id",
//            "bank reference no",
//            "amazon reference id",
//            "amazon ref id",
//            "order reference",
//            // Other apps
//            "approval code",
//            "rrn",
//            "retrieval reference"
//        )
//
//        for (i in lines.indices) {
//            val lower = lines[i].lowercase()
//            for (label in upiLabels) {
//                if (lower == label || lower.startsWith(label)) {
//                    // Value may be on the NEXT line OR same line after colon
//                    val nextLine = lines.getOrNull(i + 1) ?: continue
//
//                    // For Amazon Pay, the reference ID can be long alphanumeric
//                    // split across two lines — join them and clean
//                    val combined = nextLine.replace(" ", "")
//                    if (combined.matches(Regex("[A-Za-z0-9]{8,}")) &&
//                        !vpaEmbeddedNumbers.contains(combined)) {
//                        Log.d("OCR_MATCH", "Label '$label' → '$combined'")
//                        return combined
//                    }
//
//                    // Same-line colon value
//                    val afterColon = lines[i]
//                        .substringAfter(":", "")
//                        .trim()
//                        .replace(" ", "")
//                    if (afterColon.matches(Regex("[A-Za-z0-9]{8,}")) &&
//                        !vpaEmbeddedNumbers.contains(afterColon)) {
//                        return afterColon
//                    }
//                }
//            }
//        }
//
//        // ── Priority 1b: Two-line Amazon reference (label + partial + partial) ─
//        // Amazon Pay splits long ref IDs across TWO lines:
//        // Line N:   "APLAP6fb7d1444308dd8244648"
//        // Line N+1: "78a3a038e"
//        // Detect by finding a line that starts with "APL" or similar prefix
//        // and joining with the next line.
//        for (i in lines.indices) {
//            val lower = lines[i].lowercase()
//            if (lower.contains("amazon reference") || lower.contains("amazon ref")) {
//                // Grab next 1-2 lines and join
//                val part1 = lines.getOrNull(i + 1)?.replace(" ", "") ?: ""
//                val part2 = lines.getOrNull(i + 2)?.replace(" ", "") ?: ""
//                val joined = part1 + part2
//                if (joined.matches(Regex("[A-Za-z0-9]{10,}")) &&
//                    !vpaEmbeddedNumbers.contains(joined)) {
//                    Log.d("OCR_MATCH", "Amazon ref joined: $joined")
//                    return joined
//                }
//            }
//        }
//
//        // ── Priority 2: "Bank reference ID" value scan ─────────────────────
//        // Look for a line containing "bank reference" and extract the number
//        // on the SAME line or next line — prefer this over generic 12-digit match
//        for (i in lines.indices) {
//            val lower = lines[i].lowercase()
//            if (lower.contains("bank reference") || lower.contains("bank ref")) {
//                val candidate = lines.getOrNull(i + 1)?.replace(" ", "") ?: ""
//                if (candidate.matches(Regex("\\d{8,}")) &&
//                    !vpaEmbeddedNumbers.contains(candidate)) {
//                    Log.d("OCR_MATCH", "Bank reference ID: $candidate")
//                    return candidate
//                }
//                // Might be inline: "Bank reference ID  611765598915"
//                val inline = lower.replace(Regex("[^0-9]"), "")
//                if (inline.length in 8..15 && !vpaEmbeddedNumbers.contains(inline)) {
//                    return inline
//                }
//            }
//        }
//
//        // ── Priority 3: 12-digit standalone numeric (standard UTR) ─────────
//        val utrPattern = Regex("(?<!\\d)\\d{12}(?!\\d)")
//        utrPattern.findAll(text).forEach { match ->
//            if (!vpaEmbeddedNumbers.contains(match.value)) {
//                Log.d("OCR_MATCH", "12-digit UTR: ${match.value}")
//                return match.value
//            }
//        }
//
//        // ── Priority 4: PhonePe — T + 11 digits ────────────────────────────
//        val phonepeMatch = Regex("(?i)\\bT\\d{11}\\b").find(text)
//        if (phonepeMatch != null && !vpaEmbeddedNumbers.contains(phonepeMatch.value)) {
//            Log.d("OCR_MATCH", "PhonePe: ${phonepeMatch.value}")
//            return phonepeMatch.value.uppercase()
//        }
//
//        // ── Priority 5: Numeric 10–15 digits, not in blocklist ─────────────
//        Regex("(?<!\\d)\\d{10,15}(?!\\d)").findAll(text).forEach { match ->
//            if (!vpaEmbeddedNumbers.contains(match.value)) {
//                Log.d("OCR_MATCH", "Numeric fallback: ${match.value}")
//                return match.value
//            }
//        }
//
//        // ── Priority 6: Alphanumeric >= 10 chars, not VPA/CICA ─────────────
//        for (line in lines) {
//            val clean = line.replace(" ", "")
//            if (clean.matches(Regex("[A-Za-z0-9]{10,}")) &&
//                !clean.startsWith("CICA", ignoreCase = true) &&
//                !vpaEmbeddedNumbers.contains(clean) &&
//                !vpaRegex.matches(clean)) {
//                Log.d("OCR_MATCH", "Alphanumeric fallback: $clean")
//                return clean
//            }
//        }
//
//        Log.w("OCR_MATCH", "No transaction ID found")
//        return null
//    }

    private fun parseTransactionId(text: String): String? {
        val lines = text.lines().map { it.trim() }.filter { it.isNotEmpty() }

        // ══════════════════════════════════════════════════════════════════════
        // BLOCKLIST 1: UI button/label noise words — NEVER return these
        // Covers all payment apps: PhonePe, GPay, Paytm, Amazon Pay,
        // BHIM, CRED, MobiKwik, Airtel, WhatsApp Pay, bank apps
        // ══════════════════════════════════════════════════════════════════════
        val uiNoiseWords = setOf(
            // Navigation / buttons
            "viewhistory", "payagain", "resendsms", "splitexpense", "sharereceipt",
            "contactphonepesupport", "contactsupport", "paymore", "raisedispute",
            "rateexperience", "addtofavourites", "addfavorite", "paytocontacts",
            "downloadreceipt", "emailreceipt", "sharehelpfeedback", "helpandsupport",
            "helpsupport", "repeatpayment", "schedulepayment", "paymentoptions",
            // Section headings
            "paymentdetails", "transferdetails", "transactiondetails",
            "debitedto", "debitedfrom", "sentfrom", "paidto", "paidfrom",
            "credittoaccount", "paymentinfo", "receiptdetails",
            // Status strings
            "poweredbyupi", "poweredby", "transactionsuccessful",
            "paymentsucceeded", "paymentsuccess", "moneysent",
            "moneysentsuccessfully", "transfersuccessful", "paidsuccessfully",
            "transactioncomplete", "paymentcomplete", "paymentdone",
            "transactionapproved", "approved", "completed",
            // App names as standalone words (fine in group B but not as txn ID)
            "googlepay", "phonepe", "paytm", "amazonpay", "bhimupi",
            "whatsapppay", "credpay", "mobikwik", "airtelbank",
            // Misc noise
            "mudflap", "dateandtime", "andtime", "message", "remarks",
            "debitedfrm", "sentto", "sharecopy", "copyupiid",
            "viewreceipt", "gethelp", "reportissue", "disputetransaction"
        )

        // ══════════════════════════════════════════════════════════════════════
        // BLOCKLIST 2: Numbers inside VPA handles
        // e.g. Vyapar.173239031382@hdfcbank → block 173239031382
        //      9049019178@apl               → block 9049019178
        // ══════════════════════════════════════════════════════════════════════
        val vpaEmbeddedNumbers = mutableSetOf<String>()
        val vpaRegex = Regex("""[A-Za-z0-9._%+\-]+@[A-Za-z]{2,}""")
        vpaRegex.findAll(text).forEach { match ->
            Regex("""\d{6,}""").findAll(match.value).forEach { num ->
                vpaEmbeddedNumbers.add(num.value)
            }
        }
        Regex("""\d{10}@""").findAll(text).forEach {
            vpaEmbeddedNumbers.add(it.value.dropLast(1))
        }

        // ══════════════════════════════════════════════════════════════════════
        // BLOCKLIST 3: Masked account/card last digits
        // e.g. ****8154, XXXX9153, XXXXX106619 → block 8154, 9153, 106619
        // ══════════════════════════════════════════════════════════════════════
        val maskedAccountSuffixes = mutableSetOf<String>()
        Regex("""(?:\*{2,}|X{2,})\d{3,8}""", RegexOption.IGNORE_CASE)
            .findAll(text).forEach { match ->
                maskedAccountSuffixes.add(match.value.filter { it.isDigit() })
            }

        // ══════════════════════════════════════════════════════════════════════
        // BLOCKLIST 4: Date and time digit sequences
        // e.g. "27 Apr 2026, 3:10 PM" → block 27, 2026, 310, 3, 10
        // Covers: "28 Apr 2026", "28-04-2026", "04:19 PM", "08:45 pm"
        // ══════════════════════════════════════════════════════════════════════
        val dateTimeNoise = mutableSetOf<String>()
        // Named month format: "27 Apr 2026"
        Regex("""\d{1,2}\s+(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\s+\d{4}""",
            RegexOption.IGNORE_CASE).findAll(text).forEach { match ->
            Regex("""\d+""").findAll(match.value).forEach { d -> dateTimeNoise.add(d.value) }
        }
        // Numeric date format: "28-04-2026", "28/04/2026"
        Regex("""\d{2}[-/]\d{2}[-/]\d{4}""").findAll(text).forEach { match ->
            Regex("""\d+""").findAll(match.value).forEach { d -> dateTimeNoise.add(d.value) }
        }
        // Time format: "08:45", "3:10", "04:19"
        Regex("""\d{1,2}:\d{2}(?::\d{2})?(?:\s*[apAP][mM])?""").findAll(text).forEach { match ->
            Regex("""\d+""").findAll(match.value).forEach { d -> dateTimeNoise.add(d.value) }
        }
        // Also block standalone years
        Regex("""(?<!\d)20\d{2}(?!\d)""").findAll(text).forEach { dateTimeNoise.add(it.value) }

        // ══════════════════════════════════════════════════════════════════════
        // BLOCKLIST 5: Amount values
        // e.g. "₹1,470", "Rs 5565.00", "₹22,794" → block 1470, 5565, 22794
        // ══════════════════════════════════════════════════════════════════════
        val amountNoise = mutableSetOf<String>()
        Regex("""(?:₹|Rs\.?|INR)\s*[\d,]+(?:\.\d{1,2})?""", RegexOption.IGNORE_CASE)
            .findAll(text).forEach { match ->
                val digits = match.value.filter { it.isDigit() }
                if (digits.isNotEmpty()) amountNoise.add(digits)
                // Also add without trailing zeros for decimal amounts
                amountNoise.add(match.value.replace(Regex("[₹Rs.,INR\\s]", RegexOption.IGNORE_CASE), "")
                    .replace(".00", "").filter { it.isDigit() })
            }

        // ══════════════════════════════════════════════════════════════════════
        // MASTER noise check — call before returning ANY candidate
        // ══════════════════════════════════════════════════════════════════════
        fun isNoise(value: String): Boolean {
            val lower = value.lowercase().replace(" ", "")
            val digitsOnly = value.filter { it.isDigit() }
            return uiNoiseWords.any { lower == it || lower.contains(it) } ||
                    vpaEmbeddedNumbers.contains(value) ||
                    maskedAccountSuffixes.contains(value) ||
                    dateTimeNoise.contains(value) ||
                    amountNoise.contains(digitsOnly) ||
                    amountNoise.contains(value) ||
                    // Block pure years
                    value.matches(Regex("20[0-9]{2}")) ||
                    // Block short pure-digit noise (time fragments, day numbers)
                    (value.all { it.isDigit() } && value.length <= 4) ||
                    // Block standalone amounts like "776", "5565"
                    (value.all { it.isDigit() } && value.length in 1..6 &&
                            text.contains("₹$value") || text.contains("Rs $value",true))
        }

        // ══════════════════════════════════════════════════════════════════════
        // UNIVERSAL LABEL MAP
        // Works for ALL payment apps — scans for a label then reads its value
        // from same line (after colon) OR next line OR joined next-2 lines
        //
        // PhonePe:    "Transaction ID", "UTR:"
        // Google Pay: "UPI transaction ID", "UPI Ref No"
        // Paytm:      "Transaction ID", "Order ID", "Txn ID"
        // Amazon Pay: "Bank reference ID", "Amazon reference ID"
        // BHIM:       "Transaction Ref No", "UTR No"
        // CRED:       "Reference ID", "Txn Ref"
        // MobiKwik:   "Transaction ID", "Ref No"
        // WhatsApp:   "UPI Ref No", "Transaction ID"
        // Bank apps:  "RRN", "Approval Code", "Reference No"
        // Federal:    "Ref No", "UTR Number"
        // SBI YONO:   "Transaction Reference", "UTR"
        // HDFC:       "Transaction ID", "Reference Number"
        // ICICI:      "Transaction ID", "Unique Reference No"
        // ══════════════════════════════════════════════════════════════════════
        val universalLabels = listOf(
            // UTR / UPI reference labels (highest confidence)
            "upi transaction id", "upi transaction id:",
            "upi ref no", "upi ref no:", "upi ref:", "upi ref",
            "utr no", "utr no:", "utr number", "utr number:", "utr:", "utr",
            // Transaction ID labels
            "transaction id", "transaction id:",
            "transaction ref no", "transaction ref no:",
            "transaction ref", "transaction ref:",
            "transaction reference", "transaction reference:",
            "transaction reference number", "txn id", "txn id:",
            "txn ref", "txn ref:", "txnid",
            // Reference labels
            "ref no", "ref no:", "reference no", "reference no:",
            "reference id", "reference id:", "reference number",
            "reference number:", "payment ref", "payment ref:",
            "payment reference", "payment reference:",
            // Bank-specific labels
            "bank reference id", "bank reference id:",
            "bank ref id", "bank ref no", "bank reference no",
            "bank reference number",
            // Amazon Pay specific
            "amazon reference id", "amazon reference id:",
            "amazon ref id", "amazon ref no",
            // Other app-specific
            "order id", "order no", "order reference",
            "approval code", "approval no",
            "rrn", "rrn:", "retrieval reference",
            "unique reference no", "unique ref no",
            "imps ref no", "imps ref", "neft ref",
            "payment id", "payment id:"
        )

        // ── PASS 1: Label scan — same line (value after colon) ─────────────
        for (i in lines.indices) {
            val lower = lines[i].lowercase().trim()
            for (label in universalLabels) {
                if (lower == label || lower.startsWith("$label ") ||
                    lower.startsWith("$label:")) {

                    // Value on SAME line after colon or space
                    val afterLabel = lines[i]
                        .substringAfter(":", "")
                        .trim()
                        .replace(" ", "")
                    if (afterLabel.length >= 8 &&
                        afterLabel.matches(Regex("[A-Za-z0-9]+"))) {
                        // Extra check: must contain digits (real IDs always have digits)
                        if (afterLabel.any { it.isDigit() } && !isNoise(afterLabel)) {
                            Log.d("OCR_MATCH", "Label '$label' same-line: $afterLabel")
                            return afterLabel
                        }
                    }

                    // Value on NEXT line
                    val nextLine = lines.getOrNull(i + 1)
                        ?.trim()?.replace(" ", "") ?: continue
                    if (nextLine.length >= 8 &&
                        nextLine.matches(Regex("[A-Za-z0-9]+")) &&
                        nextLine.any { it.isDigit() } &&
                        !isNoise(nextLine)) {
                        Log.d("OCR_MATCH", "Label '$label' next-line: $nextLine")
                        return nextLine
                    }

                    // Value SPLIT across next 2 lines (Amazon Pay long ref ID)
                    val part2 = lines.getOrNull(i + 2)
                        ?.trim()?.replace(" ", "") ?: ""
                    val joined = nextLine + part2
                    if (joined.length >= 16 &&
                        joined.matches(Regex("[A-Za-z0-9]+")) &&
                        joined.any { it.isDigit() } &&
                        !isNoise(joined)) {
                        Log.d("OCR_MATCH", "Label '$label' joined: $joined")
                        return joined
                    }
                }
            }
        }

        // ── PASS 2: Inline label+value on same line without colon ──────────
        // Handles "UTR 423166258205", "Ref No 378587790170"
        for (i in lines.indices) {
            val lower = lines[i].lowercase()
            for (label in universalLabels) {
                if (lower.contains(label)) {
                    // Extract trailing alphanumeric after the label
                    val afterLabel = lines[i]
                        .substring(lower.indexOf(label) + label.length)
                        .trim()
                        .replace(Regex("[^A-Za-z0-9]"), "")
                    if (afterLabel.length >= 8 &&
                        afterLabel.any { it.isDigit() } &&
                        !isNoise(afterLabel)) {
                        Log.d("OCR_MATCH", "Inline label '$label': $afterLabel")
                        return afterLabel
                    }
                }
            }
        }

        // ── PASS 3: PhonePe Transaction ID — T + 18–22 digits ──────────────
        // e.g. T260428204549821323189
        Regex("""(?i)\bT\d{18,22}\b""").findAll(text).forEach { match ->
            val v = match.value.uppercase()
            if (!isNoise(v)) {
                Log.d("OCR_MATCH", "PhonePe TxnID: $v")
                return v
            }
        }

        // ── PASS 4: UTR inline pattern — "UTR: 423166258205" ───────────────
        Regex("""(?i)UTR[:\s#]*([A-Za-z0-9]{8,15})""").findAll(text).forEach { match ->
            val v = match.groupValues[1]
            if (!isNoise(v)) {
                Log.d("OCR_MATCH", "UTR inline pattern: $v")
                return v
            }
        }

        // ── PASS 5: 12-digit standalone numeric (universal UTR format) ──────
        Regex("""(?<!\d)\d{12}(?!\d)""").findAll(text).forEach { match ->
            val v = match.value
            if (!isNoise(v) && !vpaEmbeddedNumbers.contains(v)) {
                Log.d("OCR_MATCH", "12-digit UTR: $v")
                return v
            }
        }

        // ── PASS 6: 10–15 digit numeric not in any blocklist ───────────────
        Regex("""(?<!\d)\d{10,15}(?!\d)""").findAll(text).forEach { match ->
            val v = match.value
            if (!isNoise(v) && !vpaEmbeddedNumbers.contains(v) &&
                !maskedAccountSuffixes.contains(v) && !amountNoise.contains(v)) {
                Log.d("OCR_MATCH", "Numeric 10-15: $v")
                return v
            }
        }

        // ── PASS 7: Alphanumeric — must have 3+ digits, 1+ letters ─────────
        // Guards against returning plain English words or button labels
        for (line in lines) {
            val clean = line.trim().replace(" ", "")
            val digitCount = clean.count { it.isDigit() }
            val letterCount = clean.count { it.isLetter() }
            if (clean.length in 10..35 &&
                digitCount >= 3 &&
                letterCount >= 1 &&
                clean.matches(Regex("[A-Za-z0-9]+")) &&
                !clean.startsWith("CICA", ignoreCase = true) &&
                !isNoise(clean) &&
                !vpaRegex.matches(clean)) {
                Log.d("OCR_MATCH", "Alphanumeric fallback: $clean")
                return clean
            }
        }

        Log.w("OCR_MATCH", "No transaction ID found")
        return null
    }
    // -----------------------------------------------------------------------
    // DATE FORMAT
    // -----------------------------------------------------------------------

    private fun formatDateTime(dateTime: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
            val outputDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val outputTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(dateTime)
            "${date?.let { outputDateFormat.format(it) }} ${date?.let { outputTimeFormat.format(it) }}"
        } catch (e: Exception) {
            dateTime
        }
    }

    // -----------------------------------------------------------------------
    // FETCH DATA
    // -----------------------------------------------------------------------

    private fun fetchInvData() {
        val invNo = invoiceNumberField.text.toString()
        val progressDialog = showProgressDialog()
        val client = OkHttpClient()
        val url = ApiFile.DELIVERY_APP_URL + "/vehDelvTrans/getByGatePassId?attribute1=$invNo&location=$location"
        Log.d("URL:", url)

        val request = Request.Builder().url(url).build()

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
                        progressDialog.dismiss()
                        when (code) {
                            400 -> {
                                val obj = jsonObject.getString("obj")
                                Toast.makeText(this@VehicleDelivery, "$obj-$message", Toast.LENGTH_LONG).show()
                            }
                            200 -> {
                                val objArray = jsonObject.optJSONArray("obj")
                                if (objArray != null && objArray.length() > 0) {
                                    populateFields(buildInvoiceData(objArray.getJSONObject(0)))
                                    Toast.makeText(this@VehicleDelivery, "Details found for Gate Pass No: $invNo", Toast.LENGTH_LONG).show()
                                    fetchPaymentModes()
                                    verifyOTPButton.setBackgroundColor(ContextCompat.getColor(this@VehicleDelivery, R.color.teal_700))
                                    verifyOTPButton.isEnabled = true
                                    paymentIdSpinner.isEnabled = true
                                    paymentModeSpinner.isEnabled = true
                                } else {
                                    Toast.makeText(this@VehicleDelivery, "No details found for Gate Pass No: $invNo", Toast.LENGTH_LONG).show()
                                }
                            }
                            else -> Toast.makeText(this@VehicleDelivery, "Unexpected response: $message", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    progressDialog.dismiss()
                    Toast.makeText(this@VehicleDelivery, "Failed to fetch details. Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun vehicleNoData() {
        val vehNo = invoiceNumberField.text.toString()
        val progressDialog = showProgressDialog()
        val client = OkHttpClient()
        val url = ApiFile.DELIVERY_APP_URL + "/vehDelvTrans/getByVehicleNo?vehicleNo=$vehNo&location=$location"
        Log.d("URL:", url)

        val request = Request.Builder().url(url).build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    val code = jsonObject.getInt("code")
                    val message = jsonObject.getString("message")

                    runOnUiThread {
                        progressDialog.dismiss()
                        when (code) {
                            400 -> {
                                val obj = jsonObject.getString("obj")
                                Toast.makeText(this@VehicleDelivery, "$obj-$message", Toast.LENGTH_LONG).show()
                            }
                            200 -> {
                                val objArray = jsonObject.optJSONArray("obj")
                                if (objArray != null && objArray.length() > 0) {
                                    populateFields(buildInvoiceData(objArray.getJSONObject(0)))
                                    Toast.makeText(this@VehicleDelivery, "Details found for Vehicle No: $vehNo", Toast.LENGTH_LONG).show()
                                    fetchPaymentModes()
                                    verifyOTPButton.setBackgroundColor(ContextCompat.getColor(this@VehicleDelivery, R.color.teal_700))
                                    verifyOTPButton.isEnabled = true
                                    paymentIdSpinner.isEnabled = true
                                    paymentModeSpinner.isEnabled = true
                                } else {
                                    Toast.makeText(this@VehicleDelivery, "No details found for Vehicle No: $vehNo", Toast.LENGTH_LONG).show()
                                }
                            }
                            else -> Toast.makeText(this@VehicleDelivery, "Unexpected response: $message", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    progressDialog.dismiss()
                    Toast.makeText(this@VehicleDelivery, "Failed to fetch details. Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun buildInvoiceData(stockItem: JSONObject) = InvoiceNoData(
        VEHICLE_NO = stockItem.optString("VEHICLE_NO"),
        TRANS_REF_NUM = stockItem.optString("TRANS_REF_NUM"),
        CUSTOMER_ADDRESS = stockItem.optString("CUSTOMER_ADDRESS"),
        CUSTOMER_NAME = stockItem.optString("CUSTOMER_NAME"),
        DATE_OF_DELIVERY = stockItem.optString("DATE_OF_DELIVERY"),
        CONTACT_NO = stockItem.optString("CONTACT_NO"),
        AMOUNT_DUE_REMAINING = stockItem.optDouble("AMOUNT_DUE_REMAINING", 0.0),
        INVOICE_NO = stockItem.optString("INVOICE_NO"),
        EXECUTIVE = stockItem.optString("EXECUTIVE"),
        GATE_PASS_ID = stockItem.optString("GATE_PASS_ID"),
        SERVICE_LOCATION = stockItem.optString("SERVICE_LOCATION"),
        MODEL = stockItem.optString("MODEL"),
        PARTY_ID = stockItem.getInt("PARTY_ID")
    )

    // -----------------------------------------------------------------------
    // SAVE INVOICE DETAILS
    // -----------------------------------------------------------------------

    private fun saveInvoiceDetails() {
        stopAmountRefresh()
        val progressDialog = showProgressDialog()
        val invNo = invNumber
        val otp = otpInputField.text.toString()
        val payTransNumber = upiInputTextField.text.toString()
        val custContactNo = customerPaymentSuccessMessageContactNo
        val remainingAmount = amountRemainingTxtView.text.toString().split(": ")[1]
        val remainAmt = remainingAmount.toFloat()
        val amtPaidByUser = amountPaidTxtField.text.toString()
        val amtToBePaid = amtPaidByUser.toFloat()
        val altMobileValSend = alternateMobileField.text.toString().trim()
        val recMethodId = paymentIdSpinner.selectedItem.toString()

        if (recMethodId=="Select Payment ID"){
            Toast.makeText(this,"Please select the Payment Method.",Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()
            detailsSubmit.isEnabled = true
            return
        }

        // Block submission if UPI/non-cash payment has no transaction number
        val selectedMode = paymentModeSpinner.selectedItem?.toString() ?: ""
        val isCashOrCard = selectedMode.equals("Cash-CASH", ignoreCase = true) ||
                selectedMode
                    .equals("Credit Card-CREDIT_CARD", ignoreCase = true)

        if (!isCashOrCard && payTransNumber.isBlank()) {
            Toast.makeText(
                this,
                "Transaction reference number is required for UPI/online payments.",
                Toast.LENGTH_LONG
            ).show()
            progressDialog.dismiss()
            detailsSubmit.isEnabled = true
            return
        }


//        if (!isCashOrCard && payTransNumber.isBlank()) {
//            Toast.makeText(
//                this,
//                "Transaction reference number is required for UPI/online payments.",
//                Toast.LENGTH_LONG
//            ).show()
//            progressDialog.dismiss()
//            detailsSubmit.isEnabled = true
//            return
//        }
//
//        if (!isCashOrCard && (payTransNumber.length < 12 || payTransNumber.length > 35)) {
//            Toast.makeText(
//                this,
//                "Transaction reference number must be between 12 and 35 characters.",
//                Toast.LENGTH_LONG
//            ).show()
//            progressDialog.dismiss()
//            detailsSubmit.isEnabled = true
//            return
//        }

        if (amtPaidByUser.toFloat()<=0){
            Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_LONG).show()
            progressDialog.dismiss()
            detailsSubmit.isEnabled = true
            return
        }

        if (amtToBePaid > remainAmt) {
            Toast.makeText(this, "Entered amount is greater than remaining amount.", Toast.LENGTH_LONG).show()
            progressDialog.dismiss()
            detailsSubmit.isEnabled = true
            return
        }

        val otp2 = otp.toIntOrNull()
        if (otp2 == null) {
            Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()
            detailsSubmit.isEnabled = true
            return
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val url = ApiFile.DELIVERY_APP_URL + "/vehDelvTrans/delvPaymentComplete"

        // Build multipart body — backend uses @ModelAttribute + @RequestParam MultipartFile
        // Every field must be a separate form-data part (NOT a JSON body)
        val multipartBuilder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("invoiceNo",            invNo)
            .addFormDataPart("location",             location)
            .addFormDataPart("paymentType",          paymentType2)
            .addFormDataPart("receiptMethodId",      recMethodId)
            .addFormDataPart("otp",                  otp2.toString())
            .addFormDataPart("paymentTransactionNo", payTransNumber)
            .addFormDataPart("amountPaid",           amtToBePaid.toString())
            .addFormDataPart("ouId",                 ouId.toString())
            .addFormDataPart("department",           deptName)
            .addFormDataPart("driverName",           "$userName-$login_name")
            .addFormDataPart("driverLocationName",   location_name)
            .addFormDataPart("partyId",              partyId.text.toString().split(": ")[1])
            .addFormDataPart("attribute1",           gatePassId)
            .addFormDataPart("driverContactNo",      userContact)
            .addFormDataPart("attribute2",           altMobileValSend)
            .addFormDataPart("attribute3",           "")
            .addFormDataPart("attribute4",           "")
            .addFormDataPart("attribute5",           "")
            .addFormDataPart("driverLocId",          locId.toString())

        // Attach image file if captured
        // For UPI: backend returns 400 if file is missing (UI already enforces capture before submit)
        // For Cash/Card: backend accepts null file, so omitting is fine
        val currentPhotoFile = photoFile
        if (currentPhotoFile != null && currentPhotoFile.exists()) {
            val imageBytes = currentPhotoFile.readBytes()
            val imageRequestBody = imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
            multipartBuilder.addFormDataPart("file", currentPhotoFile.name, imageRequestBody)
            Log.d("SaveVinData", "Attaching image: ${currentPhotoFile.name} (${imageBytes.size} bytes)")
        } else {
            Log.d("SaveVinData", "No image attached")
        }

        val requestBody = multipartBuilder.build()
        Log.d("SaveVinData", "Posting multipart to: $url | paymentType=$paymentType2 | invoiceNo=$invNo")

        val request = Request.Builder().url(url).post(requestBody).build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseCode = response.code
                val responseBodyString = response.body?.string()
                Log.d("SaveVinData", "Response Code: $responseCode")
                Log.d("SaveVinData", "Response Body: $responseBodyString")

                runOnUiThread {
                    progressDialog.dismiss()
                    if (!responseBodyString.isNullOrEmpty()) {
                        try {
                            val responseJson = JSONObject(responseBodyString)
                            val message = responseJson.optString("message", "")
                            when {
                                message.contains("Payment Already Done", ignoreCase = true) -> {
                                    detailsSubmit.isEnabled = true
                                    Toast.makeText(this@VehicleDelivery, "Payment Already Done For This Invoice", Toast.LENGTH_LONG).show()
                                    resetFields()
                                }
                                responseCode == 200 -> {
                                    Toast.makeText(this@VehicleDelivery, "Data saved successfully", Toast.LENGTH_LONG).show()
                                    // Capture UI values NOW before resetFields clears them
                                    val vehicleNo   = vehicleNumberTxtView.text.toString().substringAfter(": ")
                                    val custName    = nameTxtView.text.toString().substringAfter(": ")
                                    val payMethod   = paymentIdSpinner.selectedItem?.toString() ?: ""
                                    val otpVal      = otpInputField.text.toString()
                                    val amtVal      = amountPaidTxtField.text.toString().toDoubleOrNull() ?: 0.0
                                    val altMobileVal = alternateMobileField.text.toString().trim()
                                    // Reset UI immediately so the screen is clean
                                    resetFields()
                                    // Fire SMS with pre-captured values (no UI reads inside)
                                    paymentSuccessMessage(vehicleNo, custName, payMethod, otpVal, amtVal, altMobileVal)

                                    showPaymentSuccessDialog(vehicleNo, custName, payMethod, amtVal)
                                }
                                responseCode == 400 -> {
                                    detailsSubmit.isEnabled = true
                                    Toast.makeText(this@VehicleDelivery, message, Toast.LENGTH_LONG).show()
                                }
                                else -> {
                                    detailsSubmit.isEnabled = true
                                    Toast.makeText(this@VehicleDelivery, "Failed to save data. Error code: $responseCode", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            detailsSubmit.isEnabled = true
                            Toast.makeText(this@VehicleDelivery, "Error parsing server response", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        detailsSubmit.isEnabled = true
                        Toast.makeText(this@VehicleDelivery, "Empty response from server", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("SaveData", "Error: ${Log.getStackTraceString(e)}")
                runOnUiThread {
                    progressDialog.dismiss()
                    detailsSubmit.isEnabled = true
                    Toast.makeText(this@VehicleDelivery, "Error saving data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun paymentSuccessMessage(
        vehicleNo: String,
        custName: String,
        payMethod: String,
        otp: String,
        amtToBePaid: Double,
        altMobile: String
    ) {
        // All values passed as params — no UI reads so resetFields() timing is irrelevant
//        val custContactNo      = customerPaymentSuccessMessageContactNo
        val custContactNo = sendMsgToOGNo
        val capturedGatePassId = gatePassId
        val capturedPayType    = paymentType2
        val capturedOuId       = ouId

        cityConverter = when (capturedOuId) {
            104 -> "Mumbai"
            81  -> "Pune"
            105 -> "Kolhapur"
            106 -> "Goa"
            107 -> "Cochin"
            108 -> "Hyderabad"
            else -> ""
        }

        val client = OkHttpClient()
        val url = ApiFile.DELIVERY_APP_URL + "/vehDelvTrans/sendPaymentSuccessSms"
        val jsonObject = JSONObject()

        jsonObject.put("trxNumber",      capturedGatePassId)
        jsonObject.put("instanceNumber", vehicleNo)
        jsonObject.put("partyName",      custName)
        jsonObject.put("accountType",    payMethod)
        jsonObject.put("mobileNo",       custContactNo)
        jsonObject.put("orgId",          capturedOuId)
        jsonObject.put("amount",         amtToBePaid)
        jsonObject.put("method",         capturedPayType)
        jsonObject.put("city",           cityConverter)
        jsonObject.put("secondMobileNo", altMobile)

        Log.d("PaySuccessSMS", "Sending SMS → vehicleNo=$vehicleNo amount=$amtToBePaid contact=$custContactNo altrnateMobile=$altMobile")

        val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder().url(url).post(requestBody).build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseCode = response.code
                val responseBody = response.body?.string()
                Log.d("PaySuccessSMS", "Response: $responseCode | $responseBody")
                // Payment is already confirmed — SMS is best-effort, no toast on failure
            } catch (e: Exception) {
                Log.e("PaySuccessSMS", "SMS send failed: ${e.message}")
            }
        }
    }
    // -----------------------------------------------------------------------
    // OTP
    // -----------------------------------------------------------------------

    private fun generateOTP() {
        val invNo = invNumber
        val altMobile = alternateMobileField.text.toString().trim()

        if (invNo.isEmpty()) {
            Toast.makeText(this, "Invoice Number cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate alternate mobile if entered — must be exactly 10 digits
        if (altMobile.isNotEmpty()) {
            if (!altMobile.matches(Regex("""^[6-9]\d{9}$"""))) {
                alternateMobileField.error = "Enter a valid 10-digit Indian mobile number"
                alternateMobileField.requestFocus()
                Toast.makeText(
                    this,
                    "Alternate mobile number must be a valid 10-digit number starting with 6, 7, 8 or 9.",
                    Toast.LENGTH_LONG
                ).show()
                return
            }
        }

        val contactNo = if (altMobile.isNotEmpty()) altMobile else customerPaymentSuccessMessageContactNo

        val client = OkHttpClient()
        val url = "${ApiFile.DELIVERY_APP_URL}/vehDelvOtp/generateDelvOtp?invoiceNo=$invNo&mobileNo=$contactNo"

        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create(null, byteArrayOf()))
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseCode = response.code
                val responseBody = response.body?.string()
                Log.d("generateOTP", "Response Code: $responseCode, Body: $responseBody")

                runOnUiThread {
                    if (responseBody != null) {
                        try {
                            val jsonResponse = JSONObject(responseBody)
                            val message = jsonResponse.optString("message", "")
                            when {
                                message.contains("Payment Already Done For This Invoice", ignoreCase = true) ->
                                    Toast.makeText(this@VehicleDelivery, "Payment Already Done For This Invoice", Toast.LENGTH_LONG).show()
                                responseCode == 200 ->
                                    Toast.makeText(
                                        this@VehicleDelivery,
                                        "OTP sent to $contactNo.\nValid for 3 minutes only.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                else ->
                                    Toast.makeText(this@VehicleDelivery, "Failed to generate OTP. Error: $responseCode", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(this@VehicleDelivery, "Error processing response: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@VehicleDelivery, "No response from server", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@VehicleDelivery, "Error generating OTP: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateOTP() {
        val otp = otpInputField.text.toString()
        val inv = invNumber

        if (otp.isEmpty()) {
            Toast.makeText(this, "OTP cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val client = OkHttpClient()
        val url = "${ApiFile.DELIVERY_APP_URL}/vehDelvOtp/validateDelvOtp?invoiceNo=$inv&otp=$otp&status=SEND"
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
                Log.d("generateOTP", "Response Code: $responseCode, Body: $responseBody")

                runOnUiThread {
                    if (responseBody != null) {
                        try {
                            val jsonResponse = JSONObject(responseBody)
                            val code = jsonResponse.optInt("code", -1)
                            val message = jsonResponse.optString("message", "")
                            when (code) {
                                400 -> Toast.makeText(this@VehicleDelivery, "OTP IS INVALID OR EXPIRED", Toast.LENGTH_LONG).show()
                                200 -> {
                                    Toast.makeText(this@VehicleDelivery, "Validation successful", Toast.LENGTH_LONG).show()
                                    detailsSubmit.visibility = View.VISIBLE
                                }
                                else -> Toast.makeText(this@VehicleDelivery, "Unexpected response: $message", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(this@VehicleDelivery, "Error processing response: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@VehicleDelivery, "No response from server", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@VehicleDelivery, "Error validating OTP: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // -----------------------------------------------------------------------
    // AMOUNT REFRESH
    // -----------------------------------------------------------------------

    private fun startAmountRefresh() {
        if (isRefreshing) return
        isRefreshing = true
        amountRefreshHandler = Handler(Looper.getMainLooper())
        amountRefreshRunnable = object : Runnable {
            override fun run() {
                if (isRefreshing) {
                    refreshAmountDueRemaining()
                    amountRefreshHandler?.postDelayed(this, 1000)
                }
            }
        }
        amountRefreshHandler?.post(amountRefreshRunnable!!)
    }

    private fun stopAmountRefresh() {
        isRefreshing = false
        amountRefreshRunnable?.let { amountRefreshHandler?.removeCallbacks(it) }
        amountRefreshHandler = null
        amountRefreshRunnable = null
    }

    private fun refreshAmountDueRemaining() {
        val invNo = invoiceNumberField.text.toString()
        if (invNo.isEmpty()) return

        val client = OkHttpClient()
        val url = if (searchButton2.visibility == View.VISIBLE)
            "${ApiFile.DELIVERY_APP_URL}/vehDelvTrans/getByVehicleNo?vehicleNo=$invNo&location=$location"
        else
            "${ApiFile.DELIVERY_APP_URL}/vehDelvTrans/getByGatePassId?attribute1=$invNo&location=$location"

        val request = Request.Builder().url(url).build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    if (jsonObject.getInt("code") == 200) {
                        val objArray = jsonObject.optJSONArray("obj")
                        if (objArray != null && objArray.length() > 0) {
                            val newAmount = objArray.getJSONObject(0).optDouble("AMOUNT_DUE_REMAINING", 0.0)
                            runOnUiThread {
                                val newAmountStr = newAmount.toBigDecimal().stripTrailingZeros().toPlainString()
                                amountRemainingTxtView.text = "Balance Amount: $newAmountStr"
                                amountRemaining = newAmountStr
                                if (newAmount > 0) amountPaidTxtField.setText(newAmountStr)
                                Log.d("AmountRefresh", "Updated amount: $newAmount")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("AmountRefresh", "Error refreshing amount: ${e.message}")
            }
        }
    }

    // -----------------------------------------------------------------------
    // POPULATE / RESET FIELDS
    // -----------------------------------------------------------------------


    private fun populateFields(data: InvoiceNoData) {
        invoiceNumberTxtView.text = "Invoice No.: ${data.INVOICE_NO}"
        vehicleNumberTxtView.text = "Vehicle No.: ${data.VEHICLE_NO}"
        gatePassNumberTxtView.text = "Gate Pass No.: ${data.GATE_PASS_ID}"
        addressTxtView.text = "Address: ${data.CUSTOMER_ADDRESS}"
        nameTxtView.text = "Name: ${data.CUSTOMER_NAME}"
        // contactNoTxtView is now a TextView — display only
        contactNoTxtView.text = "Customer Mobile: ${data.CONTACT_NO}"
        sendMsgToOGNo = data.CONTACT_NO
        transactionNoTxtView.text = "Transaction Ref No.: ${data.TRANS_REF_NUM}"
        transactionNoDateTxtView.text = "Delivery Date: ${formatDateTime(data.DATE_OF_DELIVERY)}"
        transactionDate = data.DATE_OF_DELIVERY
        amountRemainingTxtView.text = "Balance Amount: ${data.AMOUNT_DUE_REMAINING.toBigDecimal().stripTrailingZeros().toPlainString()}"
        serviceExecutive.text = "Service Executive: ${data.EXECUTIVE}"
        modelTextView.text = "Model: ${data.MODEL}"
        partyId.text = "Customer Id: ${data.PARTY_ID}"
        amountPaidTxtField.setText(data.AMOUNT_DUE_REMAINING.toBigDecimal().stripTrailingZeros().toPlainString())
        invNumber = data.INVOICE_NO
        gatePassId = data.GATE_PASS_ID
        customerPaymentSuccessMessageContactNo = data.CONTACT_NO
        amountRemaining = data.AMOUNT_DUE_REMAINING.toBigDecimal().stripTrailingZeros().toPlainString()

        listOf(
            vehicleNumberTxtView, gatePassNumberTxtView, modelTextView, addressTxtView,
            partyId, nameTxtView, contactNoTxtView, transactionNoTxtView,
            transactionNoDateTxtView, invoiceNumberTxtView, amountRemainingTxtView,
            serviceExecutive
        ).forEach { it.visibility = View.VISIBLE }

        val pendingAmount = amountRemaining.toFloat()
        if (pendingAmount <= 0) {
            invoiceDataLL.visibility = View.GONE
        } else {
            invoiceDataLL.visibility = View.VISIBLE
            upiInputTextField.visibility = View.GONE
            upiText.visibility = View.GONE
            upiImageSection.visibility = View.GONE
        }

        // Show alternate mobile field whenever customer data is loaded
        alternateMobileLayout.visibility = View.VISIBLE
    }

    private fun resetFields() {
        invoiceNumberField.setText("")
        listOf(
            vehicleNumberTxtView, gatePassNumberTxtView, addressTxtView, nameTxtView,
            contactNoTxtView, transactionNoTxtView, transactionNoDateTxtView,
            amountRemainingTxtView, invoiceNumberTxtView, serviceExecutive, modelTextView, partyId
        ).forEach {
            it.text = ""
            it.visibility = View.GONE
        }
        otpInputField.setText("")
        amountPaidTxtField.setText("")
        upiInputTextField.setText("")
        alternateMobileField.setText("")
        detailsSubmit.visibility = View.GONE
        sendOTP.isEnabled = false
        sendOTP.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
        verifyOTPButton.isEnabled = false
        verifyOTPButton.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
        paymentIdSpinner.isEnabled = false
        paymentModeSpinner.isEnabled = false
        resetSpinner()
        resetSpinner2()
        invoiceDataLL.visibility = View.GONE
        detailsSubmit.isEnabled = true
        searchButton2.visibility = View.GONE
        searchButton.visibility = View.GONE
        invoiceNumberField.visibility = View.GONE
        noShowingTextView.text = ""
        clearFields.visibility = View.GONE
        customerPaymentSuccessMessageContactNo = ""
        alternateMobileLayout.visibility = View.GONE
        upiImageSection.visibility = View.GONE
        imageView.setImageResource(R.drawable.imagehint)
        imageView.visibility = View.GONE
        photoUri = null
        photoFile = null
        stopAmountRefresh()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAmountRefresh()
    }

    fun resetSpinner() {
        val adapter = paymentModeSpinner.adapter as? ArrayAdapter<String>
        adapter?.apply {
            clear()
            notifyDataSetChanged()
        }
        paymentModeSpinner.setSelection(0)
    }

    fun resetSpinner2() {
        val adapter2 = paymentIdSpinner.adapter as? ArrayAdapter<String>
        adapter2?.apply {
            clear()
            notifyDataSetChanged()
        }
        paymentIdSpinner.setSelection(0)
    }

    private fun showProgressDialog(): AlertDialog {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_progress, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()
        return dialog
    }

    private fun logout() {
        SessionManager.clearSession(this)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    data class InvoiceNoData(
        val VEHICLE_NO: String,
        val TRANS_REF_NUM: String,
        val CUSTOMER_ADDRESS: String,
        val CUSTOMER_NAME: String,
        val DATE_OF_DELIVERY: String,
        val CONTACT_NO: String,
        val AMOUNT_DUE_REMAINING: Double,
        val INVOICE_NO: String,
        val EXECUTIVE: String,
        val GATE_PASS_ID: String,
        val SERVICE_LOCATION: String,
        val MODEL: String,
        val PARTY_ID: Int
    )

    private fun showPaymentSuccessDialog(
        vehicleNo: String,
        custName: String,
        payMethod: String,
        amtPaid: Double
    ) {
        Log.d("SuccessDialog", "showPaymentSuccessDialog called — vehicle=$vehicleNo amt=$amtPaid")
        val dialog = AlertDialog.Builder(this, R.style.TransparentDialog).create()
        val view = layoutInflater.inflate(R.layout.dialog_payment_success, null)
        dialog.setView(view)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Cast rows as ViewGroup, then find children inside
        val rowVehicle   = view.findViewById<android.widget.LinearLayout>(R.id.rowVehicle)
        val rowCust      = view.findViewById<android.widget.LinearLayout>(R.id.rowCust)
        val rowPayMethod = view.findViewById<android.widget.LinearLayout>(R.id.rowPayMethod)

        rowVehicle?.findViewById<TextView>(R.id.receiptLabel)?.text   = "Vehicle No."
        rowVehicle?.findViewById<TextView>(R.id.receiptValue)?.text   = vehicleNo.ifEmpty { "N/A" }

        rowCust?.findViewById<TextView>(R.id.receiptLabel)?.text      = "Customer"
        rowCust?.findViewById<TextView>(R.id.receiptValue)?.text      = custName.ifEmpty { "N/A" }

        rowPayMethod?.findViewById<TextView>(R.id.receiptLabel)?.text = "Payment Method"
        rowPayMethod?.findViewById<TextView>(R.id.receiptValue)?.text = payMethod.ifEmpty { "N/A" }

        view.findViewById<TextView>(R.id.successAmount)?.text = "₹ ${"%.2f".format(amtPaid)}"

        // Animate check circle
        val checkCircle = view.findViewById<View>(R.id.successCheckCircle)
        checkCircle?.scaleX = 0f
        checkCircle?.scaleY = 0f
        checkCircle?.animate()
            ?.scaleX(1f)?.scaleY(1f)
            ?.setDuration(500)
            ?.setInterpolator(android.view.animation.OvershootInterpolator(1.8f))
            ?.start()

        // Confetti after circle pops in
        val confetti = view.findViewById<ConfettiView>(R.id.confettiView)
        checkCircle?.postDelayed({ confetti?.startBurst() }, 350)

        view.findViewById<Button>(R.id.successDoneBtn)?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}