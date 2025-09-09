package com.hezapp.ekonomis.test_utils.rule

import com.hezapp.ekonomis.core.data.database.EkonomisDatabase
import com.hezapp.ekonomis.test_utils.TestTimeService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.rules.ExternalResource
import org.koin.core.Koin

/**
 * Koin harus berjalan terlebih dahulu
 */
class TestDataCleanerRule(
    private val getKoin: () -> Koin,
) : ExternalResource() {
    override fun before() = runBlocking {
        withContext(Dispatchers.IO) {
            getKoin().get<EkonomisDatabase>().clearAllTables()
        }
        TestTimeService.Companion.reset()
    }
}