package edu.cit.cortes.stockmate.api

import edu.cit.cortes.stockmate.model.CreateSaleRequest
import edu.cit.cortes.stockmate.model.SaleResponse
import edu.cit.cortes.stockmate.model.SalesSummaryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// Mirrors backend SaleController (@RequestMapping "/api/sales")
interface SaleApiService {
    @POST("api/sales")
    suspend fun createSale(@Body request: CreateSaleRequest): Response<SaleResponse>

    @GET("api/sales")
    suspend fun getSales(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("productName") productName: String? = null
    ): Response<List<SaleResponse>>

    @GET("api/sales/summary")
    suspend fun getSalesSummary(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<SalesSummaryResponse>
}
