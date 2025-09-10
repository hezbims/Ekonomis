package com.hezapp.ekonomis.feature.transaction.form_transaction.payment_field

import androidx.navigation.compose.rememberNavController
import com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.AddOrUpdateTransactionScreen
import com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.AddOrUpdateTransactionViewModel
import com.hezapp.ekonomis.core.domain.invoice.entity.Installment
import com.hezapp.ekonomis.core.domain.invoice.entity.InstallmentItem
import com.hezapp.ekonomis.core.domain.invoice.entity.InvoiceEntity
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice.relationship.FullInvoiceDetails
import com.hezapp.ekonomis.core.domain.invoice.relationship.InstallmentWithItems
import com.hezapp.ekonomis.core.domain.invoice.relationship.InvoiceWithInvoiceItemAndProducts
import com.hezapp.ekonomis.core.domain.invoice_item.entity.InvoiceItemEntity
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.invoice_item.relationship.InvoiceItemWithProduct
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import com.hezapp.ekonomis.core.transaction.domain.repo.ITransactionRepository
import com.hezapp.ekonomis.test_application.BaseEkonomisUiUnitTest
import com.hezapp.ekonomis.test_utils.TestTimeService
import org.junit.Before
import org.junit.Test
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock

class PaymentSwitchPaidOffStateUnitTest : BaseEkonomisUiUnitTest() {
    private var editingTransactionTestData = FullInvoiceDetails(
        profile = ProfileEntity(name = "mock-profile", type = ProfileType.CUSTOMER),
        installmentWithItems = InstallmentWithItems(
            installment = Installment(
                invoiceId = 1,
                isPaidOff = false,
            ),
            installmentItems = listOf(
                InstallmentItem(
                    id = 1,
                    installmentId = 1,
                    paymentDate = TestTimeService.Companion.get().getLocalDate(),
                    amount = 2_000_000
                )
            ),
        ),
        invoice = InvoiceWithInvoiceItemAndProducts(
            invoice = InvoiceEntity(
                id = 1,
                date = TestTimeService.Companion.get().getCalendar().timeInMillis,
                profileId = 1,
                ppn = null,
                transactionType = TransactionType.PENJUALAN,
            ),
            invoiceItemWithProducts = listOf(
                InvoiceItemWithProduct(
                    invoiceItem = InvoiceItemEntity(
                        id = 1,
                        productId = 1,
                        invoiceId = 1,
                        quantity = 1,
                        price = 2_000_000,
                        unitType = UnitType.PIECE
                    ),
                    product = ProductEntity(
                        id = 1,
                        name = "mock-barang",
                    )
                ),
                InvoiceItemWithProduct(
                    invoiceItem = InvoiceItemEntity(
                        id = 2,
                        productId = 2,
                        invoiceId = 1,
                        quantity = 1,
                        price = 1_000_000,
                        unitType = UnitType.PIECE
                    ),
                    product = ProductEntity(
                        id = 2,
                        name = "mock-barang-2",
                    )
                )
            )
        )
    )

    @Before
    fun prepare(){
        val mockTransactionRepository = mock<ITransactionRepository> {
            onBlocking { getFullInvoiceDetails(any()) } doAnswer { editingTransactionTestData }
        }

        koin.loadModules(modules = listOf(module {
            single<ITransactionRepository> { mockTransactionRepository }
        }))
    }

    private fun setContent(invoiceId: Int){
        val viewModel = koin.get<AddOrUpdateTransactionViewModel> { parametersOf(invoiceId) }
        composeRule.setContent {
            AddOrUpdateTransactionScreen(
                navController = rememberNavController(),
                onSubmitSucceed = {},
                onDeleteSucceed = {},
                viewModel = viewModel,
                timeService = koin.get(),
            )
        }
    }
    private fun setContentWithEditingTransaction(){
        setContent(invoiceId = 1)
    }

    private fun setEditingTestDataToPaidOff(){
        editingTransactionTestData = editingTransactionTestData.copy(
            installmentWithItems = editingTransactionTestData.installmentWithItems!!.copy(
                installment = Installment(
                    id = 1,
                    invoiceId = 1,
                    isPaidOff = true
                ),
                installmentItems = listOf(
                    InstallmentItem(
                        id = 1,
                        installmentId = 1,
                        paymentDate = TestTimeService.Companion.get().getLocalDate(),
                        amount = 3_000_000
                    )
                )
            )
        )
    }

    /**
     * Switch harus berubah jadi enable kalau total pembayaran cicilan yang ditambahkan
     * oleh user lebih dari atau sama dengan total harga produk.
     */
    @Test
    fun `Payment should immediately displayed as paid off when total of payment become equal or greater than total product cost after user add new item`() {
        setContentWithEditingTransaction()

        //region ACT
        utils.transactionFormRobot.addNewInstallmentItem(
            date = TestTimeService.get().getLocalDate(),
            amount = 1_000_000
        )
        //endregion

        //region ASSERT
        utils.transactionFormRobot.assertIsPaidOff(true)
        //endregion
    }

    @Test
    fun `Payment must immediately displayed as not paid off when total of payment become less than total product cost after user edit an item`(){
        //region PREPARE
        setEditingTestDataToPaidOff()
        setContentWithEditingTransaction()
        //endregion

        //region ACT
        utils.transactionFormRobot.editInstallmentItem(
            index = 0,
            date = TestTimeService.Companion.get().getLocalDate(),
            amount = 2_999_999
        )
        //endregion

        utils.transactionFormRobot.assertIsPaidOff(false)
    }

    @Test
    fun `Payment must immediately displayed as not paid off when total of payment become less than total product cost after user delete an item`(){
        //region PREPARE
        setEditingTestDataToPaidOff()
        setContentWithEditingTransaction()
        //endregion

        //region ACT
        utils.transactionFormRobot.deleteInstallmentItemAt(index = 0)
        //endregion

        utils.transactionFormRobot.assertIsPaidOff(false)
    }

    @Test
    fun `There will be warning dialog, when total payment is less than total product cost but user try to mark payment as paid off`(){
        //region PREPARE
        setEditingTestDataToPaidOff()
        setContentWithEditingTransaction()
        //endregion

        //region ACT
        utils.transactionFormRobot.editInstallmentItem(
            index = 0,
            date = TestTimeService.Companion.get().getLocalDate(),
            amount = 2_999_999
        )
        utils.transactionFormRobot.togglePaidOff()
        utils.transactionFormRobot.confirmPaidOffToggleInDialog()
        //endregion

        utils.transactionFormRobot.assertIsPaidOff(true)
    }

    @Test
    fun `Initial paid off state should be 'Not Paid Off', when user try to add new transaction`(){
        throw NotImplementedError()
    }

}