package com.hezapp.ekonomis.test_utils.db_assertion

import android.os.Build
import androidx.annotation.RequiresApi
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.dto.PaymentTypeAssertionDto
import com.hezapp.ekonomis._testing_only.test_dao.InstallmentItemTestDao
import com.hezapp.ekonomis._testing_only.test_dao.InstallmentTestDao
import com.hezapp.ekonomis._testing_only.test_dao.ProductTestDao
import com.hezapp.ekonomis._testing_only.test_dao.ProfileTestDao
import com.hezapp.ekonomis._testing_only.test_dao.TransactionTestDao
import com.hezapp.ekonomis.core.domain.utils.contains
import com.hezapp.ekonomis.test_utils.tryUntilSucceed
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

class TransactionDbAssertion(
    koin: Koin = GlobalContext.get(),
) {
    private val transactionTestDao: TransactionTestDao = koin.get()
    private val profileTestDao : ProfileTestDao = koin.get()
    private val productTestDao : ProductTestDao = koin.get()
    private val installmentTestDao: InstallmentTestDao = koin.get()
    private val installmentItemTestDao : InstallmentItemTestDao = koin.get()

    @RequiresApi(Build.VERSION_CODES.O)
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

            if (currentTransactionItems.count() != expected.productItems.count())
                return@count false

            val isProductItemsAllMatch = expected.productItems.all { expectedItem ->
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
            if (!isProductItemsAllMatch)
                return@count false

            val currentInstallment = installmentTestDao.getByInvoiceId(it.id)

            val expectedPaymentType = expected.paymentType
            when(expectedPaymentType){
                PaymentTypeAssertionDto.Cash -> {
                    return@count currentInstallment == null
                }
                is PaymentTypeAssertionDto.Installment -> {
                    if (currentInstallment == null)
                        return@count false
                    val actualInstallmentItems = installmentItemTestDao.getByInstallmentId(
                        currentInstallment.id)

                    if (actualInstallmentItems.count() != expectedPaymentType.items.count())
                        return@count false

                    return@count expectedPaymentType.items.all { expectedInstallmentItem ->
                        actualInstallmentItems.contains { actualInstallmentItem ->
                            expectedInstallmentItem.paymentDate
                                .isEqual(actualInstallmentItem.paymentDate) &&
                            expectedInstallmentItem.amount == actualInstallmentItem.amount
                        }
                    }
                }
            }
        }

        assertThat(expectedCriteriaMatchCount, equalTo(expectedCount))
    }

    fun assertCountInvoices(expectedCount: Int) = tryUntilSucceed(
        errorMessage = { "Expected invoice count : ${expectedCount}, Real invoice count : ${transactionTestDao.countInvoices()}" }
    ) {
        assertThat(transactionTestDao.countInvoices(), equalTo(expectedCount))
    }

    fun assertCountInvoiceItems(expectedCount: Int) = tryUntilSucceed(
        errorMessage = { "Expected invoice item count : ${expectedCount}, Real invoice item count : ${transactionTestDao.countInvoiceItems()}" }
    ) {
        assertThat(transactionTestDao.countInvoiceItems(), equalTo(expectedCount))
    }

    fun assertCountInstallment(expectedCount: Int) = tryUntilSucceed(
        errorMessage = { "Expected installment count : ${expectedCount}, Real installment count : ${installmentTestDao.count()}" }
    ) {
        assertThat(installmentTestDao.count(), equalTo(expectedCount))
    }

    fun assertCountInstallmentItem(expectedCount: Int) = tryUntilSucceed(
        errorMessage = { "Expected installment item count : ${expectedCount}, Real installment item count : ${installmentItemTestDao.count()}" }
    ) {
        assertThat(installmentItemTestDao.count(), equalTo(expectedCount))
    }
}

data class TransactionDetailsAssertionDto(
    val date: LocalDate,
    val profileName: String,
    val transactionType: TransactionType,
    val ppn: Int?,
    val productItems: List<TransactionDetailsItemAssertionDto>,
    val paymentType: PaymentTypeAssertionDto,
)

data class TransactionDetailsItemAssertionDto(
    val productName: String,
    val quantity : Int,
    val unitType : UnitType,
    val price : Int,
)