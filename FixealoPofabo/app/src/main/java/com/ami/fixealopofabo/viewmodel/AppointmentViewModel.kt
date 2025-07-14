package com.ami.fixealopofabo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ami.fixealopofabo.repository.AppointmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppointmentViewModel : ViewModel() {

    private val _confirmationResult = MutableLiveData<Result<Unit>>()
    val confirmationResult: LiveData<Result<Unit>> = _confirmationResult
    fun confirmAppointment(
        token: String,
        appointmentId: Int,
        date: String,
        time: String,
        latitude: String,
        longitude: String
    ) {
        android.util.Log.d("ConfirmacionCita", "Datos a enviar:")
        android.util.Log.d("ConfirmacionCita", "Token: ${token.take(15)}...")
        android.util.Log.d("ConfirmacionCita", "appointmentId: $appointmentId")
        android.util.Log.d("ConfirmacionCita", "date: $date")
        android.util.Log.d("ConfirmacionCita", "time: $time")
        android.util.Log.d("ConfirmacionCita", "latitude: $latitude")
        android.util.Log.d("ConfirmacionCita", "longitude: $longitude")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                android.util.Log.d("ConfirmacionCita", "Llamando al repositorio...")

                val latDouble = latitude.toDoubleOrNull()
                val longDouble = longitude.toDoubleOrNull()

                if (latDouble == null || longDouble == null) {
                    android.util.Log.e("ConfirmacionCita", "ERROR: Coordenadas inválidas - lat: $latitude, long: $longitude")
                    _confirmationResult.postValue(Result.failure(Exception("Coordenadas inválidas")))
                    return@launch
                }

                if (!date.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                    android.util.Log.e("ConfirmacionCita", "ERROR: Formato de fecha inválido: $date")
                    _confirmationResult.postValue(Result.failure(Exception("Formato de fecha inválido")))
                    return@launch
                }

                if (!time.matches(Regex("\\d{2}:\\d{2}"))) {
                    android.util.Log.e("ConfirmacionCita", "ERROR: Formato de hora inválido: $time")
                    _confirmationResult.postValue(Result.failure(Exception("Formato de hora inválido")))
                    return@launch
                }

                AppointmentRepository.confirmAppointment(
                    token, appointmentId, date, time, latitude, longitude
                )
                android.util.Log.d("ConfirmacionCita", "Cita confirmada exitosamente")
                _confirmationResult.postValue(Result.success(Unit))
            } catch (e: Exception) {
                android.util.Log.e("ConfirmacionCita", "ERROR en confirmación: ${e.message}")
                android.util.Log.e("ConfirmacionCita", "Causa: ${e.cause}")
                e.printStackTrace()
                _confirmationResult.postValue(Result.failure(e))
            }
        }
    }
}
