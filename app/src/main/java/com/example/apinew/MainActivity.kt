package com.example.apinew

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.text.InputFilter
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
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Properties

class MainActivity : AppCompatActivity() {
    private lateinit var loginName: EditText
    private lateinit var password: EditText
    private lateinit var btnLogin: Button
    private var isPasswordVisible: Boolean = false
    private lateinit var appversion:String
    private var buildVersion: Int = 0
    private  var hardCodedVersion:Double=0.0
    private lateinit var versionCheck:TextView


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hardCodedVersion=1.0

//        if (isEmulator()) {
//            Toast.makeText(this, "App cannot run on emulator", Toast.LENGTH_LONG).show()
//            finishAffinity()
//            return
//        }

        if (isThreatDetected()) {
            Toast.makeText(this, "Security Threat Detected!", Toast.LENGTH_LONG).show()
            finishAffinity()
        }

        loginName = findViewById(R.id.usernameEditText)
        password = findViewById(R.id.passwordEditText)
        btnLogin = findViewById(R.id.loginButton)
        versionCheck = findViewById(R.id.versionCheck)


        val tvVersion = findViewById<TextView>(R.id.tvVersion)
        val tvBuildDateTime = findViewById<TextView>(R.id.tvBuildDateTime)

        val version = when (ApiFile.APP_URL) {
            "http://182.72.0.216:7485/ErpAndroid" ->  "PRODUCTION"
            "http://172.16.21.149:6101/ErpAndroid" -> "CLONE"
            "http://10.0.2.2:8081" -> "LOCALHOST"
            else -> "UNKNOWN"
        }

//        buildVersion = BuildConfig.VERSION_CODE
//        Log.d("buildVersion->FromOnCreate",buildVersion.toString())
//        val versionName = BuildConfig.VERSION_NAME

        val buildDateTime = getBuildDateTime()

        val filter = InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                val char = source[i]

                if (!char.isLetterOrDigit() && char != '-') {
                    return@InputFilter ""
                }

                if (char == '-' && dest.contains("-")) {
                    return@InputFilter ""
                }
            }
            null
        }

        loginName.filters = arrayOf(filter)
        password.filters = arrayOf(filter)


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

//        fetchLatestVersion()

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

//    private fun login() {
//        val loginRequest = LoginRequest().apply {
//            loginName = this@MainActivity.loginName.text.toString()
//            password = this@MainActivity.password.text.toString()
//        }
//
//        val loginResponseCall = ApiClient.getUserService().userLogin(loginRequest)
//        loginResponseCall.enqueue(object : Callback<LoginResponse> {
//            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
//                if (response.isSuccessful) {
//                    val loginResponse = response.body()
//
//                    if (loginResponse != null && loginResponse.code == 200) {
//                        val attribute1 = loginResponse.attribute1
//                        val ouId = loginResponse.ouId
//                        val login_name = loginResponse.loginName
//                        val location = loginResponse.location
//                        val locId = loginResponse.locId
//                        val location_name=loginResponse.location_name
//                        val deptName=loginResponse.deptName
//                        val emailId=loginResponse.emailId
//
//                        Toast.makeText(this@MainActivity, "Login successful", Toast.LENGTH_LONG)
//                            .show()
//                        if (attribute1 != null && ouId != null &&location!=null&&locId!=null&&location_name!=null&&deptName!=null) {
//                            navigateToHomeActivity(attribute1, ouId,login_name,location,locId,location_name,deptName,emailId)
////                            navigateToHomeActivity(attribute1, ouId,login_name)
//                        } else {
//                            Toast.makeText(
//                                this@MainActivity,
//                                "Missing attributes",
//                                Toast.LENGTH_LONG
//                            ).show()
//                        }
//                    } else if (loginResponse != null && loginResponse.code == 400) {
//                        Toast.makeText(
//                            this@MainActivity,
//                            loginResponse.obj.toString(),
//                            Toast.LENGTH_LONG
//                        ).show()
//                    } else {
//                        Toast.makeText(this@MainActivity, "Unexpected response", Toast.LENGTH_LONG)
//                            .show()
//                    }
//                } else {
//                    val errorResponse = response.errorBody()?.string()
//                    if (response.code() == 400) {
//                        Toast.makeText(
//                            this@MainActivity,
//                            "Invalid credentials: $errorResponse",
//                            Toast.LENGTH_LONG
//                        ).show()
//                    } else {
//                        Toast.makeText(
//                            this@MainActivity,
//                            "Unexpected error: ${response.code()}",
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
//                Toast.makeText(this@MainActivity, "Network error", Toast.LENGTH_LONG).show()
//                Log.d("Throwable", t.printStackTrace().toString())
//                Log.d("Throwable", t.message.toString())
//
//            }
//        })
//    }


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

                    if (loginResponse != null) {
                        when (loginResponse.code) {
                            200 -> {
                                val attribute1 = loginResponse.attribute1
                                val ouId = loginResponse.ouId
                                val login_name = loginResponse.loginName
                                val location = loginResponse.location
                                val locId = loginResponse.locId
                                val location_name = loginResponse.location_name
                                val deptName = loginResponse.deptName
                                val emailId = loginResponse.emailId

                                Toast.makeText(this@MainActivity, "Login successful", Toast.LENGTH_LONG).show()

                                if (attribute1 != null && ouId != null && location != null && locId != null &&
                                    location_name != null && deptName != null) {
                                    navigateToHomeActivity(attribute1, ouId, login_name, location, locId, location_name, deptName, emailId)
                                } else {
                                    Toast.makeText(this@MainActivity, "Missing attributes", Toast.LENGTH_LONG).show()
                                }
                            }

                            400 -> {
                                Toast.makeText(this@MainActivity, loginResponse.obj.toString(), Toast.LENGTH_LONG).show()
                            }

                            429 -> {
                                Toast.makeText(this@MainActivity, loginResponse.obj.toString(), Toast.LENGTH_LONG).show()
                                Log.d("LoginDebug", "Response code from body: ${loginResponse.code}, message: ${loginResponse.message}")
                            }

                            else -> {
                                Toast.makeText(this@MainActivity, "Unexpected code: ${loginResponse.code}", Toast.LENGTH_LONG).show()
                                Log.d("LoginDebug", "Response code from body: ${loginResponse.code}, message: ${loginResponse.message}")
                            }
                        }
                    } else {
                        Toast.makeText(this@MainActivity, "Empty response body", Toast.LENGTH_LONG).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@MainActivity, "HTTP Error: ${response.code()} $errorBody", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Network error", Toast.LENGTH_LONG).show()
                Log.e("LoginFailure", "Throwable: ${t.message}", t)
            }
        })
    }


    private fun navigateToHomeActivity(attribute1: String, ouId: Int,login_name:String,location:String,locId:Int,location_name:String,deptName:String,emailId:String?=null) {
//        private fun navigateToHomeActivity(attribute1: String, ouId: Int,login_name:String)
        val intent = Intent(this@MainActivity, Controller::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name", location_name)
        intent.putExtra("deptName", deptName)
        intent.putExtra("emailId",emailId)


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

    fun isDeviceRooted(): Boolean {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3()
    }

    fun isThreatDetected(): Boolean {
        return isDeviceRooted() || isFridaDetected() || isXposedDetected()
    }


    private fun checkRootMethod1(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )
        return paths.any { File(it).exists() }
    }

    private fun checkRootMethod2(): Boolean {
        return try {
            Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
                .inputStream.bufferedReader().readLine() != null
        } catch (e: Exception) {
            false
        }
    }

    private fun checkRootMethod3(): Boolean {
        return Build.TAGS?.contains("test-keys") == true
    }

    fun isFridaDetected(): Boolean {
        return try {
            val fridaLibs = listOf("frida", "frida-agent", "gum-js-loop")
            val maps = File("/proc/${android.os.Process.myPid()}/maps").readText()
            fridaLibs.any { maps.contains(it, ignoreCase = true) }
        } catch (e: Exception) {
            false
        }
    }

    fun isXposedDetected(): Boolean {
        val xposedClasses = listOf(
            "de.robv.android.xposed.XposedBridge",
            "de.robv.android.xposed.XposedHelpers"
        )
        return xposedClasses.any {
            try {
                Class.forName(it)
                true
            } catch (e: ClassNotFoundException) {
                false
            }
        }
    }

    data class AppVersion(
        val versionName: String,
        val versionCode: String,
        val downloadUrl: String,
        val releaseDate: String
    )

    @SuppressLint("SuspiciousIndentation")
    fun fetchLatestVersion(): AppVersion? {
        Log.d("function reached","success")
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val apiUrl = "${ApiFile.APP_URL}/appVer/latest"

        try {
            val url = URL(apiUrl)
            Log.d("URL->",url.toString())
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            if (connection.responseCode == 200) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonResponse = JSONObject(response)
                val obj = jsonResponse.getJSONObject("obj")

                appversion=obj.getString("versionCode")

                Handler(Looper.getMainLooper()).postDelayed({
                    if(appversion.toDouble()==hardCodedVersion){
            btnLogin.isEnabled=true
                        Toast.makeText(this@MainActivity,"You have the latest app version you can proceed",Toast.LENGTH_LONG).show()
                    } else if (appversion.toDouble()<hardCodedVersion) {
            btnLogin.isEnabled=false
                        versionCheck.text="The new version is available kindly contact your IT-Admin"
}
        }, 1000)

//                return AppVersion(
//                    versionName = obj.getString("versionName"),
//                    versionCode = obj.getString("versionCode"),
//                    downloadUrl = obj.getString("downloadUrl"),
//                    releaseDate = obj.getString("releaseDate")
//                )
            } else {
                println("Error: ${connection.responseCode}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }



}