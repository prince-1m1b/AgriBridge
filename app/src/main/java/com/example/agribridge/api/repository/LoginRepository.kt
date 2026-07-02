package com.example.agribridge.api.repository

import com.example.agribridge.api.ApiRequestResponse
import com.example.agribridge.api.listener.LoginApiRequest
import com.google.gson.JsonObject

class LoginRepository(private val apiRequest: LoginApiRequest?) : ApiRequestResponse() {
    
    suspend fun apiCallForUserLogin(jsonObject: JsonObject) = apiRequest {
        apiRequest?.apiCallForUserLogin(jsonObject)
    }
    
}