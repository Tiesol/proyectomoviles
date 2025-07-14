package com.ami.fixealopofabo.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ami.fixealopofabo.repository.AppointmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReviewViewModel : ViewModel() {

    private val _reviewResult = MutableLiveData<Result<Boolean>>()
    val reviewResult: LiveData<Result<Boolean>> = _reviewResult

    fun sendReview(
        token: String,
        appointmentId: Int,
        rating: Int,
        comment: String,
        isDone: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val success = AppointmentRepository.sendReview(
                    token, appointmentId, rating, comment, isDone
                )

                if (success) {
                    _reviewResult.postValue(Result.success(true))
                } else {
                    _reviewResult.postValue(Result.failure(Exception("Error enviando review")))
                }
            } catch (e: Exception) {
                _reviewResult.postValue(Result.failure(e))
            }
        }
    }
}