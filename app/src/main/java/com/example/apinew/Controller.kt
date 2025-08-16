package com.example.apinew

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Controller : AppCompatActivity() {

    private var ouId: Int = 0
    private var locId: Int = 0
    private lateinit var locIdTxt :TextView
    private lateinit var username:TextView
    private lateinit var deptIntent:TextView
    private lateinit var login_name: String
    private lateinit var location_name:String
    private lateinit var attribute1: String
    private lateinit var location: String
    private lateinit var logoutButton:ImageView
    private lateinit var deptName:String
    private lateinit var emailId:String
    private lateinit var reports:View
    private lateinit var security:View
    private lateinit var vehTrack:View
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
    private lateinit var row21:View
    private lateinit var row22:View
    private lateinit var row23:View
    private lateinit var row24:View

    private lateinit var about:ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controller)


        locIdTxt = findViewById(R.id.locIdTxt)
        username = findViewById(R.id.username)
        reports = findViewById(R.id.reports)
        security = findViewById(R.id.security)
        vehTrack = findViewById(R.id.vehTrack)
        deptIntent=findViewById(R.id.deptIntent)
        about=findViewById(R.id.about)


        logoutButton = findViewById(R.id.logoutButton)

        ouId = intent.getIntExtra("ouId", 0)
        locId = intent.getIntExtra("locId", 0)

        attribute1 = intent.getStringExtra("attribute1") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""
        location_name = intent.getStringExtra("location_name") ?: ""
        location = intent.getStringExtra("location") ?: ""
        deptName = intent.getStringExtra("deptName") ?: ""
        emailId = intent.getStringExtra("emailId") ?: ""
        row1 = findViewById(R.id.row1)
        row2 = findViewById(R.id.row2)
        row3 = findViewById(R.id.row3)
        row4 = findViewById(R.id.row4)
        row5 = findViewById(R.id.row5)
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
        row21=findViewById(R.id.row21)
        row22=findViewById(R.id.row22)
        row23=findViewById(R.id.row23)
        row24=findViewById(R.id.row24)

        reports = findViewById(R.id.reports)

        locIdTxt.text = "$location_name"
        username.text = "$login_name"
        deptIntent.text=deptName



        logoutButton.setOnClickListener {
            logout()
        }

        about.setOnClickListener {
            aboutActivity()
        }

        when (deptName) {
            "ACCOUNTS" -> {
                row1.visibility = View.VISIBLE
                row2.visibility = View.VISIBLE
                row3.visibility = View.GONE
                row4.visibility = View.GONE
                row5.visibility = View.GONE
                row6.visibility = View.GONE
                row7.visibility = View.GONE
                row8.visibility = View.GONE
                row9.visibility=View.GONE
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
                row21.visibility=View.GONE
                row22.visibility=View.GONE
                row23.visibility=View.GONE
                row24.visibility=View.GONE
            }

            "SECURITY" -> {
                row1.visibility = View.GONE
                row2.visibility = View.GONE
                row3.visibility = View.GONE
                row4.visibility = View.GONE
                row5.visibility = View.VISIBLE
                row6.visibility = View.GONE
                row7.visibility = View.GONE
                row8.visibility = View.GONE
                row9.visibility=View.GONE
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
                row21.visibility=View.GONE
                row22.visibility=View.GONE
                row23.visibility=View.GONE
                row24.visibility=View.GONE

            }

            "SALES" -> {
                row1.visibility = View.VISIBLE
                row2.visibility = View.GONE
                row3.visibility = View.VISIBLE
                row4.visibility = View.VISIBLE
                row5.visibility = View.GONE
                row6.visibility = View.GONE
                row7.visibility = View.GONE
                row8.visibility = View.GONE
                row9.visibility=View.GONE
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
                row21.visibility=View.GONE
                row22.visibility=View.GONE
                row23.visibility=View.GONE
                row24.visibility=View.GONE
            }

            "SUPERADMIN" -> {
                row1.visibility = View.VISIBLE
                row2.visibility = View.GONE
                row3.visibility = View.GONE
                row4.visibility = View.VISIBLE
                row5.visibility = View.GONE
                row6.visibility = View.GONE
                row7.visibility = View.GONE
                row8.visibility = View.GONE
                row9.visibility=View.GONE
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
                row21.visibility=View.GONE
                row22.visibility=View.GONE
                row23.visibility=View.GONE
                row24.visibility=View.GONE
            }

            "TRUEVALUE" -> {
                row1.visibility = View.VISIBLE
                row2.visibility = View.GONE
                row3.visibility = View.GONE
                row4.visibility = View.GONE
                row5.visibility = View.GONE
                row6.visibility = View.VISIBLE
                row7.visibility = View.GONE
                row8.visibility = View.VISIBLE
                row9.visibility=View.INVISIBLE
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
                row21.visibility=View.GONE
                row22.visibility=View.GONE
                row23.visibility=View.GONE
                row24.visibility=View.GONE
            }

            "TVSUPERADMIN" -> {
                row1.visibility = View.VISIBLE
                row2.visibility = View.GONE
                row3.visibility = View.GONE
                row4.visibility = View.GONE
                row5.visibility = View.GONE
                row6.visibility = View.VISIBLE
                row7.visibility = View.GONE
                row8.visibility = View.VISIBLE
                row9.visibility=View.INVISIBLE
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
                row21.visibility=View.GONE
                row22.visibility=View.GONE
                row23.visibility=View.GONE
                row24.visibility=View.GONE
            }

            "TVACCOUNTS" -> {
                row1.visibility = View.VISIBLE
                row2.visibility = View.GONE
                row3.visibility = View.GONE
                row4.visibility = View.GONE
                row5.visibility = View.GONE
                row6.visibility = View.GONE
                row7.visibility = View.GONE
                row8.visibility = View.GONE
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
                row21.visibility=View.GONE
                row22.visibility=View.GONE
                row23.visibility=View.GONE
                row24.visibility=View.GONE
            }

            "SERVICE" -> {
                row1.visibility = View.VISIBLE
                row2.visibility = View.GONE
                row3.visibility = View.GONE
                row4.visibility = View.GONE
                row5.visibility = View.GONE
                row6.visibility = View.GONE
                row7.visibility = View.GONE
                row8.visibility = View.GONE
                row9.visibility=View.GONE
                row10.visibility=View.GONE
                row11.visibility=View.GONE
                row12.visibility=View.VISIBLE
                row13.visibility=View.GONE
                row14.visibility=View.VISIBLE
                row15.visibility=View.VISIBLE
                row16.visibility=View.VISIBLE
                row17.visibility=View.GONE
                row18.visibility=View.GONE
                row19.visibility=View.GONE
                row20.visibility=View.GONE
                row21.visibility=View.GONE
                row22.visibility=View.GONE
                row23.visibility=View.GONE
                row24.visibility=View.GONE
            }

            "DP" -> {
                row1.visibility = View.VISIBLE
                row2.visibility = View.GONE
                row3.visibility = View.GONE
                row4.visibility = View.GONE
                row5.visibility = View.GONE
                row6.visibility = View.GONE
                row7.visibility = View.GONE
                row8.visibility = View.GONE
                row9.visibility=View.GONE
                row10.visibility=View.GONE
                row11.visibility=View.GONE
                row12.visibility=View.VISIBLE
                row13.visibility=View.GONE
                row14.visibility=View.VISIBLE
                row15.visibility=View.VISIBLE
                row16.visibility=View.VISIBLE
                row17.visibility=View.GONE
                row18.visibility=View.GONE
                row19.visibility=View.GONE
                row20.visibility=View.GONE
                row21.visibility=View.GONE
                row22.visibility=View.GONE
                row23.visibility=View.GONE
                row24.visibility=View.GONE
            }

            "SERACCOUNTS" -> {
                row1.visibility = View.VISIBLE
                row2.visibility = View.GONE
                row3.visibility = View.GONE
                row4.visibility = View.GONE
                row5.visibility = View.GONE
                row6.visibility = View.GONE
                row7.visibility = View.GONE
                row8.visibility = View.GONE
                row9.visibility=View.GONE
                row10.visibility=View.VISIBLE
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
                row21.visibility=View.GONE
                row22.visibility=View.GONE
                row23.visibility=View.GONE
                row24.visibility=View.GONE
            }

            "SALES-SHOWROOM" -> {
                row1.visibility = View.VISIBLE
                row2.visibility = View.GONE
                row3.visibility = View.GONE
                row4.visibility = View.VISIBLE
                row5.visibility = View.GONE
                row6.visibility = View.GONE
                row7.visibility = View.GONE
                row8.visibility = View.GONE
                row9.visibility=View.GONE
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
                row21.visibility=View.GONE
                row22.visibility=View.GONE
                row23.visibility=View.GONE
                row24.visibility=View.GONE
            }

            "ADMINISTRATOR" -> {
                row1.visibility = View.GONE
                row2.visibility = View.GONE
                row3.visibility = View.GONE
                row4.visibility = View.GONE
                row5.visibility = View.GONE
                row6.visibility = View.GONE
                row7.visibility = View.GONE
                row8.visibility = View.GONE
                row9.visibility=View.GONE
                row10.visibility=View.GONE
                row11.visibility=View.GONE
                row12.visibility=View.GONE
                row13.visibility=View.VISIBLE
                row14.visibility=View.GONE
                row15.visibility=View.GONE
                row16.visibility=View.GONE
                row17.visibility=View.GONE
                row18.visibility=View.GONE
                row19.visibility=View.GONE
                row20.visibility=View.GONE
                row21.visibility=View.GONE
                row22.visibility=View.GONE
                row23.visibility=View.GONE
                row24.visibility=View.GONE
            }
            "SECURITYTD" -> {
                row1.visibility = View.VISIBLE
                row2.visibility = View.GONE
                row3.visibility = View.GONE
                row4.visibility = View.GONE
                row5.visibility = View.GONE
                row6.visibility = View.GONE
                row7.visibility = View.GONE
                row8.visibility = View.GONE
                row9.visibility=View.GONE
                row10.visibility=View.GONE
                row11.visibility=View.GONE
                row12.visibility=View.GONE
                row13.visibility=View.GONE
                row14.visibility=View.GONE
                row15.visibility=View.GONE
                row16.visibility=View.GONE
                row17.visibility=View.VISIBLE
                row18.visibility=View.GONE
                row19.visibility=View.GONE
                row20.visibility=View.GONE
                row21.visibility=View.GONE
                row22.visibility=View.GONE
                row23.visibility=View.VISIBLE
                row24.visibility=View.GONE
            }

            "SECURITYPARK" -> {
                row1.visibility = View.VISIBLE
                row2.visibility = View.GONE
                row3.visibility = View.GONE
                row4.visibility = View.GONE
                row5.visibility = View.GONE
                row6.visibility = View.GONE
                row7.visibility = View.GONE
                row8.visibility = View.GONE
                row9.visibility=View.GONE
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
                row21.visibility=View.GONE
                row22.visibility=View.GONE
                row23.visibility=View.VISIBLE
                row24.visibility=View.GONE
            }

            "SERVICE-PARK" -> {
                row1.visibility = View.VISIBLE
                row2.visibility = View.GONE
                row3.visibility = View.GONE
                row4.visibility = View.GONE
                row5.visibility = View.GONE
                row6.visibility = View.GONE
                row7.visibility = View.GONE
                row8.visibility = View.GONE
                row9.visibility=View.GONE
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
                row21.visibility=View.GONE
                row22.visibility=View.GONE
                row23.visibility=View.VISIBLE
                row24.visibility=View.GONE
            }

            "BODYSHOP-PARK" -> {
                row1.visibility = View.VISIBLE
                row2.visibility = View.GONE
                row3.visibility = View.GONE
                row4.visibility = View.GONE
                row5.visibility = View.GONE
                row6.visibility = View.GONE
                row7.visibility = View.GONE
                row8.visibility = View.GONE
                row9.visibility=View.GONE
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
                row21.visibility=View.GONE
                row22.visibility=View.GONE
                row23.visibility=View.VISIBLE
                row24.visibility=View.GONE
            }

            "SALES DEMO" -> {
                row1.visibility = View.VISIBLE
                row2.visibility = View.GONE
                row3.visibility = View.GONE
                row4.visibility = View.GONE
                row5.visibility = View.GONE
                row6.visibility = View.GONE
                row7.visibility = View.GONE
                row8.visibility = View.GONE
                row9.visibility=View.GONE
                row10.visibility=View.GONE
                row11.visibility=View.GONE
                row12.visibility=View.GONE
                row13.visibility=View.GONE
                row14.visibility=View.GONE
                row15.visibility=View.GONE
                row16.visibility=View.GONE
                row17.visibility=View.GONE
                row18.visibility=View.GONE
                row19.visibility=View.VISIBLE
                row20.visibility=View.VISIBLE
                row21.visibility=View.GONE
                row22.visibility=View.GONE
                row23.visibility=View.GONE
                row24.visibility=View.GONE
            }

            "WASHING" ->{
                row1.visibility = View.VISIBLE
                row2.visibility = View.GONE
                row3.visibility = View.GONE
                row4.visibility = View.GONE
                row5.visibility = View.GONE
                row6.visibility = View.GONE
                row7.visibility = View.GONE
                row8.visibility = View.GONE
                row9.visibility=View.GONE
                row10.visibility=View.GONE
                row11.visibility=View.GONE
                row12.visibility=View.GONE
                row13.visibility=View.GONE
                row14.visibility=View.GONE
                row15.visibility=View.GONE
                row16.visibility=View.GONE
                row17.visibility=View.GONE
                row18.visibility=View.VISIBLE
                row19.visibility=View.GONE
                row20.visibility=View.GONE
                row21.visibility=View.GONE
                row22.visibility=View.GONE
                row23.visibility=View.GONE
                row24.visibility=View.GONE
            }

            "VEHWASHGOA" ->{
                row1.visibility = View.VISIBLE
                row2.visibility = View.GONE
                row3.visibility = View.GONE
                row4.visibility = View.GONE
                row5.visibility = View.GONE
                row6.visibility = View.GONE
                row7.visibility = View.GONE
                row8.visibility = View.GONE
                row9.visibility=View.GONE
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
                row21.visibility=View.GONE
                row22.visibility=View.GONE
                row23.visibility=View.GONE
                row24.visibility=View.VISIBLE
            }

            "WM" ->{
                row1.visibility = View.VISIBLE
                row2.visibility = View.GONE
                row3.visibility = View.GONE
                row4.visibility = View.GONE
                row5.visibility = View.GONE
                row6.visibility = View.GONE
                row7.visibility = View.GONE
                row8.visibility = View.GONE
                row9.visibility=View.GONE
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
                row21.visibility=View.GONE
                row22.visibility=View.GONE
                row23.visibility=View.GONE
                row24.visibility=View.GONE
            }

            "WASHSALES" ->{
                row1.visibility = View.VISIBLE
                row2.visibility = View.GONE
                row3.visibility = View.GONE
                row4.visibility = View.GONE
                row5.visibility = View.GONE
                row6.visibility = View.GONE
                row7.visibility = View.GONE
                row8.visibility = View.GONE
                row9.visibility=View.GONE
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
                row21.visibility=View.GONE
                row22.visibility=View.VISIBLE
                row23.visibility=View.GONE
                row24.visibility=View.GONE
            }
        }
    }
    fun goToCityReport(view: View) {
        val intent = Intent(this@Controller, StockController::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name",location_name)
        intent.putExtra("deptName",deptName)
        intent.putExtra("emailId",emailId)
        startActivity(intent)
    }

    fun goToChassisReport(view: View) {
        val intent = Intent(this@Controller, ChassisActivity::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name",location_name)
        intent.putExtra("deptName",deptName)
        startActivity(intent)
    }


    fun goToStockTaking(view: View) {
        val intent = Intent(this@Controller, StockTaking::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name",location_name)
        intent.putExtra("deptName",deptName)
        startActivity(intent)
    }


    fun goToCamera(view: View) {
//        val intent = Intent(this@Controller, CameraActivity::class.java)
        val intent = Intent(this@Controller, StockTransferNew::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name",location_name)
        intent.putExtra("deptName",deptName)
        startActivity(intent)
    }

    fun goToTrueValue(view: View) {
//        val intent = Intent(this@Controller, CameraActivity::class.java)
        val intent = Intent(this@Controller, TrueValueStk::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name",location_name)
        intent.putExtra("deptName",deptName)
        startActivity(intent)
    }


    fun SecurityVehTrans(view: View) {
        val intent = Intent(this@Controller, SecurityVehicleTrack::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name",location_name)
        intent.putExtra("deptName",deptName)
        startActivity(intent)
    }

    fun trueValVehEnq(view: View) {
        val intent = Intent(this@Controller, TrueValVehicleEnquiry::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name",location_name)
        intent.putExtra("deptName",deptName)
        startActivity(intent)
    }

    fun trueValStockTake(view: View) {
        val intent = Intent(this@Controller, TrueValueStockTaking2::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name",location_name)
        intent.putExtra("deptName",deptName)
        startActivity(intent)
    }

    fun workShopStockTake(view: View) {
        val intent = Intent(this@Controller, WorkshopStockTaking::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name",location_name)
        intent.putExtra("deptName",deptName)
        startActivity(intent)
    }


    fun workShopStockTransfer(view: View) {
        val intent = Intent(this@Controller, WorkshopPendingVehicle::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name",location_name)
        intent.putExtra("deptName",deptName)
        startActivity(intent)
    }


    fun workShopChassisEnquiry(view: View) {
        val intent = Intent(this@Controller, WorkShopChassisEnquiry::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name",location_name)
        intent.putExtra("deptName",deptName)
        startActivity(intent)
    }


//    fun generateLogins(view: View) {
//        val intent = Intent(this@Controller, GenerateLogins::class.java)
//        intent.putExtra("attribute1", attribute1)
//        intent.putExtra("login_name", login_name)
//        intent.putExtra("ouId", ouId)
//        intent.putExtra("location", location)
//        intent.putExtra("locId", locId)
//        intent.putExtra("location_name",location_name)
//        intent.putExtra("deptName",deptName)
//        startActivity(intent)
//    }

    fun stockTrfReceive(view: View) {
        val intent = Intent(this@Controller, WorkShopStockReceiveWithImages::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name",location_name)
        intent.putExtra("deptName",deptName)
        startActivity(intent)
    }

    fun stockTrfMade(view: View) {
        val intent = Intent(this@Controller, WorkShopStockTransferWithImages::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name",location_name)
        intent.putExtra("deptName",deptName)
        startActivity(intent)
    }

    fun viewVehicleHistory(view: View) {
        val intent = Intent(this@Controller, WorkshopVehicleHistory::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name",location_name)
        intent.putExtra("deptName",deptName)
        startActivity(intent)
    }

    fun workshopGatePassEntry(view: View) {
        val intent = Intent(this@Controller, WorkShopGatepass2::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name",location_name)
        intent.putExtra("deptName",deptName)
        startActivity(intent)
    }

    fun workshopParking(view: View) {
        val intent = Intent(this@Controller, WorkshopParkingModule::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name",location_name)
        intent.putExtra("deptName",deptName)
        startActivity(intent)
    }

    fun workshopWashingModule(view: View) {
        val intent = Intent(this@Controller, WorkshopWashingModule::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name",location_name)
        intent.putExtra("deptName",deptName)
        startActivity(intent)
    }

    fun workshopWashingModuleChassis(view: View) {
        val intent = Intent(this@Controller, SalesWashingWithChassis::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name",location_name)
        intent.putExtra("deptName",deptName)
        startActivity(intent)
    }

    fun workshopDemoVehicle(view: View) {
        val intent = Intent(this@Controller, WorkshopDemoVehicle::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name",location_name)
        intent.putExtra("deptName",deptName)
        startActivity(intent)
    }

    fun workshopDemoVehicleAvailability(view: View) {
        val intent = Intent(this@Controller, WorkshopAvailableDemoVehicle::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name",location_name)
        intent.putExtra("deptName",deptName)
        startActivity(intent)
    }

    fun floorVehicleTracking(view: View) {
        val intent = Intent(this@Controller, FloorVehicleTracking::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name",location_name)
        intent.putExtra("deptName",deptName)
        startActivity(intent)
    }

    fun workshopWashingGoa(view: View) {
        val intent = Intent(this@Controller, WorkshopWashingGoa::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name",location_name)
        intent.putExtra("deptName",deptName)
        startActivity(intent)
    }

    private fun logout() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun aboutActivity() {
        val intent = Intent(this, AboutActivity::class.java)
        intent.putExtra("deptName",deptName)
        startActivity(intent)
    }

}


