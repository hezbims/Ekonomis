package com.hezapp.ekonomis.core.domain.utils

import java.util.Calendar

fun Calendar.toBeginningOfMonth() : Calendar {
    return calendarProvider.getCalendar().apply {
        timeInMillis = this@toBeginningOfMonth.timeInMillis
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
}

fun Long.getNextMonthYear() : Long {
    val currentMonthYearCalendar = toCalendar()
    val nextMonthYearCalendar = currentMonthYearCalendar.apply {
        add(Calendar.MONTH, 1)
    }
    return nextMonthYearCalendar.timeInMillis

}

fun Long.getPreviousMonthYear() : Long {
    val currentMonthYearCalendar = toCalendar()
    val prevMonthYearCalendar = currentMonthYearCalendar.apply {
        add(Calendar.MONTH, -1)
    }
    return prevMonthYearCalendar.timeInMillis
}

fun Long.isInAMonthYearPeriod(monthYearPeriod: Long) : Boolean {
    val currentMonthYearPeriodCalendar = monthYearPeriod.toCalendar().toBeginningOfMonth()
    val nextMonthYearPeriod = calendarProvider.getCalendar().apply {
        timeInMillis = currentMonthYearPeriodCalendar.timeInMillis
        add(Calendar.MONTH, 1)
    }

    return this >= currentMonthYearPeriodCalendar.timeInMillis &&
            this < nextMonthYearPeriod.timeInMillis
}

fun Long.toCalendar() : Calendar {
    return calendarProvider.getCalendar().apply {
        timeInMillis = this@toCalendar
    }
}