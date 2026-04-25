package com.hezapp.ekonomis.test_application

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import com.hezapp.ekonomis.test_utils.TestUiUtils
import com.hezapp.ekonomis.test_utils.rule.ComposeTreeLoggerRule
import org.junit.Rule

abstract class BaseEkonomisUiUnitTest : BaseDataUnitTest() {
    @get:Rule(3)
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @get:Rule(4)
    val composeTreeLogger = ComposeTreeLoggerRule(composeRule)

    val uiUtils by lazy { TestUiUtils(
        composeRule = composeRule,
        context = ApplicationProvider.getApplicationContext(),
    ) }
}