package com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.utils

import android.content.Context
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.add_or_update_transaction.domain.model.InvoiceItemsError
import com.hezapp.ekonomis.add_or_update_transaction.domain.model.InvoiceValidationResult
import com.hezapp.ekonomis.add_or_update_transaction.domain.model.PpnError
import com.hezapp.ekonomis.add_or_update_transaction.domain.model.ProfileInputError
import com.hezapp.ekonomis.add_or_update_transaction.domain.model.TransactionDateError
import com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.TransactionFormErrorUiModel
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType

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
                when(transactionType){
                    TransactionType.PEMBELIAN -> context.getString(R.string.seller_cant_be_empty_error)
                    TransactionType.PENJUALAN -> context.getString(R.string.buyer_cant_be_empty_error)
                }
            null ->
                null
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