package com.example.saivehicledelivery


import LoginResponse
import UserService
import com.example.apinew.LoginResponseDeserializer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder

object ApiClient {
//    private const val BASE_URL = "http://10.0.2.2:8081/"       //localhost

    private const val BASE_URL ="http://182.72.0.216:7485/"  //production new

//    private const val BASE_URL ="http://172.16.21.149:6101/"



    // ALSO CHANGE THE IP FROM network_security_config.xml file


    private val gson = GsonBuilder()
        .registerTypeAdapter(LoginResponse::class.java, LoginResponseDeserializer())
        .create()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    fun getUserService(): UserService {
        return retrofit.create(UserService::class.java)
    }
}


