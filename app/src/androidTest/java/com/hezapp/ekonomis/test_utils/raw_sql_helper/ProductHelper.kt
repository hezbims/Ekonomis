package com.hezapp.ekonomis.test_utils.raw_sql_helper

import androidx.sqlite.db.SupportSQLiteDatabase

fun SupportSQLiteDatabase.createNewProduct(name: String) : Int {
    execSQL("INSERT INTO products(name) VALUES(?)", arrayOf(name))
    return getLastInsertedId()
}