package com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.dto

import com.hezapp.ekonomis.core.domain.invoice.entity.PaymentMedia
import java.time.LocalDate

data class InstallmentItemUiDto(
    val date: LocalDate,
    val amount: Int,
    val paymentMedia: PaymentMedia,)
