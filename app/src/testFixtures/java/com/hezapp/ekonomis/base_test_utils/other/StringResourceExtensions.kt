package com.hezapp.ekonomis.base_test_utils.other

import android.content.Context
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.invoice.entity.PaymentMedia

fun Context.getString(paymentMedia: PaymentMedia) : String {
    return when(paymentMedia){
        PaymentMedia.TRANSFER -> getString(R.string.transfer_label)
        PaymentMedia.CASH -> getString(R.string.cash_label)
    }
}