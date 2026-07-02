package com.example.agribridge.api.listener

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface DiscoverApiRequest {

    @POST("farmer/discover")
    suspend fun discoverHomeData(@Body body: JsonObject): Response<Any?>?

}
