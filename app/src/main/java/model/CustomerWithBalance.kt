package com.example.indianchickencenter.model

import androidx.room.Embedded

data class CustomerWithBalance(
    @Embedded val customer: Customer,
    val totalOrdered: Double,
    val totalPaid: Double
) {
    val balance: Double
        get() = totalOrdered - totalPaid
}
