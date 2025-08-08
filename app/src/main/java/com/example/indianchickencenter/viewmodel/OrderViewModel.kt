package com.example.indianchickencenter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.indianchickencenter.model.Order
import com.example.indianchickencenter.model.OrderRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OrderViewModel(private val repository: OrderRepository) : ViewModel() {

    val allOrders: StateFlow<List<Order>> = repository.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun insert(order: Order) {
        viewModelScope.launch {
            repository.insert(order)
        }
    }

    fun delete(order: Order) {
        viewModelScope.launch {
            repository.delete(order)
        }
    }
}
