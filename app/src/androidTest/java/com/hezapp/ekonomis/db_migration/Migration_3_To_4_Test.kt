package com.hezapp.ekonomis.db_migration

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.ForeignKey
import com.hezapp.ekonomis.core.data.database.EkonomisDatabase
import com.hezapp.ekonomis.test_utils.raw_sql_helper.assertCountEntity
import com.hezapp.ekonomis.test_utils.raw_sql_helper.hasForeignKey
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

@Suppress("ClassName")
class Migration_3_To_4_Test : BaseDbMigrationTest() {
    /**
     * sekarang `invoice_items`, `installments`, `installment_items`
     * sudah punya `Foreign Key` `onDelete` `CASCADE`. Yang berarti
     * kalau `invoice` di delete, seharusnya `invoice_items`,
     * `installments`, dan `installment_items` juga ikut ke-delete,
     */
    @Test
    fun foreign_key_on_delete_constraint_should_correctly_configured() {
        val invoiceId = helper.createDatabase(EkonomisDatabase.DB_NAME, 3).run {
            setForeignKeyConstraintsEnabled(true)

            assertThat(hasForeignKey(
                tableName = "installments",
                columnName = "invoice_id",
                refTable = "invoices",
                refColumn = "id",
                onDeleteForignKeyId = ForeignKey.CASCADE,
            ), equalTo(false))
            assertThat(hasForeignKey(
                tableName = "installment_items",
                columnName = "installment_id",
                refTable = "installments",
                refColumn = "id",
                onDeleteForignKeyId = ForeignKey.CASCADE,
            ), equalTo(false))
            assertThat(hasForeignKey(
                tableName = "invoice_items",
                columnName = "invoice_id",
                refTable = "invoices",
                refColumn = "id",
                onDeleteForignKeyId = ForeignKey.CASCADE,
            ), equalTo(false))

            val profileId = insert("profiles", SQLiteDatabase.CONFLICT_FAIL, ContentValues().apply {
                put("name", "profile-name")
                put("type", 0)
            })

            val productIds = List(2) { index ->
                insert("products", SQLiteDatabase.CONFLICT_FAIL, ContentValues().apply {
                    put("name", "product-name-$index")
                })
            }

            val invoiceId = insert("invoices", SQLiteDatabase.CONFLICT_FAIL, ContentValues().apply {
                put("date", ZonedDateTime.of(
                    LocalDate.of(2025, 5, 7).atStartOfDay(),
                    ZoneId.of("UTC")
                ).toInstant().toEpochMilli())
                put("profile_id", profileId)
                put("ppn", 13)
                put("transaction_type", 0)
            })

            for (i in 0..1)
                insert("invoice_items", SQLiteDatabase.CONFLICT_FAIL, ContentValues().apply {
                    put("product_id", productIds[i])
                    put("invoice_id", invoiceId)
                    put("quantity", 3 + i)
                    put("unit_type", 1)
                    put("price", 2_500_000 * (i + 1))
                })

            val installmentId = insert("installments", SQLiteDatabase.CONFLICT_FAIL, ContentValues().apply {
                put("invoice_id", invoiceId)
                put("is_paid_off", 1)
            })
            insert("installment_items", SQLiteDatabase.CONFLICT_FAIL, ContentValues().apply {
                put("payment_date", "2023-06-22")
                put("amount", 25_000_000)
                put("installment_id", installmentId)
            })

            invoiceId
        }

        helper.runMigrationsAndValidate(
            name = EkonomisDatabase.DB_NAME,
            version = 4,
            validateDroppedTables = true
        ).apply {
            setForeignKeyConstraintsEnabled(true)
            delete("invoices", "id = ?", arrayOf(invoiceId))

            assertCountEntity("installments", 0)
            assertCountEntity("installment_items", 0)
            assertCountEntity("invoices", 0)
            assertCountEntity("invoice_items", 0)
            assertCountEntity("profiles", 1)
            assertCountEntity("products", 2)

            assertThat(hasForeignKey(
                tableName = "installments",
                columnName = "invoice_id",
                refTable = "invoices",
                refColumn = "id",
                onDeleteForignKeyId = ForeignKey.CASCADE,
            ), equalTo(true))
            assertThat(hasForeignKey(
                tableName = "installment_items",
                columnName = "installment_id",
                refTable = "installments",
                refColumn = "id",
                onDeleteForignKeyId = ForeignKey.CASCADE,
            ), equalTo(true))
            assertThat(hasForeignKey(
                tableName = "invoice_items",
                columnName = "invoice_id",
                refTable = "invoices",
                refColumn = "id",
                onDeleteForignKeyId = ForeignKey.CASCADE,
            ), equalTo(true))
        }
    }
}