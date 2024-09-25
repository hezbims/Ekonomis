package com.hezapp.ekonomis.transaction_history.domain.use_case

import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.invoice.model.PreviewTransactionFilter
import com.hezapp.ekonomis.core.domain.invoice.model.PreviewTransactionHistory
import com.hezapp.ekonomis.core.domain.invoice.repo.IInvoiceRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetPreviewTransactionHistoryUseCase(
    private val repo : IInvoiceRepo
) {

    operator fun invoke(filter: PreviewTransactionFilter) :
    Flow<ResponseWrapper<List<PreviewTransactionHistory>, MyBasicError>> =
    flow<ResponseWrapper<List<PreviewTransactionHistory>, MyBasicError>> {
        emit(ResponseWrapper.Loading())
        emit(ResponseWrapper.Succeed(repo.getPreviewInvoices(filter)))
    }.catch {
        emit(ResponseWrapper.Failed())
    }
}