package com.hezapp.ekonomis.core.domain.utils

import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

abstract class ITimeService {
    abstract fun getCurrentTimeInMillis() : Long
    abstract fun getTimezone() : TimeZone
    abstract fun getLocale() : Locale
    fun getZoneId(): ZoneId = getTimezone().toZoneId()
    fun getCalendar() : Calendar = Calendar.getInstance(getTimezone()).apply {
        timeInMillis = getCurrentTimeInMillis()
    }
    fun getLocalDate(): LocalDate =
        Instant.ofEpochMilli(getCurrentTimeInMillis())
            .atZone(getZoneId())
            .toLocalDate()

    fun convertToTimeToEpochMillis(yearMonth: YearMonth) : Long =
        yearMonth
            .atDay(1)
            .atStartOfDay(getZoneId())
            .toInstant()
            .toEpochMilli()

    private val dayDateMonthYearFormat by lazy {
        DateTimeFormatter.ofPattern("E, dd-MMM-yyyy", getLocale())
    }
    private val dayDateMonthYearFormatV2 by lazy {
        DateTimeFormatter.ofPattern("E, dd-MMM-yyyy", getLocale())
    }

    private val monthYearWordLongFormat by lazy {
        DateTimeFormatter.ofPattern("MMMM yyyy", getLocale())
    }

    private val monthYearWordShortFormat by lazy {
        DateTimeFormatter.ofPattern("MMM yyyy", getLocale())
    }

    private val yearMonthDateFormat by lazy {
        DateTimeFormatter.ofPattern("yyyy-MM-dd", getLocale())
    }

    fun toEddMMMyyyy(timeInMillis: Long): String =
        dayDateMonthYearFormat.format(getZonedDateTimeFrom(timeInMillis))

    fun toEddMMMyyyy(date: LocalDate): String =
        ZonedDateTime.of(date.atStartOfDay(), getZoneId())
            .format(dayDateMonthYearFormatV2)

    fun toMMMMyyyy(timeInMillis: Long): String =
        monthYearWordLongFormat.format(getZonedDateTimeFrom(timeInMillis))

    fun toMMMyyyy(timeInMillis: Long) : String =
        monthYearWordShortFormat.format(getZonedDateTimeFrom(timeInMillis))

    fun toYYYYMMdd(timeInMillis: Long) : String =
        Instant.ofEpochMilli(timeInMillis)
            .atZone(getZoneId())
            .format(yearMonthDateFormat)

    fun getZonedDateTimeFrom(timeInMillis: Long) : ZonedDateTime {
        return Instant.ofEpochMilli(timeInMillis)
            .atZone(getZoneId())
    }
}