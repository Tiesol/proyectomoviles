package com.ami.chamba_pofabo.model

import com.google.gson.annotations.SerializedName

data class AppointmentItemResponse(
    val id: Int,
    @SerializedName("worker_id") val workerId: Int,
    @SerializedName("category_selected_id") val categoryId: Int,
    val status: String?,
    @SerializedName("appointment_date") val date: String?,
    @SerializedName("appointment_time") val time: String?,
    @SerializedName("latitude") val latitude: String?,
    @SerializedName("longitude") val longitude: String?,
    val worker: WorkerMini?,
    val client: ClientMini?,
    val category: CategoryMini?
)

data class WorkerMini(
    val id: Int,
    val user: UserWrapper?
)

data class UserWrapper(
    val id: Int,
    val name: String,
    val email: String,
    val profile: ProfileWrapper?
)
data class ClientMini(
    val id: Int,
    val name: String,
    val email: String,
    val profile: ProfileWrapper?
)

data class ProfileWrapper(
    val name: String,
    @SerializedName("last_name") val lastName: String
)

data class CategoryMini(
    val id: Int,
    val name: String
)
