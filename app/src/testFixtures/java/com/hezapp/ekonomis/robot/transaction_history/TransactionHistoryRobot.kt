package com.hezapp.ekonomis.robot.transaction_history

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.presentation.utils.toRupiahV2
import com.hezapp.ekonomis.test_utils.TestTimeService
import com.hezapp.ekonomis.test_utils.testCalendarProvider
import java.time.LocalDate
import java.util.Calendar

class TransactionHistoryRobot(
    private val composeRule: ComposeTestRule,
    private val context: Context,
) {
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

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalTestApi::class)
    fun waitAndClickTransactionCard(
        profileName: String,
        totalPrice: Int? = null,
        date: LocalDate? = null
    ){
        val dateString = date?.let {
            testCalendarProvider.toEddMMMyyyy(
                it.atStartOfDay(testCalendarProvider.getZoneId())
                    .toInstant()
                    .toEpochMilli()
            )
        }

        var targetSemanticMatcher = hasText(profileName)
        totalPrice?.let {
            targetSemanticMatcher = targetSemanticMatcher and hasText(it.toRupiahV2())
        }
        dateString?.let {
            targetSemanticMatcher = targetSemanticMatcher and hasText(it)
        }

        composeRule.waitUntilExactlyOneExists(targetSemanticMatcher)
        composeRule.onNode(targetSemanticMatcher).performClick()
    }

    /**
     * @return bulan dan tahun dari filter setelah diubah dalam *timeInMillis*
     */
    fun openAndApplyFilter(
        targetMonth: Int,
        targetYear: Int,
        expectedFilterTimeInMillis: Long = TestTimeService.Companion.get().getCalendar().timeInMillis,
    ) : Long {
        composeRule.onNodeWithContentDescription(context.getString(R.string.open_filter_label))
            .performClick()
        composeRule.onNodeWithText(
            TestTimeService.Companion.get().toMMMyyyy(expectedFilterTimeInMillis))
            .assertExists()

        val currentCalendar = TestTimeService.Companion.get().getCalendar().apply {
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

        composeRule.onNodeWithText(
            TestTimeService.Companion.get().toMMMMyyyy(
            currentCalendar.timeInMillis
        )).assertExists()

        return currentCalendar.timeInMillis
    }

    @OptIn(ExperimentalTestApi::class)
    fun assertTransactionCardNotExist(
        profileName: String,
    ) {
        composeRule.waitUntilDoesNotExist(hasText(profileName))
    }
}