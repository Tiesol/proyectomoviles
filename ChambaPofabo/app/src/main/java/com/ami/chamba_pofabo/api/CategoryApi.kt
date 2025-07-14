package com.ami.chamba_pofabo.api

import com.ami.chamba_pofabo.model.Category
import com.ami.chamba_pofabo.model.CategoryCreateRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CategoryApi {
    @GET("categories")
    suspend fun getCategories(): List<Category>

    @POST("categories")
    suspend fun createCategory(@Body request: CategoryCreateRequest): Category
}
