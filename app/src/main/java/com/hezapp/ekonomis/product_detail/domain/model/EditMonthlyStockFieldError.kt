package com.hezapp.ekonomis.product_detail.domain.model

import com.hezapp.ekonomis.core.domain.general_model.MyBasicError

data class EditMonthlyStockValidationResult(
    val cartonError : EditMonthlyStockFieldError?,
    val pieceError : EditMonthlyStockFieldError?,
) : MyBasicError

sealed class EditMonthlyStockFieldError {
    data object FieldEmpty : EditMonthlyStockFieldError()
}