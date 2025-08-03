package com.hezapp.ekonomis.core.domain.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

abstract class ITimeService {
    abstract fun getCalendar() : Calendar
    abstract fun getTimezone() : TimeZone
    abstract fun getLocale() : Locale

    private val eDDMMMyyyyFormat by lazy {
        SimpleDateFormat("E, dd-MMM-yyyy", getLocale())
    }

    @Suppress("PrivatePropertyName")
    private val MMMMyyyyFormat by lazy {
        SimpleDateFormat("MMMM yyyy", getLocale())
    }

    private val MMMyyyyFormat by lazy {
        SimpleDateFormat("MMM yyyy", getLocale())
    }

    fun toEddMMMyyyy(timeInMillis: Long): String =
        eDDMMMyyyyFormat.format(getCalendar().apply {
            this.timeInMillis = timeInMillis
        }.time)

    fun toEddMMMyyyy(date: Date): String =
        eDDMMMyyyyFormat.format(date)

    fun toMMMMyyyy(timeInMillis: Long): String =
        MMMMyyyyFormat.format(getCalendar().apply { 
            this.timeInMillis = timeInMillis
        }.time)

    fun toMMMyyyy(timeInMillis: Long) : String =
        MMMyyyyFormat.format(getCalendar().apply {
            this.timeInMillis = timeInMillis
        }.time)
        
}