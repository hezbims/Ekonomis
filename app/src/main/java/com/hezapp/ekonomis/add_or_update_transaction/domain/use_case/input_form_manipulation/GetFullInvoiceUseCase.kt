package com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.input_form_manipulation

import com.hezapp.ekonomis.core.data.invoice.repo.FakeInvoiceRepo
import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.invoice.relationship.FullInvoiceDetails
import com.hezapp.ekonomis.core.domain.invoice.repo.IInvoiceRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetFullInvoiceUseCase {
    private val repo : IInvoiceRepo = FakeInvoiceRepo()

    operator fun invoke(id : Int) : Flow<ResponseWrapper<FullInvoiceDetails, MyBasicError>> =
    flow<ResponseWrapper<FullInvoiceDetails, MyBasicError>> {
        val result = repo.getFullInvoiceDetails(id)
        emit(ResponseWrapper.Succeed(result))
    }.catch { emit(ResponseWrapper.Failed()) }
}