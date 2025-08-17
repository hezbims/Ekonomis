package com.hezapp.ekonomis.core.data.transaction.mapper

import com.hezapp.ekonomis.core.domain.invoice.entity.InvoiceEntity
import com.hezapp.ekonomis.core.domain.invoice_item.entity.InvoiceItemEntity
import com.hezapp.ekonomis.core.domain.transaction.entity.TransactionEntity

fun TransactionEntity.toRoomInvoiceEntity() : InvoiceEntity {
    return InvoiceEntity(
        id = id,
        date = transactionDateMillis,
        ppn = ppn,
        profileId = profileId,
        transactionType = transactionType,
    )
}

fun TransactionEntity.getRoomInvoiceItemEntities(invoiceId: Int) : List<InvoiceItemEntity> {
    return items.map {
        InvoiceItemEntity(
            invoiceId = invoiceId,
            id = 0,
            productId = it.productId,
            quantity = it.quantity,
            price = it.price,
            unitType = it.unitType,
        )
    }
}