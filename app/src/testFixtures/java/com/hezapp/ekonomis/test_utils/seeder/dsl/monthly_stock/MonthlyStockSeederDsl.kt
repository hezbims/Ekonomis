package com.hezapp.ekonomis.test_utils.seeder.dsl.monthly_stock

import android.os.Build
import androidx.annotation.RequiresApi
import com.hezapp.ekonomis.core.data.monthly_stock.dao.MonthlyStockDao
import com.hezapp.ekonomis.core.domain.monthly_stock.entity.MonthlyStockEntity
import com.hezapp.ekonomis.core.domain.monthly_stock.entity.QuantityPerUnitType
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.test_utils.seeder.dsl.SeederDsl
import com.hezapp.ekonomis.test_utils.seeder.snapshot.MonthlyStockSnapshot
import kotlinx.coroutines.runBlocking
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
fun SeederDsl.monthlyStock(
    on: YearMonth,
    productId: Int,
    carton: Int,
    piece: Int
) : MonthlyStockSnapshot = runBlocking {
    val timeService = koin.get<ITimeService>()
    val dao = koin.get<MonthlyStockDao>()

    val insertedId = dao.upsert(MonthlyStockEntity(
        id = 0,
        monthYearPeriod = on.atDay(1)
            .atStartOfDay(timeService.getZoneId())
            .toInstant()
            .toEpochMilli(),
        quantityPerUnitType = QuantityPerUnitType(
            cartonQuantity = carton,
            pieceQuantity = piece,
        ),
        productId = productId,
    ))

    if (insertedId == -1L)
        throw RuntimeException("Monthly stock with " +
                "productId : {$productId}, " +
                "yearMonth : {${DateTimeFormatter.ofPattern("yyyy-MM").format(on)}}, " +
                "is already exist")

    val insertedEntity = dao.getById(insertedId.toInt())

    MonthlyStockSnapshot.fromRoomEntity(insertedEntity!!, timeService = timeService)
}