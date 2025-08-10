package com.hezapp.ekonomis.test_utils.test_dao

import androidx.room.Dao
import androidx.room.Query
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity

@Dao
interface ProductTestDao {
    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getById(id : Int) : ProductEntity
}