package com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.hezapp.ekonomis.R

@Composable
fun ConfirmUnsafePaymentTypeEditDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
){
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(stringResource(R.string.yes_label))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.no_label))
            }
        },
        text = {
            Text(stringResource(
                R.string.changing_payment_from_installment_to_cash_confirmation),
                textAlign = TextAlign.Justify)
        }
    )
}