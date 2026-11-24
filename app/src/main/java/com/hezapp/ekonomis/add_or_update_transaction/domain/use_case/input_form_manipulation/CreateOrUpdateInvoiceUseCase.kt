package com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.input_form_manipulation

import com.hezapp.ekonomis.add_or_update_transaction.domain.model.InvoiceFormModel
import com.hezapp.ekonomis.add_or_update_transaction.domain.model.InvoiceValidationResult
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.utils.IErrorReportingService
import com.hezapp.ekonomis.core.domain.utils.IPerformanceReporter
import com.hezapp.ekonomis.core.domain.utils.traced
import com.hezapp.ekonomis.core.transaction.domain.repo.ITransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

interface ICreateOrUpdateInvoiceUseCase {
    operator fun invoke(invoiceForm : InvoiceFormModel) :
            Flow<ResponseWrapper<Any?, InvoiceValidationResult>>
}

class CreateOrUpdateInvoiceUseCase(
    private val transactionRepository: ITransactionRepository,
    private val reportingService: IErrorReportingService,
    private val performanceReporter: IPerformanceReporter,
) : ICreateOrUpdateInvoiceUseCase {
    override operator fun invoke(invoiceForm : InvoiceFormModel) :
        Flow<ResponseWrapper<Any? , InvoiceValidationResult>> =
    flow<ResponseWrapper<Any? , InvoiceValidationResult>> {
        emit(ResponseWrapper.Loading())

        transactionRepository.saveInvoice(invoiceForm.toTransactionEntity())

        emit(ResponseWrapper.Succeed(null))
    }
    .traced(
        reporter = performanceReporter,
        name = TRACE_NAME,
        attributes = mapOf(
            "transaction_type" to (invoiceForm.transactionType?.name ?: "null"),
            "profile" to (invoiceForm.profile?.name ?: "null"),
            "has_installment" to (invoiceForm.installment != null).toString(),
        ),
        metricOf = { response ->
            if (response is ResponseWrapper.Succeed<*, *>)
                "item_count" to invoiceForm.newInvoiceItems.size.toLong()
            else null
        },
    )
    .catch { t ->
        val installmentLogMap = mutableMapOf<String , Any?>()
        invoiceForm.installment?.let { installment ->
            installmentLogMap["is_paid_off"] = installment.isPaidOff
            installment.items.forEachIndexed { index, item ->
                installmentLogMap["installment_item[$index].amount"] = item.amount
                installmentLogMap["installment_item[$index].date"] = item.paymentDate
            }
        }

        val invoiceItemsLogMap = mutableMapOf<String, Any?>()
        invoiceForm.newInvoiceItems.forEachIndexed {  index, item ->
            invoiceItemsLogMap["invoice_item[$index].productId"] = item.productId
            invoiceItemsLogMap["invoice_item[$index].quantity"] = item.quantity
            invoiceItemsLogMap["invoice_item[$index].price"] = item.price
            invoiceItemsLogMap["invoice_item[$index].unitType"] = item.unitType
        }

        reportingService.logNonFatalError(t , mapOf(
            "id" to invoiceForm.id,
            "transactionType" to invoiceForm.transactionType?.name,
            "profile" to invoiceForm.profile?.name,
            "transactionDateMillis" to invoiceForm.transactionDateMillis,
            "ppn" to invoiceForm.ppn,
        ) + invoiceItemsLogMap + installmentLogMap)
        emit(ResponseWrapper.Failed())
    }

    companion object {
        private const val TRACE_NAME = "uc_save_invoice"
    }
}
