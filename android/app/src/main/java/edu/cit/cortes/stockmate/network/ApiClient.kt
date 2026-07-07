package edu.cit.cortes.stockmate.network

import android.content.Context
import edu.cit.cortes.stockmate.SessionManager
import edu.cit.cortes.stockmate.api.AdminApiService
import edu.cit.cortes.stockmate.api.AuthApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val authRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getRetrofit(context: Context): Retrofit {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(AuthInterceptor(SessionManager.getInstance(context)))
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApiService: AuthApiService by lazy {
        authRetrofit.create(AuthApiService::class.java)
    }

    fun getAdminApiService(context: Context): AdminApiService = getRetrofit(context).create(AdminApiService::class.java)
}
