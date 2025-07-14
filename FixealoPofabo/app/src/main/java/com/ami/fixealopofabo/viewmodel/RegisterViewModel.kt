package com.ami.fixealopofabo.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.ami.fixealopofabo.repository.ClientRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val _registerResult = MutableLiveData<Result<Unit>>()
    val registerResult : LiveData<Result<Unit>> = _registerResult

    fun register(name: String, lastName: String, email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ClientRepository.register(name, lastName, email, password)
                Log.d("RegistroDebug", "=== DATOS DE REGISTRO ===")
                Log.d("RegistroDebug", "Email: $email")
                Log.d("RegistroDebug", "Name: $name")
                _registerResult.postValue(Result.success(Unit))

            } catch (e: Exception) {
                _registerResult.postValue(Result.failure(e))
            }
        }
    }
}

