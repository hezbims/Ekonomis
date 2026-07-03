package com.hezapp.ekonomis.base_test_utils.test_application

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.hezapp.ekonomis.utils_aggregate.ITestDataUtils
import com.hezapp.ekonomis.utils_aggregate.ITestUiUtils
import com.hezapp.ekonomis.utils_aggregate.TestDataUtils
import com.hezapp.ekonomis.utils_aggregate.TestUiUtils

@RequiresApi(Build.VERSION_CODES.O)
abstract class BaseTestDefinition(
    composeRule: ComposeTestRule,
    context: Context,
) : ITestUiUtils by TestUiUtils(composeRule, context),
    ITestDataUtils by TestDataUtils()