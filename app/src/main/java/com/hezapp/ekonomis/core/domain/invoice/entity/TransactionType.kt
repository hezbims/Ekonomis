package com.hezapp.ekonomis.core.domain.invoice.entity

import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType

enum class TransactionType(val id : Int) {
    PEMBELIAN(id = TRANSACTION_PEMBELIAN_ID),
    PENJUALAN(id = TRANSACTION_PENJUALAN_ID);

    fun getProfileType() : ProfileType =
        when(this){
            PEMBELIAN -> ProfileType.SUPPLIER
            PENJUALAN -> ProfileType.CUSTOMER
        }

    companion object {
        fun fromId(id : Int) : TransactionType =
            TransactionType.entries.single { it.id == id }
    }
}

const val TRANSACTION_PENJUALAN_ID = 1
const val TRANSACTION_PEMBELIAN_ID = 0