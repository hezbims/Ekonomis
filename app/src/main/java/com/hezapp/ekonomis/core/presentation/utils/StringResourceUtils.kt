package com.hezapp.ekonomis.core.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.add_or_update_transaction.presentation.model.PaymentType
import com.hezapp.ekonomis.core.domain.invoice.entity.PaymentMedia

@Composable
fun stringResource(paymentType: PaymentType) : String {
    val stringId = when(paymentType){
        PaymentType.CASH -> R.string.one_time_label
        PaymentType.INSTALLMENT -> R.string.installment
    }
    return stringResource(stringId)
}

@Composable
fun stringResource(paymentMedia: PaymentMedia) : String {
    val stringId = when(paymentMedia){
        PaymentMedia.CASH -> R.string.cash_label
        PaymentMedia.TRANSFER -> R.string.transfer_label
    }
    return stringResource(stringId)
}