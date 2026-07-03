package com.hezapp.ekonomis.base_test_utils.rule

import android.os.Build
import com.hezapp.ekonomis.core.data.database.EkonomisDatabase
import com.hezapp.ekonomis.app_test_utils.core.application.mock.TestTimeService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.rules.ExternalResource
import org.koin.core.Koin

/**
 * Koin harus berjalan terlebih dahulu
 */
class TestEnvironmentResetRule(
    private val getKoin: () -> Koin,
) : ExternalResource() {
    override fun before() = runBlocking {
        withContext(Dispatchers.IO) {
            getKoin().getOrNull<EkonomisDatabase>()?.clearAllTables()
        }
        TestTimeService.reset()
    }

    override fun after() : Unit = runBlocking {
        if (isRobolectricTest())
            withContext(Dispatchers.IO){
                getKoin().getOrNull<EkonomisDatabase>()?.close()
            }
    }

    private fun isRobolectricTest() : Boolean {
        return Build.FINGERPRINT.contains("robolectric", ignoreCase = true)
    }
}