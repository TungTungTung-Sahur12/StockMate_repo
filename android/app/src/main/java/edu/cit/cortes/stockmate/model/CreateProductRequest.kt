package edu.cit.cortes.stockmate.model

import com.google.gson.annotations.SerializedName

data class CreateProductRequest(
    @SerializedName("name") val name: String,
    @SerializedName("size") val size: String?,
    @SerializedName("category") val category: String,
    @SerializedName("price") val price: Double,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("lowStockThreshold") val lowStockThreshold: Int
)
