package com.hezapp.ekonomis.test_utils.seeder.snapshot

import android.os.Build
import androidx.annotation.RequiresApi
import com.hezapp.ekonomis.core.domain.monthly_stock.entity.MonthlyStockEntity
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import java.time.Instant
import java.time.YearMonth

data class MonthlyStockSnapshot(
    val id: Int,
    val monthYearPeriodInMillis: Long,
    val monthYearPeriod: YearMonth,
    val cartonQuantity: Int,
    val pieceQuantity: Int,
    val productId: Int,
) {
    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun fromRoomEntity(
            entity: MonthlyStockEntity,
            timeService: ITimeService,
        ) : MonthlyStockSnapshot {
            return MonthlyStockSnapshot(
                id = entity.id,
                monthYearPeriodInMillis = entity.monthYearPeriod,
                cartonQuantity = entity.quantityPerUnitType.cartonQuantity,
                pieceQuantity = entity.quantityPerUnitType.pieceQuantity,
                monthYearPeriod = YearMonth.from(
                    Instant.ofEpochMilli(entity.monthYearPeriod)
                        .atZone(timeService.getZoneId())
                ),
                productId = entity.productId,
            )
        }
    }
}
