package com.hezapp.ekonomis.robot._interactor

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput

open class TextFieldInteractor(
    composeRule: ComposeTestRule,
    matcher: SemanticsMatcher,
) : ComponentInteractor(composeRule, matcher) {
    constructor(
        composeRule: ComposeTestRule,
        label: String,
    ) : this(composeRule, hasText(label))

    fun inputText(text: String, fresh: Boolean = false){
        if (fresh)
            composeRule.onNode(matcher).performTextClearance()
        composeRule.onNode(matcher).performTextInput(text)
    }
}