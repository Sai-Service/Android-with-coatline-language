
package com.example.apinew

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class StockController : AppCompatActivity() {

    private var ouId: Int = 0
    private var locId: Int = 0
    private lateinit var login_name: String
    private lateinit var attribute1: String
    private lateinit var location: String
    private lateinit var location_name: String
    private lateinit var deptName: String
    private lateinit var CityReport:ImageButton
    private lateinit var SummaryReport:ImageButton
    private lateinit var SummaryReportLocationWise:ImageButton
    private lateinit var UninvoiceReport:ImageButton
    private lateinit var VehicleReady: ImageButton
    private lateinit var indReport:ImageButton
    private lateinit var invoiceReport:ImageButton
    private lateinit var homePage:ImageButton
    private lateinit var stktrfIntr:ImageButton
    private lateinit var allotedNtInv:ImageButton



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_controller)

        ouId = intent.getIntExtra("ouId", 0)
        locId = intent.getIntExtra("locId", 0)
        attribute1 = intent.getStringExtra("attribute1") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""
        location_name = intent.getStringExtra("location_name") ?: ""
        location = intent.getStringExtra("location") ?: ""
        deptName = intent.getStringExtra("deptName") ?: ""

        CityReport=findViewById(R.id.CityReport)
        SummaryReport=findViewById(R.id.SummaryReport)
        UninvoiceReport=findViewById(R.id.UninvoiceReport)
        VehicleReady=findViewById(R.id.VehicleReady)
        indReport=findViewById(R.id.indReport)
        invoiceReport=findViewById(R.id.invoiceReport)
        homePage=findViewById(R.id.homePage)
        stktrfIntr=findViewById(R.id.stktrfIntr)
        SummaryReportLocationWise=findViewById(R.id.SummaryReportLocationWise)
        allotedNtInv=findViewById(R.id.allotedNtInv)


        CityReport.setOnClickListener{
            goToCityReport()
        }

        indReport.setOnClickListener{
            IndActivity()
        }

        SummaryReport.setOnClickListener{
            CountWiseActivity()
        }

        UninvoiceReport.setOnClickListener{
            UninvoiceActivity()
        }

        VehicleReady.setOnClickListener{
            PhysicallyInActivity()
        }

        SummaryReportLocationWise.setOnClickListener {
            summaryReportLocationWise()
        }

        invoiceReport.setOnClickListener{
            IntransitVehicle()
        }
        homePage.setOnClickListener {
            backToHome()
        }
        stktrfIntr.setOnClickListener {
            StkTrfIntr()
        }
        allotedNtInv.setOnClickListener {
            allotedNotInvoice()
        }

    }

    private fun goToCityReport() {
        val intent = Intent(this@StockController, DashboardActivity::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name", location_name)
        intent.putExtra("deptName", deptName)
        startActivity(intent)
    }

    private fun CountWiseActivity() {
        val intent = Intent(this@StockController, CountWiseActivity::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name", location_name)
        intent.putExtra("deptName", deptName)
        startActivity(intent)
    }

    private fun UninvoiceActivity() {
        val intent = Intent(this@StockController, UninvoiceActivity::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name", location_name)
        intent.putExtra("deptName", deptName)
        startActivity(intent)
    }

    private fun PhysicallyInActivity() {
        val intent = Intent(this@StockController, PhysicallyInActivity::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name", location_name)
        intent.putExtra("deptName", deptName)
        startActivity(intent)
    }

    private fun IndActivity() {
        val intent = Intent(this@StockController, IndActivity::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name", location_name)
        intent.putExtra("deptName", deptName)
        startActivity(intent)
    }

    private fun IntransitVehicle() {
        val intent = Intent(this@StockController, IntransitVehicle::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name", location_name)
        intent.putExtra("deptName", deptName)
        startActivity(intent)
    }

    private fun StkTrfIntr() {
        val intent = Intent(this@StockController, StkTrfIntransit::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name", location_name)
        intent.putExtra("deptName", deptName)
        startActivity(intent)
    }

    private fun summaryReportLocationWise() {
        val intent = Intent(this@StockController, SummaryLocationWise::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name", location_name)
        intent.putExtra("deptName", deptName)
        startActivity(intent)
    }


    private fun allotedNotInvoice() {
        val intent = Intent(this@StockController, AllotedNotInvoice::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name", location_name)
        intent.putExtra("deptName", deptName)
        startActivity(intent)
    }

    private fun backToHome() {
        finish()
    }

}