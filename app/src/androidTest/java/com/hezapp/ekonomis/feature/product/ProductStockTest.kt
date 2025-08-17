package com.hezapp.ekonomis.feature.product

import androidx.test.core.app.ActivityScenario
import com.hezapp.ekonomis.MainActivity
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.monthly_stock.entity.QuantityPerUnitType
import com.hezapp.ekonomis.test_application.BaseEkonomisUiTest
import com.hezapp.ekonomis.test_utils.MySeederUtils
import org.junit.Before
import org.junit.Test

class ProductStockTest : BaseEkonomisUiTest() {
    /**
     * Memastikan harga pokok tampil dengan benar
     */
    @Test
    fun ensureCostOfGoodsCalculatedCorrectly(){
        transactionHistoryRobot.navigateToProductPreview()
        productPreviewRobot.assertProductCostOfGoods(
            productName = "Barang 1",
            costOfGoods = 2220,
            unitType = UnitType.PIECE,
        )
        productPreviewRobot.assertProductCostOfGoods(
            productName = "Barang 2",
            costOfGoods = 5106,
            unitType = UnitType.PIECE,
        )
    }

    /**
     * Memastikan data-data yang ditampilkan pada detail stock benar
     */
    @Test
    fun ensureStockDetailCalculatedCorrectly(){
        transactionHistoryRobot.navigateToProductPreview()
        productPreviewRobot.toDetailProduct(
            productName = "Barang 1"
        )

        // Assertion stock bulan ini
        productDetailRobot.apply {
            waitUntilDataLoaded()
            assertPeriod(
                year = 2020,
                month = 1,
            )
            assertInStock(
                quantity = QuantityPerUnitType(cartonQuantity = 2, pieceQuantity = 5),
                price = 15000
            )
            assertOutStock(
                quantity = QuantityPerUnitType(cartonQuantity = 0, pieceQuantity = 1),
                price = 20000,
            )
            assertFirstDayOfMonthStock(
                quantity = QuantityPerUnitType(cartonQuantity = -4, pieceQuantity = 50)
            )
            assertLatestStock(
                quantity = QuantityPerUnitType(cartonQuantity = -2, pieceQuantity = 54)
            )
        }


        // Assertion stock bulan sebelumnya
        productDetailRobot.apply {
            decrementMonth(1)
            waitUntilDataLoaded()

            assertPeriod(
                month = 12,
                year = 2019,
            )
            assertInStock(
                quantity = QuantityPerUnitType(cartonQuantity = 0, pieceQuantity = 50),
                price = 250000
            )
            assertOutStock(
                quantity = QuantityPerUnitType(cartonQuantity = 4, pieceQuantity = 0),
                price = 50000
            )
            assertFirstDayOfMonthStock(
                quantity = QuantityPerUnitType(cartonQuantity = 0, pieceQuantity = 0)
            )
            assertLatestStock(
                quantity = QuantityPerUnitType(cartonQuantity = -4, pieceQuantity = 50)
            )
        }



        // Assertion dua bulan setelahnya
        productDetailRobot.apply {
            incrementMonth(2)
            waitUntilDataLoaded()

            assertPeriod(
                month = 2,
                year = 2020,
            )
            assertInStock(
                quantity = QuantityPerUnitType(cartonQuantity = 0, pieceQuantity = 0),
                price = 0
            )
            assertOutStock(
                quantity = QuantityPerUnitType(cartonQuantity = 0, pieceQuantity = 15),
                price = 12000
            )
            assertFirstDayOfMonthStock(
                quantity = QuantityPerUnitType(cartonQuantity = -2, pieceQuantity = 54)
            )
            assertLatestStock(
                quantity = QuantityPerUnitType(cartonQuantity = -2, pieceQuantity = 39)
            )
        }
    }

    @Before
    fun before(){
        MySeederUtils.Companion.seedTigaBulanTransaksi()
        ActivityScenario.launch(MainActivity::class.java)
    }
}