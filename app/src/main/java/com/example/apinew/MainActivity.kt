package com.example.apinew

import LoginRequest
import LoginResponse
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.text.method.HideReturnsTransformationMethod
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Properties

class MainActivity : AppCompatActivity() {
    private lateinit var loginName: EditText
    private lateinit var password: EditText
    private lateinit var btnLogin: Button
    private lateinit var forgotPBtn: TextView
    private var isPasswordVisible: Boolean = false
    private lateinit var device: String

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loginName = findViewById(R.id.usernameEditText)
        password = findViewById(R.id.passwordEditText)
        btnLogin = findViewById(R.id.loginButton)
        forgotPBtn = findViewById(R.id.forgotPBtn)

        val tvVersion = findViewById<TextView>(R.id.tvVersion)
        val tvBuildDateTime = findViewById<TextView>(R.id.tvBuildDateTime)
        device = "${Build.MANUFACTURER} ${Build.MODEL}"

        // Set version information based on the URL
        val version = when (ApiFile.APP_URL) {
            "http://182.72.0.216:7485/AndroidFA" -> "PRODUCTION"
            "http://115.242.10.86:6101/AndroidFA" -> "PROD-FA-TEMP"
            "http://10.0.2.2:8081" -> "LOCALHOST"
            else -> "UNKNOWN"
        }

        val buildDateTime = getBuildDateTime()

        tvVersion.text = "INSTANCE - $version"
        tvBuildDateTime.text = "RELEASE - $buildDateTime"

        password.setOnTouchListener { _, event ->
            val DRAWABLE_RIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (password.right - password.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                    togglePasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            false
        }

        btnLogin.setOnClickListener {
            if (TextUtils.isEmpty(loginName.text.toString()) || TextUtils.isEmpty(password.text.toString())) {
                Toast.makeText(this@MainActivity, "Username/password Required", Toast.LENGTH_LONG).show()
            } else {
                login()
            }
        }

        forgotPBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, Forgotp::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            password.transformationMethod = PasswordTransformationMethod.getInstance()
            password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0)
        } else {
            password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility, 0)
        }
        password.setSelection(password.text.length)
        isPasswordVisible = !isPasswordVisible
    }

    private fun login() {
        val loginRequest = LoginRequest().apply {
            loginName = this@MainActivity.loginName.text.toString()
            password = this@MainActivity.password.text.toString()
        }

        val loginResponseCall = ApiClient.getUserService().userLogin(loginRequest)
        loginResponseCall.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null && loginResponse.code == 200) {
                        val ouId = loginResponse.ouId
                        val login_name = loginResponse.loginName
                        val location = loginResponse.locName
                        val locId = loginResponse.locId
                        val deptName = loginResponse.deptName
                        val role=loginResponse.role

                        Log.d("ouId",ouId.toString())
                        Log.d("login_name",login_name)
                        Log.d("location",location.toString())
                        Log.d("locId",locId.toString())
                        Log.d("deptName",deptName.toString())
                        Log.d("role",role.toString())

                        Log.d("LoginResponse", "Success: $loginResponse")
                        Toast.makeText(this@MainActivity, device, Toast.LENGTH_LONG).show()
                        Log.d("LoginResponse", "Device: $device")

                        if (ouId != null && location != null && locId != null && deptName != null && role!=null) {
                            navigateToHomeActivity(ouId.toString(), login_name, location,
                                locId.toString(), deptName,role)
                        } else {
                            Toast.makeText(this@MainActivity, "Missing attributes", Toast.LENGTH_LONG).show()
                        }
                    } else if (loginResponse != null && loginResponse.code == 400) {
                        Toast.makeText(this@MainActivity, loginResponse.obj.toString(), Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@MainActivity, "Unexpected response", Toast.LENGTH_LONG).show()
                    }
                } else {
                    val errorResponse = response.errorBody()?.string()
                    if (response.code() == 400) {
                        Toast.makeText(this@MainActivity, "Invalid credentials: $errorResponse", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@MainActivity, "Unexpected error: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("LoginError", "onFailure: ${t.message}", t)
                Toast.makeText(this@MainActivity, "Network error", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun navigateToHomeActivity(ouId: String, loginName: String, location: String, locId: String, deptName: String,role:String) {
        Log.d("MainActivity", "Navigating to Home Activity with ouId: $ouId, loginName: $loginName,locID:$locId")
        val intent = Intent(this@MainActivity, Controller::class.java)
        intent.putExtra("ouId", ouId)
        intent.putExtra("login_name", loginName)
        intent.putExtra("locName", location)
        intent.putExtra("locId", locId)
        intent.putExtra("deptName", deptName)
        intent.putExtra("role",role)
        startActivity(intent)
        finish()
    }

    private fun getBuildDateTime(): String {
        val buildTimeMillis = getBuildTimeFromProperties()
        val buildDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(buildTimeMillis))
        val buildTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(buildTimeMillis))
        return "$buildDate - $buildTime"
    }

    private fun getBuildTimeFromProperties(): Long {
        val properties = Properties()
        try {
            val inputStream = assets.open("app_config.properties")
            properties.load(inputStream)
            return properties.getProperty("BUILD_TIME")?.toLong() ?: System.currentTimeMillis()
        } catch (e: Exception) {
            e.printStackTrace()
            return System.currentTimeMillis()
        }
    }
}

