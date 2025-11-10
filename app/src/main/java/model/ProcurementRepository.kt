package com.example.indianchickencenter.model

import com.example.indianchickencenter.util.DateUtils
import kotlinx.coroutines.flow.Flow
import java.util.Date

class ProcurementRepository(private val dao: ProcurementDao) {

    fun observeProcurementFor(date: Date): Flow<Procurement?> =
        dao.observeProcurement(DateUtils.startOfDay(date))

    suspend fun getOrCreateTodayDefault(
        date: Date,
        defaultQuantity: Double = 5000.0,
        city: String = "Bengaluru"
    ): Procurement {
        val normalizedDate = DateUtils.startOfDay(date)
        val existing = dao.getProcurement(normalizedDate)
        return if (existing != null) {
            existing
        } else {
            val procurement = Procurement(
                procurementDate = normalizedDate,
                city = city,
                quantityKg = defaultQuantity
            )
            dao.upsert(procurement)
            procurement
        }
    }

    suspend fun updateQuantity(date: Date, quantityKg: Double, city: String = "Bengaluru") {
        val normalizedDate = DateUtils.startOfDay(date)
        val procurement = Procurement(
            procurementDate = normalizedDate,
            city = city,
            quantityKg = quantityKg
        )
        dao.upsert(procurement)
    }
}
