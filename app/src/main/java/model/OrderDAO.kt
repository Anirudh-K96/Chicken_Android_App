package com.example.indianchickencenter.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order)

    @Query("SELECT * FROM orders WHERE customerId = :customerId ORDER BY date DESC")
    fun getOrdersByCustomer(customerId: Int): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE date = :date ORDER BY orderId DESC")
    fun getOrdersForDate(date: String): Flow<List<Order>>

    @Query("SELECT * FROM orders ORDER BY date DESC")
    fun getAllOrders(): Flow<List<Order>>
}