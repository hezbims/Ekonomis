package com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.dto.InstallmentItemUiDto
import com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.view_model.rememberInstallmentItemFormViewModel
import com.hezapp.ekonomis.add_or_update_transaction.presentation.utils.RupiahVisualTransformation
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.core.domain.utils.TimeService
import com.hezapp.ekonomis.core.presentation.component.MyErrorText
import com.hezapp.ekonomis.core.presentation.component.OutlinedDateField
import com.hezapp.ekonomis.ui.theme.EkonomisTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstallmentItemBottomSheetForm(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    onSaveData: (InstallmentItemUiDto) -> Unit,
    timeService: ITimeService,
    initialData: InstallmentItemUiDto? = null,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
){
    if (!visible)
        return

    val viewModel = rememberInstallmentItemFormViewModel(timeService, initialData)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.popBackEvent.collect {
            onSaveData(
                InstallmentItemUiDto(
                    date = state.date,
                    amount = state.amount!!
                )
            )
            onDismissRequest()
        }
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            horizontalAlignment = Alignment.Companion.End,
            modifier = Modifier.Companion.padding(
                start = 24.dp, end = 24.dp, top = 10.dp, bottom = 48.dp
            )
        ) {
            Row(
                verticalAlignment = Alignment.Companion.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.Companion.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onDismissRequest,
                ) {
                    Icon(
                        Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = stringResource(R.string.cancel_edit)
                    )
                }

                Text(
                    text = stringResource(
                        if (initialData == null)
                                R.string.add_new_installment_title
                            else
                                R.string.edit_installment_title
                    ),
                    style = MaterialTheme.typography.headlineSmall,
                )

                Spacer(Modifier.Companion.width(36.dp))
            }

            OutlinedDateField(
                value = state.date,
                onValueChange = {
                    viewModel.changeDate(it)
                },
                error = null,
                label = stringResource(R.string.payment_date),
                timeService = timeService,
            )

            OutlinedTextField(
                value = state.amount?.toString() ?: "",
                onValueChange = {
                    viewModel.changeAmount(it)
                },
                label = {
                    Text(stringResource(R.string.payment_amount))
                },
                isError = state.amountHasError,
                supportingText = MyErrorText(
                    if (state.amountHasError)
                        stringResource(R.string.payment_amount_cant_be_empty)
                    else null
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.validateForm()
                    }
                ),
                visualTransformation = RupiahVisualTransformation(),
                modifier = Modifier.Companion.fillMaxWidth(),
            )

            Spacer(Modifier.Companion.height(12.dp))

            Row {
                OutlinedButton(
                    onClick = onDismissRequest
                ) {
                    Text(stringResource(R.string.cancel_label))
                }

                Spacer(Modifier.Companion.width(6.dp))

                Button(
                    onClick = {
                        viewModel.validateForm()
                    }
                ) {
                    Text(stringResource(R.string.save_label))
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
private fun PreviewInstallmentItemBottomSheetForm(){
    EkonomisTheme {
        Surface(Modifier.Companion.fillMaxSize()) {
            InstallmentItemBottomSheetForm(
                visible = true,
                onDismissRequest = {},
                sheetState = rememberStandardBottomSheetState(
                    initialValue = SheetValue.Expanded
                ),
                onSaveData = {},
                timeService = TimeService(),
            )
        }
    }
}