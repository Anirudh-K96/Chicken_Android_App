package com.example.indianchickencenter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.indianchickencenter.model.*
import com.example.indianchickencenter.ui.theme.IndianChickenCenterTheme
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel: CustomerViewModel by viewModels {
        CustomerViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IndianChickenCenterTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CustomerScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun CustomerScreen(viewModel: CustomerViewModel) {
    var shopName by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }

    val customers by viewModel.allCustomers.observeAsState(emptyList())

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        OutlinedTextField(
            value = shopName,
            onValueChange = { shopName = it },
            label = { Text("Shop Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = ownerName,
            onValueChange = { ownerName = it },
            label = { Text("Owner Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = contact,
            onValueChange = { contact = it },
            label = { Text("Contact") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (shopName.isNotBlank() && ownerName.isNotBlank() && contact.isNotBlank()) {
                    viewModel.insert(Customer(shopName = shopName, ownerName = ownerName, contact = contact))
                    shopName = ""
                    ownerName = ""
                    contact = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Customer")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Saved Customers", style = MaterialTheme.typography.titleMedium)

        LazyColumn {
            items(customers) { customer ->
                Text("- ${customer.shopName} (${customer.ownerName}) - ${customer.contact}")
            }
        }
    }
}