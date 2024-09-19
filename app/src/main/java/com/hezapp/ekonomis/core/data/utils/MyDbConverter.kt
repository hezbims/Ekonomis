package com.hezapp.ekonomis.core.data.utils

import androidx.room.TypeConverter
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType

class MyDbConverter {

    // Profile Type Enum
    @TypeConverter
    fun profileTypeToInt(value : ProfileType?) : Int? = value?.id
    @TypeConverter
    fun intToProfileType(value : Int?) : ProfileType? =
        ProfileType.entries.firstOrNull { it.id == value }
    // -------------------------


    // Transaction Type Enum
    @TypeConverter
    fun transactionTypeToInt(value : TransactionType?) : Int? = value?.id
    @TypeConverter
    fun intToTransactionType(value : Int?) : TransactionType? =
        TransactionType.entries.firstOrNull { it.id == value }
    // -------------------------


    // Unit Type Enum
    @TypeConverter
    fun unitTypeToInt(value : UnitType?) : Int? = value?.id
    @TypeConverter
    fun intToUnitType(value : Int?) : UnitType? =
        UnitType.entries.firstOrNull { value == it.id }
    // -------------------------
}