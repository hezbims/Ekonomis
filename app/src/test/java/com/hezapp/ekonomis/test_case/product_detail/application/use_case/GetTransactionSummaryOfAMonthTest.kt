package com.hezapp.ekonomis.test_case.product_detail.application.use_case

import com.hezapp.ekonomis.assertion._base.matches
import com.hezapp.ekonomis.assertion.product_detail.application.ExpectedProductTransaction
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.product.model.ProductTransaction
import com.hezapp.ekonomis.core.domain.product.model.TransactionSummary
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.product_detail.domain.use_case.GetTransactionSummaryOfAMonthUseCase
import com.hezapp.ekonomis.test_application.BaseDataUnitTest
import com.hezapp.ekonomis.test_utils.seeder.dsl.monthly_stock.monthlyStock
import com.hezapp.ekonomis.test_utils.seeder.dsl.product.product
import com.hezapp.ekonomis.test_utils.seeder.dsl.profile.customerProfile
import com.hezapp.ekonomis.test_utils.seeder.dsl.profile.supplierProfile
import com.hezapp.ekonomis.test_utils.seeder.dsl.transaction.dto.QuantityData
import com.hezapp.ekonomis.test_utils.seeder.dsl.transaction.transactionOn
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.junit.Before
import org.junit.Test
import java.time.Month
import java.time.YearMonth

@Suppress("SameParameterValue")
class GetTransactionSummaryOfAMonthTest : BaseDataUnitTest() {
    private val sut : GetTransactionSummaryOfAMonthUseCase by lazy { koin.get() }

    private val onCurrentMonth = YearMonth.of(2020, Month.JANUARY)
    private val onPreviousMonth = onCurrentMonth.minusMonths(1)
    private val onNextMonth = onCurrentMonth.plusMonths(1)
    private var primaryObservedProduct : Int = 0
    private var obstacleProduct : Int = 0
    private var silentProduct : Int = 0 // Product with no transactions

    @Before
    fun background() {
        configDsl.currentTimeIs(onCurrentMonth)

        seedData()
    }

    private fun seedData() : Unit = seederDsl.run {
        val buyerProfileId = customerProfile(name = "buyer-1").id
        val supplierProfileId = supplierProfile(name = "supplier-1").id

        primaryObservedProduct = product(name = "observed-product").id
        obstacleProduct = product(name = "other-product").id
        silentProduct = product(name = "silent-product").id

        transactionOn(onCurrentMonth) {
            `in`(day = 1, ppn = 12, profileId = supplierProfileId) {
                withProduct(id = primaryObservedProduct, quantity = QuantityData.carton(2), price = 15_000)
                withProduct(id = primaryObservedProduct, quantity = QuantityData.piece(2), price = 2_000)
            }
            out(day = 2, profileId = buyerProfileId) {
                withProduct(id = primaryObservedProduct, quantity = QuantityData.carton(5), price = 25_000)
                withProduct(id = primaryObservedProduct, quantity = QuantityData.piece(6), price = 3_500)
            }
            `in`(day = 15, ppn = 10, profileId = supplierProfileId) {
                withProduct(id = obstacleProduct, quantity = QuantityData.carton(99), price = 12_000)
            }
        }

        transactionOn(onPreviousMonth) {
            `in`(day = 5, ppn = 12, profileId = supplierProfileId) {
                withProduct(id = primaryObservedProduct, quantity = QuantityData.carton(200), price = 14_000)
                withProduct(id = primaryObservedProduct, quantity = QuantityData.piece(200), price = 1_800)
            }
        }

        transactionOn(onNextMonth) {
            out(day = 23, profileId = buyerProfileId) {
                withProduct(id = primaryObservedProduct, quantity = QuantityData.carton(1), price = 25_000)
                withProduct(id = primaryObservedProduct, quantity = QuantityData.piece(1), price = 3_500)
            }
        }
    }

    @Test
    fun `When there is no recorded stock on current month, then the first day of month stock should be zero`(){
        // GIVEN — no stock seeded for currentProduct on onCurrentMonth (intentional from background)

        // WHEN
        val summary = getSummary()

        // THEN
        assertThat(summary.firstDayOfMonthStock, nullValue())
        assertThat(summary.monthlyStockId, equalTo(0))
    }

    @Test
    fun `When there is recorded stock on current month, the the first day of month stock should be using that record`(){
        // GIVEN
        seederDsl.monthlyStock(onCurrentMonth, primaryObservedProduct, carton = 3033, piece = 4044)

        // WHEN
        val summary = getSummary()

        // THEN
        assertThat(summary.firstDayOfMonthStock?.pieceQuantity, equalTo(4044))
        assertThat(summary.firstDayOfMonthStock?.cartonQuantity, equalTo(3033))
        assertThat(summary.monthlyStockId, not(equalTo(0)))
    }

    @Test
    fun `The obtained out-product transaction must be correct based on year-month and ID`(){
        // GIVEN — background has out transactions for currentProduct on onCurrentMonth

        // WHEN
        val summary = getSummary()

        // THEN
        assertThat(summary.outProductTransactions, hasSize(2))
        assertOutTransaction(summary.outProductTransactions[0], day = 2, UnitType.CARTON, quantity = 5, price = 25_000)
        assertOutTransaction(summary.outProductTransactions[1], day = 2, UnitType.PIECE, quantity = 6, price = 3_500)
    }

    @Test
    fun `The obtained in-product transaction must be correct based on year-month and ID`(){
        // GIVEN — background has in transactions for currentProduct on onCurrentMonth

        // WHEN
        val summary = getSummary()

        // THEN
        assertThat(summary.inProductTransactions, hasSize(2))
        assertInTransaction(summary.inProductTransactions[0], day = 1, UnitType.CARTON, quantity = 2, price = 15_000, ppn = 12)
        assertInTransaction(summary.inProductTransactions[1], day = 1, UnitType.PIECE, quantity = 2, price = 2_000, ppn = 12)
    }

    @Test
    fun `(Edge) latestDayOfMonthStock should be zero when there is no stock and no transactions`(){
        // GIVEN — silentProduct has no stock and no transactions on onCurrentMonth

        // WHEN
        val summary = getSummaryForProduct(silentProduct)

        // THEN
        assertThat(summary.firstDayOfMonthStock, nullValue())
        assertThat(summary.totalOutUnit.cartonQuantity, equalTo(0))
        assertThat(summary.totalOutUnit.pieceQuantity, equalTo(0))
        assertThat(summary.totalInUnit.cartonQuantity, equalTo(0))
        assertThat(summary.totalInUnit.pieceQuantity, equalTo(0))
        assertThat(summary.latestDayOfMonthStock.cartonQuantity, equalTo(0))
        assertThat(summary.latestDayOfMonthStock.pieceQuantity, equalTo(0))
    }

    @Test
    fun `(Edge) latestDayOfMonthStock should equal firstDayOfMonthStock when there is stock but no transactions`(){
        // GIVEN
        seederDsl.monthlyStock(onCurrentMonth, silentProduct, carton = 50, piece = 100)

        // WHEN
        val summary = getSummaryForProduct(silentProduct)

        // THEN
        assertThat(summary.firstDayOfMonthStock?.cartonQuantity, equalTo(50))
        assertThat(summary.firstDayOfMonthStock?.pieceQuantity, equalTo(100))
        assertThat(summary.totalOutUnit.cartonQuantity, equalTo(0))
        assertThat(summary.totalOutUnit.pieceQuantity, equalTo(0))
        assertThat(summary.totalInUnit.cartonQuantity, equalTo(0))
        assertThat(summary.totalInUnit.pieceQuantity, equalTo(0))
        assertThat(summary.latestDayOfMonthStock.cartonQuantity, equalTo(50))
        assertThat(summary.latestDayOfMonthStock.pieceQuantity, equalTo(100))
    }

    @Test
    fun `(Edge) latestDayOfMonthStock must be calculated correctly when firstDayOfMonthStock is null but there are transactions`(){
        // GIVEN — currentProduct has transactions but no stock on onCurrentMonth
        // in: (carton 2, piece 2); out: (carton 5, piece 6)

        // WHEN
        val summary = getSummary()

        // THEN
        assertThat(summary.firstDayOfMonthStock, nullValue())
        assertThat(summary.totalOutUnit.cartonQuantity, equalTo(5))
        assertThat(summary.totalOutUnit.pieceQuantity, equalTo(6))
        assertThat(summary.totalInUnit.cartonQuantity, equalTo(2))
        assertThat(summary.totalInUnit.pieceQuantity, equalTo(2))
        assertThat(summary.latestDayOfMonthStock.cartonQuantity, equalTo(-3))
        assertThat(summary.latestDayOfMonthStock.pieceQuantity, equalTo(-4))
    }

    @Test
    fun `latestDayOfMonthStock must be calculated correctly based on firstDayOfMonthStock and product transactions`(){
        // GIVEN
        seederDsl.monthlyStock(onCurrentMonth, primaryObservedProduct, carton = 10, piece = 20)

        // WHEN
        val summary = getSummary()

        // THEN
        assertThat(summary.totalOutUnit.cartonQuantity, equalTo(5))
        assertThat(summary.totalOutUnit.pieceQuantity, equalTo(6))
        assertThat(summary.totalInUnit.cartonQuantity, equalTo(2))
        assertThat(summary.totalInUnit.pieceQuantity, equalTo(2))
        assertThat(summary.latestDayOfMonthStock.cartonQuantity, equalTo(7))
        assertThat(summary.latestDayOfMonthStock.pieceQuantity, equalTo(16))
    }

    private fun getSummary() : TransactionSummary = runBlocking {
        sut.invoke(
            startPeriod = timeService.convertToTimeToEpochMillis(onCurrentMonth),
            productId = primaryObservedProduct,
        )
    }

    private fun getSummaryForProduct(productId: Int) : TransactionSummary = runBlocking {
        sut.invoke(
            startPeriod = timeService.convertToTimeToEpochMillis(onCurrentMonth),
            productId = productId,
        )
    }

    private fun assertOutTransaction(
        actualProductTransaction: ProductTransaction,
        day: Int,
        unitType: UnitType,
        quantity: Int,
        price: Int,
    ) {
        val dateEpochMillis = onCurrentMonth.atDay(day)
            .atStartOfDay(timeService.getZoneId())
            .toInstant()
            .toEpochMilli()

        actualProductTransaction.matches(ExpectedProductTransaction(
            date = dateEpochMillis,
            unitType = unitType,
            quantity = quantity,
            price = price,
            ppn = null,
            profileName = "buyer-1",
        ))
    }

    private fun assertInTransaction(
        actualProductTransaction: ProductTransaction,
        day: Int,
        unitType: UnitType,
        quantity: Int,
        price: Int,
        ppn: Int,
    ) {
        val dateEpochMillis = onCurrentMonth.atDay(day)
            .atStartOfDay(timeService.getZoneId())
            .toInstant()
            .toEpochMilli()
        actualProductTransaction.matches(ExpectedProductTransaction(
                date = dateEpochMillis,
                unitType = unitType,
                quantity = quantity,
                price = price,
                ppn = ppn,
                profileName = "supplier-1",
        ))
    }

    private val timeService: ITimeService by lazy { koin.get() }
}
