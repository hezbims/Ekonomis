package com.hezapp.ekonomis.transaction_history.domain.model

import com.hezapp.ekonomis.core.domain.entity.support_enum.ProfileType

data class PreviewTransactionHistory(
    val id : Int,
    val personName : String,
    val personType: ProfileType,
    val date : String,
)
