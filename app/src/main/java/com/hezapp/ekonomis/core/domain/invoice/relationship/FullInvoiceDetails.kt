package com.hezapp.ekonomis.core.domain.invoice.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.hezapp.ekonomis.core.domain.invoice.entity.InvoiceEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileEntity

data class FullInvoiceDetails(
    @Embedded
    val profile: ProfileEntity,

    @Relation(
        entity = InvoiceEntity::class,
        entityColumn = "profile_id",
        parentColumn = "id"
    )
    val invoice : InvoiceWithInvoiceItemAndProducts,
)
