package com.ami.fixealopofabo.model

import android.util.Log
import com.google.gson.annotations.SerializedName

data class WorkerResponse(
    val id: Int,

    @SerializedName("picture_url")
    val pictureUrl: String?,

    @SerializedName("average_rating")
    val averageRating: Double?,

    val user: UserResponse?
) {
    fun toWorkerOrNull(): Worker? {
        val u = user ?: run {
            Log.w("WorkerResponse", "user = null")
            return null
        }

        val profile = u.profile ?: run {
            Log.w("WorkerResponse", "profile = null")
            return null
        }

        return Worker(
            id = id,
            name = profile.name,
            lastName = profile.lastName,
            profilePicture = pictureUrl?.takeIf { it != "null" },
            rating = averageRating ?: 0.0,
            userId = u.id
        )
    }
}
