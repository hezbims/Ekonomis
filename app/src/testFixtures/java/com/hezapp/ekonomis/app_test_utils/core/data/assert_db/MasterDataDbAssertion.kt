package com.hezapp.ekonomis.app_test_utils.core.data.assert_db

import com.hezapp.ekonomis._testing_only.test_dao.ProductTestDao
import com.hezapp.ekonomis._testing_only.test_dao.ProfileTestDao
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.koin.core.Koin
import org.koin.core.context.GlobalContext

class MasterDataDbAssertion(
    koin: Koin = GlobalContext.get(),
) {
    val profileTestDao: ProfileTestDao = koin.get()
    val productTestDao: ProductTestDao = koin.get()
    fun assertCount(
        expectedProductCount: Int,
        expectedProfileCount: Int,
    ) = runBlocking {
        assertThat(expectedProductCount, equalTo(productTestDao.count()))
        assertThat(expectedProfileCount, equalTo(profileTestDao.count()))
    }
}