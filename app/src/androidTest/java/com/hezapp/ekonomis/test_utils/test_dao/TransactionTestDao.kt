package com.hezapp.ekonomis.test_utils.test_dao

import androidx.room.Dao
import androidx.room.Query
import com.hezapp.ekonomis.core.domain.invoice.entity.InvoiceEntity
import com.hezapp.ekonomis.core.domain.invoice_item.entity.InvoiceItemEntity

@Dao
interface TransactionTestDao {
    @Query("SELECT COUNT(*) FROM invoices")
    suspend fun countInvoices() : Int

    @Query("SELECT COUNT(*) FROM invoice_items")
    suspend fun countInvoiceItems() : Int

    @Query("SELECT * FROM invoices")
    suspend fun getAll() : List<InvoiceEntity>

    @Query("SELECT * FROM invoice_items WHERE invoice_id = :invoiceId")
    suspend fun getItemsByInvoiceId(invoiceId : Int) : List<InvoiceItemEntity>
}