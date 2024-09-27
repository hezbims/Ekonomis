package com.hezapp.ekonomis.product_detail.domain.use_case

import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.monthly_stock.entity.QuantityPerUnitType
import com.hezapp.ekonomis.core.domain.monthly_stock.repo.IMonthlyStockRepo
import com.hezapp.ekonomis.core.domain.utils.toBeginningOfMonth
import com.hezapp.ekonomis.core.domain.utils.toCalendar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetStockOfAMonthPerUnitTypeUseCase(
    private val repo : IMonthlyStockRepo
) {
    suspend operator  fun invoke(
        monthPeriod: Long,
        productId: Int,
    ) : Flow<ResponseWrapper<QuantityPerUnitType? , MyBasicError>> =
    flow<ResponseWrapper<QuantityPerUnitType? , MyBasicError>>{

        emit(ResponseWrapper.Loading())

        val startMonthPeriod = monthPeriod.toCalendar().toBeginningOfMonth().timeInMillis
        val currentMonthStock = repo.getMonthlyStock(
            startMonthPeriod = startMonthPeriod,
            productId = productId
        )

        emit(ResponseWrapper.Succeed(data = currentMonthStock?.quantityPerUnitType))

    }.catch { emit(ResponseWrapper.Failed()) }
}