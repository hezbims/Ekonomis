package com.hezapp.ekonomis.add_or_update_transaction.domain.model

import com.hezapp.ekonomis.core.domain.general_model.MyBasicError

data class InvoiceValidationResult (
    val profileError: ProfileInputError? = null,
    val transactionTypeError: TransactionTypeError? = null,
    val ppnError: PpnError? = null,
    val transactionDateError: TransactionDateError? = null,
    val invoiceItemsError: InvoiceItemsError? = null,
) : MyBasicError {
    val hasNoError : Boolean
        get() =
            profileError == null &&
            transactionTypeError == null &&
            ppnError == null &&
            transactionDateError == null &&
            invoiceItemsError == null
}

sealed class ProfileInputError {
    data object CantBeEmpty : ProfileInputError()
}

sealed class TransactionTypeError {
    data object CantBeEmpty : TransactionTypeError()
}

sealed class PpnError {
    data object CantBeEmpty : PpnError()
}

sealed class TransactionDateError {
    data object CantBeEmpty : TransactionDateError()
}

sealed class InvoiceItemsError {
    data object CantBeEmtpy : InvoiceItemsError()
}