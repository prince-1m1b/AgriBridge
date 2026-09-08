package com.example.agribridge.api.repository

import com.example.agribridge.api.ApiRequestResponse
import com.example.agribridge.api.listener.OtpApiRequest
import com.google.gson.JsonObject

class OtpRepository(private val apiRequest: OtpApiRequest?) : ApiRequestResponse() {

    suspend fun sendOtp(phoneJson: JsonObject) = apiRequest {
        apiRequest?.sendOtp(phoneJson)
    }

    suspend fun verifyOtp(verifyJson: JsonObject) = apiRequest {
        apiRequest?.verifyOtp(verifyJson)
    }
}
