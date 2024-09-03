package com.hezapp.ekonomis.add_new_transaction.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.add_new_transaction.presentation.utils.AddNewTransactionUiUtils
import com.hezapp.ekonomis.core.domain.model.TransactionType

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
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        var isDropdownTransactionTypeExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = isDropdownTransactionTypeExpanded,
            onExpandedChange = {newExpanded -> isDropdownTransactionTypeExpanded = newExpanded},
        ) {
            TextField(
                value =
                if (state.transactionType == null)
                    ""
                else stringResource(
                    AddNewTransactionUiUtils
                        .getLabelIdFromTransactionType(state.transactionType)
                ),
                onValueChange = { },
                readOnly = true,
                label = { Text(stringResource(R.string.choose_transaction_type_label)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = isDropdownTransactionTypeExpanded,
                onDismissRequest = { isDropdownTransactionTypeExpanded = false }
            ) {

                TransactionType.entries.forEach { transactionType ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                stringResource(
                                    AddNewTransactionUiUtils
                                    .getLabelIdFromTransactionType(transactionType)
                                )
                            )
                        },
                        onClick = {
                            onEvent(AddNewTransactionEvent.ChangeTransactionType(transactionType))
                        },
                    )
                }
            }
        }
    }
}