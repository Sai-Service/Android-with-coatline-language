package com.example.apinew


import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// base URL set in ApiClientErp
    interface ErpUserService {
//    @POST("ErpAndroid/login/loginpage4/")
    @POST("login/loginpage4/")
//    @POST("ErpAndroid/login/loginpage3/")  //Clone
    suspend fun userLoginSuspend(@Body request: LoginRequest): Response<LoginResponse>
}

