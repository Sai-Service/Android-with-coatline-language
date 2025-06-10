package com.example.apinew

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class WorkshopAvailableDemoVehicle : AppCompatActivity() {
    private lateinit var login_name: String
    private lateinit var deptName: String
    private var ouId: Int = 0
    private var locId: Int = 0
    private lateinit var location_name: String
    private lateinit var attribute1:String
    private lateinit var location:String
    private lateinit var username:TextView
    private lateinit var locationName:TextView
    private lateinit var tableLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workshop_available_demo_vehicle)


        locId = intent.getIntExtra("locId", 0)
        ouId = intent.getIntExtra("ouId", 0)
        deptName = intent.getStringExtra("deptName") ?: ""
        location_name = intent.getStringExtra("location_name") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""
        location=intent.getStringExtra("location") ?:""
        attribute1=intent.getStringExtra("attribute1") ?:""
        username=findViewById(R.id.username)

        locationName=findViewById(R.id.locIdTxt)

        username.text=login_name
        locationName.text=location_name

        tableLayout = findViewById(R.id.tableLayout)



        findViewById<TextView>(R.id.refreshButton).setOnClickListener {
            fetchDemoVehicleStatus()
        }


    }

    private fun fetchDemoVehicleStatus() {
        thread {
            try {
                val url = URL("${ApiFile.APP_URL}/demoVehicleTrans/demoVehStatusList?ouId=$ouId&location=$locationName")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                val response = connection.inputStream.bufferedReader().readText()
                val json = JSONObject(response)
                val dataArray = json.getJSONArray("obj")

                runOnUiThread {
                    tableLayout.removeAllViews()
                    addTableHeader()
                    addTableData(dataArray)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun addTableHeader() {
        val headerRow = createRow(listOf("SR NO.","VEHICLE NO","STATUS"), isHeader = true)
        tableLayout.addView(headerRow)
    }

    private fun addTableData(dataArray: JSONArray) {
        for (i in 0 until dataArray.length()) {
            val obj = dataArray.getJSONObject(i)
            val srNo = (i + 1).toString()
            val instanceNo = obj.getString("INSTANCE_NUMBER")
            val status = obj.getString("STATUS")


            val row = createRow(listOf(srNo, instanceNo,status), isHeader = false)
            row.setBackgroundColor(0xFFF0F0F0.toInt())
            tableLayout.addView(row)
        }
    }


    private fun createRow(values: List<String>, isHeader: Boolean): LinearLayout {
        val row = LinearLayout(this)
        row.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        row.orientation = LinearLayout.HORIZONTAL
        row.setPadding(0, 0, 0, 0)
        row.setBackgroundColor(if (isHeader) 0xFFBBDEFB.toInt() else 0xFFFFFFFF.toInt())
        row.setBackgroundResource(R.drawable.table_row_border)

        for ((index, text) in values.withIndex()){
            val textView = TextView(this)
            textView.text = text
            textView.setPadding(16, 12, 16, 12)
            textView.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            textView.gravity = Gravity.CENTER
            textView.setBackgroundResource(R.drawable.cell_border)
            textView.setTextColor(if (isHeader) 0xFFFFFFFF.toInt() else 0xFF444444.toInt())
            textView.setTypeface(null, if (isHeader) Typeface.BOLD else Typeface.NORMAL)
            row.addView(textView)

            val weight = when (index) {
                0 -> 0.5f // SR NO
                else -> 1f // STATUS and INSTANCE NO
            }
            textView.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                weight
            )
        }

        return row
    }



}



