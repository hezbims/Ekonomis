package com.hezapp.ekonomis.test_case.product_detail.presentation

import androidx.activity.compose.setContent
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.monthly_stock.entity.QuantityPerUnitType
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.core.presentation.routing.MyRoutes
import com.hezapp.ekonomis.product_detail.domain.model.EditMonthlyStockFieldError
import com.hezapp.ekonomis.product_detail.domain.model.EditMonthlyStockValidationResult
import com.hezapp.ekonomis.product_detail.domain.use_case.EditMonthlyStockUseCase
import com.hezapp.ekonomis.product_detail.domain.use_case.GetLatestPreviousMonthStock
import com.hezapp.ekonomis.product_detail.presentation.EditCurrentMonthlyStockDialog
import com.hezapp.ekonomis.product_detail.presentation.EditMonthlyStockDialogViewModel
import com.hezapp.ekonomis.test_application.BaseEkonomisUiUnitTest
import com.hezapp.ekonomis.test_utils.TestConstant
import com.hezapp.ekonomis.test_utils.TestTimeService
import kotlinx.coroutines.flow.flow
import org.junit.Before
import org.junit.Test
import org.koin.compose.KoinIsolatedContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.YearMonth

@Suppress("UnusedFlow")
class EditStockDialogTest : BaseEkonomisUiUnitTest(
    loadDefaultKoinModules = false,
) {
    private companion object {
        const val PRODUCT_ID = 1
        const val MONTHLY_STOCK_ID = 1
        const val INITIAL_CARTON = 5
        const val INITIAL_PIECE = 3
        const val PREVIOUS_MONTH_CARTON = 10
        const val PREVIOUS_MONTH_PIECE = 7
        val CURRENT_YEAR_MONTH : YearMonth = YearMonth.of(2020, 1)
    }

    private lateinit var editStockUseCaseMock: EditMonthlyStockUseCase
    private lateinit var previousMonthStockMock: GetLatestPreviousMonthStock
    private val timeService = TestTimeService().apply {
        setCurrentTime(CURRENT_YEAR_MONTH)
    }

    private val currentYearMonthInMillis = CURRENT_YEAR_MONTH
        .atDay(1)
        .atStartOfDay(timeService.getZoneId())
        .toInstant()
        .toEpochMilli()

    private var isDialogDismissCallbackCalled = false
    private var isStockEditedCallbackCalled = false
    private val robot by lazy { uiUtils.editMonthlyStockDialogRobot }

    @Before
    fun setup() {
        editStockUseCaseMock = mock()
        previousMonthStockMock = mock()

        isDialogDismissCallbackCalled = false
        isStockEditedCallbackCalled = false

        koin.loadModules(
            listOf(
                module {
                    single<ITimeService> { timeService }
                    single { editStockUseCaseMock }
                    single { previousMonthStockMock }
                    viewModel { params ->
                        EditMonthlyStockDialogViewModel(
                            params = params.get(),
                            getLatestPreviousMonthStock = get(),
                            editMonthlyStock = get(),
                            timeService = get(),
                        )
                    }
                }
            ),
            allowOverride = true,
        )
    }

    @Test
    fun `When using calculation from previous month, should display previous month stock data correctly`() {
        stubPreviousMonthStockReturn(carton = PREVIOUS_MONTH_CARTON, piece = PREVIOUS_MONTH_PIECE)

        userOpenEditStockDialog()

        robot.clickUsePreviousMonthCalculation()
        robot.waitUntilDataLoaded()

        robot.assertCartonQuantity(PREVIOUS_MONTH_CARTON)
        robot.assertPieceQuantity(PREVIOUS_MONTH_PIECE)
    }

    @Test
    fun `Should save with calculated values from previous month when use previous month calculation is checked`() {
        stubPreviousMonthStockReturn(carton = PREVIOUS_MONTH_CARTON, piece = PREVIOUS_MONTH_PIECE)
        stubEditStockReturnSuccess()

        userOpenEditStockDialog()

        robot.clickUsePreviousMonthCalculation()
        robot.waitUntilDataLoaded()
        robot.clickSave()
        assertStockEditedCallbackCalled()

        verify(editStockUseCaseMock).invoke(
            cartonQuantity = eq(PREVIOUS_MONTH_CARTON),
            pieceQuantity = eq(PREVIOUS_MONTH_PIECE),
            monthlyStockEntityId = eq(MONTHLY_STOCK_ID),
            monthYearPeriod = eq(currentYearMonthInMillis),
            productId = eq(PRODUCT_ID),
        )
    }

    @Test
    fun `Should save with manually entered values when free text editing`() {
        val typedCarton = 15
        val typedPiece = 8

        stubEditStockReturnSuccess()

        userOpenEditStockDialog()

        robot.typeCartonQuantity(typedCarton.toString())
        robot.typePieceQuantity(typedPiece.toString())
        robot.clickSave()
        assertStockEditedCallbackCalled()

        verify(editStockUseCaseMock).invoke(
            cartonQuantity = eq(typedCarton),
            pieceQuantity = eq(typedPiece),
            monthlyStockEntityId = eq(MONTHLY_STOCK_ID),
            monthYearPeriod = eq(currentYearMonthInMillis),
            productId = eq(PRODUCT_ID),
        )
    }

    @Test
    fun `Should dismiss dialog when cancel is clicked`() {
        userOpenEditStockDialog()

        robot.clickCancel()

        composeRule.waitUntil(
            conditionDescription = "Expected dialog to be dismissed",
            timeoutMillis = TestConstant.SMALL_TIMEOUT,
        ) { isDialogDismissCallbackCalled }
    }

    @Test
    fun `Should show save button and initial values when dialog is opened`() {
        userOpenEditStockDialog()

        robot.assertSaveButtonDisplayed()
        robot.assertCartonQuantity(INITIAL_CARTON)
        robot.assertPieceQuantity(INITIAL_PIECE)
    }

    @Test
    fun `Should show field error when saving with use case that returns validation error`() {
        stubEditStockAlwaysReturnsEmptyFieldError()

        userOpenEditStockDialog()

        robot.clickSave()

        robot.assertCartonErrorDisplayed()
    }

    private fun userOpenEditStockDialog() {
        composeRule.activity.setContent {
            KoinIsolatedContext(koinApp) {
                EditCurrentMonthlyStockDialog(
                    args = MyRoutes.EditMonthlyStock(
                        period = timeService.getCurrentTimeInMillis(),
                        cartonQuantity = INITIAL_CARTON,
                        pieceQuantity = INITIAL_PIECE,
                        productId = PRODUCT_ID,
                        monthlyStockId = MONTHLY_STOCK_ID,
                    ),
                    onMonthlyStockEdited = { isStockEditedCallbackCalled = true },
                    onDismissRequest = { isDialogDismissCallbackCalled = true },
                )
            }
        }
    }

    private fun assertStockEditedCallbackCalled(){
        composeRule.waitUntil(
            conditionDescription = "Expected stock to be edited",
            timeoutMillis = TestConstant.SMALL_TIMEOUT,
        ) { isStockEditedCallbackCalled }
    }

    @Suppress("SameParameterValue")
    private fun stubPreviousMonthStockReturn(carton: Int, piece: Int) {
        whenever(previousMonthStockMock.invoke(
            currentMonthPeriod = any<Long>(),
            productId = any<Int>(),
        )).thenReturn(
            flow {
                emit(ResponseWrapper.Loading())
                emit(ResponseWrapper.Succeed(QuantityPerUnitType(carton, piece)))
            }
        )
    }

    private fun stubEditStockReturnSuccess() {
        whenever(editStockUseCaseMock.invoke(
            cartonQuantity = any(),
            pieceQuantity = any(),
            monthlyStockEntityId = any(),
            monthYearPeriod = any<Long>(),
            productId = any(),
        )).thenReturn(
            flow {
                emit(ResponseWrapper.Loading())
                emit(ResponseWrapper.Succeed(null))
            }
        )
    }

    private fun stubEditStockAlwaysReturnsEmptyFieldError() {
        whenever(
            editStockUseCaseMock.invoke(
                cartonQuantity = any(),
                pieceQuantity = any(),
                monthlyStockEntityId = any(),
                monthYearPeriod = any<Long>(),
                productId = any(),
            )
        ).thenReturn(
            flow {
                emit(ResponseWrapper.Loading())
                emit(
                    ResponseWrapper.Failed(
                        EditMonthlyStockValidationResult(
                            cartonError = EditMonthlyStockFieldError.FieldEmpty,
                            pieceError = EditMonthlyStockFieldError.FieldEmpty,
                        )
                    )
                )
            }
        )
    }
}
