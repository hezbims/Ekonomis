package com.hezapp.ekonomis.robot.transaction_form._interactor

import android.content.Context
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.robot._interactor.DropdownInteractor

class TransactionTypeDropdownInteractor(
    composeRule: ComposeTestRule,
    matcher: SemanticsMatcher,
    private val context: Context,
) : DropdownInteractor(composeRule, matcher) {

    fun openAndSelectTransactionType(transactionType: TransactionType){
        val stringId = when(transactionType){
            TransactionType.PEMBELIAN -> R.string.purchase_product_label
            TransactionType.PENJUALAN -> R.string.sale_product_label
        }

        openAndSelectValue(context.getString(stringId))
    }
}