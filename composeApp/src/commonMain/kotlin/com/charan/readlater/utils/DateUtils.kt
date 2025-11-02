package com.charan.readlater.utils

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

object DateUtils {


    @OptIn(ExperimentalTime::class)
    fun formatReadableDateFromIso(isoString: String): String {
        val instant = Instant.parse(isoString)
        val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        val month = localDate.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
        val day = localDate.dayOfMonth
        val year = localDate.year

        return when {
            year == currentDate.year -> "$month $day"
            year == currentDate.year - 1 -> "$month $day, $year"
            else -> "$month $day, $year"
        }
    }

    @OptIn(ExperimentalTime::class)
    fun isoStringToMillis(isoString: String) : Long {
        val instant = Instant.parse(isoString)
        return instant.toEpochMilliseconds()
    }
}