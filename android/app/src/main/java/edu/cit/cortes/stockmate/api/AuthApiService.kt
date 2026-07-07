package edu.cit.cortes.stockmate.api

import edu.cit.cortes.stockmate.model.AuthResponse
import edu.cit.cortes.stockmate.model.LoginRequest
import edu.cit.cortes.stockmate.model.RegisterRequest
import edu.cit.cortes.stockmate.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
}
