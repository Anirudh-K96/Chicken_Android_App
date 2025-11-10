package com.example.indianchickencenter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.indianchickencenter.model.Payment
import com.example.indianchickencenter.model.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val repository: PaymentRepository
) : ViewModel() {

    private val selectedCustomerId = MutableStateFlow<Int?>(null)
    val selectedCustomer: StateFlow<Int?> = selectedCustomerId.asStateFlow()

    val payments: StateFlow<List<Payment>> = selectedCustomerId
        .flatMapLatest { customerId ->
            if (customerId == null) {
                flowOf(emptyList())
            } else {
                repository.observePaymentsForCustomer(customerId)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun selectCustomer(customerId: Int?) {
        selectedCustomerId.value = customerId
    }

    fun recordPayment(customerId: Int, amount: Double, method: String, note: String?) {
        viewModelScope.launch {
            repository.recordPayment(customerId, amount, method, note)
            selectedCustomerId.value = customerId
        }
    }
}
