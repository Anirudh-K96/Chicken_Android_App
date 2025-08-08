package com.example.indianchickencenter.model

class OrderRepository(private val orderDao: OrderDao) {
    fun getAllOrders() = orderDao.getAllOrders()
    fun getOrdersForCustomer(customerId: Int) = orderDao.getOrdersForCustomer(customerId)
    suspend fun insert(order: Order) = orderDao.insert(order)
    suspend fun delete(order: Order) = orderDao.delete(order)
}