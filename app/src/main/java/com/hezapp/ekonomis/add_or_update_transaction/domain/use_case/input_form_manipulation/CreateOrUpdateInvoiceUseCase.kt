package com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.input_form_manipulation

import android.util.Log
import com.hezapp.ekonomis.add_or_update_transaction.domain.model.InvoiceFormModel
import com.hezapp.ekonomis.add_or_update_transaction.domain.model.InvoiceValidationResult
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.invoice.repo.IInvoiceRepo
import com.hezapp.ekonomis.core.domain.invoice_item.repo.IInvoiceItemRepo
import com.hezapp.ekonomis.core.domain.utils.ITransactionProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class CreateOrUpdateInvoiceUseCase(
    private val transactionProvider : ITransactionProvider,
    private val invoiceRepo : IInvoiceRepo,
    private val invoiceItemRepo : IInvoiceItemRepo,
) {

    // No-Database Interaction
    private val getDeletedInvoiceItems = GetDeletedInvoiceItemsUseCase()
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

        transactionProvider.withTransaction {
            var invoiceId = invoiceRepo.createOrUpdateInvoice(invoiceForm)
            if (invoiceId == -1)
                invoiceId = invoiceForm.id
            val deletedInvoiceItems = getDeletedInvoiceItems(
                oldInvoiceItems = invoiceForm.prevInvoiceItems,
                newInvoiceItem = invoiceForm.newInvoiceItems,
            )
            invoiceItemRepo.deleteInvoiceItems(invoiceItems = deletedInvoiceItems)

            invoiceItemRepo.createOrUpdateInvoiceItems(
                invoiceItems = invoiceForm.newInvoiceItems.map {
                    it.copy(invoiceId = invoiceId)
                }
            )
        }
        emit(ResponseWrapper.Succeed(null))
    }.catch {
        Log.e("qqq", "${it.stackTrace}")
        emit(ResponseWrapper.Failed())
    }
}