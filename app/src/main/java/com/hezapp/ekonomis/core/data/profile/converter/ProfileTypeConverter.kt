package com.hezapp.ekonomis.core.data.profile.converter

import androidx.room.TypeConverter
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType

class ProfileTypeConverter {
    @TypeConverter
    fun profileTypeToInt(value : ProfileType?) : Int? = value?.id
    @TypeConverter
    fun intToProfileType(value : Int?) : ProfileType? =
        ProfileType.entries.firstOrNull { it.id == value }
}