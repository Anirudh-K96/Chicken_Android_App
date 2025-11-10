package com.example.indianchickencenter.model

import kotlinx.coroutines.flow.Flow

class CustomerRepository(private val dao: CustomerDao) {

    val allCustomers = dao.getAllCustomers()

    fun observeCustomers(): Flow<List<Customer>> = dao.observeCustomers()

    fun observeCustomersWithBalance(): Flow<List<CustomerWithBalance>> = dao.observeCustomersWithBalance()

    suspend fun insert(customer: Customer) {
        dao.insert(customer)
    }

    suspend fun updateLocation(customerId: Int, latitude: Double?, longitude: Double?) {
        dao.updateLocation(customerId, latitude, longitude)
    }
}
