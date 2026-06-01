package com.hezapp.ekonomis.product_detail.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.hezapp.ekonomis.core.domain.product.model.ProductTransaction

@Dao
interface GetProductTransactionsReadDao {
    @Query("""
        SELECT 
            invoice_items.id id,
            price,
            quantity,
            unit_type,
            date,
            ppn,
            profiles.name profile_name
        FROM invoice_items
        JOIN invoices
            ON invoices.id = invoice_id
        JOIN profiles
            ON profiles.id = profile_id
        WHERE 
            product_id = :productId AND
            transaction_type = :transactionTypeId AND
            date >= :firstDayOfPeriod AND
            date < :lastDayOfPeriod
    """)
    suspend fun execute(
        productId: Int,
        firstDayOfPeriod: Long,
        lastDayOfPeriod: Long,
        transactionTypeId: Int,
    ) : List<ProductTransaction>
}