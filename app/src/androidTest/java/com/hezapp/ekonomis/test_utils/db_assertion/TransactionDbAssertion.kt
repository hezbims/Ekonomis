package com.hezapp.ekonomis.test_utils.db_assertion

import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.utils.contains
import com.hezapp.ekonomis.test_utils.test_dao.ProductTestDao
import com.hezapp.ekonomis.test_utils.test_dao.ProfileTestDao
import com.hezapp.ekonomis.test_utils.test_dao.TransactionTestDao
import com.hezapp.ekonomis.test_utils.tryUntilSucceed
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.koin.core.context.GlobalContext
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

class TransactionDbAssertion(
    private val transactionTestDao: TransactionTestDao = GlobalContext.get().get(),
    private val profileTestDao : ProfileTestDao = GlobalContext.get().get(),
    private val productTestDao : ProductTestDao = GlobalContext.get().get(),
) {
    fun assertCountTransactionDetails(
        expected : TransactionDetailsAssertionDto,
        expectedCount : Int = 1,
    ) : Unit = tryUntilSucceed {
        val allInvoicesFromDb = transactionTestDao.getAll()
        val expectedDateInMillis = ZonedDateTime.of(
            expected.date.atStartOfDay(), ZoneId.of("UTC")
        ).toInstant().toEpochMilli()

        val expectedCriteriaMatchCount = allInvoicesFromDb.count {
            val currentTransactionProfileName = profileTestDao.getById(it.profileId).name

            if (
                it.date != expectedDateInMillis ||
                currentTransactionProfileName != expected.profileName ||
                it.transactionType != expected.transactionType ||
                it.ppn != expected.ppn
            )
                return@count false

            val currentTransactionItems = transactionTestDao.getItemsByInvoiceId(it.id)

            if (currentTransactionItems.count() != expected.items.count())
                return@count false

            return@count expected.items.all { expectedItem ->
                currentTransactionItems.contains { actualItem ->
                    runBlocking {
                        val actualItemProductName = productTestDao
                            .getById(actualItem.productId).name

                        actualItem.quantity == expectedItem.quantity &&
                                actualItem.unitType == expectedItem.unitType &&
                                actualItem.price == expectedItem.price &&
                                actualItemProductName == expectedItem.productName
                    }
                }
            }
        }

        assertThat(expectedCriteriaMatchCount, equalTo(expectedCount))
    }

    fun assertCountInvoices(expectedCount: Int) = tryUntilSucceed {
        assertThat(transactionTestDao.countInvoices(), equalTo(expectedCount))
    }

    fun assertCountInvoiceItems(expectedCount: Int) = tryUntilSucceed {
        assertThat(transactionTestDao.countInvoiceItems(), equalTo(expectedCount))
    }
}

data class TransactionDetailsAssertionDto(
    val date: LocalDate,
    val profileName: String,
    val transactionType: TransactionType,
    val ppn: Int?,
    val items: List<TransactionDetailsItemAssertionDto>,
)

data class TransactionDetailsItemAssertionDto(
    val productName: String,
    val quantity : Int,
    val unitType : UnitType,
    val price : Int,
)