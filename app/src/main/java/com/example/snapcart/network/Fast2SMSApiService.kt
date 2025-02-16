package com.example.snapcart.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Fast2SMSApiService {
    @GET("bulkV2")
    fun sendOtp(
        @Query("authorization") apiKey: String,
        @Query("variables_values") otp: String,
        @Query("route") route: String = "otp",
        @Query("numbers") phoneNumber: String
    ): Call<ApiResponse>
}
