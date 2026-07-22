package edu.cit.cortes.stockmate.model

import com.google.gson.annotations.SerializedName

// Mirrors backend salesrecording.dto.CreateSaleRequest
data class CreateSaleRequest(
    @SerializedName("productId") val productId: Long,
    @SerializedName("quantitySold") val quantitySold: Int
)

// Mirrors backend salesrecording.dto.SaleResponse
data class SaleResponse(
    @SerializedName("saleId") val saleId: Long,
    @SerializedName("productId") val productId: Long,
    @SerializedName("productName") val productName: String,
    @SerializedName("quantitySold") val quantitySold: Int,
    @SerializedName("totalAmount") val totalAmount: Double,
    @SerializedName("recordedByName") val recordedByName: String?,
    @SerializedName("createdAt") val createdAt: String?
)

// Mirrors backend salesrecording.dto.SalesSummaryResponse
data class SalesSummaryResponse(
    @SerializedName("totalCount") val totalCount: Long,
    @SerializedName("totalRevenue") val totalRevenue: Double
)
