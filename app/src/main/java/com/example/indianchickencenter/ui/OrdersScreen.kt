package com.example.indianchickencenter.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.indianchickencenter.model.Order

@Composable
fun OrdersScreen(
    orders: List<Order>,
    onAddOrder: (Order) -> Unit
) {
    var quantity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Add New Order", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = quantity,
            onValueChange = { quantity = it },
            label = { Text("Quantity (kg)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price per kg") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                val qty = quantity.toDoubleOrNull() ?: 0.0
                val prc = price.toDoubleOrNull() ?: 0.0
                if (qty > 0 && prc > 0) {
                    onAddOrder(Order(quantity = qty, price = prc, date = System.currentTimeMillis().toString(), customerId = 1))
                    quantity = ""
                    price = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Order")
        }

        Spacer(Modifier.height(16.dp))
        Text("Order History", style = MaterialTheme.typography.titleLarge)

        LazyColumn {
            items(orders) { order ->
                Text("Qty: ${order.quantity}kg, Price: ₹${order.price} - ${order.date}")
                Divider()
            }
        }
    }
}