package com.example.indianchickencenter.model

    import android.content.Context
    import androidx.room.Database
    import androidx.room.Room
    import androidx.room.RoomDatabase

    @Database(entities = [Customer::class], version = 1)
    abstract class AppDatabase : RoomDatabase() {
        abstract fun customerDao(): CustomerDao
        abstract fun orderDao(): OrderDao

        companion object {
            @Volatile private var INSTANCE: AppDatabase? = null

            fun getDatabase(context: Context): AppDatabase {
                return INSTANCE ?: synchronized(this) {
                    Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "chickenbiz_db"
                    ).build().also { INSTANCE = it }
                }
            }
        }
    }