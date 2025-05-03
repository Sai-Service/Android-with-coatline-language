//package com.example.apinew
//
//import android.app.DatePickerDialog
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import android.view.ViewGroup
//import android.widget.AdapterView
//import android.widget.ArrayAdapter
//import android.widget.Button
//import android.widget.EditText
//import android.widget.Spinner
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContextCompat
//import com.google.common.reflect.TypeToken
//import com.google.gson.Gson
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.launch
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.RequestBody.Companion.toRequestBody
//import org.json.JSONObject
//import java.io.IOException
//import java.text.SimpleDateFormat
//import java.util.*
//
//class GenerateLogins : AppCompatActivity() {
//
//    private lateinit var etLoginName: EditText
//    private lateinit var etPassword: EditText
//    private lateinit var etDepartment: EditText
//    private lateinit var etLocation: EditText
//    private lateinit var etCreationDate: EditText
//    private lateinit var etLastUpdateDate: EditText
//    private lateinit var etLocId: EditText
//    private lateinit var etOuid: EditText
//    private lateinit var etLocationName: EditText
//    private lateinit var btnPost: Button
//    private lateinit var multipleBatchNameSpinner:Spinner
//    private lateinit var btnClear:Button
//    private lateinit var btnUpdate:Button
//    private lateinit var ouLocSpinner:Spinner
//    private lateinit var toLocation:String
//    private  var toLocationID:Int=0
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_generate_logins)
//
//        etLoginName = findViewById(R.id.etLoginName)
//        etPassword = findViewById(R.id.etPassword)
//        etDepartment = findViewById(R.id.etDepartment)
//        etLocation = findViewById(R.id.etLocation)
//        etCreationDate = findViewById(R.id.etCreationDate)
//        etLastUpdateDate = findViewById(R.id.etLastUpdateDate)
//        etLocId = findViewById(R.id.etLocId)
//        etOuid = findViewById(R.id.etOUID)
//        etLocationName = findViewById(R.id.etLocationName)
//        btnPost = findViewById(R.id.btnPost)
//        multipleBatchNameSpinner=findViewById(R.id.multipleBatchNameSpinner)
//        btnClear=findViewById(R.id.btnClear)
//        btnUpdate=findViewById(R.id.btnUpdate)
//        ouLocSpinner=findViewById(R.id.ouLocSpinner)
//
//        btnUpdate.setOnClickListener {
//            goToUpdateLogin()
//        }
//
//        etCreationDate = findViewById(R.id.etCreationDate)
//        etLastUpdateDate = findViewById(R.id.etLastUpdateDate)
//
//        etCreationDate.setOnClickListener {
//            openDatePickerDialog(etCreationDate)
//        }
//
//        etLastUpdateDate.setOnClickListener {
//            openDatePickerDialog(etLastUpdateDate)
//        }
//
//        btnPost.setOnClickListener {
//            postLoginData()
//        }
//
//        btnClear.setOnClickListener {
//            clearFields()
//        }
//
//        val typeOptions = mutableListOf("SELECT  OUID",  "81","104","105","106","107","108")
//        val typeAdapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, typeOptions) {
//            override fun isEnabled(position: Int): Boolean {
//                return position != 0
//            }
//
//
//            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
//                val view = super.getDropDownView(position, convertView, parent)
//                val textView = view as TextView
//                if (position == 0) {
//                    textView.setTextColor(ContextCompat.getColor(context, R.color.red))
//                } else {
//                    textView.setTextColor(ContextCompat.getColor(context, R.color.black))
//                }
//                return view
//            }
//
//        }
//        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        multipleBatchNameSpinner.adapter = typeAdapter
//
//        multipleBatchNameSpinner.setSelection(0, false)
//
//        multipleBatchNameSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                if (position > 0) {
//                    fetchOrganizations()
//                }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                //
//            }
//        }
//
//
//    }
//
//    private fun postLoginData() {
//        val client = OkHttpClient()
//        val url = ApiFile.APP_URL + "/admin/addUser"
//        val ouId=multipleBatchNameSpinner.selectedItem.toString()
//
//        val jsonObject = JSONObject().apply {
//            put("loginName", etLoginName.text.toString())
//            put("password", etPassword.text.toString())
//            put("deptName", etDepartment.text.toString())
//            put("location", etLocation.text.toString())
//            put("locId", etLocId.text.toString())
////            put("ouId", etOuid.text.toString())
//            put("ouId",ouId)
//            put("location_name", etLocationName.text.toString())
//            put("attribute1", "")
//            put("attribute2", "")
//            put("attribute3", "")
//            put("attribute4", "")
//            put("userId", "")
//            put("emailId", "")
//            put("dmsLoc", "")
//            put("role", "")
//        }
//
//        val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
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
//                runOnUiThread {
//                    if (responseBody != null) {
//                        val responseJson = JSONObject(responseBody)
//                        val message = responseJson.optString("message", "")
//
//                        when {
//                            message.contains("Login Name Already Exists", ignoreCase = true) -> {
//                                Toast.makeText(
//                                    this@GenerateLogins,
//                                    "Login Name Already Exists",
//                                    Toast.LENGTH_LONG
//                                ).show()
//                            }
//                            responseCode == 200 -> {
//                                Toast.makeText(
//                                    this@GenerateLogins,
//                                    "New User Added Successfully",
//                                    Toast.LENGTH_LONG
//                                ).show()
//                                clearFields()
//                            }
//                            else -> {
//                                Toast.makeText(
//                                    this@GenerateLogins,
//                                    "Failed to add user. Error code: $responseCode",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                    } else {
//                        Toast.makeText(
//                            this@GenerateLogins,
//                            "No response from server",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                runOnUiThread {
//                    Toast.makeText(
//                        this@GenerateLogins,
//                        "Error posting data: ${e.message}",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//    }
//
//    private fun fetchOrganizations() {
//        val ouId=multipleBatchNameSpinner.selectedItem.toString()
//        val client = OkHttpClient()
//
//        val request = Request.Builder()
//            .url("${ApiFile.APP_URL}/admin/locationListByOu?ouId=$ouId")
//            .build()
//
//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                val response = client.newCall(request).execute()
//                val responseBody = response.body?.string()
//
//                if (response.isSuccessful && responseBody != null) {
//                    val gson = Gson()
//                    val jsonObject = JSONObject(responseBody)
//                    val organizations: List<CameraActivity.Organization> = gson.fromJson(
//                        jsonObject.getJSONArray("obj").toString(),
//                        object : TypeToken<List<CameraActivity.Organization>>() {}.type
//                    )
//                    runOnUiThread {
//                        populateOrganizationSpinner(organizations)
//                    }
//                } else {
//                    runOnUiThread {
//                        Toast.makeText(this@GenerateLogins, "Failed to fetch organizations", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            } catch (e: IOException) {
//                e.printStackTrace()
//                runOnUiThread {
//                    Toast.makeText(this@GenerateLogins, "Failed to fetch organizations due to exception: ${e.message}", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
//
//    private fun populateOrganizationSpinner(organizations: List<CameraActivity.Organization>) {
//        val spinnerItems = mutableListOf("Select Organization")
//        spinnerItems.addAll(organizations.map { "${it.LOCID} - ${it.LOCATIONNAME}" })
//
//        val adapter = object : ArrayAdapter<String>(
//            this,
//            android.R.layout.simple_spinner_item,
//            spinnerItems
//        ) {
//            override fun isEnabled(position: Int): Boolean {
//                return position != 0
//            }
//
//            override fun getDropDownView(
//                position: Int,
//                convertView: View?,
//                parent: ViewGroup
//            ): View {
//                val view = super.getDropDownView(position, convertView, parent)
//                val textView = view as TextView
//
//                if (position == 0) {
//                    textView.setTextColor(ContextCompat.getColor(context, R.color.red))
//                } else {
//                    textView.setTextColor(ContextCompat.getColor(context, android.R.color.black))
//                }
//
//                return view
//            }
//        }
//
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        ouLocSpinner.adapter = adapter
//
//        ouLocSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                if (position > 0) {
//                    val selectedItem = spinnerItems[position]
//                    val selectedOrganization = organizations.find {
//                        "${it.LOCID} - ${it.LOCATIONNAME}" == selectedItem
//                    }
//                    toLocation = selectedOrganization?.LOCATIONNAME ?: ""
//                    toLocationID = (selectedOrganization?.LOCID ?: "") as Int
//                    val locIdToString=toLocationID.toString()
//                    etLocationName.setText(toLocation)
//                    etLocId.setText(locIdToString)
//                }
//            }
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//        }
//    }
//
//
//    private fun openDatePickerDialog(editText: EditText) {
//        val calendar = Calendar.getInstance()
//        val year = calendar.get(Calendar.YEAR)
//        val month = calendar.get(Calendar.MONTH)
//        val day = calendar.get(Calendar.DAY_OF_MONTH)
//
//        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
//            val selectedCalendar = Calendar.getInstance()
//            selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
//
//            val dateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
//            val formattedDate = dateFormat.format(selectedCalendar.time)
//
//            editText.setText(formattedDate)
//        }, year, month, day)
//
//        datePickerDialog.show()
//    }
//
//    private fun clearFields() {
//        etLoginName.text.clear()
//        etPassword.text.clear()
//        etDepartment.text.clear()
//        etLocation.text.clear()
//        etCreationDate.text.clear()
//        etLastUpdateDate.text.clear()
//        etLocId.text.clear()
//        etOuid.text.clear()
//        etLocationName.text.clear()
//        multipleBatchNameSpinner.setSelection(0)
//        ouLocSpinner.setSelection(0)
//    }
//
//    fun goToUpdateLogin() {
//        val intent = Intent(this@GenerateLogins, UpdateLoginInfo::class.java)
//        startActivity(intent)
//    }
//}
