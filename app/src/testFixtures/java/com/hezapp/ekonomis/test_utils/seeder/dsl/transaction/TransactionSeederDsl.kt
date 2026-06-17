package com.hezapp.ekonomis.test_utils.seeder.dsl.transaction

import android.os.Build
import androidx.annotation.RequiresApi
import com.hezapp.ekonomis.test_utils.seeder.dsl.SeederDsl
import com.hezapp.ekonomis.test_utils.seeder.dsl.transaction.scope.OneMonthTransactionsScope
import com.hezapp.ekonomis.test_utils.seeder.snapshot.InvoiceSnapshot
import kotlinx.coroutines.runBlocking
import java.time.YearMonth

/**
 * DSL entry point to create one or more transactions within a single month.
 *
 * Usage example:
 * ```
 * val snapshots = thereIsTransactionOn(YearMonth.of(2020, 2)) {
 *     out(day = 15) {
 *         withProduct(quantity = Quantity.piece(3), price = 25000)
 *         withoutInstallment(PaymentMedia.CASH)
 *     }
 *     `in`(day = 10, ppn = 11) {
 *         withProduct(quantity = Quantity.carton(2), price = 100000)
 *         withInstallment(isPaidOff = false) {
 *             withPayment(amount = 50000)
 *         }
 *     }
 * }
 * ```
 *
 * @param yearMonth the month of the transactions (all [OneMonthTransactionsScope.out] / [OneMonthTransactionsScope.in] calls share this month)
 * @param block lambda scope to define the transactions
 * @return [List] of [InvoiceSnapshot] — one snapshot per transaction
 */
@RequiresApi(Build.VERSION_CODES.O)
fun SeederDsl.transactionOn(
    yearMonth: YearMonth,
    block: suspend OneMonthTransactionsScope.() -> Unit,
): List<InvoiceSnapshot> = runBlocking {
    val scope = OneMonthTransactionsScope(
        yearMonth = yearMonth,
        koin = koin,
    )
    scope.block()
    scope.snapshots.toList()
}
