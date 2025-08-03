package com.hezapp.ekonomis.robot

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.hezapp.ekonomis.MainActivity
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.presentation.utils.toRupiah
import com.hezapp.ekonomis.test_data.testCalendarProvider
import java.time.LocalDate
import java.util.Calendar

class TransactionHistoryRobot(
    private val composeRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>
) {
    private val context by lazy { composeRule.activity }
   fun navigateToProductPreview(){
       composeRule.onNodeWithText(
            context.getString(R.string.product_stock_label),
       ).performClick()
   }

    fun navigateToAddNewTransaction(){
        composeRule.onNodeWithContentDescription(
            context.getString(R.string.add_new_transaction_content_description)
        ).performClick()
    }

    @OptIn(ExperimentalTestApi::class)
    fun clickTransactionCard(
        profileName: String,
        totalPrice: Int,
        date: LocalDate
    ){
        val targetSemanticMatcher = hasText(profileName) and
                hasText(totalPrice.toRupiah()) and
                hasText(testCalendarProvider.toEddMMMyyyy(testCalendarProvider.getCalendar().apply {
                    set(Calendar.YEAR, date.year)
                    set(Calendar.MONTH, date.monthValue - 1)
                    set(Calendar.DAY_OF_MONTH, date.dayOfMonth)
                }.timeInMillis))

        composeRule.waitUntilExactlyOneExists(targetSemanticMatcher)
        composeRule.onNode(targetSemanticMatcher).performClick()
    }

    fun openAndApplyFilter(month: Int, year: Int){
        composeRule.onNodeWithContentDescription(context.getString(R.string.open_filter_label))
            .performClick()
    }
}