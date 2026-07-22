package edu.cit.cortes.stockmate.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import edu.cit.cortes.stockmate.data.SaleRepository
import edu.cit.cortes.stockmate.model.CreateSaleRequest
import edu.cit.cortes.stockmate.model.ProductResponse
import edu.cit.cortes.stockmate.model.SaleResponse
import edu.cit.cortes.stockmate.model.SalesSummaryResponse
import kotlinx.coroutines.launch

class SaleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SaleRepository = SaleRepository(application.applicationContext)

    private val _uiState = MutableLiveData<SaleUiState>(SaleUiState.Idle)
    val uiState: LiveData<SaleUiState> = _uiState

    // Kept so the record form can validate quantity against current stock before hitting the API.
    private val _products = MutableLiveData<List<ProductResponse>>(emptyList())
    val products: LiveData<List<ProductResponse>> = _products

    fun loadProducts() {
        viewModelScope.launch {
            repository.getProducts().fold(
                onSuccess = { _products.value = it },
                onFailure = { _uiState.value = SaleUiState.Error(it.message ?: "Unable to load products.") }
            )
        }
    }

    fun recordSale(request: CreateSaleRequest) {
        viewModelScope.launch {
            _uiState.value = SaleUiState.Loading
            repository.recordSale(request).fold(
                onSuccess = {
                    _uiState.value = SaleUiState.SaleRecorded(it)
                    loadProducts() // refresh stock so subsequent validation is accurate (FR-013/017)
                },
                onFailure = { _uiState.value = SaleUiState.Error(it.message ?: "Failed to record sale.") }
            )
        }
    }

    fun loadSales(startDate: String?, endDate: String?, productName: String?) {
        viewModelScope.launch {
            _uiState.value = SaleUiState.Loading
            val salesResult = repository.getSales(startDate, endDate, productName)
            salesResult.fold(
                onSuccess = { sales ->
                    val summary = repository.getSummary(startDate, endDate).getOrNull()
                    _uiState.value = SaleUiState.SalesLoaded(sales, summary)
                },
                onFailure = { _uiState.value = SaleUiState.Error(it.message ?: "Unable to load sales history.") }
            )
        }
    }
}

sealed class SaleUiState {
    object Idle : SaleUiState()
    object Loading : SaleUiState()
    data class SaleRecorded(val sale: SaleResponse) : SaleUiState()
    data class SalesLoaded(val sales: List<SaleResponse>, val summary: SalesSummaryResponse?) : SaleUiState()
    data class Error(val message: String) : SaleUiState()
}
