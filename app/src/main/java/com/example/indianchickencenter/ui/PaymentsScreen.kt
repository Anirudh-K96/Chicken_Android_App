package com.example.indianchickencenter.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.indianchickencenter.model.CustomerWithBalance
import com.example.indianchickencenter.model.Payment
import com.example.indianchickencenter.util.CurrencyUtils
import com.example.indianchickencenter.util.DateUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentsScreen(
    customers: List<CustomerWithBalance>,
    selectedCustomerId: Int?,
    onSelectCustomer: (Int) -> Unit,
    payments: List<Payment>,
    onAddPayment: (Int, Double, String, String?) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    var expanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var amountInput by remember { mutableStateOf("") }
    var noteInput by remember { mutableStateOf("") }
    val paymentMethods = listOf("Cash", "UPI", "Other")
    var selectedMethodIndex by remember { mutableStateOf(0) }

    val selectedCustomer = customers.firstOrNull { it.customer.id == selectedCustomerId }
    LaunchedEffect(customers, selectedCustomerId) {
        if (selectedCustomerId == null && customers.isNotEmpty()) {
            onSelectCustomer(customers.first().customer.id)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = selectedCustomer?.customer?.shopName ?: "",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                label = { Text("Select customer") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )
            androidx.compose.material3.ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                customers.forEach { customerWithBalance ->
                    DropdownMenuItem(
                        text = { Text(customerWithBalance.customer.shopName) },
                        onClick = {
                            onSelectCustomer(customerWithBalance.customer.id)
                            expanded = false
                        }
                    )
                }
            }
        }

        selectedCustomer?.let {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(it.customer.shopName, style = MaterialTheme.typography.titleMedium)
                    Text("Balance: ${CurrencyUtils.format(it.balance)}", fontWeight = FontWeight.Bold)
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Add payment", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = { amountInput = it },
                        label = { Text("Amount") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    SingleChoiceSegmentedButtonRow {
                        paymentMethods.forEachIndexed { index, method ->
                            SegmentedButton(
                                selected = index == selectedMethodIndex,
                                onClick = { selectedMethodIndex = index },
                                shape = SegmentedButtonDefaults.itemShape(index, paymentMethods.size)
                            ) {
                                Text(method)
                            }
                        }
                    }
                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = { noteInput = it },
                        label = { Text("Note (optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            val amount = amountInput.toDoubleOrNull()
                            if (amount == null || amount <= 0) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Enter a valid amount")
                                }
                            } else {
                                onAddPayment(
                                    it.customer.id,
                                    amount,
                                    paymentMethods[selectedMethodIndex],
                                    noteInput.ifBlank { null }
                                )
                                amountInput = ""
                                noteInput = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Record payment")
                    }
                }
            }

            PaymentsList(payments = payments)
        } ?: Text("Select a customer to view and add payments.")
    }
}

@Composable
private fun PaymentsList(payments: List<Payment>) {
    if (payments.isEmpty()) {
        Text("No payments yet.", modifier = Modifier.padding(8.dp))
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(payments) { payment ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = CurrencyUtils.format(payment.amount),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text("Method: ${payment.method}")
                        payment.note?.let { Text("Note: $it") }
                        Text(
                            text = DateUtils.formatForList(payment.date),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
