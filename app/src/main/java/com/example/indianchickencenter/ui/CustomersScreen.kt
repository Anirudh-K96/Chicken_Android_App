package com.example.indianchickencenter.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditLocationAlt
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.indianchickencenter.model.Customer
import com.example.indianchickencenter.model.CustomerWithBalance
import com.example.indianchickencenter.util.CurrencyUtils

@Composable
fun CustomersScreen(
    customers: List<CustomerWithBalance>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onAddCustomer: (Customer) -> Unit,
    onUpdateLocation: (Int, Double?, Double?) -> Unit
) {
    var isAddDialogVisible by remember { mutableStateOf(false) }
    var locationDialogCustomer by remember { mutableStateOf<Customer?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                label = { Text("Search customers") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            if (customers.isEmpty()) {
                EmptyState(
                    title = "No customers yet",
                    body = "Add your first shop to start tracking balances."
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(customers) { customerWithBalance ->
                        CustomerCard(
                            customer = customerWithBalance.customer,
                            balance = customerWithBalance.balance,
                            onEditLocation = { locationDialogCustomer = customerWithBalance.customer },
                            onCall = { /* Hook up with dialer via intent */ }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { isAddDialogVisible = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.PersonAdd, contentDescription = "Add customer")
        }
    }

    if (isAddDialogVisible) {
        CustomerFormDialog(
            onDismiss = { isAddDialogVisible = false },
            onSave = {
                onAddCustomer(it)
                isAddDialogVisible = false
            }
        )
    }

    locationDialogCustomer?.let { customer ->
        LocationDialog(
            customer = customer,
            onDismiss = { locationDialogCustomer = null },
            onSave = { lat, lng ->
                onUpdateLocation(customer.id, lat, lng)
                locationDialogCustomer = null
            }
        )
    }
}

@Composable
private fun EmptyState(title: String, body: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text(body, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun CustomerCard(
    customer: Customer,
    balance: Double,
    onEditLocation: () -> Unit,
    onCall: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = customer.shopName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text("Owner: ${customer.ownerName}", style = MaterialTheme.typography.bodyMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Phone: ${customer.contact}", style = MaterialTheme.typography.bodySmall)
                IconButton(onClick = onCall) {
                    Icon(Icons.Default.Phone, contentDescription = "Call ${customer.shopName}")
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Balance: ${CurrencyUtils.format(balance)}",
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onEditLocation) {
                Icon(Icons.Default.EditLocationAlt, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (customer.latitude == null || customer.longitude == null) {
                        "Set location"
                    } else {
                        "Update location"
                    }
                )
            }
        }
    }
}

@Composable
private fun CustomerFormDialog(
    onDismiss: () -> Unit,
    onSave: (Customer) -> Unit
) {
    var shopName by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add customer") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = shopName,
                    onValueChange = { shopName = it },
                    label = { Text("Shop name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = ownerName,
                    onValueChange = { ownerName = it },
                    label = { Text("Owner name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = contact,
                    onValueChange = { contact = it },
                    label = { Text("Contact") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = latitude,
                        onValueChange = { latitude = it },
                        label = { Text("Latitude (optional)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = longitude,
                        onValueChange = { longitude = it },
                        label = { Text("Longitude (optional)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = shopName.isNotBlank() && ownerName.isNotBlank() && contact.isNotBlank(),
                onClick = {
                    onSave(
                        Customer(
                            shopName = shopName,
                            ownerName = ownerName,
                            contact = contact,
                            latitude = latitude.toDoubleOrNull(),
                            longitude = longitude.toDoubleOrNull()
                        )
                    )
                }
            ) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun LocationDialog(
    customer: Customer,
    onDismiss: () -> Unit,
    onSave: (Double?, Double?) -> Unit
) {
    var latitude by remember { mutableStateOf(customer.latitude?.toString() ?: "") }
    var longitude by remember { mutableStateOf(customer.longitude?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update location") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = latitude,
                    onValueChange = { latitude = it },
                    label = { Text("Latitude") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = longitude,
                    onValueChange = { longitude = it },
                    label = { Text("Longitude") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(latitude.toDoubleOrNull(), longitude.toDoubleOrNull()) }) {
                Text("Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
