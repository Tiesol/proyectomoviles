package com.ami.fixealopofabo.repository

import android.util.Log
import com.ami.fixealopofabo.model.Review
import com.ami.fixealopofabo.model.Worker
import com.ami.fixealopofabo.model.WorkerResponse
import com.ami.fixealopofabo.retrofit.RetrofitRepository

object WorkerRepository {

    suspend fun getWorkersByCategory(token: String, categoryId: Int): List<Worker> {
        val api = RetrofitRepository.getWorkerApi(token)
        val raw: List<WorkerResponse> = api.getWorkersByCategory(categoryId)

        Log.d("WorkerRepo", "Lista recibida: ${raw.size}")
        return raw.mapNotNull { it.toWorkerOrNull() }
    }

    suspend fun getWorkerById(token: String, workerId: Int): Worker? {
        val api = RetrofitRepository.getWorkerApi(token)
        val raw = api.getWorkerById(workerId)
        return raw.toWorkerOrNull()
    }

    suspend fun getWorkerRawById(token: String, workerId: Int): WorkerResponse {
        val api = RetrofitRepository.getWorkerApi(token)
        return api.getWorkerById(workerId)
    }
    suspend fun getWorkerReviews(token: String, workerId: Int): List<Review> {
        val api = RetrofitRepository.getWorkerApi(token)
        return api.getWorkerReviews(workerId)
    }
}
