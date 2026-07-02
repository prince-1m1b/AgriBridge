package com.example.agribridge.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProfileUpdateRequest(
    @SerializedName("state") val state: String? = null,
    @SerializedName("district") val district: String? = null,
    @SerializedName("password") val password: String? = null
)

data class ProfileUpdateResponseData(
    @SerializedName("id") val id: String? = null,
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("phone_number") val phoneNumber: String? = null,
    @SerializedName("state") val state: String? = null,
    @SerializedName("district") val district: String? = null,
    @SerializedName("is_active") val isActive: Boolean? = null,
    @SerializedName("created_at") val createdAt: String? = null
) : Serializable {
    fun toUserData(): UserData {
        return UserData(
            _id = id,
            first_name = firstName,
            last_name = lastName,
            email = email,
            phone_number = phoneNumber,
            state = state,
            district = district,
            is_active = isActive,
            created_at = createdAt
        )
    }
}

data class StatesResponseData(
    @SerializedName("states") val states: List<String>? = null
) : Serializable

data class DistrictsResponseData(
    @SerializedName("state") val state: String? = null,
    @SerializedName("districts") val districts: List<String>? = null
) : Serializable
