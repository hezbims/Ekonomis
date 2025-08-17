package com.hezapp.ekonomis.add_or_update_transaction.domain.model

import com.hezapp.ekonomis.core.domain.invoice.entity.InvoiceEntity
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.entity.InvoiceItemEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileEntity
import com.hezapp.ekonomis.core.domain.transaction.entity.TransactionEntity
import com.hezapp.ekonomis.core.domain.transaction.entity.TransactionItemEntity

data class InvoiceFormModel(
    val id: Int,
    val transactionType: TransactionType?,
    val profile: ProfileEntity?,
    val transactionDateMillis: Long?,
    val ppn: Int?,
    val newInvoiceItems: List<InvoiceItemEntity>,
    val prevInvoiceItems: List<InvoiceItemEntity>,
){
    fun toEntity() : InvoiceEntity =
        InvoiceEntity(
            id = id,
            date = transactionDateMillis!!,
            ppn = ppn,
            profileId = profile!!.id,
            transactionType = transactionType!!,
        )

    fun toTransactionEntity() : TransactionEntity =
        TransactionEntity(
            id = id,
            transactionType = transactionType!!,
            profileId = profile!!.id,
            transactionDateMillis = transactionDateMillis!!,
            ppn = ppn,
            items = newInvoiceItems.map {
                TransactionItemEntity(
                    productId = it.productId,
                    quantity = it.quantity,
                    price = it.price,
                    unitType = it.unitType,
                )
            }
        )

    val isEditing : Boolean
        get() = id != 0
}