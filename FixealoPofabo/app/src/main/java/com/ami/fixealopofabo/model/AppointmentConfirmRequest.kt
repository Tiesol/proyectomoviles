package com.ami.fixealopofabo.model

import com.google.gson.annotations.SerializedName

data class AppointmentConfirmRequest(
    @SerializedName("appointment_date") val date: String,
    @SerializedName("appointment_time") val time: String,
    @SerializedName("latitude") val latitude: String,
    @SerializedName("longitude") val longitude: String
)