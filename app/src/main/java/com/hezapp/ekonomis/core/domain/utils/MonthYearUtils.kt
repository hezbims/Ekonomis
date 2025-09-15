package com.hezapp.ekonomis.core.domain.utils

import org.koin.core.context.GlobalContext
import java.util.Calendar

fun Calendar.toBeginningOfMonth(timeService: ITimeService = GlobalContext.get().get()) : Calendar {
    return timeService.getCalendar().apply {
        timeInMillis = this@toBeginningOfMonth.timeInMillis
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
}

fun Long.getNextMonthYear(timeService: ITimeService = GlobalContext.get().get()) : Long {
    val currentMonthYearCalendar = toCalendar(timeService)
    val nextMonthYearCalendar = currentMonthYearCalendar.apply {
        add(Calendar.MONTH, 1)
    }
    return nextMonthYearCalendar.timeInMillis

}

fun Long.getPreviousMonthYear(timeService: ITimeService = GlobalContext.get().get()) : Long {
    val currentMonthYearCalendar = toCalendar(timeService)
    val prevMonthYearCalendar = currentMonthYearCalendar.apply {
        add(Calendar.MONTH, -1)
    }
    return prevMonthYearCalendar.timeInMillis
}

fun Long.toCalendar(timeService: ITimeService = GlobalContext.get().get()) : Calendar {
    return timeService.getCalendar().apply {
        timeInMillis = this@toCalendar
    }
}