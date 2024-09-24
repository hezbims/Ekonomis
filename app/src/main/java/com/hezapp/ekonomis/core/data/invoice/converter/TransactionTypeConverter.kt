package com.hezapp.ekonomis.core.data.invoice.converter

import androidx.room.TypeConverter
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType

class TransactionTypeConverter {
    @TypeConverter
    fun transactionTypeToInt(value : TransactionType?) : Int? = value?.id
    @TypeConverter
    fun intToTransactionType(value : Int?) : TransactionType? =
        TransactionType.entries.firstOrNull { it.id == value }
}