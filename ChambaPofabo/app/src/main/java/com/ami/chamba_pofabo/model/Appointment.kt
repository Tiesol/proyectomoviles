package com.ami.chamba_pofabo.model

data class Appointment(
    val id: Int,
    val clientName: String,
    val categoryName: String,
    val dateTime: String,
    val status: String,
    val userId: Int,
    val workerId: Int,    //
    val statusCode: Int,
    val latitude: String,
    val longitude: String,
    val categoryId: Int
)
