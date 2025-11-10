package com.example.indianchickencenter.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "procurements",
    indices = [Index(value = ["procurementDate"], unique = true)]
)
data class Procurement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val procurementDate: Date,
    val city: String = "Bengaluru",
    val quantityKg: Double = 5000.0
)
