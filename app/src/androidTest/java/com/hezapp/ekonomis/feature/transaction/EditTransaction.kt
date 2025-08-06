package com.hezapp.ekonomis.feature.transaction

import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import com.hezapp.ekonomis.robot.transaction_form.ProductFormAssertData
import com.hezapp.ekonomis.steps.DeleteProduct
import com.hezapp.ekonomis.steps.EditProduct
import com.hezapp.ekonomis.test_application.BaseEkonomisIntegrationTest
import com.hezapp.ekonomis.test_data.seeder.InvoiceItemSeed
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class EditTransaction : BaseEkonomisIntegrationTest() {
    @Before
    fun setupTestData() = runTest {
        val penjual1 = profileSeeder.run("penjual-1", ProfileType.SUPPLIER)
        profileSeeder.run("penjual-2", ProfileType.SUPPLIER)

        val products = productSeeder.run(listOf(
            ProductEntity(name = "product-0"),
            ProductEntity(name = "product-1")
        ))

        invoiceSeeder.run(
            penjual1,
            LocalDate.now()
                .withYear(2020)
                .withMonth(6)
                .withDayOfMonth(12),
            listOf(
                InvoiceItemSeed(
                    quantity = 5,
                    unitType = UnitType.CARTON,
                    product = products[0],
                    price = 25_000_000
                ),
                InvoiceItemSeed(
                    quantity = 6 ,
                    unitType = UnitType.PIECE,
                    product = products[1],
                    price = 1_000_000)
            ),
            ppn = 14,
        )
    }

    @Test(timeout = 60_000L)
    fun editedDataShouldDisplayedCorrectly() {
        transactionHistoryRobot.openAndApplyFilter(
            targetYear = 2020, targetMonth = 6)

        transactionHistoryRobot.waitAndClickTransactionCard(profileName = "penjual-1")

        filltransactionSteps.fillForm(
            date = LocalDate.now()
                .withYear(2020)
                .withMonth(6)
                .withDayOfMonth(12),
            profileName = "penjual-2",
            isRegisterNewProfile = false,
            ppn = 11,
            modifyChoosenProductActions = listOf(
                EditProduct(
                    targetName = "product-1",
                    quantity = 3,
                    unitType = UnitType.PIECE,
                    totalPrice = 2_000
                ),
                DeleteProduct("product-0")
            ),
        )

        transactionHistoryRobot.waitAndClickTransactionCard(profileName = "penjual-2")

        transactionFormRobot.assertFormContent(
            transactionType = TransactionType.PEMBELIAN,
            date = LocalDate.now()
                .withYear(2020)
                .withMonth(6)
                .withDayOfMonth(12),
            profileName = "penjual-2",
            ppn = 11,
            products = listOf(
                ProductFormAssertData(
                    name = "product-1",
                    quantity = 3,
                    unitType = UnitType.PIECE,
                    price = 2_000
                )
            )
        )
    }
}