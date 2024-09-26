package com.hezapp.ekonomis.core.data.invoice_item.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.hezapp.ekonomis.core.domain.invoice_item.entity.InvoiceItemEntity
import com.hezapp.ekonomis.core.domain.product.model.ProductTransaction

@Dao
interface InvoiceItemDao {
    @Upsert
    suspend fun upsertInvoiceItems(vararg invoiceItems: InvoiceItemEntity)

    @Delete
    suspend fun deleteInvoiceItems(vararg invoiceItems: InvoiceItemEntity)

    @Query("""
        DELETE FROM invoice_items
        WHERE invoice_id = :invoiceId
    """)
    suspend fun deleteInvoiceItemsByInvoiceId(invoiceId: Int)

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
    suspend fun getListProductTransactions(
        productId: Int,
        firstDayOfPeriod: Long,
        lastDayOfPeriod: Long,
        transactionTypeId: Int,
    ) : List<ProductTransaction>
}