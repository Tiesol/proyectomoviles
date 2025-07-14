// viewmodel/MyAppointmentsViewModel.kt
package com.ami.fixealopofabo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ami.fixealopofabo.model.Appointment
import com.ami.fixealopofabo.model.ChatMessage
import com.ami.fixealopofabo.repository.AppointmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyAppointmentsViewModel : ViewModel() {

    private val _appointments = MutableLiveData<Result<List<Appointment>>>()
    val appointments: LiveData<Result<List<Appointment>>> = _appointments
    private val _appointmentChats = MutableLiveData<Result<List<ChatMessage>>>()
    val appointmentChats: LiveData<Result<List<ChatMessage>>> = _appointmentChats
    fun loadAppointments(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val list = AppointmentRepository.getAppointments(token)
                _appointments.postValue(Result.success(list))
            } catch (e: Exception) {
                _appointments.postValue(Result.failure(e))
            }
        }
    }

    fun getAppointmentChats(token: String, appointmentId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val chats = AppointmentRepository.getAppointmentChats(token, appointmentId)
                _appointmentChats.postValue(Result.success(chats))
            } catch (e: Exception) {
                _appointmentChats.postValue(Result.failure(e))
            }
        }
    }
}
