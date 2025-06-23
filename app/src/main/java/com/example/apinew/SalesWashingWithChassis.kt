package com.example.apinew


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.Executors
import kotlin.math.max
import kotlin.math.min
import android.app.TimePickerDialog
import android.text.InputFilter
import android.widget.CheckBox
import android.widget.LinearLayout
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class SalesWashingWithChassis : AppCompatActivity() {
    private lateinit var login_name: String
    private lateinit var deptName: String
    private lateinit var attribute1: String
    private var ouId: Int = 0
    private var locId: Int = 0
    private lateinit var location_name: String
    private lateinit var username:TextView
    private lateinit var locIdTxt:TextView
    private lateinit var deptIntent:TextView

    private lateinit var washingHistory:TextView
    private lateinit var washInLL: View
    private lateinit var washIn:TextView
    private lateinit var washOut:TextView
    private lateinit var captureVehNumber:View
    private lateinit var captureRegNoCamera:ImageButton
    private lateinit var vehNoEnterLL:View
    private lateinit var enterVehNumber:EditText
    private lateinit var vehWashButtonIn:ImageButton
    private lateinit var vehWashButtonOut:ImageButton
    private lateinit var regNoDetails:TextView
    private lateinit var bestResult2: String
    private lateinit var washStageNumber:TextView
    private lateinit var washStageNumberLov:Spinner
    private lateinit var washStageType:TextView
    private lateinit var washStageTypeLov:Spinner
    private lateinit var vinNo:String
    private lateinit var regNo:String
    private lateinit var engineNo:String
    private lateinit var chassisNo:String
    private lateinit var custName:String
    private lateinit var model:String
    private lateinit var serviceAdvisor:String
    private lateinit var vehWashNo:String
    private lateinit var resetFields:TextView
    private lateinit var submitButton:TextView
    private lateinit var dateTimeToSend:String
    private lateinit var submitButtonOut:TextView

    //newly added
    private lateinit var washInByChassisLL:View
    private lateinit var washInByChassis:TextView
    private lateinit var washOutByChassis:TextView
    private lateinit var chassisNoEnterLL:View
    private lateinit var enterChassisNumber:EditText
    private lateinit var chassisWashButtonIn:ImageButton
    private lateinit var chassisWashButtonOut:ImageButton




    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        private const val CAMERA_REQUEST_CODE = 101
        private const val CAMERA_REQUEST_CODE_2 = 103
    }
    private val recognizer = TextRecognition.getClient(
        TextRecognizerOptions.Builder().setExecutor(
            Executors.newSingleThreadExecutor()
        ).build()
    )

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales_washing_with_chassis)

        username=findViewById(R.id.username)
        locIdTxt=findViewById(R.id.locIdTxt)
        deptIntent=findViewById(R.id.deptIntent)

        locId = intent.getIntExtra("locId", 0)
        ouId = intent.getIntExtra("ouId", 0)
        deptName = intent.getStringExtra("deptName") ?: ""
        location_name = intent.getStringExtra("location_name") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""
        attribute1 = intent.getStringExtra("attribute1") ?: ""

        username.text=login_name
        locIdTxt.text= location_name
        deptIntent.text=deptName


        washingHistory=findViewById(R.id.washingHistory)
        washInLL=findViewById(R.id.washInLL)
        washIn=findViewById(R.id.washIn)
        washOut=findViewById(R.id.washOut)
        captureVehNumber=findViewById(R.id.captureVehNumber)
        captureRegNoCamera=findViewById(R.id.captureRegNoCamera)
        vehNoEnterLL=findViewById(R.id.vehNoEnterLL)
        enterVehNumber=findViewById(R.id.enterVehNumber)
        vehWashButtonIn=findViewById(R.id.vehWashButtonIn)
        vehWashButtonOut=findViewById(R.id.vehWashButtonOut)
        regNoDetails=findViewById(R.id.regNoDetails)
        washStageNumber=findViewById(R.id.washStageNumber)
        washStageNumberLov=findViewById(R.id.washStageNumberLov)
        washStageType=findViewById(R.id.washStageType)
        washStageTypeLov=findViewById(R.id.washStageTypeLov)
        resetFields=findViewById(R.id.resetFields)
        submitButton=findViewById(R.id.submitButton)
        submitButtonOut=findViewById(R.id.submitButtonOut)

        //newly added
        washInByChassisLL=findViewById(R.id.washInByChassisLL)
        washInByChassisLL.visibility=View.GONE
        washInByChassis=findViewById(R.id.washInByChassis)
        washOutByChassis=findViewById(R.id.washOutByChassis)
        chassisNoEnterLL=findViewById(R.id.chassisNoEnterLL)
        enterChassisNumber=findViewById(R.id.enterChassisNumber)
        chassisWashButtonIn=findViewById(R.id.chassisWashButtonIn)
        chassisWashButtonOut=findViewById(R.id.chassisWashButtonOut)



        washStageTypeLov.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        regNoDetails.visibility=View.GONE
        captureVehNumber.visibility=View.GONE
        vehNoEnterLL.visibility=View.GONE
        washStageNumber.visibility=View.GONE
        washStageNumberLov.visibility=View.GONE
        washStageType.visibility=View.GONE
        washStageTypeLov.visibility=View.GONE
        resetFields.visibility=View.GONE
        submitButton.visibility=View.GONE
        vehWashButtonOut.visibility=View.GONE
        submitButtonOut.visibility=View.GONE
//        washInByChassis.visibility=View.GONE
//        washOutByChassis.visibility=View.GONE
        chassisNoEnterLL.visibility=View.GONE
        enterChassisNumber.visibility=View.GONE
        chassisWashButtonIn.visibility=View.GONE
        chassisWashButtonOut.visibility=View.GONE


//        if (deptName=="WASHING") {
//            washInByChassisLL.visibility=View.GONE
//        } else {
//            washInByChassisLL.visibility=View.VISIBLE
//        }

//        val noSpaceFilter = InputFilter { source, _, _, _, _, _ ->
//            if (source.any { it.isWhitespace() }) "" else null
//        }

        val alphaNumericFilter = InputFilter { source, _, _, _, _, _ ->
            if (source.all { it.isLetterOrDigit() }) null else ""
        }

        val lengthFilter = InputFilter.LengthFilter(10)



        val enterVehNumber = findViewById<EditText>(R.id.enterVehNumber)
        enterVehNumber.filters = arrayOf(alphaNumericFilter,lengthFilter)

        resetFields.setOnClickListener{
            refreshData()
        }

//        washIn.setOnClickListener{
//            Toast.makeText(this@SalesWashingWithChassis,"Clicked on Wash In Button",Toast.LENGTH_SHORT).show()
//            captureVehNumber.visibility=View.VISIBLE
//            vehNoEnterLL.visibility=View.VISIBLE
//            enterVehNumber.visibility=View.VISIBLE
//            vehWashButtonIn.visibility=View.VISIBLE
//            vehWashButtonOut.visibility=View.GONE
//            chassisNoEnterLL.visibility=View.GONE
//        }

        washIn.setOnClickListener {
            val options = arrayOf("By Vehicle Number", "By Chassis Number")

            val builder = AlertDialog.Builder(this@SalesWashingWithChassis)
            builder.setTitle("Select Wash In Method")

            builder.setItems(options) { _, which ->
                when (which) {
                    0 -> { // Vehicle Number - IN
                        Toast.makeText(this@SalesWashingWithChassis, "Clicked on Wash In by Vehicle Button", Toast.LENGTH_SHORT).show()
                        captureVehNumber.visibility = View.VISIBLE
                        vehNoEnterLL.visibility = View.VISIBLE
                        enterVehNumber.visibility = View.VISIBLE
                        vehWashButtonIn.visibility = View.VISIBLE
                        vehWashButtonOut.visibility = View.GONE
                        chassisNoEnterLL.visibility = View.GONE
                    }
                    1 -> { //Chassis Number - IN
                        Toast.makeText(this@SalesWashingWithChassis, "Clicked on Wash In by Chassis Button", Toast.LENGTH_SHORT).show()
                        captureVehNumber.visibility = View.GONE
                        chassisNoEnterLL.visibility = View.VISIBLE
                        enterChassisNumber.visibility = View.VISIBLE
                        chassisWashButtonIn.visibility = View.VISIBLE
                        chassisWashButtonOut.visibility = View.GONE
                        vehNoEnterLL.visibility = View.GONE
                    }
                }
            }
            builder.setNegativeButton("Cancel", null)
            builder.show()
        }


        washOut.setOnClickListener {
            val options = arrayOf("By Vehicle Number", "By Chassis Number")

            val builder = AlertDialog.Builder(this@SalesWashingWithChassis)
            builder.setTitle("Select Wash Out Method")

            builder.setItems(options) { _, which ->
                when (which) {
                    0 -> { //Vehicle Number - OUT
                        Toast.makeText(this@SalesWashingWithChassis,"Clicked on Wash Out By Vehicle Number",Toast.LENGTH_SHORT).show()
                        captureVehNumber.visibility=View.VISIBLE
                        vehNoEnterLL.visibility=View.VISIBLE
                        enterVehNumber.visibility=View.VISIBLE
                        vehWashButtonIn.visibility=View.GONE
                        vehWashButtonOut.visibility=View.VISIBLE
                        chassisNoEnterLL.visibility=View.GONE
                    }
                    1 -> { //Chassis Number - OUT
                        Toast.makeText(this@SalesWashingWithChassis,"Clicked on Wash Out by Chassis",Toast.LENGTH_SHORT).show()
                        captureVehNumber.visibility=View.GONE
                        chassisNoEnterLL.visibility=View.VISIBLE
                        enterChassisNumber.visibility=View.VISIBLE
                        chassisWashButtonIn.visibility=View.GONE
                        chassisWashButtonOut.visibility=View.VISIBLE
                        vehNoEnterLL.visibility=View.GONE
                    }
                }
            }

            builder.setNegativeButton("Cancel", null)
            builder.show()
        }


//        washOut.setOnClickListener{
//            Toast.makeText(this@SalesWashingWithChassis,"Clicked on Wash Out Button",Toast.LENGTH_SHORT).show()
//            captureVehNumber.visibility=View.VISIBLE
//            vehNoEnterLL.visibility=View.VISIBLE
//            enterVehNumber.visibility=View.VISIBLE
//            vehWashButtonIn.visibility=View.GONE
//            vehWashButtonOut.visibility=View.VISIBLE
//            chassisNoEnterLL.visibility=View.GONE
//        }

//        washInByChassis.setOnClickListener {
//            Toast.makeText(this@SalesWashingWithChassis,"Clicked on Wash In by Chassis Button",Toast.LENGTH_SHORT).show()
//            captureVehNumber.visibility=View.GONE
//            chassisNoEnterLL.visibility=View.VISIBLE
//            enterChassisNumber.visibility=View.VISIBLE
//            chassisWashButtonIn.visibility=View.VISIBLE
//            chassisWashButtonOut.visibility=View.GONE
//            vehNoEnterLL.visibility=View.GONE
//        }
//
//        washOutByChassis.setOnClickListener {
//            Toast.makeText(this@SalesWashingWithChassis,"Clicked on Wash Out by Chassis Button",Toast.LENGTH_SHORT).show()
//            captureVehNumber.visibility=View.GONE
//            chassisNoEnterLL.visibility=View.VISIBLE
//            enterChassisNumber.visibility=View.VISIBLE
//            chassisWashButtonIn.visibility=View.GONE
//            chassisWashButtonOut.visibility=View.VISIBLE
//            vehNoEnterLL.visibility=View.GONE
//        }

        vehWashButtonIn.setOnClickListener {
            detailsForVehicleInFirstTime()
        }

        vehWashButtonOut.setOnClickListener {
            detailsForVehicleOut()
        }

        chassisWashButtonIn.setOnClickListener {
            getChassisDetailsForInWithSelection()
        }

        chassisWashButtonOut.setOnClickListener {
            getChassisDetailsForOutWithSelection()
        }



        washStageNumberLov.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                fetchwashStageType()
                if (washStageNumberLov.selectedItem.toString()!="Select Washing Stage") {
                    washStageType.visibility = View.VISIBLE
                } else {
                    washStageType.visibility = View.GONE
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        captureRegNoCamera.setOnClickListener {
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

        submitButton.setOnClickListener{
            vehicleIn()
        }

        submitButtonOut.setOnClickListener {
            vehicleOut()
        }

        washingHistory.setOnClickListener {
            workShopTestDriveVehHistory()
        }


        washStageTypeLov.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

            }
            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun workShopTestDriveVehHistory() {
        val intent = Intent(this@SalesWashingWithChassis, WorkshopWashingVehicleHistory::class.java)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name",location_name)
        intent.putExtra("deptName",deptName)
        startActivity(intent)
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

    private fun showTimePicker(textView: TextView) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            val updatedCalendar = Calendar.getInstance()
            updatedCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
            updatedCalendar.set(Calendar.MINUTE, selectedMinute)

            val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)

            val currentDate = updatedCalendar.time

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
            val formattedDateTime = dateFormat.format(currentDate)

            val dateFormat2 = SimpleDateFormat("HH:mm", Locale.getDefault())
            val formattedDateTime2 = dateFormat2.format(currentDate)

            textView.text = formattedDateTime2

            dateTimeToSend = formattedDateTime

        }, hour, minute, true)

        timePickerDialog.show()
    }




    private fun openCamera(requestCode: Int) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, requestCode)
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
                    }
                }
            }
            CAMERA_REQUEST_CODE, CAMERA_REQUEST_CODE_2 -> {
                if (resultCode == Activity.RESULT_OK) {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    when (requestCode) {
                        CAMERA_REQUEST_CODE -> {
                            processImageWithMultipleAttempts(imageBitmap, enterVehNumber)
                        }
                        CAMERA_REQUEST_CODE_2 -> {
                            processImageWithMultipleAttempts(imageBitmap, enterVehNumber)
                        }
                    }
                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun processImageWithMultipleAttempts(originalBitmap: Bitmap, resultTextView: TextView) {
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

    private fun displayBestResult(results: List<String>, resultTextView: TextView) {
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
                    (index == 4) && char == '0' -> 'D'
                    (index == 0) && char == 'N' -> 'M'//Added on 16-05-2025
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
                resultTextView.text = modifiedString
                enterVehNumber.setText(modifiedString)
//                Toast.makeText(this, "Text recognized: $modifiedString", Toast.LENGTH_SHORT).show()
            }
            regexVehicleNo2.matches(modifiedString) -> {
                resultTextView.text = modifiedString
                enterVehNumber.setText(modifiedString)
//                Toast.makeText(this, "Text recognized: $modifiedString", Toast.LENGTH_SHORT).show()
            }
            regexVehicleNo3.matches(modifiedString) -> {
                resultTextView.text = modifiedString
                enterVehNumber.setText(modifiedString)
//                Toast.makeText(this, "Text recognized: $modifiedString", Toast.LENGTH_SHORT).show()
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



    private fun fetchwashStageNumber() {
        val cmnType="WASHSTAGE"
        val client = OkHttpClient()
        val request = Request.Builder()
//            .url("${ApiFile.APP_URL}/washingRegister/washStageByCmnType?cmnType=$cmnType")
            .url(WorkshopWashingUrlManager.fetchWashStageNumber())
            .build()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val cities = parsewashStageNumber(it)
                    runOnUiThread {
                        val adapter = ArrayAdapter(this@SalesWashingWithChassis, android.R.layout.simple_spinner_item, cities
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        washStageNumberLov.adapter = adapter
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun parsewashStageNumber(jsonData: String): List<String> {
        val cities = mutableListOf<String>()
        try {
            val jsonObject = JSONObject(jsonData)
            val jsonArray = jsonObject.getJSONArray("obj")
            cities.add("Select Washing Stage")
            for (i in 0 until jsonArray.length()) {
                val city = jsonArray.getJSONObject(i)
                val desc = city.getString("CMNCODE")
                cities.add(desc)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return cities
    }

    private val selectedStages = mutableListOf<String>()

    private fun fetchwashStageType() {
        val cmnCode = washStageNumberLov.selectedItem.toString()
        val client = OkHttpClient()
        val request = Request.Builder()
//            .url("${ApiFile.APP_URL}/washingRegister/washStageTypeByCmnCode?cmnCode=$cmnCode")
            .url(WorkshopWashingUrlManager.fetchwashStageType(cmnCode))
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val stages = parsewashStageType(it) // Get washing stage list

                    runOnUiThread {
                        val checkBoxContainer = findViewById<LinearLayout>(R.id.checkBoxContainer)
                        checkBoxContainer.removeAllViews() // Clear previous checkboxes

                        selectedStages.clear() // Reset selected stages

                        // Start from index 1 to skip Select Washing Type
                        for (i in 1 until stages.size) {
                            val stage = stages[i]

                            val checkBox = CheckBox(this@SalesWashingWithChassis)
                            checkBox.text = stage
                            checkBox.layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            checkBoxContainer.addView(checkBox)

                            checkBox.setOnCheckedChangeListener { _, isChecked ->
                                if (isChecked) {
                                    selectedStages.add(stage)
                                } else {
                                    selectedStages.remove(stage)
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun parsewashStageType(jsonData: String): List<String> {
        val cities = mutableListOf<String>()
        try {
            val jsonObject = JSONObject(jsonData)
            val jsonArray = jsonObject.getJSONArray("obj")
            cities.add("Select Washing Type")
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


    private fun populateFieldsAfterInSearch() {
        regNoDetails.visibility=View.VISIBLE
        washStageNumber.visibility=View.VISIBLE
        washStageNumberLov.visibility=View.VISIBLE
        resetFields.visibility=View.VISIBLE
        submitButton.visibility=View.VISIBLE
    }

    private fun populateFieldsAfterOutSearch(){
        regNoDetails.visibility=View.VISIBLE
        resetFields.visibility=View.VISIBLE
        submitButtonOut.visibility=View.VISIBLE
    }

    private fun refreshData(){
        regNoDetails.visibility=View.GONE
        washStageNumber.visibility=View.GONE
        washStageNumberLov.visibility=View.GONE
        washStageNumberLov.setSelection(0)
        washStageType.visibility=View.GONE
        washStageTypeLov.visibility=View.GONE
        washStageTypeLov.setSelection(0)
        resetFields.visibility=View.GONE
        val tableLayout = findViewById<TableLayout>(R.id.tableLayout2)
        tableLayout.removeAllViews()
        enterVehNumber.setText("")
        vehWashButtonIn.visibility=View.GONE
        vehWashButtonOut.visibility=View.GONE
        vehNoEnterLL.visibility=View.GONE
        captureVehNumber.visibility=View.GONE
        resetwashStageNumberLov()
        resetwashStageTypeLov()
        submitButton.visibility=View.GONE
        submitButtonOut.visibility=View.GONE
        regNo=""
        engineNo=""
        chassisNo=""
        custName=""
        dateTimeToSend=""
        model=""
        serviceAdvisor=""
        vehWashNo=""
        vinNo=""
        val checkBoxContainer = findViewById<LinearLayout>(R.id.checkBoxContainer)
        checkBoxContainer.removeAllViews()
        regNoDetails.text=""
        chassisNoEnterLL.visibility=View.GONE
        enterChassisNumber.setText("")
    }

    fun resetwashStageNumberLov() {
        val adapter2 = washStageNumberLov.adapter as? ArrayAdapter<String>
        if (adapter2 != null) {
            adapter2.clear()
            adapter2.addAll(emptyList())
            adapter2.notifyDataSetChanged()
        }
        washStageNumberLov.setSelection(0)
    }
    fun resetwashStageTypeLov() {
        val adapter2 = washStageTypeLov.adapter as? ArrayAdapter<String>
        if (adapter2 != null) {
            adapter2.clear()
            adapter2.addAll(emptyList())
            adapter2.notifyDataSetChanged()
        }
        washStageTypeLov.setSelection(0)
    }
    private fun detailsForVehicleInFirstTime() {
        val client = OkHttpClient()
        val vehNo = enterVehNumber.text.toString()
        if(vehNo.isEmpty()){
            Toast.makeText(this@SalesWashingWithChassis,"Please enter the vehicle number.",Toast.LENGTH_SHORT).show()
            return
        }
        val url = WorkshopWashingUrlManager.getdetailsForVehicleInFirstTime(vehNo)

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
                        INSTANCE_NUMBER=stockItem.optString("INSTANCE_NUMBER"),
                        CHASSIS_NO=stockItem.optString("CHASSIS_NO"),
                        ACCOUNT_NUMBER=stockItem.optString("ACCOUNT_NUMBER"),
                        ENGINE_NO=stockItem.optString("ENGINE_NO"),
                        REGISTRATION_DATE=stockItem.optString("REGISTRATION_DATE"),
                        CUST_NAME=stockItem.optString("CUST_NAME"),

                        AIR_BLOW_STN = stockItem.optString("AIR_BLOW_STN"),
                        ENGINE_ROOM_STN = stockItem.optString("ENGINE_ROOM_STN"),
                        FIRST_STAGE = stockItem.optString("FIRST_STAGE"),
                        FS_IN_TIME = stockItem.optString("FS_IN_TIME"),
                        FS_OUT_TIME = stockItem.optString("FS_OUT_TIME"),
                        LOC_ID = stockItem.optString("LOC_ID"),
                        LOOSE_ITEMS_STN = stockItem.optString("LOOSE_ITEMS_STN"),
                        MODEL = stockItem.optString("MODEL"),
                        OU_ID = stockItem.optString("OU_ID"),
                        PROMISED_TIME = stockItem.optString("PROMISED_TIME"),
                        REG_NO = stockItem.optString("REG_NO"),
                        SERVICE_ADVISOR = stockItem.optString("SERVICE_ADVISOR"),
                        SS_IN_TIME = stockItem.optString("SS_IN_TIME"),
                        STATUS = stockItem.optString("STATUS"),
                        UNDERBODY_STN = stockItem.optString("UNDERBODY_STN"),
                        VEH_INTERIOR_STN = stockItem.optString("VEH_INTERIOR_STN"),
                        VEH_WASH_NO = stockItem.optString("VEH_WASH_NO"),
                        WASHING_SUPERVISOR = stockItem.optString("WASHING_SUPERVISOR"),

                        SS_OUT_TIME = stockItem.optString("SS_OUT_TIME"),
                        DS_IN_TIME = stockItem.optString("DS_IN_TIME"),
                        GLASS_POLISH_STN = stockItem.optString("GLASS_POLISH_STN"),
                        VEH_EXTERIOR_STN = stockItem.optString("VEH_EXTERIOR_STN"),
                        VEHICLE_DESC=stockItem.optString("VEHICLE_DESC"),
                        REGNO=stockItem.optString("REGNO"),
                        UPDATED_BY=stockItem.optString("UPDATED_BY")
                    )

                    val responseMessage = jsonObject.getString("message")

                    when (responseMessage) {
                        "Details for vehicle found in washing table" -> {
                            runOnUiThread {
                                populateFieldsAForThirdStage(jcData3)
                                populateFieldsAfterInSearch()
                                regNoDetails.text="Details found by Vehicle Number"
                                Toast.makeText(
                                    this@SalesWashingWithChassis,
                                    "Details for vehicle : $vehNo found in washing table",
                                    Toast.LENGTH_SHORT
                                ).show()
                                fetchwashStageNumber()
                            }
                        }
                        "Details Found Successfully in master table" -> {
                            runOnUiThread {
                                populateFieldsForMastersTable(jcData3)
                                populateFieldsAfterInSearch()
                                regNoDetails.text="Details found by Vehicle Number"
                                Toast.makeText(
                                    this@SalesWashingWithChassis,
                                    "Details Found Successfully in master table for Vehicle No: $vehNo",
                                    Toast.LENGTH_SHORT
                                ).show()
                                fetchwashStageNumber()
                            }
                        }
                        "New Vehicle" -> {
                            runOnUiThread {
                                populateFieldsDuringInForNewVehicle(jcData3)
                                populateFieldsAfterInSearch()
                                regNoDetails.text="Details found by Vehicle Number"
                                Toast.makeText(
                                    this@SalesWashingWithChassis,
                                    "New Vehicle details for Vehicle No: $vehNo",
                                    Toast.LENGTH_SHORT
                                ).show()
                                fetchwashStageNumber()
                            }
                        }
                        else -> {
                            runOnUiThread {
                                Toast.makeText(
                                    this@SalesWashingWithChassis,
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
                        this@SalesWashingWithChassis,
                        "Failed to fetch details for vehicle No: $vehNo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun detailsForVehicleOut() {
        val client = OkHttpClient()
        val vehNo = enterVehNumber.text.toString()
        if(vehNo.isEmpty()){
            Toast.makeText(this@SalesWashingWithChassis,"Please enter the vehicle number.",Toast.LENGTH_SHORT).show()
            return
        }
//        val url = ApiFile.APP_URL + "/washingRegister/vehDetailsForWashOut?regNo=$vehNo"
        val url = WorkshopWashingUrlManager.getdetailsForVehicleOutFirstTime(vehNo)

        val request = Request.Builder().url(url).build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    val stockItem = jsonObject.getJSONArray("obj").getJSONObject(0)

                    val jcData3 = allData(
                        INSTANCE_NUMBER=stockItem.optString("INSTANCE_NUMBER"),
                        CHASSIS_NO=stockItem.optString("CHASSIS_NO"),
                        ACCOUNT_NUMBER=stockItem.optString("ACCOUNT_NUMBER"),
                        ENGINE_NO=stockItem.optString("ENGINE_NO"),
                        REGISTRATION_DATE=stockItem.optString("REGISTRATION_DATE"),
                        CUST_NAME=stockItem.optString("CUST_NAME"),
                        AIR_BLOW_STN = stockItem.optString("AIR_BLOW_STN"),
                        ENGINE_ROOM_STN = stockItem.optString("ENGINE_ROOM_STN"),
                        FIRST_STAGE = stockItem.optString("FIRST_STAGE"),
                        FS_IN_TIME = stockItem.optString("FS_IN_TIME"),
                        FS_OUT_TIME = stockItem.optString("FS_OUT_TIME"),
                        LOC_ID = stockItem.optString("LOC_ID"),
                        LOOSE_ITEMS_STN = stockItem.optString("LOOSE_ITEMS_STN"),
                        MODEL = stockItem.optString("MODEL"),
                        OU_ID = stockItem.optString("OU_ID"),
                        PROMISED_TIME = stockItem.optString("PROMISED_TIME"),
                        REG_NO = stockItem.optString("REG_NO"),
                        SERVICE_ADVISOR = stockItem.optString("SERVICE_ADVISOR"),
                        SS_IN_TIME = stockItem.optString("SS_IN_TIME"),
                        STATUS = stockItem.optString("STATUS"),
                        UNDERBODY_STN = stockItem.optString("UNDERBODY_STN"),
                        VEH_INTERIOR_STN = stockItem.optString("VEH_INTERIOR_STN"),
                        VEH_WASH_NO = stockItem.optString("VEH_WASH_NO"),
                        WASHING_SUPERVISOR = stockItem.optString("WASHING_SUPERVISOR"),
                        SS_OUT_TIME = stockItem.optString("SS_OUT_TIME"),
                        DS_IN_TIME = stockItem.optString("DS_IN_TIME"),
                        GLASS_POLISH_STN = stockItem.optString("GLASS_POLISH_STN"),
                        VEH_EXTERIOR_STN = stockItem.optString("VEH_EXTERIOR_STN"),
                        VEHICLE_DESC=stockItem.optString("VEHICLE_DESC"),
                        REGNO=stockItem.optString("REGNO"),
                        UPDATED_BY=stockItem.optString("UPDATED_BY")
                    )

                    val responseMessage = jsonObject.getString("message")

                    when (responseMessage) {
                        "Details for vehicle found in washing table" -> {
                            runOnUiThread {
                                populateFieldsAForVehicleOut(jcData3)
                                populateFieldsAfterOutSearch()
                                regNoDetails.text="Details found by Vehicle Number"
                                Toast.makeText(
                                    this@SalesWashingWithChassis,
                                    "Details for vehicle : $vehNo found in washing table",
                                    Toast.LENGTH_SHORT
                                ).show()
                                fetchwashStageNumber()
                            }
                        }
                        else -> {
                            runOnUiThread {
                                Toast.makeText(
                                    this@SalesWashingWithChassis,
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
                        this@SalesWashingWithChassis,
                        "Failed to fetch details for vehicle No: $vehNo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun vehicleIn() {

        if (selectedStages.size<1){
            Toast.makeText(this,"Please select at least one washing type.",Toast.LENGTH_SHORT).show()
            return
        }

        val url = ApiFile.APP_URL + "/washingRegister/vehWashProceed/"
        val jsonObject = JSONObject().apply {
//            put("regNo", regNo)
            if (::regNo.isInitialized) {
                if(regNo.isNotEmpty() ) {
                    put("regNo", regNo)
                }
            }
            if (::chassisNo.isInitialized) {
                if(chassisNo.isNotEmpty() ) {
                    put("chassisNo", chassisNo)
                }
            }
            if (::vinNo.isInitialized) {
                if(vinNo.isNotEmpty() ) {
                    put("attribute4", vinNo)
                }
            }
            if (::vehWashNo.isInitialized) {
                if(vehWashNo.isNotEmpty() ) {
                    put("vehWashNo",vehWashNo)
                }
            }
            if (::serviceAdvisor.isInitialized) {
                if(serviceAdvisor.isNotEmpty() ) {
                    put("serviceAdvisor",serviceAdvisor)
                }
            }
            if (::model.isInitialized) {
                if(model.isNotEmpty() ) {
                    put("model",model)
                }
            }

            put("locId", locId.toString())
            put("ouId", ouId.toString())
            put("dept", deptName)
            put("createdBy", login_name)
            put("location", location_name)
            put("authorisedBy", login_name)
            put("updatedBy", attribute1)
            put("washingSupervisor",login_name)
            put("attribute1", selectedStages.getOrNull(0) ?: "")
            put("attribute2", selectedStages.getOrNull(1) ?: "")
            put("attribute3", selectedStages.getOrNull(2) ?: "")
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
                            message.contains("A stage is already in progress. Please complete the current stage before starting a new one.", ignoreCase = true) -> {
                                Toast.makeText(
                                    this@SalesWashingWithChassis,
                                    "A stage is already in progress. Please complete the current stage before starting a new one.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            message.contains("One or more selected stages are already marked as OUT. Cannot mark them IN again in the same row.", ignoreCase = true) -> {
                                Toast.makeText(
                                    this@SalesWashingWithChassis,
                                    "Selected stages are already completed cannot IN again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            responseCode==500-> {
                                Toast.makeText(
                                    this@SalesWashingWithChassis,
                                    "Vehicle Wash In Failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            responseCode == 200 -> {
                                Toast.makeText(
                                    this@SalesWashingWithChassis,
                                    "Vehicle in successfully!!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                refreshData()
                            }
                            else -> {
                                Toast.makeText(
                                    this@SalesWashingWithChassis,
                                    "Failed to save data. Error code: $responseCode",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@SalesWashingWithChassis,
                            "No response from server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@SalesWashingWithChassis,
                        "Error saving data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun vehicleOut() {
        val url = ApiFile.APP_URL + "/washingRegister/vehWashOut/"
        val jsonObject = JSONObject().apply {
            if (::regNo.isInitialized) {
                if(regNo.isNotEmpty() ) {
                    put("regNo", regNo)
                }
            }
            if (::vinNo.isInitialized) {
                if(vinNo.isNotEmpty() ) {
                    put("attribute4", vinNo)
                }
            }
            put("updatedBy", attribute1)
        }

        val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .put(requestBody)
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
                            message.contains("No stage is marked as IN, cannot process OUT request", ignoreCase = true) -> {
                                Toast.makeText(
                                    this@SalesWashingWithChassis,
                                    "Vehicle is never in for wash , can't perform out.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            responseCode == 200 -> {
                                Toast.makeText(
                                    this@SalesWashingWithChassis,
                                    "Vehicle out successfully!!!",
                                    Toast.LENGTH_LONG
                                ).show()
                                refreshData()
                            }
                            else -> {
                                Toast.makeText(
                                    this@SalesWashingWithChassis,
                                    "Failed to save data. Error code: $responseCode",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@SalesWashingWithChassis,
                            "No response from server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@SalesWashingWithChassis,
                        "Error saving data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun populateFieldsForMastersTable(jcData: allData) {
        val table = findViewById<TableLayout>(R.id.tableLayout2)
        table.removeAllViews()

        val detailsMap = mutableMapOf(
            "REG NO" to jcData.INSTANCE_NUMBER,
            "CHASSIS NO" to jcData.CHASSIS_NO,
            "ENGINE NO" to jcData.ENGINE_NO,
            "MODEL" to jcData.VEHICLE_DESC,
            "SER ADV" to jcData.SERVICE_ADVISOR
        )

        regNo=jcData.INSTANCE_NUMBER
        engineNo=jcData.ENGINE_NO
        chassisNo=jcData.CHASSIS_NO
        custName=jcData.CUST_NAME
        model=jcData.VEHICLE_DESC
        serviceAdvisor=jcData.SERVICE_ADVISOR
        chassisNo=jcData.CHASSIS_NO

        for ((label, value) in detailsMap) {
            val row = LayoutInflater.from(this).inflate(R.layout.table_row_gate_pass, null) as TableRow
            val labelTextView = row.findViewById<TextView>(R.id.label)
            val valueTextView = row.findViewById<TextView>(R.id.value)

            labelTextView.text = label
            valueTextView.text = value

            table.addView(row)
        }

        if (table.childCount > 0) {
            val lastRow = table.getChildAt(table.childCount - 1) as TableRow
            lastRow.findViewById<View>(R.id.labelDivider).visibility = View.GONE
            lastRow.findViewById<View>(R.id.valueDivider).visibility = View.GONE
        }
    }

    private fun populateFieldsAForThirdStage(jcData: allData) {
        val table = findViewById<TableLayout>(R.id.tableLayout2)
        table.removeAllViews()

        val detailsMap = mutableMapOf(
            "REG NO" to jcData.REG_NO,
            "CHASSIS NO" to jcData.CHASSIS_NO,
            "SERVICE ADVISOR" to jcData.SERVICE_ADVISOR,
            "PROMISED TIME" to jcData.PROMISED_TIME,
            "WASHING SUPERVISOR" to jcData.WASHING_SUPERVISOR,
            "1ST STAGE IN TIME" to jcData.FS_IN_TIME,
            "1ST STAGE OUT TIME" to jcData.FS_OUT_TIME,
            "SS IN TIME" to jcData.SS_IN_TIME,
            "SS_OUT_TIME" to jcData.SS_OUT_TIME,
            "DS_IN_TIME" to jcData.DS_IN_TIME,
            "VEH EXTERIOR STN" to jcData.VEH_EXTERIOR_STN,
            "GLASS POLISH STN" to jcData.GLASS_POLISH_STN
        )

        regNo=jcData.REG_NO
        chassisNo=jcData.CHASSIS_NO
        vehWashNo=jcData.VEH_WASH_NO
        serviceAdvisor=jcData.SERVICE_ADVISOR

        for ((label, value) in detailsMap) {
            val row = LayoutInflater.from(this).inflate(R.layout.table_row_gate_pass, null) as TableRow
            val labelTextView = row.findViewById<TextView>(R.id.label)
            val valueTextView = row.findViewById<TextView>(R.id.value)

            labelTextView.text = label
            valueTextView.text = value

            table.addView(row)
        }

        if (table.childCount > 0) {
            val lastRow = table.getChildAt(table.childCount - 1) as TableRow
            lastRow.findViewById<View>(R.id.labelDivider).visibility = View.GONE
            lastRow.findViewById<View>(R.id.valueDivider).visibility = View.GONE
        }
    }


    private fun populateFieldsAForVehicleOut(jcData: allData) {
        val table = findViewById<TableLayout>(R.id.tableLayout2)
        table.removeAllViews()

        val washSuperVisor="${jcData.WASHING_SUPERVISOR}-${jcData.UPDATED_BY}"

        val detailsMap = mutableMapOf(
            "REG NO" to jcData.REG_NO,
            "CHASSIS NO" to jcData.CHASSIS_NO,
            "MODEL" to jcData.MODEL,
            "SERVICE ADVISOR" to jcData.SERVICE_ADVISOR,
            "PROMISED TIME" to jcData.PROMISED_TIME,
            "WASHING SUPERVISOR" to washSuperVisor,
            "1ST STAGE IN" to jcData.FS_IN_TIME,
            "AIR BLOW STN" to jcData.AIR_BLOW_STN,
            "UNDER BODY STN" to jcData.UNDERBODY_STN,
            "ENGINE ROOM STN" to jcData.ENGINE_ROOM_STN,
            "1ST STAGE OUT" to jcData.FS_OUT_TIME,
            "2ND STAGE IN" to jcData.SS_IN_TIME,
            "LOOSE ITEMS STN" to jcData.LOOSE_ITEMS_STN,
            "VEH INTERIOR STN" to jcData.VEH_INTERIOR_STN,
            "2ND STAGE OUT" to jcData.SS_OUT_TIME,
            "DS_IN_TIME" to jcData.DS_IN_TIME,
            "VEH EXTERIOR STN" to jcData.VEH_EXTERIOR_STN,
            "GLASS POLISH STN" to jcData.GLASS_POLISH_STN
        )

        regNo = jcData.REG_NO
        chassisNo = jcData.CHASSIS_NO
        vehWashNo = jcData.VEH_WASH_NO

        for ((label, value) in detailsMap) {
            if (value != null && value!= "-") {
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


    private fun populateFieldsDuringInForNewVehicle(jcData: allData) {
        val table = findViewById<TableLayout>(R.id.tableLayout2)
        table.removeAllViews()

        val detailsMap = mutableMapOf(
            "REG NO" to jcData.REGNO,
            "MESSAGE" to "This is new vehicle."
        )
        regNo=jcData.REGNO
        chassisNo=jcData.CHASSIS_NO
        vehWashNo=jcData.VEH_WASH_NO

        for ((label, value) in detailsMap) {
            val row = LayoutInflater.from(this).inflate(R.layout.table_row_gate_pass, null) as TableRow
            val labelTextView = row.findViewById<TextView>(R.id.label)
            val valueTextView = row.findViewById<TextView>(R.id.value)

            labelTextView.text = label
            valueTextView.text = value

            table.addView(row)
        }

        if (table.childCount > 0) {
            val lastRow = table.getChildAt(table.childCount - 1) as TableRow
            lastRow.findViewById<View>(R.id.labelDivider).visibility = View.GONE
            lastRow.findViewById<View>(R.id.valueDivider).visibility = View.GONE
        }
    }

    data class allData(
        val ACCOUNT_NUMBER:String,
        val ENGINE_NO:String,
        val REGISTRATION_DATE:String,
        val CUST_NAME:String,
        val INSTANCE_NUMBER:String,
        val CHASSIS_NO:String,



        val OU_ID:String,
        val AIR_BLOW_STN:String,
        val ENGINE_ROOM_STN:String,
        val VEH_INTERIOR_STN:String,
        val VEH_WASH_NO:String,
        val PROMISED_TIME:String,
        val FS_IN_TIME:String,
        val FIRST_STAGE:String,
        val LOC_ID:String,
        val SERVICE_ADVISOR:String,
        val REG_NO:String,
        val SS_IN_TIME:String,
        val FS_OUT_TIME:String,
        val LOOSE_ITEMS_STN:String,
        val WASHING_SUPERVISOR:String,
        val MODEL:String,
        val STATUS:String,
        val UNDERBODY_STN:String,

        val SS_OUT_TIME:String,
        val VEH_EXTERIOR_STN:String,
        val GLASS_POLISH_STN:String,
        val DS_IN_TIME:String,
        val VEHICLE_DESC:String,
        val REGNO:String,

        val UPDATED_BY:String
    )

    data class WashStageType(
        val name: String,
        var isSelected: Boolean = false
    )


    private fun getChassisDetailsForInWithSelection() {
        val client = OkHttpClient()
        val chassisNo = enterChassisNumber.text.toString()

        if (chassisNo.isEmpty()) {
            Toast.makeText(this@SalesWashingWithChassis, "Please enter chassis number", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "${ApiFile.APP_URL}/qrcode/qrDetailsByChassisBatch?chassisNo=$chassisNo&ouId=$ouId"
        val request = Request.Builder().url(url).build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()

                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    val code = jsonObject.optInt("code")
                    val objArray = jsonObject.getJSONArray("obj")

                    if (code == 200) {
                        if (objArray.length() == 1) {
                            val stockItem = objArray.getJSONObject(0)
                            val jcData = chassisData(
                                AIR_BLOW_STN = stockItem.optString("AIR_BLOW_STN"),
                                ENGINE_ROOM_STN = stockItem.optString("ENGINE_ROOM_STN"),
                                FIRST_STAGE = stockItem.optString("FIRST_STAGE"),
                                FS_IN_TIME = stockItem.optString("FS_IN_TIME"),
                                FS_OUT_TIME = stockItem.optString("FS_OUT_TIME"),
                                LOC_ID = stockItem.optString("LOC_ID"),
                                LOOSE_ITEMS_STN = stockItem.optString("LOOSE_ITEMS_STN"),
                                MODEL_CD = stockItem.optString("MODEL_CD"),
                                MODEL = stockItem.optString("MODEL"),
                                PROMISED_TIME = stockItem.optString("PROMISED_TIME"),
                                SS_IN_TIME = stockItem.optString("SS_IN_TIME"),
                                STATUS = stockItem.optString("STATUS"),
                                UNDERBODY_STN = stockItem.optString("UNDERBODY_STN"),
                                VEH_INTERIOR_STN = stockItem.optString("VEH_INTERIOR_STN"),
                                VEH_WASH_NO = stockItem.optString("VEH_WASH_NO"),
                                WASHING_SUPERVISOR = stockItem.optString("WASHING_SUPERVISOR"),
                                SS_OUT_TIME = stockItem.optString("SS_OUT_TIME"),
                                DS_IN_TIME = stockItem.optString("DS_IN_TIME"),
                                GLASS_POLISH_STN = stockItem.optString("GLASS_POLISH_STN"),
                                VEH_EXTERIOR_STN = stockItem.optString("VEH_EXTERIOR_STN"),
                                VARIANT_DESC = stockItem.optString("VARIANT_DESC"),
                                CHASSIS_NUM = stockItem.optString("CHASSIS_NUM"),
                                ENGINE_NUM = stockItem.optString("ENGINE_NUM"),
                                VIN = stockItem.optString("VIN"),
                                CHASSIS_NO = stockItem.optString("CHASSIS_NO")
                            )
                            runOnUiThread {
                                populateFieldsFromVinData(jcData)
                                regNoDetails.text="Details found by Chassis Number"
                                populateFieldsAfterInSearch()
                                fetchwashStageNumber()
                            }

                        } else if (objArray.length() > 1) {
                            val vinList = mutableListOf<String>()
                            for (i in 0 until objArray.length()) {
                                vinList.add(objArray.getJSONObject(i).optString("VIN"))
                            }

                            runOnUiThread {
                                showVinSelectionDialogIn(vinList)
                            }
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@SalesWashingWithChassis, "Error: ${jsonObject.optString("message")}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@SalesWashingWithChassis, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    private fun showVinSelectionDialogIn(vinList: List<String>) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select VIN")
        builder.setItems(vinList.toTypedArray()) { _, which ->
            val selectedVin = vinList[which]
            fetchDetailsByVin(selectedVin)
        }
        builder.show()
    }

    private fun populateFieldsFromVinData(chData: chassisData) {
        val table = findViewById<TableLayout>(R.id.tableLayout2)
        table.removeAllViews()

        val detailsMap = mutableMapOf(
            "CHASSIS NO" to chData.CHASSIS_NUM,
            "VIN" to chData.VIN,
            "ENGINE NO" to chData.ENGINE_NUM,
            "MODEL" to chData.MODEL_CD
        )

        chassisNo=chData.CHASSIS_NUM
        vinNo=chData.VIN
        engineNo=chData.ENGINE_NUM
        model=chData.MODEL_CD


        for ((label, value) in detailsMap) {
            val row = LayoutInflater.from(this).inflate(R.layout.table_row_gate_pass, null) as TableRow
            val labelTextView = row.findViewById<TextView>(R.id.label)
            val valueTextView = row.findViewById<TextView>(R.id.value)

            labelTextView.text = label
            valueTextView.text = value

            table.addView(row)
        }

        if (table.childCount > 0) {
            val lastRow = table.getChildAt(table.childCount - 1) as TableRow
            lastRow.findViewById<View>(R.id.labelDivider).visibility = View.GONE
            lastRow.findViewById<View>(R.id.valueDivider).visibility = View.GONE
        }
    }

    private fun fetchDetailsByVin(vin: String) {
        val client = OkHttpClient()
        val url = "${ApiFile.APP_URL}/qrcode/qrDetailsByVin?vin=$vin"

        val request = Request.Builder().url(url).build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()

                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    val objArray = jsonObject.getJSONArray("obj")
                    if (objArray.length() > 0) {
                        val stockItem = objArray.getJSONObject(0)

                        val jcData = chassisData(
                            AIR_BLOW_STN = stockItem.optString("AIR_BLOW_STN"),
                            ENGINE_ROOM_STN = stockItem.optString("ENGINE_ROOM_STN"),
                            FIRST_STAGE = stockItem.optString("FIRST_STAGE"),
                            FS_IN_TIME = stockItem.optString("FS_IN_TIME"),
                            FS_OUT_TIME = stockItem.optString("FS_OUT_TIME"),
                            LOC_ID = stockItem.optString("LOC_ID"),
                            LOOSE_ITEMS_STN = stockItem.optString("LOOSE_ITEMS_STN"),
                            MODEL_CD = stockItem.optString("MODEL_CD"),
                            MODEL = stockItem.optString("MODEL"),
                            PROMISED_TIME = stockItem.optString("PROMISED_TIME"),
                            SS_IN_TIME = stockItem.optString("SS_IN_TIME"),
                            STATUS = stockItem.optString("STATUS"),
                            UNDERBODY_STN = stockItem.optString("UNDERBODY_STN"),
                            VEH_INTERIOR_STN = stockItem.optString("VEH_INTERIOR_STN"),
                            VEH_WASH_NO = stockItem.optString("VEH_WASH_NO"),
                            WASHING_SUPERVISOR = stockItem.optString("WASHING_SUPERVISOR"),
                            SS_OUT_TIME = stockItem.optString("SS_OUT_TIME"),
                            DS_IN_TIME = stockItem.optString("DS_IN_TIME"),
                            GLASS_POLISH_STN = stockItem.optString("GLASS_POLISH_STN"),
                            VEH_EXTERIOR_STN = stockItem.optString("VEH_EXTERIOR_STN"),
                            VARIANT_DESC = stockItem.optString("VARIANT_DESC"),
                            CHASSIS_NUM = stockItem.optString("CHASSIS_NUM"),
                            ENGINE_NUM = stockItem.optString("ENGINE_NUM"),
                            VIN = stockItem.optString("VIN"),
                            CHASSIS_NO = stockItem.optString("CHASSIS_NO")
                        )

                        runOnUiThread {
                            populateFieldsFromVinData(jcData)
                            regNoDetails.text="Details found by Chassis Number"
                            populateFieldsAfterInSearch()
                            fetchwashStageNumber()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun getChassisDetailsForOutWithSelection() {
        val client = OkHttpClient()
        val chassisNo = enterChassisNumber.text.toString()

        if (chassisNo.isEmpty()) {
            Toast.makeText(this@SalesWashingWithChassis,"Please enter chassis number", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "${ApiFile.APP_URL}/washingRegister/vehDetailsForWashOutSales?chassisNo=$chassisNo&ouId=$ouId"
        val request = Request.Builder().url(url).build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()

                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    val code = jsonObject.optInt("code")
                    val objArray = jsonObject.getJSONArray("obj")

                    if (code == 200) {
                        if (objArray.length() == 1) {
                            val stockItem = objArray.getJSONObject(0)

                            val jcData = chassisData(
                                AIR_BLOW_STN = stockItem.optString("AIR_BLOW_STN"),
                                ENGINE_ROOM_STN = stockItem.optString("ENGINE_ROOM_STN"),
                                FIRST_STAGE = stockItem.optString("FIRST_STAGE"),
                                FS_IN_TIME = stockItem.optString("FS_IN_TIME"),
                                FS_OUT_TIME = stockItem.optString("FS_OUT_TIME"),
                                LOC_ID = stockItem.optString("LOC_ID"),
                                LOOSE_ITEMS_STN = stockItem.optString("LOOSE_ITEMS_STN"),
                                MODEL_CD = stockItem.optString("MODEL_CD"),
                                MODEL = stockItem.optString("MODEL"),
                                PROMISED_TIME = stockItem.optString("PROMISED_TIME"),
                                SS_IN_TIME = stockItem.optString("SS_IN_TIME"),
                                STATUS = stockItem.optString("STATUS"),
                                UNDERBODY_STN = stockItem.optString("UNDERBODY_STN"),
                                VEH_INTERIOR_STN = stockItem.optString("VEH_INTERIOR_STN"),
                                VEH_WASH_NO = stockItem.optString("VEH_WASH_NO"),
                                WASHING_SUPERVISOR = stockItem.optString("WASHING_SUPERVISOR"),
                                SS_OUT_TIME = stockItem.optString("SS_OUT_TIME"),
                                DS_IN_TIME = stockItem.optString("DS_IN_TIME"),
                                GLASS_POLISH_STN = stockItem.optString("GLASS_POLISH_STN"),
                                VEH_EXTERIOR_STN = stockItem.optString("VEH_EXTERIOR_STN"),
                                VARIANT_DESC = stockItem.optString("VARIANT_DESC"),
                                CHASSIS_NUM = stockItem.optString("CHASSIS_NUM"),
                                ENGINE_NUM = stockItem.optString("ENGINE_NUM"),
                                VIN = stockItem.optString("VIN"),
                                CHASSIS_NO = stockItem.optString("CHASSIS_NO")
                            )

                            runOnUiThread {
                                populateFieldsFromVinDataOut(jcData)
                                regNoDetails.text="Details found by Chassis Number"
                                populateFieldsAfterOutSearch()
                            }

                        }
//                        if (objArray.length() == 1) {
//                            val vinList = mutableListOf<String>()
//                            for (i in 0 until objArray.length()) {
//                                vinList.add(objArray.getJSONObject(i).optString("VIN"))
//                            }
//
//                            runOnUiThread {
//                                showVinSelectionDialogOut(vinList)
//                            }
//                        }
                        else if (objArray.length() > 1) {
                            val vinList = mutableListOf<String>()
                            for (i in 0 until objArray.length()) {
                                vinList.add(objArray.getJSONObject(i).optString("VIN"))
                            }

                            runOnUiThread {
                                showVinSelectionDialogOut(vinList)
                            }
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@SalesWashingWithChassis, "Error: ${jsonObject.optString("message")}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@SalesWashingWithChassis, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchDetailsByVinOut(vin: String) {
        val client = OkHttpClient()
        val url = "${ApiFile.APP_URL}/washingRegister/vehDetailsForWashOutSales?vin=$vin"

        val request = Request.Builder().url(url).build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()

                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    val objArray = jsonObject.getJSONArray("obj")
                    if (objArray.length() > 0) {
                        val stockItem = objArray.getJSONObject(0)

                        val jcData = chassisData(
                            AIR_BLOW_STN = stockItem.optString("AIR_BLOW_STN"),
                            ENGINE_ROOM_STN = stockItem.optString("ENGINE_ROOM_STN"),
                            FIRST_STAGE = stockItem.optString("FIRST_STAGE"),
                            FS_IN_TIME = stockItem.optString("FS_IN_TIME"),
                            FS_OUT_TIME = stockItem.optString("FS_OUT_TIME"),
                            LOC_ID = stockItem.optString("LOC_ID"),
                            LOOSE_ITEMS_STN = stockItem.optString("LOOSE_ITEMS_STN"),
                            MODEL_CD = stockItem.optString("MODEL_CD"),
                            MODEL =stockItem.optString("MODEL"),
                            PROMISED_TIME = stockItem.optString("PROMISED_TIME"),
                            SS_IN_TIME = stockItem.optString("SS_IN_TIME"),
                            STATUS = stockItem.optString("STATUS"),
                            UNDERBODY_STN = stockItem.optString("UNDERBODY_STN"),
                            VEH_INTERIOR_STN = stockItem.optString("VEH_INTERIOR_STN"),
                            VEH_WASH_NO = stockItem.optString("VEH_WASH_NO"),
                            WASHING_SUPERVISOR = stockItem.optString("WASHING_SUPERVISOR"),
                            SS_OUT_TIME = stockItem.optString("SS_OUT_TIME"),
                            DS_IN_TIME = stockItem.optString("DS_IN_TIME"),
                            GLASS_POLISH_STN = stockItem.optString("GLASS_POLISH_STN"),
                            VEH_EXTERIOR_STN = stockItem.optString("VEH_EXTERIOR_STN"),
                            VARIANT_DESC = stockItem.optString("VARIANT_DESC"),
                            CHASSIS_NUM = stockItem.optString("CHASSIS_NUM"),
                            ENGINE_NUM = stockItem.optString("ENGINE_NUM"),
                            VIN = stockItem.optString("VIN"),
                            CHASSIS_NO = stockItem.optString("CHASSIS_NO")
                        )

                        val responseMessage = jsonObject.getString("message")
                        val code = jsonObject.getString("code")

                        when (responseMessage) {
                            "Details for vehicle found in washing table" -> {
                                runOnUiThread {
                                    populateFieldsFromVinDataOut(jcData)
                                    regNoDetails.text="Details found by Chassis Number"
                                    populateFieldsAfterOutSearch()
                                    Toast.makeText(
                                        this@SalesWashingWithChassis,
                                        "Details found in washing table",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            "Details not found" -> {
                                runOnUiThread {
                                    Toast.makeText(
                                        this@SalesWashingWithChassis,
                                        "Details not found",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showVinSelectionDialogOut(vinList: List<String>) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select VIN")
        builder.setItems(vinList.toTypedArray()) { _, which ->
            val selectedVin = vinList[which]
            fetchDetailsByVinOut(selectedVin)
        }
        builder.show()
    }

    private fun populateFieldsFromVinDataOut(jcData: chassisData) {
        val table = findViewById<TableLayout>(R.id.tableLayout2)
        table.removeAllViews()

        val detailsMap = mutableMapOf(
            "CHASSIS NO" to jcData.CHASSIS_NO,
            "VIN" to jcData.VIN,
            "MODEL" to jcData.MODEL,
            "WASHING SUPERVISOR" to jcData.WASHING_SUPERVISOR,
            "1ST STAGE IN" to jcData.FS_IN_TIME,
            "AIR BLOW STN" to jcData.AIR_BLOW_STN,
            "UNDER BODY STN" to jcData.UNDERBODY_STN,
            "ENGINE ROOM STN" to jcData.ENGINE_ROOM_STN,
            "1ST STAGE OUT" to jcData.FS_OUT_TIME,
            "2ND STAGE IN" to jcData.SS_IN_TIME,
            "LOOSE ITEMS STN" to jcData.LOOSE_ITEMS_STN,
            "VEH INTERIOR STN" to jcData.VEH_INTERIOR_STN,
            "2ND STAGE OUT" to jcData.SS_OUT_TIME,
            "DS_IN_TIME" to jcData.DS_IN_TIME,
            "VEH EXTERIOR STN" to jcData.VEH_EXTERIOR_STN,
            "GLASS POLISH STN" to jcData.GLASS_POLISH_STN
        )

        vinNo=jcData.VIN


        for ((label, value) in detailsMap) {
            if (value != null && value!= "-") {
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

    data class chassisData(
        val CHASSIS_NO:String,
        val VIN:String,
        val CHASSIS_NUM:String,
        val ENGINE_NUM:String,
        val MODEL:String,
        val AIR_BLOW_STN:String,
        val ENGINE_ROOM_STN:String,
        val VEH_INTERIOR_STN:String,
        val VEH_WASH_NO:String,
        val PROMISED_TIME:String,
        val FS_IN_TIME:String,
        val FIRST_STAGE:String,
        val LOC_ID:String,
        val SS_IN_TIME:String,
        val FS_OUT_TIME:String,
        val LOOSE_ITEMS_STN:String,
        val WASHING_SUPERVISOR:String,
        val STATUS:String,
        val UNDERBODY_STN:String,
        val SS_OUT_TIME:String,
        val VEH_EXTERIOR_STN:String,
        val GLASS_POLISH_STN:String,
        val DS_IN_TIME:String,
        val VARIANT_DESC:String,
        val MODEL_CD:String
        )
}




