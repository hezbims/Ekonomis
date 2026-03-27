package com.hezapp.ekonomis.acceptance_test.add_new_transaction

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.test.core.app.ActivityScenario
import com.hezapp.ekonomis.MainActivity
import com.hezapp.ekonomis.core.domain.invoice.entity.PaymentMedia
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import com.hezapp.ekonomis.dto.InstallmentItemAssertionDto
import com.hezapp.ekonomis.dto.PaymentTypeAssertionDto
import com.hezapp.ekonomis.robot.transaction_form.ProductFormAssertData
import com.hezapp.ekonomis.steps.FormProductItem
import com.hezapp.ekonomis.steps.ModifyPaymentSectionAction
import com.hezapp.ekonomis.test_application.BaseTestDefinition
import com.hezapp.ekonomis.test_utils.seeder.InstallmentItemSeed
import com.hezapp.ekonomis.test_utils.seeder.InstallmentSeed
import com.hezapp.ekonomis.test_utils.seeder.InvoiceItemSeed
import com.hezapp.ekonomis.test_utils.seeder.snapshot.InvoiceSnapshot
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

class AddNewTransactionTestDefinition(composeTestRule: ComposeTestRule, context: Context)
    : BaseTestDefinition(composeTestRule, context) {

    private val supplierName = "Supplier Lama"
    private val customerName = "Pelanggan Lama"
    private val existingProductA = "Tepung Terigu"
    private val existingProductB = "Gula Pasir"
    private val newProductName = "Minyak Goreng"

    private val transaction1Date = LocalDate.of(2020, 2, 5)
    private val transaction2Date = LocalDate.of(2020, 2, 10)
    private val newTransactionDate = LocalDate.of(2020, 2, 20)

    private val transaction1TotalPrice = 50_000
    private val transaction2TotalPrice = 75_000
    private val newTransactionTotalPrice = 100_000

    lateinit var oldSupplierTransaction: InvoiceSnapshot
    lateinit var oldCustomerTransaction: InvoiceSnapshot

    /**
     * Seeds two distinct transactions directly into the database, then launches MainActivity.
     * - Transaction 1: PEMBELIAN (purchase) from [supplierName]
     * - Transaction 2: PENJUALAN (sale) to [customerName]
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun userHasTwoDistinctTransaction(): Unit = runBlocking {
        val supplier = profileSeeder.run(supplierName, ProfileType.SUPPLIER)
        val customer = profileSeeder.run(customerName, ProfileType.CUSTOMER)
        val products = productSeeder.run(listOf(
            ProductEntity(name = existingProductA),
            ProductEntity(name = existingProductB),
        ))

        oldSupplierTransaction = invoiceSeeder.run(
            profile = supplier,
            date = transaction1Date,
            invoiceItems = listOf(
                InvoiceItemSeed(
                    quantity = 3,
                    unitType = UnitType.PIECE,
                    product = products[0],
                    price = transaction1TotalPrice,
                )
            ),
            ppn = 11,
        )

        oldCustomerTransaction = invoiceSeeder.run(
            profile = customer,
            date = transaction2Date,
            invoiceItems = listOf(
                InvoiceItemSeed(
                    quantity = 2,
                    unitType = UnitType.PIECE,
                    product = products[1],
                    price = transaction2TotalPrice,
                )
            ),
            ppn = null,
            installmentSeed = InstallmentSeed(
                isPaidOff = false,
                items = listOf(
                    InstallmentItemSeed(
                        amount = 25_000,
                        paymentDate = LocalDate.of(2020, 2, 15),
                    )
                ),
            ),
        )

        ActivityScenario.launch(MainActivity::class.java)
    }

    /**
     * Navigates to the add-transaction form and fills in a new, unique transaction
     * that does not overlap with the two seeded ones.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun userAddNewUniqueTransaction() {
        transactionHistoryRobot.navigateToAddNewTransaction()
        fillTransactionSteps.fillForm(
            transactionType = TransactionType.PEMBELIAN,
            profileName = supplierName,
            isRegisterNewProfile = false,
            date = newTransactionDate,
            chooseProductActions = listOf(
                FormProductItem(
                    name = newProductName,
                    quantity = 4,
                    unitType = UnitType.PIECE,
                    totalPrice = newTransactionTotalPrice,
                    newRegistration = true,
                )
            ),
            ppn = 11,
            modifyPaymentSectionActions = listOf(
                ModifyPaymentSectionAction.SelectInstallmentPaymentType,
                ModifyPaymentSectionAction.AddNewInstallmentItem(
                    amount = 40_000,
                    date = LocalDate.of(2020, 2, 22),
                    paymentMedia = PaymentMedia.TRANSFER,
                ),
                ModifyPaymentSectionAction.AddNewInstallmentItem(
                    amount = 30_000,
                    date = LocalDate.of(2020, 2, 25),
                    paymentMedia = PaymentMedia.TRANSFER,
                ),
            ),
        )
    }

    /**
     * Opens the newly added transaction card in the history and asserts every form field
     * reflects exactly what was entered in [userAddNewUniqueTransaction].
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun theNewTransactionShouldAdded() {
        transactionHistoryRobot.waitAndClickTransactionCard(
            profileName = supplierName,
            pairTotalPriceAndProfileType = Pair(newTransactionTotalPrice, ProfileType.SUPPLIER),
            date = newTransactionDate,
        )
        transactionFormRobot.assertFormContent(
            transactionType = TransactionType.PEMBELIAN,
            date = newTransactionDate,
            profileName = supplierName,
            ppn = 11,
            products = listOf(
                ProductFormAssertData(
                    name = newProductName,
                    price = newTransactionTotalPrice,
                    quantity = 4,
                    unitType = UnitType.PIECE,
                )
            ),
            paymentTypeAssertion = PaymentTypeAssertionDto.Installment(
                isPaidOff = false,
                items = listOf(
                    InstallmentItemAssertionDto(
                        paymentDate = LocalDate.of(2020, 2, 22),
                        amount = 40_000,
                        paymentMedia = PaymentMedia.TRANSFER,
                    ),
                    InstallmentItemAssertionDto(
                        paymentDate = LocalDate.of(2020, 2, 25),
                        amount = 30_000,
                        paymentMedia = PaymentMedia.TRANSFER,
                    ),
                ),
            ),
        )

        transactionFormRobot.backToPreviousScreen()
    }

    /**
     * Navigates back to the transaction history and asserts that both original seeded
     * transactions are still present and unmodified.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun previousTwoTransactionsShouldNotAffected() {
        transactionHistoryRobot.waitAndClickTransactionCard(oldSupplierTransaction)
        transactionFormRobot.assertFormContent(oldSupplierTransaction)
        transactionFormRobot.backToPreviousScreen()

        transactionHistoryRobot.waitAndClickTransactionCard(oldCustomerTransaction)
        transactionFormRobot.assertFormContent(oldCustomerTransaction)
    }
}