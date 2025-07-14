package com.ami.fixealopofabo.repository

import android.util.Log
import com.ami.fixealopofabo.model.*
import com.ami.fixealopofabo.retrofit.RetrofitRepository

object AppointmentRepository {

    suspend fun createAppointment(
        token: String,
        workerId: Int,
        categoryId: Int
    ): Int {
        val api = RetrofitRepository.getAppointmentApi(token)
        val res = api.createAppointment(AppointmentRequest(workerId, categoryId))
        return res.id
    }

    suspend fun getAppointments(token: String): List<Appointment> {
        val api = RetrofitRepository.getAppointmentApi(token)
        val raw = api.getAppointments()

        return raw.mapNotNull { item ->
            val profile = item.worker?.user?.profile ?: return@mapNotNull null

            val statusCode = when (item.status?.trim()) {
                "0", "pending" -> 0
                "1", "requested" -> 1
                "2", "accepted" -> 2
                "3", "finished" -> 3
                else -> -1
            }

            val status = when (item.status?.trim()) {
                "0", "pending"   -> "Pendiente"
                "1", "requested" -> "Solicitada"
                "2", "accepted"  -> "Aceptada"
                "3", "finished"  -> "Completada"
                else             -> item.status ?: "—"
            }

            Appointment(
                id           = item.id,
                workerName   = "${profile.name} ${profile.lastName}",
                categoryName = item.category?.name ?: "N/D",
                dateTime     = "${item.date ?: ""} ${item.time ?: ""}".trim(),
                status       = status,
                workerUserId = item.worker?.user?.id ?: -1,
                statusCode   = statusCode,
                workerPhoto  = item.worker?.picture_url

            )
        }
    }

    suspend fun confirmAppointment(
        token: String,
        appointmentId: Int,
        date: String,
        time: String,
        latitude: String,
        longitude: String
    ) {
        try {
            val api = RetrofitRepository.getAppointmentApi(token)
            val request = AppointmentConfirmRequest(
                date = date,
                time = time,
                latitude = latitude,
                longitude = longitude
            )

            api.confirmAppointment(appointmentId, request)

        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string() ?: "No hay cuerpo de error disponible"
            throw Exception("Error en confirmación: $errorBody")
        } catch (e: Exception) {
            throw e
        }
    }
    suspend fun getAppointmentChats(token: String, appointmentId: Int): List<ChatMessage> {
        val api = RetrofitRepository.getAppointmentApi(token)
        return api.getAppointmentChats(appointmentId)
    }
    suspend fun sendReview(
        token: String,
        appointmentId: Int,
        rating: Int,
        comment: String,
        isDone: Boolean
    ): Boolean {
        return try {
            val api = RetrofitRepository.getAppointmentApi(token)
            val request = ReviewRequest(
                rating = rating,
                comment = comment,
                isDone = isDone
            )

            Log.d("ReviewRepository", "Enviando review: rating=$rating, comment='$comment', isDone=$isDone")

            val response = api.sendReview(appointmentId, request)

            if (response.isSuccessful) {
                Log.d("ReviewRepository", "Review enviado exitosamente")
                true
            } else {
                Log.e("ReviewRepository", "Error enviando review: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e("ReviewRepository", "Excepción enviando review: ${e.message}")
            false
        }
    }
}
