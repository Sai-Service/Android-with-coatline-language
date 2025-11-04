// ApiService.kt
package com.example.chassisimagecapture

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @Multipart
    @POST("/img-to-pdf")
    fun uploadFilesAsPdf(
        @Part file1: MultipartBody.Part,
        @Part file2: MultipartBody.Part,
        @Part("chasisNo") chasisNo: String,
        @Part("createdBy") createdBy: String
    ): Call<SaiResponse>  // This should match your SaiResponse class
}
