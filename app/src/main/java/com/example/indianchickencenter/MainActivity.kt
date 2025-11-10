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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.indianchickencenter.model.AppDatabase
import com.example.indianchickencenter.model.CustomerRepository
import com.example.indianchickencenter.model.OrderRepository
import com.example.indianchickencenter.model.PaymentRepository
import com.example.indianchickencenter.model.ProcurementRepository
import com.example.indianchickencenter.ui.BottomNavigationBar
import com.example.indianchickencenter.ui.CustomersScreen
import com.example.indianchickencenter.ui.OrdersScreen
import com.example.indianchickencenter.ui.PaymentsScreen
import com.example.indianchickencenter.ui.theme.IndianChickenCenterTheme
import com.example.indianchickencenter.viewmodel.CustomerViewModel
import com.example.indianchickencenter.viewmodel.CustomerViewModelFactory
import com.example.indianchickencenter.viewmodel.OrderViewModel
import com.example.indianchickencenter.viewmodel.OrderViewModelFactory
import com.example.indianchickencenter.viewmodel.PaymentViewModel
import com.example.indianchickencenter.viewmodel.PaymentViewModelFactory
import com.example.indianchickencenter.viewmodel.ProcurementViewModel
import com.example.indianchickencenter.viewmodel.ProcurementViewModelFactory
import com.example.indianchickencenter.viewmodel.RouteViewModel
import com.example.indianchickencenter.viewmodel.RouteViewModelFactory

class MainActivity : ComponentActivity() {

    private val customerViewModel: CustomerViewModel by viewModels {
        CustomerViewModelFactory(application)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(applicationContext)
        val orderRepository = OrderRepository(database.orderDao())
        val paymentRepository = PaymentRepository(database.paymentDao())
        val procurementRepository = ProcurementRepository(database.procurementDao())
        val customerRepository = CustomerRepository(database.customerDao())

        setContent {
            IndianChickenCenterTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: "customers"
                val snackbarHostState = remember { SnackbarHostState() }

                val orderViewModel: OrderViewModel = viewModel(factory = OrderViewModelFactory(orderRepository))
                val paymentViewModel: PaymentViewModel = viewModel(factory = PaymentViewModelFactory(paymentRepository))
                val procurementViewModel: ProcurementViewModel = viewModel(
                    factory = ProcurementViewModelFactory(procurementRepository, orderRepository)
                )
                val routeViewModel: RouteViewModel = viewModel(
                    factory = RouteViewModelFactory(orderRepository, customerRepository, procurementRepository)
                )

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
                                onUpdateLocation = customerViewModel::updateLocation
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
