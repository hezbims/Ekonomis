package com.hezapp.ekonomis.core.data.invoice_item.converter

import androidx.room.TypeConverter
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType

class UnitTypeConverter {
    @TypeConverter
    fun unitTypeToInt(value : UnitType?) : Int? = value?.id
    @TypeConverter
    fun intToUnitType(value : Int?) : UnitType? =
        UnitType.entries.firstOrNull { value == it.id }
}