package com.hezapp.ekonomis.core.presentation.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.core.domain.utils.TimeService
import com.hezapp.ekonomis.ui.theme.EkonomisTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Bentukannya textfield, namun apabila diklik, akan muncul calendar popup untuk memilih tanggal
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlinedDateField(
    value : LocalDate?,
    onValueChange: (LocalDate) -> Unit,
    error: String?,
    label: String,
    timeService: ITimeService,
){
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    val textFieldInteractionSource =
        remember { MutableInteractionSource() }.also { interactionSource ->
            LaunchedEffect(interactionSource) {
                interactionSource.interactions.collect {
                    if (it is PressInteraction.Release)
                        showDatePicker = true
                }
            }
        }

    OutlinedTextField(
        value = value?.let {
            timeService.toEddMMMyyyy(it)
        } ?: "",
        onValueChange = {},
        readOnly = true,
        label = {
            Text(label)
        },
        isError = error != null,
        supportingText = MyErrorText(error),
        interactionSource = textFieldInteractionSource,
        trailingIcon = {
            Icon(
                Icons.Filled.CalendarMonth,
                contentDescription = stringResource(R.string.calendar_icon_content_description)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
    )

    if (showDatePicker){
        val currentDateUtc = ZonedDateTime.of(
            value?.atStartOfDay() ?: LocalDate.now().atStartOfDay(),
            ZoneId.of("UTC"))
        val currentDateUtcInMillis = currentDateUtc.toInstant().toEpochMilli()
        val currentYear = currentDateUtc.year

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = currentDateUtcInMillis,
            yearRange = (currentYear - 5)..(currentYear + 5)
        )
        DatePickerDialog (
            onDismissRequest = { showDatePicker = false },
            dismissButton = {
                TextButton(
                    onClick = {showDatePicker = false}
                ) {
                    Text(stringResource(R.string.cancel_label))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedDateInMillis = datePickerState.selectedDateMillis
                        if (selectedDateInMillis == null)
                            return@TextButton

                        val selectedDate = ZonedDateTime.ofInstant(
                            Instant.ofEpochMilli(selectedDateInMillis),
                            ZoneId.of("UTC")
                        ).toLocalDate()

                        onValueChange(selectedDate)
                        showDatePicker = false
                    }
                ) {
                    Text(stringResource(R.string.choose_label))
                }
            },
        ){
            DatePicker(
                title = {
                    Text(stringResource(R.string.choose_date_title),
                        modifier = Modifier.padding(start = 24.dp, end = 12.dp, top = 16.dp))
                },
                state = datePickerState
            )
        }
    }
}

@Preview
@Composable
private fun PreviewDateField(){
    EkonomisTheme {
        Surface(Modifier.padding(24.dp)) {
            OutlinedDateField(
                value = LocalDate.now()
                    .withYear(2020)
                    .withMonth(3)
                    .withDayOfMonth(13),
                error = null,
                onValueChange = {},
                label = "Tanggal main",
                timeService = TimeService(),
            )
        }
    }
}