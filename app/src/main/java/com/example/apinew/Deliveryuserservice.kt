package com.example.apinew

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// Delivery-specific service — base URL set in ApiClientDelivery (e.g. https://delivery.yourserver.com/)
// ⚠️ Replace the @POST path below with your actual Delivery login endpoint
interface DeliveryUserService {

//    @POST("SaiVehicleDelivery/login/vehDelvLogin6/")
//    @POST("SaiVehicleDelivery/login/vehDelvLogin5/")    //clone      // ← UPDATE THIS to your real delivery endpoint
    @POST("/login/vehDelvLogin6/")

// ← UPDATE THIS to your real delivery endpoint
    suspend fun userLoginSuspend(@Body request: LoginRequest): Response<LoginResponse>
}