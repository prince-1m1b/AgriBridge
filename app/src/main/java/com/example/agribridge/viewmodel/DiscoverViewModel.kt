package com.example.agribridge.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agribridge.api.ApiResponse
import com.example.agribridge.api.repository.DiscoverRepository
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.launch

class DiscoverViewModel(private val repository: DiscoverRepository) : ViewModel() {

    private val _discoverData = MutableLiveData<ApiResponse?>()
    val discoverData: LiveData<ApiResponse?> = _discoverData

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    var currentLanguage: String? = null

    fun fetchHomeDiscoverData(state: String, district: String, language: String) {
        currentLanguage = language
        _loading.value = true
        viewModelScope.launch {
            val body = JsonObject().apply {
                addProperty("type", "home")
                addProperty("state", state.ifEmpty { "Uttar Pradesh" })
                addProperty("district", district.ifEmpty { "Lucknow" })
                
                val locationArray = JsonArray().apply {
                    add(26.8467)
                    add(80.9462)
                }
                add("location", locationArray)
                addProperty("language", language)
                addProperty("user_preferences", "Solar Pumps, Organic Seeds")
            }
            val response = repository.getHomeDiscoverData(body)
            _discoverData.value = response
            _loading.value = false
        }
    }

    fun fetchDiscoverList(type: String, state: String, district: String, language: String) {
        currentLanguage = language
        _loading.value = true
        viewModelScope.launch {
            val body = JsonObject().apply {
                addProperty("type", type)
                addProperty("state", state.ifEmpty { "Uttar Pradesh" })
                addProperty("district", district.ifEmpty { "Lucknow" })
                
                val locationArray = JsonArray().apply {
                    add(26.8467)
                    add(80.9462)
                }
                add("location", locationArray)
                addProperty("language", language)
                addProperty("user_preferences", "Solar Pumps, Organic Seeds")
            }
            val response = repository.getHomeDiscoverData(body)
            _discoverData.value = response
            _loading.value = false
        }
    }
}
