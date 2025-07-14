package com.ami.fixealopofabo.model

data class Review(
    val id: Int,
    val comment: String,
    val rating: Double,
    val user: ReviewUser
)

data class ReviewUser(
    val id: Int,
    val name: String,
    val lastName: String? = null
)