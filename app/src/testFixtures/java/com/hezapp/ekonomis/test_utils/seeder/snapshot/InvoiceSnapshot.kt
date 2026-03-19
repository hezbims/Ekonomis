package com.hezapp.ekonomis.test_utils.seeder.snapshot

import android.os.Build
import androidx.annotation.RequiresApi
import com.hezapp.ekonomis.core.domain.invoice.entity.PaymentMedia
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.test_utils.TestTimeService
import java.time.Instant
import java.time.LocalDate

data class InvoiceSnapshot(
    val id: Int,
    val dateInMillis: Long,
    val ppn: Int?,
    val transactionType: TransactionType,
    val paymentMedia: PaymentMedia,
    val profile: ProfileSnapshot,
    val invoiceItems: List<InvoiceItemSnapshot>,
    val installment: InstallmentSnapshot?,
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getLocalDate(testTimeService: TestTimeService = TestTimeService.get()): LocalDate =
        Instant.ofEpochMilli(dateInMillis)
            .atZone(testTimeService.getZoneId())
            .toLocalDate()
}