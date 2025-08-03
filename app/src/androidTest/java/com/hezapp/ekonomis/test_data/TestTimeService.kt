package com.hezapp.ekonomis.test_data

import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.core.domain.utils.TimeService
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class TestTimeService private constructor() : ITimeService()  {
    companion object {
        private val productionTimeService = TimeService()
        private val instance = TestTimeService()
        fun get() = instance

        private val defaultTestTimezone = productionTimeService.getTimezone()
        private val defaultTestCalendar = productionTimeService.getCalendar().apply {
            set(
                2020,
                1,
                15,
                0,
                0,
                0
            )
        }

        /**
         * Silahkan ubah ini sesuai kebutuhan
         */
        private var testTimeZone: TimeZone =  defaultTestTimezone
        @Suppress("unused")
        fun setTestTimeZone(newTimeZone: TimeZone){
            testTimeZone = newTimeZone
        }

        /**
         * Silahkan ubah ini sesuai kebutuhan test
         */
        private var testCalendar: Calendar = defaultTestCalendar
        @Suppress("unused")
        fun setTestCalendar(newCalendar: Calendar){
            testCalendar = newCalendar
        }

        fun reset(){
            testTimeZone = defaultTestTimezone
            testCalendar = defaultTestCalendar
        }
    }
    override fun getCalendar(): Calendar = testCalendar.clone() as Calendar
    override fun getTimezone(): TimeZone = testTimeZone.clone() as TimeZone
    override fun getLocale(): Locale = productionTimeService.getLocale()
}

val testCalendarProvider = TestTimeService.get()