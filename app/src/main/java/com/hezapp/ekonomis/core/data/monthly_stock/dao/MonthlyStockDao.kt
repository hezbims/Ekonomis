package com.hezapp.ekonomis.core.data.monthly_stock.dao

import androidx.room.Dao
import androidx.room.Query
import com.hezapp.ekonomis.core.domain.monthly_stock.entity.MonthlyStockEntity

@Dao
interface MonthlyStockDao {
    @Query("""
        SELECT *
        FROM monthly_stock
        WHERE 
            month_year_period >= :startPeriod AND 
            month_year_period < :endPeriod AND
            product_id = :productId
    """)
    suspend fun getMonthlyStock(
        startPeriod: Long,
        endPeriod: Long,
        productId: Int,
    ) : MonthlyStockEntity?
}