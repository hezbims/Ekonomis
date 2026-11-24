package com.hezapp.ekonomis.db_migration

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.hezapp.ekonomis.core.data.database.EkonomisDatabase
import com.hezapp.ekonomis.test_utils.raw_sql_helper.assertCountEntity
import com.hezapp.ekonomis.test_utils.raw_sql_helper.hasIndex
import com.hezapp.ekonomis.test_utils.raw_sql_helper.hasIndexOn
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

@Suppress("ClassName")
class Migration_5_To_6_Test : BaseDbMigrationTest() {

    @Test
    fun invoice_indices_should_be_created_and_data_preserved() {
        val invoiceDate = 1_600_000_000_000L
        val invoiceId: Long

        helper.createDatabase(EkonomisDatabase.DB_NAME, 5).run {
            setForeignKeyConstraintsEnabled(true)

            assertThat(hasIndexOn("invoices", "date"), equalTo(false))
            assertThat(hasIndexOn("invoices", "transaction_type", "date"), equalTo(false))

            val profileId = insert("profiles", SQLiteDatabase.CONFLICT_FAIL, ContentValues().apply {
                put("name", "profile-1")
                put("type", 0)
            })
            invoiceId = insert("invoices", SQLiteDatabase.CONFLICT_FAIL, ContentValues().apply {
                put("date", invoiceDate)
                put("profile_id", profileId)
                put("ppn", 11)
                put("transaction_type", 0)
                put("payment_media", 0)
            })

            assertCountEntity("invoices", 1)
        }

        helper.runMigrationsAndValidate(
            name = EkonomisDatabase.DB_NAME,
            version = 6,
            validateDroppedTables = true,
        ).apply {
            setForeignKeyConstraintsEnabled(true)

            // dicek berdasarkan nama index bawaan Room
            assertThat(hasIndex("invoices", "date"), equalTo(true))
            assertThat(hasIndex("invoices", "transaction_type", "date"), equalTo(true))
            assertThat(hasIndex("invoices", "profile_id"), equalTo(true))

            // dicek berdasarkan kolom yang di-index beserta urutannya
            assertThat(hasIndexOn("invoices", "date"), equalTo(true))
            assertThat(hasIndexOn("invoices", "transaction_type", "date"), equalTo(true))
            assertThat(hasIndexOn("invoices", "profile_id"), equalTo(true))

            assertCountEntity("invoices", 1)
            query("SELECT date FROM invoices WHERE id = ?", arrayOf(invoiceId)).use { cursor ->
                cursor.moveToNext()
                assertThat(cursor.getLong(0), equalTo(invoiceDate))
            }
        }
    }
}
