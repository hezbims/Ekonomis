package com.hezapp.ekonomis.test_utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.core.domain.utils.TimeService
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class TestTimeService(
    private var currentTimeInMillis: Long = defaultTestCalendar.timeInMillis,
    private var timeZone: TimeZone = defaultTestTimezone.clone() as TimeZone,
    private val locale: Locale = defaultTestLocale.clone() as Locale,
) : ITimeService()  {

    @RequiresApi(Build.VERSION_CODES.O)
    fun setCurrentTime(localDate: LocalDate, zoneId: ZoneId? = null){
        if (zoneId != null)
            timeZone = TimeZone.getTimeZone(zoneId)

        currentTimeInMillis = localDate
            .atStartOfDay(getTimezone().toZoneId())
            .toInstant()
            .toEpochMilli()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setCurrentTime(yearMonth: YearMonth, zoneId: ZoneId? = null) {
        setCurrentTime(yearMonth.atDay(1), zoneId)
    }

    companion object {
        private val productionTimeService = TimeService()
        private val defaultTestTimezone = productionTimeService.getTimezone()
        private val defaultTestCalendar = productionTimeService.getCalendar().apply {
            set(
                2020,
                1, // februari
                15,
                0,
                0,
                0
            )
        }
        private val defaultTestLocale = productionTimeService.getLocale()

        private val instance = TestTimeService()
        @Deprecated(message = "Avoid using this singleton method. " +
                "Please create new TestTimeService instead for every test, " +
                "and inject it using koin for better isolation")
        fun get() = instance

        private var testTimeZone: TimeZone =  defaultTestTimezone
        @Suppress("unused")
        @Deprecated(message = "Avoid using this singleton method. " +
                "Please create new TestTimeService instead and use setCurrentTime method")
        fun setTestTimeZone(newTimeZone: TimeZone){
            testTimeZone = newTimeZone
        }

        private var testCalendar: Calendar = defaultTestCalendar
        @Suppress("unused")
        @Deprecated(message = "Avoid using this singleton method. " +
                "Please create new TestTimeService instead and use setCurrentTime method")
        fun setTestCalendar(newCalendar: Calendar){
            testCalendar = newCalendar
        }

        @Deprecated(message = "Avoid using this singleton method. " +
                "Please create new TestTimeService instead for every test, " +
                "and inject it using koin for better isolation")
        fun reset(){
            testTimeZone = defaultTestTimezone
            testCalendar = defaultTestCalendar
        }
    }
    override fun getCurrentTimeInMillis(): Long = currentTimeInMillis
    override fun getTimezone(): TimeZone = timeZone
    override fun getLocale(): Locale = locale
}

@Deprecated("Avoid this singleton. " +
        "Please create new TestTimeService for every test instead for better test isolation")
val testCalendarProvider = TestTimeService.get()