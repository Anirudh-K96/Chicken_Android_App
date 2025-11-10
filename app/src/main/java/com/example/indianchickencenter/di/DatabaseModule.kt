package com.example.indianchickencenter.di

import android.content.Context
import com.example.indianchickencenter.model.AppDatabase
import com.example.indianchickencenter.model.CustomerDao
import com.example.indianchickencenter.model.OrderDao
import com.example.indianchickencenter.model.PaymentDao
import com.example.indianchickencenter.model.ProcurementDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.getDatabase(context)

    @Provides
    fun provideCustomerDao(db: AppDatabase): CustomerDao = db.customerDao()

    @Provides
    fun provideOrderDao(db: AppDatabase): OrderDao = db.orderDao()

    @Provides
    fun providePaymentDao(db: AppDatabase): PaymentDao = db.paymentDao()

    @Provides
    fun provideProcurementDao(db: AppDatabase): ProcurementDao = db.procurementDao()
}
