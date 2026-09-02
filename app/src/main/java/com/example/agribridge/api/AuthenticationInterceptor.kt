package com.example.agribridge.api

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.agribridge.utils.Constant.Api.AUTHORIZATION
import com.example.agribridge.utils.Constant.Preference.LOGIN_CODE
import com.example.agribridge.utils.Constant.Preference.TOKEN
import com.example.agribridge.utils.getUser
import com.example.agribridge.utils.logout
import com.example.agribridge.utils.prefManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthenticationInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val rawToken = context.prefManager().getStringPreferenceForUser(com.example.agribridge.utils.Constant.UserDetails.ACCESS_TOKEN)
        val token = if (rawToken.isNotEmpty()) "Bearer $rawToken" else ""
        val originalRequest =
            chain.request().newBuilder().header(AUTHORIZATION, token).header(LOGIN_CODE, "").build()
        val response = chain.proceed(originalRequest)

        return response
    }

}