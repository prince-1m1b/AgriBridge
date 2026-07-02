package com.example.agribridge

import android.app.Application
import com.example.agribridge.utils.Constant
import com.example.agribridge.utils.LocaleHelper
import com.example.agribridge.utils.Preference
import com.example.agribridge.utils.LanguageManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class App : Application() {
    
    companion object {
        
        var app: App? = null
        val gson: Gson
            get() {
                return GsonBuilder().setLenient().create()
            }
    }
    
    override fun onCreate() {
        super.onCreate()
        init()
        applySavedLocale()
    }
    
    private fun init() {
        app = this@App
    }
    
    private fun applySavedLocale() {
        LanguageManager.applySavedLocale(this)
    }
}