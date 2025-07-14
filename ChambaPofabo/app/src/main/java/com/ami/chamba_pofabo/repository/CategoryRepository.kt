package com.ami.chamba_pofabo.repository

import com.ami.chamba_pofabo.model.Category
import com.ami.chamba_pofabo.model.CategoryCreateRequest
import com.ami.chamba_pofabo.retrofit.RetrofitRepository

object CategoryRepository {

    suspend fun getCategories(token: String): List<Category> {
        val api = RetrofitRepository.getCategoryApi(token)
        return api.getCategories()
    }

    suspend fun createCategory(token: String, name: String): Category {
        val api = RetrofitRepository.getCategoryApi(token)
        val request = CategoryCreateRequest(name)
        return api.createCategory(request)
    }
}
