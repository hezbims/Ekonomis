package com.hezapp.ekonomis.test_case.product_detail.application.use_case

import com.hezapp.ekonomis.core.domain.monthly_stock.entity.QuantityPerUnitType
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.product_detail.domain.use_case.GetLatestPreviousMonthStock
import com.hezapp.ekonomis.test_application.BaseDataUnitTest
import com.hezapp.ekonomis.test_utils.seeder.dsl.monthly_stock.monthlyStock
import com.hezapp.ekonomis.test_utils.seeder.dsl.product.product
import com.hezapp.ekonomis.test_utils.seeder.dsl.profile.customerProfile
import com.hezapp.ekonomis.test_utils.seeder.dsl.profile.supplierProfile
import com.hezapp.ekonomis.test_utils.seeder.dsl.transaction.dto.QuantityData
import com.hezapp.ekonomis.test_utils.seeder.dsl.transaction.transactionOn
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import java.time.Month
import java.time.YearMonth

class GetLatestPreviousMonthStockTest : BaseDataUnitTest() {

    private val sut by lazy { koin.get<GetLatestPreviousMonthStock>() }

    private val onCurrentMonth = YearMonth.of(2020, Month.JANUARY)
    private val onPreviousMonth = onCurrentMonth.minusMonths(1)
    private val onNextMonth = onCurrentMonth.plusMonths(1)
    private var primaryObservedProduct: Int = 0
    private var obstacleProduct: Int = 0
    private var buyerProfileId: Int = 0
    private var supplierProfileId: Int = 0

    @Before
    fun background() {
        configDsl.currentTimeIs(onCurrentMonth)
        seedObstacleData()
    }

    private fun seedObstacleData() = seederDsl.run {
        primaryObservedProduct = product(name = "observed-product").id
        obstacleProduct = product(name = "obstacle-product").id
        buyerProfileId = customerProfile(name = "buyer-1").id
        supplierProfileId = supplierProfile(name = "supplier-1").id

        transactionOn(onCurrentMonth) {
            `in`(day = 1, ppn = 12, profileId = supplierProfileId) {
                withProduct(id = primaryObservedProduct, quantity = QuantityData.carton(99), price = 10_000)
            }
            out(day = 2, profileId = buyerProfileId) {
                withProduct(id = primaryObservedProduct, quantity = QuantityData.carton(50), price = 20_000)
            }
        }

        transactionOn(onNextMonth) {
            `in`(day = 1, ppn = 12, profileId = supplierProfileId) {
                withProduct(id = primaryObservedProduct, quantity = QuantityData.carton(999), price = 10_000)
            }
        }
    }

    @Test
    fun `When there is no recorded stock on previous month, but there is transaction on previous month, then the result must be total of the previous month transaction`() {
        seederDsl.run {
            transactionOn(onPreviousMonth) {
                `in`(day = 5, ppn = 12, profileId = supplierProfileId) {
                    withProduct(id = primaryObservedProduct, quantity = QuantityData.carton(10), price = 15_000)
                    withProduct(id = primaryObservedProduct, quantity = QuantityData.piece(20), price = 2_000)
                }
                out(day = 10, profileId = buyerProfileId) {
                    withProduct(id = primaryObservedProduct, quantity = QuantityData.carton(3), price = 25_000)
                    withProduct(id = primaryObservedProduct, quantity = QuantityData.piece(5), price = 3_500)
                }
            }
        }

        val result = getPreviousMonthStock()

        // latestDayOfMonthStock = 0 (null firstDayOfMonthStock) + totalIn(10,20) - totalOut(3,5)
        assertThat(result.cartonQuantity, equalTo(7))
        assertThat(result.pieceQuantity, equalTo(15))
    }

    @Test
    fun `When there is recorded stock on previous month, and there is transaction on previous month, then the result must be total of the previous month transaction + previous month recorded stock`() {
        seederDsl.run {
            monthlyStock(onPreviousMonth, productId = primaryObservedProduct, carton = 50, piece = 100)
            transactionOn(onPreviousMonth) {
                `in`(day = 5, ppn = 12, profileId = supplierProfileId) {
                    withProduct(id = primaryObservedProduct, quantity = QuantityData.carton(10), price = 15_000)
                    withProduct(id = primaryObservedProduct, quantity = QuantityData.piece(20), price = 2_000)
                }
                out(day = 10, profileId = buyerProfileId) {
                    withProduct(id = primaryObservedProduct, quantity = QuantityData.carton(3), price = 25_000)
                    withProduct(id = primaryObservedProduct, quantity = QuantityData.piece(5), price = 3_500)
                }
            }
        }

        val result = getPreviousMonthStock()

        // latestDayOfMonthStock = stock(50,100) + totalIn(10,20) - totalOut(3,5)
        assertThat(result.cartonQuantity, equalTo(57))
        assertThat(result.pieceQuantity, equalTo(115))
    }

    @Test
    fun `When there is recorded stock on previous month, but there is not transaction on previous month, then the result must be the recorded stock of previous month`() {
        seederDsl.monthlyStock(onPreviousMonth, productId = primaryObservedProduct, carton = 30, piece = 60)

        val result = getPreviousMonthStock()

        assertThat(result.cartonQuantity, equalTo(30))
        assertThat(result.pieceQuantity, equalTo(60))
    }

    @Test
    fun `When there is no recorded stock on previous month, and there is no transaction on previous month, then the result must be zero`() {
        val result = getPreviousMonthStock()

        assertThat(result.cartonQuantity, equalTo(0))
        assertThat(result.pieceQuantity, equalTo(0))
    }

    @Test
    fun `(Edge) Obstacle product transactions and stock on previous month should not affect observed product`() {
        seederDsl.run {
            monthlyStock(onPreviousMonth, productId = obstacleProduct, carton = 999, piece = 888)
            transactionOn(onPreviousMonth) {
                `in`(day = 1, ppn = 12, profileId = supplierProfileId) {
                    withProduct(id = obstacleProduct, quantity = QuantityData.carton(100), price = 10_000)
                }
            }
        }

        val result = getPreviousMonthStock()

        assertThat(result.cartonQuantity, equalTo(0))
        assertThat(result.pieceQuantity, equalTo(0))
    }

    @Test
    fun `(Edge) Only in transactions on previous month (no out) and no stock should equal total in`() {
        seederDsl.run {
            transactionOn(onPreviousMonth) {
                `in`(day = 5, ppn = 12, profileId = supplierProfileId) {
                    withProduct(id = primaryObservedProduct, quantity = QuantityData.carton(8), price = 15_000)
                    withProduct(id = primaryObservedProduct, quantity = QuantityData.piece(4), price = 2_000)
                }
            }
        }

        val result = getPreviousMonthStock()

        assertThat(result.cartonQuantity, equalTo(8))
        assertThat(result.pieceQuantity, equalTo(4))
    }

    @Test
    fun `(Edge) Only out transactions on previous month (no in) and no stock should equal negative total out`() {
        seederDsl.run {
            transactionOn(onPreviousMonth) {
                out(day = 10, profileId = buyerProfileId) {
                    withProduct(id = primaryObservedProduct, quantity = QuantityData.carton(2), price = 25_000)
                    withProduct(id = primaryObservedProduct, quantity = QuantityData.piece(7), price = 3_500)
                }
            }
        }

        val result = getPreviousMonthStock()

        assertThat(result.cartonQuantity, equalTo(-2))
        assertThat(result.pieceQuantity, equalTo(-7))
    }

    @Test
    fun `(Edge) Stock on current month should not be picked up as previous month stock`() {
        seederDsl.run {
            monthlyStock(onCurrentMonth, productId = primaryObservedProduct, carton = 999, piece = 999)
        }

        val result = getPreviousMonthStock()

        assertThat(result.cartonQuantity, equalTo(0))
        assertThat(result.pieceQuantity, equalTo(0))
    }

    private fun getPreviousMonthStock(): QuantityPerUnitType = runBlocking {
        val flowResult = sut.invoke(
            currentMonthPeriod = timeService.convertToTimeToEpochMillis(onCurrentMonth),
            productId = primaryObservedProduct,
        ).last()
        flowResult.asSucceed().data
    }

    private val timeService: ITimeService by lazy { koin.get() }
}
