package com.hezapp.ekonomis.robot.transaction_history._interactor

import android.content.Context
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.hezapp.ekonomis.robot._interactor.ComponentInteractor
import com.hezapp.ekonomis.R

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