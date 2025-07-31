package com.example.indianchickencenter.model

    import androidx.lifecycle.LiveData
    import androidx.room.*

    @Dao
    interface CustomerDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insert(customer: Customer)

        @Query("SELECT * FROM customers ORDER BY id DESC")
        fun getAllCustomers(): LiveData<List<Customer>>
    }