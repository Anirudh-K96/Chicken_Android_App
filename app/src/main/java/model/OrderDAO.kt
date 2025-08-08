package com.example.indianchickencenter.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: Order)

    @Query("SELECT * FROM orders ORDER BY date DESC")
    fun getAllOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE customerId = :customerId ORDER BY date DESC")
    fun getOrdersForCustomer(customerId: Int): Flow<List<Order>>

    @Delete
    suspend fun delete(order: Order)
}