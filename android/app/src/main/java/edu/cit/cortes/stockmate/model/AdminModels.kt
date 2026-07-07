package edu.cit.cortes.stockmate.model

import com.google.gson.annotations.SerializedName

data class CreateStaffRequest(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class UpdateStatusRequest(
    @SerializedName("isActive") val isActive: Boolean
)

data class UserResponse(
    @SerializedName("userId") val userId: Long,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: String,
    @SerializedName("isActive") val isActive: Boolean
)
