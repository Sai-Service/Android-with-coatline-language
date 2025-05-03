package com.example.apinew

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.IOException

class VehicleEntry : AppCompatActivity() {

    private lateinit var tableLayout: TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_entry)

        tableLayout = findViewById(R.id.tableLayout)

        val vin = intent.getStringExtra("vin") ?: ""
        if (vin.isNotEmpty()) {
            fetchDataFromApi(vin)
        } else {
            Toast.makeText(this, "VIN not provided", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun fetchDataFromApi(vin: String) {
        val url = "${ApiFile.APP_URL}/qrcode/detailsByVin?vin=$vin"
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                    val jsonObject = JSONObject(responseBody)
                    val obj = jsonObject.getJSONObject("obj")

                    runOnUiThread {
                        displayDataInTable(obj)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@VehicleEntry, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@VehicleEntry, "Failed to fetch data due to exception: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun displayDataInTable(obj: JSONObject) {
        val layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )

        val keys = obj.keys()
        val tableRowHeader = TableRow(this)
        tableRowHeader.layoutParams = layoutParams

        while (keys.hasNext()) {
            val key = keys.next()
            val value = obj.getString(key)

            val textView = TextView(this)
            textView.text = "$key: $value"
            textView.setPadding(10, 10, 10, 10)

            tableRowHeader.addView(textView)
        }

        val updateButton = Button(this)
        updateButton.text = "Update"
        updateButton.setOnClickListener {
            updateVehicleDetails(obj.getString("vin"))
        }

        tableRowHeader.addView(updateButton)
        tableLayout.addView(tableRowHeader)
    }

    private fun updateVehicleDetails(vin: String) {
        val url = "${ApiFile.APP_URL}/qrcode/updateVehicle/$vin"

        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .put(RequestBody.create(null, ByteArray(0)))
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()

                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@VehicleEntry, "Vehicle details updated successfully!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@VehicleEntry, "Failed to update vehicle details: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@VehicleEntry, "Failed to update vehicle details due to exception: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
