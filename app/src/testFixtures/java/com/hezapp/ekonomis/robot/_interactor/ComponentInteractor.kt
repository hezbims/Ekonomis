package com.hezapp.ekonomis.robot._interactor

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick

open class ComponentInteractor (
    val composeRule: ComposeTestRule,
    val matcher: SemanticsMatcher,
) {
    constructor(composeRule: ComposeTestRule, label: String) : this(composeRule, hasText(label))

    fun click(){
        composeRule.onNode(matcher).performClick()
    }

    fun assertExist(){
        composeRule.onNode(matcher).assertExists()
    }

    fun assertDoesNotExist(){
        composeRule.onNode(matcher).assertDoesNotExist()
    }

    fun assertHasText(expectedText: String){
        composeRule.onNode(matcher).assertTextContains(expectedText)
    }
}