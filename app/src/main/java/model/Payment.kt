package com.example.indianchickencenter.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["customerId"])]
)
data class Payment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerId: Int,
    val date: Date,
    val amount: Double,
    val method: String,
    val note: String? = null
)
