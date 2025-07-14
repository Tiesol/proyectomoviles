// viewmodel/MyAppointmentsViewModel.kt
package com.ami.chamba_pofabo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ami.chamba_pofabo.model.Appointment
import com.ami.chamba_pofabo.model.ChatMessage
import com.ami.chamba_pofabo.repository.AppointmentRepository
import com.ami.chamba_pofabo.repository.WorkerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyAppointmentsViewModel : ViewModel() {

    private val _appointments = MutableLiveData<Result<List<Appointment>>>()
    val appointments: LiveData<Result<List<Appointment>>> = _appointments

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
    private val _profileImageUrl = MutableLiveData<Result<String>>()
    val profileImageUrl: LiveData<Result<String>> = _profileImageUrl

    fun loadProfileImage(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = WorkerRepository.getWorkerPictureUrl(token)
            _profileImageUrl.postValue(result)
        }
    }
}
