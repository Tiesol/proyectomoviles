package com.ami.chamba_pofabo.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ami.chamba_pofabo.model.Category
import com.ami.chamba_pofabo.model.MeResponse
import com.ami.chamba_pofabo.repository.CategoryRepository
import com.ami.chamba_pofabo.repository.WorkerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryListViewModel : ViewModel() {

    private val _categoryList = MutableLiveData<Result<List<Category>>>()
    val categoryList: LiveData<Result<List<Category>>> = _categoryList

    private val _createCategoryResult = MutableLiveData<Result<Category>>()
    val createCategoryResult: LiveData<Result<Category>> = _createCategoryResult

    private val _profileData = MutableLiveData<Result<MeResponse>>()
    val profileData: LiveData<Result<MeResponse>> = _profileData

    private val _uploadImageResult = MutableLiveData<Result<String>>()
    val uploadImageResult: LiveData<Result<String>> = _uploadImageResult

    private val _updateCategoriesResult = MutableLiveData<Result<String>>()
    val updateCategoriesResult: LiveData<Result<String>> = _updateCategoriesResult

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

    fun getMyProfile(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = WorkerRepository.getMyProfile(token)
            _profileData.postValue(result)
        }
    }

    fun uploadProfilePicture(token: String, imageUri: Uri, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = WorkerRepository.uploadProfilePicture(token, imageUri, context)
            _uploadImageResult.postValue(result)
        }
    }

    fun updateWorkerCategories(token: String, workerId: Int, categories: List<Category>) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = WorkerRepository.updateWorkerCategories(token, workerId, categories)
            _updateCategoriesResult.postValue(result)
        }
    }

    fun createCategory(token: String, categoryName: String) {
        viewModelScope.launch {
            try {
                val newCategory = CategoryRepository.createCategory(token, categoryName)
                _createCategoryResult.postValue(Result.success(newCategory))

                loadCategories(token)

            } catch (e: Exception) {
                _createCategoryResult.postValue(Result.failure(e))
            }
        }
    }
}