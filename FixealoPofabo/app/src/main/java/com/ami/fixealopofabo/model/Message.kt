package com.ami.fixealopofabo.model

import com.google.gson.annotations.SerializedName

data class Message(
    val id: Int,
    val message: String,
    val sender: SimpleUser,
    val receiver: SimpleUser,
    @SerializedName("created_at") val createdAt: String
)

data class SimpleUser(
    val id: Int,
    val name: String,
    @SerializedName("last_name") val lastName: String
)
