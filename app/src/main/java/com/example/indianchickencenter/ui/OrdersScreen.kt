package com.example.indianchickencenter.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.indianchickencenter.model.Customer
import com.example.indianchickencenter.model.Order
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    orders: List<Order>,
    customers: List<Customer>,   // ✅ now passed correctly from MainActivity
    onAddOrder: (Order) -> Unit
) {
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var quantityKg by remember { mutableStateOf("") }
    var pricePerKg by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Customer dropdown
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedCustomer?.shopName ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Customer") },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                customers.forEach { customer ->
                    DropdownMenuItem(
                        text = { Text("${customer.shopName} (${customer.ownerName})") },
                        onClick = {
                            selectedCustomer = customer
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

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
                val qty = quantityKg.toDoubleOrNull()
                val price = pricePerKg.toDoubleOrNull()

                if (selectedCustomer != null && qty != null && price != null) {
                    onAddOrder(
                        Order(
                            customerId = selectedCustomer!!.id,
                            date = Date(),
                            quantityKg = qty,
                            pricePerKg = price
                        )
                    )
                    selectedCustomer = null
                    quantityKg = ""
                    pricePerKg = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Order")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Orders", style = MaterialTheme.typography.titleMedium)

        val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

        LazyColumn {
            items(orders) { order ->
                val customer = customers.find { it.id == order.customerId }
                val customerName = customer?.shopName ?: "Unknown Customer"
                val formattedDate = dateFormatter.format(order.date)
                Text("- $customerName: ${order.quantityKg} kg @ ₹${order.pricePerKg} on $formattedDate")
            }
        }
    }
}
