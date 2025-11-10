package com.example.indianchickencenter.ui

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.indianchickencenter.model.Customer
import com.example.indianchickencenter.model.Order
import com.example.indianchickencenter.util.CurrencyUtils
import com.example.indianchickencenter.util.DateUtils
import com.example.indianchickencenter.util.FinanceUtils
import com.example.indianchickencenter.util.RouteStop
import com.example.indianchickencenter.viewmodel.InventoryState
import com.example.indianchickencenter.viewmodel.RouteUiState
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    customers: List<Customer>,
    orders: List<Order>,
    inventoryState: InventoryState,
    snackbarHostState: SnackbarHostState,
    onAddOrder: (Order) -> Unit,
    onDeleteOrder: (Order) -> Unit,
    onUpdateProcurement: (Double) -> Unit,
    onPlanRoute: () -> Unit,
    routeUiState: RouteUiState
) {
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }
    var quantityInput by remember { mutableStateOf("") }
    var priceInput by remember { mutableStateOf("") }
    var procurementInput by remember { mutableStateOf(inventoryState.procurement?.quantityKg?.toString() ?: "5000") }
    val coroutineScope = rememberCoroutineScope()

    val remainingKg = inventoryState.remainingKg
    val isInventoryAvailable = inventoryState.procurement != null

    LaunchedEffect(inventoryState.procurement?.quantityKg) {
        inventoryState.procurement?.quantityKg?.let {
            procurementInput = it.toString()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ProcurementCard(
            procurementInput = procurementInput,
            onProcurementChange = { procurementInput = it },
            onSave = {
                procurementInput.toDoubleOrNull()?.let { qty ->
                    onUpdateProcurement(qty)
                }
            },
            inventoryState = inventoryState
        )

        OrderForm(
            customers = customers,
            selectedCustomer = selectedCustomer,
            onCustomerSelected = { selectedCustomer = it },
            quantityInput = quantityInput,
            onQuantityChange = { quantityInput = it },
            priceInput = priceInput,
            onPriceChange = { priceInput = it },
            remainingKg = remainingKg,
            onAddOrder = {
                val qty = quantityInput.toDoubleOrNull()
                val price = priceInput.toDoubleOrNull()
                when {
                    selectedCustomer == null -> coroutineScope.launch {
                        snackbarHostState.showSnackbar("Pick a customer")
                    }
                    qty == null || qty <= 0 -> coroutineScope.launch {
                        snackbarHostState.showSnackbar("Quantity must be > 0")
                    }
                    price == null || price <= 0 -> coroutineScope.launch {
                        snackbarHostState.showSnackbar("Price must be > 0")
                    }
                    !isInventoryAvailable -> coroutineScope.launch {
                        snackbarHostState.showSnackbar("Set today's procurement first")
                    }
                    qty > max(0.0, remainingKg) -> coroutineScope.launch {
                        snackbarHostState.showSnackbar("Not enough inventory left for today")
                    }
                    else -> {
                        onAddOrder(
                            Order(
                                customerId = selectedCustomer!!.id,
                                date = Date(),
                                quantityKg = qty,
                                pricePerKg = price
                            )
                        )
                        quantityInput = ""
                        priceInput = ""
                    }
                }
            }
        )

        RoutePlannerSection(
            routeUiState = routeUiState,
            onPlanRoute = onPlanRoute
        )

        OrdersList(
            orders = orders,
            customers = customers,
            onDeleteOrder = onDeleteOrder,
            modifier = Modifier.weight(1f, fill = false)
        )

        OrdersFooter(orders = orders)
    }
}

@Composable
private fun ProcurementCard(
    procurementInput: String,
    onProcurementChange: (String) -> Unit,
    onSave: () -> Unit,
    inventoryState: InventoryState
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Daily procurement", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = procurementInput,
                    onValueChange = onProcurementChange,
                    label = { Text("Quantity (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = onSave, modifier = Modifier.alignByBaseline()) {
                    Text("Save")
                }
            }
            val procurement = inventoryState.procurement
            Text(
                text = "Remaining today: ${"%.2f".format(inventoryState.remainingKg)} kg",
                fontWeight = FontWeight.Bold
            )
            procurement?.let {
                Text("City: ${it.city} • Ordered: ${"%.2f".format(inventoryState.orderedKg)} kg")
            } ?: Text("Set procurement to track remaining inventory.")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderForm(
    customers: List<Customer>,
    selectedCustomer: Customer?,
    onCustomerSelected: (Customer) -> Unit,
    quantityInput: String,
    onQuantityChange: (String) -> Unit,
    priceInput: String,
    onPriceChange: (String) -> Unit,
    remainingKg: Double,
    onAddOrder: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Create order", style = MaterialTheme.typography.titleMedium)
            CustomerDropdown(
                customers = customers,
                selectedCustomer = selectedCustomer,
                onCustomerSelected = onCustomerSelected
            )
            OutlinedTextField(
                value = quantityInput,
                onValueChange = onQuantityChange,
                label = { Text("Quantity (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = priceInput,
                onValueChange = onPriceChange,
                label = { Text("Price per kg") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            val qty = quantityInput.toDoubleOrNull()
            val price = priceInput.toDoubleOrNull()
            val total = FinanceUtils.calculateOrderValue(qty, price)
            Text("Order total: ${CurrencyUtils.format(total)}")
            Text("Remaining inventory: ${"%.2f".format(remainingKg)} kg")
            Button(
                onClick = onAddOrder,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add order")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomerDropdown(
    customers: List<Customer>,
    selectedCustomer: Customer?,
    onCustomerSelected: (Customer) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    androidx.compose.material3.ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedCustomer?.shopName ?: "",
            onValueChange = {},
            label = { Text("Select customer") },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            readOnly = true,
            trailingIcon = {
                androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            }
        )
        androidx.compose.material3.ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            customers.forEach { customer ->
                androidx.compose.material3.DropdownMenuItem(
                    text = { Text("${customer.shopName} (${customer.ownerName})") },
                    onClick = {
                        onCustomerSelected(customer)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun RoutePlannerSection(
    routeUiState: RouteUiState,
    onPlanRoute: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Routing", style = MaterialTheme.typography.titleMedium)
                Button(onClick = onPlanRoute) {
                    Icon(Icons.Default.Route, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Plan route")
                }
            }
            when {
                routeUiState.isLoading -> CircularProgressIndicator()
                routeUiState.errorMessage != null -> Text(routeUiState.errorMessage!!)
                routeUiState.stops.isNotEmpty() -> RouteStopsList(
                    stops = routeUiState.stops,
                    totalDistance = routeUiState.totalDistanceKm
                )
            }
            if (routeUiState.missingLocationCustomers.isNotEmpty()) {
                Text(
                    text = "Set locations for: ${routeUiState.missingLocationCustomers.joinToString()}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun RouteStopsList(stops: List<RouteStop>, totalDistance: Double) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        stops.forEachIndexed { index, stop ->
            Text("${index + 1}. ${stop.label} (${String.format("%.1f km", stop.distanceFromPreviousKm)})")
        }
        Text("Total distance: ${String.format("%.1f km", totalDistance)}", fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun OrdersList(
    orders: List<Order>,
    customers: List<Customer>,
    onDeleteOrder: (Order) -> Unit,
    modifier: Modifier = Modifier
) {
    if (orders.isEmpty()) {
        OrdersEmptyState(
            title = "No orders yet",
            body = "Add an order to see it here."
        )
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = modifier.fillMaxWidth()
        ) {
            items(orders) { order ->
                val customer = customers.find { it.id == order.customerId }
                OrderCard(order = order, customerName = customer?.shopName ?: "Unknown", onDelete = {
                    onDeleteOrder(order)
                })
            }
        }
    }
}

@Composable
private fun OrdersEmptyState(title: String, body: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text(body, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun OrderCard(order: Order, customerName: String, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(customerName, style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete order")
                }
            }
            Text("Quantity: ${order.quantityKg} kg @ ₹${order.pricePerKg}")
            Text("Total: ${CurrencyUtils.format(order.quantityKg * order.pricePerKg)}")
            Text("Date: ${DateUtils.formatForList(order.date)}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun OrdersFooter(orders: List<Order>) {
    val today = DateUtils.startOfDay(Date())
    val todaysOrders = orders.filter { DateUtils.startOfDay(it.date) == today }
    val totalKg = todaysOrders.sumOf { it.quantityKg }
    val totalValue = FinanceUtils.aggregateOrdersValue(todaysOrders)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Today's summary", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text("Total kg: ${"%.2f".format(totalKg)}")
            Text("Total value: ${CurrencyUtils.format(totalValue)}")
        }
    }
}
