package com.example.indianchickencenter.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.indianchickencenter.model.AppDatabase
import com.example.indianchickencenter.model.Customer
import com.example.indianchickencenter.model.CustomerRepository
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
