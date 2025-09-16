package com.hezapp.ekonomis.core.domain.invoice.entity

enum class PaymentMedia(val id: Int) {
    TRANSFER(0), CASH(1);

    companion object {
        fun from(id: Int) : PaymentMedia {
            return PaymentMedia.entries.singleOrNull {
                id == it.id
            } ?: throw IllegalArgumentException("Invalid id for payment media")
        }
    }
}