package com.ami.chamba_pofabo.api

import com.ami.chamba_pofabo.model.AppointmentConfirmRequest
import com.ami.chamba_pofabo.model.AppointmentItemResponse
import com.ami.chamba_pofabo.model.ChatMessage
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface AppointmentApi {

    @GET("appointments")
    suspend fun getAppointments(): List<AppointmentItemResponse>

    @GET("appointments/{id}/chats")
    suspend fun getAppointmentChats(@Path("id") appointmentId: Int): List<ChatMessage>

    @POST("appointments/{id}/confirm")
    suspend fun confirmAppointment(@Path("id") appointmentId: Int, @Body body: AppointmentConfirmRequest): Response<Void>

    @POST("appointments/{id}/finalize")
    suspend fun finalizeAppointment(@Path("id") appointmentId: Int, @Body body: AppointmentConfirmRequest): Response<Void>
}