package com.example.indianchickencenter.model

class CustomerRepository(private val dao: CustomerDao) {

    val allCustomers = dao.getAllCustomers()

    suspend fun insert(customer: Customer) {
        dao.insert(customer)
    }
}
