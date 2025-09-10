package com.hezapp.ekonomis.robot.transaction_form._interactor

import android.content.Context
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performSemanticsAction
import com.hezapp.ekonomis.add_or_update_transaction.presentation.model.PaymentType
import com.hezapp.ekonomis.R

class PaymentTypeRadioGroupInteractor(
    private val composeRule: ComposeTestRule,
    private val context: Context,
) {
    fun assertSelectedPaymentType(expectedPaymentType: PaymentType){
        val label = getLabelByPaymentType(expectedPaymentType)
        composeRule.onNode(hasText(label) and
                SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton))
            .assertIsSelected()
    }

    private fun getLabelByPaymentType(paymentType: PaymentType) : String {
        return when(paymentType){
            PaymentType.CASH -> context.getString(R.string.cash)
            PaymentType.INSTALLMENT -> context.getString(R.string.installment)
        }
    }

    fun changeSelectedPaymentType(paymentType: PaymentType) {
        val label = getLabelByPaymentType(paymentType)
        composeRule.onNode(hasText(label) and
            SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton))
            .performSemanticsAction(SemanticsActions.OnClick)
    }
}