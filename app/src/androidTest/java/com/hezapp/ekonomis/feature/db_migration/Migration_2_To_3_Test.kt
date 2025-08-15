package com.hezapp.ekonomis.feature.db_migration

import com.hezapp.ekonomis.core.data.database.EkonomisDatabase
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import com.hezapp.ekonomis.test_utils.raw_sql_helper.assertCountEntities
import com.hezapp.ekonomis.test_utils.raw_sql_helper.createNewInvoice
import com.hezapp.ekonomis.test_utils.raw_sql_helper.createNewInvoiceItem
import com.hezapp.ekonomis.test_utils.raw_sql_helper.createNewProduct
import com.hezapp.ekonomis.test_utils.raw_sql_helper.createNewProfile
import org.junit.Test
import java.time.LocalDate

@Suppress("className")
class Migration_2_To_3_Test : BaseDbMigrationTest() {
    @Test
    fun migration_2_To_3_Must_Succeed(){
        val invoiceId : Int
        helper.createDatabase(EkonomisDatabase.DB_NAME, 2).apply {
            beginTransaction()
            val profileId = createNewProfile("ini adalah nama", ProfileType.SUPPLIER)
            val productId = createNewProduct("contoh produk")
            invoiceId = createNewInvoice(
                date = LocalDate.now()
                    .withYear(2020)
                    .withMonth(5)
                    .withDayOfYear(7),
                profileId = profileId,
                ppn = 13,
            )
            createNewInvoiceItem(
                productId = productId,
                invoiceId = invoiceId,
                quantity = 3,
                unitType = UnitType.PIECE,
                totalPrice = 2_500_000,
            )
            setTransactionSuccessful()
            endTransaction()

            assertCountEntities(
                invoiceCount = 1,
                invoiceItemCount = 1,
                profileCount = 1,
                productCount = 1,
            )
        }

        val db = helper.runMigrationsAndValidate(
            name = EkonomisDatabase.DB_NAME,
            version = 3,
            validateDroppedTables = true)

        db.apply {
            assertCountEntities(
                installmentCount = 0,
                installmentItemsCount = 0,
                invoiceCount = 1,
                invoiceItemCount = 1,
                profileCount = 1,
                productCount = 1,
            )
        }
    }

}