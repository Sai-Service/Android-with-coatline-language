package com.example.apinew

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class WorkshopViewTrfRecImages : Activity() {

    private lateinit var chassisNoEditText: EditText
    private var ouId: Int = 0
    private lateinit var location_name:String
    private lateinit var login_name: String

    private lateinit var stockTrfNo: String
    private lateinit var jobCardNo: String
    private lateinit var vehNo:String
    private lateinit var btnTransferred:Button
    private lateinit var btnReceived:Button
    private lateinit var VEH_IMAGE:ImageView
    private lateinit var VEH_IMAGE2:ImageView
    private lateinit var VEH_IMAGE3:ImageView
    private lateinit var VEH_IMAGE4:ImageView
    private lateinit var VEH_IMAGE5:ImageView
    private lateinit var VEH_IMAGE6:ImageView
    private lateinit var VEH_IMAGE7:ImageView
    private lateinit var VEH_IMAGE8:ImageView
    private lateinit var VEH_IMAGE9:ImageView
    private lateinit var VEH_IMAGE10:ImageView
    private lateinit var VEH_IMAGE11:ImageView
    private lateinit var VEH_IMAGE12:ImageView
    private lateinit var VEH_IMAGE13:ImageView
    private lateinit var VEH_IMAGE14:ImageView
    private lateinit var vehNoTxt:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workshop_view_trf_rec_images)

        ouId = intent.getIntExtra("ouId", 0)
        location_name = intent.getStringExtra("location_name") ?: ""
        login_name = intent.getStringExtra("login_name") ?: ""

        stockTrfNo = intent.getStringExtra("STOCK_TRF_NO") ?: ""
        jobCardNo = intent.getStringExtra("JOB_CARD_NO") ?: ""
        vehNo = intent.getStringExtra("REG_NO") ?: ""
        btnTransferred=findViewById(R.id.btnTransferred)
        btnReceived=findViewById(R.id.btnReceived)

        vehNoTxt=findViewById(R.id.vehNoTxt)
        vehNoTxt.text="Vehicle No.- $vehNo"

        VEH_IMAGE=findViewById(R.id.VEH_IMAGE)
        VEH_IMAGE2=findViewById(R.id.VEH_IMAGE2)
        VEH_IMAGE3=findViewById(R.id.VEH_IMAGE3)
        VEH_IMAGE4=findViewById(R.id.VEH_IMAGE4)
        VEH_IMAGE5=findViewById(R.id.VEH_IMAGE5)
        VEH_IMAGE6=findViewById(R.id.VEH_IMAGE6)
        VEH_IMAGE7=findViewById(R.id.VEH_IMAGE7)
        VEH_IMAGE8=findViewById(R.id.VEH_IMAGE8)
        VEH_IMAGE9=findViewById(R.id.VEH_IMAGE9)
        VEH_IMAGE10=findViewById(R.id.VEH_IMAGE10)
        VEH_IMAGE11=findViewById(R.id.VEH_IMAGE11)
        VEH_IMAGE12=findViewById(R.id.VEH_IMAGE12)
        VEH_IMAGE13=findViewById(R.id.VEH_IMAGE13)
        VEH_IMAGE14=findViewById(R.id.VEH_IMAGE14)

        VEH_IMAGE.visibility=View.GONE
        VEH_IMAGE2.visibility=View.GONE
        VEH_IMAGE3.visibility=View.GONE
        VEH_IMAGE4.visibility=View.GONE
        VEH_IMAGE5.visibility=View.GONE
        VEH_IMAGE6.visibility=View.GONE
        VEH_IMAGE7.visibility=View.GONE
        VEH_IMAGE8.visibility=View.GONE
        VEH_IMAGE9.visibility=View.GONE
        VEH_IMAGE10.visibility=View.GONE
        VEH_IMAGE11.visibility=View.GONE
        VEH_IMAGE12.visibility=View.GONE
        VEH_IMAGE13.visibility=View.GONE
        VEH_IMAGE14.visibility=View.GONE


        btnTransferred.setOnClickListener {
            fetchTransferImages()
        }

        btnReceived.setOnClickListener {
            fetchReceiveImages()
        }


//        refreshButton = findViewById(R.id.refreshButton)
//        refreshButton.setOnClickListener {
//            resetFields()
//        }
    }
        private fun resetFields() {
        findViewById<ImageView>(R.id.VEH_IMAGE).setImageBitmap(null)
            findViewById<ImageView>(R.id.VEH_IMAGE2).setImageBitmap(null)
            findViewById<ImageView>(R.id.VEH_IMAGE3).setImageBitmap(null)
            findViewById<ImageView>(R.id.VEH_IMAGE4).setImageBitmap(null)
            findViewById<ImageView>(R.id.VEH_IMAGE5).setImageBitmap(null)
            findViewById<ImageView>(R.id.VEH_IMAGE6).setImageBitmap(null)
            findViewById<ImageView>(R.id.VEH_IMAGE7).setImageBitmap(null)
            findViewById<ImageView>(R.id.VEH_IMAGE8).setImageBitmap(null)
            findViewById<ImageView>(R.id.VEH_IMAGE9).setImageBitmap(null)
            findViewById<ImageView>(R.id.VEH_IMAGE10).setImageBitmap(null)
            findViewById<ImageView>(R.id.VEH_IMAGE11).setImageBitmap(null)
            findViewById<ImageView>(R.id.VEH_IMAGE12).setImageBitmap(null)
            findViewById<ImageView>(R.id.VEH_IMAGE13).setImageBitmap(null)
            findViewById<ImageView>(R.id.VEH_IMAGE14).setImageBitmap(null)

//            findViewById<ImageView>(R.id.VEH_IMAGE_NEW).setImageBitmap(null)

    }

    // To change the date format as dd-mm-yyyy
    private fun formatDateTime(dateTime: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
            val outputDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val outputTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(dateTime)
            val formattedDate = date?.let { outputDateFormat.format(it) }
            val formattedTime = date?.let { outputTimeFormat.format(it) }
            "$formattedDate $formattedTime"
        } catch (e: Exception) {
            dateTime
        }
    }

    private fun showProgressDialog(): AlertDialog {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_progress, null)
        builder.setView(dialogView)
        builder.setCancelable(false)

        val dialog = builder.create()
        dialog.show()
        return dialog
    }

    private fun fetchTransferImages() {
        resetFields()
        val dialogBox=showProgressDialog()
        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
        val request = Request.Builder()
            .url("${ApiFile.APP_URL}/service/wsTrfImage?stockTrfNo=$stockTrfNo")
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()

                Log.d("API_RESPONSE", "Raw response from /Download: $jsonData")

                jsonData?.let {
                    val imagePaths = JSONArray(it)

                    for (i in 0 until imagePaths.length()) {
                        val imagePath = imagePaths.optString(i)

                        if (!imagePath.isNullOrEmpty()) {
                            val downloadUrl =
                                "${ApiFile.APP_URL}/service/wsTrfImageDownload?imagePath=$imagePath"

                            Log.d("DOWNLOAD_URL", "Downloading image from URL: $downloadUrl")

                            runOnUiThread {
                                dialogBox.dismiss()
                                Picasso.get().load(downloadUrl).into(getImageViewForIndex(i))
                                populateFields()
                                Toast.makeText(
                                    this@WorkshopViewTrfRecImages,
                                    "Images displayed for $vehNo are at the time of Stock Transfer.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } ?: run {
                    Log.e("API_ERROR", "No response received for Vehicle no: $vehNo")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    dialogBox.dismiss()
                    Log.e(
                        "API_ERROR",
                        "Error fetching images Vehicle no: $vehNo - ${e.javaClass.simpleName}: ${e.message}"
                    )
                    Toast.makeText(
                        this@WorkshopViewTrfRecImages,
                        "Error fetching images for Vehicle no: $vehNo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun fetchReceiveImages() {
        val dialogBox=showProgressDialog()
        resetFields()
//        val client = OkHttpClient()
        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
        val request = Request.Builder()
            .url("${ApiFile.APP_URL}/service/wsRecImage?stockTrfNo=$stockTrfNo")
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()

                Log.d("API_RESPONSE", "Raw response from /Download: $jsonData")

                jsonData?.let {
                    val imagePaths = JSONArray(it)

                    for (i in 0 until imagePaths.length()) {
                        val imagePath = imagePaths.optString(i)

                        if (!imagePath.isNullOrEmpty()) {
                            val downloadUrl =
                                "${ApiFile.APP_URL}/service/wsRecImageDownload?imagePath=$imagePath"

                            Log.d("DOWNLOAD_URL", "Downloading image from URL: $downloadUrl")

                            runOnUiThread {
                                dialogBox.dismiss()
                                Picasso.get().load(downloadUrl).into(getImageViewForIndex(i))

                                populateFields()
                                Toast.makeText(
                                    this@WorkshopViewTrfRecImages,
                                    "Images displayed for $vehNo are at the time of Stock Receive.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } ?: run {
                    Log.e("API_ERROR", "No response received for Vehicle No: $vehNo")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    dialogBox.dismiss()
                    Log.e(
                        "API_ERROR",
                        "Error fetching images for Vehicle no: $vehNo - ${e.javaClass.simpleName}: ${e.message}"
                    )
                    Toast.makeText(
                        this@WorkshopViewTrfRecImages,
                        "Error fetching images for Vehicle no: $vehNo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun populateFields(){
        VEH_IMAGE.visibility=View.VISIBLE
        VEH_IMAGE2.visibility=View.VISIBLE
        VEH_IMAGE3.visibility=View.VISIBLE
        VEH_IMAGE4.visibility=View.VISIBLE
        VEH_IMAGE5.visibility=View.VISIBLE
        VEH_IMAGE6.visibility=View.VISIBLE
        VEH_IMAGE7.visibility=View.VISIBLE
        VEH_IMAGE8.visibility=View.VISIBLE
        VEH_IMAGE9.visibility=View.VISIBLE
        VEH_IMAGE10.visibility=View.VISIBLE
        VEH_IMAGE11.visibility=View.VISIBLE
        VEH_IMAGE12.visibility=View.VISIBLE
        VEH_IMAGE13.visibility=View.VISIBLE
        VEH_IMAGE14.visibility=View.VISIBLE
    }

    private fun getImageViewForIndex(index: Int): ImageView {
        return when (index) {
            0 -> findViewById(R.id.VEH_IMAGE)
            1 -> findViewById(R.id.VEH_IMAGE2)
            2 -> findViewById(R.id.VEH_IMAGE3)
            3 -> findViewById(R.id.VEH_IMAGE4)
            4 -> findViewById(R.id.VEH_IMAGE5)
            5 -> findViewById(R.id.VEH_IMAGE6)
            6 -> findViewById(R.id.VEH_IMAGE7)
            7 -> findViewById(R.id.VEH_IMAGE8)
            8 -> findViewById(R.id.VEH_IMAGE9)
            9 -> findViewById(R.id.VEH_IMAGE10)
            10 -> findViewById(R.id.VEH_IMAGE11)
            11 -> findViewById(R.id.VEH_IMAGE12)
            12 -> findViewById(R.id.VEH_IMAGE13)
            13 -> findViewById(R.id.VEH_IMAGE14)

            else -> findViewById(R.id.VEH_IMAGE)
        }
    }


    private fun logout() {
        val intent = Intent(this@WorkshopViewTrfRecImages, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun rd_to_cam() {
        val chassisNo = chassisNoEditText.text.toString()
        if (chassisNo.isNotEmpty()) {
            val intent = Intent(this, UploadImage::class.java)
            intent.putExtra("CHASSIS_NO", chassisNo)
            intent.putExtra("login_name", login_name)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Please enter a Chassis Number", Toast.LENGTH_SHORT).show()
        }
    }

    private fun backToHome() {
        finish()
    }


    private fun back() {
        finish()
    }
}
