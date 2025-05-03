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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.ByteArrayOutputStream


class WorkShopStockReceiveWithImages : AppCompatActivity() {

    private var clickedPlaceholder: ImageView? = null
    private lateinit var jobCardInputField:EditText
    private lateinit var searchButton:ImageButton
    private lateinit var jCDetails:TextView
    private lateinit var login_name: String
    private lateinit var deptName: String
    private var ouId: Int = 0
    private var locId: Int = 0
    private lateinit var location_name: String
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
    private lateinit var chassisNo:String
    private lateinit var regNo:String
    private lateinit var engineNo:String
    private lateinit var vin:String
    private lateinit var fromLocation:String
    private lateinit var driverName:String
    private lateinit var stockTrfNo:String
    private lateinit var jobCardNo:String
    private lateinit var stockTrfDate:String
    private lateinit var FROM_LOC_CODE:String
    private var fromKm:Int=0

    private lateinit var vehNoLL:View
    private lateinit var jobCardLL:View
    private lateinit var vehNoInputField:EditText
    private lateinit var searchVehButton:ImageButton
    private lateinit var byjobCardNo:Button
    private lateinit var byVehicleNo:Button
    private lateinit var imageGrid: GridLayout
    private lateinit var redirectImages:Button
    private lateinit var captureToKm:ImageButton

    private lateinit var viewPendingVehicles:Button
    private lateinit var vehicleNumber:String
    private var photoUri: Uri? = null
    private var photoFile: File? = null
    private lateinit var departmentName:String




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workshop_stock_receive)

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


        locId = intent.getIntExtra("locId", 0)
        ouId = intent.getIntExtra("ouId", 0)
        deptName = intent.getStringExtra("deptName") ?: ""
        location_name = intent.getStringExtra("location_name") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""
        vehicleNumber = intent.getStringExtra("REG_NO") ?: ""


        username.text=login_name
        locIdTxt.text= location_name
        deptIntent.text=deptName

        viewPendingVehicles=findViewById(R.id.viewPendingVehicles)

        byjobCardNo=findViewById(R.id.byjobCardNo)
        byVehicleNo=findViewById(R.id.byVehicleNo)
        vehNoLL=findViewById(R.id.vehNoLL)
        jobCardLL=findViewById(R.id.jobCardLL)
        vehNoInputField=findViewById(R.id.vehNoInputField)
        searchVehButton=findViewById(R.id.searchVehButton)

        redirectImages=findViewById(R.id.redirectImages)
        redirectImages.visibility=View.GONE

        jobCardLL.visibility=View.GONE
        vehNoLL.visibility=View.GONE

        val tableLayout = findViewById<TableLayout>(R.id.tableLayout2)
        tableLayout.removeAllViews()

        jobCardInputField=findViewById(R.id.jobCardInputField)
        searchButton=findViewById(R.id.searchButton)
        jCDetails=findViewById(R.id.jCDetails)

        saveData=findViewById(R.id.saveData)
        refreshData=findViewById(R.id.refreshData)


        currentKmsText=findViewById(R.id.currentKmsText)
        currentKms=findViewById(R.id.currentKms)
        remarkText=findViewById(R.id.remarkText)
        Remarks=findViewById(R.id.Remarks)
        imageUploadTitle=findViewById(R.id.imageUploadTitle)

        captureToKm=findViewById(R.id.captureToKm)

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


        currentKmsText.visibility=View.GONE
        currentKms.visibility=View.GONE
        captureToKm.visibility=View.GONE
//        captureToKm.visibility=View.VISIBLE
//        currentKmsText.visibility=View.VISIBLE
//        currentKms.visibility=View.VISIBLE
        remarkText.visibility=View.GONE
        Remarks.visibility=View.GONE
        imageUploadTitle.visibility=View.GONE

        redirectImages.setOnClickListener {
            redirect()
        }

        captureToKm.setOnClickListener {
            clickedPlaceholder = captureToKm
            openCamera()
        }

        viewPendingVehicles.setOnClickListener {
            workShopStockTransfer()
        }


        searchButton.setOnClickListener {
            fetchJobCardData()
            jCDetails.text="Details By Job Card Number"
        }

        searchVehButton.setOnClickListener {
            fetchRegNoData()
            jCDetails.text="Details By Vehicle Number"
        }

        refreshData.setOnClickListener {
            resetFields2()
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

        if (vehicleNumber.isNotEmpty()){
            vehNoInputField.setText(vehicleNumber)
            vehNoLL.visibility=View.VISIBLE
        } else{
            vehNoLL.visibility=View.GONE
        }
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

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {
//            val imageBitmap = data?.extras?.get("data") as? Bitmap
//            if (imageBitmap != null) {
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
            val bitmap = BitmapFactory.decodeFile(photoFile?.absolutePath)

            if (clickedPlaceholder == captureToKm) {
                processImageForText(bitmap)
            } else {
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

//    private fun handleExtractedText(result: Text) {
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


    private fun handleExtractedText(result:com.google.mlkit.vision.text.Text) {
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


    private fun redirect() {
        val intent = Intent(this, WorkshopViewTrfRecImages::class.java)
        intent.putExtra("STOCK_TRF_NO", stockTrfNo)
        intent.putExtra("JOB_CARD_NO", jobCardNo)
        intent.putExtra("REG_NO",regNo)
        startActivity(intent)
    }

    private fun populateFieldsAfterJobCNoSearch(){
        jCDetails.visibility=View.VISIBLE
        currentKmsText.visibility=View.VISIBLE
        currentKms.visibility=View.VISIBLE
        captureToKm.visibility=View.VISIBLE
        remarkText.visibility=View.VISIBLE
        Remarks.visibility=View.VISIBLE
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
        imageGrid.visibility=View.VISIBLE
    }


    private fun fetchJobCardData() {
        val client = OkHttpClient()
        val jobCardNo=jobCardInputField.text.toString()
        val url =ApiFile.APP_URL+"/service/wsVehReceiveDetByJobCardNo?jobCardNo=$jobCardNo"

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
                        REG_NO = stockItem.getString("REG_NO"),
                        FROM_LOCATION = stockItem.getString("FROM_LOCATION"),
                        ENGINENO = stockItem.getString("ENGINENO"),
                        SERVICEADVISOR = stockItem.getString("SERVICEADVISOR"),
                        VEH_STATUS = stockItem.getString("VEH_STATUS"),
                        STATUS = stockItem.getString("STATUS"),
                        CUSTNAME = stockItem.getString("CUSTNAME"),
                        ERPACCTNO = stockItem.getString("ERPACCTNO"),
                        FROM_KM = stockItem.getInt("FROM_KM"),
                        MADE_BY = stockItem.getString("MADE_BY"),
                        STOCK_TRF_NO = stockItem.getString("STOCK_TRF_NO"),
                        STOCK_TRF_DATE = stockItem.getString("STOCK_TRF_DATE"),
                        TO_LOCATION = stockItem.getString("TO_LOCATION"),
                        CHASSIS_NO=stockItem.getString("CHASSIS_NO"),
                        DRIVER_NAME=stockItem.getString("DRIVER_NAME"),
                        FROM_LOC_CODE=stockItem.getString("FROM_LOC_CODE"),
                        DEPT=stockItem.getString("DEPT")
                    )
                    runOnUiThread {
                        if (jcData.TO_LOCATION!= location_name) {
                            Toast.makeText(
                                this@WorkShopStockReceiveWithImages,
                                "Vehicle is not stock transfer at $location_name",
                                Toast.LENGTH_SHORT
                            ).show()
                            populateFields3(jcData)
                            refreshData.visibility=View.VISIBLE
                            redirectImages.visibility=View.VISIBLE
                        }
                        else if (jcData.VEH_STATUS=="Stock Transfer In-Transit" && jcData.DEPT==deptName) {
                            Toast.makeText(
                                this@WorkShopStockReceiveWithImages,
                                "Vehicle status is ${jcData.VEH_STATUS}",
                                Toast.LENGTH_SHORT
                            ).show()
                            populateFields3(jcData)
                            populateFieldsAfterJobCNoSearch()
                            refreshData.visibility=View.VISIBLE
                            saveData.visibility=View.VISIBLE
                            redirectImages.visibility=View.VISIBLE
                        }
                        else {
                            Toast.makeText(
                                this@WorkShopStockReceiveWithImages,
                                "Details found Successfully \n for Job Card No: $jobCardNo",
                                Toast.LENGTH_LONG
                            ).show()
                            populateFields3(jcData)
                            refreshData.visibility=View.VISIBLE
                            redirectImages.visibility=View.VISIBLE
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@WorkShopStockReceiveWithImages,
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
        val url =ApiFile.APP_URL+"/service/wsVehReceiveDetByRegNo?regNo=$vehNo"

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
                        REG_NO = stockItem.getString("REG_NO"),
                        FROM_LOCATION = stockItem.getString("FROM_LOCATION"),
                        ENGINENO = stockItem.getString("ENGINENO"),
                        SERVICEADVISOR = stockItem.getString("SERVICEADVISOR"),
                        VEH_STATUS = stockItem.getString("VEH_STATUS"),
                        STATUS = stockItem.getString("STATUS"),
                        CUSTNAME = stockItem.getString("CUSTNAME"),
                        ERPACCTNO = stockItem.getString("ERPACCTNO"),
                        FROM_KM = stockItem.getInt("FROM_KM"),
                        MADE_BY = stockItem.getString("MADE_BY"),
                        STOCK_TRF_NO = stockItem.getString("STOCK_TRF_NO"),
                        STOCK_TRF_DATE = stockItem.getString("STOCK_TRF_DATE"),
                        TO_LOCATION = stockItem.getString("TO_LOCATION"),
                        CHASSIS_NO=stockItem.getString("CHASSIS_NO"),
                        DRIVER_NAME=stockItem.getString("DRIVER_NAME"),
                        FROM_LOC_CODE=stockItem.getString("FROM_LOC_CODE"),
                        DEPT=stockItem.getString("DEPT")
                    )
                    runOnUiThread {
                        if (jcData.TO_LOCATION!= location_name) {
                            Toast.makeText(
                                this@WorkShopStockReceiveWithImages,
                                "Vehicle is not stock transfer at $location_name",
                                Toast.LENGTH_SHORT
                            ).show()
                            populateFields3(jcData)
                            refreshData.visibility=View.VISIBLE
                            redirectImages.visibility=View.VISIBLE

                        }
                        else if (jcData.VEH_STATUS=="Stock Transfer In-Transit"&& jcData.DEPT==deptName) {
                            Toast.makeText(
                                this@WorkShopStockReceiveWithImages,
                                "Vehicle status is ${jcData.VEH_STATUS}",
                                Toast.LENGTH_SHORT
                            ).show()
                            populateFields3(jcData)
                            populateFieldsAfterJobCNoSearch()
                            refreshData.visibility=View.VISIBLE
                            saveData.visibility=View.VISIBLE
                            redirectImages.visibility=View.VISIBLE

                        }
                        else {
                            Toast.makeText(
                                this@WorkShopStockReceiveWithImages,
                                "Details found Successfully \n for Vehicle No: $vehNo",
                                Toast.LENGTH_LONG
                            ).show()
                            populateFields3(jcData)
                            refreshData.visibility=View.VISIBLE
                            redirectImages.visibility=View.VISIBLE

                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@WorkShopStockReceiveWithImages,
                        "Failed to fetch details for Job Card No: $vehNo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun convertStringToDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("dd-MM-yy", Locale.getDefault())
            val date = inputFormat.parse(dateString)

            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            outputFormat.format(date!!)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
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
        stockTrfDate = getFormattedDate()
        val toKm = currentKms.text.toString()
        if (toKm.isEmpty()) {
            Toast.makeText(this, "Please enter the Current KM", Toast.LENGTH_SHORT).show()
            return
        }
        val tokmInt = toKm.toInt()

        val frmKm = fromKm

        if (currentKmsText.text.toString().isEmpty()) {
            Toast.makeText(this, "Please enter the Current KM", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()
            return
        }

        if (toKm.isEmpty()) {
            Toast.makeText(this, "Please enter the Current KM", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()
            return
        }

        if (tokmInt <= frmKm) {
            Toast.makeText(this, "To KM must be greater than From KM", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()
            return
        }

        val client = OkHttpClient()
        val url = ApiFile.APP_URL + "/service/wsVehTransReceive"


        val bodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)

        bodyBuilder.addFormDataPart("chassisNo", chassisNo)
        bodyBuilder.addFormDataPart("regNo", regNo)
        bodyBuilder.addFormDataPart("jobCardNo", jobCardNo)
        bodyBuilder.addFormDataPart("vin", vin)
        bodyBuilder.addFormDataPart("madeBy", login_name)
        bodyBuilder.addFormDataPart("toLocation", location_name)
        bodyBuilder.addFormDataPart("fromLocation", fromLocation)
        bodyBuilder.addFormDataPart("engineNo", engineNo)
        bodyBuilder.addFormDataPart("driverName", driverName)
        bodyBuilder.addFormDataPart("authorisedBy", "$login_name-$location_name")
        bodyBuilder.addFormDataPart("ou", ouId.toString())
        bodyBuilder.addFormDataPart("fromKm", fromKm.toString())
        bodyBuilder.addFormDataPart("updatedBy", login_name)
        bodyBuilder.addFormDataPart("lastUploadedBy", login_name)
        bodyBuilder.addFormDataPart("createdBy", login_name)
        bodyBuilder.addFormDataPart("dept", deptName)
        bodyBuilder.addFormDataPart("fromLocCode", FROM_LOC_CODE)
        bodyBuilder.addFormDataPart("toLocCode", locId.toString())
        bodyBuilder.addFormDataPart("stockTrfNo", stockTrfNo)
        bodyBuilder.addFormDataPart("stockTrfDate", stockTrfDate)
        bodyBuilder.addFormDataPart("receivedBy", login_name)
        bodyBuilder.addFormDataPart("toKm", tokmInt.toString())

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

        if (imageViews.any { it.drawable == null || it.drawable.constantState == resources.getDrawable(R.drawable.uploadimgstk).constantState }) {
            Toast.makeText(this, "Please upload all 14 images", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()
            return
        }

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
            .put(requestBody)
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
                            message.contains("Registration No already exists for this batchName", ignoreCase = true) -> {
                                Toast.makeText(
                                    this@WorkShopStockReceiveWithImages,
                                    "Stock taking is already completed for this vehicle",
                                    Toast.LENGTH_LONG
                                ).show()
                                progressDialog.dismiss()
                                resetFields()
                            }
                            responseCode == 200 -> {
                                Toast.makeText(
                                    this@WorkShopStockReceiveWithImages,
                                    "Vehicle Received Successfully at location $location_name",
                                    Toast.LENGTH_LONG
                                ).show()
                                progressDialog.dismiss()
                                resetFields()
                            }
                            else -> {
                                Toast.makeText(
                                    this@WorkShopStockReceiveWithImages,
                                    "Failed to Receive Vehicle. Error code: $responseCode",
                                    Toast.LENGTH_SHORT
                                ).show()
                                progressDialog.dismiss()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@WorkShopStockReceiveWithImages,
                            "No response from server",
                            Toast.LENGTH_SHORT
                        ).show()
                        progressDialog.dismiss()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@WorkShopStockReceiveWithImages,
                        "Error saving data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    fun getFormattedDate(): String {
        val date = Date()
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(date)
    }

    private fun formatDateTime(dateTime: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
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



    private fun populateFields3(jcData:jobCardDetails) {
        val table = findViewById<TableLayout>(R.id.tableLayout2)
        table.removeAllViews()

        val detailsMap = mutableMapOf(
            "JOB CARD NO" to jcData.JOBCARDNO,
            "VIN" to jcData.VIN,
            "VEH NO" to jcData.REG_NO,
            "STOCK TRF NO" to jcData.STOCK_TRF_NO,
            "VARIANT CODE" to jcData.VARIANT_CODE,
            "MODEL DESC" to jcData.MODEL_DESC,
            "ENGINE NO" to jcData.ENGINENO,
            "VEH STATUS" to jcData.VEH_STATUS,
            "STATUS" to jcData.STATUS,
            "CUSTOMER" to jcData.CUSTNAME,
            "SERVICE ADVISOR" to jcData.SERVICEADVISOR,
            "FROM LOCATION" to jcData.FROM_LOCATION,
            "FROM KM" to jcData.FROM_KM,
            "TRANSFER BY" to jcData.MADE_BY,
            "STK TRF DATE" to formatDateTime(jcData.STOCK_TRF_DATE)
        )

        chassisNo=jcData.CHASSIS_NO
        regNo=jcData.REG_NO
        jobCardNo=jcData.JOBCARDNO
        engineNo=jcData.ENGINENO
        vin=jcData.VIN
        fromLocation=jcData.FROM_LOCATION
        driverName=jcData.DRIVER_NAME
        stockTrfNo=jcData.STOCK_TRF_NO
        stockTrfDate=jcData.STOCK_TRF_DATE
        fromKm=jcData.FROM_KM
        FROM_LOC_CODE=jcData.FROM_LOC_CODE
        departmentName=jcData.DEPT


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
//        stockTrfNo=""
        val tableLayout = findViewById<TableLayout>(R.id.tableLayout2)
//        tableLayout.removeAllViews()
        jobCardInputField.setText("")
        vehNoInputField.setText("")
        jCDetails.visibility=View.GONE
        saveData.visibility=View.GONE
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
        resetImagePlaceholders()
        currentKmsText.visibility=View.GONE
        currentKms.setText("")
        currentKms.visibility=View.GONE
        captureToKm.visibility=View.GONE
        remarkText.visibility=View.GONE
        Remarks.setText("")
        Remarks.visibility=View.GONE
        imageUploadTitle.visibility=View.GONE
//        refreshData.visibility=View.GONE
    }

    private fun resetFields2(){
        stockTrfNo=""
        val tableLayout = findViewById<TableLayout>(R.id.tableLayout2)
        tableLayout.removeAllViews()
        jobCardInputField.setText("")
        vehNoInputField.setText("")
        jCDetails.visibility=View.GONE
        saveData.visibility=View.GONE
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
        resetImagePlaceholders()
        currentKmsText.visibility=View.GONE
        currentKms.setText("")
        currentKms.visibility=View.GONE
        captureToKm.visibility=View.GONE
        remarkText.visibility=View.GONE
        Remarks.setText("")
        Remarks.visibility=View.GONE
        imageUploadTitle.visibility=View.GONE
        refreshData.visibility=View.GONE
        redirectImages.visibility=View.GONE
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

    private fun workShopStockTransfer() {
        val intent = Intent(this@WorkShopStockReceiveWithImages, WorkshopPendingVehicle::class.java)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name",location_name)
        intent.putExtra("deptName",deptName)
        startActivity(intent)
    }

    data class jobCardDetails(
        val VIN:String,
        val JOBCARDNO:String,
        val VARIANT_CODE:String,
        val FROM_LOCATION:String,
        val MODEL_DESC:String,
        val ENGINENO:String,
        val SERVICEADVISOR:String,
        val VEH_STATUS:String,
        val REG_NO:String,
        val CUSTNAME:String,
        val STATUS:String,
        val ERPACCTNO:String,
        val FROM_KM:Int,
        val TO_LOCATION:String,
        val MADE_BY:String,
        val STOCK_TRF_NO:String,
        val STOCK_TRF_DATE:String,
        val CHASSIS_NO:String,
        val DRIVER_NAME:String,
        val FROM_LOC_CODE:String,
        val DEPT:String
    )

}



