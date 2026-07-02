package com.example.agribridge.api.repository

import com.example.agribridge.api.ApiRequestResponse
import com.example.agribridge.api.listener.DiscoverApiRequest
import com.google.gson.JsonObject

class DiscoverRepository(private val apiRequest: DiscoverApiRequest?) : ApiRequestResponse() {

    suspend fun getHomeDiscoverData(body: JsonObject) = apiRequest {
        apiRequest?.discoverHomeData(body)
    }

}
