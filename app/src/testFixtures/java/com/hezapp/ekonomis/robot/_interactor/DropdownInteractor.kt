package com.hezapp.ekonomis.robot._interactor

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isPopup
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick

open class DropdownInteractor(
    composeRule: ComposeTestRule,
    matcher: SemanticsMatcher
) : ComponentInteractor(composeRule, matcher) {
    fun openAndSelectValue(value: String) : Unit =
        openAndSelectValue(
            hasText(value) and
                    hasAnyAncestor(isPopup()))

    private fun openAndSelectValue(valueMatcher: SemanticsMatcher){
        composeRule.onNode(matcher).performClick()
        composeRule.onNode(valueMatcher).performClick()
    }
}