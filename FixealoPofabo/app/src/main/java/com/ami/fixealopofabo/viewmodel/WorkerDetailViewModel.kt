package com.ami.fixealopofabo.viewmodel

import androidx.lifecycle.*
import com.ami.fixealopofabo.model.Review
import com.ami.fixealopofabo.model.Worker
import com.ami.fixealopofabo.repository.AppointmentRepository
import com.ami.fixealopofabo.repository.WorkerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WorkerDetailViewModel : ViewModel() {

    private val _worker = MutableLiveData<Result<Worker>>()
    val worker: LiveData<Result<Worker>> = _worker

    private val _createResult = MutableLiveData<Result<Int>>()
    val createResult: LiveData<Result<Int>> = _createResult

    private val _reviews = MutableLiveData<Result<List<Review>>>()
    val reviews: LiveData<Result<List<Review>>> = _reviews

    var receiverId: Int? = null
        private set

    fun loadWorker(token: String, workerId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val raw = WorkerRepository.getWorkerRawById(token, workerId)
                val user = raw.user
                if (user == null) {
                    _worker.postValue(Result.failure(Exception("Trabajador inválido: user=null")))
                    return@launch
                }

                receiverId = user.id
                val w = raw.toWorkerOrNull()
                if (w != null) {
                    _worker.postValue(Result.success(w))

                } else {
                    _worker.postValue(Result.failure(Exception("Trabajador sin datos válidos")))
                }

            } catch (e: Exception) {
                _worker.postValue(Result.failure(e))
            }
        }
    }

    fun loadWorkerReviews(token: String, workerId: Int) {
        val currentWorker = _worker.value?.getOrNull()
        if (currentWorker != null) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val reviews = WorkerRepository.getWorkerReviews(token, workerId)
                    _reviews.postValue(Result.success(reviews))
                } catch (e: Exception) {
                    _reviews.postValue(Result.failure(e))
                }
            }
        } else {
            _reviews.postValue(Result.failure(Exception("Trabajador no cargado o inválido")))
        }
    }

    fun createAppointment(token: String, workerId: Int, categoryId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val id = AppointmentRepository.createAppointment(token, workerId, categoryId)
                _createResult.postValue(Result.success(id))
            } catch (e: Exception) {
                _createResult.postValue(Result.failure(e))
            }
        }
    }
}