package com.hezapp.ekonomis.product_detail.presentation.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.core.domain.utils.getNextMonthYear
import com.hezapp.ekonomis.core.domain.utils.getPreviousMonthYear
import com.hezapp.ekonomis.core.presentation.component.MonthYearPicker
import org.koin.compose.koinInject

@Composable
fun ChangePeriodDialog(
    initialPeriod: Long,
    onDismissRequest: () -> Unit,
    onConfirmPeriod: (Long) -> Unit,
    timeService: ITimeService = koinInject(),
){
    var currentPeriod by rememberSaveable { mutableLongStateOf(initialPeriod) }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(stringResource(R.string.change_period_label))
        },
        text = {
            MonthYearPicker(
                monthYear = currentPeriod,
                onIncrementMonthYear = { currentPeriod = currentPeriod.getNextMonthYear(timeService) },
                onDecrementMonthYear = { currentPeriod = currentPeriod.getPreviousMonthYear(timeService) }
            )
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
                onClick = { onConfirmPeriod(currentPeriod) }
            ) {
                Text(stringResource(R.string.confirm_label))
            }
        }
    )
}