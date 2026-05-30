package com.hezapp.ekonomis.transaction_history.application.dto

import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import com.hezapp.ekonomis.transaction_history.data.dto.PreviewTransactionQueryResult

data class PreviewTransactionHistory(
    val id : Int,
    val profileName : String,
    val profileType: ProfileType,
    val date : Long,
    val totalPrice: Long,
    val isPaidOff: Boolean,
) {
    companion object {
        fun fromQueryResult(queryResult : PreviewTransactionQueryResult) : PreviewTransactionHistory {
            return PreviewTransactionHistory(
                id = queryResult.id,
                profileName = queryResult.profileName,
                profileType = queryResult.profileType,
                date = queryResult.date,
                totalPrice = queryResult.totalPrice,
                isPaidOff = queryResult.isPaidOff
            )
        }
    }
}