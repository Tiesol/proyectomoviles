package com.ami.fixealopofabo.model

data class Appointment(
    val id: Int,
    val workerName: String,
    val categoryName: String,
    val dateTime: String,
    val status: String,
    val workerUserId: Int,
    val statusCode: Int,
    val workerPhoto: String?
)
