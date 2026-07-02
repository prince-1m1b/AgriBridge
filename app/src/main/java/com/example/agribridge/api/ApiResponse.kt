package com.example.agribridge.api

import java.io.Serializable

data class ApiResponse(
    var statusCode: Int? = null,
    var type: String? = null,
    var message: String? = null,
    var data: Any? = null,
) : Serializable