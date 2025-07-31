package com.example.indianchickencenter.model

import androidx.lifecycle.LiveData

class CustomerRepository(private val dao: CustomerDao) {

    val allCustomers: LiveData<List<Customer>> = dao.getAllCustomers()

    suspend fun insert(customer: Customer) {
        dao.insert(customer)
    }
}
