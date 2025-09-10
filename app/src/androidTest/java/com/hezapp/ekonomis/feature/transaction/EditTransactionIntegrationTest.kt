package com.hezapp.ekonomis.feature.transaction

import androidx.test.core.app.ActivityScenario
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
import com.hezapp.ekonomis.dto.InstallmentItemAssertionDto
import com.hezapp.ekonomis.dto.PaymentTypeAssertionDto
import com.hezapp.ekonomis.steps.ModifyPaymentSectionAction
import com.hezapp.ekonomis.test_utils.seeder.InstallmentItemSeed
import com.hezapp.ekonomis.test_utils.seeder.InstallmentSeed
import com.hezapp.ekonomis.test_utils.seeder.InvoiceItemSeed
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class EditTransactionIntegrationTest : BaseEkonomisUiTest() {
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
            installmentSeed = InstallmentSeed(
                isPaidOff = false,
                items = listOf(
                    InstallmentItemSeed(
                        amount = 12_000_000,
                        paymentDate = LocalDate.now()
                            .withYear(2020)
                            .withMonth(6)
                            .withDayOfMonth(13)
                    ),
                    InstallmentItemSeed(
                        amount = 11_000_000,
                        paymentDate = LocalDate.now()
                            .withYear(2020)
                            .withMonth(6)
                            .withDayOfMonth(14)
                    ),
                )
            )
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
            installmentSeed = InstallmentSeed(
                isPaidOff = false,
                items = listOf(
                    InstallmentItemSeed(
                        amount = 2000,
                        paymentDate = LocalDate.now()
                            .withYear(2020)
                            .withMonth(6)
                            .withDayOfMonth(14)
                    )
                )
            )
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
            modifyPaymentSectionActions = listOf(
                ModifyPaymentSectionAction.EditInstallmentItem(
                    index = 1,
                    amount = 10_000_000,
                    date = LocalDate.now()
                        .withYear(2020)
                        .withMonth(6)
                        .withDayOfMonth(15)
                ),
                ModifyPaymentSectionAction.DeleteInstallmentItem(index = 0),
                ModifyPaymentSectionAction.AddNewInstallmentItem(
                    amount = 7_000_000,
                    date = LocalDate.now()
                        .withYear(2020)
                        .withMonth(6)
                        .withDayOfMonth(19),
                ),
                ModifyPaymentSectionAction.AddNewInstallmentItem(
                    amount = 9_000_000,
                    date = LocalDate.now()
                        .withYear(2020)
                        .withMonth(6)
                        .withDayOfMonth(23),
                )
            ),
        )

        transactionHistoryRobot.waitAndClickTransactionCard(profileName = "penjual-2")

        val expectedPaymentTypeData = PaymentTypeAssertionDto.Installment(
            isPaidOff = true,
            items = listOf(
                InstallmentItemAssertionDto(
                    paymentDate = LocalDate.now()
                        .withYear(2020)
                        .withMonth(6)
                        .withDayOfMonth(15),
                    amount = 10_000_000,
                ),
                InstallmentItemAssertionDto(
                    paymentDate = LocalDate.now()
                        .withYear(2020)
                        .withMonth(6)
                        .withDayOfMonth(19),
                    amount = 7_000_000,
                ),
                InstallmentItemAssertionDto(
                    paymentDate = LocalDate.now()
                        .withYear(2020)
                        .withMonth(6)
                        .withDayOfMonth(23),
                    amount = 9_000_000,
                ),
            )
        )

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
            ),
            paymentTypeAssertion = expectedPaymentTypeData,
        )

        transactionDbAssertion.assertCountInvoices(2)
        transactionDbAssertion.assertCountTransactionDetails(TransactionDetailsAssertionDto(
            date = LocalDate.now()
                .withYear(2020).withMonth(6).withDayOfMonth(15),
            profileName = "penjual-2",
            transactionType = TransactionType.PEMBELIAN,
            ppn = 14,
            productItems = listOf(
                TransactionDetailsItemAssertionDto(
                    productName = "product-1",
                    quantity = 3,
                    unitType = UnitType.PIECE,
                    price = 2000,
                )
            ),
            paymentType = expectedPaymentTypeData,
        ), 1)
        transactionDbAssertion.assertCountInvoiceItems(2)
    }
}