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

    @Insert
    suspend fun insertNewProducts(newProducts: List<ProductEntity>) : List<Long>

    @Query("""
        SELECT *
        FROM products
        WHERE id IN (:ids)
    """)
    suspend fun getProductsByIds(ids: List<Int>) : List<ProductEntity>

    @Query("""
        SELECT * 
        FROM products
        WHERE id = :id
    """)
    suspend fun getProduct(id: Int) : ProductEntity?

    @Query("""
        SELECT * 
        FROM products
        WHERE LOWER(name) = LOWER(:name) 
    """)
    suspend fun getProductByName(name: String) : ProductEntity?

    @Query("UPDATE products SET name = :newName WHERE id = :id")
    suspend fun updateProductName(id: Int, newName: String)
}