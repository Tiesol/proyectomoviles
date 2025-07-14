package com.ami.fixealopofabo.model

data class RegisterResponse(
    val id: Int,
    val name: String,
    val email: String,
    val profile: ProfileResponse
)

