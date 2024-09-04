package com.hezapp.ekonomis.add_new_transaction.presentation.component

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.add_new_transaction.presentation.AddNewTransactionEvent
import com.hezapp.ekonomis.add_new_transaction.presentation.AddNewTransactionUiState
import com.hezapp.ekonomis.add_new_transaction.presentation.utils.AddNewTransactionUiUtils
import com.hezapp.ekonomis.core.domain.model.ResponseWrapper
import com.hezapp.ekonomis.core.presentation.component.ResponseLoader

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchAndChoosePersonBottomSheet(
    isShowing : Boolean,
    onDismissBottomSheet : () -> Unit,
    state: AddNewTransactionUiState,
    onEvent : (AddNewTransactionEvent) -> Unit,
){
    if (isShowing) {
        val personTypeString = stringResource(
            AddNewTransactionUiUtils.getPersonIdFromTransactionType(state.transactionType!!)
        )
        var showCreateNewPersonBottomSheet by rememberSaveable { mutableStateOf(false) }

        val createNewPersonLabel = remember {
            buildAnnotatedString {
                append("Nama ${personTypeString.replaceFirstChar { it.lowercase() }} tidak ketemu?\n")
                withLink(
                    link = LinkAnnotation.Clickable(
                        tag = "create-new-person",
                        styles = TextLinkStyles(
                            style = SpanStyle(color = Color.Blue)
                        )
                    ) { link ->
                        showCreateNewPersonBottomSheet = true
                    },
                ) {
                    append("Buat baru disini")
                }
            }
        }

        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var searchText by rememberSaveable { mutableStateOf("") }

        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = onDismissBottomSheet,
        ) {
            
            LaunchedEffect(searchText) {
                onEvent(AddNewTransactionEvent.ChangeSearchQuery(searchText))
            }

            val textFieldFocus = remember { FocusRequester() }
            LaunchedEffect(Unit) {
                textFieldFocus.requestFocus()
            }

            Column(
                modifier = Modifier.padding(
                    start = 24.dp, end = 24.dp, top = 6.dp,
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    IconButton(
                        onClick = onDismissBottomSheet,
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close")
                    }

                    Text(
                        "Pilih $personTypeString",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                    )

                    Spacer(Modifier.width(32.dp))
                }

                Spacer(Modifier.height(24.dp))

                TextField(
                    value = searchText,
                    onValueChange = { newValue -> searchText = newValue },
                    label = { Text("Cari Nama $personTypeString") },
                    trailingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search Icon") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(textFieldFocus),
                )

                ResponseLoader(
                    response = state.availablePerson,
                    onRetry = { onEvent(AddNewTransactionEvent.ChangeSearchQuery(searchText)) },
                    modifier = Modifier.weight(1f),
                ) { listPerson ->
                    if (listPerson.isEmpty())
                        Text(
                            createNewPersonLabel,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    else
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(
                                top = 24.dp, bottom = 64.dp,
                            ),
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            item {
                                Text(
                                    createNewPersonLabel,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 12.dp)
                                )
                            }
                            items(listPerson, key = { data -> data.id }) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onEvent(AddNewTransactionEvent.ChooseNewPerson(it))
                                            onDismissBottomSheet()
                                        }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 12.dp)
                                    ) {
                                        Text(
                                            it.name,
                                            fontSize = 20.sp,
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                }
            }
        }

        val createNewPersonResponse = state.createNewPersonResponse
        val context = LocalContext.current
        LaunchedEffect(createNewPersonResponse) { 
            if (createNewPersonResponse is ResponseWrapper.Succeed){
                onEvent(AddNewTransactionEvent.DoneHandlingSuccessCreateNewProfile)
                onEvent(AddNewTransactionEvent.ChangeSearchQuery(searchQuery = searchText))
                showCreateNewPersonBottomSheet = false
                Toast.makeText(
                    context,
                    context.getString(R.string.success_create_new_profile),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (createNewPersonResponse is ResponseWrapper.Failed) {
                Toast.makeText(
                    context,
                    context.getString(R.string.failed_create_new_profile),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        CreateNewProfileBottomSheet(
            isShowing = showCreateNewPersonBottomSheet,
            onDismiss = { showCreateNewPersonBottomSheet = false },
            onEvent = onEvent,
            state = state,
        )
    }
}