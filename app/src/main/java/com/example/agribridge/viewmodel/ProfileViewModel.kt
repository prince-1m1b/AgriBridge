package com.example.agribridge.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agribridge.api.ApiResponse
import com.example.agribridge.api.repository.ProfileRepository
import com.example.agribridge.model.ProfileUpdateRequest
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {

    private val _statesResult = MutableLiveData<ApiResponse?>()
    val statesResult: LiveData<ApiResponse?> = _statesResult

    private val _districtsResult = MutableLiveData<ApiResponse?>()
    val districtsResult: LiveData<ApiResponse?> = _districtsResult

    private val _profileUpdateResult = MutableLiveData<ApiResponse?>()
    val profileUpdateResult: LiveData<ApiResponse?> = _profileUpdateResult

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun getStates() {
        _loading.value = true
        viewModelScope.launch {
            val response = repository.getStates()
            _statesResult.value = response
            _loading.value = false
        }
    }

    fun getDistricts(state: String) {
        _loading.value = true
        viewModelScope.launch {
            val response = repository.getDistricts(state)
            _districtsResult.value = response
            _loading.value = false
        }
    }

    fun updateProfile(state: String? = null, district: String? = null, password: String? = null) {
        _loading.value = true
        viewModelScope.launch {
            val requestBody = ProfileUpdateRequest(state, district, password)
            val response = repository.updateProfile(requestBody)
            _profileUpdateResult.value = response
            _loading.value = false
        }
    }

    fun clearProfileUpdateResult() {
        _profileUpdateResult.value = null
    }

    fun clearStatesResult() {
        _statesResult.value = null
    }

    fun clearDistrictsResult() {
        _districtsResult.value = null
    }
}
