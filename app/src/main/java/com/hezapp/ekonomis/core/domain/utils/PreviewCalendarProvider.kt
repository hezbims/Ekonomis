package com.hezapp.ekonomis.core.domain.utils

import java.util.TimeZone

/**
 * For Preview Composable Only
 */
class PreviewCalendarProvider : CalendarProvider() {
    override fun getTimezone(): TimeZone {
        return TimeZone.getTimeZone("UTC+8")
    }
}