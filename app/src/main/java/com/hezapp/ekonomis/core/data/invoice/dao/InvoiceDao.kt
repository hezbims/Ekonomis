package com.hezapp.ekonomis.core.data.invoice.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.hezapp.ekonomis.core.domain.invoice.entity.InvoiceEntity
import com.hezapp.ekonomis.core.domain.invoice.model.PreviewTransactionHistory
import com.hezapp.ekonomis.core.domain.invoice.relationship.FullInvoiceDetails

@Dao
interface InvoiceDao {
    @Query("""
        SELECT 
            invoices.id id, 
            name profile_name, 
            type profile_type,
            date,
            (
                SELECT SUM(price)
                FROM invoice_items
                WHERE invoice_id = invoices.id
            ) total_price,
            COALESCE((
                SELECT is_paid_off
                FROM installments
                WHERE 
                    installments.invoice_id = invoices.id AND
                    (
                        :isOnlyNotPaidOff = 0 OR
                         is_paid_off = 1
                    )
            ), 1) is_paid_off
        FROM invoices
        JOIN profiles
            ON profile_id = profiles.id
        WHERE date >= :firstDayOfMonth AND date < :lastDayOfMonth
        ORDER BY
            date DESC,
            id DESC
    """)
    suspend fun getPreviewTransactionHistory(
        firstDayOfMonth: Long,
        lastDayOfMonth: Long,
        isOnlyNotPaidOff : Boolean,
    ) : List<PreviewTransactionHistory>

    @Transaction
    @Query("SELECT * FROM invoices WHERE id = :id")
    suspend fun getFullInvoiceDetails(id: Int) : FullInvoiceDetails

    @Upsert
    suspend fun upsertInvoice(invoice: InvoiceEntity) : Long

    @Query("DELETE FROM invoices WHERE id = :invoiceId")
    suspend fun deleteInvoice(invoiceId: Int)
}