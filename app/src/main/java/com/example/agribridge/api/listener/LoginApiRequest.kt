package com.example.agribridge.api.listener

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApiRequest {
    
    @POST("auth/login")
    suspend fun apiCallForUserLogin(@Body jsonObject: JsonObject?): Response<Any?>?
    
}