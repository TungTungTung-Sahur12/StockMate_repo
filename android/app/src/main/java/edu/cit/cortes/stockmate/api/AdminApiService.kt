package edu.cit.cortes.stockmate.api

import edu.cit.cortes.stockmate.model.CreateStaffRequest
import edu.cit.cortes.stockmate.model.UpdateStatusRequest
import edu.cit.cortes.stockmate.model.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface AdminApiService {
    @GET("api/admin/users")
    suspend fun getUsers(): Response<List<UserResponse>>

    @POST("api/admin/staff")
    suspend fun createStaff(@Body request: CreateStaffRequest): Response<UserResponse>

    @PATCH("api/admin/users/{userId}/status")
    suspend fun updateStatus(
        @Path("userId") userId: Long,
        @Body request: UpdateStatusRequest
    ): Response<UserResponse>
}
