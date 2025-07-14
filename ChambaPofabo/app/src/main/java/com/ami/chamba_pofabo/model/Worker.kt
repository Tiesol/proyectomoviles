package com.ami.chamba_pofabo.model

data class Worker(
    val id: Int,
    val name: String,
    val lastName: String,
    val profilePicture: String?,
    val rating: Double,
    val userId: Int
)

