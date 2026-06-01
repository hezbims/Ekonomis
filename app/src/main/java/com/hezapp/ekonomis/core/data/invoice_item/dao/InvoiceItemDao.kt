package com.hezapp.ekonomis.core.data.invoice_item.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.hezapp.ekonomis.core.domain.invoice_item.entity.InvoiceItemEntity

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
}