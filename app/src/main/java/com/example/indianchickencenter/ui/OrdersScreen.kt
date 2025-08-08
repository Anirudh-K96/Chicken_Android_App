package com.example.indianchickencenter.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.indianchickencenter.model.Order
import java.util.Date

@Composable
fun OrdersScreen(
    orders: List<Order>,
    onAddOrder: (Order) -> Unit
) {
    var customerId by remember { mutableStateOf("") }
    var quantityKg by remember { mutableStateOf("") }
    var pricePerKg by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = customerId,
            onValueChange = { customerId = it },
            label = { Text("Customer ID") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = quantityKg,
            onValueChange = { quantityKg = it },
            label = { Text("Quantity (kg)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = pricePerKg,
            onValueChange = { pricePerKg = it },
            label = { Text("Price per kg") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val custId = customerId.toIntOrNull()
                val qty = quantityKg.toDoubleOrNull()
                val price = pricePerKg.toDoubleOrNull()

                if (custId != null && qty != null && price != null) {
                    onAddOrder(
                        Order(
                            customerId = custId,
                            date = Date(),
                            quantityKg = qty,
                            pricePerKg = price
                        )
                    )
                    customerId = ""
                    quantityKg = ""
                    pricePerKg = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Order")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Orders",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(orders) { order ->
                Text(
                    "- Customer ${order.customerId}: ${order.quantityKg} kg @ ₹${order.pricePerKg} on ${order.date}"
                )
            }
        }
    }
}
