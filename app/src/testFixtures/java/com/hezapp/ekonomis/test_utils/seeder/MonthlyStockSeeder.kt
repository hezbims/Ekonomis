package com.hezapp.ekonomis.test_utils.seeder

import android.os.Build
import androidx.annotation.RequiresApi
import com.hezapp.ekonomis.core.data.monthly_stock.dao.MonthlyStockDao
import com.hezapp.ekonomis.core.domain.monthly_stock.entity.MonthlyStockEntity
import com.hezapp.ekonomis.core.domain.monthly_stock.entity.QuantityPerUnitType
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.test_utils.seeder.snapshot.MonthlyStockSnapshot
import kotlinx.coroutines.runBlocking
import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import java.time.YearMonth

class MonthlyStockSeeder(
    koin: Koin = GlobalContext.get(),
) {
    private val dao : MonthlyStockDao = koin.get()
    private val timeService: ITimeService = koin.get()

    @RequiresApi(Build.VERSION_CODES.O)
    fun run(
        productId: Int,
        monthYear: YearMonth,
        cartonQuantity: Int,
        pieceQuantity: Int
    ) : MonthlyStockSnapshot = runBlocking {
        val id = dao.upsert(MonthlyStockEntity(
            id = 0,
            monthYearPeriod = monthYear.atDay(1)
                .atStartOfDay(timeService.getZoneId())
                .toInstant()
                .toEpochMilli(),
            quantityPerUnitType = QuantityPerUnitType(
                cartonQuantity = cartonQuantity,
                pieceQuantity = pieceQuantity,
            ),
            productId = productId,
        ))

        val insertedEntity = dao.getById(id.toInt())

        MonthlyStockSnapshot.fromRoomEntity(insertedEntity, timeService = timeService)
    }
}