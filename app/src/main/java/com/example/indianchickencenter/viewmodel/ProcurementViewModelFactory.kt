package com.example.indianchickencenter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.indianchickencenter.model.OrderRepository
import com.example.indianchickencenter.model.ProcurementRepository

class ProcurementViewModelFactory(
    private val procurementRepository: ProcurementRepository,
    private val orderRepository: OrderRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProcurementViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProcurementViewModel(procurementRepository, orderRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
