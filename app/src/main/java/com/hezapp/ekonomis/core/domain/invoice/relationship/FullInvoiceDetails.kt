package com.hezapp.ekonomis.core.domain.invoice.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileEntity

data class FullInvoiceDetails(
    @Relation(
        entityColumn = "id",
        parentColumn = "profile_id"
    )
    val profile: ProfileEntity,

    @Embedded
    val invoice : InvoiceWithInvoiceItemAndProducts,
)
