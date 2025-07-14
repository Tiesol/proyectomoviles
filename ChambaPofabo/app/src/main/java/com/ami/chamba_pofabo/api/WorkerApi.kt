package com.ami.chamba_pofabo.api

import com.ami.chamba_pofabo.model.CategoriesRequest
import com.ami.chamba_pofabo.model.MeResponse
import com.ami.chamba_pofabo.model.UploadResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface WorkerApi {
    @GET("me")
    suspend fun getMe(): MeResponse

    @Multipart
    @POST("workers/profile-picture")
    suspend fun uploadProfilePicture(
        @Part picture: MultipartBody.Part
    ): Response<UploadResponse>
    @POST("workers/{workerId}/categories")
    suspend fun updateWorkerCategories(
        @Path("workerId") workerId: Int,
        @Body categoriesRequest: CategoriesRequest
    ): Response<UploadResponse>
}