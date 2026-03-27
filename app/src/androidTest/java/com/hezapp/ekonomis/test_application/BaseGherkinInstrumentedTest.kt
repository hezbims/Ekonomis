package com.hezapp.ekonomis.test_application

import android.content.Context
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.hezapp.ekonomis.MainActivity
import com.hezapp.ekonomis.test_utils.rule.ComposeTreeLoggerRule
import com.hezapp.ekonomis.test_utils.rule.GlobalTimeConfigRule
import com.hezapp.ekonomis.test_utils.rule.TestEnvironmentResetRule
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext

@RunWith(AndroidJUnit4::class)
abstract class BaseGherkinInstrumentedTest<T : BaseTestDefinition>(
    createTestDefinition: (composeTestRule: ComposeTestRule, context: Context) -> T,
    private val immediatelyLaunchMainActivity: Boolean = false,
) {

    val testDefinition : T by lazy {
        createTestDefinition(
            composeRule,
            InstrumentationRegistry.getInstrumentation().targetContext)
    }

    val given : T
        get() = testDefinition
    val `when` : T
        get() = testDefinition
    val then : T
        get() = testDefinition
    val and : T
        get() = testDefinition

    @get:Rule(order = 1)
    val timeConfigSetup = GlobalTimeConfigRule()
    @get:Rule(order = 2)
    val composeRule = createEmptyComposeRule()

    @get:Rule(order = 3)
    val logErrorRule = ComposeTreeLoggerRule(composeRule)

    @get:Rule(order = 4)
    val dataCleanerRule = TestEnvironmentResetRule { GlobalContext.get() }

    @Before
    fun reset(){
        if (immediatelyLaunchMainActivity)
            ActivityScenario.launch(MainActivity::class.java)
    }
}