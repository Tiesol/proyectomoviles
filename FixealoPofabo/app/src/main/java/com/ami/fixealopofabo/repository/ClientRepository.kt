package com.ami.fixealopofabo.repository

import com.ami.fixealopofabo.model.*
import com.ami.fixealopofabo.retrofit.RetrofitRepository

object ClientRepository {

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
