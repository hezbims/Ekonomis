package com.hezapp.ekonomis.edit_product_name_dialog.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.presentation.component.MyErrorText
import com.hezapp.ekonomis.core.presentation.utils.ObserveEvent
import com.hezapp.ekonomis.edit_product_name_dialog.application.model.EditProductNameError
import com.hezapp.ekonomis.ui.theme.EkonomisTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun EditProductNameDialog(
    productId: Int,
    onEdited: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val viewModel: EditProductNameDialogViewModel = koinViewModel(
        parameters = { parametersOf(productId) }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveEvent(viewModel.oneTimeEvent) { event ->
        when(event){
            EditProductNameDialogOneTimeEvent.EditSucceed ->
                onEdited()
        }
    }

    EditProductNameDialog(
        state = state,
        onEvent = viewModel::onEvent,
        onDismissRequest = onDismissRequest,
    )
}

@Composable
private fun EditProductNameDialog(
    state: EditProductNameDialogUiState,
    onEvent: (EditProductNameDialogEvent) -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.edit_product_name_title)) },
        text = {
            val submitResponse = state.submitResponse

            Column {
                OutlinedTextField(
                    value = state.nameInput,
                    onValueChange = { onEvent(EditProductNameDialogEvent.ChangeName(it)) },
                    label = { Text(stringResource(R.string.product_name_label)) },
                    isError = submitResponse is ResponseWrapper.Failed,
                    supportingText = MyErrorText(
                        submitResponse?.let { response ->
                            if (response !is ResponseWrapper.Failed)
                                return@let null

                            val error = response.error
                            when (error) {
                                EditProductNameError.EmptyName ->
                                    stringResource(R.string.name_cant_be_empty)
                                EditProductNameError.ProductNameAlreadyExist ->
                                    stringResource(R.string.name_already_used)
                                EditProductNameError.ProductIdNotFound, null ->
                                    stringResource(R.string.unknown_error_occured)
                            }
                        }
                    ),
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.cancel_label))
            }
        },
        confirmButton = {
            TextButton(onClick = { onEvent(EditProductNameDialogEvent.Submit) }) {
                Text(stringResource(R.string.save_label))
            }
        },
    )
}

@Preview
@Composable
fun PreviewEditProductNameDialog(){
    EkonomisTheme {
        Surface {
            EditProductNameDialog(
                state = EditProductNameDialogUiState(),
                onEvent = {},
                onDismissRequest = {},
            )
        }
    }
}