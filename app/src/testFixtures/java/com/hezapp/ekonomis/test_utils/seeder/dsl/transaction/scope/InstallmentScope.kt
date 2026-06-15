package com.hezapp.ekonomis.test_utils.seeder.dsl.transaction.scope

import android.os.Build
import androidx.annotation.RequiresApi
import com.hezapp.ekonomis.core.domain.invoice.entity.PaymentMedia
import com.hezapp.ekonomis.test_utils.seeder.dsl.transaction.dto.PaymentData
import java.time.LocalDate
import java.time.YearMonth

/**
 * Scope to define a list of installment payments.
 *
 * Instances are created by [OneDayTransactionScope.withInstallment].
 * Inside the lambda block, call [withPayment] one or more times.
 */
class InstallmentScope internal constructor(
    private val yearMonth: YearMonth,
) {
    internal val payments = mutableListOf<PaymentData>()

    /**
     * Adds a single installment payment.
     *
     * @param amount payment amount (required)
     * @param date payment date.
     *   If `null`, defaults to the 1st day of the transaction month.
     * @param media payment media, defaults to [com.hezapp.ekonomis.core.domain.invoice.entity.PaymentMedia.CASH]
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun withPayment(
        amount: Int,
        date: LocalDate? = null,
        media: PaymentMedia = PaymentMedia.CASH,
    ) {
        payments.add(
            PaymentData(
                amount = amount,
                date = date ?: yearMonth.atDay(1),
                media = media,
            )
        )
    }
}