package com.hezapp.ekonomis.product_detail.domain.use_case

import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.repo.IInvoiceItemRepo
import com.hezapp.ekonomis.core.domain.product.model.ProductDetail
import com.hezapp.ekonomis.core.domain.product.repo.IProductRepo
import com.hezapp.ekonomis.core.domain.utils.getNextMonthYear
import com.hezapp.ekonomis.core.domain.utils.toBeginningOfMonth
import com.hezapp.ekonomis.core.domain.utils.toCalendar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last

class GetProductDetailUseCase(
    private val productRepo : IProductRepo,
    private val invoiceItemRepo: IInvoiceItemRepo,
    private val getStockOfAMonthPerUnitTypeUseCase: GetStockOfAMonthPerUnitTypeUseCase,
) {
    operator fun invoke(
        productId : Int,
        monthYearPeriod: Long,
    ) : Flow<ResponseWrapper<ProductDetail, MyBasicError>> =
    flow<ResponseWrapper<ProductDetail, MyBasicError>> {
        emit(ResponseWrapper.Loading())

        val startMonthPeriod = monthYearPeriod.toCalendar().toBeginningOfMonth().timeInMillis
        val endMonthPeriod = startMonthPeriod.getNextMonthYear()

        val currentProduct = productRepo.getProduct(productId = productId)
        val outTransactions = invoiceItemRepo.getProductTransactions(
            productId = productId,
            startPeriod = startMonthPeriod,
            endPeriod = endMonthPeriod,
            transactionType = TransactionType.PENJUALAN,
        )
        val inTransactions = invoiceItemRepo.getProductTransactions(
            productId = productId,
            startPeriod = startMonthPeriod,
            endPeriod = endMonthPeriod,
            transactionType = TransactionType.PEMBELIAN,
        )

        val firstDayOfMonthStock = getStockOfAMonthPerUnitTypeUseCase(
            monthPeriod = startMonthPeriod,
            productId = productId,
        ).last()

        emit(ResponseWrapper.Succeed(
            ProductDetail(
                id = currentProduct.id,
                inProductTransactions = inTransactions,
                outProductTransactions = outTransactions,
                productName = currentProduct.name,
                firstDayOfMonthStock = firstDayOfMonthStock.asSucceed().data,
            )
        ))
    }.catch { emit(ResponseWrapper.Failed()) }
}