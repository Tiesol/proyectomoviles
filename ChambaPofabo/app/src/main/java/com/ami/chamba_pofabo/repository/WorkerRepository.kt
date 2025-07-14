package com.ami.chamba_pofabo.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.ami.chamba_pofabo.model.*
import com.ami.chamba_pofabo.retrofit.RetrofitRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

object WorkerRepository {
    suspend fun getMyProfile(token: String): Result<MeResponse> {
        return try {
            val api = RetrofitRepository.getWorkerApi(token)
            val response = api.getMe()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(Exception("Error al obtener perfil: ${e.message}"))
        }
    }

    suspend fun uploadProfilePicture(token: String, imageUri: Uri, context: Context): Result<String> {
        return try {
            val api = RetrofitRepository.getWorkerApi(token)

            Log.d("WorkerRepo", "====== SUBIDA DE IMAGEN ======")
            Log.d("WorkerRepo", "URI original: $imageUri")
            Log.d("WorkerRepo", "Tipo MIME: ${context.contentResolver.getType(imageUri)}")

            val file = uriToFile(imageUri, context)
            Log.d("WorkerRepo", "Archivo temporal: ${file.absolutePath}")
            Log.d("WorkerRepo", "Tamaño del archivo: ${file.length()} bytes")

            val imageType = context.contentResolver.getType(imageUri) ?: "image/*"
            Log.d("WorkerRepo", "Usando tipo MIME: $imageType")

            val requestFile = file.asRequestBody(imageType.toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("picture", file.name, requestFile)
            Log.d("WorkerRepo", "Nombre del campo: picture")
            Log.d("WorkerRepo", "Nombre del archivo: ${file.name}")

            val response = api.uploadProfilePicture(imagePart)

            Log.d("WorkerRepo", "Código de respuesta: ${response.code()}")
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string() ?: "Sin cuerpo de error"
                Log.e("WorkerRepo", "Error del servidor: $errorBody")
            }

            if (response.isSuccessful) {
                Result.success("Imagen subida exitosamente")
            } else {
                Result.failure(Exception("Error al subir imagen: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("WorkerRepo", "Excepción: ${e.message}", e)
            Result.failure(Exception("Error: ${e.message}"))
        }
    }

    suspend fun updateWorkerCategories(token: String, workerId: Int, categories: List<Category>): Result<String> {
        return try {
            val api = RetrofitRepository.getWorkerApi(token)

            val categoryIds = categories.map { CategoryIdRequest(it.id) }
            val request = CategoriesRequest(categoryIds)

            val response = api.updateWorkerCategories(workerId, request)

            if (response.isSuccessful) {
                Result.success("Categorías actualizadas exitosamente")
            } else {
                Result.failure(Exception("Error al actualizar categorías: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error: ${e.message}"))
        }
    }
    suspend fun getWorkerPictureUrl(token: String): Result<String> {
        return try {
            val api = RetrofitRepository.getWorkerApi(token)
            val response = api.getMe()

            val pictureUrl = response.worker.picture_url
            if (pictureUrl != null && pictureUrl != "null") {
                Result.success(pictureUrl)
            } else {
                Result.success("")
            }
        } catch (e: Exception) {
            Log.e("WorkerRepo", "Error al obtener URL de imagen: ${e.message}")
            Result.failure(Exception("Error al obtener imagen: ${e.message}"))
        }
    }

    private fun uriToFile(uri: Uri, context: Context): File {
        val mimeType = context.contentResolver.getType(uri)
        val extension = when {
            mimeType?.contains("jpeg") == true || mimeType?.contains("jpg") == true -> ".jpg"
            mimeType?.contains("png") == true -> ".png"
            else -> ".jpg"
        }

        val fileName = "profile_picture_${System.currentTimeMillis()}"
        val tempFile = File.createTempFile(fileName, extension, context.cacheDir)
        tempFile.deleteOnExit()

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        Log.d("WorkerRepo", "Archivo convertido: ${tempFile.absolutePath} (${tempFile.length()} bytes)")
        return tempFile
    }
}