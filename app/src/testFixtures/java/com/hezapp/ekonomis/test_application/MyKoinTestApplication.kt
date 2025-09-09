package com.hezapp.ekonomis.test_application

import android.app.Application
import androidx.test.platform.app.InstrumentationRegistry
import com.hezapp.ekonomis.MainApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

class MyKoinTestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (GlobalContext.getOrNull() == null) {
            val appContext =
                InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
            startKoin {
                allowOverride(true)
                androidContext(appContext)
                modules(MainApplication.Companion.koinModules)
            }
            loadTestKoinModules(appContext)
        }
    }
}