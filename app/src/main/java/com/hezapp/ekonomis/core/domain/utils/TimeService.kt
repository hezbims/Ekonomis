package com.hezapp.ekonomis.core.domain.utils

import java.time.Instant
import java.util.Locale
import java.util.TimeZone

class TimeService : ITimeService() {
    override fun getCurrentTimeInMillis() : Long {
        return Instant.now().toEpochMilli()
    }

    override fun getTimezone() : TimeZone {
        return TimeZone.getTimeZone("GMT+8")
    }

    override fun getLocale(): Locale {
        return Locale.forLanguageTag("id-ID")
    }
}