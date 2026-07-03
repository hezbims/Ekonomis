package com.hezapp.ekonomis.base_test_utils.robot.component

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performFirstLinkClick

class AnnotatedTextInteractor(
    composeRule: ComposeTestRule,
    matcher: SemanticsMatcher,
) : ComponentInteractor(composeRule, matcher) {
    fun clickLink() {
        composeRule.onNode(matcher).performFirstLinkClick()
    }
}