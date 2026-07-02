package com.example.agribridge.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.agribridge.utils.LocaleHelper

class LanguageViewModel : ViewModel() {

    private val _currentLanguage = MutableLiveData<String>()
    val currentLanguage: LiveData<String> = _currentLanguage

    init {
        _currentLanguage.value = LocaleHelper.getLocale()
    }

    fun changeLanguage(languageCode: String) {
        LocaleHelper.setLocale(languageCode)
        _currentLanguage.value = languageCode
    }
}
