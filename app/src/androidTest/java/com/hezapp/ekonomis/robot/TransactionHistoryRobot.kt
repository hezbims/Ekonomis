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
import com.hezapp.ekonomis.core.presentation.utils.toRupiahV2
import com.hezapp.ekonomis.test_data.TestTimeService
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
        val dateString = testCalendarProvider.toEddMMMyyyy(
            testCalendarProvider.getCalendar().apply {
                set(Calendar.YEAR, date.year)
                set(Calendar.MONTH, date.monthValue - 1)
                set(Calendar.DAY_OF_MONTH, date.dayOfMonth)
            }.timeInMillis
        )

        val targetSemanticMatcher = hasText(profileName) and
                hasText(totalPrice.toRupiahV2()) and
                hasText(dateString)

        composeRule.waitUntilExactlyOneExists(targetSemanticMatcher)
        composeRule.onNode(targetSemanticMatcher).performClick()
    }

    /**
     * @return bulan dan tahun dari filter setelah diubah dalam *timeInMillis*
     */
    fun openAndApplyFilter(
        targetMonth: Int,
        targetYear: Int,
        expectedFilterTimeInMillis: Long = TestTimeService.get().getCalendar().timeInMillis,
    ) : Long {
        composeRule.onNodeWithContentDescription(context.getString(R.string.open_filter_label))
            .performClick()
        composeRule.onNodeWithText(
            TestTimeService.get().toMMMyyyy(expectedFilterTimeInMillis))
            .assertExists()

        val currentCalendar = TestTimeService.get().getCalendar().apply {
            timeInMillis = expectedFilterTimeInMillis
        }
        val currentYear = { currentCalendar.get(Calendar.YEAR)  }
        val currentMonth = { currentCalendar.get(Calendar.MONTH) + 1 }
        var totalBackwardStep = 0
        var totalForwardStep = 0

        while (currentYear() < targetYear ||
            currentYear() == targetYear &&
            currentMonth() < targetMonth
        ) {
            composeRule.onNodeWithContentDescription(
                context.getString(
                    R.string.increment_month_and_year_label
                )
            ).performClick()

            currentCalendar.add(Calendar.MONTH, 1)
            totalForwardStep++
        }

        while (currentYear() > targetYear ||
            currentYear() == targetYear &&
            currentMonth() > targetMonth
        ) {
            composeRule.onNodeWithContentDescription(
                context.getString(
                    R.string.decrement_month_and_year_label
                )
            ).performClick()

            currentCalendar.add(Calendar.MONTH, -1)
            totalBackwardStep--
        }

        composeRule.onNodeWithText(context.getString(R.string.apply_label))
            .performClick()

        composeRule.onNodeWithText(TestTimeService.get().toMMMMyyyy(
            currentCalendar.timeInMillis
        )).assertExists()

        return currentCalendar.timeInMillis
    }
}