package com.example.agribridge.api

import android.content.Context
import com.example.agribridge.BuildConfig
import com.example.agribridge.App.Companion.gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient(context: Context) {

    private val loggingInterceptor = if (BuildConfig.DEBUG) {
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    } else {
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
    }

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient
            .Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(AuthenticationInterceptor(context))
            .connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .build()
    }

    val retrofit: Retrofit by lazy {
        val rawUrl = "http://10.16.75.112:6000/api"
        val baseUrl = if (rawUrl.endsWith("/")) rawUrl else "$rawUrl/"

        // "http://10.16.75.112:6000/api/"
        Retrofit
            .Builder()
            .baseUrl("http://192.168.1.30:6000/api/")
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

}