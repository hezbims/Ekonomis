package com.hezapp.ekonomis.core.data.monthly_stock.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.hezapp.ekonomis.core.domain.monthly_stock.entity.MonthlyStockEntity

@Dao
interface MonthlyStockDao {
    @Upsert
    suspend fun upsert(monthlyStockEntity: MonthlyStockEntity) : Long

    @Query("SELECT * FROM monthly_stock WHERE id = :id")
    suspend fun getById(id: Int) : MonthlyStockEntity?
}