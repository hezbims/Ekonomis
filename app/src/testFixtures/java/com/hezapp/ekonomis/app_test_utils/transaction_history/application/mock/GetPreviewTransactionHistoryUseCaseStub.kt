package com.hezapp.ekonomis.app_test_utils.transaction_history.application.mock

import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.transaction_history.application.use_case.iface.IGetPreviewTransactionHistoryUseCase
import kotlinx.coroutines.flow.flow
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

fun IGetPreviewTransactionHistoryUseCase.stubReturnLoadingOnly() : IGetPreviewTransactionHistoryUseCase {
    whenever(this.invoke(any()))
        .thenReturn(flow { emit(ResponseWrapper.Loading()) })
    return this
}