package com.example.apinew

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class QrScanner : AppCompatActivity() {

    private val client = OkHttpClient()
    private lateinit var btnGenerate: Button
    private lateinit var fetchChassisDataButton: Button
    private lateinit var tvResult: TextView
    private lateinit var chassis_no: EditText
    private lateinit var model_desc: EditText
    private lateinit var fuel_desc: EditText
    private lateinit var colour: EditText
    private lateinit var vin: EditText
    private lateinit var qrCodeImageView: ImageView
    private lateinit var refreshButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrscanner)

        btnGenerate = findViewById(R.id.btnGenerate)
        tvResult = findViewById(R.id.tvResult)
        chassis_no = findViewById(R.id.chassis_no)
        model_desc = findViewById(R.id.model_desc)
        fuel_desc = findViewById(R.id.fuel_desc)
        colour = findViewById(R.id.colour)
        vin = findViewById(R.id.vin)
        qrCodeImageView = findViewById(R.id.qrCodeImageView)
        fetchChassisDataButton = findViewById(R.id.fetchChassisDataButton)
        refreshButton = findViewById(R.id.refreshButton)

        btnGenerate.setOnClickListener {
            generateQRCode()
        }

        refreshButton.setOnClickListener {
            resetFields()
        }

        fetchChassisDataButton.setOnClickListener {
            val chassisNo = chassis_no.text.toString()
            if (chassisNo.isNotEmpty()) {
                fetchChassisData(chassisNo)
            } else {
                Toast.makeText(this, "Please enter a Chassis Number", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetFields() {
        chassis_no.setText("")
        model_desc.setText("")
        vin.setText("")
        colour.setText("")
        fuel_desc.setText("")
        qrCodeImageView.setImageBitmap(null)
        tvResult.text = ""
    }

    private fun fetchChassisData(chassisNo: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
//            .url("http://10.0.2.2:8081/fndcom/stockDetailsByChassis?chassis_no=$chassisNo")
            .url("${ApiFile.APP_URL}/fndcom/stockDetailsByChassis?chassis_no=$chassisNo")

            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()
                jsonData?.let {
                    val jsonObject = JSONObject(it)
                    Log.d("Data", it)
                    val stockItem = jsonObject.getJSONArray("obj").getJSONObject(0)

                    val chassisData = chassis_data(
                        chassis_no = stockItem.getString("CHASSIS_NO"),
                        modelDesc = stockItem.getString("MODEL_DESC"),
                        vin = stockItem.getString("VIN"),
                        colour = stockItem.getString("COLOUR"),
                        fuelDesc = stockItem.getString("FUEL_DESC")
                    )

                    runOnUiThread {
                        populateFields(chassisData)
                        Toast.makeText(
                            this@QrScanner,
                            "Details found Successfully \n for Chassis no. $chassisNo",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@QrScanner,
                        "$chassisNo Chassis number not found",
                        Toast.LENGTH_SHORT
                    ).show()
                    findViewById<EditText>(R.id.chassis_no).apply {
                        setText(chassisNo)
                        isEnabled = true
                    }
                }
            }
        }
    }

    private fun populateFields(chassisData: chassis_data) {
        chassis_no.setText(chassisData.chassis_no)
        model_desc.setText(chassisData.modelDesc)
        vin.setText(chassisData.vin)
        colour.setText(chassisData.colour)
        fuel_desc.setText(chassisData.fuelDesc)
    }

    private fun generateQRCode() {
        val chassis_no = chassis_no.text.toString()
        val model_desc = model_desc.text.toString()
        val fuel_desc = fuel_desc.text.toString()
        val colour = colour.text.toString()
        val vin = vin.text.toString()

        if (chassis_no.isEmpty() || model_desc.isEmpty() || fuel_desc.isEmpty() || colour.isEmpty() || vin.isEmpty()) {
            Toast.makeText(this, "Please enter all values", Toast.LENGTH_SHORT).show()
            return
        }

        val formBody = FormBody.Builder()
            .add("chassisNo", chassis_no)
            .add("modelDesc", model_desc)
            .add("fuelDesc", fuel_desc)
            .add("colour", colour)
            .add("vin", vin)
            .build()

        val request = Request.Builder()
//            .url("http://10.0.2.2:8081/qrcode/generate")
            .url("${ApiFile.APP_URL }}/qrcode/generate")
            .post(formBody)
            .build()

        Log.d("QrScanner", "Request URL: ${request.url}")

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                runOnUiThread {
                    tvResult.text = "Failed to generate QR Code"
                    Toast.makeText(this@QrScanner, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                Log.e("QrScanner", "Failed to generate QR Code", e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    runOnUiThread {
                        tvResult.text = "QR Code generated successfully"
                        fetchQRCodeImage(chassis_no)
                        decodeQRCode(chassis_no)
                    }
                } else {
                    val responseBody = response.body?.string()
                    runOnUiThread {
                        tvResult.text = "Failed to generate QR Code: ${response.message}"
                        Toast.makeText(
                            this@QrScanner,
                            "Server Error: ${response.message}\n${responseBody}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("QrScanner", "Server Response: ${response.message}\n${responseBody}")
                    }
                }
            }
        })
    }

    private fun fetchQRCodeImage(chassisNo: String) {
//        val url = "http://10.0.2.2:8081/qrcode/image/$chassisNo"
        val url ="${ApiFile.APP_URL}/qrcode/image/$chassisNo"
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                runOnUiThread {
                    tvResult.text = "Failed to fetch QR Code image"
                    Toast.makeText(this@QrScanner, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                Log.e("QrScanner", "Failed to fetch QR Code image", e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.bytes()
                    runOnUiThread {
                        if (responseData != null && responseData.isNotEmpty()) {
                            val bitmap =
                                BitmapFactory.decodeByteArray(responseData, 0, responseData.size)
                            qrCodeImageView.setImageBitmap(bitmap)
                        } else {
                            tvResult.text = "Failed to fetch QR Code image: Empty response"
                            Toast.makeText(
                                this@QrScanner,
                                "Server Error: Empty response",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("QrScanner", "Server Response: Empty response")
                        }
                    }
                } else {
                    val responseBody = response.body?.string()
                    runOnUiThread {
                        tvResult.text = "Failed to fetch QR Code image: ${response.message}"
                        Toast.makeText(
                            this@QrScanner,
                            "Server Error: ${response.message}\n${responseBody}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("QrScanner", "Server Response: ${response.message}\n${responseBody}")
                    }
                }
            }
        })
    }


    private fun decodeQRCode(chassisNo: String) {
//        val url = "http://10.0.2.2:8081/qrcode/decode/$chassisNo"
        val url = "${ApiFile.APP_URL}/qrcode/decode/$chassisNo"
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                runOnUiThread {
                    tvResult.text = "Failed to generate QR Code"
                }
                Log.e("QrScanner", "Failed to decode QR Code", e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    runOnUiThread {
                        if (responseData != null) {
                            tvResult.text = "QR Code generated successfully"
                        } else {
                            tvResult.text = "Failed to generate QR Code"
                        }
                    }
                } else {
                    runOnUiThread {
                        tvResult.text = "Failed to generate QR Code: ${response.message}"
                    }
                    Log.e("QrScanner", "Failed to generate QR Code: ${response.message}")
                }
            }
        })
    }

    data class chassis_data(
        val chassis_no: String,
        val modelDesc: String,
        val vin: String,
        val colour: String,
        val fuelDesc: String
    )
}



