package com.hezapp.ekonomis.product_detail.presentation

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.monthly_stock.entity.QuantityPerUnitType
import com.hezapp.ekonomis.core.domain.utils.PreviewCalendarProvider
import com.hezapp.ekonomis.core.presentation.component.MyErrorText
import com.hezapp.ekonomis.core.presentation.component.ResponseLoader
import com.hezapp.ekonomis.core.presentation.routing.MyRoutes
import com.hezapp.ekonomis.core.presentation.utils.getStringId
import com.hezapp.ekonomis.core.presentation.utils.toShortMonthYearString
import com.hezapp.ekonomis.product_detail.domain.model.EditMonthlyStockFieldError
import com.hezapp.ekonomis.ui.theme.EkonomisTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun EditCurrentMonthlyStockDialog(
    args: MyRoutes.EditMonthlyStock,
    onMonthlyStockEdited: () -> Unit,
    onDismissRequest: () -> Unit,
){
    val viewModel : EditMonthlyStockDialogViewModel = koinViewModel(
        parameters = {
            parametersOf(
                EditMonthlyStockDialogViewModel.Params(
                    period = args.period,
                    quantityPerUnitType = QuantityPerUnitType(
                        cartonQuantity = args.cartonQuantity,
                        pieceQuantity = args.pieceQuantity
                    ),
                    productId = args.productId,
                    monthlyStockId = args.monthlyStockId,
                )
            )
        }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val saveMonthlyStockResponse = state.saveMonthlyStockResponse
    LaunchedEffect(saveMonthlyStockResponse) {
        when(saveMonthlyStockResponse){
            is ResponseWrapper.Failed -> {
                viewModel.onEvent(EditMonthlyStockDialogEvent.DoneHandlingSaveMonthlyStockResponse)
                if (saveMonthlyStockResponse.error == null)
                    Toast.makeText(
                        context,
                        context.getString(R.string.unknown_error_occured),
                        Toast.LENGTH_SHORT
                    ).show()
            }
            is ResponseWrapper.Succeed -> {
                viewModel.onEvent(EditMonthlyStockDialogEvent.DoneHandlingSaveMonthlyStockResponse)
                onMonthlyStockEdited()
            }
            is ResponseWrapper.Loading -> Unit
            null -> Unit
        }
    }

    EditCurrentMonthlyStockDialog(
        state = state,
        onEvent = viewModel::onEvent,
        period = args.period,
        onDismissRequest = onDismissRequest,
    )
}

@Composable
private fun EditCurrentMonthlyStockDialog(
    state : SetMonthlyStockDialogUiState,
    onEvent: (EditMonthlyStockDialogEvent) -> Unit,
    period: Long,
    onDismissRequest: () -> Unit,
){
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Column {
                Text(
                    text = stringResource(R.string.edit_first_day_of_month_stock),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    "${stringResource(R.string.period_label)} : ${period.toShortMonthYearString()}",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        text = {
            ResponseLoader(
                response = state.quantityResponse,
                onRetry = {}
            ) {
                Column {
                    TextField(
                        value = it.cartonQuantity?.toString() ?: "",
                        onValueChange = {
                            onEvent(EditMonthlyStockDialogEvent.ChangeCartonQuantity(it))
                        },
                        isError = state.cartonError != null,
                        supportingText = MyErrorText(
                            state.cartonError?.let {
                                when(it){
                                    EditMonthlyStockFieldError.FieldEmpty ->
                                        stringResource(
                                            R.string.cant_be_empty,
                                            stringResource(UnitType.CARTON.getStringId())
                                        )
                                }
                            }
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        suffix = { Text(stringResource(UnitType.CARTON.getStringId())) },
                        enabled = !state.useCalculationFromPreviousMonth,
                    )

                    Spacer(Modifier.height(12.dp))

                    TextField(
                        value = it.pieceQuantity?.toString() ?: "",
                        onValueChange = {
                            onEvent(EditMonthlyStockDialogEvent.ChangePieceQuantity(it))
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        suffix = { Text(stringResource(UnitType.PIECE.getStringId())) },
                        isError = state.pieceError != null,
                        supportingText = MyErrorText(
                            state.pieceError?.let {
                                when(it){
                                    EditMonthlyStockFieldError.FieldEmpty ->
                                        stringResource(
                                            R.string.cant_be_empty,
                                            stringResource(UnitType.PIECE.getStringId())
                                        )
                                }
                            }
                        ),
                        enabled = !state.useCalculationFromPreviousMonth,
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            onEvent(EditMonthlyStockDialogEvent.ChangeUseFromPreviousMonthStock)
                        }
                    ) {
                        Checkbox(
                            checked = state.useCalculationFromPreviousMonth,
                            onCheckedChange = {
                                onEvent(EditMonthlyStockDialogEvent.ChangeUseFromPreviousMonthStock)
                            }
                        )
                        
                        Text(
                            text = stringResource(R.string.use_previous_month_calculation),
                            textAlign = TextAlign.Justify,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.cancel_label))
            }
        },
        confirmButton = {
            if (state.quantityResponse.isSucceed())
                TextButton(
                    onClick = {
                        onEvent(
                            EditMonthlyStockDialogEvent.SaveMonthlyStock
                        )
                    }
                ) {
                    Text(stringResource(R.string.save_label))
                }
        }
    )
}

@Preview
@Composable
private fun SetCurrentMonthlyStockDialogPreview(){
    EkonomisTheme {
        Surface {
            EditCurrentMonthlyStockDialog(
                state = SetMonthlyStockDialogUiState(
                    quantityResponse = ResponseWrapper.Succeed(
                        QuantityField(cartonQuantity = 12, pieceQuantity = 2)
                    )
                ),
                onDismissRequest = {},
                period = PreviewCalendarProvider().getCalendar().timeInMillis,
                onEvent = {}
            )
        }
    }
}

