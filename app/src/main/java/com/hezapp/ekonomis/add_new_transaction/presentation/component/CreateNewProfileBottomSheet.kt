package com.hezapp.ekonomis.add_new_transaction.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.add_new_transaction.presentation.AddNewTransactionEvent
import com.hezapp.ekonomis.add_new_transaction.presentation.AddNewTransactionUiState
import com.hezapp.ekonomis.add_new_transaction.presentation.utils.AddNewTransactionUiUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNewProfileBottomSheet(
    isShowing : Boolean,
    onDismiss : () -> Unit,
    state: AddNewTransactionUiState,
    onEvent: (AddNewTransactionEvent) -> Unit,
    initialProfileName : String = "",
){
    if (isShowing){
        val userTypeString = stringResource(
            AddNewTransactionUiUtils.getPersonIdFromTransactionType(state.transactionType!!)
        )

        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = onDismiss
        ) {
            var profileName by rememberSaveable { mutableStateOf(initialProfileName) }
            val textFieldFocus = remember { FocusRequester() }
            LaunchedEffect(Unit) {
                textFieldFocus.requestFocus()
            }

            Column(
                modifier = Modifier.padding(
                    start = 24.dp, end = 24.dp, top = 6.dp, bottom = 24.dp
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_label)
                        )
                    }

                    Text(
                        "Buat Profil Baru",
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                    )

                    Spacer(Modifier.width(32.dp))
                }

                Spacer(Modifier.height(24.dp))

                TextField(
                    readOnly = true,
                    enabled = false,
                    value = userTypeString,
                    onValueChange = {},
                    label = { Text(stringResource(R.string.profile_type_label)) },
                    trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = "Dropdown Icon") },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(12.dp))

                TextField(
                    value = profileName,
                    onValueChange = { newValue -> profileName = newValue },
                    label = { Text("Nama $userTypeString") },
                    trailingIcon = { Icon(Icons.Outlined.Person, contentDescription = "Person Icon") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(textFieldFocus),
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    OutlinedButton(
                        onClick = {
                            onEvent(AddNewTransactionEvent.CreateNewProfile(profileName))
                        }
                    ) {
                        Text(stringResource(R.string.save_profile_label))
                    }
                }
            }

        }
    }
}