package com.example.snapcart.network



import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://www.fast2sms.com/dev/"

    val apiService: Fast2SMSApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Fast2SMSApiService::class.java)
    }
}
