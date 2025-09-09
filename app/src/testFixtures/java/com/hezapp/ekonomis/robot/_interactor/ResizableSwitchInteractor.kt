package com.hezapp.ekonomis.robot._interactor

import android.content.Context
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.hezapp.ekonomis.R

class ResizableSwitchInteractor(
    composeRule: ComposeTestRule,
    matcher: SemanticsMatcher,
    private val context: Context,
) : ComponentInteractor(composeRule, matcher) {
    fun assertIsOn(){
        composeRule.onNode(matcher).assert(SemanticsMatcher.expectValue(
            SemanticsProperties.StateDescription,
            context.getString(R.string.on_label)
        ))
    }

    fun assertIsOff(){
        composeRule.onNode(matcher).assert(SemanticsMatcher.expectValue(
            SemanticsProperties.StateDescription,
            context.getString(R.string.off_label)
        ))
    }

}