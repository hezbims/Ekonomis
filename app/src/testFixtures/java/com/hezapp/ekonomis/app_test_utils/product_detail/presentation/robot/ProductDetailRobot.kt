package com.hezapp.ekonomis.app_test_utils.product_detail.presentation.robot

import android.content.Context
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasAnySibling
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.monthly_stock.entity.QuantityPerUnitType
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.core.presentation.utils.getStringId
import com.hezapp.ekonomis.core.presentation.utils.toRupiahV2
import com.hezapp.ekonomis.product_detail.presentation.test_tag.ProductDetailTestTag
import com.hezapp.ekonomis.base_test_utils.constant.TestConstant
import java.text.SimpleDateFormat
import java.util.Calendar

/**
 * This is robot for [ProductDetailScreen][com.hezapp.ekonomis.product_detail.presentation.ProductDetailScreen]
 */
@OptIn(ExperimentalTestApi::class)
class ProductDetailRobot(
    private val composeRule: ComposeTestRule,
    private val context: Context,
    private val timeService: ITimeService,
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

        composeRule.waitUntilExactlyOneExists(
            matcher = hasText(firstDayOfMonthSectionLabel) and
                    hasAnySibling(
                        hasText(getQuantityString(quantity)) and
                                hasTestTag(ProductDetailTestTag.beginningOfMonthStock)
                    ),
            timeoutMillis = TestConstant.SMALL_TIMEOUT,
        )
    }

    fun assertTransactionHistoriesEmptyIndicatorAppear(){
        composeRule.onNode(hasScrollAction())
            .performScrollToNode(
                hasText(context.getString(
                    R.string.transaction_histories_doesnt_exists)))

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
        val calendar = timeService.getCalendar().apply {
            set(Calendar.MONTH, month)
            set(Calendar.YEAR, year)
        }
        val dateFormat = SimpleDateFormat("MMMM yyyy", timeService.getLocale())
        composeRule.onNodeWithText(
            dateFormat.format(calendar.time)
        ).assertExists()
    }

    fun assertProductName(name: String){
        composeRule.waitUntilExactlyOneExists(
            timeoutMillis = TestConstant.SMALL_TIMEOUT,
            matcher = hasTestTag(ProductDetailTestTag.productName)
                and hasText(name),
        )
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

    fun openChangePeriodDialog(){
        composeRule
            .onNodeWithContentDescription(context.getString(R.string.change_period_button))
            .performClick()
    }

    fun assertChangePeriodDialogOpened(){
        composeRule.waitUntilExactlyOneExists(
            timeoutMillis = TestConstant.SMALL_TIMEOUT,
            matcher = isDialog() and
                hasAnyDescendant(
                    hasText(context.getString(R.string.change_period_label)))
        )
    }

    fun assertRetryButtonDisplayed(){
        composeRule.waitUntilExactlyOneExists(
            timeoutMillis = TestConstant.SMALL_TIMEOUT,
            matcher = hasText(context.getString(R.string.retry_label)),
        )
    }

    fun waitUntilDataLoaded(){
        composeRule.waitUntilExactlyOneExists(
            hasText(context.getString(R.string.transaction_summary))
        )
    }

    fun openEditStockDialog(){
        composeRule.onNodeWithContentDescription(
            context.getString(R.string.edit_first_day_of_month_stock)
        ).performClick()
    }

    private fun getQuantityString(quantity: QuantityPerUnitType) : String {
        val cartonString = context.getString(UnitType.CARTON.getStringId())
        val pieceString = context.getString(UnitType.PIECE.getStringId())
        return "${quantity.cartonQuantity} $cartonString, ${quantity.pieceQuantity} $pieceString"
    }

    fun assertLoadingIndicatorDisplayed() {
        composeRule.waitUntilExactlyOneExists(
            hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))

        composeRule.onNodeWithText(
            context.getString(R.string.transaction_summary)
        ).assertDoesNotExist()
    }
}