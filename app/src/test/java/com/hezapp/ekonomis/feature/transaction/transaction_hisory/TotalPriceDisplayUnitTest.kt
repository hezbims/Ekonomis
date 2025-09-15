package com.hezapp.ekonomis.feature.transaction.transaction_hisory

import androidx.navigation.compose.rememberNavController
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.test_application.BaseEkonomisUiUnitTest
import com.hezapp.ekonomis.test_utils.seeder.InvoiceItemSeed
import com.hezapp.ekonomis.transaction_history.presentation.TransactionHistoryScreen
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class TotalPriceDisplayUnitTest : BaseEkonomisUiUnitTest() {
    @Before
    fun prepare() = runTest {
        val supplier = utils.profileSeeder.run("supplier-1", ProfileType.SUPPLIER)
        val customer = utils.profileSeeder.run("customer-1", ProfileType.CUSTOMER)
        val products = utils.productSeeder.run(listOf(
            ProductEntity(name = "product-1"), ProductEntity(name = "product-2")
        ))

        // buying transaction
        utils.invoiceSeeder.run(
            profile = supplier,
            date = koin.get<ITimeService>().getLocalDate(),
            invoiceItems = listOf(
                InvoiceItemSeed(
                    quantity = 1,
                    unitType = UnitType.PIECE,
                    product = products[0],
                    price = 2_000_000_000
                ),
                InvoiceItemSeed(
                    quantity = 3,
                    unitType = UnitType.CARTON,
                    product = products[1],
                    price = 1_500_000_000
                ),
                InvoiceItemSeed(
                    quantity = 3,
                    unitType = UnitType.CARTON,
                    product = products[1],
                    price = 1_600_000_000
                ),
            ),
            ppn = 11,
            installmentSeed = null,
        )

        // selling transaction
        utils.invoiceSeeder.run(
            profile = customer,
            date = koin.get<ITimeService>().getLocalDate(),
            invoiceItems = listOf(
                InvoiceItemSeed(
                    quantity = 1,
                    unitType = UnitType.PIECE,
                    product = products[0],
                    price = 1_600_000_000
                ),
                InvoiceItemSeed(
                    quantity = 4,
                    unitType = UnitType.CARTON,
                    product = products[1],
                    price = 1_700_000_000
                ),
            ),
            ppn = null,
            installmentSeed = null,
        )

        composeRule.setContent {
            TransactionHistoryScreen(
                navController = rememberNavController(),
                viewModel = koin.get(),
                timeService = koin.get(),
            )
        }
    }

    @Test
    fun `Should display total price of product correctly from sum of invoice items`(){
        utils.transactionHistoryRobot. apply {
            assertTransactionCardExistWith(totalPrice = "-Rp5.100.000.000")
            assertTransactionCardExistWith(totalPrice = "+Rp3.300.000.000")
        }
    }
}