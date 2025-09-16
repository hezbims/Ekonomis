package com.hezapp.ekonomis.feature.transaction.transaction_hisory

import androidx.navigation.compose.rememberNavController
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.core.domain.utils.TimeService
import com.hezapp.ekonomis.test_application.BaseEkonomisUiUnitTestWithoutRunner
import com.hezapp.ekonomis.test_utils.seeder.InstallmentSeed
import com.hezapp.ekonomis.test_utils.seeder.InvoiceItemSeed
import com.hezapp.ekonomis.transaction_history.presentation.TransactionHistoryScreen
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.robolectric.ParameterizedRobolectricTestRunner
import java.time.LocalDate
import java.time.YearMonth
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@RunWith(ParameterizedRobolectricTestRunner::class)
class FilterFunctionalityUnitTest(
    private val yearMonthFilter: YearMonth?,
    private val isOnlyNotPaidOffFilter: Boolean,
    private val expectedDateDatas: Array<LocalDate>,
    private val expectedNotExistDateDatas: Array<LocalDate>,
) : BaseEkonomisUiUnitTestWithoutRunner() {

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun data() : Collection<Array<Any?>> = listOf(
            arrayOf(null, false, arrayOf(
                LocalDate.of(2023, 1, 12),
                LocalDate.of(2023, 1, 11)
            ), arrayOf<LocalDate>()),
            arrayOf(YearMonth.of(2022, 12), true, arrayOf(
                LocalDate.of(2022, 12, 14),
            ), arrayOf<LocalDate>(
                LocalDate.of(2022, 12, 5),
                LocalDate.of(2022, 12, 2)
            )),
            arrayOf(YearMonth.of(2024, 1), true, arrayOf(
                LocalDate.of(2024, 1, 14)
            ), arrayOf<LocalDate>(
                LocalDate.of(2024, 1, 16)
            ))
        )
    }

    @Before
    fun prepare() = runTest {
        val prodTimeService = TimeService()

        val timeService = object : ITimeService(){
            override fun getCurrentTimeInMillis(): Long {
                return Calendar.getInstance(getTimezone()).apply {
                    set(2023, Calendar.JANUARY, 4, 15, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            }

            override fun getTimezone(): TimeZone {
                return prodTimeService.getTimezone()
            }

            override fun getLocale(): Locale {
                return prodTimeService.getLocale()
            }
        }

        koin.loadModules(listOf(module {
            single<ITimeService>{ timeService }
        }), allowOverride = true)

        val customer = utils.profileSeeder.run(profileName = "customer-1", profileType = ProfileType.CUSTOMER)
        val supplier = utils.profileSeeder.run(profileName = "supplier-1", profileType = ProfileType.SUPPLIER)
        val product1 = utils.productSeeder.run(
            products = listOf(
                ProductEntity(1, "product-1")
            )
        ).single()

        //region Dua bulan sebelumnya
        utils.invoiceSeeder.run(
            profile = customer,
            date = timeService.getLocalDate().minusMonths(2).withDayOfMonth(2),
            invoiceItems = listOf(InvoiceItemSeed(
                quantity = 2,
                unitType = UnitType.PIECE,
                product = product1,
                price = 2_000_000
            )),
            ppn = null,
            installmentSeed = null,
        )
        //endregion
        //region Satu bulan sebelumnya
        utils.invoiceSeeder.run(
            profile = customer,
            date = timeService.getLocalDate().minusMonths(1).withDayOfMonth(14),
            invoiceItems = listOf(InvoiceItemSeed(
                quantity = 2,
                unitType = UnitType.PIECE,
                product = product1,
                price = 2_000_000
            )),
            ppn = null,
            installmentSeed = InstallmentSeed(isPaidOff = false, items = listOf()),
        )
        utils.invoiceSeeder.run(
            profile = supplier,
            date = timeService.getLocalDate().minusMonths(1).withDayOfMonth(2),
            invoiceItems = listOf(InvoiceItemSeed(
                quantity = 2,
                unitType = UnitType.PIECE,
                product = product1,
                price = 2_000_000
            )),
            ppn = 11,
            installmentSeed = InstallmentSeed(isPaidOff = true, items = emptyList()),
        )
        utils.invoiceSeeder.run(
            profile = customer,
            date = timeService.getLocalDate().minusMonths(1).withDayOfMonth(5),
            invoiceItems = listOf(InvoiceItemSeed(
                quantity = 2,
                unitType = UnitType.PIECE,
                product = product1,
                price = 2_000_000
            )),
            ppn = null,
            installmentSeed = null,
        )
        //endregion
        //region Bulan sekarang
        utils.invoiceSeeder.run(
            profile = customer,
            date = timeService.getLocalDate().withDayOfMonth(12),
            invoiceItems = listOf(InvoiceItemSeed(
                quantity = 2,
                unitType = UnitType.PIECE,
                product = product1,
                price = 2_000_000
            )),
            ppn = null,
            installmentSeed = InstallmentSeed(isPaidOff = false, items = listOf()),
        )
        utils.invoiceSeeder.run(
            profile = supplier,
            date = timeService.getLocalDate().withDayOfMonth(11),
            invoiceItems = listOf(InvoiceItemSeed(
                quantity = 2,
                unitType = UnitType.PIECE,
                product = product1,
                price = 2_000_000
            )),
            ppn = 11,
            installmentSeed = InstallmentSeed(isPaidOff = true, items = emptyList()),
        )
        //endregion
        //region Satu bulan setelahnya
        utils.invoiceSeeder.run(
            profile = customer,
            date = timeService.getLocalDate().plusMonths(1).withDayOfMonth(14),
            invoiceItems = listOf(InvoiceItemSeed(
                quantity = 2,
                unitType = UnitType.PIECE,
                product = product1,
                price = 2_000_000
            )),
            ppn = null,
            installmentSeed = InstallmentSeed(isPaidOff = false, items = listOf()),
        )
        utils.invoiceSeeder.run(
            profile = supplier,
            date = timeService.getLocalDate().plusMonths(1).withDayOfMonth(2),
            invoiceItems = listOf(InvoiceItemSeed(
                quantity = 2,
                unitType = UnitType.PIECE,
                product = product1,
                price = 2_000_000
            )),
            ppn = 11,
            installmentSeed = InstallmentSeed(isPaidOff = true, items = emptyList()),
        )
        utils.invoiceSeeder.run(
            profile = customer,
            date = timeService.getLocalDate().plusMonths(1).withDayOfMonth(5),
            invoiceItems = listOf(InvoiceItemSeed(
                quantity = 2,
                unitType = UnitType.PIECE,
                product = product1,
                price = 2_000_000
            )),
            ppn = null,
            installmentSeed = null,
        )
        //endregion
        //region Satu tahun setelahnya
        utils.invoiceSeeder.run(
            profile = customer,
            date = timeService.getLocalDate().plusYears(1).withDayOfMonth(14),
            invoiceItems = listOf(InvoiceItemSeed(
                quantity = 2,
                unitType = UnitType.PIECE,
                product = product1,
                price = 2_000_000
            )),
            ppn = null,
            installmentSeed = InstallmentSeed(isPaidOff = false, items = listOf()),
        )
        utils.invoiceSeeder.run(
            profile = customer,
            date = timeService.getLocalDate().plusYears(1).withDayOfMonth(16),
            invoiceItems = listOf(InvoiceItemSeed(
                quantity = 2,
                unitType = UnitType.PIECE,
                product = product1,
                price = 2_000_000
            )),
            ppn = null,
            installmentSeed = InstallmentSeed(isPaidOff = true, items = listOf()),
        )
        //endregion

        composeRule.setContent {
            TransactionHistoryScreen(
                navController = rememberNavController(),
                viewModel = koin.get(),
                timeService = timeService
            )
        }
    }

    @Test
    fun `Should filter data correctly`(){
        utils.transactionHistoryRobot.actionOpenAndApplyFilter(
            yearMonthFilter,
            isOnlyNotPaidOffFilter)

        for (expectedDate in expectedDateDatas)
            utils.transactionHistoryRobot.assertTransactionCardExistWith(
                date = expectedDate
            )
        for (expectedNotExistDate in expectedNotExistDateDatas)
            utils.transactionHistoryRobot.assertTransactionCardNotExistWith(
                date = expectedNotExistDate
            )
    }
}