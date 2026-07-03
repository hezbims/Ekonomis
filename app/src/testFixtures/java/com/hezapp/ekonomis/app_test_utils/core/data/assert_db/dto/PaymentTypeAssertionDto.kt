package com.hezapp.ekonomis.app_test_utils.core.data.assert_db.dto

import com.hezapp.ekonomis.core.domain.invoice.entity.PaymentMedia
import java.time.LocalDate

sealed interface PaymentTypeAssertionDto {
    data object Cash : PaymentTypeAssertionDto
    data class Installment(
        val isPaidOff : Boolean,
        val items : List<InstallmentItemAssertionDto>,
    ) : PaymentTypeAssertionDto
}

data class InstallmentItemAssertionDto(
    val paymentDate : LocalDate,
    val amount: Int,
    val paymentMedia: PaymentMedia,
)