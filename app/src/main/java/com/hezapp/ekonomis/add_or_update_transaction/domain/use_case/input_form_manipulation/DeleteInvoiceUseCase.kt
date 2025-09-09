package com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.input_form_manipulation

import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.transaction.domain.repo.ITransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class DeleteInvoiceUseCase(
    private val transactionRepo: ITransactionRepository,
) {
    operator fun invoke(invoiceId: Int) : Flow<ResponseWrapper<Any? , MyBasicError>> =
    flow<ResponseWrapper<Any? , MyBasicError>> {
        emit(ResponseWrapper.Loading())
        transactionRepo.delete(invoiceId)
        emit(ResponseWrapper.Succeed(null))
    }.catch { emit(ResponseWrapper.Failed()) }
}