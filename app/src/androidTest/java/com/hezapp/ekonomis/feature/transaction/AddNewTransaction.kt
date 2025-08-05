package com.hezapp.ekonomis.feature.transaction

import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.robot.transaction_form.ProductFormAssertData
import com.hezapp.ekonomis.test_application.BaseEkonomisIntegrationTest
import org.junit.Test
import java.time.LocalDate

class AddNewTransaction : BaseEkonomisIntegrationTest() {
    /**
     * Memastikan data yang dibuat tampil dengan akurat.
     */
    @Test(timeout = 60_000)
    fun insertedDataShouldDisplayedCorrectly() {
        transactionHistoryRobot.navigateToAddNewTransaction()

        transactionFormRobot.apply {
            chooseTransactionType(TransactionType.PEMBELIAN)

            chooseTransactionDate(
                day = 1,
                month = 1,
                year = 2021,
            )

            navigateToChooseProfile()
        }

        transactionFormRobot.chooseProfileRobot.apply {
            val profileName = "profile-1"
            registerNewProfile(profileName)

            chooseProfile(profileName)
        }

        transactionFormRobot.navigateToChooseProduct()

        transactionFormRobot.chooseProductRobot.apply {
            registerNewProduct("Tuna kaleng")

            chooseProductForTransaction(
                productName = "Tuna kaleng",
                quantity = 3,
                unitType = UnitType.PIECE,
                totalPrice = 250_000)

            confirmAllSelectedProducts(1)
        }

        transactionFormRobot.fillPpn(11)

        transactionFormRobot.submitTransactionForm()

        transactionHistoryRobot.apply {
            openAndApplyFilter(
                targetMonth = 1,
                targetYear = 2021,
            )

            waitAndClickTransactionCard(
                profileName = "profile-1",
                totalPrice = -250_000,
                date = LocalDate.now()
                    .withYear(2021)
                    .withMonth(1)
                    .withDayOfMonth(1)
            )
        }

        transactionFormRobot.assertFormContent(
            transactionType = TransactionType.PEMBELIAN,
            date = LocalDate.now()
                .withYear(2021)
                .withMonth(1)
                .withDayOfMonth(1),
            profileName = "profile-1",
            ppn = 11,
            products = listOf(
                ProductFormAssertData(
                    name = "Tuna kaleng",
                    price = 250_000,
                    quantity = 3,
                    unitType = UnitType.PIECE,
                )
            ),
        )
    }
}