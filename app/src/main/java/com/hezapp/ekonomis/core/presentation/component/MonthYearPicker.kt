package com.hezapp.ekonomis.core.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.utils.PreviewTimeService
import com.hezapp.ekonomis.core.domain.utils.calendarProvider
import com.hezapp.ekonomis.core.presentation.utils.toShortMonthYearString
import com.hezapp.ekonomis.ui.theme.EkonomisTheme

@Composable
fun MonthYearPicker(
    monthYear: Long,
    onDecrementMonthYear: () -> Unit,
    onIncrementMonthYear: () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = stringResource(R.string.period_label),
){
    Column(
        modifier = modifier,
    ) {
        label?.let {
            Text(
                label,
                style = MaterialTheme.typography.titleSmall,
            )

            Spacer(Modifier.height(4.dp))
        }

        OutlinedCard {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton (
                    onClick = onDecrementMonthYear,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                        contentDescription = stringResource(R.string.decrement_month_and_year_label)
                    )
                }

                val monthYearText = remember(monthYear) {
                    calendarProvider.getCalendar().apply {
                        timeInMillis = monthYear
                    }.timeInMillis.toShortMonthYearString()
                }

                Text(
                    monthYearText,
                    modifier = Modifier.padding(
                        vertical = 10.dp, horizontal = 12.dp,
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
                IconButton (
                    onClick = onIncrementMonthYear,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = stringResource(R.string.increment_month_and_year_label)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewMonthYearPicker(){
    EkonomisTheme {
        Surface {
            MonthYearPicker(
                monthYear = PreviewTimeService().getCalendar().timeInMillis,
                onDecrementMonthYear = {},
                onIncrementMonthYear = {},
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}