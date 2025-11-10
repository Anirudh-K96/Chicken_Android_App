package com.example.indianchickencenter.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Date

class DateConverterTest {

    private val converter = DateConverter()

    @Test
    fun `date to timestamp and back`() {
        val date = Date()
        val timestamp = converter.dateToTimestamp(date)
        val restored = converter.fromTimestamp(timestamp)
        assertEquals(date.time, restored?.time)
    }

    @Test
    fun `null conversions`() {
        assertNull(converter.dateToTimestamp(null))
        assertNull(converter.fromTimestamp(null))
    }
}
