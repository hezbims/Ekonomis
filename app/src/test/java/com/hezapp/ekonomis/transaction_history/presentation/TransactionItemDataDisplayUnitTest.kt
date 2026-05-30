package com.hezapp.ekonomis.transaction_history.presentation

import androidx.navigation.compose.rememberNavController
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.test_application.BaseEkonomisUiUnitTest
import com.hezapp.ekonomis.test_utils.TestTimeService
import com.hezapp.ekonomis.transaction_history.application.dto.PreviewTransactionHistory
import com.hezapp.ekonomis.transaction_history.application.use_case.iface.IGetPreviewTransactionHistoryUseCase
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class TransactionItemDataDisplayUnitTest : BaseEkonomisUiUnitTest(loadDefaultKoinModules = false) {
    @Before
    fun prepare(){
        val mockUseCase = mock<IGetPreviewTransactionHistoryUseCase>()
        val fakeTransactionData = listOf(
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

        whenever(mockUseCase.invoke(any()))
            .thenReturn(
                flowOf(
                    ResponseWrapper.Loading(),
                    ResponseWrapper.Succeed(fakeTransactionData)
                )
            )

        koin.loadModules(modules = listOf(module {
            single<IGetPreviewTransactionHistoryUseCase> { mockUseCase }
            single<ITimeService> { TestTimeService.get() }
            viewModel { _ ->
                TransactionHistoryViewModel(getPreviewTransactionHistory = get(), timeService = get())
            }
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
        uiUtils.transactionHistoryRobot.itemWithProfileName("Si Pencicil")
            .assertIsNotPaidOff()
    }

    @Test
    fun `Paid off transaction should be displayed as 'Paid off'`(){
        uiUtils.transactionHistoryRobot.itemWithProfileName("Si Lunas")
            .assertIsPaidOff()
    }

    @Test
    fun `Transaction with type 'Penjualan barang' should displayed with '+' sign rupiah`(){
        uiUtils.transactionHistoryRobot.itemWithProfileName("Si Pencicil")
            .assertTotalProductPrice("+Rp2.000.000")
    }

    @Test
    fun `Transaction with type 'Pembelian barang' should displayed with '-' sign rupiah`(){
        uiUtils.transactionHistoryRobot.itemWithProfileName("Si Lunas")
            .assertTotalProductPrice("-Rp20.000.000")
    }
}