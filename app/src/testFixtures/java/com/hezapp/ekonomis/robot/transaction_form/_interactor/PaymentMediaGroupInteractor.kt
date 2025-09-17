package com.hezapp.ekonomis.robot.transaction_form._interactor

import android.content.Context
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performSemanticsAction
import com.hezapp.ekonomis.core.domain.invoice.entity.PaymentMedia
import com.hezapp.ekonomis.R

class PaymentMediaGroupInteractor(
    private val composeRule: ComposeTestRule,
    private val context: Context,
) {
    fun selectPaymentMedia(paymentMedia: PaymentMedia){
        composeRule.onNode(hasText(getLabel(paymentMedia))
            and SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton))
            .performSemanticsAction(SemanticsActions.OnClick)
    }

    private fun getLabel(paymentMedia: PaymentMedia) : String {
        return when(paymentMedia){
            PaymentMedia.TRANSFER -> context.getString(R.string.transfer_label)
            PaymentMedia.CASH -> context.getString(R.string.cash_label)
        }
    }
}