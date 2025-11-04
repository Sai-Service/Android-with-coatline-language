package com.example.chassisimagecapture

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.mlkit.vision.text.TextRecognition
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.concurrent.TimeUnit
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileOutputStream
import java.io.IOException

class CaptureImage : AppCompatActivity() {

    private lateinit var chasisNo: AutoCompleteTextView
    private lateinit var vinAdapter: ArrayAdapter<String>
    private lateinit var imageView1: ImageView
    private lateinit var imageView2: ImageView
    private lateinit var captureImage1Button: Button
    private lateinit var captureImage2Button: Button
    private lateinit var uploadButton: Button
    private lateinit var login_name: String
    private lateinit var logoutButton: ImageView
    private lateinit var resetButton: Button
    private lateinit var viewpdfButton: Button
    private lateinit var dwnldpdfcv: CardView

    private var photo1Uri: Uri? = null
    private var photo2Uri: Uri? = null

    private val CAMERA_REQUEST_CODE = 101
    private val CAMERA_REQUEST_CODE2 = 102
    private val CAMERA_PERMISSION_REQUEST_CODE = 201
    private lateinit var scanButton:Button
    private lateinit var username:TextView
    private lateinit var downloadPdfCmd:TextView
    private lateinit var imageCaptureLL:View
    private lateinit var scanButtonLL:View
    private lateinit var uplResLL:View
    private lateinit var refreshBtnNew:ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_image)

        chasisNo = findViewById(R.id.chasisNo)
        login_name = intent.getStringExtra("login_name") ?: ""

        imageView1 = findViewById(R.id.imageView1)
        imageView2 = findViewById(R.id.imageView2)
        captureImage1Button = findViewById(R.id.captureImage1)
        captureImage2Button = findViewById(R.id.captureImage2)
        uploadButton = findViewById(R.id.uploadButton)
        scanButton = findViewById(R.id.scanButton)
        logoutButton = findViewById(R.id.logoutButton)
        resetButton = findViewById(R.id.resetButton)
        viewpdfButton = findViewById(R.id.viewPdf)
        dwnldpdfcv = findViewById(R.id.dwnldpdfcv)


        username=findViewById(R.id.username)
        downloadPdfCmd=findViewById(R.id.downloadPdfCmd)
        imageCaptureLL=findViewById(R.id.imageCaptureLL)
        scanButtonLL=findViewById(R.id.scanButtonLL)
        uplResLL=findViewById(R.id.uplResLL)
        refreshBtnNew=findViewById(R.id.refreshBtnNew)

        refreshBtnNew.visibility=View.GONE


        username.text=login_name




        if (!hasCameraPermission()) requestCameraPermission()

        captureImage1Button.setOnClickListener {
            if (hasCameraPermission()) takePhoto(CAMERA_REQUEST_CODE) else requestCameraPermission()
        }

        captureImage2Button.setOnClickListener {
            if (hasCameraPermission()) takePhoto(CAMERA_REQUEST_CODE2) else requestCameraPermission()
        }

        scanButton.setOnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
            integrator.setPrompt("Scan a QR Code or Barcode")
            integrator.setCameraId(0)
            integrator.setBeepEnabled(true)
            integrator.setBarcodeImageEnabled(true)
            integrator.setOrientationLocked(false)
            integrator.initiateScan()

        }


        uploadButton.setOnClickListener {
            uploadImages()
        }

        logoutButton.setOnClickListener {
            logout()
        }

        resetButton.setOnClickListener {
            reset()
        }

        viewpdfButton.setOnClickListener {
            downloadVehPdf1()
            downloadVehPdf2()
        }

        downloadPdfCmd.setOnClickListener {
            disableButtonAndViews()
//            fetchVehicleList()
        }

        vinAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mutableListOf())
        chasisNo.setAdapter(vinAdapter)
        chasisNo.threshold = 1

        chasisNo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty() && s.length >= 1) {
                    fetchVehicleList(s.toString())
                }
            }
        })


        refreshBtnNew.setOnClickListener {
            reRefresh()
        }


    }

//camera permissions
    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    //click/take photos
    private fun takePhoto(requestCode: Int) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            val photoFile: File = createImageFile()
            val photoURI: Uri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.fileprovider", photoFile)

            if (requestCode == CAMERA_REQUEST_CODE) {
                photo1Uri = photoURI
            } else {
                photo2Uri = photoURI
            }

            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(intent, requestCode)
        }
    }

    //create jpg image file
    private fun createImageFile(): File {
        val imageFileName = "JPEG_${System.currentTimeMillis()}_"
        val storageDir: File = externalCacheDir!!
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                val vin = result.contents
                chasisNo.setText(vin)
            }
            return
        }

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    if (photo1Uri != null) {
                        imageView1.setImageURI(photo1Uri)
                       detectCarFrontView(photo1Uri!!)

                    }
                }
                CAMERA_REQUEST_CODE2 -> {
                    if (photo2Uri != null) {
                        imageView2.setImageURI(photo2Uri)
                        if(chasisNo.text.toString().isEmpty()) {
                            recognizeChassisNumber(photo2Uri!!)
                        }
                    }
                }
            }
        }
    }


// car detection
    private fun detectCarFrontView(imageUri: Uri) {
        val image = InputImage.fromFilePath(this, imageUri)
        val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

        labeler.process(image)
            .addOnSuccessListener { labels ->
                val hasCar = labels.any { it.text.equals("car", ignoreCase = true) && it.confidence > 0.7 }
                if (!hasCar) {
                    Toast.makeText(this, "Not a car front view. Please try again.", Toast.LENGTH_LONG).show()
                    imageView1.setImageResource(R.drawable.defaultimg)
                    photo1Uri = null
                } else {
                    Toast.makeText(this, "Car front view detected.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Image labeling failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

//    private fun detectCarFrontView(imageUri: Uri) {
//        val image = InputImage.fromFilePath(this, imageUri)
//        val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
//
//        labeler.process(image)
//            .addOnSuccessListener { labels ->
//                val hasCar = labels.any {
//                    it.text.equals("car", ignoreCase = true) && it.confidence > 0.7
//                }
//
//                val hasLogo = labels.any {
//                    (it.text.contains("maruti", ignoreCase = true) || it.text.contains("suzuki", ignoreCase = true))
//                            && it.confidence > 0.7
//                }
//
//                when {
//                    !hasCar -> {
//                        Toast.makeText(this, "Not a car front view. Please try again.", Toast.LENGTH_LONG).show()
//                        imageView1.setImageResource(R.drawable.defaultimg)
//                        photo1Uri = null
//                    }
////
////                    !hasLogo -> {
////                        Toast.makeText(this, "Car detected, but Maruti/Suzuki logo not found.", Toast.LENGTH_SHORT).show()
////                    }
//
//                    else -> {
//                        Toast.makeText(this, "Maruti/Suzuki car front view detected.", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//            .addOnFailureListener {
//                Toast.makeText(this, "Image labeling failed: ${it.message}", Toast.LENGTH_SHORT).show()
//            }
//    }

// read chassis number
    private fun recognizeChassisNumber(imageUri: Uri) {
        val image = InputImage.fromFilePath(this, imageUri)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val allText = visionText.text
                val pattern = Regex("[A-HJ-NPR-Z0-9]{17}") // Typical VIN pattern
                val match = pattern.find(allText)
                if (match != null) {
                    chasisNo.setText(match.value)
                    Toast.makeText(this, "Chassis No: ${match.value}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Chassis number not detected", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Text recognition failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

//progress spin bar
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

    // upload images
    private fun uploadImages() {
        val progressDialog = showProgressDialog()
        if (photo1Uri == null || photo2Uri == null) {
            Toast.makeText(this, "Please capture both images first", Toast.LENGTH_SHORT).show()
            return
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .build()

        val url = "${ApiFile.APP_URL}/VehicleStock/img-to-pdf"

        val bodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)

        val chasisNo = chasisNo.text.toString()
        val createdBy = login_name

        bodyBuilder.addFormDataPart("chasisNo", chasisNo)
        bodyBuilder.addFormDataPart("createdBy", createdBy)

        val imageUris = listOf(photo1Uri, photo2Uri)
        val fileKeys = listOf("file1", "file2")

        for ((index, uri) in imageUris.withIndex()) {
            if (uri == null) continue

            try {
                val inputStream = contentResolver.openInputStream(uri)
                val tempFile = File.createTempFile("upload_img_$index", ".jpg", cacheDir)
                tempFile.outputStream().use { outputStream ->
                    inputStream?.copyTo(outputStream)
                }

                val requestBody = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                bodyBuilder.addFormDataPart(fileKeys[index], tempFile.name, requestBody)

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to read image $index: ${e.message}", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val request = Request.Builder()
            .url(url)
            .post(bodyBuilder.build())
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                runOnUiThread {
                    if (response.isSuccessful && responseBody != null) {
                        val message = JSONObject(responseBody).optString("message", "Upload successful")
                        Toast.makeText(this@CaptureImage, message, Toast.LENGTH_LONG).show()
                        reset()
                        progressDialog.dismiss()
                    } else {
                        Toast.makeText(
                            this@CaptureImage,
                            "Upload failed: ${response.code}",
                            Toast.LENGTH_SHORT
                        ).show()
                        progressDialog.dismiss()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@CaptureImage, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
            }
        }
    }

//reset to start process again
    private fun reset(){
        chasisNo.setText("")
        imageView1.setImageDrawable(null)
        imageView1.setImageResource(R.drawable.defaultimg)
        imageView2.setImageDrawable(null)
        imageView2.setImageResource(R.drawable.defaultimg)
        resetPdfFabs()
    }

//logout
    private fun logout() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

//download pdf 1
    private fun downloadVehPdf1() {
        val chassis_No = chasisNo.text.toString()
        if (chassis_No.isEmpty()) {
            Toast.makeText(this@CaptureImage, "Enter the vin number", Toast.LENGTH_SHORT).show()
            return
        }

        val client = OkHttpClient()
        val url = "${ApiFile.APP_URL}/VehicleStock/downloadVehPdf1?chasisNo=$chassis_No"

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@CaptureImage, "Download failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val body = response.body
                    if (body != null) {
                        val bytes = body.bytes()
                        val disposition = response.header("Content-Disposition")
                        val filename = disposition?.substringAfter("filename=")?.replace("\"", "")
                            ?: "vehPdf1_${chassis_No}.pdf" // keep same name (no timestamp)

                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                // For Android 10+
                                val resolver = contentResolver
                                val collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI
                                val existingFileCursor = resolver.query(
                                    collection,
                                    arrayOf(MediaStore.Downloads._ID),
                                    "${MediaStore.Downloads.DISPLAY_NAME}=?",
                                    arrayOf(filename),
                                    null
                                )

                                // If file already exists -> delete it
                                if (existingFileCursor != null && existingFileCursor.moveToFirst()) {
                                    val id = existingFileCursor.getLong(0)
                                    val existingUri = ContentUris.withAppendedId(collection, id)
                                    resolver.delete(existingUri, null, null)
                                }
                                existingFileCursor?.close()

                                // Insert new file
                                val values = ContentValues().apply {
                                    put(MediaStore.Downloads.DISPLAY_NAME, filename)
                                    put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                                }
                                val uri = resolver.insert(collection, values)
                                uri?.let {
                                    resolver.openOutputStream(it)?.use { outputStream ->
                                        outputStream.write(bytes)
                                    }
                                }
                            } else {
                                // For Android 9 and below
                                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                val file = File(downloadsDir, filename)
                                if (file.exists()) file.delete() // delete old file
                                FileOutputStream(file).use { it.write(bytes) }
                            }

                            runOnUiThread {
                                Toast.makeText(this@CaptureImage, "PDF 1 saved to Downloads", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: IOException) {
                            runOnUiThread {
                                Toast.makeText(this@CaptureImage, "File save error: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@CaptureImage, "Server error: ${response.code}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }



//download pdf2
    private fun downloadVehPdf2() {
        val chassis_No = chasisNo.text.toString()
        if (chassis_No.isEmpty()) {
            Toast.makeText(this@CaptureImage, "Enter the vin number", Toast.LENGTH_SHORT).show()
            return
        }

        val client = OkHttpClient()
        val url = "${ApiFile.APP_URL}/VehicleStock/downloadVehPdf2?chasisNo=$chassis_No"

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@CaptureImage, "Download failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val body = response.body
                    if (body != null) {
                        val bytes = body.bytes()
                        val disposition = response.header("Content-Disposition")
                        val filename = disposition?.substringAfter("filename=")?.replace("\"", "")
                            ?: "vehPdf2_${chassis_No}.pdf"  // No timestamp, fixed name

                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                val resolver = contentResolver
                                val collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI

                                // Check if file already exists
                                val existingFileCursor = resolver.query(
                                    collection,
                                    arrayOf(MediaStore.Downloads._ID),
                                    "${MediaStore.Downloads.DISPLAY_NAME}=?",
                                    arrayOf(filename),
                                    null
                                )

                                // If exists → delete it
                                if (existingFileCursor != null && existingFileCursor.moveToFirst()) {
                                    val id = existingFileCursor.getLong(0)
                                    val existingUri = ContentUris.withAppendedId(collection, id)
                                    resolver.delete(existingUri, null, null)
                                }
                                existingFileCursor?.close()

                                // Save new file
                                val values = ContentValues().apply {
                                    put(MediaStore.Downloads.DISPLAY_NAME, filename)
                                    put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                                }
                                val uri = resolver.insert(collection, values)
                                uri?.let {
                                    resolver.openOutputStream(it)?.use { outputStream ->
                                        outputStream.write(bytes)
                                    }
                                }
                            } else {
                                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                val file = File(downloadsDir, filename)
                                if (file.exists()) file.delete() // delete old file
                                FileOutputStream(file).use { it.write(bytes) }
                            }

                            runOnUiThread {
                                Toast.makeText(this@CaptureImage, "PDF 2 saved to Downloads", Toast.LENGTH_LONG).show()
                            }

                        } catch (e: IOException) {
                            runOnUiThread {
                                Toast.makeText(this@CaptureImage, "File save error: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@CaptureImage, "Server error: ${response.code}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }


//reset pdf fabs (not in use)
    private fun resetPdfFabs() {
        val rootView = findViewById<ViewGroup>(android.R.id.content)

        // Remove all dynamically added FABs by tag
        val fabTags = listOf("pdf1_fab_tag", "pdf2_fab_tag")
        for (tag in fabTags) {
            val fab = rootView.findViewWithTag<View>(tag)
            if (fab != null) {
                (fab.parent as? ViewGroup)?.removeView(fab)
            }
        }
    }

//disable click , upload and reset button onclick download button
    private fun disableButtonAndViews(){
        imageCaptureLL.visibility=View.GONE
        scanButtonLL.visibility=View.GONE
        uplResLL.visibility=View.GONE
        refreshBtnNew.visibility=View.VISIBLE
        dwnldpdfcv.visibility=View.VISIBLE
        downloadPdfCmd.visibility=View.GONE
    }
//enable clicl,upload and reset onclick refresh icon
    private fun reRefresh(){
        refreshBtnNew.visibility=View.GONE
        dwnldpdfcv.visibility=View.GONE
        imageCaptureLL.visibility=View.VISIBLE
        scanButtonLL.visibility=View.VISIBLE
        uplResLL.visibility=View.VISIBLE
        chasisNo.setText("")
        downloadPdfCmd.visibility=View.VISIBLE
    }


//suggestlist while searching for vin number
    private fun fetchVehicleList(query: String) {
        val url = "${ApiFile.APP_URL}/VehicleStock/VinNolike?chasisNo=$query"
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        Log.d("API-URL", url)

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
                            val vinList = parseVehicleListData(obj)
                            Log.d("jsonResponse",jsonResponse.toString())
                            runOnUiThread {
                                updateAutoCompleteList(vinList)
                            }
                        }
                    }
                }
            }
        })
    }

    private fun parseVehicleListData(objArray: JSONArray): List<String> {
        val vinList = mutableListOf<String>()
        for (i in 0 until objArray.length()) {
            val item = objArray.getJSONObject(i)
            val name = item.getString("chasisNo")
            vinList.add(name)
        }
        return vinList
    }

    private fun updateAutoCompleteList(vehicleNumbers: List<String>) {
        vinAdapter.clear()
        vinAdapter.addAll(vehicleNumbers)
        vinAdapter.notifyDataSetChanged()
        chasisNo.showDropDown()
    }


}