package edu.cit.cortes.stockmate.data

import android.content.Context
import edu.cit.cortes.stockmate.api.ProductApiService
import edu.cit.cortes.stockmate.api.SaleApiService
import edu.cit.cortes.stockmate.model.CreateSaleRequest
import edu.cit.cortes.stockmate.model.ProductResponse
import edu.cit.cortes.stockmate.model.SaleResponse
import edu.cit.cortes.stockmate.model.SalesSummaryResponse
import edu.cit.cortes.stockmate.network.ApiClient

class SaleRepository(
    context: Context,
    private val saleApi: SaleApiService = ApiClient.getSaleApiService(context),
    private val productApi: ProductApiService = ApiClient.getProductApiService(context)
) {
    suspend fun getProducts(): Result<List<ProductResponse>> {
        return try {
            val response = productApi.getProducts()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message() ?: "Could not load products."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Could not reach the server. Please try again."))
        }
    }

    suspend fun recordSale(request: CreateSaleRequest): Result<SaleResponse> {
        return try {
            val response = saleApi.createSale(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                // Surface backend validation messages (e.g. insufficient stock — FR-014)
                val errorBody = response.errorBody()?.string()
                val message = extractMessage(errorBody) ?: response.message() ?: "Failed to record sale."
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Could not reach the server. Please try again."))
        }
    }

    suspend fun getSales(
        startDate: String?,
        endDate: String?,
        productName: String?
    ): Result<List<SaleResponse>> {
        return try {
            val response = saleApi.getSales(startDate, endDate, productName)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message() ?: "Could not load sales history."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Could not reach the server. Please try again."))
        }
    }

    suspend fun getSummary(startDate: String?, endDate: String?): Result<SalesSummaryResponse> {
        return try {
            val response = saleApi.getSalesSummary(startDate, endDate)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message() ?: "Could not load sales summary."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Could not reach the server. Please try again."))
        }
    }

    // Backend returns {"message": "..."} on bad requests; pull it out for a clean toast.
    private fun extractMessage(body: String?): String? {
        if (body.isNullOrBlank()) return null
        val marker = "\"message\""
        val idx = body.indexOf(marker)
        if (idx == -1) return body
        val start = body.indexOf('"', idx + marker.length + 1)
        if (start == -1) return body
        val end = body.indexOf('"', start + 1)
        if (end == -1) return body
        return body.substring(start + 1, end)
    }
}
