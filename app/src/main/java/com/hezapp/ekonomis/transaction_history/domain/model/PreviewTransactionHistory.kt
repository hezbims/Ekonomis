package com.hezapp.ekonomis.transaction_history.domain.model

import com.hezapp.ekonomis.core.domain.model.PersonType

data class PreviewTransactionHistory(
    val id : Int,
    val personName : String,
    val personType: PersonType,
    val date : String,
)
