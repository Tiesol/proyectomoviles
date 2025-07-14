package com.ami.fixealopofabo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ami.fixealopofabo.model.Category
import com.ami.fixealopofabo.repository.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryListViewModel : ViewModel() {

    private val _categoryList = MutableLiveData<Result<List<Category>>>()
    val categoryList : LiveData<Result<List<Category>>> = _categoryList

    fun loadCategories(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val categories = CategoryRepository.getCategories(token)
                _categoryList.postValue(Result.success(categories))
            } catch (e: Exception) {
                _categoryList.postValue(
                    Result.failure(Exception("Error al obtener categorías: ${e.message}"))
                )
            }
        }
    }
}
