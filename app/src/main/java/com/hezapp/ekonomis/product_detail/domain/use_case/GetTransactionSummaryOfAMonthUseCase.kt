package com.hezapp.ekonomis.product_detail.domain.use_case

import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.product.model.TransactionSummary
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.core.domain.utils.getNextMonthYear
import com.hezapp.ekonomis.product_detail.data.dao.GetMonthlyStockByPeriodReadDao
import com.hezapp.ekonomis.product_detail.data.dao.GetProductTransactionsReadDao

class GetTransactionSummaryOfAMonthUseCase(
    private val getMonthlyStockByPeriod: GetMonthlyStockByPeriodReadDao,
    private val getProductTransactions: GetProductTransactionsReadDao,
    private val timeService: ITimeService,
) {
    suspend operator fun invoke(
        startPeriod: Long,
        productId: Int,
    ) : TransactionSummary {
        val nextMonth = startPeriod.getNextMonthYear(timeService)

        val outTransactions = getProductTransactions.execute(
            productId = productId,
            firstDayOfPeriod = startPeriod,
            lastDayOfPeriod = nextMonth,
            transactionTypeId = TransactionType.PENJUALAN.id
        )

        val inTransactions = getProductTransactions.execute(
            productId = productId,
            firstDayOfPeriod = startPeriod,
            lastDayOfPeriod = nextMonth,
            transactionTypeId = TransactionType.PEMBELIAN.id
        )
        val currentMonthStock = getMonthlyStockByPeriod.execute(
            startMonthPeriod = startPeriod,
            productId = productId,
            timeService = timeService,
        )

        return TransactionSummary(
            inProductTransactions = inTransactions,
            outProductTransactions = outTransactions,
            firstDayOfMonthStock = currentMonthStock?.quantityPerUnitType,
            monthlyStockId = currentMonthStock?.id ?: 0,
        )
    }
}