package com.hezapp.ekonomis.dto

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
)