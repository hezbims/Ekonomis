package com.hezapp.ekonomis.product_detail.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.hezapp.ekonomis.core.domain.monthly_stock.entity.MonthlyStockEntity
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.core.domain.utils.getNextMonthYear
import com.hezapp.ekonomis.core.domain.utils.toBeginningOfMonth
import com.hezapp.ekonomis.core.domain.utils.toCalendar

@Dao
interface GetMonthlyStockByPeriodReadDao {
    @Query("""
        SELECT *
        FROM monthly_stock
        WHERE 
            month_year_period >= :startPeriod AND 
            month_year_period < :endPeriod AND
            product_id = :productId
    """)
    suspend fun execute(
        startPeriod: Long,
        endPeriod: Long,
        productId: Int,
    ) : MonthlyStockEntity?

    suspend fun execute(
        startMonthPeriod: Long,
        productId: Int,
        timeService: ITimeService,
    ) : MonthlyStockEntity? {
        val firstDayMonthPeriod = startMonthPeriod
            .toCalendar(timeService)
            .toBeginningOfMonth(timeService)
            .timeInMillis
        val lastDayOfMonthPeriod = firstDayMonthPeriod.getNextMonthYear(timeService)

        return execute(
            startPeriod = firstDayMonthPeriod,
            endPeriod = lastDayOfMonthPeriod,
            productId = productId,
        )
    }
}