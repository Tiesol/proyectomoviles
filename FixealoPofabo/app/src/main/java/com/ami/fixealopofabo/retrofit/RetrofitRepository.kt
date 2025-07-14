package com.ami.fixealopofabo.retrofit

import com.ami.fixealopofabo.api.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitRepository {

    private const val BASE_URL = "http://trabajos.jmacboy.com/api/"

    private fun okHttp(token: String? = null): OkHttpClient =
        OkHttpClient.Builder().apply {
            if (!token.isNullOrBlank()) {
                addInterceptor { chain ->
                    val req = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                    chain.proceed(req)
                }
            }
        }.build()

    fun getRetrofit(token: String? = null): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttp(token))
            .build()

    fun getClientApi()       = getRetrofit().create(ClientApi::class.java)
    fun getWorkerApi(t: String?)      = getRetrofit(t).create(WorkerApi::class.java)
    fun getCategoryApi(t: String?)    = getRetrofit(t).create(CategoryApi::class.java)
    fun getAppointmentApi(t: String?) = getRetrofit(t).create(AppointmentApi::class.java)
    fun getChatApi(token: String): ChatApi =
        getRetrofit(token).create(ChatApi::class.java)
}
