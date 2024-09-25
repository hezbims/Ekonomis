package com.hezapp.ekonomis.core.domain.invoice.model

import androidx.room.ColumnInfo
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType

data class PreviewTransactionHistory(
    val id : Int,

    @ColumnInfo(name = "profile_name")
    val profileName : String,

    @ColumnInfo(name = "profile_type")
    val profileType: ProfileType,

    val date : Long,

    @ColumnInfo(name = "total_price")
    val totalPrice: Long,
)
