package com.hezapp.ekonomis.test_utils.raw_sql_helper

import androidx.room.ForeignKey
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hezapp.ekonomis.core.data.database.TableNames
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

fun SupportSQLiteDatabase.getLastInsertedId() : Int {
    return query("SELECT last_insert_rowid()").run {
        moveToNext()
        getInt(0)
    }
}

fun SupportSQLiteDatabase.assertCountEntity(tableName: String, expectedCount: Int){
    query("SELECT COUNT(*) FROM $tableName").run {
        moveToNext()
        val actualCount = getInt(0)
        assertThat(actualCount, equalTo(expectedCount))
    }
}

fun SupportSQLiteDatabase.assertCountEntities(
    installmentCount : Int? = null,
    installmentItemsCount : Int? = null,
    invoiceCount : Int? = null,
    invoiceItemCount : Int? = null,
    profileCount : Int? = null,
    productCount : Int? = null){
    installmentCount?.let {
        assertCountEntity(TableNames.INSTALLMENT, it)
    }

    installmentItemsCount?.let {
        assertCountEntity(TableNames.INSTALLMENT_ITEMS, it)
    }

    invoiceCount?.let {
        assertCountEntity(TableNames.INVOICE, it)
    }

    invoiceItemCount?.let {
        assertCountEntity(TableNames.INVOICE_ITEM, it)
    }

    profileCount?.let {
        assertCountEntity(TableNames.PROFILE, it)
    }

    productCount?.let {
        assertCountEntity(TableNames.PRODUCT, it)
    }
}

fun SupportSQLiteDatabase.hasColumn(tableName: String, columnName: String): Boolean {
    val cursor = this.query("PRAGMA table_info($tableName)")
    val nameIndex = cursor.getColumnIndex("name")
    while (cursor.moveToNext()) {
        val currentColumn = cursor.getString(nameIndex)
        if (currentColumn.equals(columnName, ignoreCase = true)) {
            return true
        }
    }

    return false
}

fun SupportSQLiteDatabase.hasForeignKey(
    tableName: String,
    columnName: String,
    refTable: String,
    refColumn: String,
    onDeleteForignKeyId: Int,
): Boolean {
    val onDeleteForignKeyString = getForeignKeyTypeString(onDeleteForignKeyId)

    val cursor = this.query("PRAGMA foreign_key_list($tableName)")

    val fromColumnIndex = cursor.getColumnIndex("from")
    val refTableIndex = cursor.getColumnIndex("table")
    val refColumnIndex = cursor.getColumnIndex("to")
    val onDeleteIndex = cursor.getColumnIndex("on_delete")

    while (cursor.moveToNext()) {
        val curFromColumn = cursor.getString(fromColumnIndex)
        val curRefTable = cursor.getString(refTableIndex)
        val curRefColumn = cursor.getString(refColumnIndex)
        val curOnDelete = cursor.getString(onDeleteIndex)

        if (curFromColumn.equals(columnName, ignoreCase = true) &&
            curRefTable.equals(refTable, ignoreCase = true) &&
            curRefColumn.equals(refColumn, ignoreCase = true) &&
            curOnDelete.equals(onDeleteForignKeyString, ignoreCase = true)
        ) {
            return true
        }
    }
    return false
}

private fun getForeignKeyTypeString(foreignKeyId : Int) : String {
    return when(foreignKeyId){
        ForeignKey.CASCADE -> "CASCADE"
        ForeignKey.RESTRICT -> "RESTRICT"
        else -> throw IllegalArgumentException("Unkown foreign key id '$foreignKeyId'")
    }
}

fun SupportSQLiteDatabase.hasIndex(tableName: String, vararg columns: String): Boolean {
    val cursor = this.query("PRAGMA index_list($tableName)")
    val nameIndex = cursor.getColumnIndex("name")
    while (cursor.moveToNext()) {
        val currentIndex = cursor.getString(nameIndex)
        if (currentIndex.equals("index_${columns.joinToString("_")}", ignoreCase = true)) {
            return true
        }
    }

    return false
}