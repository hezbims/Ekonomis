package com.hezapp.ekonomis.feature.transaction

import androidx.test.core.app.ActivityScenario
import com.hezapp.ekonomis.MainActivity
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import com.hezapp.ekonomis.test_application.BaseEkonomisUiTest
import com.hezapp.ekonomis.test_utils.seeder.InvoiceItemSeed
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class DeleteTransaction : BaseEkonomisUiTest() {
    @Before
    fun prepare() = runTest {
        val profiles = List(3){
            profileSeeder.run("profileName-${it + 1}", ProfileType.SUPPLIER)
        }
        val products = productSeeder.run(listOf(
            ProductEntity(name = "product-1"),
            ProductEntity(name = "product-2")
        ))

        // generate 2 invoices, make sure only one deleted
        for (invoiceIndex in 1..2)
            invoiceSeeder.run(
                profile = profiles[invoiceIndex - 1],
                date = LocalDate.now()
                    .withYear(2020)
                    .withMonth(2)
                    .withDayOfMonth(15 + invoiceIndex),
                invoiceItems = products.mapIndexed { productIndex, product ->
                    InvoiceItemSeed(
                        quantity = productIndex,
                        unitType = UnitType.PIECE,
                        product = product,
                        price = 25_000 * (productIndex + 1)
                    )
                },
                ppn = 13,
            )
        ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun should_delete_the_transaction_from_ui_and_database(){
        transactionHistoryRobot.waitAndClickTransactionCard("profileName-1")

        transactionFormRobot.deleteCurrentTransaction()

        transactionDbAssertion.assertCountInvoices(1)
        transactionDbAssertion.assertCountInvoiceItems(2)
        masterDataDbAssertion.assertCount(
            expectedProductCount = 2,
            expectedProfileCount = 3,
        )
        transactionHistoryRobot.assertTransactionCardNotExist("profileName-1")
    }
}