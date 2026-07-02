package com.example.agribridge.api.repository

import com.example.agribridge.api.ApiRequestResponse
import com.example.agribridge.api.listener.SignUpApiRequest
import com.google.gson.JsonObject

class SignUpRepository(private val apiRequest: SignUpApiRequest?) : ApiRequestResponse() {
    
    suspend fun apiCallForUserRegistration(jsonObject: JsonObject) = apiRequest {
        apiRequest?.apiCallForUserRegistration(jsonObject)
    }
    
}