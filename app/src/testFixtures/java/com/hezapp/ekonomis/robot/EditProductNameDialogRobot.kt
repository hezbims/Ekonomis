package com.hezapp.ekonomis.robot

import android.content.Context
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.hezapp.ekonomis.R

/**
 * Robot for [EditProductNameDialog][com.hezapp.ekonomis.edit_product_name_dialog.presentation.EditProductNameDialog]
 */
class EditProductNameDialogRobot(
    private val composeRule: ComposeTestRule,
    private val context: Context,
) {
    fun enterName(name: String) {
        composeRule
            .onNodeWithText(context.getString(R.string.product_name_label))
            .performTextInput(name)
    }

    fun clickSave() {
        composeRule
            .onNodeWithText(context.getString(R.string.save_label))
            .performClick()
        composeRule.waitForIdle()
    }

    @OptIn(ExperimentalTestApi::class)
    fun assertErrorMessageDisplayed(errorMessage: String) {
        composeRule.waitUntilExactlyOneExists(
            matcher = hasText(errorMessage),
            timeoutMillis = 2_500,
        )
    }
}