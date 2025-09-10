package com.hezapp.ekonomis.robot.transaction_form._interactor

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import com.hezapp.ekonomis.robot._interactor.ComponentInteractor
import com.hezapp.ekonomis.test_utils.TestTimeService
import java.time.LocalDate

class DateFieldInteractor(
    matcher: SemanticsMatcher,
    composeRule: ComposeTestRule,
    private val context: Context,
) : ComponentInteractor(composeRule = composeRule, matcher = matcher) {
    private val calendarPopup = CalendarPopupInteractor(
        composeRule = composeRule,
        context = context
    )

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalTestApi::class)
    fun chooseDate(date: LocalDate){
        composeRule.onNode(matcher).performClick()
        calendarPopup.waitUntilAppear()
        calendarPopup.changeDate(date)
        calendarPopup.confirmDateSelection()
    }

    fun assertContent(expectedDate: LocalDate){
        composeRule.onNode(matcher)
            .assertTextContains(TestTimeService.get().toEddMMMyyyy(expectedDate))
    }
}