package com.hezapp.ekonomis.db_migration

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.hezapp.ekonomis.core.data.database.EkonomisDatabase
import com.hezapp.ekonomis.core.data.invoice.converter.PaymentMediaConverter
import com.hezapp.ekonomis.core.domain.invoice.entity.PaymentMedia
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

@Suppress("ClassName")
class Migration_4_To_5_Test : BaseDbMigrationTest() {

    @Test
    fun default_value_of_payment_media_after_migration_must_be_Transfer() {
        val installmentItemId : Long
        val invoiceId : Long

        helper.createDatabase(EkonomisDatabase.DB_NAME, 4).run {
            setForeignKeyConstraintsEnabled(true)
            val supplierId = insert("profiles", SQLiteDatabase.CONFLICT_FAIL, ContentValues().apply {
                put("name", "supplier-1")
                put("type", 0)
            })
            val productId = insert("products", SQLiteDatabase.CONFLICT_FAIL, ContentValues().apply {
                put("name", "product-1")
            })
            beginTransaction()
            invoiceId = insert("invoices", SQLiteDatabase.CONFLICT_FAIL, ContentValues().apply {
                put("date", ZonedDateTime.of(
                    LocalDate.of(2020, 1, 1).atStartOfDay(),
                    ZoneId.of("UTC")
                ).toInstant().toEpochMilli())
                put("profile_id", supplierId)
                put("ppn", 11)
                put("transaction_type", 0)
            })
            insert("invoice_items", SQLiteDatabase.CONFLICT_FAIL, ContentValues().apply {
                put("product_id", productId)
                put("invoice_id", invoiceId)
                put("quantity", 5)
                put("price", 500_000)
                put("unit_type", 0)
            })
            val installmentId = insert("installments", SQLiteDatabase.CONFLICT_FAIL, ContentValues().apply {
                put("invoice_id", invoiceId)
                put("is_paid_off", 0)
            })
            installmentItemId = insert("installment_items", SQLiteDatabase.CONFLICT_FAIL, ContentValues().apply {
                put("installment_id", installmentId)
                put("payment_date", "2020-12-25")
                put("amount", 1)
            })
            insert("monthly_stock", SQLiteDatabase.CONFLICT_FAIL, ContentValues().apply {
                put("month_year_period", ZonedDateTime.of(
                    LocalDate.of(2020, 12, 1).atStartOfDay(),
                    ZoneId.of("UTC+8")
                ).toInstant().toEpochMilli())

                put("carton_quantity", 3)
                put("piece_quantity", 1)
                put("product_id", productId)
            })
            setTransactionSuccessful()
            endTransaction()
        }

        helper.runMigrationsAndValidate(
            name = EkonomisDatabase.DB_NAME,
            version = 5,
            validateDroppedTables = true
        ).apply {
            setForeignKeyConstraintsEnabled(true)
            val converter = PaymentMediaConverter()
            query("""
                SELECT payment_media FROM installment_items
                WHERE id = ?
            """.trimMargin(), arrayOf(installmentItemId)).apply {
                moveToNext()
                assertThat(converter.intToPaymentMedia(getInt(0)), equalTo(PaymentMedia.TRANSFER))
            }

            query("""
                SELECT payment_media FROM invoices
                WHERE id = ?
            """.trimMargin(), arrayOf(invoiceId)).apply {
                moveToNext()
                assertThat(converter.intToPaymentMedia(getInt(0)), equalTo(PaymentMedia.TRANSFER))
            }
        }
    }
}