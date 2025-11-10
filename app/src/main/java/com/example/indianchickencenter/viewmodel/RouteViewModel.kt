package com.example.indianchickencenter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.indianchickencenter.model.CustomerRepository
import com.example.indianchickencenter.model.OrderRepository
import com.example.indianchickencenter.model.ProcurementRepository
import com.example.indianchickencenter.util.CustomerRouteInfo
import com.example.indianchickencenter.util.DateUtils
import com.example.indianchickencenter.util.RoutePlan
import com.example.indianchickencenter.util.RoutePlanner
import com.example.indianchickencenter.util.RouteStop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import java.util.Date

data class RouteUiState(
    val isLoading: Boolean = false,
    val stops: List<RouteStop> = emptyList(),
    val totalDistanceKm: Double = 0.0,
    val missingLocationCustomers: List<String> = emptyList(),
    val errorMessage: String? = null
)

class RouteViewModel(
    private val orderRepository: OrderRepository,
    private val customerRepository: CustomerRepository,
    private val procurementRepository: ProcurementRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RouteUiState())
    val uiState: StateFlow<RouteUiState> = _uiState

    fun generateRouteForToday() {
        viewModelScope.launch {
            _uiState.value = RouteUiState(isLoading = true)
            val today = DateUtils.startOfDay(Date())
            val orders = orderRepository.getAllOrders().first()
            val customers = customerRepository.observeCustomers().first()
            val procurement = procurementRepository.getOrCreateTodayDefault(today)

            val todaysOrders = orders.filter { DateUtils.startOfDay(it.date) == today }
            if (todaysOrders.isEmpty()) {
                _uiState.value = RouteUiState(
                    isLoading = false,
                    errorMessage = "No deliveries scheduled today."
                )
                return@launch
            }

            val customerMap = customers.associateBy { it.id }
            val missing = mutableListOf<String>()
            val candidates = todaysOrders.mapNotNull { order ->
                val customer = customerMap[order.customerId]
                if (customer == null) {
                    null
                } else if (customer.latitude == null || customer.longitude == null) {
                    missing += customer.shopName
                    null
                } else {
                    CustomerRouteInfo(
                        customer = customer,
                        latitude = customer.latitude,
                        longitude = customer.longitude
                    )
                }
            }.distinctBy { it.customer.id }

            if (candidates.isEmpty()) {
                _uiState.value = RouteUiState(
                    isLoading = false,
                    missingLocationCustomers = missing,
                    errorMessage = "Set locations for today's customers to plan the route."
                )
                return@launch
            }

            val plan: RoutePlan? = RoutePlanner.planRoute(candidates, procurement)
            if (plan == null) {
                _uiState.value = RouteUiState(
                    isLoading = false,
                    errorMessage = "Unable to build route."
                )
                return@launch
            }

            _uiState.value = RouteUiState(
                isLoading = false,
                stops = plan.stops,
                totalDistanceKm = plan.totalDistanceKm,
                missingLocationCustomers = missing
            )
        }
    }
}
