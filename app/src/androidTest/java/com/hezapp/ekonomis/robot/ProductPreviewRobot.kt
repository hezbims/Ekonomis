package com.hezapp.ekonomis.robot

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.hezapp.ekonomis.MainActivity
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.presentation.utils.getStringId
import com.hezapp.ekonomis.core.presentation.utils.toRupiah

class ProductPreviewRobot(
    private val composeRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>
) {
    private val context by lazy { composeRule.activity }

    fun assertProductCostOfGoods(
        productName: String,
        costOfGoods: Int,
        unitType: UnitType,
    ){
        val nodeInteraction = composeRule.onAllNodes(
        hasText(productName)
                and
                hasText(
                    context.getString(
                        R.string.cost_of_goods_label,
                        "${costOfGoods.toRupiah()}/${context.getString(unitType.getStringId())}"
                    )
                )
        )

        nodeInteraction.assertCountEquals(1)
    }

    fun toDetailProduct(productName: String){
        composeRule.onNode(
            hasText(productName)
        ).performClick()
    }
}