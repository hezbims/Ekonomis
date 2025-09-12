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
        WITH totalPricePerInvoice AS (
            SELECT invoice_id, SUM(price) total_price
            FROM invoice_items
            GROUP BY invoice_id
        ),
        currentPeriodInvoices AS (
            SELECT *
            FROM invoices
            WHERE date >= :firstDayOfMonth AND date < :lastDayOfMonth
        )
        SELECT 
            invoice_id id, 
            name profile_name, 
            type profile_type,
            date,
            total_price,
            COALESCE((
                SELECT is_paid_off
                FROM installments
                WHERE installments.invoice_id = currentPeriodInvoices.id
            ), 0) is_paid_off
        FROM currentPeriodInvoices
        JOIN profiles
            ON profile_id = profiles.id
        JOIN totalPricePerInvoice
            ON invoice_id = currentPeriodInvoices.id
        ORDER BY
            date DESC,
            id DESC
    """)
    suspend fun getPreviewTransactionHistory(
        firstDayOfMonth: Long,
        lastDayOfMonth: Long,
    ) : List<PreviewTransactionHistory>

    @Transaction
    @Query("SELECT * FROM invoices WHERE id = :id")
    suspend fun getFullInvoiceDetails(id: Int) : FullInvoiceDetails

    @Upsert
    suspend fun upsertInvoice(invoice: InvoiceEntity) : Long

    @Query("DELETE FROM invoices WHERE id = :invoiceId")
    suspend fun deleteInvoice(invoiceId: Int)
}