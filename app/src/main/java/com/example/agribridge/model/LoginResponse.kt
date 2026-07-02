package com.example.agribridge.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LoginResponse(
    val access_token: String? = null, val token_type: String? = null, val user: UserData? = null
) : Serializable

