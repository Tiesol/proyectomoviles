package com.ami.chamba_pofabo.repository

import android.util.Log
import com.ami.chamba_pofabo.model.*
import com.ami.chamba_pofabo.retrofit.RetrofitRepository

object AppointmentRepository {

    suspend fun getAppointments(token: String): List<Appointment> {
        val api = RetrofitRepository.getAppointmentApi(token)
        val raw = api.getAppointments()

        return raw.mapNotNull { item ->
            val clientProfile = item.client?.profile ?: return@mapNotNull null

            Log.d("AppointmentRepository", "=== PROCESANDO CITA ${item.id} ===")
            Log.d("AppointmentRepository", "Raw latitude: '${item.latitude}'")
            Log.d("AppointmentRepository", "Raw longitude: '${item.longitude}'")

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
            val finalLatitude = item.latitude ?: "0.0"
            val finalLongitude = item.longitude ?: "0.0"
            Log.d("AppointmentRepository", "Final latitude: '$finalLatitude'")
            Log.d("AppointmentRepository", "Final longitude: '$finalLongitude'")
            Appointment(
                id = item.id,
                clientName = "${clientProfile.name} ${clientProfile.lastName}",
                categoryName = item.category?.name ?: "N/D",
                dateTime = "${item.date ?: ""} ${item.time ?: ""}".trim(),
                status = status,
                userId = item.client?.id ?: -1,
                workerId = item.worker?.id ?: -1,
                statusCode = statusCode,
                latitude = finalLatitude,
                longitude = finalLongitude,
                categoryId = item.categoryId
            )
        }
    }
    suspend fun confirmAppointment(
        token: String,
        appointmentId: Int,
        workerId: Int,
        categoryId: Int
    ): Boolean {
        return try {
            val api = RetrofitRepository.getAppointmentApi(token)
            val request = AppointmentConfirmRequest(
                workerId = workerId.toString(),
                categoryId = categoryId
            )

            Log.d("AppointmentRepository", "Confirmando cita $appointmentId con workerId=$workerId, categoryId=$categoryId")

            val response = api.confirmAppointment(appointmentId, request)

            if (response.isSuccessful) {
                Log.d("AppointmentRepository", "Cita confirmada exitosamente")
                true
            } else {
                Log.e("AppointmentRepository", "Error confirmando cita: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e("AppointmentRepository", "Excepción confirmando cita: ${e.message}")
            false
        }
    }

    suspend fun finalizeAppointment(
        token: String,
        appointmentId: Int,
        workerId: Int,
        categoryId: Int
    ): Boolean {
        return try {
            val api = RetrofitRepository.getAppointmentApi(token)
            val request = AppointmentConfirmRequest(
                workerId = workerId.toString(),
                categoryId = categoryId
            )

            Log.d(
                "AppointmentRepository",
                "Finalizando cita $appointmentId con workerId=$workerId, categoryId=$categoryId"
            )

            val response = api.finalizeAppointment(appointmentId, request)

            if (response.isSuccessful) {
                Log.d("AppointmentRepository", "Cita finalizada exitosamente")
                true
            } else {
                Log.e("AppointmentRepository", "Error finalizando cita: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e("AppointmentRepository", "Excepción finalizando cita: ${e.message}")
            false
        }
    }
}
