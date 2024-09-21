package com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hezapp.ekonomis.R

@Composable
fun DeleteTransactionDialog(
    onDismissRequest: () -> Unit,
    onDeleteConfirmed: () -> Unit,
    isShown: Boolean,
){
    if (isShown)
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(stringResource(R.string.delete_transaction_label))
            },
            text = {
                Text(stringResource(R.string.are_you_sure_to_delete_current_transaction))
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissRequest
                ) {
                    Text(stringResource(R.string.cancel_label))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onDeleteConfirmed
                ) {
                    Text(stringResource(R.string.yes_label))
                }
            },
        )
}