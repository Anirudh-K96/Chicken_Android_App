package com.example.indianchickencenter.model

import kotlinx.coroutines.flow.Flow
import java.util.Date

class PaymentRepository(private val dao: PaymentDao) {

    fun observePaymentsForCustomer(customerId: Int): Flow<List<Payment>> =
        dao.observePaymentsForCustomer(customerId)

    fun observeAllPayments(): Flow<List<Payment>> = dao.observeAllPayments()

    suspend fun insert(payment: Payment) {
        dao.insert(payment)
    }

    suspend fun recordPayment(
        customerId: Int,
        amount: Double,
        method: String,
        note: String?
    ) {
        val payment = Payment(
            customerId = customerId,
            date = Date(),
            amount = amount,
            method = method,
            note = note
        )
        dao.insert(payment)
    }
}
