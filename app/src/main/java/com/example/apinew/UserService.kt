package com.example.apinew

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("/ErpAndroid/login/loginpage1/") // for production and clone this remains same
//    @POST("/login/loginpage1/") // for localhost
    fun userLogin(@Body loginRequest: LoginRequest): Call<LoginResponse>

//    @POST("user/login")
@POST("/ErpAndroid/login/loginpage1/")
    suspend fun userLoginSuspend(@Body request: LoginRequest): Response<LoginResponse>
}
