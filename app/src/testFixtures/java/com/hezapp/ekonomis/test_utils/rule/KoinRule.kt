package com.hezapp.ekonomis.test_utils.rule

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.hezapp.ekonomis.MainApplication
import com.hezapp.ekonomis.test_application.loadTestKoinModules
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication

class KoinRule(
    private val appContext: Context,
    private val onKoinApplicationCreated: (KoinApplication) -> Unit,
    private val options: KoinOptions,
) : TestWatcher() {
    private lateinit var koinApp : KoinApplication

    @RequiresApi(Build.VERSION_CODES.O)
    override fun starting(description: Description?) {
        koinApp = koinApplication {
            allowOverride(true)
            androidContext(appContext)
            if (options.loadDefaultKoinModules)
                modules(MainApplication.koinModules)
        }.apply {
            if (options.loadDefaultKoinModules)
                loadTestKoinModules(
                    appContext = appContext,
                    koin = koin,
                    useInMemoryDb = options.useInMemoryDb)
        }
        onKoinApplicationCreated(koinApp)
        super.starting(description)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        koinApp.close()
    }

    data class KoinOptions(
        val loadDefaultKoinModules: Boolean = true,
        val useInMemoryDb: Boolean = true,
    )
}