package com.hezapp.ekonomis.app_test_utils.add_or_update_transaction.presentation.robot.component

import android.content.Context
import androidx.compose.ui.semantics.SemanticsActions
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