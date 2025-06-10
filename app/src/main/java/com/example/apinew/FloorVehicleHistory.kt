package com.example.apinew

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class FloorVehicleHistory : AppCompatActivity() {

    private var ouId: Int = 0
    private var locId: Int = 0
    private lateinit var login_name: String
    private lateinit var attribute1: String
    private lateinit var location: String
    private lateinit var tableLayout: TableLayout
    private lateinit var location_name: String
    private lateinit var intransitBtn: Button
    private lateinit var stktrfIntrBtn: TextView
    private lateinit var textView13: TextView
    private lateinit var textViewStatus: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_floor_vehicle_history)
        location_name = intent.getStringExtra("location_name") ?: ""
        ouId = intent.getIntExtra("ouId", 0)
        attribute1 = intent.getStringExtra("attribute1") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""
        location = intent.getStringExtra("location") ?: ""
        location_name = intent.getStringExtra("location_name") ?: ""
        tableLayout = findViewById(R.id.transferTable)
        intransitBtn = findViewById(R.id.intransitBtn)
        stktrfIntrBtn = findViewById(R.id.stktrfIntrBtn)
        textView13 = findViewById(R.id.textView13)
        textViewStatus = findViewById(R.id.textViewStatus)

        stktrfIntrBtn.setOnClickListener {
            fetchVehicleHistory()
        }

        val blockSpecialCharFilter = InputFilter { source, _, _, _, _, _ ->
            val pattern = Regex("^[a-zA-Z0-9]+$")
            if (source.isEmpty() || source.matches(pattern)) {
                source
            } else {
                ""
            }
        }

        textViewStatus.filters=arrayOf(blockSpecialCharFilter, InputFilter.AllCaps())

        val alphaNumericFilter = InputFilter { source, _, _, _, _, _ ->
            if (source.all { it.isLetterOrDigit() }) null else ""
        }

        val lengthFilter = InputFilter.LengthFilter(10)


        val textViewStatus = findViewById<EditText>(R.id.textViewStatus)
        textViewStatus.filters = arrayOf(alphaNumericFilter,lengthFilter)

    }

//    private fun setupTableHeader(headers: Array<String>) {
//        val tableHeader = TableRow(this)
//        tableHeader.layoutParams = TableRow.LayoutParams(
//            TableRow.LayoutParams.MATCH_PARENT,
//            TableRow.LayoutParams.WRAP_CONTENT
//        )
//
//        headers.forEach { header ->
//            val textView = TextView(this)
//            textView.text = header
//            textView.setPadding(16, 16, 16, 16)
//            textView.setTypeface(null, Typeface.BOLD)
//            textView.setBackgroundColor(Color.parseColor("#83BEF8"))
//            tableHeader.addView(textView)
//        }
//
//        tableLayout.addView(tableHeader)
//    }


    private fun setupTableHeader(headers: Array<String>) {
        val tableHeader = TableRow(this)
        tableHeader.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )

        headers.forEach { header ->
            val textView = TextView(this)
            textView.text = header
            textView.setPadding(16, 16, 16, 16)
            textView.setTypeface(null, Typeface.BOLD)
            textView.setBackgroundColor(Color.parseColor("#FFD700"))
            textView.setBackgroundResource(R.drawable.table_cell_header_border_washing)
            textView.setTextColor(Color.BLUE)
            tableHeader.addView(textView)
        }

        tableLayout.addView(tableHeader)
    }


    private fun populateTable(vehicleList: List<RegPending>) {
        tableLayout.removeAllViews()
        if (vehicleList.isEmpty()) {
            tableLayout.visibility = View.GONE
        } else {
            tableLayout.visibility = View.VISIBLE
            setupTableHeader(arrayOf("SR NO","REG NO","VIN","CHASSIS NO","ENGINE NO","DESCRIPTION","TECHNICIAN",
                "LOCATION", "LABOURS COUNT","REMARKS","IN TIME","OUT TIME","STATUS","ADVISOR"))

            vehicleList.forEachIndexed { index, vehicle ->
                addDataRow(index + 1, vehicle)
            }
        }
    }

    private fun addDataRow(id: Int, vehicle: RegPending) {
        val tableRow = TableRow(this)
        tableRow.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )

        val values = arrayOf(
            id.toString(),
          vehicle.regNo,
            vehicle.vin,
            vehicle.chassisNo,
            vehicle.engineNo,
            vehicle.remarks,
            vehicle.technicianName,
            vehicle.location,
            vehicle.attribute1,
            vehicle.attribute2,
            vehicle.inTime,
            vehicle.outTime,
            vehicle.status,
            vehicle.createdBy
        )
        values.forEach { value ->
            val textView = TextView(this)
            textView.text = value
            textView.setPadding(16, 16, 16, 16)
            textView.setBackgroundResource(R.drawable.table_cell_border)
            tableRow.addView(textView)
        }
        tableLayout.addView(tableRow)
    }

    private fun formatDateTime(dateTime: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
            val outputDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val outputTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(dateTime)
            val formattedDate = date?.let { outputDateFormat.format(it) }
            val formattedTime = date?.let { outputTimeFormat.format(it) }
            "$formattedDate "+ "$formattedTime"
        } catch (e: Exception) {
            dateTime
        }
    }


    private fun fetchVehicleHistory() {
        val client = OkHttpClient()
        val vehNo=textViewStatus.text.toString()
        if(vehNo.isEmpty()){
            Toast.makeText(this@FloorVehicleHistory, "Please enter vehicle number...", Toast.LENGTH_SHORT).show()
            return
        }
        val url = "${ApiFile.APP_URL}/VehicleTrack/VehicleHistory?regNo=$vehNo"

        val request = Request.Builder()
            .url(url)
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    val jsonObject = JSONObject(responseBody)
                    val jsonArray = jsonObject.getJSONArray("obj")
                    val vehicleList = mutableListOf<RegPending>()
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        val vehicle = RegPending(
                            regNo = item.optString("regNo"),
                            chassisNo = item.optString("chassisNo"),
                            engineNo = item.optString("engineNo"),
                            vin = item.optString("vin"),
                            technicianName = item.optString("technicianName"),
                            location = item.optString("location"),
                            createdBy = item.optString("createdBy"),
                            attribute1 = item.optString("attribute1"),
                            attribute2 = item.optString("attribute2"),
                            outTime = formatDateTime(item.optString("outTime")),
                            inTime = formatDateTime(item.optString("inTime")),
                            status = item.optString("status"),
                            remarks = item.optString("remarks")
                        )
                        vehicleList.add(vehicle)
                    }

                    runOnUiThread {
                        if(vehicleList.size<=0){
                            Toast.makeText(this@FloorVehicleHistory, "This vehicle has not any transfer history", Toast.LENGTH_LONG).show()
                            return@runOnUiThread
                        }
                        Toast.makeText(this@FloorVehicleHistory, "Details found successfully for Vehicle No.$vehNo", Toast.LENGTH_LONG).show()
                        populateTable(vehicleList)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@FloorVehicleHistory, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@FloorVehicleHistory, "Failed to fetch data due to exception: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    data class RegPending(
       val regNo:String,
       val chassisNo:String,
        val engineNo:String,
        val vin:String,
        val technicianName:String,
        val location:String,
        val createdBy:String,
        val attribute1:String,
        val attribute2:String,
        val outTime:String,
        val inTime:String,
        val status:String,
        val remarks:String
    )
}





