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
abstract class BaseEkonomisUiUnitTest : IGherkinSyntax {
    //region JUnit Rule
    private var _koinApp: KoinApplication? = null
    protected val koinApp: KoinApplication
        get() = _koinApp!!
    protected val koin: Koin
        get() = _koinApp!!.koin
    protected val appContext : Context = ApplicationProvider.getApplicationContext()

    @get:Rule(order = 0)
    val koinAppRule = object : TestWatcher(){
        override fun starting(description: Description?) {
            ShadowLog.stream = System.out
            _koinApp = koinApplication {
                allowOverride(true)
                androidContext(appContext)
                modules(MainApplication.koinModules)
            }.apply {
                loadTestKoinModules(appContext = appContext, koin = koin, useInMemoryDb = true)
            }
            super.starting(description)
        }

        override fun finished(description: Description?) {
            super.finished(description)
            _koinApp?.close()
        }
    }

    @get:Rule(1)
    val globalTimeConfigRule = GlobalTimeConfigRule()

    @get:Rule(2)
    val testEnvironementResetRule = TestEnvironmentResetRule { koin }

    @get:Rule(3)
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @get:Rule(4)
    val composeTreeLogger = ComposeTreeLoggerRule(composeRule)
    //endregion

    val uiUtils by lazy { TestUiUtils(
        composeRule = composeRule,
        context = ApplicationProvider.getApplicationContext(),
    ) }

    val dataUtils by lazy {
        TestDataUtils(koin = koin)
    }
}