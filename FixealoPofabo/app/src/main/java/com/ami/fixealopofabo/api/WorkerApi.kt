package com.ami.fixealopofabo.api

import com.ami.fixealopofabo.model.Review
import com.ami.fixealopofabo.model.WorkerResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface WorkerApi {
    @GET("categories/{categoryId}/workers")
    suspend fun getWorkersByCategory(@Path("categoryId") categoryId: Int): List<WorkerResponse>
    @GET("workers/{id}")
    suspend fun getWorkerById(@Path("id") id: Int): WorkerResponse
    @GET("workers/{id}/reviews")
    suspend fun getWorkerReviews(@Path("id") workerId: Int): List<Review>
}
