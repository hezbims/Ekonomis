package com.hezapp.ekonomis.add_new_transaction.presentation.main_form

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
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
import androidx.navigation.NavHostController
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.add_new_transaction.presentation.component.ListSelectedProductField
import com.hezapp.ekonomis.add_new_transaction.presentation.utils.PercentageVisualTransformation
import com.hezapp.ekonomis.core.domain.entity.support_enum.TransactionType
import com.hezapp.ekonomis.core.presentation.routing.MyRoutes
import com.hezapp.ekonomis.core.presentation.utils.getProfileStringId
import com.hezapp.ekonomis.core.presentation.utils.getTransactionStringId
import com.hezapp.ekonomis.core.presentation.utils.navigateOnce
import com.hezapp.ekonomis.core.presentation.utils.toMyDateString
import java.util.Calendar

@Composable
fun AddNewTransactionScreen(
    navController : NavHostController,
    viewModel : AddNewTransactionViewModel,
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    AddNewTransactionScreen(
        navController = navController,
        state = state,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun AddNewTransactionScreen(
    navController: NavHostController,
    state : AddNewTransactionUiState,
    onEvent : (AddNewTransactionEvent) -> Unit,
){
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .verticalScroll(state = rememberScrollState())
        ) {
            TransactionTypeDropdown(
                value = state.transactionType,
                onValueChange = { newTransactionType ->
                    onEvent(AddNewTransactionEvent.ChangeTransactionType(newTransactionType))
                },
            )

            state.transactionType?.let { transactionType ->
                ChooseDateField(
                    value = state.transactionDateMillis,
                    onValueChange = { selectedDate ->
                        onEvent(AddNewTransactionEvent.ChangeTransactionDate(selectedDate))
                    }
                )

                ChooseProfileField(
                    navController = navController,
                    state = state,
                )

                if (transactionType == TransactionType.PEMBELIAN)
                    PpnField(
                        value = state.ppn,
                        onValueChange = {
                            onEvent(AddNewTransactionEvent.ChangePpn(it))
                        }
                    )

                ListSelectedProductField(
                    navController = navController,
                    state = state,
                    onEvent = onEvent,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }

            Spacer(Modifier.height(24.dp))
        }

        state.transactionType?.let {
            Button(
                contentPadding = PaddingValues(vertical = 16.dp),
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = 48.dp, start = 24.dp, end = 24.dp
                    )
            ) {
                Text("Simpan")
            }
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
            else stringResource(value.getTransactionStringId()),
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
                        Text(stringResource(transactionType.getTransactionStringId()))
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
private fun ChooseProfileField(
    navController: NavHostController,
    state: AddNewTransactionUiState,
){
    val personTypeString = stringResource(state.transactionType!!.getProfileStringId())

    val textFieldInteractionSource =
        remember(state.transactionType.id) { MutableInteractionSource() }.also { interactionSource ->
            LaunchedEffect(interactionSource) {
                interactionSource.interactions.collect {
                    if (it is PressInteraction.Release)
                        navController.navigateOnce(MyRoutes.SearchAndChooseProfile(state.transactionType.id))
                }
            }
        }

    TextField(
        value = state.profile?.name ?: "",
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