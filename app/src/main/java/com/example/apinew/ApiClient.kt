package com.example.apinew

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:8081/"       //localhost
<<<<<<< Updated upstream:app/src/main/java/ApiClient.kt
//    private const val BASE_URL ="http://182.73.44.117:8080/"  //production
=======
>>>>>>> Stashed changes:app/src/main/java/com/example/apinew/ApiClient.kt
//    private const val BASE_URL ="http://182.72.0.216:7485/"  //production new

<<<<<<< Updated upstream:app/src/main/java/ApiClient.kt
//    private const val BASE_URL = "http://115.242.10.85:7485/"   //13-04-2025-prod



=======
>>>>>>> Stashed changes:app/src/main/java/com/example/apinew/ApiClient.kt
//    private const val BASE_URL ="http://115.242.10.86:6101/"

//    private const val BASE_URL ="http://203.115.117.157:6101/"// Temporary Clone


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


