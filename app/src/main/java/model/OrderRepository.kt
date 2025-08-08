package com.example.indianchickencenter.model

class OrderRepository(private val orderDao: OrderDao) {

    fun getAllOrders() = orderDao.getAllOrders()

    suspend fun insert(order: Order) {
        orderDao.insertOrder(order)
    }
}