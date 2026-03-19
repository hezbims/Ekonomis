package com.hezapp.ekonomis.test_utils.seeder.snapshot

import com.hezapp.ekonomis.core.domain.invoice.entity.PaymentMedia
import java.time.LocalDate

data class InstallmentItemSnapshot(
    val id: Int,
    val amount: Int,
    val paymentDate: LocalDate,
    val paymentMedia: PaymentMedia,
)