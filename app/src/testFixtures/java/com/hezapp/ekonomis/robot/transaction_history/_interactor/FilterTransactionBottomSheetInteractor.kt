package com.hezapp.ekonomis.robot.transaction_history._interactor

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasAnySibling
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.robot._interactor.ComponentInteractor
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class FilterTransactionBottomSheetInteractor(
    composeRule: ComposeTestRule,
    matcher: SemanticsMatcher,
    private val context: Context,
) : ComponentInteractor(composeRule, matcher) {
    @OptIn(ExperimentalTestApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    fun applyFilter(targetPeriod: YearMonth?, isOnlyNotPaidOff: Boolean?){
        applyPeriodFilter(targetPeriod)

        applyPaidOffFilter(isOnlyNotPaidOff)

        composeRule.onNode(
            hasAnyAncestor(matcher) and
            hasText(context.getString(R.string.apply_label)))
    }

    private fun applyPaidOffFilter(isOnlyNotPaidOff: Boolean?){
        if (isOnlyNotPaidOff == null)
            return

        val isCurrentCheckboxOnlyFilterNotPaidOff = composeRule.onNodeWithText(
            context.getString(R.string.only_not_paid_off_label))
            .fetchSemanticsNode()
            .config[SemanticsProperties.ToggleableState].let {
                it == ToggleableState.On
        }

        if (isCurrentCheckboxOnlyFilterNotPaidOff == isOnlyNotPaidOff)
            return

        composeRule.onNodeWithText(
            context.getString(R.string.only_not_paid_off_label))
            .performClick()
    }

    @OptIn(ExperimentalTestApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    private fun applyPeriodFilter(targetPeriod: YearMonth?){
        if (targetPeriod == null)
            return
        val periodFormat = DateTimeFormatter.ofPattern("MMM yyyy")
        var currentPeriod = composeRule.onNode(
            hasAnyAncestor(matcher) and
                    hasAnySibling(hasContentDescription(context.getString(R.string.decrement_month_and_year_label))) and
                    hasAnySibling(hasContentDescription(context.getString(R.string.increment_month_and_year_label))) and
                    hasText("", substring = true)
        )
            .fetchSemanticsNode()
            .config[SemanticsProperties.Text]
            .single()
            .toString().let {
                YearMonth.parse(it, periodFormat)
            }

        // this two variables is only for debugging
        var totalBackwardStep = 0
        var totalForwardStep = 0

        while (currentPeriod < targetPeriod) {
            composeRule.onNodeWithContentDescription(
                context.getString(
                    R.string.increment_month_and_year_label
                )
            ).performClick()

            currentPeriod = currentPeriod.plusMonths(1)
            totalForwardStep++
        }

        while (currentPeriod > targetPeriod) {
            composeRule.onNodeWithContentDescription(
                context.getString(
                    R.string.decrement_month_and_year_label
                )
            ).performClick()

            currentPeriod = currentPeriod.minusMonths(1)
            totalBackwardStep--
        }

        composeRule.waitUntilExactlyOneExists(
            hasAnyAncestor(matcher) and
                    hasText(currentPeriod.format(periodFormat)))
    }

}