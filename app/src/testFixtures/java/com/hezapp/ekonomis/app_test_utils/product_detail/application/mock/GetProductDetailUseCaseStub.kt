package com.hezapp.ekonomis.app_test_utils.product_detail.application.mock

import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.product_detail.application.dto.ProductDetail
import com.hezapp.ekonomis.product_detail.domain.use_case.GetProductDetailUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

fun GetProductDetailUseCase.stubReturnsLoadingOnly() {
    whenever(this.invoke(any(), any<Long>()))
        .thenReturn(flow { emit(ResponseWrapper.Loading()) })
}

fun GetProductDetailUseCase.stubReturnsError() {
    whenever(this.invoke(any(), any<Long>()))
        .thenReturn(flow {
            emit(ResponseWrapper.Loading())
            emit(ResponseWrapper.Failed())
        })
}

fun GetProductDetailUseCase.stubSequentiallyReturns(
    first: ProductDetail,
    second: ProductDetail,
) {
    whenever(this.invoke(any(), any<Long>()))
        .thenReturn(
            makeDetailFlow(first),
            makeDetailFlow(second),
        )
}

fun GetProductDetailUseCase.stubReturns(data: ProductDetail) {
    whenever(this.invoke(any(), any<Long>()))
        .thenReturn(makeDetailFlow(data))
}

private fun makeDetailFlow(data: ProductDetail): Flow<ResponseWrapper<ProductDetail, MyBasicError>> =
    flow {
        emit(ResponseWrapper.Loading())
        emit(ResponseWrapper.Succeed(data))
    }