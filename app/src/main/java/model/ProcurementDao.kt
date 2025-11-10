package com.example.indianchickencenter.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ProcurementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(procurement: Procurement)

    @Query("SELECT * FROM procurements WHERE procurementDate = :date LIMIT 1")
    suspend fun getProcurement(date: Date): Procurement?

    @Query("SELECT * FROM procurements WHERE procurementDate = :date LIMIT 1")
    fun observeProcurement(date: Date): Flow<Procurement?>
}
