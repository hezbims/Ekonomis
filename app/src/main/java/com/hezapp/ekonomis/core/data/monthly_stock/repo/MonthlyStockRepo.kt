package com.hezapp.ekonomis.core.data.monthly_stock.repo

import com.hezapp.ekonomis.core.data.monthly_stock.dao.MonthlyStockDao
import com.hezapp.ekonomis.core.domain.monthly_stock.entity.MonthlyStockEntity
import com.hezapp.ekonomis.core.domain.monthly_stock.repo.IMonthlyStockRepo

class MonthlyStockRepo(
    private val dao : MonthlyStockDao,
) : IMonthlyStockRepo {
    override suspend fun upsertMonthlyStock(monthlyStockEntity: MonthlyStockEntity) : Int {
        return dao.upsert(monthlyStockEntity).toInt()
    }
}