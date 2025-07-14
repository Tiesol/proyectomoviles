package com.ami.fixealopofabo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ami.fixealopofabo.model.AuthResponse
import com.ami.fixealopofabo.repository.ClientRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _loginResult = MutableLiveData<Result<Pair<String, Int>>>()
    val loginResult: LiveData<Result<Pair<String, Int>>> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response: AuthResponse = ClientRepository.login(email, password)
                _loginResult.postValue(Result.success(Pair(response.access_token, response.user_id)))
            } catch (e: Exception) {
                _loginResult.postValue(Result.failure(e))
            }
        }
    }
}
