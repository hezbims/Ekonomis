package com.hezapp.ekonomis.test_application

import android.content.Context
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.hezapp.ekonomis.test_utils.ITestUtils
import com.hezapp.ekonomis.test_utils.TestUtils

abstract class BaseTestDefinition(
    composeRule: ComposeTestRule,
    context: Context,
) : ITestUtils by TestUtils(composeRule, context)