package com.hezapp.ekonomis.core.data.product.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hezapp.ekonomis.core.domain.invoice.entity.TRANSACTION_PEMBELIAN_ID
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.product.model.PreviewProductSummary

@Dao
interface ProductDao {
    @Query("""
        SELECT * FROM products
        WHERE name LIKE '%' || :searchQuery || '%'
    """)
    suspend fun getAllProducts(searchQuery: String) : List<ProductEntity>

    @Query("""
        WITH latestSalePerProduct AS (
            SELECT
                product_id, price, ppn, quantity, date, unit_type,
                (
                    SELECT COUNT(*)
                    FROM invoice_items
                    JOIN invoices
                        ON invoice_id = invoices.id
                    WHERE 
                        transaction_type = $TRANSACTION_PEMBELIAN_ID AND
                        curItem.product_id = product_id AND (
                            curInv.date < date OR 
                            curInv.date = date AND
                            curItem.id < invoice_items.id
                        )
                ) row_num_reversed
                
            FROM invoice_items curItem
            JOIN invoices curInv
                ON invoice_id = curInv.id
            WHERE 
                transaction_type = $TRANSACTION_PEMBELIAN_ID AND
                row_num_reversed = 0
        )
        SELECT id, name, quantity, price, ppn, unit_type
        FROM products
        LEFT JOIN latestSalePerProduct
            ON  product_id = id
        WHERE name LIKE '%' || :searchQuery || '%'
        ORDER BY LOWER(name)
    """)
    suspend fun getPreviewProductSummaries(searchQuery: String) : List<PreviewProductSummary>

    @Insert
    suspend fun insertNewProduct(newProduct: ProductEntity)

    @Query("""
        SELECT * 
        FROM products
        WHERE id = :id
    """)
    suspend fun getProduct(id: Int) : ProductEntity
}