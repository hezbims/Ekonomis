package com.hezapp.ekonomis.transaction_history.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.core.domain.utils.getNextMonthYear
import com.hezapp.ekonomis.core.domain.utils.toBeginningOfMonth
import com.hezapp.ekonomis.core.domain.utils.toCalendar
import com.hezapp.ekonomis.transaction_history.data.dto.PreviewTransactionQueryResult

@Dao
interface GetPreviewTransactionsDao {
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
            COALESCE(is_paid_off, 1) is_paid_off
        FROM invoices
        JOIN profiles
            ON profile_id = profiles.id
        LEFT JOIN installments
            ON installments.invoice_id = invoices.id
        WHERE date >= :firstDayOfMonth AND date < :lastDayOfMonth AND
        (
            :isOnlyNotPaidOff = 0 OR
            installments.is_paid_off = 0
        )
        ORDER BY
            date DESC,
            id DESC
    """)
    suspend fun execute(
        firstDayOfMonth: Long,
        lastDayOfMonth: Long,
        isOnlyNotPaidOff : Boolean,
    ) : List<PreviewTransactionQueryResult>

    suspend fun execute(
        currentMonthYear: Long,
        isOnlyNotPaidOff: Boolean,
        timeService: ITimeService,
    ) : List<PreviewTransactionQueryResult> {
        val firstDayOfCurrentPeriod = currentMonthYear
            .toCalendar(timeService)
            .toBeginningOfMonth(timeService)
            .timeInMillis
        val nextMonthYear = firstDayOfCurrentPeriod.getNextMonthYear(timeService)
        val result = execute(
            firstDayOfMonth = firstDayOfCurrentPeriod,
            lastDayOfMonth = nextMonthYear,
            isOnlyNotPaidOff = isOnlyNotPaidOff,
        )
        return result
    }
}