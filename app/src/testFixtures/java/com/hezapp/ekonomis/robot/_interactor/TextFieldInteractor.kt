package com.hezapp.ekonomis.robot._interactor

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.test.espresso.Espresso

open class TextFieldInteractor(
    composeRule: ComposeTestRule,
    matcher: SemanticsMatcher,
) : ComponentInteractor(composeRule, matcher) {
    constructor(
        composeRule: ComposeTestRule,
        label: String,
    ) : this(composeRule, hasText(label))

    fun inputText(text: String, replaceText: Boolean = false){
        val node = composeRule.onNode(matcher)
        if (replaceText)
            node.performTextReplacement(text)
        else
            node.performTextInput(text)
        Espresso.closeSoftKeyboard()
    }
}