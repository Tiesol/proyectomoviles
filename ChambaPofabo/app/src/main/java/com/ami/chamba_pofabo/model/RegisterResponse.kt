package com.ami.chamba_pofabo.model

data class RegisterResponse(
    val id: Int,
    val name: String,
    val email: String,
    val profile: ProfileResponse
)

