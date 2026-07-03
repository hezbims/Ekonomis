package com.hezapp.ekonomis.app_test_utils.transaction_history.presentation.robot.component

import android.content.Context
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.base_test_utils.robot.component.ComponentInteractor

class TransactionPreviewItemInteractor(
    matcher: SemanticsMatcher,
    private val context: Context,
    composeRule: ComposeTestRule,
) : ComponentInteractor(composeRule, matcher){
    fun assertIsPaidOff(){
        composeRule.onNode(
            hasContentDescription(
                context.getString(R.string.paid_off_label)) and
            matcher
        ).assertExists()
    }

    fun assertIsNotPaidOff(){
        composeRule.onNode(hasContentDescription(
            context.getString(R.string.not_paid_off_label)) and
            matcher
        ).assertExists()
    }

    fun assertTotalProductPrice(price: String) {
        composeRule.onNode(hasText(price) and matcher)
            .assertExists()
    }
}