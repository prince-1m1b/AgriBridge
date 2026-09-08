package com.example.agribridge.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.agribridge.api.ApiResponse
import com.example.agribridge.api.Coroutines
import com.example.agribridge.api.repository.OtpRepository
import com.google.gson.JsonObject

class OtpViewModel(private val repository: OtpRepository) : ViewModel() {

    private val _sendOtpResult = MutableLiveData<ApiResponse?>()
    val sendOtpResult: LiveData<ApiResponse?> get() = _sendOtpResult

    private val _verifyOtpResult = MutableLiveData<ApiResponse?>()
    val verifyOtpResult: LiveData<ApiResponse?> get() = _verifyOtpResult

    fun sendOtp(phoneNumber: String) {
        val jsonObject = JsonObject().apply {
            addProperty("phone_number", phoneNumber)
        }
        Coroutines.ioThenMain(
            { repository.sendOtp(jsonObject) }
        ) { response ->
            _sendOtpResult.value = response
        }
    }

    fun verifyOtp(phoneNumber: String, otp: String) {
        val jsonObject = JsonObject().apply {
            addProperty("phone_number", phoneNumber)
            addProperty("otp", otp)
        }
        Coroutines.ioThenMain(
            { repository.verifyOtp(jsonObject) }
        ) { response ->
            _verifyOtpResult.value = response
        }
    }

    fun clearVerifyResult() {
        _verifyOtpResult.value = null
    }

    fun clearSendResult() {
        _sendOtpResult.value = null
    }
}
