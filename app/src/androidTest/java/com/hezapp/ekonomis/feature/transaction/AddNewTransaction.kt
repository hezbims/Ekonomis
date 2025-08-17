package com.hezapp.ekonomis.feature.transaction

import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.steps.FormProductItem
import com.hezapp.ekonomis.test_application.BaseEkonomisIntegrationTest
import com.hezapp.ekonomis.test_utils.db_assertion.TransactionDetailsAssertionDto
import com.hezapp.ekonomis.test_utils.db_assertion.TransactionDetailsItemAssertionDto
import org.junit.Test
import java.time.LocalDate

class AddNewTransaction : BaseEkonomisIntegrationTest() {
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
                items = listOf(
                    TransactionDetailsItemAssertionDto(
                        productName = "Tuna kaleng",
                        quantity = 3,
                        unitType = UnitType.PIECE,
                        price = 250_000
                    )
                )
            ),
        )
        transactionDbAssertion.assertCountInvoiceItems(1)
    }
}