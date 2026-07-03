package com.hezapp.ekonomis.app_test_utils.core.data.seeder._snapshot

import com.hezapp.ekonomis.core.domain.invoice.entity.PaymentMedia
import java.time.LocalDate

data class InstallmentItemSnapshot(
    val id: Int,
    val amount: Int,
    val paymentDate: LocalDate,
    val paymentMedia: PaymentMedia,
)