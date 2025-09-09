package com.hezapp.ekonomis.core.domain.invoice.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.hezapp.ekonomis.core.domain.invoice.entity.Installment
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileEntity

data class FullInvoiceDetails(
    @Relation(
        entityColumn = "id",
        parentColumn = "profile_id"
    )
    val profile: ProfileEntity,

    @Relation(
        entity = Installment::class,
        entityColumn = "invoice_id",
        parentColumn = "id"
    )
    val installmentWithItems: InstallmentWithItems?,

    @Embedded
    val invoice : InvoiceWithInvoiceItemAndProducts,
)