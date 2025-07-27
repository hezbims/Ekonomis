package com.hezapp.ekonomis.core.domain.utils

import java.util.Calendar
import java.util.TimeZone

interface ITimeService {
    fun getCalendar() : Calendar
    fun getTimezone() : TimeZone
}