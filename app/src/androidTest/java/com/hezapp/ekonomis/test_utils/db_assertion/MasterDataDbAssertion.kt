package com.hezapp.ekonomis.test_utils.db_assertion

import com.hezapp.ekonomis.test_utils.test_dao.ProductTestDao
import com.hezapp.ekonomis.test_utils.test_dao.ProfileTestDao
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.koin.core.context.GlobalContext

class MasterDataDbAssertion(
    val profileTestDao: ProfileTestDao = GlobalContext.get().get(),
    val productTestDao: ProductTestDao = GlobalContext.get().get(),
) {
    fun assertCount(
        expectedProductCount: Int,
        expectedProfileCount: Int,
    ) = runBlocking {
        assertThat(expectedProductCount, equalTo(productTestDao.count()))
        assertThat(expectedProfileCount, equalTo(profileTestDao.count()))
    }
}