package com.example.agribridge.api.repository

import com.example.agribridge.api.ApiRequestResponse
import com.example.agribridge.api.listener.LogoutApiRequest
import com.google.gson.JsonObject

class LogoutRepository(private val apiRequest: LogoutApiRequest?) : ApiRequestResponse() {
    suspend fun apiCallForUserLogout(jsonObj : JsonObject) = apiRequest {
        apiRequest?.apiCallForUserLogout(jsonObj)
    }
}
