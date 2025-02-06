package com.hezapp.ekonomis.test_application

import android.app.Application
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.hezapp.ekonomis.MainApplication
import com.hezapp.ekonomis.core.data.database.EkonomisDatabase
import com.hezapp.ekonomis.core.domain.utils.CalendarProvider
import com.hezapp.ekonomis.test_data.TestCalendarProvider
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MyKoinTestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        startKoin {
            allowOverride(true)
            androidContext(appContext)
            modules(MainApplication.koinModules)
        }
        loadKoinModules(
            module = module {
                single<EkonomisDatabase> {
                    Room.inMemoryDatabaseBuilder(
                        appContext,
                        EkonomisDatabase::class.java,
                    ).build()
                }
                single<CalendarProvider> {
                    TestCalendarProvider()
                }
            }
        )
    }
}