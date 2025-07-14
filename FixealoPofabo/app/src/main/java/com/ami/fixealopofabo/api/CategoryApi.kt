package com.ami.fixealopofabo.api

import com.ami.fixealopofabo.model.Category
import retrofit2.http.GET

interface CategoryApi {
    @GET("categories")
    suspend fun getCategories(): List<Category>
}
