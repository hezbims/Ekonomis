package com.hezapp.ekonomis.core.domain.invoice.model

import com.hezapp.ekonomis.core.domain.entity.support_enum.ProfileType

data class PreviewTransactionHistory(
    val id : Int,
    val profileName : String,
    val profileType: ProfileType,
    val date : Long,
    val totalPrice: Long,
)
