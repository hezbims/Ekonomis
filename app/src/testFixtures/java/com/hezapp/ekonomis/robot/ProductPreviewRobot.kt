package com.hezapp.ekonomis.robot

import android.content.Context
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.presentation.utils.getStringId
import com.hezapp.ekonomis.core.presentation.utils.toRupiah

class ProductPreviewRobot(
    private val composeRule: ComposeTestRule,
    private val context: Context,
) {

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