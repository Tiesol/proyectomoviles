// Crear AppointmentConfirmRequest.kt
package com.ami.chamba_pofabo.model

import com.google.gson.annotations.SerializedName

data class AppointmentConfirmRequest(
    @SerializedName("worker_id") val workerId: String,
    @SerializedName("category_selected_id") val categoryId: Int
)