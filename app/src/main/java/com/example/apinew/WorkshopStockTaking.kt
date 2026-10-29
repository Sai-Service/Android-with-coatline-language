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
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

class WorkshopStockTaking : AppCompatActivity() {

    private lateinit var scanButton: Button
    private lateinit var VinQr: EditText
    private lateinit var VinDetails: TextView
    private lateinit var batchEditText: EditText
    private lateinit var multipleBatchNameSpinner: Spinner
    private lateinit var batchName: TextView
    private lateinit var addBtn: Button
    private lateinit var save_button: Button
    private lateinit var viewReportsButton: Button
    private lateinit var login_name: String
    private lateinit var dmsLoc: String
    private lateinit var deptName: String
    private var ouId: Int = 0
    private var locId: Int = 0
    private lateinit var location_name: String
    private lateinit var usernameText: TextView
    private lateinit var locationText: TextView
    private lateinit var homepage: ImageButton

    private lateinit var qr_result_textview: TextView
    private lateinit var refreshButton: Button
    private lateinit var fetchChassisDataButton: Button
    private lateinit var findByChassis: ImageButton
    private lateinit var chassis_no: EditText
    private lateinit var vintypeSpinner: Spinner
    private lateinit var fetchVinData2: ImageButton
    private lateinit var captureVinNo: Button
    private lateinit var enterVehNo: Button
    private lateinit var captureVehNo: Button
    private lateinit var vehNoEditText: EditText
    private lateinit var fetchVehNoData: ImageButton
    private lateinit var vehNoTextView: TextView
    private lateinit var vinNoTextView: TextView
    private val recognizer = TextRecognition.getClient(
        TextRecognizerOptions.Builder().setExecutor(
            Executors.newSingleThreadExecutor()
        ).build()
    )
    private lateinit var fetchVinNoTextView: ImageButton
    private lateinit var fetchVehNoTextView: ImageButton
    private var bestResult2: String = ""
    private lateinit var trValChassis: TextView
    private lateinit var trValVin: TextView
    private lateinit var trValVariantDesc: TextView
    private lateinit var trValFuelDesc: TextView
    private lateinit var trValModelDesc: TextView
    private lateinit var trValEngineNo: TextView
    private lateinit var trValVehStatus: TextView
    private lateinit var DRIVER_NAME: TextView
    private lateinit var STOCK_TRF_NO: TextView
    private lateinit var JOB_CARD_NO: TextView
    private lateinit var VIN: TextView
    private lateinit var CHASSIS_NUM: TextView
    private lateinit var VARIANT_CD: TextView
    private lateinit var FUEL_DESC: TextView
    private lateinit var MODEL_CD: TextView
    private lateinit var ENGINE_NO: TextView
    private lateinit var LOCATION: TextView
    private lateinit var save_button2: Button
    private lateinit var checkBatchStatus: String

    // ---------- Full-resolution camera (same as Washing Module) ----------
    private var currentPhotoPath: String? = null
    private var pendingCameraRequestCode: Int = -1

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        private const val CAMERA_REQUEST_CODE = 101      // Capture VIN No button
        private const val CAMERA_REQUEST_CODE_2 = 103    // Capture Vehicle No button
        private const val KEY_PHOTO_PATH = "current_photo_path"
    }

    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workshop_stock_taking)

        currentPhotoPath = savedInstanceState?.getString(KEY_PHOTO_PATH)

        scanButton = findViewById(R.id.scan_button)
        save_button = findViewById(R.id.save_button)
        save_button2 = findViewById(R.id.save_button2)
        VinQr = findViewById(R.id.vin_qr_edittext)
        VinDetails = findViewById(R.id.VinDetails)
        batchEditText = findViewById(R.id.batchEditText)
        multipleBatchNameSpinner = findViewById(R.id.multipleBatchNameSpinner)
        multipleBatchNameSpinner.visibility = View.GONE
        batchName = findViewById(R.id.batchNameLabel)
        addBtn = findViewById(R.id.addBtn)
        usernameText = findViewById(R.id.usernameText)
        locationText = findViewById(R.id.locationText)
        viewReportsButton = findViewById(R.id.viewReportsButton)
        homepage = findViewById(R.id.homepage)
        qr_result_textview = findViewById(R.id.qr_result_textview)
        locId = intent.getIntExtra("locId", 0)
        ouId = intent.getIntExtra("ouId", 0)
        deptName = intent.getStringExtra("deptName") ?: ""
        location_name = intent.getStringExtra("location_name") ?: ""
        dmsLoc = intent.getStringExtra("dmsLoc") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""
        VinDetails.visibility = View.GONE
        save_button.visibility = View.GONE
        refreshButton = findViewById(R.id.refreshButton)
        fetchChassisDataButton = findViewById(R.id.fetchChassisDataButton)
        chassis_no = findViewById(R.id.chassis_no)
        findByChassis = findViewById(R.id.findByChassis)
        vintypeSpinner = findViewById(R.id.vintypeSpinner)
        vintypeSpinner.visibility = View.GONE
        fetchVinData2 = findViewById(R.id.fetchVinData2)
        fetchVinData2.visibility = View.GONE

        vehNoTextView = findViewById(R.id.vehNoTextView)
        vinNoTextView = findViewById(R.id.vinNoTextView)

        trValVin = findViewById(R.id.trValVin)
        trValChassis = findViewById(R.id.trValChassis)
        trValEngineNo = findViewById(R.id.trValEngineNo)
        trValVehStatus = findViewById(R.id.trValVehStatus)
        trValFuelDesc = findViewById(R.id.trValFuelDesc)
        trValModelDesc = findViewById(R.id.trValModelDesc)
        trValVariantDesc = findViewById(R.id.trValVariantDesc)
        DRIVER_NAME = findViewById(R.id.DRIVER_NAME)
        STOCK_TRF_NO = findViewById(R.id.STOCK_TRF_NO)
        captureVinNo = findViewById(R.id.captureVinNo)
        enterVehNo = findViewById(R.id.enterVehNo)
        captureVehNo = findViewById(R.id.captureVehNo)
        vehNoEditText = findViewById(R.id.vehNoEditText)
        fetchVehNoData = findViewById(R.id.fetchVehNoData)
        VIN = findViewById(R.id.VIN)
        VARIANT_CD = findViewById(R.id.VARIANT_CD)
        FUEL_DESC = findViewById(R.id.FUEL_DESC)
        MODEL_CD = findViewById(R.id.MODEL_CD)
        LOCATION = findViewById(R.id.LOCATION)
        CHASSIS_NUM = findViewById(R.id.CHASSIS_NUM)
        ENGINE_NO = findViewById(R.id.ENGINE_NO)
        JOB_CARD_NO = findViewById(R.id.JOB_CARD_NO)

        fetchVinNoTextView = findViewById(R.id.fetchVinNoTextView)
        fetchVehNoTextView = findViewById(R.id.fetchVehNoTextView)

        chassis_no.visibility = View.GONE
        findByChassis.visibility = View.GONE

        findBybatchNameOpenStatus()

        findByChassis.setOnClickListener {
            fetchChassisData()
            VinDetails.text = "Details By Job Card Number"
        }

        fetchVehNoData.setOnClickListener {
            fetchRegNoData()
            save_button.visibility = View.INVISIBLE
            save_button2.visibility = View.VISIBLE
            VinDetails.text = "Details By Vehicle Number"
        }

        fetchVehNoTextView.setOnClickListener {
            fetchRegNoDataByCamera()
            VinDetails.text = "Details By Vehicle Number"
        }

        captureVinNo.setOnClickListener {
            vinNoTextView.visibility = View.VISIBLE
            vehNoTextView.visibility = View.GONE
            fetchVinNoTextView.visibility = View.VISIBLE
            refreshButton.visibility = View.VISIBLE
            save_button.visibility = View.INVISIBLE
            save_button2.visibility = View.INVISIBLE

            checkPermissionAndOpenCamera(CAMERA_REQUEST_CODE)
        }

        captureVehNo.setOnClickListener {
            chassis_no.visibility = View.GONE
            findByChassis.visibility = View.GONE
            vehNoEditText.visibility = View.GONE
            fetchVehNoData.visibility = View.GONE
            vehNoTextView.visibility = View.VISIBLE
            fetchVehNoTextView.visibility = View.VISIBLE
            refreshButton.visibility = View.VISIBLE
            save_button.visibility = View.INVISIBLE
            save_button2.visibility = View.INVISIBLE

            checkPermissionAndOpenCamera(CAMERA_REQUEST_CODE_2)
        }

        enterVehNo.setOnClickListener {
            vehNoEditText.visibility = View.VISIBLE
            fetchVehNoData.visibility = View.VISIBLE
            refreshButton.visibility = View.VISIBLE
            chassis_no.visibility = View.GONE
            findByChassis.visibility = View.GONE
            vehNoTextView.visibility = View.GONE
            fetchVehNoTextView.visibility = View.GONE
            save_button.visibility = View.INVISIBLE
            save_button2.visibility = View.INVISIBLE
        }

        CHASSIS_NUM.visibility = View.GONE
        VIN.visibility = View.GONE
        VARIANT_CD.visibility = View.GONE
        FUEL_DESC.visibility = View.GONE
        MODEL_CD.visibility = View.GONE
        ENGINE_NO.visibility = View.GONE
        LOCATION.visibility = View.GONE
        DRIVER_NAME.visibility = View.GONE
        STOCK_TRF_NO.visibility = View.GONE
        trValChassis.visibility = View.GONE
        trValVin.visibility = View.GONE
        trValFuelDesc.visibility = View.GONE
        trValModelDesc.visibility = View.GONE
        trValVehStatus.visibility = View.GONE
        trValEngineNo.visibility = View.GONE
        trValVariantDesc.visibility = View.GONE
        JOB_CARD_NO.visibility = View.GONE

        val currentDate: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

        usernameText.text = login_name
        locationText.text = location_name

        batchEditText.setText("$location_name-$currentDate")
        batchEditText.isEnabled = false

        addBtn.setOnClickListener {
            scanButton.visibility = View.VISIBLE
            VinDetails.visibility = View.VISIBLE
            save_button.visibility = View.VISIBLE
        }

        viewReportsButton.setOnClickListener {
            stockReports()
        }

        homepage.setOnClickListener {
            backToHome()
        }

        refreshButton.setOnClickListener {
            resetFields()
        }

        fetchChassisDataButton.setOnClickListener {
            runOnUiThread {
                chassis_no.visibility = View.VISIBLE
                findByChassis.visibility = View.VISIBLE
                vehNoEditText.visibility = View.GONE
                fetchVehNoData.visibility = View.GONE
                vehNoTextView.visibility = View.GONE
                fetchVehNoTextView.visibility = View.GONE
                refreshButton.visibility = View.VISIBLE
                save_button.visibility = View.INVISIBLE
                save_button2.visibility = View.INVISIBLE
            }
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
            Handler(Looper.getMainLooper()).postDelayed({
            }, 10000)
        }

        save_button.setOnClickListener {
            val regNo = LOCATION.text.toString().split(": ").getOrNull(1) ?: ""
            val selectedBatchName = multipleBatchNameSpinner.selectedItem?.toString() ?: ""

            when {
                selectedBatchName == "Select Batch Name" -> {
                    Toast.makeText(this, "Please select a Batch Name first", Toast.LENGTH_SHORT).show()
                }
                selectedBatchName.isNotEmpty() -> {
                    if (regNo.isNotEmpty()) {
                        saveVinData(regNo, selectedBatchName)
                    } else {
                        Toast.makeText(this, "VIN is empty", Toast.LENGTH_SHORT).show()
                    }
                }
                else -> {
                    val batchName = batchEditText.text.toString()
                    if (regNo.isNotEmpty()) {
                        saveVinData(regNo, batchName)
                    } else {
                        Toast.makeText(this, "VIN or Batch Name is empty", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        save_button2.setOnClickListener {
            val regNo = LOCATION.text.toString().split(": ").getOrNull(1) ?: ""
            val selectedBatchName = multipleBatchNameSpinner.selectedItem?.toString() ?: ""

            when {
                selectedBatchName == "Select Batch Name" -> {
                    Toast.makeText(this, "Please select a Batch Name first", Toast.LENGTH_SHORT).show()
                }
                selectedBatchName.isNotEmpty() -> {
                    if (regNo.isNotEmpty()) {
                        saveVinData2(regNo, selectedBatchName)
                    } else {
                        Toast.makeText(this, "VIN is empty", Toast.LENGTH_SHORT).show()
                    }
                }
                else -> {
                    val batchName = batchEditText.text.toString()
                    if (regNo.isNotEmpty()) {
                        saveVinData2(regNo, batchName)
                    } else {
                        Toast.makeText(this, "VIN or Batch Name is empty", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        currentPhotoPath?.let { outState.putString(KEY_PHOTO_PATH, it) }
    }

    // =====================================================================
    //  CAMERA CAPTURE (full-resolution, same approach as Washing Module)
    // =====================================================================

    private fun checkPermissionAndOpenCamera(requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            dispatchTakePictureIntent(requestCode)
        } else {
            pendingCameraRequestCode = requestCode
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (pendingCameraRequestCode != -1) {
                    dispatchTakePictureIntent(pendingCameraRequestCode)
                }
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
            pendingCameraRequestCode = -1
        }
    }

    private fun dispatchTakePictureIntent(requestCode: Int) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                ex.printStackTrace()
                null
            }
            if (photoFile != null) {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    "${applicationContext.packageName}.provider",
                    photoFile
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                startActivityForResult(takePictureIntent, requestCode)
            } else {
                Toast.makeText(this, "Unable to create image file", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
            .apply { currentPhotoPath = absolutePath }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            IntentIntegrator.REQUEST_CODE -> {
                val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
                if (result != null) {
                    if (result.contents == null) {
                        Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
                    } else {
                        val vin = result.contents
                        qr_result_textview.visibility = View.VISIBLE
                        qr_result_textview.text = vin
                        VinQr.setText(vin)
                    }
                }
            }

            CAMERA_REQUEST_CODE, CAMERA_REQUEST_CODE_2 -> {
                if (resultCode == Activity.RESULT_OK) {
                    val path = currentPhotoPath
                    val file = if (path != null) File(path) else null
                    if (file != null && file.exists()) {
                        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                        if (bitmap != null) {
                            val target = if (requestCode == CAMERA_REQUEST_CODE) vinNoTextView else vehNoTextView
                            processImageWithMultipleAttempts(bitmap, target)
                        } else {
                            Toast.makeText(this, "Unable to read captured image", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Captured image not found", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    // =====================================================================
    //  OCR PIPELINE (same as Washing Module)
    // =====================================================================

    private fun processImageWithMultipleAttempts(originalBitmap: Bitmap, resultTextView: TextView) {
        val bitmapsToTry = mutableListOf<Bitmap>()
        bitmapsToTry.add(originalBitmap)
        bitmapsToTry.add(resizeKeepAspect(originalBitmap, 1000))
        bitmapsToTry.add(adaptiveThreshold(resizeKeepAspect(originalBitmap, 1000)))

        val angles = listOf(-6f, -3f, 0f, 3f, 6f)
        val initialList = bitmapsToTry.toList()
        for (bm in initialList) {
            for (ang in angles) {
                bitmapsToTry.add(rotateBitmap(bm, ang))
            }
        }

        var foundValid = false

        fun processNext(index: Int) {
            if (index >= bitmapsToTry.size) {
                if (!foundValid) {
                    runOnUiThread {
                        Toast.makeText(this, "Cant able to read Vehicle Number", Toast.LENGTH_SHORT).show()
                    }
                }
                return
            }
            val image = InputImage.fromBitmap(bitmapsToTry[index], 0)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val candidates = extractPlateCandidates(visionText)
                    for (cand in candidates) {
                        val corrected = correctByPosition(cand)
                        val cleaned = corrected.replace("\\s+".toRegex(), "")
                        if (isValidVehicleNo(cleaned)) {
                            runOnUiThread {
                                bestResult2 = cleaned
                                resultTextView.text = cleaned
                                vehNoTextView.text = cleaned
                                vinNoTextView.text = cleaned
                                Toast.makeText(this, "Result: $cleaned", Toast.LENGTH_SHORT).show()
                            }
                            foundValid = true
                            return@addOnSuccessListener
                        }
                    }
                    if (!foundValid) processNext(index + 1)
                }
                .addOnFailureListener {
                    if (!foundValid) processNext(index + 1)
                }
        }
        processNext(0)
    }

    private fun isValidVehicleNo(s: String): Boolean {
        val regexVehicleNo  = Regex("^[A-Z]{2}\\d{2}[A-Z]{2}\\d{4}$")
        val regexVehicleNo2 = Regex("^[A-Z]{2}\\d{2}[A-Z]\\d{4}$")
        val regexVehicleNo3 = Regex("^[A-Z]{2}\\d{2}[A-Z]{3}\\d{4}$")
        val regexVehicleNo4 = Regex("^\\d{2}\\s?[A-Z]{2}\\s?\\d{4}\\s?[A-Z]$")
        return regexVehicleNo.matches(s) || regexVehicleNo2.matches(s) ||
                regexVehicleNo3.matches(s) || regexVehicleNo4.matches(s)
    }

    private fun rotateBitmap(src: Bitmap, angle: Float): Bitmap {
        val matrix = android.graphics.Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
    }

    private fun adaptiveThreshold(bitmap: Bitmap): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        val out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(w * h)
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h)
        val gray = IntArray(w * h)
        for (i in pixels.indices) {
            val p = pixels[i]
            val r = (p shr 16) and 0xFF
            val g = (p shr 8) and 0xFF
            val b = p and 0xFF
            gray[i] = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
        }
        val outGray = IntArray(w * h)
        val radius = 3
        for (y in 0 until h) {
            for (x in 0 until w) {
                var sum = 0
                var count = 0
                val y0 = maxOf(0, y - radius)
                val y1 = minOf(h - 1, y + radius)
                val x0 = maxOf(0, x - radius)
                val x1 = minOf(w - 1, x + radius)
                for (yy in y0..y1) {
                    val base = yy * w
                    for (xx in x0..x1) {
                        sum += gray[base + xx]
                        count++
                    }
                }
                val mean = if (count > 0) sum / count else gray[y * w + x]
                outGray[y * w + x] = if (gray[y * w + x] > mean - 10) 255 else 0
            }
        }
        for (i in outGray.indices) {
            val v = outGray[i]
            out.setPixel(i % w, i / w, Color.rgb(v, v, v))
        }
        return out
    }

    private fun resizeKeepAspect(bitmap: Bitmap, targetWidth: Int): Bitmap {
        val aspect = bitmap.width.toDouble() / bitmap.height.toDouble()
        val targetHeight = (targetWidth / aspect).toInt()
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }

    private fun extractPlateCandidates(visionText: Text): List<String> {
        val candidateRegex = Regex("[A-Z0-9\\s]{8,12}")
        val results = mutableListOf<String>()
        for (block in visionText.textBlocks) {
            for (line in block.lines) {
                val lineText = line.text.replace("[^A-Za-z0-9\\s]".toRegex(), "").uppercase(Locale.US)
                candidateRegex.findAll(lineText).forEach {
                    val token = it.value.replace("\\s+".toRegex(), "")
                    if (token.length >= 8) results.add(token)
                }
                for (elem in line.elements) {
                    val e = elem.text.replace("[^A-Za-z0-9]".toRegex(), "").uppercase(Locale.US)
                    if (e.length in 6..12) {
                        if (candidateRegex.matches(e) || e.length >= 8)
                            results.add(e.replace("\\s+".toRegex(), ""))
                    }
                }
            }
        }
        val whole = visionText.text.replace("[^A-Za-z0-9\\s]".toRegex(), "")
            .replace("\\s+".toRegex(), "").uppercase(Locale.US)
        if (whole.length in 8..12) results.add(whole)
        return results.distinct()
    }

    private fun correctByPosition(candidate: String): String {
        val s = candidate.uppercase(Locale.US).trim()

        fun applyMask(original: String, digitMask: BooleanArray): String {
            val arr = original.toCharArray()
            for (i in arr.indices) {
                if (i >= digitMask.size) break
                if (digitMask[i]) {
                    when (arr[i]) {
                        'O', 'Q', 'D' -> arr[i] = '0'
                        'I', 'L' -> arr[i] = '1'
                        'S' -> arr[i] = '5'
                        'Z' -> arr[i] = '2'
                        'B' -> arr[i] = '8'
                    }
                } else {
                    if (!arr[i].isLetter()) {
                        when (arr[i]) {
                            '0' -> arr[i] = 'O'
                            '1' -> arr[i] = 'I'
                            '8' -> arr[i] = 'B'
                        }
                    }
                }
            }
            return String(arr)
        }

        val possibleCorrections = mutableListOf<String>()
        val len = s.length

        if (len == 10) {
            val mask = BooleanArray(len)
            mask[2] = true; mask[3] = true; mask[6] = true; mask[7] = true; mask[8] = true; mask[9] = true
            possibleCorrections.add(applyMask(s, mask))
        }
        if (len == 9) {
            val mask = BooleanArray(len)
            mask[2] = true; mask[3] = true
            for (i in 5 until len) mask[i] = true
            possibleCorrections.add(applyMask(s, mask))
        }
        if (len == 11) {
            val mask = BooleanArray(len)
            mask[2] = true; mask[3] = true
            for (i in 7 until len) mask[i] = true
            possibleCorrections.add(applyMask(s, mask))
        }
        if (len in 8..12) {
            val mask = BooleanArray(len)
            if (len >= 6) {
                for (i in (len - 4) until len) mask[i] = true
                if (len > 3) { mask[2] = true; mask[3] = true }
            }
            possibleCorrections.add(applyMask(s, mask))
        }

        for (test in possibleCorrections.distinct()) {
            val cleaned = test.replace("\\s+".toRegex(), "")
            if (isValidVehicleNo(cleaned)) return cleaned
        }
        return s.replace("\\s+".toRegex(), "")
    }

    // =====================================================================
    //  BATCH HANDLING
    // =====================================================================

    private fun findBybatchNameOpenStatus() {
        val LocationName = location_name
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.ERP_APP_URL}/srAccounts/findSrBatchNameStatus?location=$LocationName")
            .build()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                val jsonObject = JSONObject(jsonData.toString())
                val jsonArray = jsonObject.getJSONArray("obj")
                val batStatus = jsonArray.getJSONObject(0)
                checkBatchStatus = batStatus.getString("BATCHSTATUS")

                runOnUiThread {
                    if (checkBatchStatus.equals("open", ignoreCase = true)) {
                        multipleBatchNameSpinner.visibility = View.VISIBLE
                        batchEditText.visibility = View.INVISIBLE
                        fetchBatchData()
                    } else if (checkBatchStatus.equals("Closed", ignoreCase = true)) {
                        findBybatchNameStatus()
                        save_button.visibility = View.INVISIBLE
                        save_button2.visibility = View.INVISIBLE
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun findBybatchNameStatus() {
        val currentDate: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val batchName = "$location_name-$currentDate"
        val LocationName = location_name
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.ERP_APP_URL}/srAccounts/findBatchNameStatus?batchName=$batchName&location=$LocationName")
            .build()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()

                val jsonObject = JSONObject(jsonData.toString())
                val jsonArray = jsonObject.getJSONArray("obj")
                val batStatus = jsonArray.getJSONObject(0)
                val batchStatus = batStatus.getString("batchStatus")
                runOnUiThread {
                    if (jsonArray.length() == 0) {
                        batchEditText.visibility = View.VISIBLE
                        multipleBatchNameSpinner.visibility = View.INVISIBLE
                    } else {
                        batchEditText.visibility = View.GONE
                        multipleBatchNameSpinner.visibility = View.VISIBLE
                        fetchBatchData()
                    }

                    if (batchName == batchEditText.text.toString() && batchStatus == "Closed") {
                        save_button.visibility = View.INVISIBLE
                        save_button2.visibility = View.INVISIBLE
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun fetchBatchData() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${ApiFile.ERP_APP_URL}/srAccounts/srBatchNameOpen?location=${location_name}")
            .build()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val batchCodeList = parseCities(it)
                    runOnUiThread {
                        val adapter = ArrayAdapter(
                            this@WorkshopStockTaking,
                            android.R.layout.simple_spinner_item,
                            batchCodeList
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        multipleBatchNameSpinner.adapter = adapter
                        if (batchCodeList.size > 1) {
                            multipleBatchNameSpinner.visibility = View.VISIBLE
                            batchEditText.visibility = View.GONE
                        } else {
                            multipleBatchNameSpinner.visibility = View.GONE
                            batchEditText.visibility = View.VISIBLE
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun parseCities(jsonData: String): List<String> {
        val batchCodeList = mutableListOf<String>()
        try {
            val jsonObject = JSONObject(jsonData)
            val jsonArray = jsonObject.getJSONArray("obj")
            batchCodeList.add("Select Batch Name")
            for (i in 0 until jsonArray.length()) {
                val batchList = jsonArray.getJSONObject(i)
                batchCodeList.add(batchList.getString("BATCHNAME"))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return batchCodeList
    }

    // =====================================================================
    //  RESET
    // =====================================================================

    private fun resetFields() {
        chassis_no.setText("")
        vintypeSpinner.visibility = View.GONE
        qr_result_textview.text = ""
        JOB_CARD_NO.visibility = View.GONE
        JOB_CARD_NO.text = ""
        VinDetails.visibility = View.INVISIBLE
        VinDetails.text = ""
        findByChassis.visibility = View.INVISIBLE
        chassis_no.visibility = View.GONE
        resetVintypeSpinner()
        save_button.visibility = View.INVISIBLE
        save_button2.visibility = View.INVISIBLE
        vintypeSpinner.setSelection(0)
        fetchVinData2.visibility = View.INVISIBLE
        bestResult2 = ""

        fetchVinNoTextView.visibility = View.GONE
        fetchVehNoTextView.visibility = View.GONE
        vehNoTextView.text = ""
        vehNoTextView.visibility = View.GONE
        vinNoTextView.visibility = View.GONE
        vehNoEditText.visibility = View.GONE
        fetchVehNoData.visibility = View.GONE
        vehNoEditText.setText("")
        vinNoTextView.text = ""
        DRIVER_NAME.visibility = View.GONE
        DRIVER_NAME.text = ""
        STOCK_TRF_NO.visibility = View.GONE
        STOCK_TRF_NO.text = ""
        CHASSIS_NUM.visibility = View.GONE
        CHASSIS_NUM.text = ""
        VIN.visibility = View.GONE
        VIN.text = ""
        VARIANT_CD.visibility = View.GONE
        VARIANT_CD.text = ""
        FUEL_DESC.visibility = View.GONE
        FUEL_DESC.text = ""
        MODEL_CD.visibility = View.GONE
        MODEL_CD.text = ""
        ENGINE_NO.visibility = View.GONE
        ENGINE_NO.text = ""
        LOCATION.visibility = View.GONE
        LOCATION.text = ""
        trValChassis.visibility = View.GONE
        trValChassis.text = ""
        trValVin.visibility = View.GONE
        trValVin.text = ""
        trValFuelDesc.visibility = View.GONE
        trValFuelDesc.text = ""
        trValModelDesc.visibility = View.GONE
        trValModelDesc.text = ""
        trValVehStatus.visibility = View.GONE
        trValVehStatus.text = ""
        trValEngineNo.visibility = View.GONE
        trValEngineNo.text = ""
        trValVariantDesc.visibility = View.GONE
        trValVariantDesc.text = ""
    }

    private fun resetVintypeSpinner() {
        val vintypeItems = listOf("Select Batch Name")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, vintypeItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        vintypeSpinner.adapter = adapter
        vintypeSpinner.setSelection(0)
    }

    private fun parseVindata(objArray: String): List<String> {
        val parseVindataList = mutableListOf<String>()
        try {
            val jsonObject = JSONObject(objArray)
            val jsonArray = jsonObject.getJSONArray("obj")
            parseVindataList.add("Select Vin")
            for (i in 0 until jsonArray.length()) {
                parseVindataList.add(jsonArray.getJSONObject(i).getString("VIN"))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return parseVindataList
    }

    // =====================================================================
    //  FETCH DETAILS
    // =====================================================================

    private fun parseStockItem(stockItem: JSONObject): regNoDataStkTake2 {
        return regNoDataStkTake2(
            CHASSISNO = stockItem.optString("CHASSISNO","-"),
//            VARIANT = stockItem.optString("VARIANT","-"),
//            MODELDESC = stockItem.optString("MODELDESC","-"),
            VIN = stockItem.optString("VIN","-"),
            ENGINNO = stockItem.optString("ENGINNO","-"),
            JOBCARDNO = stockItem.optString("JOBCARDNO","-"),
//            VEH_STATUS = stockItem.optString("VEH_STATUS","-"),
            REGNO = stockItem.optString("REGNO","-"),
//            PHYSICALLOCATION = stockItem.optString("PHYSICALLOCATION","-"),
//            STATUS = stockItem.optString("STATUS","-"),
//            FUEL_DESC = stockItem.optString("FUEL_DESC","-"),
//            COLOR = stockItem.optString("COLOR","-"),
//            COLORDESC = stockItem.optString("COLORDESC","-"),
            MODEL = stockItem.optString("MODEL","-"),
            SERVICEADVISOR = stockItem.optString("SERVICEADVISOR","-")
        )
    }

    private fun showDetailViews() {
        qr_result_textview.visibility = View.VISIBLE
        VinDetails.visibility = View.VISIBLE
        refreshButton.visibility = View.VISIBLE
        CHASSIS_NUM.visibility = View.GONE
        VIN.visibility = View.GONE
        VARIANT_CD.visibility = View.GONE
        FUEL_DESC.visibility = View.GONE
        MODEL_CD.visibility = View.GONE
        ENGINE_NO.visibility = View.GONE
        LOCATION.visibility = View.VISIBLE
        DRIVER_NAME.visibility = View.VISIBLE
        STOCK_TRF_NO.visibility = View.GONE
        JOB_CARD_NO.visibility = View.VISIBLE
        trValChassis.visibility = View.VISIBLE
        trValVin.visibility = View.VISIBLE
        trValFuelDesc.visibility = View.GONE
        trValModelDesc.visibility = View.VISIBLE
        trValVehStatus.visibility = View.VISIBLE
        trValEngineNo.visibility = View.VISIBLE
        trValVariantDesc.visibility = View.VISIBLE
    }

    private fun fetchJcData() {
        val client = OkHttpClient()
        val chassis = chassis_no.text.toString()
        val url = ApiFile.ERP_APP_URL + "/srAccounts/srAccDetailsByJobCardNo?jobCardNo=$chassis"
        val request = Request.Builder().url(url).build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val stockItem = JSONObject(it).getJSONArray("obj").getJSONObject(0)
                    val regData = parseStockItem(stockItem)
                    runOnUiThread {
                        populateFields(regData)
                        showDetailViews()
                        Toast.makeText(
                            this@WorkshopStockTaking,
                            "Details found Successfully \n for Job Card No: $chassis",
                            Toast.LENGTH_LONG
                        ).show()
                        save_button.visibility = View.INVISIBLE
                        save_button2.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@WorkshopStockTaking,
                        "Failed to fetch details for Job Card No: $chassis",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun fetchChassisData() {
        val client = OkHttpClient()
        val chassis = chassis_no.text.toString()
        val url = ApiFile.ERP_APP_URL + "/srAccounts/srAccDetailsByJobCardNo?jobCardNo=$chassis&locCd=$dmsLoc"
        val request = Request.Builder().url(url).build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val stockItem = JSONObject(it).getJSONArray("obj").getJSONObject(0)
                    val regData = parseStockItem(stockItem)
                    runOnUiThread {
                        populateFields(regData)
                        showDetailViews()
                        Toast.makeText(
                            this@WorkshopStockTaking,
                            "Details found Successfully \n for Job Card No: $chassis",
                            Toast.LENGTH_LONG
                        ).show()
                        save_button.visibility = View.INVISIBLE
                        save_button2.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@WorkshopStockTaking,
                        "Failed to fetch details for Job Card No: $chassis",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun fetchRegNoDataByCamera() {
        val client = OkHttpClient()
        val vehicleNo2 = vehNoTextView.text.toString().trim()
        if (vehicleNo2.isEmpty()) {
            Toast.makeText(this, "Please capture the vehicle number first", Toast.LENGTH_SHORT).show()
            return
        }
        val url = ApiFile.ERP_APP_URL + "/srAccounts/srAccDetailsByRegNo?regNo=$vehicleNo2"
        val request = Request.Builder().url(url).build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val stockItem = JSONObject(it).getJSONArray("obj").getJSONObject(0)
                    val regData = parseStockItem(stockItem)
                    runOnUiThread {
                        populateFields(regData)
                        showDetailViews()
                        Toast.makeText(
                            this@WorkshopStockTaking,
                            "Details found Successfully \n for vehicle no.: $vehicleNo2",
                            Toast.LENGTH_LONG
                        ).show()
                        save_button.visibility = View.VISIBLE
                        save_button2.visibility = View.INVISIBLE
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@WorkshopStockTaking,
                        "Failed to fetch details for vehicle no.: $vehicleNo2",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun fetchRegNoData() {
        val client = OkHttpClient()
        val vehicleNo = vehNoEditText.text.toString()
        val url = ApiFile.ERP_APP_URL + "/srAccounts/srAccDetailsByRegNo?regNo=$vehicleNo"
        val request = Request.Builder().url(url).build()
        Log.d("URL",request.toString())

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                Log.d("jsonData",jsonData.toString())
                jsonData?.let {
                    val stockItem = JSONObject(it).getJSONArray("obj").getJSONObject(0)
                    val regData = parseStockItem(stockItem)
                    runOnUiThread {
                        populateFields(regData)
                        showDetailViews()
                        Toast.makeText(
                            this@WorkshopStockTaking,
                            "Details found Successfully \n for vehicle no.: $vehicleNo",
                            Toast.LENGTH_LONG
                        ).show()
                        save_button.visibility = View.INVISIBLE
                        save_button2.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@WorkshopStockTaking,
                        "Failed to fetch details for vehicle no.: $vehicleNo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun stockReports() {
        val intent = Intent(this, WorkshopViewReports::class.java)
        intent.putExtra("login_name", login_name)
        intent.putExtra("locId", locId)
        startActivity(intent)
    }

    private fun populateFields(regData: regNoDataStkTake2) {
        JOB_CARD_NO.text = "JOB CARD NO.: ${regData.JOBCARDNO}"
        LOCATION.text = "VEHICLE NO.: ${regData.REGNO}"
        trValVin.text = "VIN: ${regData.VIN}"
        trValChassis.text = "CHASSIS NO.: ${regData.CHASSISNO}"
        trValEngineNo.text = "ENGINE NO: ${regData.ENGINNO}"
        trValModelDesc.text = "MODEL DESC: ${regData.MODEL}"
        trValVehStatus.text = "SERV ADV.: ${regData.SERVICEADVISOR}"
//        trValVariantDesc.text = "FUEL DESC: ${regData.FUEL_DESC}"
//        DRIVER_NAME.text = "LOCATION: ${regData.PHYSICALLOCATION}"
    }

    // =====================================================================
    //  SAVE
    // =====================================================================

    private fun valueAfterColon(tv: TextView): String =
        tv.text.toString().split(": ").getOrNull(1) ?: ""

    private fun buildSaveJson(regNo: String, batchName: String): JSONObject {
        val jsonObject = JSONObject()
        val currentDateTime = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        val formattedDate = formatter.format(currentDateTime.time)
        currentDateTime.add(Calendar.DAY_OF_MONTH, 1)
        val batchCodeEndDate = formatter.format(currentDateTime.time)

        jsonObject.put("regNo", regNo)
        jsonObject.put("jobCardNo", valueAfterColon(JOB_CARD_NO))
        jsonObject.put("chassisNo", valueAfterColon(trValChassis))
        jsonObject.put("vin", valueAfterColon(trValVin))
        jsonObject.put("fuelDesc", valueAfterColon(trValVariantDesc))
        jsonObject.put("modelDesc", valueAfterColon(trValModelDesc))
        jsonObject.put("engineNo", valueAfterColon(trValEngineNo))
        jsonObject.put("colour", valueAfterColon(trValVehStatus))
        jsonObject.put("batchName", batchName)
        jsonObject.put("locationId", locId)
        jsonObject.put("locationName", locationText.text.toString())
        jsonObject.put("createdBy", login_name)
        jsonObject.put("batchStatus", "open")
        jsonObject.put("batchCreationDate", formattedDate)
        jsonObject.put("batchCodeEndDate", batchCodeEndDate)
        jsonObject.put("updatedBy", login_name)
        return jsonObject
    }

    private fun postSave(url: String, jsonObject: JSONObject) {
        val client = OkHttpClient()
        val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder().url(url).post(requestBody).build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseCode = response.code
                val responseBody = response.body?.string()

                runOnUiThread {
                    if (responseBody != null) {
                        val message = try {
                            JSONObject(responseBody).optString("message", "")
                        } catch (e: JSONException) {
                            ""
                        }
                        when {
                            message.contains("Registration No already exists for this batchName", ignoreCase = true) -> {
                                Toast.makeText(
                                    this@WorkshopStockTaking,
                                    "Stock taking is already completed for this vehicle",
                                    Toast.LENGTH_LONG
                                ).show()
                                clearFields()
                            }
                            responseCode == 200 -> {
                                Toast.makeText(
                                    this@WorkshopStockTaking,
                                    "Data saved successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                                clearFields()
                            }
                            else -> {
                                Toast.makeText(
                                    this@WorkshopStockTaking,
                                    "Failed to save data. Error code: $responseCode",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@WorkshopStockTaking,
                            "No response from server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@WorkshopStockTaking,
                        "Error saving data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun saveVinData(regNo: String, batchName: String) {
        postSave(ApiFile.ERP_APP_URL + "/srAccounts/srBatchNameScan", buildSaveJson(regNo, batchName))
    }

    private fun saveVinData2(regNo: String, batchName: String) {
        postSave(ApiFile.ERP_APP_URL + "/srAccounts/srBatchNameManual", buildSaveJson(regNo, batchName))
    }

    private fun clearFields() {
        resetFields()
    }

    private fun backToHome() {
        finish()
    }

    data class regNoDataStkTake2(
        val CHASSISNO: String,
//        val VEH_STATUS: String,
        val REGNO: String,
        val VIN: String,
//        val MODELDESC: String,
        val ENGINNO: String,
//        val VARIANT: String,
//        val STATUS: String,
//        val PHYSICALLOCATION: String,
//        val FUEL_DESC: String,
//        val COLOR: String,
//        val COLORDESC: String,
        val JOBCARDNO: String,
        val MODEL : String,
        val SERVICEADVISOR : String
    )
}