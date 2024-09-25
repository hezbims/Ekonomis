package com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.input_form_manipulation

import com.hezapp.ekonomis.add_or_update_transaction.domain.model.InvoiceFormModel
import com.hezapp.ekonomis.add_or_update_transaction.domain.model.InvoiceItemsError
import com.hezapp.ekonomis.add_or_update_transaction.domain.model.InvoiceValidationResult
import com.hezapp.ekonomis.add_or_update_transaction.domain.model.PpnError
import com.hezapp.ekonomis.add_or_update_transaction.domain.model.ProfileInputError
import com.hezapp.ekonomis.add_or_update_transaction.domain.model.TransactionDateError
import com.hezapp.ekonomis.add_or_update_transaction.domain.model.TransactionTypeError
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType

class ValidateInvoiceFormSubmission {
    operator fun invoke(invoiceForm: InvoiceFormModel) : InvoiceValidationResult {
        var validationResult = InvoiceValidationResult()

        if (invoiceForm.transactionType == null){
            validationResult = validationResult.copy(
                transactionTypeError = TransactionTypeError.CantBeEmpty
            )
        }

        if (invoiceForm.profile == null){
            validationResult = validationResult.copy(
                profileError = ProfileInputError.CantBeEmpty
            )
        }

        if (invoiceForm.transactionDateMillis == null){
            validationResult = validationResult.copy(
                transactionDateError = TransactionDateError.CantBeEmpty
            )
        }

        // Kalau kita ngejual barang ke orang lain, tidak perlu ada validasi PPN
        if (invoiceForm.transactionType != TransactionType.PENJUALAN && invoiceForm.ppn == null){
            validationResult = validationResult.copy(
                ppnError = PpnError.CantBeEmpty
            )
        }

        if (invoiceForm.newInvoiceItems.isEmpty()){
            validationResult = validationResult.copy(
                invoiceItemsError = InvoiceItemsError.CantBeEmtpy
            )
        }

        return validationResult
    }
}