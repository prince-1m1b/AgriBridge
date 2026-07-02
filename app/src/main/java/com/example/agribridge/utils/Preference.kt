package com.example.agribridge.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.agribridge.utils.Constant.Preference.PREFERENCE_NAME
import com.example.agribridge.utils.Constant.Preference.USER
import com.example.agribridge.utils.Constant.Preference.VERSION

class Preference(private val context: Context?) {
    
    private val sharedPreferences: SharedPreferences? = context?.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor? = sharedPreferences?.edit()
    
    private val sharedPreferencesForUser: SharedPreferences? = context?.getSharedPreferences(USER, Context.MODE_PRIVATE)
    private val editorUser: SharedPreferences.Editor? = sharedPreferencesForUser?.edit()
    
    //SET STRING PREFERENCE
    fun setStringPreference(key: String?, value: String?) {
        editor?.putString(key, value)
        editor?.commit()
        editor?.apply()
    }
    
    //GET STRING PREFERENCE
    fun getStringPreference(key: String?): String {
        return sharedPreferences?.getString(key, "").toString()
    }
    
    //SET STRING PREFERENCE
    fun setStringPreferenceForUser(key: String?, value: String?) {
        editorUser?.putString(key, value)
        editorUser?.commit()
        editorUser?.apply()
    }
    
    
    //SET STRING PREFERENCE
    fun setIntPreferenceForUser(key: String?, value: Int?) {
        editorUser?.putString(key, "$value")
        editorUser?.commit()
        editorUser?.apply()
    }
    
    //GET STRING PREFERENCE
    fun getStringPreferenceForUser(key: String?): String {
        return sharedPreferencesForUser?.getString(key, "").toString()
    }
    
    //SET BOOLEAN PREFERENCE
    fun setBooleanPreference(key: String?, value: Boolean) {
        editor?.putBoolean(key, value)
        editor?.commit()
        editor?.apply()
    }

    //SET BOOLEAN PREFERENCE FOR USER
    fun setBooleanPreferenceForUser(key: String?, value: Boolean) {
        editorUser?.putBoolean(key, value)
        editorUser?.commit()
        editorUser?.apply()
    }
    
    //GET BOOLEAN PREFERENCE
    fun getBooleanPreference(key: String?, default: Boolean): Boolean? {
        return sharedPreferences?.getBoolean(key, default)
    }

    //GET BOOLEAN PREFERENCE FOR USER
    fun getBooleanPreferenceForUser(key: String?, default: Boolean): Boolean? {
        return sharedPreferencesForUser?.getBoolean(key, default)
    }
    
    fun getVersionPreference(context: Context): SharedPreferences {
        return context.getSharedPreferences(VERSION, Context.MODE_PRIVATE)
    }
    
    //REMOVE PREFERENCE BY KEY
    fun removePreference() {
        editor?.remove(PREFERENCE_NAME)
        editor?.commit()
        editor?.apply()
    }
    
    fun removePreferenceFileForUser() {
        context?.getSharedPreferences(USER, Context.MODE_PRIVATE)?.edit()?.clear()?.apply()
    }
    
    //REMOVE USER PREFERENCE
    fun removeUserPreference() {
        editor?.remove(USER)
        editor?.commit()
        editor?.apply()
    }
}