package com.hezapp.ekonomis.core.domain.utils

import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 * For Preview Composable Only
 */
class PreviewTimeService : ITimeService() {
    override fun getCalendar(): Calendar {
        return Calendar.getInstance(getTimezone())
    }

    override fun getTimezone(): TimeZone {
        return TimeZone.getTimeZone("UTC+8")
    }

    override fun getLocale(): Locale {
        return Locale.forLanguageTag("id-ID")
    }
}