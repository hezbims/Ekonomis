package com.hezapp.ekonomis.core.data.product.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity

@Dao
interface ProductDao {
    @Query("""
        SELECT * FROM products
        WHERE name LIKE '%' || :searchQuery || '%'
    """)
    suspend fun getAllProducts(searchQuery: String) : List<ProductEntity>

    @Insert
    suspend fun insertNewProduct(newProduct: ProductEntity)
}