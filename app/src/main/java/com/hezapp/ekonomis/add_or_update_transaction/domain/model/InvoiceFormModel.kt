package com.hezapp.ekonomis.add_or_update_transaction.domain.model

import com.hezapp.ekonomis.core.domain.invoice.entity.InvoiceEntity
import com.hezapp.ekonomis.core.domain.invoice_item.entity.InvoiceItemEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileEntity
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType

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
            date = transactionDateMillis!!,
            ppn = ppn,
            profileId = profile!!.id,
            transactionType = transactionType!!,
        )

    val isEditing : Boolean
        get() = id != 0
}