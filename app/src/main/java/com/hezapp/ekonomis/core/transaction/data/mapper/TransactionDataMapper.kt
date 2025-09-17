package com.hezapp.ekonomis.core.transaction.data.mapper

import com.hezapp.ekonomis.core.domain.invoice.entity.Installment
import com.hezapp.ekonomis.core.domain.invoice.entity.InstallmentItem
import com.hezapp.ekonomis.core.domain.invoice.entity.InvoiceEntity
import com.hezapp.ekonomis.core.domain.invoice_item.entity.InvoiceItemEntity
import com.hezapp.ekonomis.core.transaction.domain.entity.InstallmentEntity
import com.hezapp.ekonomis.core.transaction.domain.entity.InstallmentItemEntity
import com.hezapp.ekonomis.core.transaction.domain.entity.TransactionEntity

fun TransactionEntity.toRoomInvoiceEntity() : InvoiceEntity {
    return InvoiceEntity(
        id = id,
        date = transactionDateMillis,
        ppn = ppn,
        profileId = profileId,
        transactionType = transactionType,
        paymentMedia = paymentMedia,
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

fun InstallmentEntity.toRoomInstallmentEntity(invoiceId: Int) : Installment {
    return Installment(
        invoiceId = invoiceId,
        isPaidOff = isPaidOff,
    )
}

fun InstallmentItemEntity.toRoomInstallmentItemEntity(installmentId: Int) : InstallmentItem {
    return InstallmentItem(
        installmentId = installmentId,
        paymentDate = paymentDate,
        amount = amount,
    )
}