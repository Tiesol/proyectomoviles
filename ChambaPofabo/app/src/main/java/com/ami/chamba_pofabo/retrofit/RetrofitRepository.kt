package com.ami.chamba_pofabo.retrofit

import android.util.Log
import com.ami.chamba_pofabo.api.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitRepository {

    private const val BASE_URL = "http://trabajos.jmacboy.com/api/"
    private const val TAG = "RetrofitDebug"

    private fun okHttp(token: String? = null): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d(TAG, message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder().apply {
            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)

            addInterceptor(loggingInterceptor)

            if (!token.isNullOrBlank()) {
                addInterceptor { chain ->
                    val req = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                    Log.d(TAG, "Enviando petición con token: ${token.take(15)}...")
                    chain.proceed(req)
                }
            }
        }.build()
    }

    fun getRetrofit(token: String? = null): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttp(token))
            .build()

    fun getClientApi()       = getRetrofit().create(ChambaApi::class.java)
    fun getWorkerApi(t: String?)      = getRetrofit(t).create(WorkerApi::class.java)
    fun getCategoryApi(t: String?)    = getRetrofit(t).create(CategoryApi::class.java)
    fun getAppointmentApi(t: String?) = getRetrofit(t).create(AppointmentApi::class.java)
    fun getChatApi(token: String): ChatApi =
        getRetrofit(token).create(ChatApi::class.java)
}