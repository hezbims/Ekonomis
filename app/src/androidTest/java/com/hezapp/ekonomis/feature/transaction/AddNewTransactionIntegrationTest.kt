package com.hezapp.ekonomis.feature.transaction

import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.dto.InstallmentItemAssertionDto
import com.hezapp.ekonomis.dto.PaymentTypeAssertionDto
import com.hezapp.ekonomis.steps.FormProductItem
import com.hezapp.ekonomis.steps.ModifyPaymentSectionAction
import com.hezapp.ekonomis.test_application.BaseEkonomisUiTest
import com.hezapp.ekonomis.test_utils.db_assertion.TransactionDetailsAssertionDto
import com.hezapp.ekonomis.test_utils.db_assertion.TransactionDetailsItemAssertionDto
import org.junit.Test
import java.time.LocalDate

class AddNewTransactionIntegrationTest : BaseEkonomisUiTest(immediatelyLaunchMainActivity = true) {
    /**
     * Memastikan data yang dibuat tampil dengan akurat.
     */
    @Test(timeout = 60_000)
    fun insertedDataShouldDisplayedCorrectly() {
        transactionHistoryRobot.navigateToAddNewTransaction()

        filltransactionSteps.fillForm(
            transactionType = TransactionType.PEMBELIAN,
            profileName = "profile-1",
            isRegisterNewProfile = true,
            date = LocalDate.now()
                .withDayOfMonth(1)
                .withMonth(1)
                .withYear(2021),
            chooseProductActions = listOf(
                FormProductItem(
                    name = "Tuna kaleng",
                    totalPrice = 250_000,
                    quantity = 3,
                    unitType = UnitType.PIECE,
                    newRegistration = true,
                )
            ),
            ppn = 11,
            modifyPaymentSectionActions = listOf(
                ModifyPaymentSectionAction.SelectInstallmentPaymentType,
                ModifyPaymentSectionAction.AddNewInstallmentItem(
                    amount = 3_000_000,
                    date = LocalDate.of(2015, 1, 1)
                ),
                ModifyPaymentSectionAction.AddNewInstallmentItem(
                    amount = 1_500,
                    date = LocalDate.of(2015, 1, 1),
                ),
                ModifyPaymentSectionAction.AddNewInstallmentItem(
                    amount = 5_000,
                    date = LocalDate.of(2015, 1, 3)
                ),
                ModifyPaymentSectionAction.AddNewInstallmentItem(
                    amount = 3_000_000,
                    date = LocalDate.of(2015, 1, 1)
                ),
                ModifyPaymentSectionAction.EditInstallmentItem(
                    index = 1,
                    amount = 2_000,
                    date = LocalDate.of(2015, 1, 2),
                ),
                ModifyPaymentSectionAction.DeleteInstallmentItem(index = 3),
            )
        )

        transactionDbAssertion.assertCountInvoices(1)
        transactionDbAssertion.assertCountTransactionDetails(
            expected = TransactionDetailsAssertionDto(
                date = LocalDate.now()
                    .withYear(2021)
                    .withMonth(1)
                    .withDayOfMonth(1),
                profileName = "profile-1",
                transactionType = TransactionType.PEMBELIAN,
                ppn = 11,
                productItems = listOf(
                    TransactionDetailsItemAssertionDto(
                        productName = "Tuna kaleng",
                        quantity = 3,
                        unitType = UnitType.PIECE,
                        price = 250_000
                    )
                ),
                paymentType = PaymentTypeAssertionDto.Installment(
                    isPaidOff = true,
                    items = listOf(
                        InstallmentItemAssertionDto(
                            paymentDate = LocalDate.of(2015, 1, 1),
                            amount = 3_000_000
                        ),
                        InstallmentItemAssertionDto(
                            paymentDate = LocalDate.of(2015, 1, 2),
                            amount = 2_000
                        ),
                        InstallmentItemAssertionDto(
                            paymentDate = LocalDate.of(2015, 1, 3),
                            amount = 5_000
                        ),
                    ),
                )
            ),
        )
        transactionDbAssertion.assertCountInvoiceItems(1)
    }
}