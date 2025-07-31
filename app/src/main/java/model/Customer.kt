package com.example.indianchickencenter.model

    import androidx.room.Entity
    import androidx.room.PrimaryKey

    @Entity(tableName = "customers")
    data class Customer(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        val shopName: String,
        val ownerName: String,
        val contact: String
    )