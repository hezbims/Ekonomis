package com.hezapp.ekonomis.feature.transaction.transaction_hisory

import androidx.navigation.compose.rememberNavController
import com.hezapp.ekonomis.core.domain.invoice.model.PreviewTransactionHistory
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import com.hezapp.ekonomis.core.transaction.domain.repo.ITransactionRepository
import com.hezapp.ekonomis.test_application.BaseEkonomisUiUnitTest
import com.hezapp.ekonomis.test_utils.TestTimeService
import com.hezapp.ekonomis.transaction_history.presentation.TransactionHistoryScreen
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock

class TransactionItemDataDisplayUnitTest : BaseEkonomisUiUnitTest() {
    @Before
    fun prepare(){
        val mockTransactionRepo = mock<ITransactionRepository> {
            onBlocking { getPreviewInvoices(any()) } doAnswer {
                listOf(
                    PreviewTransactionHistory(
                        id = 1,
                        profileName = "Si Lunas",
                        profileType = ProfileType.SUPPLIER,
                        date = TestTimeService.get().getCalendar().timeInMillis,
                        totalPrice = 20_000_000,
                        isPaidOff = true,
                    ),
                    PreviewTransactionHistory(
                        id = 2,
                        profileName = "Si Pencicil",
                        profileType = ProfileType.CUSTOMER,
                        date = TestTimeService.get().getCalendar().timeInMillis,
                        totalPrice = 2_000_000,
                        isPaidOff = false,
                    ),
                )
            }
        }

        koin.loadModules(modules = listOf(module {
            single<ITransactionRepository> { mockTransactionRepo }
        }))
        composeRule.setContent {
            TransactionHistoryScreen(
                navController = rememberNavController(),
                viewModel = koin.get(),
                timeService = koin.get()
            )
        }
    }

    @Test
    fun `Not paid off transaction should displayed as 'Not Paid Off'`(){
        utils.transactionHistoryRobot.itemWithProfileName("Si Pencicil")
            .assertIsNotPaidOff()
    }

    @Test
    fun `Paid off transaction should be displayed as 'Paid off'`(){
        utils.transactionHistoryRobot.itemWithProfileName("Si Lunas")
            .assertIsPaidOff()
    }

    @Test
    fun `Transaction with type 'Penjualan barang' should displayed with '+' sign rupiah`(){
        utils.transactionHistoryRobot.itemWithProfileName("Si Pencicil")
            .assertTotalProductPrice("+Rp2.000.000")
    }

    @Test
    fun `Transaction with type 'Pembelian barang' should displayed with '-' sign rupiah`(){
        utils.transactionHistoryRobot.itemWithProfileName("Si Lunas")
            .assertTotalProductPrice("-Rp20.000.000")
    }
}