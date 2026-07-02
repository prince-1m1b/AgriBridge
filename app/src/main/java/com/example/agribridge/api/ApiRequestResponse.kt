package com.example.agribridge.api

import android.util.Log
import com.example.agribridge.R
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import com.example.agribridge.App.Companion.app
import com.example.agribridge.App.Companion.gson
import retrofit2.Response
import java.io.IOException

abstract class ApiRequestResponse {
    
    suspend fun <T : Any?> apiRequest(call: suspend () -> Response<T>?): ApiResponse {
        val apiResponse = ApiResponse()
        
        try {
            val response = call.invoke()
            
            if (response == null) {
                apiResponse.message = "No response from server"
                apiResponse.data = {}
                apiResponse.statusCode = 500
                return apiResponse
            }
            
            Log.d("TAG", "setObserver=============>ApiRequestResponse : $response ")
            apiResponse.statusCode = response.code()
            Log.d("API>>", "Status Code: ${response.code()}")
            Log.d("API>>", "Method: ${response.raw().request.method}")
            Log.d("API>>", "URL: ${response.raw().request.url}")
            Log.d("API>>", "Raw Response: ${gson.toJson(response.body())}")
            
            if (!response.isSuccessful || gson.toJson(response.body()) == null || gson.toJson(response.body()) == "null") {
                return parseErrorBody(response, apiResponse)
            }
            
            response.let {
                try {
                    val rawJson = gson.toJsonTree(response.body())?.asJsonObject
                    
                    if (rawJson?.has("result") == true) {
                        val result = rawJson.getAsJsonObject("result")
                        apiResponse.type = result.get("status")?.asString
                        apiResponse.message = result.get("message")?.asString
                        apiResponse.data = result.get("data")
                        return apiResponse
                    }
                    
                    if (rawJson?.has("status") == true || rawJson?.has("data") == true) {
                        apiResponse.type = rawJson.get("status")?.asString
                        apiResponse.message = rawJson.get("message")?.asString
                        apiResponse.data = rawJson.get("data")
                        return apiResponse
                    }
                    
                    if (rawJson?.has("error") == true) {
                        val error = rawJson.getAsJsonObject("error")
                        val errorData = error.getAsJsonObject("data")
                        
                        apiResponse.type = app?.getString(R.string.error)
                        apiResponse.message = errorData?.get("message")?.asString ?: error.get("message")?.asString ?: app?.getString(R.string.e_something_went_wrong)
                        return apiResponse
                    }
                    
                } catch (e: Exception) {
                    Log.e("API>>", "Body parsing exception: ${e.localizedMessage}")
                    apiResponse.message = app?.getString(R.string.e_something_went_wrong)
                }
                
                Log.d("API>>", "Final ApiResponse: $apiResponse")
                return apiResponse
            }
            
        } catch (e: IOException) {
            apiResponse.message = "Server Down or Network Failure"
            return apiResponse
            
        } catch (e: JsonSyntaxException) {
            apiResponse.message = app?.getString(R.string.e_something_went_wrong)
            return apiResponse
            
        } catch (e: Exception) {
            apiResponse.message = app?.getString(R.string.e_something_went_wrong)
            return apiResponse
        }
    }
    
    private fun parseErrorBody(response: Response<*>, apiResponse: ApiResponse): ApiResponse {
        try {
            val errorBodyStr = response.errorBody()?.string()
            if (errorBodyStr?.trim()?.startsWith("<") == true) {
                apiResponse.message = "Server Down"
            }
            val errorJson = gson.fromJson(errorBodyStr, JsonObject::class.java)
            if (errorJson.has("error")) {
                val error = errorJson.getAsJsonObject("error")
                val errorData = error.getAsJsonObject("data")
                apiResponse.message = errorData?.get("message")?.asString ?: error.get("message")?.asString ?: app?.getString(R.string.e_something_went_wrong)
                
            } else if (errorJson.has("result")) {
                val result = errorJson.getAsJsonObject("result")
                apiResponse.message = result.get("message")?.asString ?: app?.getString(R.string.e_something_went_wrong)
                
            } else {
                apiResponse.message = app?.getString(R.string.e_something_went_wrong)
            }
            return apiResponse
            
        } catch (e: Exception) {
            Log.e("API>>", "ErrorBody parse exception: ${e.localizedMessage}")
            apiResponse.message = "Service Unavailable, try After some Time"
            apiResponse.data = null
            apiResponse.statusCode = 503
            return apiResponse
        }
    }
}
