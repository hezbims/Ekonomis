package com.hezapp.ekonomis.test_data

import com.hezapp.ekonomis.core.domain.utils.ITimeService
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class TestTimeService private constructor() : ITimeService()  {
    companion object {
        var currentTimezone: TimeZone = TimeZone.getTimeZone("GMT+8")

        var currentCalendar: Calendar = Calendar.getInstance(
            currentTimezone
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

        val instance = TestTimeService()
        fun get() = instance
    }
    override fun getCalendar(): Calendar = currentCalendar.clone() as Calendar
    override fun getTimezone(): TimeZone = currentTimezone.clone() as TimeZone
    override fun getLocale(): Locale = Locale.forLanguageTag("id-ID")
}

val testCalendarProvider = TestTimeService.get()