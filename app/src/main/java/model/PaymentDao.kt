package com.example.indianchickencenter.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(payment: Payment)

    @Query("SELECT * FROM payments WHERE customerId = :customerId ORDER BY date DESC")
    fun observePaymentsForCustomer(customerId: Int): Flow<List<Payment>>

    @Query("SELECT * FROM payments ORDER BY date DESC")
    fun observeAllPayments(): Flow<List<Payment>>
}
