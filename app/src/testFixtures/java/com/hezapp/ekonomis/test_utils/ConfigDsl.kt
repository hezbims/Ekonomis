package com.hezapp.ekonomis.test_utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import org.koin.core.Koin
import java.time.YearMonth
import java.time.ZoneId

class ConfigDsl(
    private val koin: Koin
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun currentTimeIs(yearMonth: YearMonth, zoneId: ZoneId = ZoneId.of("UTC+8")){
        (koin.get<ITimeService>() as TestTimeService).setCurrentTime(
            localDate = yearMonth.atDay(1),
            zoneId = zoneId,
        )
    }
}