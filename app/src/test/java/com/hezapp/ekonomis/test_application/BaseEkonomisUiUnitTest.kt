package com.hezapp.ekonomis.test_application

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hezapp.ekonomis.MainApplication
import com.hezapp.ekonomis.test_utils.TestDataUtils
import com.hezapp.ekonomis.test_utils.TestUiUtils
import com.hezapp.ekonomis.test_utils.gherkin.IGherkinSyntax
import com.hezapp.ekonomis.test_utils.rule.ComposeTreeLoggerRule
import com.hezapp.ekonomis.test_utils.rule.GlobalTimeConfigRule
import com.hezapp.ekonomis.test_utils.rule.TestEnvironmentResetRule
import com.hezapp.ekonomis.test_utils.rule.TestRepeatRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication
import org.robolectric.shadows.ShadowLog


@RunWith(AndroidJUnit4::class)
abstract class BaseEkonomisUiUnitTest(
    loadDefaultKoinModules : Boolean = true,
    useConfinedTestDispatcher : Boolean = true,
) : IGherkinSyntax {

    private var _koinApp: KoinApplication? = null
    protected val koinApp: KoinApplication
        get() = _koinApp!!
    protected val koin: Koin
        get() = _koinApp!!.koin
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
    val koinAppRule = object : TestWatcher(){
        override fun starting(description: Description?) {
            _koinApp = koinApplication {
                allowOverride(true)
                androidContext(appContext)
                if (loadDefaultKoinModules)
                    modules(MainApplication.koinModules)
            }.apply {
                if (loadDefaultKoinModules)
                    loadTestKoinModules(appContext = appContext, koin = koin, useInMemoryDb = true)
            }
            super.starting(description)
        }

        override fun finished(description: Description?) {
            super.finished(description)
            _koinApp?.close()
        }
    }

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
        context = ApplicationProvider.getApplicationContext(),
    ) }
}