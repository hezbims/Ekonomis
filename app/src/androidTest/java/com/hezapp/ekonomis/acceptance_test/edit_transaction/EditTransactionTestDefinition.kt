package com.hezapp.ekonomis.acceptance_test.edit_transaction

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
import com.hezapp.ekonomis.steps.DeleteProduct
import com.hezapp.ekonomis.steps.EditProduct
import com.hezapp.ekonomis.steps.ModifyPaymentSectionAction
import com.hezapp.ekonomis.test_application.BaseTestDefinition
import com.hezapp.ekonomis.test_utils.seeder.InstallmentItemSeed
import com.hezapp.ekonomis.test_utils.seeder.InstallmentSeed
import com.hezapp.ekonomis.test_utils.seeder.InvoiceItemSeed
import com.hezapp.ekonomis.test_utils.seeder.snapshot.InvoiceSnapshot
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

class EditTransactionTestDefinition(composeTestRule: ComposeTestRule, context: Context)
    : BaseTestDefinition(composeTestRule, context) {

    private val supplierNameFirst  = "Supplier Pertama"
    private val supplierNameSecond = "Supplier Kedua"
    private val customerName       = "Pelanggan Utama"

    private val productA = "Tepung Terigu"  // transaction 1
    private val productB = "Gula Pasir"     // transaction 2 — invoice item 1 (will be DELETED)
    private val productC = "Minyak Goreng"  // transaction 3
    private val productD = "Beras"          // transaction 2 — invoice item 2 (will be EDITED)
    private val productE = "Kedelai"        // transaction 2 — invoice item 3 (unchanged)

    // Original dates for all three transactions
    private val transaction1Date = LocalDate.of(2020, 2, 5)
    private val transaction2Date = LocalDate.of(2020, 2, 10)
    private val transaction3Date = LocalDate.of(2020, 2, 20)

    // Transaction 2 — initial invoice item prices
    private val transaction2Item1Price = 40_000   // productB — will be DELETED
    private val transaction2Item2Qty   = 3
    private val transaction2Item2Price = 60_000   // productD — will be EDITED
    private val transaction2Item3Price = 25_000   // productE — unchanged
    // Total price initially shown on the card (used to locate the card before editing)
    private val transaction2InitialTotalPrice =
        transaction2Item1Price + transaction2Item2Price + transaction2Item3Price // 125_000

    // Transaction 2 — edited values for invoice item 2 (productD)
    private val editedItem2Qty   = 5
    private val editedItem2Price = 75_000
    // Total price shown on the card after the edit: edited item 2 + unchanged item 3
    private val editedTransaction2TotalPrice = editedItem2Price + transaction2Item3Price // 100_000

    // Transaction 2 — initial installment item dates
    private val installmentItem1Date = LocalDate.of(2020, 2, 12) // unchanged
    private val installmentItem2Date = LocalDate.of(2020, 2, 15) // will be EDITED
    private val installmentItem3Date = LocalDate.of(2020, 2, 20) // will be DELETED

    // Transaction 2 — initial installment item amounts
    private val installmentItem1Amount = 20_000  // unchanged
    private val installmentItem2Amount = 25_000  // will be EDITED
    private val installmentItem3Amount = 30_000  // will be DELETED

    // Edited values for installment item 2
    private val editedInstallmentItem2Amount = 35_000
    private val editedInstallmentItem2Date   = LocalDate.of(2020, 2, 18)

    // Edited date / PPN for transaction 2
    private val editedTransaction2Date = LocalDate.of(2020, 2, 12)
    private val editedTransaction2Ppn  = 12

    // Prices for transactions 1 & 3 (single invoice item each)
    private val transaction1Price = 50_000
    private val transaction3Price = 120_000

    private lateinit var transaction1: InvoiceSnapshot
    private lateinit var transaction2: InvoiceSnapshot
    private lateinit var transaction3: InvoiceSnapshot

    /**
     * Seeds three distinct transactions directly into the database, then launches MainActivity.
     * - Transaction 1: PEMBELIAN from [supplierNameFirst], with PPN, cash payment.
     * - Transaction 2: PEMBELIAN from [supplierNameSecond], with PPN, **3 invoice items**,
     *   and **3 installment items** — the richest precondition, enabling the edit step to
     *   exercise every kind of product-list and installment-list modification:
     *   edit, delete, and keep-unchanged.
     * - Transaction 3: PENJUALAN to [customerName], with one installment item
     *   — serves as the unaffected side-effect guard.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun userHasExistingThreeUniqueTransaction(): Unit = runBlocking {
        val supplierFirst  = profileSeeder.run(supplierNameFirst,  ProfileType.SUPPLIER)
        val supplierSecond = profileSeeder.run(supplierNameSecond, ProfileType.SUPPLIER)
        val customer       = profileSeeder.run(customerName,       ProfileType.CUSTOMER)

        val products = productSeeder.run(listOf(
            ProductEntity(name = productA),
            ProductEntity(name = productB),
            ProductEntity(name = productC),
            ProductEntity(name = productD),
            ProductEntity(name = productE),
        ))

        transaction1 = invoiceSeeder.run(
            profile = supplierFirst,
            date = transaction1Date,
            invoiceItems = listOf(
                InvoiceItemSeed(
                    quantity = 3,
                    unitType = UnitType.PIECE,
                    product  = products[0], // productA
                    price    = transaction1Price,
                )
            ),
            ppn = 11,
        )

        // 3 invoice items and 3 installment items so the edit step can cover:
        //   invoice items  : item-1 deleted, item-2 edited, item-3 unchanged
        //   installment    : item-1 unchanged, item-2 edited, item-3 deleted
        transaction2 = invoiceSeeder.run(
            profile = supplierSecond,
            date = transaction2Date,
            invoiceItems = listOf(
                InvoiceItemSeed(                            // item 1 — will be DELETED
                    quantity = 2,
                    unitType = UnitType.PIECE,
                    product  = products[1], // productB
                    price    = transaction2Item1Price,
                ),
                InvoiceItemSeed(                            // item 2 — will be EDITED
                    quantity = transaction2Item2Qty,
                    unitType = UnitType.PIECE,
                    product  = products[3], // productD
                    price    = transaction2Item2Price,
                ),
                InvoiceItemSeed(                            // item 3 — unchanged
                    quantity = 1,
                    unitType = UnitType.PIECE,
                    product  = products[4], // productE
                    price    = transaction2Item3Price,
                ),
            ),
            ppn = 11,
            installmentSeed = InstallmentSeed(
                isPaidOff = false,
                items = listOf(
                    InstallmentItemSeed(                    // item 1 — unchanged
                        amount      = installmentItem1Amount,
                        paymentDate = installmentItem1Date,
                        paymentMedia = PaymentMedia.TRANSFER,
                    ),
                    InstallmentItemSeed(                    // item 2 — will be EDITED
                        amount      = installmentItem2Amount,
                        paymentDate = installmentItem2Date,
                        paymentMedia = PaymentMedia.CASH,
                    ),
                    InstallmentItemSeed(                    // item 3 — will be DELETED
                        amount      = installmentItem3Amount,
                        paymentDate = installmentItem3Date,
                        paymentMedia = PaymentMedia.TRANSFER,
                    ),
                ),
            ),
        )

        transaction3 = invoiceSeeder.run(
            profile = customer,
            date = transaction3Date,
            invoiceItems = listOf(
                InvoiceItemSeed(
                    quantity = 5,
                    unitType = UnitType.PIECE,
                    product  = products[2], // productC
                    price    = transaction3Price,
                )
            ),
            ppn = null,
            installmentSeed = InstallmentSeed(
                isPaidOff = false,
                items = listOf(
                    InstallmentItemSeed(
                        amount      = 60_000,
                        paymentDate = LocalDate.of(2020, 2, 25),
                        paymentMedia = PaymentMedia.TRANSFER,
                    )
                ),
            ),
        )

        ActivityScenario.launch(MainActivity::class.java)
    }

    /**
     * Opens the second transaction card and performs a comprehensive edit:
     * - date and PPN changed
     * - invoice item 2 ([productD]) quantity and price edited
     * - invoice item 1 ([productB]) deleted
     * - installment item 2 amount and date edited
     * - installment item 3 deleted
     *
     * This covers every kind of list-item mutation (edit + delete) in a single edit action.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun userEditTheSecondTransaction() {
        transactionHistoryRobot.waitAndClickTransactionCard(
            profileName = supplierNameSecond,
            pairTotalPriceAndProfileType = Pair(transaction2InitialTotalPrice, ProfileType.SUPPLIER),
            date = transaction2Date,
        )

        fillTransactionSteps.fillForm(
            profileName = supplierNameSecond,
            isRegisterNewProfile = false,
            date = editedTransaction2Date,
            ppn  = editedTransaction2Ppn,
            modifyChoosenProductActions = listOf(
                EditProduct(                    // edit item 2 first, while its index is still stable
                    targetName = productD,
                    quantity   = editedItem2Qty,
                    unitType   = UnitType.CARTON,
                    totalPrice = editedItem2Price,
                ),
                DeleteProduct(                  // then delete item 1 by name (name-based, order-safe)
                    targetName = productB,
                ),
            ),
            modifyPaymentSectionActions = listOf(
                ModifyPaymentSectionAction.EditInstallmentItem(  // edit item 2 while index is stable
                    index        = 1,
                    amount       = editedInstallmentItem2Amount,
                    date         = editedInstallmentItem2Date,
                    paymentMedia = PaymentMedia.TRANSFER,
                ),
                ModifyPaymentSectionAction.DeleteInstallmentItem( // delete item 3 (index 2)
                    index = 2,
                ),
            ),
        )
    }

    /**
     * Locates the edited second transaction by its **new** date and **new** total price,
     * opens it, and asserts every form field reflects all changes applied in
     * [userEditTheSecondTransaction]:
     * - Only [productD] and [productE] remain; [productB] is gone.
     * - [productD] shows the edited quantity and price.
     * - Only installment items 1 and 2 remain; item 1 is original, item 2 is edited.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun theSecondTransactionShouldEdited() {
        transactionHistoryRobot.waitAndClickTransactionCard(
            profileName = supplierNameSecond,
            pairTotalPriceAndProfileType = Pair(editedTransaction2TotalPrice, ProfileType.SUPPLIER),
            date = editedTransaction2Date,
        )

        transactionFormRobot.assertFormContent(
            transactionType = TransactionType.PEMBELIAN,
            date            = editedTransaction2Date,
            profileName     = supplierNameSecond,
            ppn             = editedTransaction2Ppn,
            products        = listOf(
                ProductFormAssertData(          // item 2 — edited values
                    name     = productD,
                    price    = editedItem2Price,
                    quantity = editedItem2Qty,
                    unitType = UnitType.CARTON,
                ),
                ProductFormAssertData(          // item 3 — unchanged
                    name     = productE,
                    price    = transaction2Item3Price,
                    quantity = 1,
                    unitType = UnitType.PIECE,
                ),
            ),
            paymentTypeAssertion = PaymentTypeAssertionDto.Installment(
                isPaidOff = false,
                items     = listOf(
                    InstallmentItemAssertionDto(    // item 1 — unchanged
                        paymentDate  = installmentItem1Date,
                        amount       = installmentItem1Amount,
                        paymentMedia = PaymentMedia.TRANSFER,
                    ),
                    InstallmentItemAssertionDto(    // item 2 — edited values
                        paymentDate  = editedInstallmentItem2Date,
                        amount       = editedInstallmentItem2Amount,
                        paymentMedia = PaymentMedia.TRANSFER,
                    ),
                ),
            ),
        )

        transactionFormRobot.backToPreviousScreen()
    }

    /**
     * Asserts that the first and third transactions are still present and unmodified —
     * guarding against unintended side-effects from the edit action.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun otherTransactionsIsUnchanged() {
        transactionHistoryRobot.waitAndClickTransactionCard(transaction1)
        transactionFormRobot.assertFormContent(transaction1)
        transactionFormRobot.backToPreviousScreen()

        transactionHistoryRobot.waitAndClickTransactionCard(transaction3)
        transactionFormRobot.assertFormContent(transaction3)
    }
}