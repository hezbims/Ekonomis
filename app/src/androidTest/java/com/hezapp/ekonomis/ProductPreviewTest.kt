package com.hezapp.ekonomis

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.robot.ProductPreviewRobot
import com.hezapp.ekonomis.robot.TransactionHistoryRobot
import com.hezapp.ekonomis.test_data.MySeederUtils
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductPreviewTest {
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

    @Before
    fun before(){
        MySeederUtils.seedTigaBulanTransaksi()
    }

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()
    private val transactionHistoryRobot = TransactionHistoryRobot(composeRule)
    private val productPreviewRobot = ProductPreviewRobot(composeRule)
}