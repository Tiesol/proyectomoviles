package com.ami.fixealopofabo.model

import com.google.gson.annotations.SerializedName

data class SendMessageRequest(
    @SerializedName("message")
    val message: String,

    @SerializedName("receiver_id")
    val receiverId: Int
)
