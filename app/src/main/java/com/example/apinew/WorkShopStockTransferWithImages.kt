package com.example.apinew

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
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
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.w3c.dom.Text
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class WorkShopStockTransferWithImages : AppCompatActivity() {

    private val REQUEST_IMAGE_CAPTURE = 1
    private var clickedPlaceholder: ImageView? = null
    private lateinit var jobCardInputField:EditText
    private lateinit var searchButton:ImageButton
    private lateinit var jCDetails:TextView
    private lateinit var login_name: String
    private lateinit var deptName: String
    private var ouId: Int = 0
    private var locId: Int = 0
    private lateinit var location_name: String
    private lateinit var vehicleOut:TextView
    private lateinit var organizationSpinner:Spinner
    private lateinit var currentKmsText:TextView
    private lateinit var currentKms:EditText
    private lateinit var remarkText:TextView
    private lateinit var Remarks:EditText
    private lateinit var imageUploadTitle:TextView
    private val organizationMap = mutableMapOf<String, Int>()
    private lateinit var imagePlaceholder1:ImageView
    private lateinit var imagePlaceholder2:ImageView
    private lateinit var imagePlaceholder3:ImageView
    private lateinit var imagePlaceholder4:ImageView
    private lateinit var imagePlaceholder5:ImageView
    private lateinit var imagePlaceholder6:ImageView
    private lateinit var imagePlaceholder7:ImageView
    private lateinit var imagePlaceholder8:ImageView
    private lateinit var imagePlaceholder9:ImageView
    private lateinit var imagePlaceholder10:ImageView
    private lateinit var imagePlaceholder11:ImageView
    private lateinit var imagePlaceholder12:ImageView
    private lateinit var imagePlaceholder13:ImageView
    private lateinit var imagePlaceholder14:ImageView
    private lateinit var saveData:Button
    private lateinit var refreshData:Button
    private lateinit var username:TextView
    private lateinit var locIdTxt:TextView
    private lateinit var deptIntent:TextView
    private lateinit var driverTxt:TextView
    private lateinit var driverInput:EditText
    private lateinit var chassisNo:String
    private lateinit var regNo:String
    private lateinit var stockTrfNo:String
    private lateinit var jobCardNo:String
    private lateinit var engineNo:String
    private lateinit var vin:String
    private lateinit var toLocation:String
    private  var toLocationID:Int=0
    private lateinit var vehNoLL:View
    private lateinit var jobCardLL:View
    private lateinit var vehNoInputField:EditText
    private lateinit var searchVehButton:ImageButton
    private lateinit var byjobCardNo:Button
    private lateinit var byVehicleNo:Button
    private lateinit var imageGrid:GridLayout
    private lateinit var redirectImages:Button
    private lateinit var captureToKm:ImageButton
    private var photoUri: Uri? = null
    private var photoFile: File? = null

    private var submittedOnce = false






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workshop_stock_transfer_with_images)

        val imagePlaceholders = listOf(
            findViewById<ImageView>(R.id.imagePlaceholder1),
            findViewById<ImageView>(R.id.imagePlaceholder2),
            findViewById<ImageView>(R.id.imagePlaceholder3),
            findViewById<ImageView>(R.id.imagePlaceholder4),
            findViewById<ImageView>(R.id.imagePlaceholder5),
            findViewById<ImageView>(R.id.imagePlaceholder6),
            findViewById<ImageView>(R.id.imagePlaceholder7),
            findViewById<ImageView>(R.id.imagePlaceholder8),
            findViewById<ImageView>(R.id.imagePlaceholder9),
            findViewById<ImageView>(R.id.imagePlaceholder10),
            findViewById<ImageView>(R.id.imagePlaceholder11),
            findViewById<ImageView>(R.id.imagePlaceholder12),
            findViewById<ImageView>(R.id.imagePlaceholder13),
            findViewById<ImageView>(R.id.imagePlaceholder14)
        )

        username=findViewById(R.id.username)
        locIdTxt=findViewById(R.id.locIdTxt)
        deptIntent=findViewById(R.id.deptIntent)
        imageGrid=findViewById(R.id.imageGrid)

        redirectImages=findViewById(R.id.redirectImages)

        captureToKm=findViewById(R.id.captureToKm)

        locId = intent.getIntExtra("locId", 0)
        ouId = intent.getIntExtra("ouId", 0)
        deptName = intent.getStringExtra("deptName") ?: ""
        location_name = intent.getStringExtra("location_name") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""

        username.text=login_name
        locIdTxt.text= location_name
        deptIntent.text=deptName

        byjobCardNo=findViewById(R.id.byjobCardNo)
        byVehicleNo=findViewById(R.id.byVehicleNo)
        vehNoLL=findViewById(R.id.vehNoLL)
        jobCardLL=findViewById(R.id.jobCardLL)
        vehNoInputField=findViewById(R.id.vehNoInputField)
        searchVehButton=findViewById(R.id.searchVehButton)

        val tableLayout = findViewById<TableLayout>(R.id.tableLayout2)
        tableLayout.removeAllViews()

        jobCardInputField=findViewById(R.id.jobCardInputField)
        searchButton=findViewById(R.id.searchButton)
        jCDetails=findViewById(R.id.jCDetails)

        saveData=findViewById(R.id.saveData)
        refreshData=findViewById(R.id.refreshData)


        vehicleOut=findViewById(R.id.vehicleOut)
        organizationSpinner=findViewById(R.id.organizationSpinner)
        currentKmsText=findViewById(R.id.currentKmsText)
        currentKms=findViewById(R.id.currentKms)
        remarkText=findViewById(R.id.remarkText)
        Remarks=findViewById(R.id.Remarks)
        imageUploadTitle=findViewById(R.id.imageUploadTitle)
        driverTxt=findViewById(R.id.driverTxt)
        driverInput=findViewById(R.id.driverInput)

        imagePlaceholder1=findViewById(R.id.imagePlaceholder1)
        imagePlaceholder2=findViewById(R.id.imagePlaceholder2)
        imagePlaceholder3=findViewById(R.id.imagePlaceholder3)
        imagePlaceholder4=findViewById(R.id.imagePlaceholder4)
        imagePlaceholder5=findViewById(R.id.imagePlaceholder5)
        imagePlaceholder6=findViewById(R.id.imagePlaceholder6)
        imagePlaceholder7=findViewById(R.id.imagePlaceholder7)
        imagePlaceholder8=findViewById(R.id.imagePlaceholder8)
        imagePlaceholder9=findViewById(R.id.imagePlaceholder9)
        imagePlaceholder10=findViewById(R.id.imagePlaceholder10)
        imagePlaceholder11=findViewById(R.id.imagePlaceholder11)
        imagePlaceholder12=findViewById(R.id.imagePlaceholder12)
        imagePlaceholder13=findViewById(R.id.imagePlaceholder13)
        imagePlaceholder14=findViewById(R.id.imagePlaceholder14)

        imagePlaceholder1.visibility=View.GONE
        imagePlaceholder2.visibility=View.GONE
        imagePlaceholder3.visibility=View.GONE
        imagePlaceholder4.visibility=View.GONE
        imagePlaceholder5.visibility=View.GONE
        imagePlaceholder6.visibility=View.GONE
        imagePlaceholder7.visibility=View.GONE
        imagePlaceholder8.visibility=View.GONE
        imagePlaceholder9.visibility=View.GONE
        imagePlaceholder10.visibility=View.GONE
        imagePlaceholder11.visibility=View.GONE
        imagePlaceholder12.visibility=View.GONE
        imagePlaceholder13.visibility=View.GONE
        imagePlaceholder14.visibility=View.GONE
        imageGrid.visibility=View.GONE

        saveData.visibility=View.GONE
        refreshData.visibility=View.GONE

        jCDetails.visibility=View.GONE

        captureToKm.setOnClickListener {
            clickedPlaceholder = captureToKm
            openCamera()
        }

        currentKmsText.visibility=View.GONE
        currentKms.visibility=View.GONE
        captureToKm.visibility=View.GONE

//        captureToKm.visibility=View.VISIBLE
//        currentKmsText.visibility=View.VISIBLE
//        currentKms.visibility=View.VISIBLE

        vehicleOut.visibility=View.GONE
        organizationSpinner.visibility=View.GONE
        remarkText.visibility=View.GONE
        Remarks.visibility=View.GONE
        imageUploadTitle.visibility=View.GONE
        driverTxt.visibility=View.GONE
        driverInput.visibility=View.GONE

        jobCardLL.visibility=View.GONE
        vehNoLL.visibility=View.GONE


        fetchOrganizations()


        searchButton.setOnClickListener {
            fetchJobCardData()
            jCDetails.text="Details By Job Card Number"
        }

        redirectImages.setOnClickListener {
            redirect()
        }

        searchVehButton.setOnClickListener {
            fetchRegNoData()
            jCDetails.text="Details By Vehicle Number"
        }

        refreshData.setOnClickListener {
            resetFields()
        }

        imagePlaceholders.forEach { placeholder ->
            placeholder.setOnClickListener {
                clickedPlaceholder = placeholder
                openCamera()
            }
        }

        saveData.setOnClickListener {
            postData()
        }

        byjobCardNo.setOnClickListener {
            jobCardLL.visibility=View.VISIBLE
            vehNoLL.visibility=View.GONE
        }
        byVehicleNo.setOnClickListener {
            jobCardLL.visibility=View.GONE
            vehNoLL.visibility=View.VISIBLE
        }

    }

    private fun redirect() {
        val intent = Intent(this, WorkshopViewTrfRecImages::class.java)
        intent.putExtra("STOCK_TRF_NO", stockTrfNo)
        intent.putExtra("JOB_CARD_NO", jobCardNo)
        startActivity(intent)
    }

//    private fun openCamera() {
//        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        if (takePictureIntent.resolveActivity(packageManager) != null) {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
//        }
//    }

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



    private fun compressBitmapQuality(bitmap: Bitmap, quality: Int): Bitmap {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val compressedByteArray = outputStream.toByteArray()
        return BitmapFactory.decodeByteArray(compressedByteArray, 0, compressedByteArray.size)
    }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {
//            val imageBitmap = data?.extras?.get("data") as? Bitmap
//            if (imageBitmap != null) {
//
//                if (clickedPlaceholder == captureToKm) {
//                    processImageForText(imageBitmap)
//                } else {
//                    clickedPlaceholder?.setImageBitmap(imageBitmap)
//                }
//            }
//        }
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK) {
            // Decode the image from the saved file path
            val bitmap = BitmapFactory.decodeFile(photoFile?.absolutePath)

            if (clickedPlaceholder == captureToKm) {
                // Pass the bitmap to processImageForText()
                processImageForText(bitmap)
            } else {
                // Set the bitmap on the clicked ImageView
                clickedPlaceholder?.setImageBitmap(bitmap)
            }
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

//    private fun handleExtractedText(result: com.google.mlkit.vision.text.Text) {
//        val recognizedText = result.text
//        if (recognizedText.isNotEmpty()) {
//            val numericText = recognizedText.filter { it.isDigit() }
//            currentKms.setText(numericText)
//        } else {
//            Toast.makeText(this, "No text detected", Toast.LENGTH_SHORT).show()
//        }
//    }


//    private fun handleExtractedText(result: com.google.mlkit.vision.text.Text) {
//        val recognizedText = result.text
//        if (recognizedText.isNotEmpty()) {
//            val regex = Regex("\\d+")
//            val matchResult = regex.find(recognizedText)
//
//            if (matchResult != null) {
//                val numericText = matchResult.value
//                currentKms.setText(numericText)
//            } else {
//                Toast.makeText(this, "No valid number detected in the image", Toast.LENGTH_SHORT).show()
//            }
//        } else {
//            Toast.makeText(this, "No text detected", Toast.LENGTH_SHORT).show()
//        }
//    }

    private fun handleExtractedText(result: com.google.mlkit.vision.text.Text) {
        val recognizedText = result.text
        if (recognizedText.isNotEmpty()) {
            val regex = Regex("(\\d+)\\s*(?=km)", RegexOption.IGNORE_CASE)
            val matchResult = regex.find(recognizedText)

            if (matchResult != null) {
                val numericText = matchResult.value.trim()
                currentKms.setText(numericText)
            } else {
                Toast.makeText(this, "No valid reading found", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No text found", Toast.LENGTH_SHORT).show()
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
                    val organizations: List<CameraActivity.Organization> = gson.fromJson(
                        jsonObject.getJSONArray("obj").toString(),
                        object : TypeToken<List<CameraActivity.Organization>>() {}.type
                    )
                    runOnUiThread {
                        populateOrganizationSpinner(organizations)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@WorkShopStockTransferWithImages, "Failed to fetch organizations", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@WorkShopStockTransferWithImages, "Failed to fetch organizations due to exception: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun populateOrganizationSpinner(organizations: List<CameraActivity.Organization>) {
        val spinnerItems = mutableListOf("Select Organization")
        spinnerItems.addAll(organizations.map { "${it.LOCID} - ${it.LOCATIONNAME}" })

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
                if (position > 0) {
                    val selectedItem = spinnerItems[position]
                    val selectedOrganization = organizations.find {
                        "${it.LOCID} - ${it.LOCATIONNAME}" == selectedItem
                    }
                    toLocation = selectedOrganization?.LOCATIONNAME ?: ""
                    toLocationID = (selectedOrganization?.LOCID ?: "") as Int
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }



    private fun populateFieldsAfterJobCNoSearch(){
        captureToKm.visibility=View.VISIBLE
        jCDetails.visibility=View.VISIBLE
        vehicleOut.visibility=View.VISIBLE
        organizationSpinner.visibility=View.VISIBLE
        currentKmsText.visibility=View.VISIBLE
        currentKms.visibility=View.VISIBLE
        remarkText.visibility=View.VISIBLE
        Remarks.visibility=View.VISIBLE
        imageGrid.visibility=View.VISIBLE
        imageUploadTitle.visibility=View.VISIBLE
        imagePlaceholder1.visibility=View.VISIBLE
        imagePlaceholder2.visibility=View.VISIBLE
        imagePlaceholder3.visibility=View.VISIBLE
        imagePlaceholder4.visibility=View.VISIBLE
        imagePlaceholder5.visibility=View.VISIBLE
        imagePlaceholder6.visibility=View.VISIBLE
        imagePlaceholder7.visibility=View.VISIBLE
        imagePlaceholder8.visibility=View.VISIBLE
        imagePlaceholder9.visibility=View.VISIBLE
        imagePlaceholder10.visibility=View.VISIBLE
        imagePlaceholder11.visibility=View.VISIBLE
        imagePlaceholder12.visibility=View.VISIBLE
        imagePlaceholder13.visibility=View.VISIBLE
        imagePlaceholder14.visibility=View.VISIBLE
        driverTxt.visibility=View.VISIBLE
        driverInput.visibility=View.VISIBLE
    }


    private fun fetchJobCardData() {
        val client = OkHttpClient()
        val jobCardNo=jobCardInputField.text.toString()
        val url =ApiFile.APP_URL+"/service/wsVehTransDetByJobCardNo?jobCardNo=$jobCardNo"

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

                    val jcData = jobCardDetails(
                        VARIANT_CODE = stockItem.getString("VARIANT_CODE"),
                        JOBCARDNO = stockItem.getString("JOBCARDNO"),
                        MODEL_DESC = stockItem.getString("MODEL_DESC"),
                        VIN = stockItem.getString("VIN"),
                        REGNO = stockItem.getString("REGNO"),
                        PHYSICALLOCATION = stockItem.getString("PHYSICALLOCATION"),
                        ENGINENO = stockItem.getString("ENGINENO"),
                        SERVICEADVISOR = stockItem.getString("SERVICEADVISOR"),
                        VEH_STATUS = stockItem.getString("VEH_STATUS"),
                        STATUS = stockItem.getString("STATUS"),
                        CUSTNAME = stockItem.getString("CUSTNAME"),
                        ERPACCTNO = stockItem.getString("ERPACCTNO"),
                        CHASSIS_NO=stockItem.getString("CHASSIS_NO"),
                        TRANSSEGMENT=stockItem.getString("TRANSSEGMENT"),
                        DMSLOCATION=stockItem.getString("DMSLOCATION"),
                    )
                    runOnUiThread {
                        if (jcData.PHYSICALLOCATION!= location_name) {
                            Toast.makeText(
                                this@WorkShopStockTransferWithImages,
                                "Vehicle is not at $location_name",
                                Toast.LENGTH_SHORT
                            ).show()
                            populateFields3(jcData)
                            refreshData.visibility=View.VISIBLE
                        }
                        else if (jcData.VEH_STATUS=="STOCK" && jcData.TRANSSEGMENT=="BANDP" && deptName=="DP") {
                            Toast.makeText(
                                this@WorkShopStockTransferWithImages,
                                "Vehicle status is ${jcData.VEH_STATUS}",
                                Toast.LENGTH_SHORT
                            ).show()
                            populateFields3(jcData)
                            populateFieldsAfterJobCNoSearch()
                            refreshData.visibility=View.VISIBLE
                            saveData.visibility=View.VISIBLE
                        }
                        else if (jcData.VEH_STATUS=="STOCK" && jcData.TRANSSEGMENT!="BANDP" && deptName=="SERVICE") {
                            Toast.makeText(
                                this@WorkShopStockTransferWithImages,
                                "Vehicle status is ${jcData.VEH_STATUS}",
                                Toast.LENGTH_SHORT
                            ).show()
                            populateFields3(jcData)
                            populateFieldsAfterJobCNoSearch()
                            refreshData.visibility=View.VISIBLE
                            saveData.visibility=View.VISIBLE
                        }
                        else {
                            Toast.makeText(
                                this@WorkShopStockTransferWithImages,
                                "Details found Successfully \n for Job Card No: $jobCardNo",
                                Toast.LENGTH_LONG
                            ).show()
                            populateFields3(jcData)
                            refreshData.visibility=View.VISIBLE
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@WorkShopStockTransferWithImages,
                        "Failed to fetch details for Job Card No: $jobCardNo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun fetchRegNoData() {
        val client = OkHttpClient()
        val vehNo=vehNoInputField.text.toString()
        val url =ApiFile.APP_URL+"/service/wsVehTransDetByRegNo?regNo=$vehNo"

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

                    val jcData = jobCardDetails(
                        VARIANT_CODE = stockItem.getString("VARIANT_CODE"),
                        JOBCARDNO = stockItem.getString("JOBCARDNO"),
                        MODEL_DESC = stockItem.getString("MODEL_DESC"),
                        VIN = stockItem.getString("VIN"),
                        REGNO = stockItem.getString("REGNO"),
                        PHYSICALLOCATION = stockItem.getString("PHYSICALLOCATION"),
                        ENGINENO = stockItem.getString("ENGINENO"),
                        SERVICEADVISOR = stockItem.getString("SERVICEADVISOR"),
                        VEH_STATUS = stockItem.getString("VEH_STATUS"),
                        STATUS = stockItem.getString("STATUS"),
                        CUSTNAME = stockItem.getString("CUSTNAME"),
                        ERPACCTNO = stockItem.getString("ERPACCTNO"),
                        CHASSIS_NO=stockItem.getString("CHASSIS_NO"),
                        TRANSSEGMENT=stockItem.getString("TRANSSEGMENT"),
                        DMSLOCATION=stockItem.getString("DMSLOCATION")
                    )
                    runOnUiThread {
                        if (jcData.PHYSICALLOCATION!= location_name) {
                            Toast.makeText(
                                this@WorkShopStockTransferWithImages,
                                "Vehicle is not at $location_name",
                                Toast.LENGTH_SHORT
                            ).show()
                            populateFields3(jcData)
                            refreshData.visibility=View.VISIBLE
                        }
                        else if (jcData.VEH_STATUS=="STOCK" && jcData.TRANSSEGMENT=="BANDP" && deptName=="DP") {
                            Toast.makeText(
                                this@WorkShopStockTransferWithImages,
                                "Vehicle status is ${jcData.VEH_STATUS}",
                                Toast.LENGTH_SHORT
                            ).show()
                            populateFields3(jcData)
                            populateFieldsAfterJobCNoSearch()
                            refreshData.visibility=View.VISIBLE
                            saveData.visibility=View.VISIBLE
                        }
                        else if (jcData.VEH_STATUS=="STOCK" && jcData.TRANSSEGMENT!="BANDP" && deptName=="SERVICE") {
                            Toast.makeText(
                                this@WorkShopStockTransferWithImages,
                                "Vehicle status is ${jcData.VEH_STATUS}",
                                Toast.LENGTH_SHORT
                            ).show()
                            populateFields3(jcData)
                            populateFieldsAfterJobCNoSearch()
                            refreshData.visibility=View.VISIBLE
                            saveData.visibility=View.VISIBLE
                        }
                        else {
                            Toast.makeText(
                                this@WorkShopStockTransferWithImages,
                                "Details found Successfully \n for Vehicle No: $vehNo",
                                Toast.LENGTH_LONG
                            ).show()
                            populateFields3(jcData)
                            refreshData.visibility=View.VISIBLE
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@WorkShopStockTransferWithImages,
                        "Failed to fetch details for vehicle No: $vehNo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun showProgressDialog(): AlertDialog {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_progress, null)
        builder.setView(dialogView)
        builder.setCancelable(false)

        val dialog = builder.create()
        dialog.show()
        return dialog
    }


    private fun postData() {
        val progressDialog = showProgressDialog()
        if (submittedOnce) {
            Toast.makeText(
                this@WorkShopStockTransferWithImages,
                "This data has already been submitted.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val imageViews = listOf(
            findViewById<ImageView>(R.id.imagePlaceholder1),
            findViewById<ImageView>(R.id.imagePlaceholder2),
            findViewById<ImageView>(R.id.imagePlaceholder3),
            findViewById<ImageView>(R.id.imagePlaceholder4),
            findViewById<ImageView>(R.id.imagePlaceholder5),
            findViewById<ImageView>(R.id.imagePlaceholder6),
            findViewById<ImageView>(R.id.imagePlaceholder7),
            findViewById<ImageView>(R.id.imagePlaceholder8),
            findViewById<ImageView>(R.id.imagePlaceholder9),
            findViewById<ImageView>(R.id.imagePlaceholder10),
            findViewById<ImageView>(R.id.imagePlaceholder11),
            findViewById<ImageView>(R.id.imagePlaceholder12),
            findViewById<ImageView>(R.id.imagePlaceholder13),
            findViewById<ImageView>(R.id.imagePlaceholder14)
        )

        // Ensure all images are provided
        val placeholderDrawable = resources.getDrawable(R.drawable.uploadimgstk, null)
        val missingImages = imageViews.any { imageView ->
            imageView.drawable?.constantState == placeholderDrawable.constantState
        }

        if (missingImages) {
            Toast.makeText(
                this@WorkShopStockTransferWithImages,
                "Please upload all 14 images before submitting.",
                Toast.LENGTH_LONG
            ).show()
            progressDialog.dismiss()
            return
        }

        val client = OkHttpClient()
        val url = ApiFile.APP_URL + "/service/wsVehTransMake"

        if (locId == toLocationID) {
            Toast.makeText(
                this@WorkShopStockTransferWithImages,
                "Vehicle cannot be transferred from $location_name to $toLocation",
                Toast.LENGTH_LONG
            ).show()
            progressDialog.dismiss()
            return
        }

        val bodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
        bodyBuilder.addFormDataPart("chassisNo", chassisNo)
        bodyBuilder.addFormDataPart("regNo", regNo)
        bodyBuilder.addFormDataPart("jobCardNo", jobCardNo)
        bodyBuilder.addFormDataPart("vin", vin)
        bodyBuilder.addFormDataPart("madeBy", login_name)
        bodyBuilder.addFormDataPart("toLocation", toLocation)
        bodyBuilder.addFormDataPart("engineNo", engineNo)
        bodyBuilder.addFormDataPart("fromLocation", location_name)
        bodyBuilder.addFormDataPart("driverName", driverInput.text.toString())
        bodyBuilder.addFormDataPart("authorisedBy", "$login_name-$location_name")
        bodyBuilder.addFormDataPart("ou", ouId.toString())
        bodyBuilder.addFormDataPart("fromKm", currentKms.text.toString())
        bodyBuilder.addFormDataPart("updatedBy", login_name)
        bodyBuilder.addFormDataPart("lastUploadedBy", login_name)
        bodyBuilder.addFormDataPart("createdBy", login_name)
        bodyBuilder.addFormDataPart("dept", deptName)
        bodyBuilder.addFormDataPart("fromLocCode", locId.toString())
        bodyBuilder.addFormDataPart("toLocCode", toLocationID.toString())

        for ((index, imageView) in imageViews.withIndex()) {
            val drawable = imageView.drawable ?: continue

            val bitmap = (drawable as BitmapDrawable).bitmap
            val compressedFile = File(cacheDir, "compressed_image_${index + 1}.jpg")
            compressedFile.outputStream().use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 10, it)
            }

            val imageBody = compressedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            bodyBuilder.addFormDataPart("images", compressedFile.name, imageBody)
        }

        val requestBody = bodyBuilder.build()

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
                    progressDialog.dismiss()
                    if (responseBody != null) {
                        val jsonObject = JSONObject(responseBody)
                        val message = jsonObject.optString("message", "")

                        when {
                            message.contains("Registration No already exists for this batchName", ignoreCase = true) -> {
                                submittedOnce = true
                                Toast.makeText(
                                    this@WorkShopStockTransferWithImages,
                                    "Stock taking is already completed for this vehicle",
                                    Toast.LENGTH_LONG
                                ).show()
                                resetFields()
                            }
                            responseCode == 200 -> {
                                submittedOnce = true
                                Toast.makeText(
                                    this@WorkShopStockTransferWithImages,
                                    "Vehicle Transferred to location $toLocation",
                                    Toast.LENGTH_LONG
                                ).show()
                                resetFields()
                            }
                            else -> {
                                Toast.makeText(
                                    this@WorkShopStockTransferWithImages,
                                    "Failed to Transfer Vehicle. Error code: $responseCode",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@WorkShopStockTransferWithImages,
                            "No response from server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@WorkShopStockTransferWithImages,
                        "Error saving data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }




    private fun populateFields3(jcData:jobCardDetails) {
        val table = findViewById<TableLayout>(R.id.tableLayout2)
        table.removeAllViews()

        val detailsMap = mutableMapOf(
            "JOB CARD NO" to jcData.JOBCARDNO,
            "VIN" to jcData.VIN,
            "VEH NO" to jcData.REGNO,
            "VARIANT CODE" to jcData.VARIANT_CODE,
            "MODEL DESC" to jcData.MODEL_DESC,
            "ENGINE NO" to jcData.ENGINENO,
            "VEH STATUS" to jcData.VEH_STATUS,
            "STATUS" to jcData.STATUS,
            "CUSTOMER" to jcData.CUSTNAME,
            "SERVICE ADVISOR" to jcData.SERVICEADVISOR,
            "LOCATION" to jcData.PHYSICALLOCATION,
            "DMS LOC" to jcData.DMSLOCATION,
            "TRANS SEGMENT" to jcData.TRANSSEGMENT

        )

        chassisNo=jcData.CHASSIS_NO
        regNo=jcData.REGNO
        jobCardNo=jcData.JOBCARDNO
        engineNo=jcData.ENGINENO
        vin=jcData.VIN


//        if (vin_data.VEH_STATUS != "Stock Transfer In-Transit") {
//            detailsMap["LOCATION"] = vin_data.LOCATION
//        }

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

    private fun resetFields(){
        val tableLayout = findViewById<TableLayout>(R.id.tableLayout2)
        tableLayout.removeAllViews()
        jobCardInputField.setText("")
        vehNoInputField.setText("")
        jCDetails.visibility=View.GONE
        saveData.visibility=View.GONE
        imageGrid.visibility=View.GONE
        captureToKm.visibility=View.GONE
        imagePlaceholder1.visibility=View.GONE
        imagePlaceholder2.visibility=View.GONE
        imagePlaceholder3.visibility=View.GONE
        imagePlaceholder4.visibility=View.GONE
        imagePlaceholder5.visibility=View.GONE
        imagePlaceholder6.visibility=View.GONE
        imagePlaceholder7.visibility=View.GONE
        imagePlaceholder8.visibility=View.GONE
        imagePlaceholder9.visibility=View.GONE
        imagePlaceholder10.visibility=View.GONE
        imagePlaceholder11.visibility=View.GONE
        imagePlaceholder12.visibility=View.GONE
        imagePlaceholder13.visibility=View.GONE
        imagePlaceholder14.visibility=View.GONE
        resetImagePlaceholders()
        vehicleOut.visibility=View.GONE
        organizationSpinner.setSelection(0)
        organizationSpinner.visibility=View.GONE
        currentKmsText.visibility=View.GONE
        currentKms.setText("")
        currentKms.visibility=View.GONE
        remarkText.visibility=View.GONE
        Remarks.setText("")
        Remarks.visibility=View.GONE
        imageUploadTitle.visibility=View.GONE
        refreshData.visibility=View.GONE
        driverTxt.visibility=View.GONE
        driverInput.visibility=View.GONE
        driverInput.setText("")
    }

    private fun resetImagePlaceholders() {
        val imagePlaceholders = listOf(
            imagePlaceholder1, imagePlaceholder2, imagePlaceholder3,
            imagePlaceholder4, imagePlaceholder5, imagePlaceholder6,
            imagePlaceholder7, imagePlaceholder8, imagePlaceholder9,
            imagePlaceholder10, imagePlaceholder11, imagePlaceholder12,
            imagePlaceholder13, imagePlaceholder14
        )

        imagePlaceholders.forEach { placeholder ->
            placeholder.setImageResource(R.drawable.uploadimgstk)
        }
    }

    data class jobCardDetails(
            val VIN:String,
            val JOBCARDNO:String,
            val VARIANT_CODE:String,
            val PHYSICALLOCATION:String,
            val MODEL_DESC:String,
            val ENGINENO:String,
            val SERVICEADVISOR:String,
            val VEH_STATUS:String,
            val REGNO:String,
            val CUSTNAME:String,
            val STATUS:String,
            val ERPACCTNO:String,
            val CHASSIS_NO:String,
            val TRANSSEGMENT:String,
        val DMSLOCATION:String
            )

}



