package com.example.agribridge.model

import java.io.Serializable

data class SignUpResponse(
    val _id: String? = null,
    val first_name: String? = null,
    val last_name: String? = null,
    val email: String? = null,
    val phone_number: String? = null,
    val state: String? = null,
    val district: String? = null,
    val is_active: Boolean? = null,
    val created_at: String? = null
) : Serializable
