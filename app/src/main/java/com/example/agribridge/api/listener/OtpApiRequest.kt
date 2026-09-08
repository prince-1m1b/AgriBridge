package com.example.agribridge.api.listener

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OtpApiRequest {

    @POST("auth/send-otp")
    suspend fun sendOtp(@Body jsonObject: JsonObject): Response<Any?>?

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body jsonObject: JsonObject): Response<Any?>?
}
