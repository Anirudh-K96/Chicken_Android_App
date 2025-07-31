package com.example.indianchickencenter.model

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CustomerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CustomerRepository
    val allCustomers: LiveData<List<Customer>>

    init {
        val dao = AppDatabase.getDatabase(application).customerDao()
        repository = CustomerRepository(dao)
        allCustomers = repository.allCustomers
    }

    fun insert(customer: Customer) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(customer)
    }
}
