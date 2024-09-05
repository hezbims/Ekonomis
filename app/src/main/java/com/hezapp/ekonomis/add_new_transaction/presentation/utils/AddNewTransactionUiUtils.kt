package com.hezapp.ekonomis.add_new_transaction.presentation.utils

import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.entity.support_enum.TransactionType

class AddNewTransactionUiUtils {
    companion object {
        fun getProductTransactionTypeIdFromTransactionType(transactionType: TransactionType) : Int =
            when(transactionType){
                TransactionType.PEMBELIAN -> R.string.sale_product_label
                TransactionType.PENJUALAN -> R.string.purchase_product_label
            }

        fun getPersonIdFromTransactionType(transactionType: TransactionType) : Int =
            when(transactionType){
                TransactionType.PENJUALAN -> R.string.customer_label // kalau sekarang kita sedang jualan, maka object pelanggan kita adalah pembeli
                TransactionType.PEMBELIAN -> R.string.seller_label // kalau kita sekarang ngelakuin pembelian, maka object pelanggan kita adalah penjual
            }
    }
}