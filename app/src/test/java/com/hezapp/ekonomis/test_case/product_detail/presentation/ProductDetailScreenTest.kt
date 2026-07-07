package com.hezapp.ekonomis.test_case.product_detail.presentation

import androidx.activity.compose.setContent
import com.hezapp.ekonomis.MainComposable
import com.hezapp.ekonomis.app_test_utils.core.application.mock.FakeErrorReportingService
import com.hezapp.ekonomis.app_test_utils.core.application.mock.TestDispatcherProvider
import com.hezapp.ekonomis.app_test_utils.core.application.mock.TestTimeService
import com.hezapp.ekonomis.app_test_utils.product_detail.application.mock.stubReturns
import com.hezapp.ekonomis.app_test_utils.product_detail.application.mock.stubReturnsError
import com.hezapp.ekonomis.app_test_utils.product_detail.application.mock.stubReturnsLoadingOnly
import com.hezapp.ekonomis.app_test_utils.product_detail.application.mock.stubReturnsSuccess
import com.hezapp.ekonomis.app_test_utils.product_detail.application.mock.stubSequentiallyReturns
import com.hezapp.ekonomis.core.application.utils.IDispatcherProvider
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.utils.IErrorReportingService
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.core.presentation.routing.MyRoutes
import com.hezapp.ekonomis.product_detail.application.dto.ProductDetail
import com.hezapp.ekonomis.product_detail.application.dto.ProductQuantityDto
import com.hezapp.ekonomis.product_detail.application.dto.ProductTransaction
import com.hezapp.ekonomis.product_detail.application.dto.TransactionSummary
import com.hezapp.ekonomis.product_detail.domain.use_case.EditMonthlyStockUseCase
import com.hezapp.ekonomis.product_detail.domain.use_case.GetLatestPreviousMonthStock
import com.hezapp.ekonomis.product_detail.domain.use_case.GetProductDetailUseCase
import com.hezapp.ekonomis.product_detail.presentation.EditMonthlyStockDialogViewModel
import com.hezapp.ekonomis.product_detail.presentation.ProductDetailViewModel
import com.hezapp.ekonomis.test_application.BaseEkonomisUiUnitTest
import org.junit.Before
import org.junit.Test
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.mockito.kotlin.mock
import java.time.YearMonth

class ProductDetailScreenTest : BaseEkonomisUiUnitTest(
    loadDefaultKoinModules = false,
) {
    private companion object {
        const val PRODUCT_ID = 0
        const val MONTHLY_STOCK_ID = 100
        val CURRENT_YEAR_MONTH: YearMonth = YearMonth.of(2020, 1)
        val INITIAL_STOCK = ProductQuantityDto(cartonQuantity = 5, pieceQuantity = 10)
        val UPDATED_STOCK = ProductQuantityDto(cartonQuantity = 10, pieceQuantity = 20)
        const val PRODUCT_NAME = "Test Product"
    }

    private lateinit var getProductDetailUseCase: GetProductDetailUseCase
    private lateinit var editMonthlyStockUseCase: EditMonthlyStockUseCase
    private lateinit var getLatestPreviousMonthStock: GetLatestPreviousMonthStock
    private val timeService = TestTimeService().apply {
        setCurrentTime(CURRENT_YEAR_MONTH)
    }
    private val inTransactions = listOf(
        ProductTransaction(
            id = 1,
            date = CURRENT_YEAR_MONTH.atDay(5)
                .atStartOfDay(timeService.getTimezone().toZoneId())
                .toInstant()
                .toEpochMilli(),
            price = 100_000,
            quantity = 2,
            unitType = UnitType.CARTON,
            ppn = null,
            profileName = "Supplier A",
        )
    )

    private val outTransactions = listOf(
        ProductTransaction(
            id = 2,
            date = CURRENT_YEAR_MONTH.atDay(10)
                .atStartOfDay(timeService.getTimezone().toZoneId())
                .toInstant()
                .toEpochMilli(),
            price = 50_000,
            quantity = 1,
            unitType = UnitType.PIECE,
            ppn = null,
            profileName = "Customer B",
        )
    )

    private val robot by lazy { uiUtils.productDetailRobot }

    private fun createProductDetail(
        name: String = PRODUCT_NAME,
        stock: ProductQuantityDto = INITIAL_STOCK,
        inTransactions: List<ProductTransaction> = this@ProductDetailScreenTest.inTransactions,
        outTransactions: List<ProductTransaction> = this@ProductDetailScreenTest.outTransactions,
    ) = ProductDetail(
        id = PRODUCT_ID,
        productName = name,
        transactionSummary = TransactionSummary(
            firstDayOfMonthStock = stock,
            monthlyStockId = MONTHLY_STOCK_ID,
            inProductTransactions = inTransactions,
            outProductTransactions = outTransactions,
        )
    )

    @Before
    fun setup() {
        getProductDetailUseCase = mock()
        editMonthlyStockUseCase = mock()
        getLatestPreviousMonthStock = mock()

        koin.loadModules(
            listOf(
                module {
                    single<ITimeService> { timeService }
                    single<IErrorReportingService> { FakeErrorReportingService() }
                    single<IDispatcherProvider> { TestDispatcherProvider() }
                    single { getProductDetailUseCase }
                    single { editMonthlyStockUseCase }
                    single { getLatestPreviousMonthStock }
                    viewModel { params ->
                        ProductDetailViewModel(
                            productId = params.get(),
                            getProductDetail = get(),
                            timeService = get(),
                        )
                    }
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

    private fun userOpenProductDetailScreen() {
        composeRule.activity.setContent {
            MainComposable(
                koinApp = koinApp,
                startDestination = MyRoutes.NavGraph.ProductDetail,
            )
        }
    }

    @Test
    fun `Should show loading indicator when data is loading`() {
        given {
            getProductDetailUseCase.stubReturnsLoadingOnly()
            userOpenProductDetailScreen()
        }

        then {
            robot.assertLoadingIndicatorDisplayed()
        }
    }

    @Test
    fun `Should show error state and retry button when use case fails`() {
        given {
            getProductDetailUseCase.stubReturnsError()
            userOpenProductDetailScreen()
        }

        then {
            robot.assertRetryButtonDisplayed()
        }
    }

    @Test
    fun `Should display product name period and transaction summary correctly when succeed`() {
        val productDetail = createProductDetail()

        given {
            getProductDetailUseCase.stubReturns(productDetail)
            userOpenProductDetailScreen()
            robot.waitUntilDataLoaded()
        }

        then {
            robot.assertProductName(PRODUCT_NAME)
            robot.assertPeriod(month = 0, year = 2020)
            robot.assertFirstDayOfMonthStock(INITIAL_STOCK)
            robot.assertLatestStock(
                ProductQuantityDto(cartonQuantity = 7, pieceQuantity = 9)
            )
            robot.assertInStock(
                quantity = ProductQuantityDto(cartonQuantity = 2, pieceQuantity = 0),
                price = 100_000L,
            )
            robot.assertOutStock(
                quantity = ProductQuantityDto(cartonQuantity = 0, pieceQuantity = 1),
                price = 50_000L,
            )
        }
    }

    @Test
    fun `Should show empty transaction messages when in and out transactions list is empty`() {
        val emptyDetail = createProductDetail(
            inTransactions = emptyList(),
            outTransactions = emptyList(),
        )

        given {
            getProductDetailUseCase.stubReturns(emptyDetail)
            userOpenProductDetailScreen()
            robot.waitUntilDataLoaded()
        }

        then {
            robot.assertTransactionHistoriesEmptyIndicatorAppear()
        }
    }

    @Test
    fun `When change period button clicked should show ChangePeriodDialog`() {
        given {
            getProductDetailUseCase.stubReturns(createProductDetail())
            userOpenProductDetailScreen()
            robot.waitUntilDataLoaded()
        }

        `when` {
            robot.openChangePeriodDialog()
        }

        then {
            robot.assertChangePeriodDialogOpened()
        }
    }

    @Test
    fun `When period changed via dialog should reload detail product with new period`() {
        val newProductName = "Updated Product After Period Change"
        val initialDetail = createProductDetail()
        val differentDetail = createProductDetail(name = newProductName)

        given {
            getProductDetailUseCase.stubSequentiallyReturns(first = initialDetail, second = differentDetail)
            userOpenProductDetailScreen()
            robot.waitUntilDataLoaded()
            robot.assertProductName(PRODUCT_NAME)
        }

        `when` {
            robot.incrementMonth(1)
        }

        then {
            robot.assertProductName(newProductName)
        }
    }

    @Test
    fun `When edit stock button clicked should navigate to EditMonthlyStock dialog`() {
        given {
            getProductDetailUseCase.stubReturns(createProductDetail())
            userOpenProductDetailScreen()
            robot.waitUntilDataLoaded()
        }

        `when` {
            robot.openEditStockDialog()
        }

        then {
            uiUtils.editMonthlyStockDialogRobot.assertAppear()
        }
    }

    @Test
    fun `When stock edited should reflect updated stock on screen after returning`() {
        val initialDetail = createProductDetail()
        val updatedDetail = createProductDetail(stock = UPDATED_STOCK)

        given {
            getProductDetailUseCase.stubSequentiallyReturns(
                first = initialDetail,
                second = updatedDetail,
            )
            editMonthlyStockUseCase.stubReturnsSuccess()
            userOpenProductDetailScreen()
            robot.assertFirstDayOfMonthStock(INITIAL_STOCK)
        }

        `when` {
            robot.openEditStockDialog()

            uiUtils.editMonthlyStockDialogRobot.run {
                waitUntilLoaded()
                clickSave()
            }
        }

        then {
            robot.assertFirstDayOfMonthStock(UPDATED_STOCK)
        }
    }

    @Test
    fun `When product name edited via dialog should reflect new name on screen`() {
        TODO("Not yet implemented \u2014 requires EditProductNameDialog integration in ProductDetail nav graph")
    }
}
