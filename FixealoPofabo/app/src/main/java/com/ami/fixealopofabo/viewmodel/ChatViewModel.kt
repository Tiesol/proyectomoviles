package com.ami.fixealopofabo.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.ami.fixealopofabo.model.Message
import com.ami.fixealopofabo.repository.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private var pollingJob: Job? = null

    fun startPolling(context: Context, appointmentId: Int) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                fetchMessages(context, appointmentId)
                delay(30_000)
            }
        }
    }

    fun stopPolling() {
        pollingJob?.cancel()
        Log.d("ChatViewModel", "Polling detenido")
    }

    fun fetchMessages(context: Context, appointmentId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val res = ChatRepository.getMessages(context, appointmentId)
                if (res.isSuccessful) {
                    val msgs = res.body()
                    _messages.postValue(msgs ?: emptyList())
                } else {
                    _error.postValue("Error al obtener mensajes")
                }
            } catch (e: Exception) {
                _error.postValue("Error de conexión")
                Log.e("ChatViewModel", "Excepción en fetchMessages", e)
            }
        }
    }

    fun sendMessage(context: Context, appointmentId: Int, text: String, receiverId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (text.isBlank()) {
                    _error.postValue("Mensaje vacío")
                    return@launch
                }

                val res = ChatRepository.sendMessage(context, appointmentId, text, receiverId)
                if (res.isSuccessful) {
                    fetchMessages(context, appointmentId)
                } else {
                    _error.postValue("Error al enviar mensaje")
                    Log.e("ChatViewModel", "sendMessage falló: ${res.code()} ${res.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Error de red al enviar mensaje")
                Log.e("ChatViewModel", "Excepción en sendMessage", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopPolling()
    }
}
