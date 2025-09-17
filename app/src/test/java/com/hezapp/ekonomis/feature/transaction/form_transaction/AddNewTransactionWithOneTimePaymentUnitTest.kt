package com.hezapp.ekonomis.feature.transaction.form_transaction

import com.hezapp.ekonomis.MainComposable
import com.hezapp.ekonomis.core.domain.invoice.entity.PaymentMedia
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.dto.PaymentTypeAssertionDto
import com.hezapp.ekonomis.steps.FormProductItem
import com.hezapp.ekonomis.steps.ModifyPaymentSectionAction
import com.hezapp.ekonomis.test_application.BaseEkonomisUiUnitTestWithoutRunner
import com.hezapp.ekonomis.test_utils.db_assertion.TransactionDetailsAssertionDto
import com.hezapp.ekonomis.test_utils.db_assertion.TransactionDetailsItemAssertionDto
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner

/**
 * Memastikan payment media bisa masuk ke database dengan benar apabila user memilih
 * tipe pembayaran 'langsung'
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
class AddNewTransactionWithOneTimePaymentUnitTest(
    private val selectedPaymentMedia: PaymentMedia?,
    private val expectedPaymentMediaInDb: PaymentMedia,
) : BaseEkonomisUiUnitTestWithoutRunner() {

    companion object {
        @Suppress("Unused")
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun parameter() = listOf(
            arrayOf(null, PaymentMedia.TRANSFER),
            arrayOf(PaymentMedia.CASH, PaymentMedia.CASH)
        )
    }

    @Before
    fun prepare() = runTest {
        utils.productSeeder
            .run(listOf(ProductEntity(name = "product-1")))
        utils.profileSeeder
            .run(profileName = "customer-1", profileType = ProfileType.CUSTOMER)

        composeRule.setContent {
            MainComposable(koinApp)
        }
        utils.transactionHistoryRobot.navigateToAddNewTransaction()
    }

    @Test
    fun `The payment media that inserted to database when user choose one-time payment must be correct`(){
        utils.filltransactionSteps.fillForm(
            transactionType = TransactionType.PENJUALAN,
            profileName = "customer-1",
            date = koin.get<ITimeService>().getLocalDate(),
            chooseProductActions = listOf(
                FormProductItem(
                    name = "product-1",
                    quantity = 1,
                    unitType = UnitType.PIECE,
                    totalPrice = 20_000,
                    newRegistration = false
                )
            ),
            modifyPaymentSectionActions = selectedPaymentMedia?.let {
                listOf(ModifyPaymentSectionAction.SelectPaymentMedia(it))
            } ?: emptyList(),
            ppn = null,
            isRegisterNewProfile = false
        )

        composeRule.waitForIdle()

        utils.transactionDbAssertion.assertCountTransactionDetails(
            expected = TransactionDetailsAssertionDto(
                date = koin.get<ITimeService>().getLocalDate(),
                profileName = "customer-1",
                transactionType = TransactionType.PENJUALAN,
                ppn = null,
                productItems = listOf(
                    TransactionDetailsItemAssertionDto(
                        productName = "product-1",
                        quantity = 1,
                        unitType = UnitType.PIECE,
                        price = 20_000
                    )
                ),
                paymentType = PaymentTypeAssertionDto.Cash,
                paymentMedia = expectedPaymentMediaInDb,
            ),
            expectedCount = 1,
        )
    }
}