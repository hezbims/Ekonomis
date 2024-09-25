package com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.input_form_manipulation

import com.hezapp.ekonomis.core.data.invoice.FakeInvoiceRepo
import com.hezapp.ekonomis.core.data.invoice_item.FakeInvoiceItemRepo
import com.hezapp.ekonomis.core.data.database.FakeTransactionProvider
import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.invoice.repo.IInvoiceRepo
import com.hezapp.ekonomis.core.domain.invoice_item.repo.IInvoiceItemRepo
import com.hezapp.ekonomis.core.domain.utils.ITransactionProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class DeleteInvoiceUseCase {
    private val invoiceRepo: IInvoiceRepo = FakeInvoiceRepo()
    private val invoiceItemRepo : IInvoiceItemRepo = FakeInvoiceItemRepo()
    private val transactionProvider : ITransactionProvider = FakeTransactionProvider()

    operator fun invoke(id: Int) : Flow<ResponseWrapper<Any? , MyBasicError>> =
    flow<ResponseWrapper<Any? , MyBasicError>> {
        emit(ResponseWrapper.Loading())
        transactionProvider.withTransaction {
            invoiceItemRepo.deleteInvoiceItems(invoiceId = id)
            invoiceRepo.deleteInvoice(id = id)
        }
        emit(ResponseWrapper.Succeed(null))
    }.catch { emit(ResponseWrapper.Failed()) }
}