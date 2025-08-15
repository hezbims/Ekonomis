package com.hezapp.ekonomis.feature.db_migration

import android.content.Context
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.hezapp.ekonomis.core.data.database.EkonomisDatabase
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
abstract class BaseDbMigrationTest {
    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        instrumentation = InstrumentationRegistry.getInstrumentation(),
        databaseClass = EkonomisDatabase::class.java,
        specs = listOf(

        ),
        openFactory = FrameworkSQLiteOpenHelperFactory()
    )

    @Before
    fun cleanupBeforeEachTest(){
        cleanup()
    }

    private fun cleanup(){
        ApplicationProvider.getApplicationContext<Context>()
            .deleteDatabase(EkonomisDatabase.DB_NAME)
    }


}