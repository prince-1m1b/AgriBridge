package com.example.agribridge.api.listener

import com.example.agribridge.model.ProfileUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

interface ProfileApiRequest {

    @GET("location/states")
    suspend fun getStates(): Response<Any?>?

    @GET("location/districts")
    suspend fun getDistricts(@Query("state") state: String): Response<Any?>?

    @PUT("auth/profile")
    suspend fun updateProfile(@Body body: ProfileUpdateRequest): Response<Any?>?
}
