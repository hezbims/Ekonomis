package com.hezapp.ekonomis.test_case.transaction_history.presentation

import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.hezapp.ekonomis.app_test_utils.core.application.mock.TestTimeService
import com.hezapp.ekonomis.app_test_utils.transaction_history.application.mock.stubReturnLoadingOnly
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.test_application.BaseEkonomisUiUnitTest
import com.hezapp.ekonomis.transaction_history.application.use_case.iface.IGetPreviewTransactionHistoryUseCase
import com.hezapp.ekonomis.transaction_history.presentation.TransactionHistoryScreen
import com.hezapp.ekonomis.transaction_history.presentation.TransactionHistoryViewModel
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinIsolatedContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.mockito.kotlin.mock
import org.robolectric.ParameterizedRobolectricTestRunner
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale

@RunWith(ParameterizedRobolectricTestRunner::class)
@Suppress("ClassName", "Junit4RunWithInspection")
class `Transaction History Screen - Top Bar Period Display`(
    val currentTime: LocalDateTime,
    val expectedPeriodDisplay: String,
) : BaseEkonomisUiUnitTest(
    loadDefaultKoinModules = false
) {
    @Test
    fun `Should display current period`(){
        // Given
        currentTimeIs(currentTime)

        // When
        userOpenedTransactionHistoryScreen()

        // Then
        periodDisplayedOnTopBarShouldBe(expectedPeriodDisplay)
    }

    private fun currentTimeIs(time: LocalDateTime){
        timeService = TestTimeService(locale = Locale.forLanguageTag("id-ID")).apply {
            setCurrentTime(time, ZoneId.of("GMT+8"))
        }
    }

    private fun userOpenedTransactionHistoryScreen(){
        val koinApp = koinApplication {
            modules(module {
                single { timeService }
                single<IGetPreviewTransactionHistoryUseCase> {
                    mock<IGetPreviewTransactionHistoryUseCase>().stubReturnLoadingOnly()
                }
                viewModel {
                    TransactionHistoryViewModel(get(), get())
                }
            })
        }

        composeRule.activity.setContent {
            KoinIsolatedContext(koinApp) {
                TransactionHistoryScreen(
                    navController = rememberNavController(),
                    viewModel = koinViewModel(),
                )
            }
        }
    }

    private fun periodDisplayedOnTopBarShouldBe(expectedDisplay: String){
        uiUtils.transactionHistoryRobot.assertTopBarPeriod(expectedDisplay)
    }

    private lateinit var timeService: ITimeService

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun testData() : Collection<Array<Any?>> = listOf(
            arrayOf(LocalDateTime.of(2026, 6, 1, 10, 30), "Juni 2026"),
            arrayOf(LocalDateTime.of(2026, 6, 1, 0, 0, 0, 0), "Juni 2026"),
            arrayOf(LocalDateTime.of(2026, 6, 30, 0, 0, 0, 0), "Juni 2026"),
            arrayOf(LocalDateTime.of(2026, 6, 30, 23, 59, 59, 999_999_999), "Juni 2026"),
            arrayOf(LocalDateTime.of(2020, 12, 31, 23, 59, 59, 999_999_999), "Desember 2020")
        )
    }
}