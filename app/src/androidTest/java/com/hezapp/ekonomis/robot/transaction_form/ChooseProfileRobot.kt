package com.hezapp.ekonomis.robot.transaction_form

import android.content.Context
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isFocusable
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performFirstLinkClick
import androidx.compose.ui.test.performTextInput
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.TestConstant
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.presentation.utils.getProfileStringId

class ChooseProfileRobot(
    private val composeRule: ComposeTestRule,
    private val context: Context,
){
    @OptIn(ExperimentalTestApi::class)
    fun registerNewProfile(profileName: String){
        composeRule.apply {
            waitUntilExactlyOneExists(
                hasText(
                context.getString(R.string.create_new_here_label),
                substring = true
            ), timeoutMillis = TestConstant.MEDIUM_TIMEOUT)

            onNodeWithText(
                context.getString(R.string.create_new_here_label),
                substring = true).performFirstLinkClick()

            onNode(
                (
                    hasText(
                        context.getString(
                            R.string.profile_name_label,
                            context.getString(TransactionType.PENJUALAN.getProfileStringId())
                        ),
                        ignoreCase = true
                    )
                    or hasText(
                        context.getString(
                            R.string.profile_name_label,
                            context.getString(TransactionType.PEMBELIAN.getProfileStringId())
                        ),
                        ignoreCase = true
                    )
                )
                and isFocusable()
            ).performTextInput(profileName)

            onNodeWithText(
                context.getString(R.string.save_profile_label))
                .performClick()
        }
    }

    @OptIn(ExperimentalTestApi::class)
    fun chooseProfile(profileName: String){
        composeRule.apply {
            waitUntilAtLeastOneExists(hasText(profileName), timeoutMillis = TestConstant.MEDIUM_TIMEOUT)

            onNodeWithText(profileName).performClick()
        }
    }
}