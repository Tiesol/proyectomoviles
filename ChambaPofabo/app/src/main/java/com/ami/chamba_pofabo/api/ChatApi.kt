package com.ami.chamba_pofabo.api

import com.ami.chamba_pofabo.model.Message
import com.ami.chamba_pofabo.model.SendMessageRequest
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
