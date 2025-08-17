package com.hezapp.ekonomis.feature.transaction

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.hezapp.ekonomis.MainActivity
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import com.hezapp.ekonomis.robot.transaction_form.ProductFormAssertData
import com.hezapp.ekonomis.steps.DeleteProduct
import com.hezapp.ekonomis.steps.EditProduct
import com.hezapp.ekonomis.test_application.BaseEkonomisUiTest
import com.hezapp.ekonomis.test_utils.db_assertion.TransactionDetailsAssertionDto
import com.hezapp.ekonomis.test_utils.db_assertion.TransactionDetailsItemAssertionDto
import com.hezapp.ekonomis.test_utils.seeder.InvoiceItemSeed
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class EditTransaction : BaseEkonomisUiTest() {
    @Before
    fun setupTestData() = runTest {
        val penjual1 = profileSeeder.run("penjual-1", ProfileType.SUPPLIER)
        profileSeeder.run("penjual-2", ProfileType.SUPPLIER)
        val pembeli = profileSeeder.run("pembali-1", ProfileType.CUSTOMER)

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
        invoiceSeeder.run(
            pembeli,
            LocalDate.now()
                .withYear(2020)
                .withMonth(6)
                .withDayOfMonth(13),
            listOf(
                InvoiceItemSeed(
                    quantity = 51,
                    unitType = UnitType.CARTON,
                    product = products[1],
                    price = 25_000
                ),
            ),
            ppn = null,
        )
        ActivityScenario.launch(MainActivity::class.java)
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
                .withDayOfMonth(15),
            profileName = "penjual-2",
            isRegisterNewProfile = false,
            ppn = 14,
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
                .withDayOfMonth(15),
            profileName = "penjual-2",
            ppn = 14,
            products = listOf(
                ProductFormAssertData(
                    name = "product-1",
                    quantity = 3,
                    unitType = UnitType.PIECE,
                    price = 2_000
                )
            )
        )

        transactionDbAssertion.assertCountInvoices(2)
        transactionDbAssertion.assertCountTransactionDetails(TransactionDetailsAssertionDto(
            date = LocalDate.now()
                .withYear(2020).withMonth(6).withDayOfMonth(15),
            profileName = "penjual-2",
            transactionType = TransactionType.PEMBELIAN,
            ppn = 14,
            items = listOf(
                TransactionDetailsItemAssertionDto(
                    productName = "product-1",
                    quantity = 3,
                    unitType = UnitType.PIECE,
                    price = 2000,
                )
            )
        ), 1)
        transactionDbAssertion.assertCountInvoiceItems(2)
    }
}