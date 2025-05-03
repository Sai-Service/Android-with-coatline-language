package com.example.saivehicledelivery


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
    private lateinit var userContact:String
    private lateinit var userName:String
    private lateinit var logoutButton:ImageView
    private lateinit var deptName:String
    private lateinit var reports:View
    private lateinit var security:View
    private lateinit var vehTrack:View
    private lateinit var row1:View
    private lateinit var row2:View




    private lateinit var about:ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controller)

        row1=findViewById(R.id.row1)
        row2=findViewById(R.id.row2)

        login_name = intent.getStringExtra("login_name") ?: ""
        location_name = intent.getStringExtra("location_name") ?: ""
        deptName = intent.getStringExtra("deptName") ?: ""
        locId = intent.getIntExtra("locId", 0)
        ouId = intent.getIntExtra("ouId", 0)
        location=intent.getStringExtra("location") ?:""
        userContact=intent.getStringExtra("userContact") ?:""
        userName=intent.getStringExtra("userName") ?:""



        locIdTxt=findViewById(R.id.locIdTxt)
        username=findViewById(R.id.username)

        locIdTxt.text = "$location_name"
        username.text = "$login_name"

        logoutButton=findViewById(R.id.logoutButton)

        logoutButton.setOnClickListener{
            logout()
        }


    }


    fun goToVehDelivery(view: View) {
        val intent = Intent(this@Controller, VehicleDelivery::class.java)
        intent.putExtra("login_name", login_name)
        intent.putExtra("location",location)
        intent.putExtra("ouId", ouId)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name", location_name)
        intent.putExtra("deptName", deptName)
        intent.putExtra("userContact", userContact)
        intent.putExtra("userName", userName)

        startActivity(intent)
    }

    fun goToReports(view: View) {
        val intent = Intent(this@Controller, DeliveryReport::class.java)
        intent.putExtra("login_name", login_name)
        intent.putExtra("location",location)
        intent.putExtra("ouId", ouId)
        intent.putExtra("locId", locId)
        intent.putExtra("location_name", location_name)
        intent.putExtra("deptName", deptName)
        intent.putExtra("userContact", userContact)
        intent.putExtra("userContact", userContact)
        startActivity(intent)
    }


    private fun logout() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

//    private fun aboutActivity() {
//        val intent = Intent(this, AboutActivity::class.java)
//        intent.putExtra("deptName",deptName)
//        startActivity(intent)
//    }

}


