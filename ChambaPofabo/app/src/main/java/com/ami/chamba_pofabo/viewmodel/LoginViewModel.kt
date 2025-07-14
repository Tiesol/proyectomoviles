package com.ami.chamba_pofabo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ami.chamba_pofabo.model.AuthResponse
import com.ami.chamba_pofabo.repository.ChambaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _loginResult = MutableLiveData<Result<Pair<String, Int>>>()
    val loginResult: LiveData<Result<Pair<String, Int>>> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response: AuthResponse = ChambaRepository.login(email, password)
                _loginResult.postValue(Result.success(Pair(response.access_token, response.user_id)))
            } catch (e: Exception) {
                _loginResult.postValue(Result.failure(e))
            }
        }
    }
}
