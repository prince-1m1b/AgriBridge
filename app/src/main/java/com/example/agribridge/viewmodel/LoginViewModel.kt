package com.example.agribridge.viewmodel

import androidx.lifecycle.*
import com.example.agribridge.api.*
import com.example.agribridge.api.repository.LoginRepository
import com.google.gson.JsonObject

class LoginViewModel(private val repository: LoginRepository) : ViewModel() {
    
    private val userLoginResponse = MutableLiveData<ApiResponse?>()
    
    fun apiCallForUserLogin(jsonObject: JsonObject) {
        Coroutines.ioThenMain(
            { repository.apiCallForUserLogin(jsonObject) }
        ) { response ->
            userLoginResponse.value = response
        }
    }
    
    val getUserLoginResponse: LiveData<ApiResponse?> = userLoginResponse
    
}