package com.hezapp.ekonomis.core.domain.utils

import org.koin.core.context.GlobalContext
import java.util.Calendar
import java.util.TimeZone

open class CalendarProvider {
    open fun getCalendar() : Calendar {
        return Calendar.getInstance(
            getTimezone()
        )
    }

    open fun getTimezone() : TimeZone {
        return TimeZone.getTimeZone("UTC+8")
    }
}

val calendarProvider : CalendarProvider by lazy {
    GlobalContext.get().get()
}