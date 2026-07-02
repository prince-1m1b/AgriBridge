package com.example.agribridge.utils

import android.text.InputFilter
import android.text.Spanned

class UppercaseNumericInputFilter(private val country: String) : InputFilter {
    
    private lateinit var regex: Regex
    
    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): String? {
        val newString = if (country == "Canada") {
            StringBuilder(dest).replace(dstart, dend, source.subSequence(start, end).toString()).toString()
            
        } else {
            StringBuilder(dest).replace(dstart, dend, source.subSequence(start, end).toString()).toString().replace("-", "")
        }
        
        regex = when (country) {
            "Canada" -> {
                "^[A-Z0-9]{0,6}\$".toRegex()
            }
            
            "United States" -> {
                "^[0-9]{0,9}\$".toRegex()
            }
            
            else -> {
                "".toRegex()
            }
        }
        
        val string = newString.replace(" ", "")
        if (!string.matches(regex)) {
            if (source.isEmpty()) {
                return dest.subSequence(dstart, dend).toString()
            }
            return ""
        }
        
        return null
    }
}