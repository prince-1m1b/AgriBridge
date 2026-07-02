package com.example.agribridge.utils

import android.app.Activity
import android.graphics.Rect
import android.view.ViewGroup

object KeyboardUtil {
    
    fun assistActivity(activity: Activity) {
        val content = (activity.findViewById<ViewGroup>(android.R.id.content)).getChildAt(0)
        content.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            content.getWindowVisibleDisplayFrame(r)
            val screenHeight = content.rootView.height
            val keypadHeight = screenHeight - r.bottom
            
            if (keypadHeight > screenHeight * 0.15) { // Keyboard is opened
                content.setPadding(0, 0, 0, keypadHeight)
            } else {
                content.setPadding(0, 0, 0, 0)
            }
        }
    }
}