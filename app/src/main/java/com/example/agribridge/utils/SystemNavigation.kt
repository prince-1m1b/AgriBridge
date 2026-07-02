package com.example.agribridge.utils

import android.app.Activity
import android.provider.Settings

enum class SystemNavigation {
    THREE_BUTTON,
    TWO_BUTTON,
    GESTURE;
    
    companion object {
        fun create(activity: Activity): SystemNavigation {
            return try {
                val valueFromSettings = Settings.Secure.getInt(
                    activity.contentResolver,
                    "navigation_mode"
                )
                
                return when (valueFromSettings) {
                    0 -> THREE_BUTTON   // 3-button nav (classic Back, Home, Recents)
                    1 -> TWO_BUTTON     // 2-button nav (Back + pill)
                    2 -> GESTURE        // Fully gesture-based navigation
                    else -> THREE_BUTTON
                }
            } catch (e: Exception) {
                THREE_BUTTON
            }
        }
    }
}