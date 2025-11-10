package com.example.indianchickencenter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.indianchickencenter.ui.BottomNavigationBar
import com.example.indianchickencenter.ui.CustomersScreen
import com.example.indianchickencenter.ui.OrdersScreen
import com.example.indianchickencenter.ui.PaymentsScreen
import com.example.indianchickencenter.ui.theme.IndianChickenCenterTheme
import com.example.indianchickencenter.viewmodel.CustomerViewModel
import com.example.indianchickencenter.viewmodel.OrderViewModel
import com.example.indianchickencenter.viewmodel.PaymentViewModel
import com.example.indianchickencenter.viewmodel.ProcurementViewModel
import com.example.indianchickencenter.viewmodel.RouteViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val customerViewModel: CustomerViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            IndianChickenCenterTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: "customers"
                val snackbarHostState = remember { SnackbarHostState() }

                val orderViewModel: OrderViewModel = hiltViewModel()
                val paymentViewModel: PaymentViewModel = hiltViewModel()
                val procurementViewModel: ProcurementViewModel = hiltViewModel()
                val routeViewModel: RouteViewModel = hiltViewModel()

                val customers by customerViewModel.allCustomers.collectAsState()
                val customerBalances by customerViewModel.filteredCustomers.collectAsState()
                val searchQuery by customerViewModel.searchText.collectAsState()
                val orders by orderViewModel.allOrders.collectAsState()
                val inventory by procurementViewModel.inventory.collectAsState()
                val payments by paymentViewModel.payments.collectAsState()
                val selectedPaymentCustomerId by paymentViewModel.selectedCustomer.collectAsState()
                val routeUiState by routeViewModel.uiState.collectAsState()

                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text(titleForRoute(currentRoute)) })
                    },
                    bottomBar = { BottomNavigationBar(navController) },
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "customers",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("customers") {
                            CustomersScreen(
                                customers = customerBalances,
                                searchQuery = searchQuery,
                                onSearchChange = customerViewModel::updateSearch,
                                onAddCustomer = customerViewModel::insert,
                                onUpdateLocation = customerViewModel::updateLocation,
                                orders = orders,
                                payments = payments
                            )
                        }
                        composable("orders") {
                            OrdersScreen(
                                customers = customers,
                                orders = orders,
                                inventoryState = inventory,
                                snackbarHostState = snackbarHostState,
                                onAddOrder = orderViewModel::insert,
                                onDeleteOrder = orderViewModel::delete,
                                onUpdateProcurement = procurementViewModel::updateQuantity,
                                onPlanRoute = routeViewModel::generateRouteForToday,
                                routeUiState = routeUiState
                            )
                        }
                        composable("payments") {
                            PaymentsScreen(
                                customers = customerBalances,
                                selectedCustomerId = selectedPaymentCustomerId,
                                onSelectCustomer = paymentViewModel::selectCustomer,
                                payments = payments,
                                onAddPayment = paymentViewModel::recordPayment,
                                snackbarHostState = snackbarHostState
                            )
                        }
                    }
                }
            }
        }
    }

    private fun titleForRoute(route: String): String = when (route) {
        "customers" -> "Customers"
        "orders" -> "Orders"
        "payments" -> "Payments"
        else -> "Indian Chicken Center"
    }
}
