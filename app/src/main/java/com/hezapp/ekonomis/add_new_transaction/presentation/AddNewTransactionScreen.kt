package com.hezapp.ekonomis.add_new_transaction.presentation

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.add_new_transaction.presentation.component.SearchAndChoosePersonBottomSheet
import com.hezapp.ekonomis.add_new_transaction.presentation.utils.AddNewTransactionUiUtils
import com.hezapp.ekonomis.add_new_transaction.presentation.utils.PercentageVisualTransformation
import com.hezapp.ekonomis.core.domain.model.TransactionType
import com.hezapp.ekonomis.core.presentation.utils.toMyDateString
import java.util.Calendar

@Composable
fun AddNewTransactionScreen(
    navController : NavHostController,
    viewModel : AddNewTransactionViewModel = viewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    AddNewTransactionScreen(
        navController = navController,
        state = state,
        onEvent = viewModel::onEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddNewTransactionScreen(
    navController: NavHostController,
    state : AddNewTransactionUiState,
    onEvent : (AddNewTransactionEvent) -> Unit,
){
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        TransactionTypeDropdown(
            value = state.transactionType,
            onValueChange = { newTransactionType ->
                onEvent(AddNewTransactionEvent.ChangeTransactionType(newTransactionType))
            },
        )

        if (state.transactionType != null) {
            ChooseDateField(
                value = state.transactionDateMillis,
                onValueChange = { selectedDate ->
                    onEvent(AddNewTransactionEvent.ChangeTransactionDate(selectedDate))
                }
            )

            ChoosePersonField(
                state = state,
                onEvent = onEvent,
            )

            PpnField(
                value = state.ppn,
                onValueChange = {
                    onEvent(AddNewTransactionEvent.ChangePpn(it))
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionTypeDropdown(
    value: TransactionType?,
    onValueChange : (TransactionType) -> Unit,
){
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {newExpanded -> expanded = newExpanded},
    ) {
        TextField(
            value =
            if (value == null) ""
            else stringResource(
                AddNewTransactionUiUtils
                    .getProductTransactionTypeIdFromTransactionType(value)
            ),
            onValueChange = { },
            readOnly = true,
            label = { Text(stringResource(R.string.choose_transaction_type_label)) },
            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = "Dropdown Symbol") },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            val transactionTypes = remember { TransactionType.entries }

            transactionTypes.forEach { transactionType ->
                DropdownMenuItem(
                    text = {
                        Text(
                            stringResource(
                                AddNewTransactionUiUtils
                                    .getProductTransactionTypeIdFromTransactionType(transactionType)
                            )
                        )
                    },
                    onClick = {
                        onValueChange(transactionType)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ChoosePersonField(
    state: AddNewTransactionUiState,
    onEvent: (AddNewTransactionEvent) -> Unit,
){
    val personTypeString = stringResource(
        AddNewTransactionUiUtils.getPersonIdFromTransactionType(state.transactionType!!)
    )
    var showSearchAndChoosePersonBottomSheet by rememberSaveable {
        mutableStateOf(false)
    }

    val textFieldInteractionSource =
        remember { MutableInteractionSource() }.also { interactionSource ->
            LaunchedEffect(interactionSource) {
                interactionSource.interactions.collect {
                    if (it is PressInteraction.Release)
                        showSearchAndChoosePersonBottomSheet = true
                }
            }
        }

    TextField(
        value = state.person?.name ?: "",
        onValueChange = { },
        readOnly = true,
        label = {
            Text(
                "Nama $personTypeString"
            )
        },
        interactionSource = textFieldInteractionSource,
        trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = "Dropdown Symbol") },
        modifier = Modifier
            .fillMaxWidth(),
    )

    SearchAndChoosePersonBottomSheet(
        isShowing = showSearchAndChoosePersonBottomSheet,
        state = state,
        onEvent = onEvent,
        onDismissBottomSheet = { showSearchAndChoosePersonBottomSheet = false }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChooseDateField(
    value : Long?,
    onValueChange: (Long) -> Unit,
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

    TextField(
        value = value?.toMyDateString() ?: "",
        onValueChange = {},
        readOnly = true,
        label = {
            Text(stringResource(R.string.transaction_date_label))
        },
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
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = value ?: Calendar.getInstance().timeInMillis
        )
        DatePickerDialog (
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedDate = datePickerState.selectedDateMillis
                        if (selectedDate != null) {
                            onValueChange(selectedDate)
                            showDatePicker = false
                        }
                    }
                ) {
                    Text(stringResource(R.string.choose_label))
                }
            },
        ){
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun PpnField(
    value : Int?,
    onValueChange : (String) -> Unit,
){
    TextField(
        value = value?.toString() ?: "",
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.ppn_label)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        visualTransformation = PercentageVisualTransformation(),
        modifier = Modifier.fillMaxWidth()
    )
}