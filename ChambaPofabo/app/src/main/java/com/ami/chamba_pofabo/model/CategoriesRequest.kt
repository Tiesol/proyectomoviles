package com.ami.chamba_pofabo.model

data class CategoriesRequest(
    val categories: List<CategoryIdRequest>
)

data class CategoryIdRequest(
    val id: Int
)

data class UploadResponse(
    val success: Boolean,
    val message: String
)