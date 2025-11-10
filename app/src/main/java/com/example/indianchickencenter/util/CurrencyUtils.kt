package com.example.indianchickencenter.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {
    private val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    fun format(amount: Double): String = formatter.format(amount)
}
