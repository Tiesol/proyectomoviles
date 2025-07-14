package com.ami.chamba_pofabo.viewmodel

import androidx.lifecycle.*
import com.ami.chamba_pofabo.repository.ChambaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val _registerResult = MutableLiveData<Result<Unit>>()
    val registerResult : LiveData<Result<Unit>> = _registerResult

    fun register(name: String, lastName: String, email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ChambaRepository.register(name, lastName, email, password)
                _registerResult.postValue(Result.success(Unit))
            } catch (e: Exception) {
                _registerResult.postValue(Result.failure(e))
            }
        }
    }
}

