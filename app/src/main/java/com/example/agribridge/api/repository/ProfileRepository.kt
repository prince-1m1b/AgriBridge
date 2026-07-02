package com.example.agribridge.api.repository

import com.example.agribridge.api.ApiRequestResponse
import com.example.agribridge.api.listener.ProfileApiRequest
import com.example.agribridge.model.ProfileUpdateRequest

class ProfileRepository(private val apiRequest: ProfileApiRequest?) : ApiRequestResponse() {

    suspend fun getStates() = apiRequest {
        apiRequest?.getStates()
    }

    suspend fun getDistricts(state: String) = apiRequest {
        apiRequest?.getDistricts(state)
    }

    suspend fun updateProfile(body: ProfileUpdateRequest) = apiRequest {
        apiRequest?.updateProfile(body)
    }
}
