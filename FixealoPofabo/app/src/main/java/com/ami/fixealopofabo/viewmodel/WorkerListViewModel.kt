package com.ami.fixealopofabo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ami.fixealopofabo.model.Worker
import com.ami.fixealopofabo.repository.WorkerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WorkerListViewModel : ViewModel() {

    private val _workerList = MutableLiveData<Result<List<Worker>>>()
    val workerList: LiveData<Result<List<Worker>>> = _workerList

    fun loadWorkers(token: String, categoryId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val workers = WorkerRepository.getWorkersByCategory(token, categoryId)
                _workerList.postValue(Result.success(workers))
            } catch (e: Exception) {
                _workerList.postValue(Result.failure(e))
            }
        }
    }
}
