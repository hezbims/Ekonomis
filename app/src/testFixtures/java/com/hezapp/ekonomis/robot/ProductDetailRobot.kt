package com.hezapp.ekonomis.robot

import android.content.Context
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasAnySibling
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.monthly_stock.entity.QuantityPerUnitType
import com.hezapp.ekonomis.core.presentation.utils.getStringId
import com.hezapp.ekonomis.core.presentation.utils.toRupiahV2
import com.hezapp.ekonomis.product_detail.presentation.test_tag.ProductDetailTestTag
import com.hezapp.ekonomis.test_utils.testCalendarProvider
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProductDetailRobot(
    private val composeRule: ComposeTestRule,
    private val context: Context,
) {
    fun assertInStock(
        quantity: QuantityPerUnitType,
        price: Long,
    ){
        val inSectionLabel = "\u2022 ${context.getString(R.string.in_product_label)}"

        composeRule.onNode(
            hasText(inSectionLabel) and
            hasAnySibling(
                hasText(getQuantityString(quantity)) and
                hasTestTag(ProductDetailTestTag.inQuantity)
            ) and
            hasAnySibling(
                hasText(price.toRupiahV2()) and
                hasTestTag(ProductDetailTestTag.totalInPrice)
            )
        ).assertExists()
    }

    fun assertOutStock(
        quantity: QuantityPerUnitType,
        price: Long,
    ){
        val outSectionLabel = "\u2022 ${context.getString(R.string.out_product_label)}"

        composeRule.onNode(
            hasText(outSectionLabel) and
            hasAnySibling(
                hasText(getQuantityString(quantity)) and
                hasTestTag(ProductDetailTestTag.outQuantity)
            ) and
            hasAnySibling(
                hasText(price.toRupiahV2()) and
                hasTestTag(ProductDetailTestTag.totalOutPrice)
            )
        ).assertExists()
    }

    fun assertFirstDayOfMonthStock(quantity: QuantityPerUnitType){
        val firstDayOfMonthSectionLabel = "\u2022 ${context.getString(R.string.beginning_of_month_stock_label)}"

        composeRule.onNode(
            hasText(firstDayOfMonthSectionLabel) and
            hasAnySibling(
                hasText(getQuantityString(quantity)) and
                hasTestTag(ProductDetailTestTag.beginningOfMonthStock)
            )
        ).assertExists()
    }

    fun assertLatestStock(quantity: QuantityPerUnitType){
        val latestStockSectionLabel = "\u2022 ${context.getString(R.string.latest_stock_label)}"

        composeRule.onNode(
            hasText(latestStockSectionLabel) and
            hasAnySibling(
                hasText(getQuantityString(quantity)) and
                hasTestTag(ProductDetailTestTag.latestStock)
            )
        )
    }

    fun assertPeriod(
        month: Int,
        year: Int,
    ){
        val calendar = testCalendarProvider.getCalendar().apply {
            set(Calendar.MONTH, month)
            set(Calendar.YEAR, year)
        }
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.US)
        composeRule.onNodeWithText(
            dateFormat.format(calendar.time)
        ).assertExists()
    }

    fun incrementMonth(totalIncrement: Int){
        composeRule
            .onNodeWithContentDescription(context.getString(R.string.change_period_button))
            .performClick()

        composeRule.onNodeWithContentDescription(context.getString(R.string.increment_month_and_year_label)).let { node ->
            repeat(totalIncrement) {
                node.performClick()
            }
        }
        composeRule.onNodeWithText(context.getString(R.string.confirm_label)).performClick()
    }

    fun decrementMonth(totalDecrement: Int){
        composeRule
            .onNodeWithContentDescription(context.getString(R.string.change_period_button))
            .performClick()

        composeRule.onNodeWithContentDescription(context.getString(R.string.decrement_month_and_year_label)).let { node ->
            repeat (totalDecrement) {
                node.performClick()
            }
        }
        composeRule.onNodeWithText(context.getString(R.string.confirm_label)).performClick()
    }

    @OptIn(ExperimentalTestApi::class)
    fun waitUntilDataLoaded(){
        composeRule.waitUntilExactlyOneExists(
            hasText(context.getString(R.string.transaction_summary))
        )
    }

    private fun getQuantityString(quantity: QuantityPerUnitType) : String {
        val cartonString = context.getString(UnitType.CARTON.getStringId())
        val pieceString = context.getString(UnitType.PIECE.getStringId())
        return "${quantity.cartonQuantity} $cartonString, ${quantity.pieceQuantity} $pieceString"
    }
}