package com.hezapp.ekonomis.transaction_history.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.invoice.model.PreviewTransactionFilter
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.core.domain.utils.TimeService
import com.hezapp.ekonomis.core.presentation.component.MonthYearPicker
import com.hezapp.ekonomis.transaction_history.presentation.TransactionFilterEvent
import com.hezapp.ekonomis.transaction_history.presentation.rememberTransactionFilterViewModel
import com.hezapp.ekonomis.ui.theme.EkonomisTheme
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFilterBottomSheet(
    initialState: PreviewTransactionFilter,
    onDismiss: () -> Unit,
    onConfirmFilter: (PreviewTransactionFilter) -> Unit,
    timeService : ITimeService,
    sheetState : SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
){
    val viewModel = rememberTransactionFilterViewModel(initialState, timeService)
    val state by viewModel.state.collectAsStateWithLifecycle()

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(0.6f)
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 24.dp, end = 24.dp, top = 6.dp, bottom = 48.dp,
                )
        ) {
            Text(
                stringResource(R.string.filter_transaction_title),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            MonthYearPicker(
                monthYear = state.monthYear,
                onDecrementMonthYear = {
                    viewModel.onEvent(TransactionFilterEvent.DecrementMonthYear)
                },
                onIncrementMonthYear = {
                    viewModel.onEvent(TransactionFilterEvent.IncrementMonthYear)
                },
                timeService = timeService,
            )

            Spacer(Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .toggleable(
                        value = state.isOnlyNotPaidOff,
                        onValueChange = {
                            viewModel.onEvent(
                                TransactionFilterEvent.ChangeOnlyNotPaidOffFilter(it)
                            )
                        },
                        role = Role.Checkbox,
                    ),
            ) {
                Checkbox(
                    checked = state.isOnlyNotPaidOff,
                    onCheckedChange = null,
                )

                Text(stringResource(R.string.only_not_paid_off_label))
            }

            Spacer(Modifier.weight(1f))

            OutlinedButton (
                contentPadding = PaddingValues(vertical = 16.dp),
                onClick = {
                    onConfirmFilter(state)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.apply_label))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun TransactionFilterBottomSheet_Preview(){
    EkonomisTheme {
        Surface {
            TransactionFilterBottomSheet(
                initialState = PreviewTransactionFilter(
                    monthYear = Calendar.getInstance().timeInMillis,
                ),
                onDismiss = { },
                onConfirmFilter = { },
                sheetState = rememberStandardBottomSheetState(
                    initialValue = SheetValue.Expanded,
                ),
                timeService = TimeService(),
            )
        }
    }
}