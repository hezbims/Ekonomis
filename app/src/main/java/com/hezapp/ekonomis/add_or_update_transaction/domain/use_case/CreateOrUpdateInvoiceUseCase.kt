package com.hezapp.ekonomis.add_or_update_transaction.domain.use_case

import com.hezapp.ekonomis.add_or_update_transaction.domain.model.InvoiceFormModel
import com.hezapp.ekonomis.add_or_update_transaction.domain.model.InvoiceValidationResult
import com.hezapp.ekonomis.core.data.invoice.FakeInvoiceRepo
import com.hezapp.ekonomis.core.data.invoice_item.FakeInvoiceItemRepo
import com.hezapp.ekonomis.core.data.utils.FakeTransactionProvider
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.utils.ITransactionProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class CreateOrUpdateInvoiceUseCase {

    private val getDeletedInvoiceItems = GetDeletedInvoiceItemsUseCase()
    private val validateInvoiceFormSubmission = ValidateInvoiceFormSubmission()
    private val transactionProvider : ITransactionProvider = FakeTransactionProvider()
    private val invoiceRepo = FakeInvoiceRepo()
    private val invoiceItemRepo = FakeInvoiceItemRepo()

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
            val invoiceId =
                if (invoiceForm.isEditing)
                    invoiceRepo.updateInvoice(invoiceForm)
                else
                    invoiceRepo.createNewInvoice(invoiceForm)
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
    }.catch { emit(ResponseWrapper.Failed()) }
}