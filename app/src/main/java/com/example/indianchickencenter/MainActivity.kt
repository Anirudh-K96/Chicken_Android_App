package com.example.indianchickencenter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.indianchickencenter.model.AppDatabase
import com.example.indianchickencenter.model.Customer
import com.example.indianchickencenter.model.OrderRepository
import com.example.indianchickencenter.ui.OrdersScreen
import com.example.indianchickencenter.ui.theme.IndianChickenCenterTheme
import com.example.indianchickencenter.viewmodel.CustomerViewModel
import com.example.indianchickencenter.viewmodel.CustomerViewModelFactory
import com.example.indianchickencenter.viewmodel.OrderViewModel
import com.example.indianchickencenter.viewmodel.OrderViewModelFactory

class MainActivity : ComponentActivity() {

    private val customerViewModel: CustomerViewModel by viewModels {
        CustomerViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val orderRepository = OrderRepository(
            AppDatabase.getDatabase(application).orderDao()
        )
        val orderFactory = OrderViewModelFactory(orderRepository)

        setContent {
            IndianChickenCenterTheme {
                val navController = rememberNavController()
                val orderViewModel: OrderViewModel = viewModel(factory = orderFactory)

                Scaffold(
                    bottomBar = { BottomNavigationBar(navController) }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "customers",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("customers") {
                            CustomerScreen(customerViewModel)
                        }
                        composable("orders") {
                            OrdersScreen(
                                orders = orderViewModel.allOrders.collectAsState().value,
                                onAddOrder = { orderViewModel.insert(it) }
                            )
                        }
                    }
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

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (shopName.isNotBlank() && ownerName.isNotBlank() && contact.isNotBlank()) {
                    viewModel.insert(
                        Customer(shopName = shopName, ownerName = ownerName, contact = contact)
                    )
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

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("Customers", "customers", Icons.Default.Person),
        BottomNavItem("Orders", "orders", Icons.Default.ShoppingCart)
    )

    NavigationBar {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.name) },
                label = { Text(item.name) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

data class BottomNavItem(
    val name: String,
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
