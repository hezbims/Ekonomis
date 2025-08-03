package com.hezapp.ekonomis.robot.transaction_form._interactor

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.hezapp.ekonomis.test_data.TestTimeService
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarPopupInteractor(
    val composeRule : ComposeTestRule,
    private val confirmLabel : String,
) {
    @OptIn(ExperimentalTestApi::class)
    fun changeYear(year: Int){
        composeRule.apply {
            onNodeWithContentDescription(
                label = "Switch to selecting a year",
                substring = true
            ).performClick()

            waitUntilAtLeastOneExists(hasText("Navigate to year", substring = true))

            onNode(hasText("Navigate to year $year")).performClick()
        }
    }

    fun changeMonth(targetMonth: Int, expectedCurrentMonth: Int){
        var currentMonth = expectedCurrentMonth
        while (currentMonth > targetMonth){
            composeRule.onNodeWithContentDescription("Change to previous month")
                .performClick()
            currentMonth--
        }
        while(currentMonth < targetMonth){
            composeRule.onNodeWithContentDescription("Change to next month")
                .performClick()
            currentMonth++
        }
    }

    fun changeDayOfMonth(dayOfMonth: Int, month: Int, year: Int){
        val formatter = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.ENGLISH).apply {
            timeZone = TestTimeService.get().getTimezone()
        }
        val targetCalendar = Calendar.getInstance(TestTimeService.get().getTimezone()).apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }
        val targetCalendarString = formatter.format(targetCalendar.time)
        composeRule.onNodeWithText(targetCalendarString).performClick()
    }

    fun confirmDateSelection(){
        composeRule.onNodeWithText(confirmLabel, ignoreCase = true)
            .performClick()
    }
}