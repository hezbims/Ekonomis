package com.hezapp.ekonomis.product_detail.domain.use_case

import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.repo.IInvoiceItemRepo
import com.hezapp.ekonomis.core.domain.monthly_stock.repo.IMonthlyStockRepo
import com.hezapp.ekonomis.core.domain.product.model.TransactionSummary
import com.hezapp.ekonomis.core.domain.utils.getNextMonthYear

class GetTransactionSummaryOfAMonthUseCase(
    private val monthlyStockRepo: IMonthlyStockRepo,
    private val invoiceItemRepo: IInvoiceItemRepo,
) {
    suspend operator fun invoke(
        startPeriod: Long,
        productId: Int,
    ) : TransactionSummary {
        val nextMonth = startPeriod.getNextMonthYear()

        val outTransactions = invoiceItemRepo.getProductTransactions(
            productId = productId,
            startPeriod = startPeriod,
            endPeriod = nextMonth,
            transactionType = TransactionType.PENJUALAN
        )

        val inTransactions = invoiceItemRepo.getProductTransactions(
            productId = productId,
            startPeriod = startPeriod,
            endPeriod = nextMonth,
            transactionType = TransactionType.PEMBELIAN
        )
        val currentMonthStock = monthlyStockRepo.getMonthlyStock(
            startMonthPeriod = startPeriod,
            productId = productId,
        )

        return TransactionSummary(
            inProductTransactions = inTransactions,
            outProductTransactions = outTransactions,
            firstDayOfMonthStock = currentMonthStock?.quantityPerUnitType,
            monthlyStockId = currentMonthStock?.id ?: 0,
        )
    }
}