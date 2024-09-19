package com.hezapp.ekonomis.core.presentation.utils

import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType

fun UnitType.getStringId() : Int =
    when(this){
        UnitType.CARTON -> R.string.carton_label
        UnitType.PIECE -> R.string.piece_label
    }

fun TransactionType.getProfileStringId() : Int =
    when(this){
        TransactionType.PENJUALAN -> R.string.customer_label // kalau sekarang kita sedang jualan, maka object pelanggan kita adalah pembeli
        TransactionType.PEMBELIAN -> R.string.seller_label // kalau kita sekarang ngelakuin pembelian, maka object pelanggan kita adalah penjual
    }

fun TransactionType.getTransactionStringId() : Int =
    when(this){
        TransactionType.PEMBELIAN -> R.string.purchase_product_label
        TransactionType.PENJUALAN -> R.string.sale_product_label
    }