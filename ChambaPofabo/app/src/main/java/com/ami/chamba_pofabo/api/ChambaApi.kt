package com.ami.chamba_pofabo.api

import com.ami.chamba_pofabo.model.*
import retrofit2.http.Body
import retrofit2.http.POST

interface ChambaApi {
    @POST("worker/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse
    @POST("worker/register1")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse
}
