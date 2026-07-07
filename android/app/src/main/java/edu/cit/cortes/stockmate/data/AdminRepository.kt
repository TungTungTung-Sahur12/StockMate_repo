package edu.cit.cortes.stockmate.data

import android.content.Context
import edu.cit.cortes.stockmate.api.AdminApiService
import edu.cit.cortes.stockmate.model.CreateStaffRequest
import edu.cit.cortes.stockmate.model.UpdateStatusRequest
import edu.cit.cortes.stockmate.model.UserResponse
import edu.cit.cortes.stockmate.network.ApiClient

class AdminRepository(
    context: Context,
    private val apiService: AdminApiService = ApiClient.getAdminApiService(context)
) {
    suspend fun getUsers(): Result<List<UserResponse>> {
        return try {
            val response = apiService.getUsers()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message() ?: "Could not load users."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Could not reach the server. Please try again."))
        }
    }

    suspend fun createStaff(request: CreateStaffRequest): Result<UserResponse> {
        return try {
            val response = apiService.createStaff(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception(errorBody?.takeIf { it.isNotBlank() } ?: response.message() ?: "Failed to create staff."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Could not reach the server. Please try again."))
        }
    }

    suspend fun updateStatus(userId: Long, isActive: Boolean): Result<UserResponse> {
        return try {
            val response = apiService.updateStatus(userId, UpdateStatusRequest(isActive))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message() ?: "Failed to update status."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Could not reach the server. Please try again."))
        }
    }
}
