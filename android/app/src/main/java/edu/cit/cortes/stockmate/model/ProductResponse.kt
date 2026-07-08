package edu.cit.cortes.stockmate.model

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    @SerializedName("productId") val productId: Long,
    @SerializedName("name") val name: String,
    @SerializedName("category") val category: String,
    @SerializedName("size") val size: String?,
    @SerializedName("price") val price: Double,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("lowStockThreshold") val lowStockThreshold: Int,
    @SerializedName("isLowStock") val isLowStock: Boolean,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null
)
