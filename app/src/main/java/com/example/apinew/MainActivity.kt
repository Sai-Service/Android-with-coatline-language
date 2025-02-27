//package com.example.apinew
//
//import LoginRequest
//import LoginResponse
//import android.annotation.SuppressLint
//import android.content.Intent
//import android.os.Bundle
//import android.text.TextUtils
//import android.text.method.PasswordTransformationMethod
//import android.text.method.HideReturnsTransformationMethod
//import android.util.Log
//import android.view.MotionEvent
//import android.widget.Button
//import android.widget.EditText
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import retrofit2.Call
//import retrofit2.Response
//
//class MainActivity : AppCompatActivity() {
//    private lateinit var loginName: EditText
//    private lateinit var password: EditText
//    private lateinit var btnLogin: Button
//    private lateinit var forgotPBtn: TextView
//    private var isPasswordVisible: Boolean = false
//
//    @SuppressLint("ClickableViewAccessibility")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        loginName = findViewById(R.id.usernameEditText)
//        password = findViewById(R.id.passwordEditText)
//        btnLogin = findViewById(R.id.loginButton)
//        forgotPBtn = findViewById(R.id.forgotPBtn)
//
//        password.setOnTouchListener { _, event ->
//            val DRAWABLE_RIGHT = 2
//            if (event.action == MotionEvent.ACTION_UP) {
//                if (event.rawX >= (password.right - password.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
//                    togglePasswordVisibility()
//                    return@setOnTouchListener true
//                }
//            }
//            false
//        }
//
//        btnLogin.setOnClickListener {
//            if (TextUtils.isEmpty(loginName.text.toString()) || TextUtils.isEmpty(password.text.toString())) {
//                Toast.makeText(this@MainActivity, "Username/password Required", Toast.LENGTH_LONG)
//                    .show()
//            } else {
//                login()
//            }
//        }
//        forgotPBtn.setOnClickListener {
//            val intent = Intent(this@MainActivity, Forgotp::class.java)
//            startActivity(intent)
//            finish()
//        }
//    }
//
//    private fun togglePasswordVisibility() {
//        if (isPasswordVisible) {
//            password.transformationMethod = PasswordTransformationMethod.getInstance()
//            password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0)
//        } else {
//            password.transformationMethod = HideReturnsTransformationMethod.getInstance()
//            password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility, 0)
//        }
//        password.setSelection(password.text.length)
//        isPasswordVisible = !isPasswordVisible
//    }
//
//    private fun login() {
//        val loginRequest = LoginRequest()
//        loginRequest.loginName = loginName.text.toString()
//        loginRequest.password = password.text.toString()
//
//        val loginResponseCall = ApiClient.getUserService().userLogin(loginRequest)
//        loginResponseCall.enqueue(object : retrofit2.Callback<LoginResponse>{
//            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
//                if (response.isSuccessful) {
//                    val loginResponse = response.body()
//                    if (loginResponse != null && loginResponse.code == 200) {
//                        val attribute1 = loginResponse.obj.attribute1
//                        val ouId = loginResponse.obj.ouId
//                        Log.d(loginResponse.toString(), "success")
//                        Toast.makeText(this@MainActivity, "Login successful", Toast.LENGTH_LONG)
//                            .show()
//                        navigateToHomeActivity(attribute1, ouId)
//                    }
//                } else {
//                    val code = response.code()
//                    Log.d("code",code.toString())
//                    if (code == 400) {
//                        Toast.makeText(this@MainActivity, "Invalid credentials", Toast.LENGTH_LONG)
//                            .show()
//                    }
//                }
//            }
//            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
//                Toast.makeText(this@MainActivity, "Network error", Toast.LENGTH_LONG).show()
//
//            }
//        })
//    }
//            private fun navigateToHomeActivity(attribute1: String, ouId: Int) {
//                val intent = Intent(this@MainActivity, CameraActivity::class.java)
//                intent.putExtra("attribute1", attribute1)
//                intent.putExtra("ouId", ouId)
//                startActivity(intent)
//                finish()
//            }
//        }
//



package com.example.apinew

import LoginRequest
import LoginResponse
import android.annotation.SuppressLint
import android.content.Intent
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

        val version = when (ApiFile.APP_URL) {
            "http://182.72.0.216:7485/ErpAndroid" ->  "PRODUCTION"
            "http://115.242.10.86:6101/ErpAndroid" -> "CLONE"
            "http://10.0.2.2:8081" -> "LOCALHOST"
            "http://203.115.117.157:6101/ErpAndroid" ->"CLONE2"
            "http://115.242.10.85:7485/ErpAndroid"->"PRODUCTION"
            
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
                Toast.makeText(this@MainActivity, "Username/password Required", Toast.LENGTH_LONG)
                    .show()
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

                    Log.d("LoginResponse",  loginResponse.toString())
                    if (loginResponse != null && loginResponse.code == 200) {
                        val attribute1 = loginResponse.attribute1
                        val ouId = loginResponse.ouId
                        val login_name = loginResponse.loginName
                        val location = loginResponse.location
                        val locId = loginResponse.locId
                        val location_name=loginResponse.location_name
                        val deptName=loginResponse.deptName
                        Log.d("LoginResponse", "Success: $loginResponse")

                        Toast.makeText(this@MainActivity, "Login successful", Toast.LENGTH_LONG)
                            .show()
                        if (attribute1 != null && ouId != null &&location!=null&&locId!=null&&location_name!=null&&deptName!=null) {
                            navigateToHomeActivity(attribute1, ouId,login_name,location,locId,location_name,deptName)
//                            navigateToHomeActivity(attribute1, ouId,login_name)
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Missing attributes",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else if (loginResponse != null && loginResponse.code == 400) {
                        Toast.makeText(
                            this@MainActivity,
                            loginResponse.obj.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(this@MainActivity, "Unexpected response", Toast.LENGTH_LONG)
                            .show()
                    }
                } else {
                    val errorResponse = response.errorBody()?.string()
                    if (response.code() == 400) {
                        Toast.makeText(
                            this@MainActivity,
                            "Invalid credentials: $errorResponse",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Unexpected error: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("LoginError", "onFailure: ${t.message}", t)
                Toast.makeText(this@MainActivity, "Network error", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun navigateToHomeActivity(attribute1: String, ouId: Int,login_name:String,location:String,locId:Int,location_name:String,deptName:String) {
//        private fun navigateToHomeActivity(attribute1: String, ouId: Int,login_name:String) {
            Log.d(
            "MainActivity",
            "Navigating to Home Activity with attribute1: $attribute1, ouId: $ouId")
        val intent = Intent(this@MainActivity, Controller::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name", location_name)
        intent.putExtra("deptName", deptName)


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