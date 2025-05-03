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

class tRY : AppCompatActivity() {

    private lateinit var location: EditText
    private  lateinit var find:Button
    private  lateinit var viewLocation:TextView
    private  lateinit var itemCode:EditText
    private  lateinit var viewItemType:TextView
    private lateinit var findItemCode:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_try)

        location = findViewById(R.id.location)
        find= findViewById(R.id.find)
        viewLocation=findViewById(R.id.viewLocation)
        itemCode=findViewById(R.id.itemCode)
        viewItemType=findViewById(R.id.viewItemType)
        findItemCode=findViewById(R.id.findItemCode)



        find.setOnClickListener {
            viewValueLocation()
        }

        findItemCode.setOnClickListener{
            viewItemType()
        }
    }

    private fun viewValueLocation (){
        val Vloction =location.text.toString ()
        val LocId =Vloction.toInt()
        viewLocation.text=Vloction
    }

    private fun viewItemType (){
        val itemcode =itemCode.text.toString()
        val clientName =OkHttpClient()
        val api =ApiFile.APP_URL+"/ItemMst/asset/${itemcode}"
        val request=Request.Builder()
            .url(api)
            .build()
        GlobalScope.launch( Dispatchers.IO){
            try{
                Log.d("URL",request.toString())
                val getRes =clientName.newCall(request)
                    .execute()
                val ItemC=getRes.body?.string()

                ItemC?.let {
                    val JsonData=JSONObject(it)
                    val jsonobjdata=JsonData.getJSONObject("obj")
                    Log.d("JsonData",request.toString())
                    Log.d("JsonData----->",JsonData.toString())

                    val itemget=WorkshopFlow(
                        itemCode=jsonobjdata.getString("itemCode"),
                        itemType=jsonobjdata.getString("itemType")
                    )
                    runOnUiThread {
                        Log.d("itemCode-->",itemget.itemCode)
                        Log.d("itemCode-->",itemget.itemType)

                        viewItemType.text=itemget.itemType
                    }
                }

            }
            catch (e: Exception) {
            }
        }
    }
}



data class WorkshopFlow (
    var itemCode:String,
    var itemType:String,


    )

