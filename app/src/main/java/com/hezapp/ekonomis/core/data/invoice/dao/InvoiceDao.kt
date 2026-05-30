package com.hezapp.ekonomis.core.data.invoice.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.hezapp.ekonomis.core.domain.invoice.entity.InvoiceEntity
import com.hezapp.ekonomis.core.domain.invoice.relationship.FullInvoiceDetails

@Dao
interface InvoiceDao {
    @Transaction
    @Query("SELECT * FROM invoices WHERE id = :id")
    suspend fun getFullInvoiceDetails(id: Int) : FullInvoiceDetails

    @Upsert
    suspend fun upsertInvoice(invoice: InvoiceEntity) : Long

    @Query("DELETE FROM invoices WHERE id = :invoiceId")
    suspend fun deleteInvoice(invoiceId: Int)
}