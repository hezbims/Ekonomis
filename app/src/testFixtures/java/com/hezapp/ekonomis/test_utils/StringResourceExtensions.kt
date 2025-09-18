package com.hezapp.ekonomis.test_utils

import android.content.Context
import com.hezapp.ekonomis.core.domain.invoice.entity.PaymentMedia
import com.hezapp.ekonomis.R

fun Context.getString(paymentMedia: PaymentMedia) : String {
    return when(paymentMedia){
        PaymentMedia.TRANSFER -> getString(R.string.transfer_label)
        PaymentMedia.CASH -> getString(R.string.cash_label)
    }
}