package com.ami.fixealopofabo.model

import com.google.gson.annotations.SerializedName

data class ReviewRequest(
    val rating: Int,
    val comment: String,
    @SerializedName("is_done") val isDone: Boolean
)