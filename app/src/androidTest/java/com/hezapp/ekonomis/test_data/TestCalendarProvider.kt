package com.hezapp.ekonomis.test_data

import com.hezapp.ekonomis.core.domain.utils.CalendarProvider
import java.util.Calendar

class TestCalendarProvider : CalendarProvider() {
    override fun getCalendar(): Calendar {
        return Calendar.getInstance(
            super.getTimezone()
        ).apply {
            set(
                2020,
                1,
                15,
                0,
                0,
                0
            )
        }
    }
}

val testCalendarProvider = TestCalendarProvider()