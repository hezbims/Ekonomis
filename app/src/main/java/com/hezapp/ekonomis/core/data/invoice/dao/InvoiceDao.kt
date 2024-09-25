package com.hezapp.ekonomis.core.data.invoice.dao

import androidx.room.Dao
import androidx.room.Query
import com.hezapp.ekonomis.core.domain.invoice.model.PreviewTransactionHistory

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
            total_price
        FROM currentPeriodInvoices
        JOIN profiles
            ON profile_id = profiles.id
        JOIN totalPricePerInvoice
            ON invoice_id = currentPeriodInvoices.id
    """)
    suspend fun getPreviewTransactionHistory(
        firstDayOfMonth: Long,
        lastDayOfMonth: Long,
    ) : List<PreviewTransactionHistory>
}