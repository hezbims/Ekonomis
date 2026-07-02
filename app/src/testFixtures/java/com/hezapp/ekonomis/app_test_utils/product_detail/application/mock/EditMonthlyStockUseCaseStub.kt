package com.hezapp.ekonomis.app_test_utils.product_detail.application.mock

import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.product_detail.domain.use_case.EditMonthlyStockUseCase
import kotlinx.coroutines.flow.flow
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

fun EditMonthlyStockUseCase.stubReturnsSuccess() {
    whenever(this.invoke(
        cartonQuantity = any(),
        pieceQuantity = any(),
        monthlyStockEntityId = any(),
        monthYearPeriod = any<Long>(),
        productId = any(),
    )).thenReturn(flow {
        emit(ResponseWrapper.Loading())
        emit(ResponseWrapper.Succeed(null))
    })
}