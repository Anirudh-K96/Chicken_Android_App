package com.example.indianchickencenter.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    private val displayFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    private val shortDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    fun startOfDay(date: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }

    fun formatForList(date: Date): String = displayFormat.format(date)

    fun formatDateOnly(date: Date): String = shortDateFormat.format(date)
}
