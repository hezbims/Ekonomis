package com.hezapp.ekonomis.core.domain.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

abstract class ITimeService {
    abstract fun getCalendar() : Calendar
    abstract fun getTimezone() : TimeZone
    fun getZoneId() = getTimezone().toZoneId()
    abstract fun getLocale() : Locale

    private val dayDateMonthYearFormat by lazy {
        SimpleDateFormat("E, dd-MMM-yyyy", getLocale())
    }

    private val monthYearWordLongFormat by lazy {
        SimpleDateFormat("MMMM yyyy", getLocale())
    }

    private val monthYearWordShortFormat by lazy {
        SimpleDateFormat("MMM yyyy", getLocale())
    }

    private val yearMonthDateFormat by lazy {
        DateTimeFormatter.ofPattern("yyyy-MM-dd", getLocale())
    }

    fun toEddMMMyyyy(timeInMillis: Long): String =
        dayDateMonthYearFormat.format(getCalendar().apply {
            this.timeInMillis = timeInMillis
        }.time)

    fun toMMMMyyyy(timeInMillis: Long): String =
        monthYearWordLongFormat.format(getCalendar().apply {
            this.timeInMillis = timeInMillis
        }.time)

    fun toMMMyyyy(timeInMillis: Long) : String =
        monthYearWordShortFormat.format(getCalendar().apply {
            this.timeInMillis = timeInMillis
        }.time)

    fun toYYYYMMdd(timeInMillis: Long) : String =
        Instant.ofEpochMilli(timeInMillis)
            .atZone(getZoneId())
            .format(yearMonthDateFormat)
}