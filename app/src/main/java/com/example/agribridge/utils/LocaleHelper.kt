package com.example.agribridge.utils

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LocaleHelper {
    fun setLocale(languageCode: String) {
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    fun getLocale(): String {
        return AppCompatDelegate.getApplicationLocales().toLanguageTags()
    }
}
