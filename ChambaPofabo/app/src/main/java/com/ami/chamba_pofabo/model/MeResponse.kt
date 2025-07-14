package com.ami.chamba_pofabo.model

data class MeResponse(
    val id: Int,
    val name: String,
    val email: String,
    val profile: Profile,
    val worker: WorkerInfo
)

data class Profile(
    val id: Int,
    val name: String,
    val last_name: String,
    val type: Int
)

data class WorkerInfo(
    val id: Int,
    val user_id: Int,
    val picture_url: String?,
    val average_rating: Float,
    val reviews_count: Int
)