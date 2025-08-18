package com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.input_form_manipulation

import android.util.Log
import com.hezapp.ekonomis.add_or_update_transaction.domain.model.InvoiceFormModel
import com.hezapp.ekonomis.add_or_update_transaction.domain.model.InvoiceValidationResult
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.transaction.domain.repo.ITransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class CreateOrUpdateInvoiceUseCase(
    private val transactionRepository: ITransactionRepository,
) {
    private val validateInvoiceFormSubmission = ValidateInvoiceFormSubmission()

    operator fun invoke(invoiceForm : InvoiceFormModel) :
        Flow<ResponseWrapper<Any? , InvoiceValidationResult>> =
    flow<ResponseWrapper<Any? , InvoiceValidationResult>> {
        emit(ResponseWrapper.Loading())

        val validationResult = validateInvoiceFormSubmission(invoiceForm)
        if (!validationResult.hasNoError){
            emit(ResponseWrapper.Failed(validationResult))
            return@flow
        }

        transactionRepository.saveInvoice(invoiceForm.toTransactionEntity())

        emit(ResponseWrapper.Succeed(null))
    }.catch {
        Log.e("qqq", "${it.stackTrace}")
        emit(ResponseWrapper.Failed())
    }
}