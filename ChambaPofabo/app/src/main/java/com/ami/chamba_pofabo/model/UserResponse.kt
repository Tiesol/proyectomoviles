package com.ami.chamba_pofabo.model
import com.google.gson.annotations.SerializedName

data class UserResponse(
    val id: Int,
    val name: String,
    val email: String,
    val profile: ProfileResponse?
)
data class ProfileResponse(
    val id: Int,
    val name: String,
    @SerializedName("last_name")
    val lastName: String,
    val type: Int
)

