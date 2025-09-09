package com.hezapp.ekonomis.core.domain.invoice.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.hezapp.ekonomis.core.domain.invoice.entity.Installment
import com.hezapp.ekonomis.core.domain.invoice.entity.InstallmentItem

data class InstallmentWithItems(
    @Embedded
    val installment: Installment,

    @Relation(
        entityColumn = "installment_id",
        parentColumn = "id"
    )
    val installmentItems : List<InstallmentItem>,
)
