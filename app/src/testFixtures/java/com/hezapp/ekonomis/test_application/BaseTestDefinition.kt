package com.hezapp.ekonomis.test_application

import android.content.Context
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.hezapp.ekonomis.test_utils.TestDataUtils
import com.hezapp.ekonomis.test_utils.ITestDataUtils
import com.hezapp.ekonomis.test_utils.ITestUiUtils
import com.hezapp.ekonomis.test_utils.TestUiUtils

abstract class BaseTestDefinition(
    composeRule: ComposeTestRule,
    context: Context,
) : ITestUiUtils by TestUiUtils(composeRule, context),
    ITestDataUtils by TestDataUtils()