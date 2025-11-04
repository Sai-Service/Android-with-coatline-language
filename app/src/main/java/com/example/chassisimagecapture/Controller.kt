package com.example.chassisimagecapture

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
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


    private lateinit var about:ImageButton



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controller)


        locIdTxt = findViewById(R.id.locIdTxt)
        username = findViewById(R.id.username)
        reports = findViewById(R.id.reports)
        deptIntent = findViewById(R.id.deptIntent)
        about = findViewById(R.id.about)


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

        reports = findViewById(R.id.reports)

        locIdTxt.text = "$location_name"
        username.text = "$login_name"
        deptIntent.text = deptName



        logoutButton.setOnClickListener {
            logout()
        }


        when (deptName) {
            "ACCOUNTS" -> {
                row1.visibility = View.VISIBLE
            }


        }
    }

    private fun logout() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


}


