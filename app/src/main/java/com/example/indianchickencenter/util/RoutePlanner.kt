package com.example.indianchickencenter.util

import com.example.indianchickencenter.model.Customer
import com.example.indianchickencenter.model.Procurement
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

data class LatLng(val latitude: Double, val longitude: Double)

enum class RouteStopType { BASE, PROCUREMENT, CUSTOMER }

data class RouteStop(
    val label: String,
    val type: RouteStopType,
    val latitude: Double,
    val longitude: Double,
    val relatedCustomer: Customer? = null,
    val distanceFromPreviousKm: Double = 0.0
)

data class RoutePlan(
    val stops: List<RouteStop>,
    val totalDistanceKm: Double
)

data class CustomerRouteInfo(
    val customer: Customer,
    val latitude: Double,
    val longitude: Double
)

object RoutePlanner {

    private const val EARTH_RADIUS_KM = 6371.0

    val baseLocation = LatLng(latitude = 12.9141, longitude = 74.8560) // Default base: Mangaluru
    val procurementLocation = LatLng(latitude = 12.9716, longitude = 77.5946) // Bengaluru

    fun planRoute(customers: List<CustomerRouteInfo>, procurement: Procurement?): RoutePlan? {
        if (customers.isEmpty() || procurement == null) return null

        val stops = mutableListOf<RouteStop>()
        var previous = baseLocation
        var totalDistance = 0.0

        stops += RouteStop(
            label = "Base",
            type = RouteStopType.BASE,
            latitude = baseLocation.latitude,
            longitude = baseLocation.longitude,
            distanceFromPreviousKm = 0.0
        )

        val procurementLatLng = procurementLocation
        val distanceToProcurement = haversine(previous, procurementLatLng)
        totalDistance += distanceToProcurement
        stops += RouteStop(
            label = "Procurement (${procurement.city})",
            type = RouteStopType.PROCUREMENT,
            latitude = procurementLatLng.latitude,
            longitude = procurementLatLng.longitude,
            distanceFromPreviousKm = distanceToProcurement
        )
        previous = procurementLatLng

        val remainingCustomers = customers.toMutableList()
        while (remainingCustomers.isNotEmpty()) {
            val next = remainingCustomers.minByOrNull { customer ->
                val latLng = LatLng(customer.latitude, customer.longitude)
                haversine(previous, latLng)
            }!!
            val nextLatLng = LatLng(next.latitude, next.longitude)
            val legDistance = haversine(previous, nextLatLng)
            totalDistance += legDistance
            stops += RouteStop(
                label = next.customer.shopName,
                type = RouteStopType.CUSTOMER,
                latitude = next.latitude,
                longitude = next.longitude,
                relatedCustomer = next.customer,
                distanceFromPreviousKm = legDistance
            )
            previous = nextLatLng
            remainingCustomers.remove(next)
        }

        return RoutePlan(stops = stops, totalDistanceKm = totalDistance)
    }

    private fun haversine(a: LatLng, b: LatLng): Double {
        val dLat = Math.toRadians(b.latitude - a.latitude)
        val dLon = Math.toRadians(b.longitude - a.longitude)
        val lat1 = Math.toRadians(a.latitude)
        val lat2 = Math.toRadians(b.latitude)

        val sinLat = sin(dLat / 2).pow(2.0)
        val sinLon = sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(
            sqrt(sinLat + cos(lat1) * cos(lat2) * sinLon),
            sqrt(1 - (sinLat + cos(lat1) * cos(lat2) * sinLon))
        )
        return EARTH_RADIUS_KM * c
    }
}
