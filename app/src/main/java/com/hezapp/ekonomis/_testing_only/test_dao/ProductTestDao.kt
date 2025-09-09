package com.hezapp.ekonomis._testing_only.test_dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hezapp.ekonomis.core.data.database.TableNames
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity

@Dao
interface ProductTestDao {
    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getById(id : Int) : ProductEntity

    @Insert
    suspend fun insertNewProducts(newProducts: List<ProductEntity>) : List<Long>

    @Query("""
        SELECT *
        FROM products
        WHERE id IN (:ids)
    """)
    suspend fun getByIds(ids: List<Int>) : List<ProductEntity>

    @Query("SELECT COUNT(*) FROM ${TableNames.PRODUCT}")
    suspend fun count() : Int
}