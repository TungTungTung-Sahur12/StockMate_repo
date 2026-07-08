package edu.cit.cortes.stockmate

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InventoryUiUtilsTest {
    @Test
    fun lowStockIsDetectedWhenQuantityIsAtOrBelowThreshold() {
        assertTrue(InventoryUiUtils.isLowStock(4, 5))
        assertTrue(InventoryUiUtils.isLowStock(5, 5))
        assertFalse(InventoryUiUtils.isLowStock(6, 5))
    }
}
