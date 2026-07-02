package com.example.agribridge.model

import java.io.Serializable

data class SignUpUserResponse(
    val mobile: String? = null,
    val otp: String? = null,
    val requires_otp: Boolean? = null,
) : Serializable