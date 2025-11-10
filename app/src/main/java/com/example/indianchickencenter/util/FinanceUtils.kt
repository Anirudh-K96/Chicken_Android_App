package com.example.indianchickencenter.util

import com.example.indianchickencenter.model.Order

object FinanceUtils {

    fun calculateOrderValue(quantityKg: Double?, pricePerKg: Double?): Double {
        if (quantityKg == null || pricePerKg == null) return 0.0
        if (quantityKg <= 0 || pricePerKg <= 0) return 0.0
        return quantityKg * pricePerKg
    }

    fun aggregateOrdersValue(orders: List<Order>): Double =
        orders.sumOf { calculateOrderValue(it.quantityKg, it.pricePerKg) }

    fun calculateBalance(totalOrders: Double, totalPayments: Double): Double =
        totalOrders - totalPayments
}
