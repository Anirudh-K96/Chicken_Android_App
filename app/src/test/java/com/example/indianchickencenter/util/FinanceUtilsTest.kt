package com.example.indianchickencenter.util

import com.example.indianchickencenter.model.Order
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Date

class FinanceUtilsTest {

    @Test
    fun `calculate order value guards invalid input`() {
        assertEquals(0.0, FinanceUtils.calculateOrderValue(-1.0, 10.0), 0.0)
        assertEquals(0.0, FinanceUtils.calculateOrderValue(5.0, -10.0), 0.0)
        assertEquals(0.0, FinanceUtils.calculateOrderValue(null, 10.0), 0.0)
    }

    @Test
    fun `aggregate order value sums totals`() {
        val orders = listOf(
            Order(customerId = 1, date = Date(), quantityKg = 10.0, pricePerKg = 120.0),
            Order(customerId = 2, date = Date(), quantityKg = 5.0, pricePerKg = 150.0)
        )
        assertEquals(10.0 * 120 + 5.0 * 150, FinanceUtils.aggregateOrdersValue(orders), 0.0)
    }

    @Test
    fun `balance equals orders minus payments`() {
        assertEquals(500.0, FinanceUtils.calculateBalance(1500.0, 1000.0), 0.0)
    }
}
