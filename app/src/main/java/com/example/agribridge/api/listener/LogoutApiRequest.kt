package com.example.agribridge.api.listener

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LogoutApiRequest {
    @POST("auth/logout")
    suspend fun apiCallForUserLogout(@Body jsonObject: JsonObject?): Response<Any?>?
}
