//package com.example.apinew
//
//import android.annotation.SuppressLint
//import android.content.Intent
//import android.os.Bundle
//import android.text.method.HideReturnsTransformationMethod
//import android.text.method.PasswordTransformationMethod
//import android.util.Log
//import android.view.MotionEvent
//import android.widget.Button
//import android.widget.EditText
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import okhttp3.*
//import okhttp3.MediaType.Companion.toMediaType
//import org.json.JSONObject
//import java.io.IOException
//
//
//class Forgotp : AppCompatActivity() {
//    private lateinit var usernameEditText: EditText
//    private lateinit var npasswordEditText: EditText
//    private lateinit var cPasswordEditText: EditText
//    private lateinit var savePassword: Button
//    private lateinit var backToLogin: Button
//    private var isPasswordVisible: Boolean = false
//
//    @SuppressLint("ClickableViewAccessibility")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_forgotp)
//
//        usernameEditText = findViewById(R.id.usernameEditText)
//        npasswordEditText = findViewById(R.id.npasswordEditText)
//        cPasswordEditText = findViewById(R.id.cpasswordEditText)
//        savePassword = findViewById(R.id.savePassword)
//        backToLogin = findViewById(R.id.backTologin)
//
//
//
//        npasswordEditText.setOnTouchListener { _, event ->
//            val drawable = 2
//            if (event.action == MotionEvent.ACTION_UP) {
//                if (event.rawX >= (npasswordEditText.right - npasswordEditText.compoundDrawables[drawable].bounds.width())) {
//                    togglePasswordVisibility()
//                    return@setOnTouchListener true
//                }
//            }
//            false
//        }
//        cPasswordEditText.setOnTouchListener { _, event ->
//            val drawable = 2
//            if (event.action == MotionEvent.ACTION_UP) {
//                if (event.rawX >= (cPasswordEditText.right - cPasswordEditText.compoundDrawables[drawable].bounds.width())) {
//                    togglePasswordVisibility2()
//                    return@setOnTouchListener true
//                }
//            }
//            false
//        }
//        savePassword.setOnClickListener {
//            updatePassword()
//        }
//
//        backToLogin.setOnClickListener {
//            backButton()
//        }
//    }
//
//    private fun togglePasswordVisibility() {
//        if (isPasswordVisible) {
//            npasswordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
//            npasswordEditText.setCompoundDrawablesWithIntrinsicBounds(
//                0,
//                0,
//                R.drawable.ic_visibility_off,
//                0
//            )
//        } else {
//            npasswordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
//            npasswordEditText.setCompoundDrawablesWithIntrinsicBounds(
//                0,
//                0,
//                R.drawable.ic_visibility,
//                0
//            )
//        }
//        npasswordEditText.setSelection(npasswordEditText.text.length)
//        isPasswordVisible = !isPasswordVisible
//    }
//
//    private fun togglePasswordVisibility2() {
//        if (isPasswordVisible) {
//            cPasswordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
//            cPasswordEditText.setCompoundDrawablesWithIntrinsicBounds(
//                0,
//                0,
//                R.drawable.ic_visibility_off,
//                0
//            )
//        } else {
//            cPasswordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
//            cPasswordEditText.setCompoundDrawablesWithIntrinsicBounds(
//                0,
//                0,
//                R.drawable.ic_visibility,
//                0
//            )
//        }
//        cPasswordEditText.setSelection(cPasswordEditText.text.length)
//        isPasswordVisible = !isPasswordVisible
//    }
//
//    private fun backButton() {
//        val intent = Intent(this@Forgotp, MainActivity::class.java)
//        startActivity(intent)
//        finish()
//    }
//
//    private fun updatePassword() {
//        val loginName = usernameEditText.text.toString().trim()
//        val nPassword = npasswordEditText.text.toString().trim()
//        val cPassword = cPasswordEditText.text.toString().trim()
//
//        if (loginName.isEmpty() || nPassword.isEmpty() || cPassword.isEmpty()) {
//            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        if (nPassword != cPassword) {
//            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val client = OkHttpClient()
//
//        val jsonBody = JSONObject()
//        jsonBody.put("loginName", loginName)
//        jsonBody.put("nPassword", nPassword)
//        jsonBody.put("cPassword", cPassword)
//
//        val requestBody = RequestBody.create("application/json".toMediaType(), jsonBody.toString())
//
//        val request = Request.Builder()
//            .url("${ApiFile.APP_URL}/login/resetpassword")
//            .put(requestBody)
//            .addHeader("Content-Type", "application/json")
//            .build()
//
//        client.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                e.printStackTrace()
//                runOnUiThread {
//                    Toast.makeText(this@Forgotp, "Failed to update password", Toast.LENGTH_SHORT)
//                        .show()
//                }
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                val responseBody = response.body?.string() ?: ""
//                runOnUiThread {
//                    try {
//                        val jsonObject = JSONObject(responseBody)
//                        val message = jsonObject.getString("message")
//                        val code = jsonObject.getInt("code")
//
//                        if(code == 200) {
//                            Toast.makeText(this@Forgotp, message, Toast.LENGTH_SHORT).show()
//                            val intent = Intent(this@Forgotp, MainActivity::class.java)
//                            startActivity(intent)
//                            finish()
//                        } else {
//                            Toast.makeText(this@Forgotp, message, Toast.LENGTH_SHORT).show()
//                        }
//                    } catch (e: Exception) {
//                        Toast.makeText(
//                            this@Forgotp,
//                            "An unexpected error occurred",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            }
//        })
//    }
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
