package com.hezapp.ekonomis.core.domain.monthly_stock.repo

import com.hezapp.ekonomis.core.domain.monthly_stock.entity.MonthlyStockEntity

interface IMonthlyStockRepo {
    suspend fun upsertMonthlyStock(monthlyStockEntity: MonthlyStockEntity) : Int
}