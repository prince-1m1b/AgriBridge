package com.example.agribridge.viewmodel

import androidx.lifecycle.*
import com.example.agribridge.api.*
import com.example.agribridge.api.repository.LogoutRepository
import com.google.gson.JsonObject

class LogoutViewModel(private val repository: LogoutRepository) : ViewModel() {
    
    private val userLogoutResponse = MutableLiveData<ApiResponse?>()
    
    fun apiCallForUserLogout(jsonObj : JsonObject) {
        Coroutines.ioThenMain(
            { repository.apiCallForUserLogout(jsonObj) }
        ) { response ->
            userLogoutResponse.value = response
        }
    }
    
    val getUserLogoutResponse: LiveData<ApiResponse?> = userLogoutResponse
}
