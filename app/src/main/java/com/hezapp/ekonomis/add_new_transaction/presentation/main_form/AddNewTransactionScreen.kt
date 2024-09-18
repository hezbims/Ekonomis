package com.hezapp.ekonomis.add_new_transaction.presentation.main_form

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.add_new_transaction.presentation.main_form.component.ListSelectedProductField
import com.hezapp.ekonomis.add_new_transaction.presentation.main_form.utils.toFormErrorUiModel
import com.hezapp.ekonomis.add_new_transaction.presentation.utils.PercentageVisualTransformation
import com.hezapp.ekonomis.core.domain.entity.support_enum.TransactionType
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.presentation.component.MyErrorText
import com.hezapp.ekonomis.core.presentation.model.MyAppBarState
import com.hezapp.ekonomis.core.presentation.routing.MyRoutes
import com.hezapp.ekonomis.core.presentation.utils.getProfileStringId
import com.hezapp.ekonomis.core.presentation.utils.getTransactionStringId
import com.hezapp.ekonomis.core.presentation.utils.goBackSafely
import com.hezapp.ekonomis.core.presentation.utils.navigateOnce
import com.hezapp.ekonomis.core.presentation.utils.toMyDateString
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun AddNewTransactionScreen(
    navController : NavHostController,
    viewModel : AddNewTransactionViewModel,
    onSubmitSucceed: () -> Unit,
    onNewAppBarState: (MyAppBarState) -> Unit,
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val submitResponse = state.submitResponse
    val context = LocalContext.current

    LaunchedEffect(state.transactionType) {
        onNewAppBarState(
            MyAppBarState(
                title =  {
                    Text(context.getString(R.string.add_new_transaction_content_description))
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (state.transactionType != null)
                                viewModel.onEvent(
                                    AddNewTransactionEvent.ShowQuitConfirmationDialog
                                )
                            else navController.goBackSafely()
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = context.getString(
                                R.string.back_icon_content_description
                            )
                        )
                    }
                }
            )
        )
    }
    LaunchedEffect(submitResponse) {
        when(submitResponse){
            is ResponseWrapper.Failed -> {
                viewModel.onEvent(AddNewTransactionEvent.DoneHandlingSubmitDataResponse)
                val validationResult = submitResponse.error
                if (validationResult != null){
                    viewModel.onEvent(AddNewTransactionEvent.UpdateFormError(
                        validationResult.toFormErrorUiModel(context, state.transactionType!!)
                    ))
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.unknown_error_occured),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            is ResponseWrapper.Succeed -> {
                viewModel.onEvent(AddNewTransactionEvent.DoneHandlingSubmitDataResponse)
                onSubmitSucceed()
            }
            is ResponseWrapper.Loading -> Unit
            null -> Unit
        }
    }

    AddNewTransactionScreen(
        navController = navController,
        state = state,
        onEvent = viewModel::onEvent,
    )

    ConfirmQuitBackHanlder(
        state = state,
        onEvent = viewModel::onEvent,
        navController = navController,
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
                    },
                    error = state.formError.transactionDateError,
                )

                ChooseProfileField(
                    navController = navController,
                    state = state,
                    error = state.formError.profileError,
                )

                if (transactionType == TransactionType.PEMBELIAN)
                    PpnField(
                        value = state.ppn,
                        onValueChange = {
                            onEvent(AddNewTransactionEvent.ChangePpn(it))
                        },
                        error = state.formError.ppnError,
                    )

                ListSelectedProductField(
                    navController = navController,
                    state = state,
                    onEvent = onEvent,
                    modifier = Modifier.padding(top = 12.dp),
                    error = state.formError.invoiceItemsError,
                )
            }

            Spacer(Modifier.height(24.dp))
        }

        state.transactionType?.let {
            Button(
                contentPadding = PaddingValues(vertical = 16.dp),
                enabled = state.submitResponse?.isLoading() != true,
                onClick = {
                    onEvent(AddNewTransactionEvent.SubmitData)
                },
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
    error: String?,
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
        isError = error != null,
        supportingText = MyErrorText(error),
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
    error: String?,
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
            Text(stringResource(R.string.transaction_date_title_label))
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
fun ConfirmQuitBackHanlder(
    state: AddNewTransactionUiState,
    onEvent: (AddNewTransactionEvent) -> Unit,
    navController: NavHostController,
){
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    var isBackPressDone by rememberSaveable { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    BackHandler(
        enabled = isBackPressDone
    ) {
        isBackPressDone = false
        coroutineScope.launch {
            awaitFrame()
            if (state.transactionType != null)
                onEvent(AddNewTransactionEvent.ShowQuitConfirmationDialog)
            else
                onBackPressedDispatcher?.onBackPressed()
            isBackPressDone = true
        }
    }

    val onDismissRequest = { onEvent(AddNewTransactionEvent.DoneShowQuitConfirmationDialog) }

    if (state.showQuitConfirmationDialog)
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text("Yakin ingin kembali?")
            },
            text = {
                Text("Data form sekarang tidak akan disimpan.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        navController.goBackSafely()
                    }
                ) {
                    Text(stringResource(R.string.yes_label))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissRequest
                ) {
                    Text(stringResource(R.string.cancel_label))
                }
            }
        )
}

@Composable
private fun PpnField(
    value : Int?,
    onValueChange : (String) -> Unit,
    error: String?,
){
    TextField(
        value = value?.toString() ?: "",
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.ppn_label)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        visualTransformation = PercentageVisualTransformation(),
        isError = error != null,
        supportingText = MyErrorText(error),
        modifier = Modifier.fillMaxWidth()
    )
}