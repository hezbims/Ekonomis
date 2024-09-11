package com.hezapp.ekonomis.core.domain.entity.support_enum

enum class TransactionType(val id : Int) {
    PEMBELIAN(0), PENJUALAN(1);

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