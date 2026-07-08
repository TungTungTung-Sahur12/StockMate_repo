package edu.cit.cortes.stockmate.api

import edu.cit.cortes.stockmate.model.CreateProductRequest
import edu.cit.cortes.stockmate.model.ProductResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApiService {
    @GET("api/products")
    suspend fun getProducts(@Query("category") category: String? = null): Response<List<ProductResponse>>

    @POST("api/products")
    suspend fun createProduct(@Body request: CreateProductRequest): Response<ProductResponse>

    @DELETE("api/admin/products/{id}")
    suspend fun deleteProduct(@Path("id") productId: Long): Response<Void>
}
