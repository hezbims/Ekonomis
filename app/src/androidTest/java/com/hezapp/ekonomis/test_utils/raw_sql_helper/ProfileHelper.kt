package com.hezapp.ekonomis.test_utils.raw_sql_helper

import androidx.sqlite.db.SupportSQLiteDatabase
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType

fun SupportSQLiteDatabase.createNewProfile(name: String, type: ProfileType) : Int {
    execSQL("""
        INSERT INTO profiles(name, type)
        VALUES(? , ?)
    """.trimIndent(),
        arrayOf<Any>(name, profileTypeConverter.profileTypeToInt(type)!!))
    return getLastInsertedId()
}