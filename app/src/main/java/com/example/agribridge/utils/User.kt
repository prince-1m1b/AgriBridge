package com.example.agribridge.utils

import java.io.Serializable

data class User(
    var _id: String? = null,
    var fullName: String? = null,
    var slug: String? = null,
    var email: String? = null,
    var mobile: String? = null,
    var role: String? = null,
    var from: String? = null,
    var access: String? = null,
    var deviceType: String? = null,
    var loginCode: String? = null,
    var userTimezone: String? = null,
    var permissions: ArrayList<String>? = null,
    var userTerritories: ArrayList<String>? = null
) : Serializable