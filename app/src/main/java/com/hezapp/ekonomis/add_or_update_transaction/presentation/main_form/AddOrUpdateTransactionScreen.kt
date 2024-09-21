package com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form

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
import androidx.compose.foundation.layout.imePadding
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
import com.hezapp.ekonomis.MyScaffold
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.component.ListSelectedProductField
import com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.utils.toFormErrorUiModel
import com.hezapp.ekonomis.add_or_update_transaction.presentation.utils.PercentageVisualTransformation
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.presentation.component.MyErrorText
import com.hezapp.ekonomis.core.presentation.component.ResponseLoader
import com.hezapp.ekonomis.core.presentation.model.MyScaffoldState
import com.hezapp.ekonomis.core.presentation.routing.MyRoutes
import com.hezapp.ekonomis.core.presentation.utils.getProfileStringId
import com.hezapp.ekonomis.core.presentation.utils.getTransactionStringId
import com.hezapp.ekonomis.core.presentation.utils.goBackSafely
import com.hezapp.ekonomis.core.presentation.utils.navigateOnce
import com.hezapp.ekonomis.core.presentation.utils.rememberIsKeyboardOpen
import com.hezapp.ekonomis.core.presentation.utils.toMyDateString
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun AddOrUpdateTransactionScreen(
    navController : NavHostController,
    onSubmitSucceed: () -> Unit,
    viewModel : AddOrUpdateTransactionViewModel,
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val submitResponse = state.submitResponse
    val context = LocalContext.current

    LaunchedEffect(submitResponse) {
        when(submitResponse){
            is ResponseWrapper.Failed -> {
                viewModel.onEvent(AddOrUpdateTransactionEvent.DoneHandlingSubmitDataResponse)
                val validationResult = submitResponse.error
                if (validationResult != null){
                    viewModel.onEvent(AddOrUpdateTransactionEvent.UpdateFormError(
                        validationResult.toFormErrorUiModel(context, state.curFormData.transactionType!!)
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
                viewModel.onEvent(AddOrUpdateTransactionEvent.DoneHandlingSubmitDataResponse)
                onSubmitSucceed()
            }
            is ResponseWrapper.Loading -> Unit
            null -> Unit
        }
    }

    val scaffoldState = remember(state.curFormData.transactionType) {
        MyScaffoldState(
            title =  {
                Text(
                    context.getString(
                        if (!state.curFormData.isEditing)
                            R.string.add_new_transaction_content_description
                        else
                            R.string.edit_transaction_title
                    )
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        if (state.isFormDataEdited)
                            viewModel.onEvent(
                                AddOrUpdateTransactionEvent.ShowQuitConfirmationDialog
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
    }

    MyScaffold(
        scaffoldState = scaffoldState,
        navController = navController,
    ) {
        ResponseLoader(
            response = state.prevFormData,
            onRetry = {},
            modifier = Modifier.fillMaxSize()
        ) {
            AddOrUpdateTransactionScreen(
                navController = navController,
                state = state,
                onEvent = viewModel::onEvent,
            )
        }

        ConfirmQuitBackHanlder(
            state = state,
            onEvent = viewModel::onEvent,
            navController = navController,
        )
    }
}

@Composable
private fun AddOrUpdateTransactionScreen(
    navController: NavHostController,
    state : AddOrUpdateTransactionUiState,
    onEvent : (AddOrUpdateTransactionEvent) -> Unit,
){
    Column (
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            TransactionTypeDropdown(
                value = state.curFormData.transactionType,
                onValueChange = { newTransactionType ->
                    onEvent(AddOrUpdateTransactionEvent.ChangeTransactionType(newTransactionType))
                },
                isEnabled = !state.curFormData.isEditing
            )

            state.curFormData.transactionType?.let { transactionType ->
                ChooseDateField(
                    value = state.curFormData.transactionDateMillis,
                    onValueChange = { selectedDate ->
                        onEvent(AddOrUpdateTransactionEvent.ChangeTransactionDate(selectedDate))
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
                        value = state.curFormData.ppn,
                        onValueChange = {
                            onEvent(AddOrUpdateTransactionEvent.ChangePpn(it))
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

        state.curFormData.transactionType?.let {
            val isKeyboardOpened by rememberIsKeyboardOpen()
            Button(
                contentPadding = PaddingValues(vertical = 16.dp),
                enabled = state.submitResponse?.isLoading() != true,
                onClick = {
                    onEvent(AddOrUpdateTransactionEvent.SubmitData)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = if (isKeyboardOpened) 0.dp else 48.dp,
                        start = 24.dp, end = 24.dp
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
    isEnabled: Boolean,
){
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            newExpanded ->
                if (isEnabled)
                    expanded = newExpanded
       },
    ) {
        TextField(
            value =
            if (value == null) ""
            else stringResource(value.getTransactionStringId()),
            onValueChange = { },
            readOnly = true,
            label = { Text(stringResource(R.string.choose_transaction_type_label)) },
            enabled = isEnabled,
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
    state: AddOrUpdateTransactionUiState,
){
    val personTypeString = stringResource(state.curFormData.transactionType!!.getProfileStringId())

    val textFieldInteractionSource =
        remember(state.curFormData.transactionType.id) { MutableInteractionSource() }.also { interactionSource ->
            LaunchedEffect(interactionSource) {
                interactionSource.interactions.collect {
                    if (it is PressInteraction.Release)
                        navController.navigateOnce(
                            MyRoutes.SearchAndChooseProfile(
                                transactionTypeId = state.curFormData.transactionType.id,
                            )
                        )
                }
            }
        }

    TextField(
        value = state.curFormData.profile?.name ?: "",
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
    state: AddOrUpdateTransactionUiState,
    onEvent: (AddOrUpdateTransactionEvent) -> Unit,
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
            if (state.isFormDataEdited)
                onEvent(AddOrUpdateTransactionEvent.ShowQuitConfirmationDialog)
            else
                onBackPressedDispatcher?.onBackPressed()
            isBackPressDone = true
        }
    }

    val onDismissRequest = { onEvent(AddOrUpdateTransactionEvent.DoneShowQuitConfirmationDialog) }

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