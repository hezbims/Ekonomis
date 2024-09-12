package com.hezapp.ekonomis.add_new_transaction.domain.model

import com.hezapp.ekonomis.core.domain.entity.InvoiceEntity
import com.hezapp.ekonomis.core.domain.entity.InvoiceItemEntity
import com.hezapp.ekonomis.core.domain.entity.ProfileEntity
import com.hezapp.ekonomis.core.domain.entity.support_enum.TransactionType

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