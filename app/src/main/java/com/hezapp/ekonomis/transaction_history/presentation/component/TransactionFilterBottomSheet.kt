package com.hezapp.ekonomis.transaction_history.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.invoice.model.PreviewTransactionFilter
import com.hezapp.ekonomis.core.presentation.component.MonthYearPicker
import com.hezapp.ekonomis.transaction_history.presentation.TransactionFilterEvent
import com.hezapp.ekonomis.transaction_history.presentation.rememberTransactionFilterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFilterBottomSheet(
    initialState: PreviewTransactionFilter,
    onDismiss: () -> Unit,
    onConfirmFilter: (PreviewTransactionFilter) -> Unit,
){
    val viewModel = rememberTransactionFilterViewModel(initialState)
    val state by viewModel.state.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
                "Filter Transaksi",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                MonthYearPicker(
                    monthYear = state.monthYear,
                    onDecrementMonthYear = {
                        viewModel.onEvent(TransactionFilterEvent.DecrementMonthYear)
                    },
                    onIncrementMonthYear = {
                        viewModel.onEvent(TransactionFilterEvent.IncrementMonthYear)
                    }
                )
            }

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