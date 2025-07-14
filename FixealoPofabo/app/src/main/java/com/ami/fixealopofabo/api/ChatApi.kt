package com.ami.fixealopofabo.api

import com.ami.fixealopofabo.model.Message
import com.ami.fixealopofabo.model.SendMessageRequest
import retrofit2.Response
import retrofit2.http.*

interface ChatApi {

    @GET("appointments/{id}/chats")
    suspend fun getMessages(@Path("id") appointmentId: Int): Response<List<Message>>

    @POST("appointments/{id}/chats")
    suspend fun sendMessage(
        @Path("id") appointmentId: Int,
        @Body request: SendMessageRequest
    ): Response<Message>
}
