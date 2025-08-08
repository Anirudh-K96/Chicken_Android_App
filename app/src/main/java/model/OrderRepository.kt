package com.example.indianchickencenter.model

import kotlinx.coroutines.flow.Flow

class OrderRepository(private val dao: OrderDao) {

    fun getAllOrders(): Flow<List<Order>> = dao.getAllOrders()

    suspend fun insert(order: Order) {
        dao.insert(order)
    }

    suspend fun delete(order: Order) {
        dao.delete(order)
    }
}
