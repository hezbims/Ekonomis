package com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.dto

import java.time.LocalDate

data class InstallmentItemUiDto(
    val date: LocalDate,
    val amount: Int,)
