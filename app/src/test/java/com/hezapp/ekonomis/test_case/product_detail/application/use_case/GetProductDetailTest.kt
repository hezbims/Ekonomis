package com.hezapp.ekonomis.test_case.product_detail.application.use_case

import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.product.model.ProductDetail
import com.hezapp.ekonomis.core.domain.product.model.ProductTransaction
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.product_detail.domain.use_case.GetProductDetailUseCase
import com.hezapp.ekonomis.test_application.BaseDataUnitTest
import com.hezapp.ekonomis.test_utils.TestTimeService
import com.hezapp.ekonomis.test_utils.seeder.dsl.monthly_stock.thereIsMonthlyStock
import com.hezapp.ekonomis.test_utils.seeder.dsl.product.thereIsProduct
import com.hezapp.ekonomis.test_utils.seeder.dsl.transaction.dto.QuantityData
import com.hezapp.ekonomis.test_utils.seeder.dsl.transaction.thereIsTransactionOn
import kotlinx.coroutines.flow.last
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
import java.time.ZoneId

class GetProductDetailTest : BaseDataUnitTest() {
    private val onCurrentMonth = YearMonth.of(2020, Month.JANUARY)
    private val onPreviousMonth = onCurrentMonth.minusMonths(1)
    private val onNextMonth = onCurrentMonth.plusMonths(1)
    private var currentProduct : Int = 0
    private var otherProduct : Int = 0

    @Before
    fun background() : Unit = runBlocking {
        currentMonthYearIs(onCurrentMonth)

        val buyerProfileId = profileSeeder.runV2(
            profileName = "buyer-1",
            profileType = ProfileType.CUSTOMER,
        ).id
        val supplierProfileId = profileSeeder.runV2(
            profileName = "supplier-1",
            profileType = ProfileType.SUPPLIER,
        ).id

        currentProduct = seederDsl.thereIsProduct(name = "observed-product").id
        otherProduct = seederDsl.thereIsProduct(name = "otherProduct").id

        seederDsl.thereIsTransactionOn(onCurrentMonth) {
            `in`(day = 1, ppn = 12, profileId = supplierProfileId) {
                withProduct(id = currentProduct, quantity = QuantityData.carton(2), price = 15_000)
                withProduct(id = currentProduct, quantity = QuantityData.piece(2), price = 2_000)
            }
            out(day = 2, profileId = buyerProfileId) {
                withProduct(id = currentProduct, quantity = QuantityData.carton(5), price = 25_000)
                withProduct(id = currentProduct, quantity = QuantityData.piece(6), price = 3_500)
            }
            `in`(day = 3, ppn = 10, profileId = supplierProfileId) {
                withProduct(id = currentProduct, quantity = QuantityData.carton(4), price = 20_000)
                withProduct(id = currentProduct, quantity = QuantityData.piece(3), price = 3_000)
            }
            out(day = 4, profileId = buyerProfileId) {
                withProduct(id = currentProduct, quantity = QuantityData.carton(6), price = 30_000)
                withProduct(id = currentProduct, quantity = QuantityData.piece(8), price = 4_500)
            }
            `in`(day = 15, ppn = 12, profileId = supplierProfileId) {
                withProduct(id = otherProduct, quantity = QuantityData.carton(99), price = 12_000)
                withProduct(id = otherProduct, quantity = QuantityData.piece(99), price = 1_500)
            }
        }

        seederDsl.thereIsTransactionOn(onPreviousMonth) {
            `in`(day = 5, ppn = 12, profileId = supplierProfileId) {
                withProduct(id = currentProduct, quantity = QuantityData.carton(200), price = 14_000)
                withProduct(id = currentProduct, quantity = QuantityData.piece(200), price = 1_800)
            }
        }

        seederDsl.thereIsTransactionOn(onNextMonth) {
            out(day = 23, profileId = buyerProfileId) {
                withProduct(id = currentProduct, quantity = QuantityData.carton(1), price = 25_000)
                withProduct(id = currentProduct, quantity = QuantityData.piece(1), price = 3_500)
            }
        }

        // Obstacle
        seederDsl.thereIsMonthlyStock(onNextMonth, productId = currentProduct,  carton = 10, piece = 15)
        seederDsl.thereIsMonthlyStock(onCurrentMonth, productId = otherProduct, carton = 21, piece = 23)
    }

    @Test
    fun `(Initial Month Stock) - When there is no recorded stock data on this month, but there is recorded stock from previous month, initial stock should use calculation from previous month`() {
        // GIVEN
        seederDsl.thereIsMonthlyStock(onPreviousMonth, productId = currentProduct, carton = 9, piece = 5)

        // WHEN
        val productDetail = getCurrentProductDetailOnCurrentMonth()

        // THEN
        assertThat(productDetail.firstDayOfMonthStock.pieceQuantity, equalTo(205))
        assertThat(productDetail.firstDayOfMonthStock.cartonQuantity, equalTo(209))
    }

    @Test
    fun `(Initial Month Stock) - When there is no recorded stock data on this month, and there is no recorded stock from previous month, initial stock should use calculation from previous month`(){
        // WHEN
        val productDetail = getCurrentProductDetailOnCurrentMonth()

        // THEN
        assertThat(productDetail.firstDayOfMonthStock.pieceQuantity, equalTo(200))
        assertThat(productDetail.firstDayOfMonthStock.cartonQuantity, equalTo(200))
    }

    @Test
    fun `(Initial Month Stock) - When there is recorded stock data on this month, initial stock should use that record`() {
        // GIVEN
        seederDsl.thereIsMonthlyStock(onCurrentMonth, productId = currentProduct, carton = 3033, piece = 4044)
        seederDsl.thereIsMonthlyStock(onPreviousMonth, productId = currentProduct, carton = 9, piece = 5) // obstacle

        // WHEN
        val productDetail = getCurrentProductDetailOnCurrentMonth()

        // THEN
        assertThat(productDetail.firstDayOfMonthStock.pieceQuantity, equalTo(4044))
        assertThat(productDetail.firstDayOfMonthStock.cartonQuantity, equalTo(3033))
    }

    @Test
    fun `(Other Data) - Should capture other data correctly`(){
        // GIVEN
        seederDsl.thereIsMonthlyStock(onCurrentMonth, productId = currentProduct, carton = 3033, piece = 4044)

        // WHEN
        val productDetail = getCurrentProductDetailOnCurrentMonth()

        // THEN
        assertThat(productDetail.id, equalTo(currentProduct))
        assertThat(productDetail.productName, equalTo("observed-product"))
        assertThat(productDetail.firstDayOfMonthStock.pieceQuantity, equalTo(4044))
        assertThat(productDetail.firstDayOfMonthStock.cartonQuantity, equalTo(3033))
        assertThat(productDetail.monthlyStockId, not(equalTo(0)))

        assertThat(productDetail.outProductTransactions, hasSize(4))
        assertOutTransactionItemsContain(productDetail.outProductTransactions, day = 2, UnitType.CARTON, quantity = 5, price = 25_000)
        assertOutTransactionItemsContain(productDetail.outProductTransactions, day = 2, UnitType.PIECE, quantity = 6, price = 3_500)
        assertOutTransactionItemsContain(productDetail.outProductTransactions, day = 4, UnitType.CARTON, quantity = 6, price = 30_000)
        assertOutTransactionItemsContain(productDetail.outProductTransactions, day = 4, UnitType.PIECE, quantity = 8, price = 4_500)

        assertThat(productDetail.inProductTransactions, hasSize(4))
        assertInTransactionItemsContain(productDetail.inProductTransactions, day = 1, UnitType.CARTON, quantity = 2, price = 15_000, ppn = 12)
        assertInTransactionItemsContain(productDetail.inProductTransactions, day = 1, UnitType.PIECE, quantity = 2, price = 2_000, ppn = 12)
        assertInTransactionItemsContain(productDetail.inProductTransactions, day = 3, UnitType.CARTON, quantity = 4, price = 20_000, ppn = 10)
        assertInTransactionItemsContain(productDetail.inProductTransactions, day = 3, UnitType.PIECE, quantity = 3, price = 3_000, ppn = 10)

        assertThat(productDetail.totalOutUnit.cartonQuantity, equalTo(11))
        assertThat(productDetail.totalOutUnit.pieceQuantity, equalTo(14))
        assertThat(productDetail.totalInUnit.cartonQuantity, equalTo(6))
        assertThat(productDetail.totalInUnit.pieceQuantity, equalTo(5))
        assertThat(productDetail.latestDayOfMonthStock.cartonQuantity, equalTo(3028))
        assertThat(productDetail.latestDayOfMonthStock.pieceQuantity, equalTo(4035))
        assertThat(productDetail.totalOutPrice, equalTo(63_000L))
        assertThat(productDetail.totalInPrice, equalTo(40_000L))
    }

    private fun assertOutTransactionItemsContain(
        actualTransactions: List<ProductTransaction>,
        day: Int,
        unitType: UnitType,
        quantity: Int,
        price: Int,
    ) {
        val dateEpochMillis = onCurrentMonth.atDay(day)
            .atStartOfDay(timeService.getZoneId())
            .toInstant()
            .toEpochMilli()
        val expectedPpn = null
        val expectedProfileName = "buyer-1"
        val matchingTransaction = actualTransactions.firstOrNull {
            it.date == dateEpochMillis &&
            it.unitType == unitType &&
            it.quantity == quantity &&
            it.price == price &&
            it.ppn == expectedPpn &&
            it.profileName == expectedProfileName
        }
        assertThat(
            "There is no ProductTransaction found with specified attributes: " +
            "day=$day, unitType=$unitType, quantity=$quantity, " +
            "price=$price, ppn=$expectedPpn, profileName=$expectedProfileName",
            matchingTransaction,
            not(nullValue())
        )
    }

    private fun assertInTransactionItemsContain(
        actualTransactions: List<ProductTransaction>,
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
        val expectedProfileName = "supplier-1"
        val matchingTransaction = actualTransactions.firstOrNull {
            it.date == dateEpochMillis &&
            it.unitType == unitType &&
            it.quantity == quantity &&
            it.price == price &&
            it.ppn == ppn &&
            it.profileName == expectedProfileName
        }
        assertThat(
            "There is no ProductTransaction found with specified attributes: " +
            "day=$day, unitType=$unitType, quantity=$quantity, " +
            "price=$price, ppn=$ppn, profileName=$expectedProfileName",
            matchingTransaction,
            not(nullValue())
        )
    }

    fun currentMonthYearIs(yearMonth: YearMonth){
        (koin.get<ITimeService>() as TestTimeService).setCurrentTime(
            localDate = yearMonth.atDay(1),
            zoneId = ZoneId.of("UTC+8")
        )
    }

    fun getCurrentProductDetailOnCurrentMonth() : ProductDetail = runBlocking {
        val productDetailResult = getProductDetailUseCase.invoke(
            productId = currentProduct,
            monthYearPeriod = timeService.convertToTimeToEpochMillis(onCurrentMonth)).last()

        return@runBlocking productDetailResult.asSucceed().data
    }

    private val timeService: ITimeService by lazy { koin.get() }
    private val getProductDetailUseCase by lazy { koin.get<GetProductDetailUseCase>() }
}
