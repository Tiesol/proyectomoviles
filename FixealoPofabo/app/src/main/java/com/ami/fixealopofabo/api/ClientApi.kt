package com.ami.fixealopofabo.api

import com.ami.fixealopofabo.model.*
import retrofit2.http.Body
import retrofit2.http.POST

interface ClientApi {
    @POST("client/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse
    @POST("client/register1")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse
}
