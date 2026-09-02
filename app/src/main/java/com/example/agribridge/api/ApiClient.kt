package com.example.agribridge.api

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.agribridge.BuildConfig
import com.example.agribridge.App.Companion.gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.GINGERBREAD)
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
        Retrofit
            .Builder()
            .baseUrl("http://192.168.1.11:8000/api/")
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

}