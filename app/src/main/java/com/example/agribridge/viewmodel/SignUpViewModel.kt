package com.example.agribridge.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.agribridge.api.ApiResponse
import com.example.agribridge.api.Coroutines
import com.example.agribridge.api.repository.SignUpRepository
import com.google.gson.JsonObject

class SignUpViewModel(private val repository: SignUpRepository) : ViewModel() {
    
    private val userSignUpResponse = MutableLiveData<ApiResponse?>()
    
    fun apiCallForUserSignUp(jsonObject: JsonObject) {
        Coroutines.ioThenMain(
            { repository.apiCallForUserRegistration(jsonObject) }
        ) { response ->
            userSignUpResponse.value = response
        }
    }
    
    val getUserSignUpResponse: LiveData<ApiResponse?> get() = userSignUpResponse
    
}