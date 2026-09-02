package com.example.agribridge.utils

import android.content.Context

object LanguageManager {

    /**
     * Set the application language, persist it in SharedPreferences,
     * and update the application locale.
     */
    fun setLanguage(context: Context, languageCode: String) {
        val preference = Preference(context)
        preference.setBooleanPreference(Constant.Preference.IS_LANGUAGE_SELECTED, true)
        preference.setStringPreference(Constant.Preference.SELECTED_LANGUAGE, languageCode)
        LocaleHelper.setLocale(languageCode)
    }

    /**
     * Retrieve the currently selected language code.
     * Defaults to "en" (English) if no language has been selected yet.
     */
    fun getLanguage(context: Context): String {
        val preference = Preference(context)
        return preference.getStringPreference(Constant.Preference.SELECTED_LANGUAGE).ifEmpty { "en" }
    }

    /**
     * Check whether the user has already selected a language.
     */
    fun isLanguageSelected(context: Context): Boolean {
        val preference = Preference(context)
        return preference.getBooleanPreference(Constant.Preference.IS_LANGUAGE_SELECTED, false) ?: false
    }

    /**
     * Apply the saved locale during app startup or activity initialization.
     */
    fun applySavedLocale(context: Context) {
        if (isLanguageSelected(context)) {
            val savedLang = getLanguage(context)
            LocaleHelper.setLocale(savedLang)
        }
    }

    /**
     * Struct representing supported languages, making the application
     * easily scalable for future languages.
     */
    data class SupportedLanguage(
        val code: String,
        val displayName: String
    )

    /**
     * List of all supported languages in the application.
     */
    val supportedLanguages = listOf(
        SupportedLanguage("en", "English"),
        SupportedLanguage("hi", "Hindi"),
        SupportedLanguage("kn", "Kannada"),
        SupportedLanguage("mr", "Marathi")
    )
}
