package com.example.apinew

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class UpdateLoginInfo : AppCompatActivity() {

    private lateinit var etLoginName: EditText
    private lateinit var etPassword: EditText
    private lateinit var etDepartment: EditText
    private lateinit var etLocation: EditText
    private lateinit var etCreationDate: EditText
    private lateinit var etLastUpdateDate: EditText
    private lateinit var etLocId: EditText
    private lateinit var etOuid: EditText
    private lateinit var etLocationName: EditText
    private lateinit var btnPost: Button
    private lateinit var multipleBatchNameSpinner:Spinner
    private lateinit var btnClear:Button
    private lateinit var btnUpdate:Button
    private lateinit var btngetInfo:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_login_info)

        etLoginName = findViewById(R.id.etLoginName)
        etPassword = findViewById(R.id.etPassword)
        etDepartment = findViewById(R.id.etDepartment)
        etLocation = findViewById(R.id.etLocation)
        etCreationDate = findViewById(R.id.etCreationDate)
        etLastUpdateDate = findViewById(R.id.etLastUpdateDate)
        etLocId = findViewById(R.id.etLocId)
        etOuid = findViewById(R.id.etOUID)
        etLocationName = findViewById(R.id.etLocationName)
        btnPost = findViewById(R.id.btnPost)
        multipleBatchNameSpinner=findViewById(R.id.multipleBatchNameSpinner)
        btnClear=findViewById(R.id.btnClear)
        btnUpdate=findViewById(R.id.btnUpdate)
        btngetInfo=findViewById(R.id.btngetInfo)

        etCreationDate = findViewById(R.id.etCreationDate)
        etLastUpdateDate = findViewById(R.id.etLastUpdateDate)

        etCreationDate.setOnClickListener {
            openDatePickerDialog(etCreationDate)
        }

        etLastUpdateDate.setOnClickListener {
            openDatePickerDialog(etLastUpdateDate)
        }

        btnUpdate.setOnClickListener {
            updateLoginInfo()
        }

        btnClear.setOnClickListener {
            clearFields()
        }

        btngetInfo.setOnClickListener {
            fetchLoginData()
        }

        val typeOptions = mutableListOf("SELECT  OU ID",  "81","104","105","106","107","108")
        val typeAdapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, typeOptions) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view as TextView
                if (position == 0) {
                    textView.setTextColor(ContextCompat.getColor(context, R.color.red))
                } else {
                    textView.setTextColor(ContextCompat.getColor(context, R.color.black))
                }
                return view
            }
        }
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        multipleBatchNameSpinner.adapter = typeAdapter

        multipleBatchNameSpinner.setSelection(0, false)


    }

    private fun updateLoginInfo() {
        val client = OkHttpClient()
        val url = ApiFile.APP_URL + "/admin/updateUser"

        val jsonObject = JSONObject().apply {
            put("loginName", etLoginName.text.toString())
            put("password", etPassword.text.toString())
            put("deptName", etDepartment.text.toString())
            put("location", etLocation.text.toString())
            put("locId", etLocId.text.toString())
            put("ouId", etOuid.text.toString())
//            put("ouId",ouId)
            put("location_name", etLocationName.text.toString())
            put("attribute1", "")
            put("attribute2", "")
            put("attribute3", "")
            put("attribute4", "")
            put("userId", "")
            put("emailId", "")
            put("dmsLoc", "")
            put("role", "")
        }

        val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
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
                        val responseJson = JSONObject(responseBody)
                        val message = responseJson.optString("message", "")

                        when {
                            responseCode == 200 -> {
                                Toast.makeText(
                                    this@UpdateLoginInfo,
                                    "User Updated Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                clearFields()
                            }
                            else -> {
                                Toast.makeText(
                                    this@UpdateLoginInfo,
                                    "Failed to update user. Error code: $responseCode",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@UpdateLoginInfo,
                            "No response from server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@UpdateLoginInfo,
                        "Error updating data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun openDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(selectedYear, selectedMonth, selectedDay)

            val dateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(selectedCalendar.time)

            editText.setText(formattedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun fetchLoginData() {
        val loginName=etLoginName.text.toString()
        val client = OkHttpClient()
        val url =ApiFile.APP_URL+"/admin/detailsByLoginName?loginName=$loginName"

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

                    val RegData =User(
                        LOCATION = stockItem.getString("LOCATION"),
                        LOGINNAME = stockItem.getString("LOGINNAME"),
                        LOCATION_NAME = stockItem.getString("LOCATION_NAME"),
                        PASSWORD = stockItem.getString("PASSWORD"),
                        DEPTNAME = stockItem.getString("DEPTNAME"),
                        LOCID = stockItem.getInt("LOCID"),
                        OUID = stockItem.getInt("OUID")
                        )
                    runOnUiThread {
                        populateFields(RegData)
                        Toast.makeText(
                            this@UpdateLoginInfo,
                            "Details found Successfully \n for User: $loginName",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@UpdateLoginInfo,
                        "Failed to fetch details for User: $loginName",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun populateFields(user:User) {
    etLoginName.setText("${user.LOGINNAME}")
        etOuid.setText("${user.OUID}")
        etLocId.setText("${user.LOCID}")
        etPassword.setText("${user.PASSWORD}")
        etDepartment.setText("${user.DEPTNAME}")
        etLocationName.setText("${user.LOCATION_NAME}")
        etLocation.setText("${user.LOCATION}")
    }

    private fun clearFields() {
        etLoginName.text.clear()
        etPassword.text.clear()
        etDepartment.text.clear()
        etLocation.text.clear()
        etCreationDate.text.clear()
        etLastUpdateDate.text.clear()
        etLocId.text.clear()
        etOuid.text.clear()
        etLocationName.text.clear()
    }

    data class User(
        val LOCATION:String,
        val LOGINNAME:String,
        val LOCATION_NAME:String,
        val PASSWORD:String,
        val LOCID:Int,
        val DEPTNAME:String,
        val OUID:Int
    )


}
