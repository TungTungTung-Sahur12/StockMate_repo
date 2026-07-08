package edu.cit.cortes.stockmate

object InventoryUiUtils {
    fun isLowStock(quantity: Int, threshold: Int): Boolean = quantity <= threshold
}
