package com.hezapp.ekonomis.core.data.invoice.converter

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LocalDateConverter {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    @TypeConverter
    fun stringToLocalDate(value : String?) : LocalDate? =
        value?.let {
            LocalDate.parse(it, formatter)
        }
    @TypeConverter
    fun localDateToString(value : LocalDate?) : String? =
        value?.let {
            value.format(formatter)
        }

}