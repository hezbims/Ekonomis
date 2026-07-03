package com.hezapp.ekonomis.app_test_utils.core.data.seeder.transaction.dto

import com.hezapp.ekonomis.core.domain.invoice.entity.PaymentMedia
import java.time.LocalDate

internal data class PaymentData(
    val amount: Int,
    val date: LocalDate,
    val media: PaymentMedia,
)