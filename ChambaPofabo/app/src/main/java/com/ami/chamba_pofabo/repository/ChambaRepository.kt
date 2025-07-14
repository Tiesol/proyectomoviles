package com.ami.chamba_pofabo.repository

import com.ami.chamba_pofabo.model.*
import com.ami.chamba_pofabo.retrofit.RetrofitRepository

object ChambaRepository {

    suspend fun login(email: String, password: String): AuthResponse {
        return RetrofitRepository
            .getClientApi()
            .login(LoginRequest(email, password))
    }

    suspend fun register(
        name: String,
        lastName: String,
        email: String,
        password: String
    ): RegisterResponse {
        return RetrofitRepository
            .getClientApi()
            .register(
                RegisterRequest(name, lastName, email, password)
            )
    }
}
