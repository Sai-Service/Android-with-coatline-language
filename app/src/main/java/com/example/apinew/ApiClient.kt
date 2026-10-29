package com.example.apinew

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// ─── ERP Client ──────────────────────────────────────────────────────────────

object ApiClientErp {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ApiFile.ERP_LOGIN_URL)
            .client(buildOkHttp())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Returns the ERP-specific service
    fun getUserService(): ErpUserService =
        retrofit.create(ErpUserService::class.java)
}

// ─── Delivery Client ──────────────────────────────────────────────────────────

object ApiClientDelivery {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ApiFile.DELIVERY_LOGIN_URL)
            .client(buildOkHttp())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Returns the Delivery-specific service
    fun getUserService(): DeliveryUserService =
        retrofit.create(DeliveryUserService::class.java)
}

// ─── Shared OkHttp builder ────────────────────────────────────────────────────

//private fun buildOkHttp(): OkHttpClient =
//    OkHttpClient.Builder()
//        .addInterceptor(
//            HttpLoggingInterceptor().apply {
//                level = HttpLoggingInterceptor.Level.BODY   // use NONE in production
//            }
//        )
//        .build()

private fun buildOkHttp(): OkHttpClient =
    OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.NONE  // ← Just set NONE directly
            }
        )
        .build()