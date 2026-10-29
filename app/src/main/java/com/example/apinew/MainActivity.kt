package com.example.apinew

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import androidx.core.app.ActivityCompat
import android.text.InputFilter
import android.text.TextUtils
import android.net.Uri
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.datatransport.BuildConfig
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Properties
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.net.Network
import android.net.NetworkRequest

class MainActivity : AppCompatActivity() {

    private lateinit var loginName: EditText
    private lateinit var password: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvVersion: TextView
    private lateinit var tvBuildDateTime: TextView
    private var isPasswordVisible: Boolean = false
    private val authManager = AuthManager()
    private lateinit var appversion:String
    private var buildVersion: Int = 0
    private  var hardCodedVersion:Double=2.5 // updated on 15-09-2026
    private lateinit var versionCheck:TextView
    private lateinit var downloadBtn:MaterialButton
//    private lateinit var apkLink:String
private var apkLink: String = ""
    private lateinit var linkTextView:TextView
    private lateinit var connectivityManager: ConnectivityManager
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private var versionCheckCompleted = false   // avoids repeated calls once we succeed
    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>
    private var pendingControllerSession: UserSession? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Must register before STARTED — do this before any early return.
        notificationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { _ ->
            pendingControllerSession?.let { proceedToController(it) }
            pendingControllerSession = null
        }

        // Stay logged in until the user explicitly taps Logout.
        SessionManager.getSession(this)?.let { savedSession ->
            navigateToController(savedSession)
            return
        }

        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        if (!BuildConfig.DEBUG) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }

        setContentView(R.layout.activity_main)
        Log.d("DEBUG", "downloadBtn view: ${findViewById<View>(R.id.downloadBtn)}")
        Log.d("DEBUG", "linkTextView view: ${findViewById<View>(R.id.linkTextView)}")
        Log.d("DEBUG", "versionCheck view: ${findViewById<View>(R.id.versionCheck)}")


        loginName       = findViewById(R.id.usernameEditText)
        password        = findViewById(R.id.passwordEditText)
        btnLogin        = findViewById(R.id.loginButton)
        tvVersion       = findViewById(R.id.tvVersion)
        tvBuildDateTime = findViewById(R.id.tvBuildDateTime)
        versionCheck = findViewById(R.id.versionCheck)
        downloadBtn = findViewById(R.id.downloadBtn)
        linkTextView = findViewById(R.id.linkTextView)

        linkTextView.visibility= View.GONE
        downloadBtn.visibility=View.GONE

        // ── Input filter ──────────────────────────────────────
        val inputFilter = InputFilter { source, start, end, dest, _, _ ->
            for (i in start until end) {
                val char = source[i]
                if (!char.isLetterOrDigit() && char != '-') return@InputFilter ""
                if (char == '-' && dest.contains("-"))       return@InputFilter ""
            }
            null
        }

        loginName.filters = arrayOf(inputFilter)
        password.filters  = arrayOf(inputFilter)

        // ── Version + build info ──────────────────────────────
        tvVersion.text       = "INSTANCE - ${resolveInstance()}"
        tvBuildDateTime.text = "RELEASE  - ${getBuildDateTime()}"

        // ── Password toggle ───────────────────────────────────
        password.setOnTouchListener { _, event ->
            val drawableRight = password.compoundDrawables[2]
            if (event.action == MotionEvent.ACTION_UP && drawableRight != null) {
                if (event.rawX >= password.right - drawableRight.bounds.width()) {
                    togglePasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            false
        }

        // ── Login button ──────────────────────────────────────
        btnLogin.setOnClickListener {
            val user = loginName.text.toString().trim()
            val pass = password.text.toString().trim()
            if (TextUtils.isEmpty(user) || TextUtils.isEmpty(pass)) {
                Toast.makeText(this, "Username/password Required", Toast.LENGTH_LONG).show()
            } else {
                performLogin(user, pass)
            }
        }

        fetchLatestVersion()

        setupNetworkCallback()

//                Handler(Looper.getMainLooper()).postDelayed({
//            linkTextView.text=apkLink
//        }, 3000)

        Handler(Looper.getMainLooper()).postDelayed({
            if (apkLink.isNotEmpty()) {
                linkTextView.text = apkLink
            }
        }, 3000)



        downloadBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(apkLink)
            startActivity(intent)
        }
    }

    // ── Login ─────────────────────────────────────────────────
//    private fun performLogin(username: String, pass: String) {
//        btnLogin.isEnabled = false
//        btnLogin.text      = "Logging in..."
//
//        lifecycleScope.launch {
//            when (val result = authManager.login(username, pass)) {
//
//                is AuthManager.AuthResult.Success -> {
//                    Log.d("Login", "Success via ${result.session.source}")
//                    Toast.makeText(
//                        this@MainActivity, "Login successful", Toast.LENGTH_SHORT
//                    ).show()
//                    navigateToController(result.session)
//                }
//
//                // No dialog — merged session goes straight to Controller
//                // Controller will show rows from BOTH apps combined
//                is AuthManager.AuthResult.MergedSuccess -> {
//                    Log.d("Login", "Merged success — ERP + Delivery combined")
//                    Toast.makeText(
//                        this@MainActivity, "Login successful", Toast.LENGTH_SHORT
//                    ).show()
//                    navigateToController(result.session)
//                }
//
//                is AuthManager.AuthResult.Failure -> {
//                    Log.e("Login", "Failed: ${result.message}")
//                    Toast.makeText(
//                        this@MainActivity, result.message, Toast.LENGTH_LONG
//                    ).show()
//                    btnLogin.isEnabled = true
//                    btnLogin.text      = "LOGIN"
//                }
//            }
//        }
//    }


    private fun setupNetworkCallback() {
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()   // ⚠️ removed NET_CAPABILITY_VALIDATED from the request itself

        networkCallback = object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                Log.d("NetworkCallback", "onAvailable: $network")
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                val isValidated = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                Log.d("NetworkCallback", "onCapabilitiesChanged: validated=$isValidated, versionCheckCompleted=$versionCheckCompleted")

                if (isValidated && !versionCheckCompleted) {
                    runOnUiThread {
                        Log.d("NetworkCallback", "Triggering fetchLatestVersion()")
                        fetchLatestVersion()
                    }
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                Log.d("NetworkCallback", "onLost: $network")
            }
        }

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        networkCallback?.let {
            connectivityManager.unregisterNetworkCallback(it)
        }
    }

    private fun performLogin(username: String, pass: String) {
        btnLogin.isEnabled = false
        btnLogin.text      = "Logging in..."

        lifecycleScope.launch {
            when (val result = authManager.login(username, pass)) {

                is AuthManager.AuthResult.Success -> {
                    Log.d("Login", "Success via ${result.session.source}")
                    Toast.makeText(this@MainActivity, "Login successful", Toast.LENGTH_SHORT).show()
                    SessionManager.saveSession(this@MainActivity, result.session)
                    navigateToController(result.session)
                }

                is AuthManager.AuthResult.MergedSuccess -> {
                    Log.d("Login", "Merged success — ERP + Delivery combined")
                    Toast.makeText(this@MainActivity, "Login successful", Toast.LENGTH_SHORT).show()
                    SessionManager.saveSession(this@MainActivity, result.session)
                    navigateToController(result.session)
                }

                // ── ERP 429 rate limit — restore original behaviour ──
                is AuthManager.AuthResult.RateLimited -> {
                    Log.w("Login", "Rate limited: ${result.message}")
                    Toast.makeText(
                        this@MainActivity,
                        result.message,         // shows the server's message e.g. "Too many attempts"
                        Toast.LENGTH_LONG
                    ).show()
                    btnLogin.isEnabled = true
                    btnLogin.text      = "LOGIN"
                }

                is AuthManager.AuthResult.Failure -> {
                    Log.e("Login", "Failed: ${result.message}")
                    Toast.makeText(
                        this@MainActivity,
                        result.message,
                        Toast.LENGTH_LONG
                    ).show()
                    btnLogin.isEnabled = true
                    btnLogin.text      = "LOGIN"
                }
            }
        }
    }
    // ── Navigate — now includes erpDeptName + deliveryDeptName ─
    private fun navigateToController(session: UserSession) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            pendingControllerSession = session
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            return
        }
        proceedToController(session)
    }

    private fun proceedToController(session: UserSession) {
        DemoVehicleAlertScheduler.scheduleIfTeamLeader(this, session)

        val intent = Intent(this, Controller::class.java).apply {
            putExtra("source",           session.source)
            putExtra("login_name",       session.loginName)
            putExtra("ouId",             session.ouId)
            putExtra("location",         session.location)
            putExtra("locId",            session.locId)
            putExtra("location_name",    session.locationName)
            putExtra("deptName",         session.deptName)
            putExtra("attribute1",       session.attribute1)
            putExtra("attribute2",       session.attribute2)
            putExtra("emailId",          session.emailId)
            putExtra("userContact",      session.userContact)
            putExtra("role",      session.role)
            putExtra("dmsLoc",      session.dmsLoc)
            putExtra("userName",         session.userName)
            putExtra("erpDeptName",      session.erpDeptName)
            // ↓ FIX: for pure delivery, erpDeptName = "" and deliveryDeptName = deptName
            putExtra("deliveryDeptName", session.deliveryDeptName
                .takeIf { it.isNotEmpty() } ?: session.deptName)
        }
        startActivity(intent)
        finish()
    }

    // ── Password show/hide ────────────────────────────────────
    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            password.transformationMethod = PasswordTransformationMethod.getInstance()
            password.setCompoundDrawablesWithIntrinsicBounds(
                0, 0, R.drawable.ic_visibility_off, 0
            )
        } else {
            password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            password.setCompoundDrawablesWithIntrinsicBounds(
                0, 0, R.drawable.ic_visibility, 0
            )
        }
        password.setSelection(password.text.length)
        isPasswordVisible = !isPasswordVisible
    }

    // ── Instance label ────────────────────────────────────────
    private fun resolveInstance(): String {
        return when (ApiFile.ERP_APP_URL.trimEnd('/')) {
            "http://182.72.0.216:7485/ErpAndroid"  -> "PRODUCTION NEW"
            "https://nexgenapp.saihorizon.org:7585/ErpAndroid" -> "PRODUCTION"
            "http://172.16.21.149:6101/ErpAndroid" -> "CLONE"
            "http://10.0.2.2:8081"                 -> "LOCALHOST"
            else                                   -> "UNKNOWN"
        }
    }

    // ── Build time ────────────────────────────────────────────
    private fun getBuildDateTime(): String {
        val millis = getBuildTimeFromProperties()
        val fmt    = SimpleDateFormat("dd-MM-yyyy - HH:mm:ss", Locale.getDefault())
        return fmt.format(Date(millis))
    }

    private fun getBuildTimeFromProperties(): Long {
        return try {
            val props = Properties()
            props.load(assets.open("app_config.properties"))
            props.getProperty("BUILD_TIME")?.toLong() ?: System.currentTimeMillis()
        } catch (e: Exception) {
            Log.e("MainActivity", "Could not read BUILD_TIME: ${e.message}")
            System.currentTimeMillis()
        }
    }


    data class AppVersion(
        val versionName: String,
        val versionCode: String,
        val downloadUrl: String,
        val releaseDate: String
    )

    private fun isNetworkAvailable(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    @SuppressLint("SuspiciousIndentation")
    private fun fetchLatestVersion() {
        if (!isNetworkAvailable()) {
            Log.w("fetchLatestVersion", "No network connection — skipping version check")
            btnLogin.isEnabled = true
            linkTextView.visibility = View.GONE
            downloadBtn.visibility = View.GONE
            Toast.makeText(this, "You are offline.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val apiUrl = "${ApiFile.ERP_APP_URL}/appVer/latest"
            try {
                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                if (connection.responseCode == 200) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonResponse = JSONObject(response)
                    val obj = jsonResponse.getJSONObject("obj")

                    appversion = obj.getString("versionCode")
                    apkLink = obj.optString("downloadUrl")

                    withContext(Dispatchers.Main) {
                        versionCheckCompleted = true   // ✅ mark done so callback stops re-firing

                        if (appversion.toDouble() == hardCodedVersion) {
                            btnLogin.isEnabled = true
                            linkTextView.visibility = View.GONE
                            downloadBtn.visibility = View.GONE
                        } else if (appversion.toDouble() > hardCodedVersion) {
                            btnLogin.isEnabled = false
                            linkTextView.visibility = View.VISIBLE
                            downloadBtn.visibility = View.VISIBLE
                            versionCheck.text = "The new version is available kindly contact your IT-Admin/Download app from below."
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        btnLogin.isEnabled = true
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    btnLogin.isEnabled = true
                    linkTextView.visibility = View.GONE
                    downloadBtn.visibility = View.GONE
                    // no toast here either — internet may still be flaky, callback will retry
                }
            }
        }
    }
}