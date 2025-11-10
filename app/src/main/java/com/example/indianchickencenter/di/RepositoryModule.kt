package com.example.indianchickencenter.di

import com.example.indianchickencenter.model.CustomerDao
import com.example.indianchickencenter.model.CustomerRepository
import com.example.indianchickencenter.model.OrderDao
import com.example.indianchickencenter.model.OrderRepository
import com.example.indianchickencenter.model.PaymentDao
import com.example.indianchickencenter.model.PaymentRepository
import com.example.indianchickencenter.model.ProcurementDao
import com.example.indianchickencenter.model.ProcurementRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideCustomerRepository(dao: CustomerDao): CustomerRepository = CustomerRepository(dao)

    @Provides
    @Singleton
    fun provideOrderRepository(dao: OrderDao): OrderRepository = OrderRepository(dao)

    @Provides
    @Singleton
    fun providePaymentRepository(dao: PaymentDao): PaymentRepository = PaymentRepository(dao)

    @Provides
    @Singleton
    fun provideProcurementRepository(dao: ProcurementDao): ProcurementRepository = ProcurementRepository(dao)
}
