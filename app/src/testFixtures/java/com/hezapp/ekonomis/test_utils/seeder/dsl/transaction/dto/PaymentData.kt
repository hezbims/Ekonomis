package com.hezapp.ekonomis.test_utils.seeder.dsl.transaction.dto

import com.hezapp.ekonomis.core.domain.invoice.entity.PaymentMedia
import java.time.LocalDate

internal data class PaymentData(
    val amount: Int,
    val date: LocalDate,
    val media: PaymentMedia,
)