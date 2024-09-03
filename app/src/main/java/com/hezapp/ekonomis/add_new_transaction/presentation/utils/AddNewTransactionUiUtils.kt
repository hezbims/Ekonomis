package com.hezapp.ekonomis.add_new_transaction.presentation.utils

import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.model.TransactionType

class AddNewTransactionUiUtils {
    companion object {
        fun getLabelIdFromTransactionType(transactionType: TransactionType) : Int =
            when(transactionType){
                TransactionType.PEMBELIAN -> R.string.sale_label
                TransactionType.PENJUALAN -> R.string.purchase_label
            }
    }
}