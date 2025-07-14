package com.ami.fixealopofabo.api

import com.ami.fixealopofabo.model.AppointmentConfirmRequest
import com.ami.fixealopofabo.model.AppointmentItemResponse
import com.ami.fixealopofabo.model.AppointmentRequest
import com.ami.fixealopofabo.model.AppointmentResponse
import com.ami.fixealopofabo.model.ChatMessage
import com.ami.fixealopofabo.model.ReviewRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface AppointmentApi {
    @POST("appointments")
    suspend fun createAppointment(@Body body: AppointmentRequest): AppointmentResponse
    @GET("appointments")
    suspend fun getAppointments(): List<AppointmentItemResponse>
    @POST("appointments/{id}/make")
    suspend fun confirmAppointment(
        @Path("id") appointmentId: Int,
        @Body body: AppointmentConfirmRequest
    ): Unit
    @GET("appointments/{id}/chats")
    suspend fun getAppointmentChats(@Path("id") appointmentId: Int): List<ChatMessage>
    @POST("appointments/{id}/review")
    suspend fun sendReview(
        @Path("id") appointmentId: Int,
        @Body request: ReviewRequest
    ): Response<Void>
}