package com.example.apinew

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.common.api.Api.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject


class practicePage : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practicpage)
    }


    private fun fetchData() {
        val client=OkHttpClient()
        val vehNo="102030"
        val url=ApiFile.APP_URL + "/service/wsVehDetForTestDriveOut?regNo=$vehNo"

        val request= Request.Builder().url(url).build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response= client.newCall(request).execute()
                val jsonData=response.body?.toString()
                jsonData?.let {
                    val jsonObject=JSONObject(it)
                    val nss=jsonObject.getJSONArray("obj").getJSONObject(0)

                    val dataExtract=pract(
                        JOBCARDNO = nss.optString("JOBCARDNO"),
                        DEPT = nss.optString("NSS")
                    )


                }

            } catch (e:Exception){
                    e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@practicePage,
                        "Failed to fetch details for vehicle No: $vehNo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    data class pract(
        val JOBCARDNO:String,
        val DEPT:String
    )
}