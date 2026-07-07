package com.hezapp.ekonomis.product_detail.domain.use_case

import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.core.domain.utils.getNextMonthYear
import com.hezapp.ekonomis.product_detail.application.dto.ProductQuantityDto
import com.hezapp.ekonomis.product_detail.application.dto.ProductTransaction
import com.hezapp.ekonomis.product_detail.application.dto.TransactionSummary
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
        ).map { queryResult ->
            ProductTransaction.fromQueryResult(queryResult)
        }

        val inTransactions = getProductTransactions.execute(
            productId = productId,
            firstDayOfPeriod = startPeriod,
            lastDayOfPeriod = nextMonth,
            transactionTypeId = TransactionType.PEMBELIAN.id
        ).map { queryResult ->
            ProductTransaction.fromQueryResult(queryResult)
        }

        val currentMonthStockEntity = getMonthlyStockByPeriod.execute(
            startMonthPeriod = startPeriod,
            productId = productId,
            timeService = timeService,
        )

        return TransactionSummary(
            inProductTransactions = inTransactions,
            outProductTransactions = outTransactions,
            firstDayOfMonthStock = currentMonthStockEntity?.quantityPerUnitType?.let {
                ProductQuantityDto.fromQueryResult(it)
            },
            monthlyStockId = currentMonthStockEntity?.id ?: 0,
        )
    }
}