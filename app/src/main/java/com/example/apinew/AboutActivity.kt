package com.example.apinew

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Properties

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val tvVersion = findViewById<TextView>(R.id.tvVersion)
        val tvBuildDateTime = findViewById<TextView>(R.id.tvBuildDateTime)

        val version = when (ApiFile.APP_URL) {
//            "http://182.73.44.117:8080/ErpAndroid" -> "BOR PRODUCTION"
            "http://182.72.0.216:7485/ErpAndroid/" ->  "PRODUCTION"
            "http://115.242.10.86:6101/AndroidFA" -> "PROD-FA-TEMP"
            "http://10.0.2.2:8081" -> "LOCALHOST"

            else -> "UNKNOWN"
        }

        val buildDateTime = getBuildDateTime()

        tvVersion.text = "Version: $version"
        tvBuildDateTime.text = "Release Date & Time: $buildDateTime"
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