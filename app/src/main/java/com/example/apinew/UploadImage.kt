package com.example.apinew

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException

class UploadImage : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var btnOpenCamera: Button
    private lateinit var btnDelete: Button
    private lateinit var btnUpload: Button
    private lateinit var qrBtn: Button
    private lateinit var chassis: TextView
    private lateinit var chassisTextView: TextView
    private var photoUri: Uri? = null
    private var photoFile: File? = null
    private var chassisNo: String? = null
    private lateinit var qrResultTextView: TextView

    private lateinit var attributeTextView: TextView
    private lateinit var attribute1: String
    private lateinit var login_name: String
    private lateinit var progressBar:ProgressBar
    private lateinit var uploadingTextView:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_image)

        attribute1 = intent.getStringExtra("attribute1") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""

        imageView = findViewById(R.id.imageView)
        btnOpenCamera = findViewById(R.id.btnOpenCamera)
        btnDelete = findViewById(R.id.btnDelete)
        btnUpload = findViewById(R.id.btnUpload)
        chassisTextView = findViewById(R.id.chassisTextView)
        qrBtn = findViewById(R.id.qrBtn)
        qrResultTextView = findViewById(R.id.qrResultTextView)
        attributeTextView = findViewById(R.id.attributeTextView)
        attributeTextView.text = attribute1
        progressBar=findViewById(R.id.progressBar)
        uploadingTextView=findViewById(R.id.uploadingTextView)

        progressBar.visibility=View.GONE
        uploadingTextView.visibility=View.GONE

        chassisNo = intent.getStringExtra("CHASSIS_NO")
        chassisTextView.text = chassisNo

        btnOpenCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
            } else {
                openCamera()
            }
        }

        qrBtn.setOnClickListener {
            rd_to_qr()
        }

        btnDelete.setOnClickListener {
            imageView.setImageURI(null)
            photoUri = null
            photoFile = null
            btnDelete.visibility = Button.GONE
            btnUpload.visibility = Button.GONE
        }

        btnUpload.setOnClickListener {
            photoFile?.let { file ->
                chassisNo?.let { chassisNo ->
                    uploadImage(file, chassisNo)
                }
            }
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

    private fun startQrScanner() {
        val qrScan = IntentIntegrator(this)
        qrScan.initiateScan()
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
            btnDelete.visibility = Button.VISIBLE
            btnUpload.visibility = Button.VISIBLE
        } else {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
                    qrResultTextView.text = result.contents
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun rd_to_qr() {
        val intent = Intent(this, QrScanner::class.java)
        startActivity(intent)
    }

    private fun uploadImage(file: File, chassisNo: String) {
        val client = OkHttpClient()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file.name, RequestBody.create("image/jpeg".toMediaTypeOrNull(), file))
            .addFormDataPart("chassis_no", chassisNo)
            .addFormDataPart("lastUploadedBy", login_name)
            .build()

        val request = Request.Builder()
            .url("${ApiFile.APP_URL}/invstock/upload")
            .post(requestBody)
            .build()

        progressBar.visibility=View.VISIBLE
        uploadingTextView.visibility=View.VISIBLE


        Log.d("Url", requestBody.toString())

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseCode = response.code
                val responseBody = response.body?.string()

                Log.d("upload image", "Response Code: $responseCode")

                runOnUiThread {
                    if (responseBody != null) {
                        val jsonObject = JSONObject(responseBody)
                        val message = jsonObject.optString("message", "")

                        when {
                            message.contains("Images already uploaded for $chassisNo", ignoreCase = true) -> {
                                Toast.makeText(this@UploadImage, "Image already present for this vehicle!", Toast.LENGTH_LONG).show()
                                val intent = Intent(this@UploadImage, ChassisActivity::class.java)
                                intent.putExtra("CHASSIS_NO", chassisNo)
                                intent.putExtra("login_name", login_name)
                                progressBar.visibility=View.GONE
                                uploadingTextView.visibility=View.GONE
                                startActivity(intent)
                                finish()
                            }
                            responseCode == 200 -> {
                                Toast.makeText(this@UploadImage, "Image uploaded successfully", Toast.LENGTH_LONG).show()
                                val intent = Intent(this@UploadImage, ChassisActivity::class.java)
                                intent.putExtra("CHASSIS_NO", chassisNo)
                                intent.putExtra("login_name", login_name)
                                progressBar.visibility=View.GONE
                                uploadingTextView.visibility=View.GONE
                                startActivity(intent)
                                finish()
                            }
                            else -> {
                                progressBar.visibility=View.GONE
                                uploadingTextView.visibility=View.GONE
                                Toast.makeText(this@UploadImage, "Failed to upload image. Error code: $responseCode", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        progressBar.visibility=View.GONE
                        uploadingTextView.visibility=View.GONE
                        Toast.makeText(this@UploadImage, "No response from server", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Upload image", "Error: ${e.message}")
                runOnUiThread {
                    progressBar.visibility=View.GONE
                    uploadingTextView.visibility=View.GONE
                    Toast.makeText(this@UploadImage, "Error saving image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}















