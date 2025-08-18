package com.hezapp.ekonomis.db_migration

import com.hezapp.ekonomis.core.data.database.EkonomisDatabase
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import com.hezapp.ekonomis.test_utils.raw_sql_helper.CreateInstallmentItemRawSqlDto
import com.hezapp.ekonomis.test_utils.raw_sql_helper.CreateInstallmentRawSqlDto
import com.hezapp.ekonomis.test_utils.raw_sql_helper.assertCountEntities
import com.hezapp.ekonomis.test_utils.raw_sql_helper.createInstallments
import com.hezapp.ekonomis.test_utils.raw_sql_helper.createNewInvoice
import com.hezapp.ekonomis.test_utils.raw_sql_helper.createNewInvoiceItem
import com.hezapp.ekonomis.test_utils.raw_sql_helper.createNewProduct
import com.hezapp.ekonomis.test_utils.raw_sql_helper.createNewProfile
import com.hezapp.ekonomis.test_utils.raw_sql_helper.deleteInvoice
import org.junit.Test
import java.time.LocalDate

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

            val profileId = createNewProfile("profile-name", ProfileType.SUPPLIER)
            val productIds = List(2) { index ->
                createNewProduct("product-name-$index")
            }
            val invoiceId = createNewInvoice(
                date = LocalDate.now()
                    .withYear(2020)
                    .withMonth(5)
                    .withDayOfYear(7),
                profileId = profileId,
                ppn = 13,
            )

            for (i in 0..1)
                createNewInvoiceItem(
                    productId = productIds[i],
                    invoiceId = invoiceId,
                    quantity = 3 + i,
                    unitType = UnitType.PIECE,
                    totalPrice = 2_500_000 * (i + 1),
                )

            createInstallments(CreateInstallmentRawSqlDto(
                invoiceId = invoiceId,
                isPaidOff = true,
                items = listOf(
                    CreateInstallmentItemRawSqlDto(
                        paymentDate = "22-06-2023",
                        amount = 25_000_000
                    )
                )
            ))

            invoiceId
        }

        helper.runMigrationsAndValidate(
            name = EkonomisDatabase.DB_NAME,
            version = 4,
            validateDroppedTables = true
        ).apply {
            setForeignKeyConstraintsEnabled(true)
            deleteInvoice(invoiceId)

            assertCountEntities(
                installmentCount = 0,
                installmentItemsCount = 0,
                invoiceCount = 0,
                invoiceItemCount = 0,
                profileCount = 1,
                productCount = 2,
            )
        }
    }
}