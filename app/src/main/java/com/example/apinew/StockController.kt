
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
    private lateinit var trueValueStockReport:ImageButton
    private lateinit var trueValueStockTrnasferReport:ImageButton
    private lateinit var trueValueReportModelWise:ImageButton
    private lateinit var trueValueReportLocationWise:ImageButton
    private lateinit var trueValueReportDelivered:ImageButton
    private lateinit var serviveStockReport:ImageButton
    private lateinit var servivedDeliveredVehicles:ImageButton
    private lateinit var serviceLocationWiseReport:ImageButton
    private lateinit var serviceStkIntransitReport:ImageButton
    private lateinit var serviceStockTransferReport:ImageButton
    private lateinit var serviceTestDriveReport:ImageButton
    private lateinit var row1:View
    private lateinit var row2:View
    private lateinit var row3:View
    private lateinit var row4:View
    private lateinit var row5:View
    private lateinit var row6:View
    private lateinit var row7:View
    private lateinit var row8:View
    private lateinit var row9:View
    private lateinit var row10:View
    private lateinit var row11:View
    private lateinit var row12:View
    private lateinit var row13:View
    private lateinit var row14:View
    private lateinit var row15:View
    private lateinit var row16:View
    private lateinit var row17:View
    private lateinit var row18:View
    private lateinit var row19:View
    private lateinit var row20:View





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
        trueValueStockReport=findViewById(R.id.trueValueStockReport)
        trueValueStockTrnasferReport=findViewById(R.id.trueValueStockTrnasferReport)
        trueValueReportModelWise=findViewById(R.id.trueValueReportModelWise)
        trueValueReportLocationWise=findViewById(R.id.trueValueReportLocationWise)
        trueValueReportDelivered=findViewById(R.id.trueValueReportDelivered)
        serviveStockReport=findViewById(R.id.serviveStockReport)
        servivedDeliveredVehicles=findViewById(R.id.servivedDeliveredVehicles)
        serviceLocationWiseReport=findViewById(R.id.serviceLocationWiseReport)
        serviceStkIntransitReport=findViewById(R.id.serviceStkIntransitReport)
        serviceStockTransferReport=findViewById(R.id.serviceStockTransferReport)
        serviceTestDriveReport=findViewById(R.id.serviceTestDriveReport)


        row1=findViewById(R.id.row1)
        row2=findViewById(R.id.row2)
        row3=findViewById(R.id.row3)
        row4=findViewById(R.id.row4)
        row5=findViewById(R.id.row5)
        row6=findViewById(R.id.row6)
        row7=findViewById(R.id.row7)
        row8=findViewById(R.id.row8)
        row9=findViewById(R.id.row9)
        row10=findViewById(R.id.row10)
        row11=findViewById(R.id.row11)
        row12=findViewById(R.id.row12)
        row13=findViewById(R.id.row13)
        row14=findViewById(R.id.row14)
        row15=findViewById(R.id.row15)
        row16=findViewById(R.id.row16)
        row17=findViewById(R.id.row17)
        row18=findViewById(R.id.row18)
        row19=findViewById(R.id.row19)
        row20=findViewById(R.id.row20)



        when (deptName) {
            "TRUEVALUE" -> {
                row1.visibility=View.GONE
                row2.visibility=View.GONE
                row3.visibility=View.GONE
                row4.visibility=View.GONE
                row5.visibility=View.GONE
                row6.visibility=View.GONE
                row7.visibility=View.GONE
                row8.visibility=View.GONE
                row9.visibility=View.GONE
                row10.visibility=View.VISIBLE
                row11.visibility=View.VISIBLE
                row12.visibility=View.GONE
                row13.visibility=View.VISIBLE
                row14.visibility=View.VISIBLE
                row15.visibility=View.GONE
                row16.visibility=View.GONE
                row17.visibility=View.GONE
                row18.visibility=View.GONE
                row19.visibility=View.GONE
                row20.visibility=View.GONE
            }

            "TVSUPERADMIN" -> {
                row1.visibility=View.GONE
                row2.visibility=View.GONE
                row3.visibility=View.GONE
                row4.visibility=View.GONE
                row5.visibility=View.GONE
                row6.visibility=View.GONE
                row7.visibility=View.GONE
                row8.visibility=View.GONE
                row9.visibility=View.GONE
                row10.visibility=View.VISIBLE
                row11.visibility=View.VISIBLE
                row12.visibility=View.GONE
                row13.visibility=View.VISIBLE
                row14.visibility=View.VISIBLE
                row15.visibility=View.GONE
                row16.visibility=View.GONE
                row17.visibility=View.GONE
                row18.visibility=View.GONE
                row19.visibility=View.GONE
                row20.visibility=View.GONE

            }

            "TVACCOUNTS"->{
                row1.visibility=View.GONE
                row2.visibility=View.GONE
                row3.visibility=View.GONE
                row4.visibility=View.GONE
                row5.visibility=View.GONE
                row6.visibility=View.GONE
                row7.visibility=View.GONE
                row8.visibility=View.GONE
                row9.visibility=View.GONE
                row10.visibility=View.VISIBLE
                row11.visibility=View.VISIBLE
                row12.visibility=View.GONE
                row13.visibility=View.VISIBLE
                row14.visibility=View.VISIBLE
                row15.visibility=View.GONE
                row16.visibility=View.GONE
                row17.visibility=View.GONE
                row18.visibility=View.GONE
                row19.visibility=View.GONE
                row20.visibility=View.GONE

            }

            "SERACCOUNTS"->{
                row1.visibility=View.GONE
                row2.visibility=View.GONE
                row3.visibility=View.GONE
                row4.visibility=View.GONE
                row5.visibility=View.GONE
                row6.visibility=View.GONE
                row7.visibility=View.GONE
                row8.visibility=View.GONE
                row9.visibility=View.GONE
                row10.visibility=View.GONE
                row11.visibility=View.GONE
                row12.visibility=View.GONE
                row13.visibility=View.GONE
                row14.visibility=View.GONE
                row15.visibility=View.VISIBLE
                row16.visibility=View.VISIBLE
                row17.visibility=View.VISIBLE
                row18.visibility=View.VISIBLE
                row19.visibility=View.VISIBLE
                row20.visibility=View.VISIBLE
            }

            "SERVICE"->{
                row1.visibility=View.GONE
                row2.visibility=View.GONE
                row3.visibility=View.GONE
                row4.visibility=View.GONE
                row5.visibility=View.GONE
                row6.visibility=View.GONE
                row7.visibility=View.GONE
                row8.visibility=View.GONE
                row9.visibility=View.GONE
                row10.visibility=View.GONE
                row11.visibility=View.GONE
                row12.visibility=View.GONE
                row13.visibility=View.GONE
                row14.visibility=View.GONE
                row15.visibility=View.VISIBLE
                row16.visibility=View.VISIBLE
                row17.visibility=View.VISIBLE
                row18.visibility=View.VISIBLE
                row19.visibility=View.VISIBLE
                row20.visibility=View.VISIBLE
            }

            "DP"->{
                row1.visibility=View.GONE
                row2.visibility=View.GONE
                row3.visibility=View.GONE
                row4.visibility=View.GONE
                row5.visibility=View.GONE
                row6.visibility=View.GONE
                row7.visibility=View.GONE
                row8.visibility=View.GONE
                row9.visibility=View.GONE
                row10.visibility=View.GONE
                row11.visibility=View.GONE
                row12.visibility=View.GONE
                row13.visibility=View.GONE
                row14.visibility=View.GONE
                row15.visibility=View.VISIBLE
                row16.visibility=View.VISIBLE
                row17.visibility=View.VISIBLE
                row18.visibility=View.VISIBLE
                row19.visibility=View.VISIBLE
                row20.visibility=View.VISIBLE
            }


            else->{
                row1.visibility=View.VISIBLE
                row2.visibility=View.VISIBLE
                row3.visibility=View.VISIBLE
                row4.visibility=View.VISIBLE
                row5.visibility=View.VISIBLE
                row6.visibility=View.VISIBLE
                row7.visibility=View.VISIBLE
                row8.visibility=View.VISIBLE
                row9.visibility=View.VISIBLE
                row10.visibility=View.GONE
                row11.visibility=View.GONE
                row12.visibility=View.GONE
                row13.visibility=View.GONE
                row14.visibility=View.GONE
                row15.visibility=View.GONE
                row16.visibility=View.GONE
                row17.visibility=View.GONE
                row18.visibility=View.GONE
                row19.visibility=View.GONE
                row20.visibility=View.GONE
            }
        }

        serviveStockReport.setOnClickListener {
            serviceStockReport()
        }

        serviceLocationWiseReport.setOnClickListener {
            serviceLocationWiseReport()
        }

        serviceStkIntransitReport.setOnClickListener {
            serviceStkIntrReport()
        }

        servivedDeliveredVehicles.setOnClickListener {
            serviceDeliveredVehicleReport()
        }

        serviceStockTransferReport.setOnClickListener {
            serviceStockTransferReport()
        }

        serviceTestDriveReport.setOnClickListener {
            serviceTestDriveReport()
        }

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
        trueValueStockReport.setOnClickListener {
            TrueValueStockReport()
        }
        trueValueStockTrnasferReport.setOnClickListener {
            trueValueStockTrnasferReport()
        }
        trueValueReportModelWise.setOnClickListener {
            trueValueModelWiseReport()
        }
        trueValueReportLocationWise.setOnClickListener {
            trueValueSummaryWiseReport()
        }
        trueValueReportDelivered.setOnClickListener {
            trueValueVehicleDelivered()
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

    private fun TrueValueStockReport() {
        val intent = Intent(this@StockController, TrueValueStockReport::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name", location_name)
        intent.putExtra("deptName", deptName)
        startActivity(intent)
    }

    private fun trueValueStockTrnasferReport() {
        val intent = Intent(this@StockController, TrueValueStockTransferReport::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name", location_name)
        intent.putExtra("deptName", deptName)
        startActivity(intent)
    }

    private fun trueValueModelWiseReport() {
        val intent = Intent(this@StockController, TrueValueReportModelWise::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name", location_name)
        intent.putExtra("deptName", deptName)
        startActivity(intent)
    }


    private fun trueValueSummaryWiseReport() {
        val intent = Intent(this@StockController, TrueValueReportLocationWise::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name", location_name)
        intent.putExtra("deptName", deptName)
        startActivity(intent)
    }

    private fun trueValueVehicleDelivered() {
        val intent = Intent(this@StockController, TrueValueDeliveredReports::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name", location_name)
        intent.putExtra("deptName", deptName)
        startActivity(intent)
    }

    private fun serviceStockReport() {
        val intent = Intent(this@StockController, WorkshopStockReport::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name", location_name)
        intent.putExtra("deptName", deptName)
        startActivity(intent)
    }

    private fun serviceStkIntrReport() {
        val intent = Intent(this@StockController, WorkshopStkIntransitReport::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name", location_name)
        intent.putExtra("deptName", deptName)
        startActivity(intent)
    }

    private fun serviceDeliveredVehicleReport() {
        val intent = Intent(this@StockController, WorkshopDeliveredVehicleReports::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name", location_name)
        intent.putExtra("deptName", deptName)
        startActivity(intent)
    }


    private fun serviceLocationWiseReport() {
        val intent = Intent(this@StockController, WorkshopLocationWiseReport::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name", location_name)
        intent.putExtra("deptName", deptName)
        startActivity(intent)
    }

    private fun serviceStockTransferReport() {
        val intent = Intent(this@StockController, WorkshopStockTransferReport::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name", location_name)
        intent.putExtra("deptName", deptName)
        startActivity(intent)
    }

    private fun serviceTestDriveReport() {
        val intent = Intent(this@StockController, WorkshopTestDriveReport::class.java)
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