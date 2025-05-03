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

//    private var ouId: Int = 0
//    private var locId: Int = 0
private lateinit var ouId: String
    private lateinit var locId: String
    private lateinit var locIdTxt :TextView
    private lateinit var username:TextView
    private lateinit var login_name: String
    private lateinit var attribute1: String
    private lateinit var role:String
    private lateinit var location: String
    private lateinit var logoutButton:ImageView
    private lateinit var location_name:String
    private lateinit var deptName:String
    private lateinit var reports:View
    private lateinit var security:View
    private lateinit var vehTrack:View
    private lateinit var row2:View
    private lateinit var about:ImageButton
//    private lateinit var chassisLay:View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controller)


        locIdTxt = findViewById(R.id.locIdTxt)
        username = findViewById(R.id.username)
//        stockTake = findViewById(R.id.stockTake)
//        chassisLay=findViewById(R.id.chassisLay)
        about=findViewById(R.id.about)


//        container = findViewById(R.id.container)
        logoutButton = findViewById(R.id.logoutButton)

//        ouId = intent.getIntExtra("ouId", 0)
//        locId = intent.getIntExtra("locId", 0)




        attribute1 = intent.getStringExtra("attribute1") ?: ""
        role = intent.getStringExtra("role") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""
        location_name = intent.getStringExtra("locName") ?: ""
        location = intent.getStringExtra("location") ?: ""
        deptName = intent.getStringExtra("deptName") ?: ""
        ouId = intent.getStringExtra("ouId") ?: ""
        locId = intent.getStringExtra("locId") ?: ""

        Log.d("locId", locId.toString())
        Log.d("ouId", ouId.toString())




        locIdTxt.text = "$location_name"
        username.text = "$login_name"


        logoutButton.setOnClickListener {
            logout()
        }

        about.setOnClickListener {
            aboutActivity()
        }


    }


    fun goToStockTaking(view: View) {
        val intent = Intent(this@Controller, StockTaking::class.java)
        intent.putExtra("attribute1", attribute1)
        intent.putExtra("login_name", login_name)
        intent.putExtra("ouId", ouId)
        intent.putExtra("location", location)
        intent.putExtra("locId", locId)
        intent.putExtra("locName",location_name)
        intent.putExtra("deptName",deptName)
        intent.putExtra("role",role)
        startActivity(intent)
    }

    private fun logout() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun aboutActivity() {
        val intent = Intent(this, AboutActivity::class.java)
        startActivity(intent)
    }

}


