package com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.input_form_manipulation

import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.invoice.relationship.FullInvoiceDetails
import com.hezapp.ekonomis.core.domain.utils.IErrorReportingService
import com.hezapp.ekonomis.core.transaction.domain.repo.ITransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetFullInvoiceUseCase(
    private val repo : ITransactionRepository,
    private val reportingService: IErrorReportingService,
) {
    operator fun invoke(id : Int) : Flow<ResponseWrapper<FullInvoiceDetails, MyBasicError>> =
    flow<ResponseWrapper<FullInvoiceDetails, MyBasicError>> {
        val result = repo.getFullInvoiceDetails(id)
        emit(ResponseWrapper.Succeed(result))
    }.catch { t ->
        reportingService.logNonFatalError(t, mapOf("id" to id))
        emit(ResponseWrapper.Failed())
    }
}