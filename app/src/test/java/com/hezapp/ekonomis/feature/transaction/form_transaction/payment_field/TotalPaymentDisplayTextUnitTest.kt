package com.hezapp.ekonomis.feature.transaction.form_transaction.payment_field

import androidx.compose.ui.test.hasText
import androidx.navigation.compose.rememberNavController
import com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.AddOrUpdateTransactionScreen
import com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.AddOrUpdateTransactionViewModel
import com.hezapp.ekonomis.core.domain.invoice.entity.Installment
import com.hezapp.ekonomis.core.domain.invoice.entity.InstallmentItem
import com.hezapp.ekonomis.core.domain.invoice.entity.InvoiceEntity
import com.hezapp.ekonomis.core.domain.invoice.entity.PaymentMedia
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
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import java.time.LocalDate

class TotalPaymentDisplayTextUnitTest : BaseEkonomisUiUnitTest() {
    val testData = FullInvoiceDetails(
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
                    amount = 35_000_000
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
    fun prepare() {
        val mockTransactionRepository = mock<ITransactionRepository> {
            onBlocking { getFullInvoiceDetails(1) } doAnswer { testData }
        }

        koin.loadModules(modules = listOf(module {
            single<ITransactionRepository> { mockTransactionRepository }
        }))

        val viewModel = koin.get<AddOrUpdateTransactionViewModel> { parametersOf(1) }
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

    @Test
    fun `Total payment should displayed correctly in rupiah after modifying payment field`(){
        repeat(3) {
            utils.transactionFormRobot.addNewInstallmentItem(
                date = LocalDate.of(2021, 3, 23),
                amount = 1_000_000_000,
                paymentMedia = PaymentMedia.CASH,
            )
        }

        composeRule.onNode(hasText("Total : Rp3.035.000.000"))
            .assertExists()
    }
}