package edu.cit.cortes.stockmate.data

import com.google.gson.Gson
import edu.cit.cortes.stockmate.api.AuthApiService
import edu.cit.cortes.stockmate.model.AuthResponse
import edu.cit.cortes.stockmate.model.LoginRequest
import edu.cit.cortes.stockmate.model.RegisterRequest
import edu.cit.cortes.stockmate.model.RegisterResponse
import edu.cit.cortes.stockmate.network.ApiClient
import retrofit2.Response

/**
 * Small helper class matching the backend's JSON error shape, e.g.:
 * { "message": "Invalid email or password" }
 * The backend also sometimes returns per-field validation errors like
 * { "email": "Email format is invalid" } — we fall back to showing the
 * raw error body in that case, since it's still human-readable.
 */
private data class ApiErrorResponse(val message: String?)

class AuthRepository(
    private val apiService: AuthApiService = ApiClient.authApiService
) {

    /**
     * Extracts a readable error message from a failed Retrofit response.
     * Falls back gracefully if the body isn't in the expected shape.
     */
    private fun <T> extractErrorMessage(response: Response<T>): String {
        val rawErrorBody = response.errorBody()?.string()
        if (rawErrorBody.isNullOrBlank()) {
            return response.message() ?: "Something went wrong. Please try again."
        }

        return try {
            val parsed = Gson().fromJson(rawErrorBody, ApiErrorResponse::class.java)
            parsed.message ?: rawErrorBody
        } catch (e: Exception) {
            // Body wasn't in the { "message": "..." } shape (e.g. field validation
            // errors like { "email": "..." }) — just show the raw body as-is.
            rawErrorBody
        }
    }

    suspend fun register(request: RegisterRequest): Result<RegisterResponse> {
        return try {
            val response = apiService.register(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(extractErrorMessage(response)))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Could not reach the server. Check your connection and try again."))
        }
    }

    suspend fun login(request: LoginRequest): Result<AuthResponse> {
        return try {
            val response = apiService.login(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(extractErrorMessage(response)))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Could not reach the server. Check your connection and try again."))
        }
    }
}