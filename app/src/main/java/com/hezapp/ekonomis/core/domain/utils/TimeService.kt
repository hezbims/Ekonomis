package com.hezapp.ekonomis.core.domain.utils

import org.koin.core.context.GlobalContext
import java.util.Calendar
import java.util.TimeZone

class TimeService : ITimeService {
    override fun getCalendar() : Calendar {
        return Calendar.getInstance(
            getTimezone()
        )
    }

    override fun getTimezone() : TimeZone {
        return TimeZone.getTimeZone("GMT+8")
    }
}

val calendarProvider : ITimeService by lazy {
    GlobalContext.get().get()
}