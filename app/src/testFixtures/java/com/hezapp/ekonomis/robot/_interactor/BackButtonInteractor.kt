package com.hezapp.ekonomis.robot._interactor

import android.content.Context
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.custom_matcher.hasRole

class BackButtonInteractor(
    composeRule: ComposeTestRule,
    context: Context,
) : ComponentInteractor(
        composeRule,
        hasContentDescription(
            context.getString(R.string.back_icon_content_description))
        and hasRole(Role.Button)
    )