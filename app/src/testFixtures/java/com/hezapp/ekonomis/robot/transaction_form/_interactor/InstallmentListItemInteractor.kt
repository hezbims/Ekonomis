package com.hezapp.ekonomis.robot.transaction_form._interactor

import android.content.Context
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import com.hezapp.ekonomis.R

class InstallmentListItemInteractor(
    private val index: Int,
    private val composeRule: ComposeTestRule,
    private val context: Context,
) {
    fun clickEditIcon(){
        composeRule.onAllNodes(
            hasContentDescription(
                context.getString(R.string.edit_installment_item)
            )
        )[index].performSemanticsAction(SemanticsActions.OnClick)
    }

    @OptIn(ExperimentalTestApi::class)
    fun performDelete(){
        composeRule.onAllNodes(
            hasContentDescription(
                context.getString(R.string.delete_installment_item))
        )[index].performSemanticsAction(SemanticsActions.OnClick)

        composeRule.onNode(
            hasText(context.getString(R.string.yes_label))
        ).performClick()
    }
}