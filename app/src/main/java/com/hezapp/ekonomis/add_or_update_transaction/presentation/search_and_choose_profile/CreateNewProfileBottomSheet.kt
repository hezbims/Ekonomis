package com.hezapp.ekonomis.add_or_update_transaction.presentation.search_and_choose_profile

import android.widget.Toast
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.profile.model.CreateNewProfileError
import com.hezapp.ekonomis.core.presentation.utils.getProfileStringId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNewProfileBottomSheet(
    isShowing : Boolean,
    onDismiss : () -> Unit,
    state: SearchAndChooseProfileUiState,
    onEvent: (SearchAndChooseProfileEvent) -> Unit,
    initialProfileName : String = "",
){
    if (isShowing){
        val userTypeString = stringResource(state.transactionType.getProfileStringId())

        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var textFieldError by rememberSaveable { mutableStateOf<String?>(null) }

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
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
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
                    onValueChange = { newValue ->
                        profileName = newValue
                        textFieldError = null
                    },
                    label = {
                        Text(stringResource(R.string.profile_name_label, userTypeString))
                    },
                    isError = textFieldError != null,
                    supportingText = {
                        textFieldError?.let {
                            Text(it)
                        }
                    },
                    trailingIcon = { Icon(Icons.Outlined.Person, contentDescription = "Person Icon") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(textFieldFocus),
                )

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    OutlinedButton(
                        onClick = {
                            onEvent(SearchAndChooseProfileEvent.CreateNewProfile(profileName))
                        }
                    ) {
                        Text(stringResource(R.string.save_profile_label))
                    }
                }
            }

            val createNewPersonResponse = state.createNewProfileResponse
            val context = LocalContext.current
            LaunchedEffect(createNewPersonResponse) {
                when (createNewPersonResponse) {
                    is ResponseWrapper.Succeed -> {
                        onEvent(SearchAndChooseProfileEvent.DoneHandlingCreateNewProfileResponse)
                        onEvent(SearchAndChooseProfileEvent.LoadAvailableProfiles)
                        onDismiss()
                        Toast.makeText(
                            context,
                            context.getString(R.string.success_create_new_profile),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ResponseWrapper.Failed -> {
                        onEvent(SearchAndChooseProfileEvent.DoneHandlingCreateNewProfileResponse)
                        when(createNewPersonResponse.error){
                            CreateNewProfileError.NameCantBeEmpty ->
                                textFieldError = context.getString(R.string.name_cant_be_empty)
                            CreateNewProfileError.NameAlreadyExist ->
                                textFieldError = context.getString(R.string.name_already_used)
                            null ->
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.unknown_error_occured),
                                    Toast.LENGTH_SHORT
                                ).show()

                        }
                    }
                    is ResponseWrapper.Loading -> Unit
                    null -> Unit
                }
        }
    }


    }
}