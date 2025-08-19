package com.example.indianchickencenter.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.indianchickencenter.model.Customer

@Composable
fun CustomersScreen(
    customers: List<Customer>,
    onAddCustomer: (Customer) -> Unit
) {
    var shopName by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
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

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                if (shopName.isNotBlank() && ownerName.isNotBlank() && contact.isNotBlank()) {
                    onAddCustomer(
                        Customer(
                            shopName = shopName,
                            ownerName = ownerName,
                            contact = contact
                        )
                    )
                    shopName = ""; ownerName = ""; contact = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Add Customer") }

        Spacer(Modifier.height(16.dp))

        Text("Saved Customers", style = MaterialTheme.typography.titleMedium)

        LazyColumn {
            items(customers) { customer ->
                Text("- ${customer.shopName} (${customer.ownerName}) - ${customer.contact}")
            }
        }
    }
}
