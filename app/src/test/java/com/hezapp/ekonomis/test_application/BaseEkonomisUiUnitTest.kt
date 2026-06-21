package com.hezapp.ekonomis.test_application

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hezapp.ekonomis.test_utils.ITestDataUtils
import com.hezapp.ekonomis.test_utils.TestDataUtils
import com.hezapp.ekonomis.test_utils.TestUiUtils
import com.hezapp.ekonomis.test_utils.gherkin.IGherkinSyntax
import com.hezapp.ekonomis.test_utils.rule.ComposeTreeLoggerRule
import com.hezapp.ekonomis.test_utils.rule.GlobalTimeConfigRule
import com.hezapp.ekonomis.test_utils.rule.KoinRule
import com.hezapp.ekonomis.test_utils.rule.TestEnvironmentResetRule
import com.hezapp.ekonomis.test_utils.rule.TestRepeatRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import org.junit.runner.RunWith
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication
import org.robolectric.shadows.ShadowLog

/// Using private constructor so [koinApp] won't be able to passed from test cases
@RunWith(AndroidJUnit4::class)
abstract class BaseEkonomisUiUnitTest private constructor(
    loadDefaultKoinModules : Boolean,
    useConfinedTestDispatcher : Boolean,
    protected val koinApp : KoinApplication,
) : IGherkinSyntax,
    ITestDataUtils by TestDataUtils(koinApp.koin)
{

    constructor(
        loadDefaultKoinModules : Boolean = true,
        useConfinedTestDispatcher : Boolean = true,
    ) : this(
        loadDefaultKoinModules = loadDefaultKoinModules,
        useConfinedTestDispatcher = useConfinedTestDispatcher,
        koinApp = koinApplication()
    )

    protected val koin: Koin get() = koinApp.koin
    protected val appContext : Context = ApplicationProvider.getApplicationContext()

    init {
        if (useConfinedTestDispatcher) {
            @OptIn(ExperimentalCoroutinesApi::class)
            Dispatchers.setMain(UnconfinedTestDispatcher())
        }

        ShadowLog.stream = System.out
    }

    @get:Rule(0)
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    // for debugging flaky test use large times
    @get:Rule(1)
    val testRepeatRule = TestRepeatRule(times = 1)

    @get:Rule(order = 2)
    val koinAppRule = KoinRule(
        appContext = appContext,
        koinApp = koinApp,
        options = KoinRule.KoinOptions(
            loadDefaultKoinModules = loadDefaultKoinModules
        )
    )

    @get:Rule(3)
    val globalTimeConfigRule = GlobalTimeConfigRule()

    @get:Rule(4)
    val testEnvironementResetRule = TestEnvironmentResetRule { koin }

    val dataUtils by lazy {
        TestDataUtils(koin = koin)
    }

    @get:Rule(5)
    val composeTreeLogger = ComposeTreeLoggerRule(composeRule)

    val uiUtils by lazy { TestUiUtils(
        composeRule = composeRule,
        context = appContext,
        koin = koin,
    ) }
}