package com.hezapp.ekonomis.core.domain.profile.entity

import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType

enum class ProfileType(val id : Int) {
    SUPPLIER(0), CUSTOMER(1);

    fun getTransactionType() : TransactionType {
        return when(this){
            SUPPLIER -> TransactionType.PEMBELIAN
            CUSTOMER -> TransactionType.PENJUALAN
        }
    }
}