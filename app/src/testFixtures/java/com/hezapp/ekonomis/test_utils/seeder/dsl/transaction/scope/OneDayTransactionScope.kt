package com.hezapp.ekonomis.test_utils.seeder.dsl.transaction.scope

import com.hezapp.ekonomis.core.domain.invoice.entity.PaymentMedia
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.test_utils.seeder.dsl.transaction.dto.InstallmentData
import com.hezapp.ekonomis.test_utils.seeder.dsl.transaction.dto.ProductData
import com.hezapp.ekonomis.test_utils.seeder.dsl.transaction.dto.QuantityData
import java.time.YearMonth

/**
 * Scope to define a single transaction (either [OneMonthTransactionsScope.out] or
 * [OneMonthTransactionsScope.in]).
 *
 * Instances are created by [OneMonthTransactionsScope.out] or
 * [OneMonthTransactionsScope.in]. Inside the lambda block, call:
 * - [withProduct] — add a product item (at least one required)
 * - [withoutInstallment] — mark as a cash (no-installment) transaction
 * - [withInstallment] — mark as an installment transaction
 */
class OneDayTransactionScope internal constructor(
    internal val transactionType: TransactionType,
    internal val yearMonth: YearMonth,
    internal val day: Int,
    internal val ppn: Int?,
    internal val profileId: Int?,
) {
    internal val productSeeds = mutableListOf<ProductData>()
    internal var paymentMedia: PaymentMedia = PaymentMedia.CASH
    internal var installmentSeeds: InstallmentData? = null

    /**
     * Adds a product item to the transaction.
     *
     * Must be called at least once per transaction.
     *
     * @param id product id in the database.
     *   If `null`, uses the first product found in the database.
     *   If the database is empty, creates a new product named `"product-default"`.
     * @param quantity quantity, defaults to [com.hezapp.ekonomis.test_utils.seeder.dsl.transaction.dto.QuantityData.piece]`(1)`
     * @param price unit price, defaults to `1`
     */
    fun withProduct(
        id: Int? = null,
        quantity: QuantityData = QuantityData.piece(),
        price: Int = 1,
    ) {
        productSeeds.add(
            ProductData(
                id = id,
                quantity = quantity,
                price = price,
            )
        )
    }

    /**
     * Marks the transaction as **cash / no installment** (immediate payment).
     *
     * @param media payment media, defaults to [PaymentMedia.CASH].
     *   Use [PaymentMedia.TRANSFER] for bank transfer.
     */
    fun withoutInstallment(media: PaymentMedia) {
        paymentMedia = media
    }

    /**
     * Adds an installment plan to the transaction.
     *
     * Inside the lambda, call [InstallmentScope.withPayment] one or more times.
     *
     * @param isPaidOff paid-off status.
     *   - `true` → paid off
     *   - `false` → not paid off
     *   - `null` (default) → auto-calculated:
     *     `total payments >= total invoice → true`
     * @param block lambda scope to define the installment payments
     */
    fun withInstallment(
        isPaidOff: Boolean? = null,
        block: InstallmentScope.() -> Unit,
    ) {
        val scope = InstallmentScope(yearMonth)
        scope.block()
        installmentSeeds = InstallmentData(
            isPaidOff = isPaidOff,
            paymentSeeds = scope.payments.toList(),
        )
    }

    internal fun validate() {
        require(productSeeds.isNotEmpty()) {
            "Transaction must have at least one product. " +
                "Call withProduct() inside the out/in block."
        }
    }
}