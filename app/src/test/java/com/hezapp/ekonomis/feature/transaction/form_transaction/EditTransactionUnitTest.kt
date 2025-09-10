package com.hezapp.ekonomis.feature.transaction.form_transaction

import androidx.navigation.compose.rememberNavController
import com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.AddOrUpdateTransactionScreen
import com.hezapp.ekonomis.add_or_update_transaction.presentation.model.PaymentType
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import com.hezapp.ekonomis.test_application.BaseEkonomisUiUnitTest
import com.hezapp.ekonomis.test_utils.seeder.InstallmentItemSeed
import com.hezapp.ekonomis.test_utils.seeder.InstallmentSeed
import com.hezapp.ekonomis.test_utils.seeder.InvoiceItemSeed
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.koin.core.parameter.parametersOf
import java.time.LocalDate

class EditTransactionUnitTest : BaseEkonomisUiUnitTest() {
    @Test
    fun `when invoice doesn't have installment, the radio button should select cash payment type`() =
        runTest {
            //region Prepare
            val product = utils.productSeeder
                .run(listOf(ProductEntity(name = "product-1")))
                .single()
            val profile = utils.profileSeeder
                .run(profileName = "profile-1", profileType = ProfileType.CUSTOMER)
            val invoiceId = utils.invoiceSeeder.run(
                profile = profile,
                date = LocalDate.now()
                    .withYear(2020)
                    .withMonth(2)
                    .withDayOfMonth(1),
                ppn = null,
                invoiceItems = listOf(
                    InvoiceItemSeed(
                        quantity = 3,
                        unitType = UnitType.PIECE,
                        product = product,
                        price = 2_500_000
                    )
                )
            )
            composeRule.setContent {
                AddOrUpdateTransactionScreen(
                    navController = rememberNavController(),
                    onSubmitSucceed = {},
                    onDeleteSucceed = {},
                    viewModel = koin.get(
                        parameters = { parametersOf(invoiceId) }
                    ),
                    timeService = koin.get(),
                )
            }
            //endregion

            utils.transactionFormRobot.assertSelectedPaymentType(PaymentType.CASH)
        }

    @Test
    fun `when user edit transaction from installment to cash, the previous installment should be deleted`() =
        runTest {
            val product = utils.productSeeder
                .run(listOf(ProductEntity(name = "product-1")))
                .single()
            val profile = utils.profileSeeder
                .run(profileName = "profile-1", profileType = ProfileType.CUSTOMER)
            val invoiceId = utils.invoiceSeeder.run(
                profile = profile,
                date = LocalDate.now()
                    .withYear(2020)
                    .withMonth(2)
                    .withDayOfMonth(1),
                ppn = null,
                invoiceItems = listOf(
                    InvoiceItemSeed(
                        quantity = 3,
                        unitType = UnitType.PIECE,
                        product = product,
                        price = 2_500_000
                    )
                ),
                installmentSeed = InstallmentSeed(
                    isPaidOff = true,
                    items = listOf(
                        InstallmentItemSeed(
                            amount = 2_000_000,
                            paymentDate = LocalDate.of(2025, 1, 1)
                        ),
                        InstallmentItemSeed(
                            amount = 500_000,
                            paymentDate = LocalDate.of(2025, 1, 2)
                        ),
                    )
                )
            )
            utils.invoiceSeeder.run(
                profile = profile,
                date = LocalDate.now()
                    .withYear(2020)
                    .withMonth(2)
                    .withDayOfMonth(1),
                ppn = null,
                invoiceItems = listOf(
                    InvoiceItemSeed(
                        quantity = 3,
                        unitType = UnitType.PIECE,
                        product = product,
                        price = 2_500_000
                    )
                ),
                installmentSeed = InstallmentSeed(
                    isPaidOff = true,
                    items = listOf(
                        InstallmentItemSeed(
                            amount = 500_000,
                            paymentDate = LocalDate.of(2025, 1, 2)
                        ),
                    )
                )
            )
            composeRule.setContent {
                AddOrUpdateTransactionScreen(
                    navController = rememberNavController(),
                    onSubmitSucceed = {},
                    onDeleteSucceed = {},
                    viewModel = koin.get(
                        parameters = { parametersOf(invoiceId) }
                    ),
                    timeService = koin.get(),
                )
            }

            utils.transactionFormRobot.changeSelectedPaymentType(PaymentType.CASH)
            utils.transactionFormRobot.submitTransactionForm()
            utils.transactionFormRobot.confirmUnsafePaymentTypeChange()

            utils.transactionDbAssertion.assertCountInstallment(1)
            utils.transactionDbAssertion.assertCountInstallmentItem(1)
        }
}