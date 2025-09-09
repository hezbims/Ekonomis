package com.hezapp.ekonomis.core.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.add_or_update_transaction.presentation.model.PaymentType

@Composable
fun stringResource(paymentType: PaymentType) : String {
    val stringId = when(paymentType){
        PaymentType.CASH -> R.string.cash
        PaymentType.INSTALLMENT -> R.string.installment
    }
    return stringResource(stringId)
}