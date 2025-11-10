package com.example.indianchickencenter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.indianchickencenter.model.Order
import com.example.indianchickencenter.model.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val repository: OrderRepository
) : ViewModel() {

    val allOrders: StateFlow<List<Order>> = repository.getAllOrders()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun insert(order: Order) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(order)
        }
    }

    fun delete(order: Order) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(order)
        }
    }
}
