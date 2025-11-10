package com.example.indianchickencenter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.indianchickencenter.model.CustomerRepository
import com.example.indianchickencenter.model.OrderRepository
import com.example.indianchickencenter.model.ProcurementRepository

class RouteViewModelFactory(
    private val orderRepository: OrderRepository,
    private val customerRepository: CustomerRepository,
    private val procurementRepository: ProcurementRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RouteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RouteViewModel(orderRepository, customerRepository, procurementRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
