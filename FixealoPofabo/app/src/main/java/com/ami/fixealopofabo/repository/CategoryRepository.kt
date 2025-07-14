package com.ami.fixealopofabo.repository

import com.ami.fixealopofabo.model.Category
import com.ami.fixealopofabo.retrofit.RetrofitRepository

object CategoryRepository {

    suspend fun getCategories(token: String): List<Category> {
        val api = RetrofitRepository.getCategoryApi(token)
        return api.getCategories()
    }
}
