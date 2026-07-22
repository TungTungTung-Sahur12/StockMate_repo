package edu.cit.cortes.stockmate.network

import android.content.Context
import edu.cit.cortes.stockmate.BuildConfig
import edu.cit.cortes.stockmate.SessionManager
import edu.cit.cortes.stockmate.api.AdminApiService
import edu.cit.cortes.stockmate.api.AuthApiService
import edu.cit.cortes.stockmate.api.ProductApiService
import edu.cit.cortes.stockmate.api.SaleApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private val BASE_URL = BuildConfig.BASE_URL

    // Render's free tier spins the backend down when idle and can take 30-50s to wake up,
    // so timeouts need to comfortably outlast a cold start instead of failing fast.
    private const val TIMEOUT_SECONDS = 60L

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authRetrofit: Retrofit by lazy {
        val client = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getRetrofit(context: Context): Retrofit {
        val client = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
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

    fun getProductApiService(context: Context): ProductApiService = getRetrofit(context).create(ProductApiService::class.java)

    fun getSaleApiService(context: Context): SaleApiService = getRetrofit(context).create(SaleApiService::class.java)
}