package com.example.indianchickencenter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.indianchickencenter.model.Customer
import com.example.indianchickencenter.model.CustomerRepository
import com.example.indianchickencenter.model.CustomerWithBalance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerViewModel @Inject constructor(
    private val repository: CustomerRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    val searchText: StateFlow<String> = searchQuery.asStateFlow()

    val allCustomers: StateFlow<List<Customer>> = repository.observeCustomers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val customerBalances: StateFlow<List<CustomerWithBalance>> = repository.observeCustomersWithBalance()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val filteredCustomers: StateFlow<List<CustomerWithBalance>> =
        combine(customerBalances, searchQuery) { balances, query ->
            if (query.isBlank()) {
                balances
            } else {
                val lowered = query.lowercase()
                balances.filter {
                    it.customer.shopName.lowercase().contains(lowered) ||
                        it.customer.ownerName.lowercase().contains(lowered)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun insert(customer: Customer) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(customer)
    }

    fun updateLocation(customerId: Int, latitude: Double?, longitude: Double?) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateLocation(customerId, latitude, longitude)
        }

    fun updateSearch(query: String) {
        searchQuery.value = query
    }
}
