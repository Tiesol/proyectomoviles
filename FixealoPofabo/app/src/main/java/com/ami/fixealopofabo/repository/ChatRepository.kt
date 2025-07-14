package com.ami.fixealopofabo.repository

import android.content.Context
import android.util.Log
import com.ami.fixealopofabo.model.Message
import com.ami.fixealopofabo.model.SendMessageRequest
import com.ami.fixealopofabo.retrofit.RetrofitRepository
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import retrofit2.Response

object ChatRepository {

    private fun getToken(ctx: Context): String? {
        return ctx.getSharedPreferences("session", Context.MODE_PRIVATE)
            .getString("token", null)
    }

    suspend fun getMessages(ctx: Context, appointmentId: Int): Response<List<Message>> {
        val token = getToken(ctx)
        if (token.isNullOrEmpty()) {
            Log.e("ChatRepository", "Token no encontrado")
            return Response.error(
                401,
                ResponseBody.create("text/plain".toMediaTypeOrNull(), "Unauthorized")
            )
        }

        val api = RetrofitRepository.getChatApi(token)
        return api.getMessages(appointmentId)
    }

    suspend fun sendMessage(ctx: Context, appointmentId: Int, text: String, receiverId: Int): Response<Message> {
        val token = getToken(ctx) ?: return Response.error(401, okhttp3.ResponseBody.create(null, "Unauthorized"))
        val api = RetrofitRepository.getChatApi(token)
        val body = SendMessageRequest(text, receiverId)

        Log.d("ChatRepository", "JSON ENVIADO: ${Gson().toJson(body)}")

        return api.sendMessage(appointmentId, body)
    }

}
