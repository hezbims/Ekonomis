package com.hezapp.ekonomis.core.data.monthly_stock.repo

import com.hezapp.ekonomis.core.data.monthly_stock.dao.MonthlyStockDao
import com.hezapp.ekonomis.core.domain.monthly_stock.entity.MonthlyStockEntity
import com.hezapp.ekonomis.core.domain.monthly_stock.repo.IMonthlyStockRepo
import com.hezapp.ekonomis.core.domain.utils.getNextMonthYear
import com.hezapp.ekonomis.core.domain.utils.toBeginningOfMonth
import com.hezapp.ekonomis.core.domain.utils.toCalendar

class MonthlyStockRepo(
    private val dao : MonthlyStockDao,
) : IMonthlyStockRepo {
    override suspend fun getMonthlyStock(
        startMonthPeriod: Long,
        productId: Int
    ): MonthlyStockEntity? {
        val firstDayMonthPeriod = startMonthPeriod.toCalendar().toBeginningOfMonth().timeInMillis
        val lastDayOfMonthPeriod = firstDayMonthPeriod.getNextMonthYear()

        return dao.getMonthlyStock(
            startPeriod = firstDayMonthPeriod,
            endPeriod = lastDayOfMonthPeriod,
            productId = productId,
        )
    }

    override suspend fun upsertMonthlyStock(monthlyStockEntity: MonthlyStockEntity) : Int {
        return dao.upsert(monthlyStockEntity).toInt()
    }
}