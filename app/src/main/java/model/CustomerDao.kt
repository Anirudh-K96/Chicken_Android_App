package com.example.indianchickencenter.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(customer: Customer)

    @Query("SELECT * FROM customers ORDER BY id DESC")
    fun getAllCustomers(): LiveData<List<Customer>>

    @Query("SELECT * FROM customers ORDER BY shopName ASC")
    fun observeCustomers(): Flow<List<Customer>>

    @Query(
        """
        SELECT c.*,
        (SELECT COALESCE(SUM(o.quantityKg * o.pricePerKg), 0.0) FROM orders o WHERE o.customerId = c.id) AS totalOrdered,
        (SELECT COALESCE(SUM(p.amount), 0.0) FROM payments p WHERE p.customerId = c.id) AS totalPaid
        FROM customers c
        ORDER BY c.shopName ASC
        """
    )
    fun observeCustomersWithBalance(): Flow<List<CustomerWithBalance>>

    @Query("UPDATE customers SET latitude = :latitude, longitude = :longitude WHERE id = :customerId")
    suspend fun updateLocation(customerId: Int, latitude: Double?, longitude: Double?)
}
