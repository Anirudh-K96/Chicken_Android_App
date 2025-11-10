package com.example.indianchickencenter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.indianchickencenter.model.Order
import com.example.indianchickencenter.model.OrderRepository
import com.example.indianchickencenter.model.Procurement
import com.example.indianchickencenter.model.ProcurementRepository
import com.example.indianchickencenter.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date

data class InventoryState(
    val procurement: Procurement?,
    val orderedKg: Double,
    val remainingKg: Double
)

class ProcurementViewModel(
    private val procurementRepository: ProcurementRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val today = MutableStateFlow(DateUtils.startOfDay(Date()))

    private val procurementFlow = today.flatMapLatest { day ->
        procurementRepository.observeProcurementFor(day)
    }

    private val todaysOrdersFlow = today.flatMapLatest { day ->
        orderRepository.getAllOrders().map { orders ->
            orders.filter { order -> DateUtils.startOfDay(order.date) == day }
        }
    }

    val inventory: StateFlow<InventoryState> = combine(procurementFlow, todaysOrdersFlow) { procurement, orders ->
        val orderedKg = orders.sumOf(Order::quantityKg)
        val remaining = (procurement?.quantityKg ?: 0.0) - orderedKg
        InventoryState(procurement, orderedKg, remaining)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = InventoryState(procurement = null, orderedKg = 0.0, remainingKg = 0.0)
    )

    init {
        viewModelScope.launch {
            procurementRepository.getOrCreateTodayDefault(Date())
        }
    }

    fun updateQuantity(quantityKg: Double) {
        viewModelScope.launch {
            val day = today.value
            procurementRepository.updateQuantity(day, quantityKg)
        }
    }

    fun refreshDate(date: Date) {
        today.value = DateUtils.startOfDay(date)
    }
}
