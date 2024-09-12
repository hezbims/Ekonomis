package com.hezapp.ekonomis.add_new_transaction.presentation.main_form.utils

import android.content.Context
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.add_new_transaction.domain.model.InvoiceItemsError
import com.hezapp.ekonomis.add_new_transaction.domain.model.InvoiceValidationResult
import com.hezapp.ekonomis.add_new_transaction.domain.model.PpnError
import com.hezapp.ekonomis.add_new_transaction.domain.model.ProfileInputError
import com.hezapp.ekonomis.add_new_transaction.domain.model.TransactionDateError
import com.hezapp.ekonomis.add_new_transaction.presentation.main_form.TransactionFormErrorUiModel
import com.hezapp.ekonomis.core.domain.entity.support_enum.TransactionType
import com.hezapp.ekonomis.core.presentation.utils.getProfileStringId

fun InvoiceValidationResult.toFormErrorUiModel(
    context: Context,
    transactionType: TransactionType,
) : TransactionFormErrorUiModel {
    val invoiceItemsError =
        when(this.invoiceItemsError){
            InvoiceItemsError.CantBeEmtpy ->
                context.getString(
                    R.string.cant_be_empty,
                    context.getString(R.string.product_list_label)
                )
            null -> null
        }

    val ppnError =
        when(this.ppnError){
            PpnError.CantBeEmpty ->
                context.getString(
                    R.string.cant_be_empty,
                    context.getString(R.string.ppn_label)
                )
            null -> null
        }

    val profileError =
        when(this.profileError){
            ProfileInputError.CantBeEmpty ->
                context.getString(
                    R.string.cant_be_empty,
                    context.getString(
                        R.string.profile_name_label,
                        context.getString(transactionType.getProfileStringId())
                    )
                )
            null -> null
        }

    val transactionDateError =
        when(this.transactionDateError){
            TransactionDateError.CantBeEmpty ->
                context.getString(
                    R.string.cant_be_empty,
                    context.getString(R.string.transaction_date_label)
                )
            null -> null
        }

    return TransactionFormErrorUiModel(
        invoiceItemsError = invoiceItemsError,
        ppnError = ppnError,
        profileError = profileError,
        transactionDateError = transactionDateError,
    )
}