package com.hezapp.ekonomis.add_new_transaction.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.add_new_transaction.presentation.AddNewTransactionEvent
import com.hezapp.ekonomis.add_new_transaction.presentation.AddNewTransactionUiState
import com.hezapp.ekonomis.core.domain.entity.ProductEntity
import com.hezapp.ekonomis.core.presentation.component.ResponseLoader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAndChooseProductBottomSheet(
    state: AddNewTransactionUiState,
    onEvent: (AddNewTransactionEvent) -> Unit,
    onDismiss: () -> Unit,
    isShowing: Boolean,
){
    if (isShowing) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = onDismiss,
        ) {
            Column(
                modifier = Modifier.padding(
                    top = 8.dp, start = 24.dp, end = 24.dp,
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    IconButton(
                        onClick = onDismiss
                    ) {
                        Icon(Icons.Outlined.Close, contentDescription = "Close")
                    }

                    Text(
                        stringResource(R.string.select_product_label),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(Modifier.width(32.dp))
                }

                var searchText by rememberSaveable { mutableStateOf("") }
                val focusRequester = remember { FocusRequester() }
                LaunchedEffect(searchText) {
                    onEvent(AddNewTransactionEvent.LoadAvailableProductsWithSearchQuery(searchText))
                }
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = searchText,
                    onValueChange = { newValue -> searchText = newValue },
                    trailingIcon = {
                        Icon(Icons.Outlined.Search, contentDescription = "Search Icon")
                    },
                    label = {
                        Text(stringResource(R.string.search_product_name_label))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                )

                ResponseLoader(
                    response = state.availableProducts,
                    onRetry = {
                        onEvent(AddNewTransactionEvent.LoadAvailableProductsWithSearchQuery(searchText))
                    },
                    modifier = Modifier.fillMaxSize()
                ) { data ->
                    LazyColumn(
                        contentPadding = PaddingValues(
                            bottom = 48.dp, top = 24.dp,
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(data){
                            ListAvailableProductCardItem(
                                it,
                                onClick = {

                                }
                            )
                        }
                    }
                }

            }
        }
    }
}

@Composable
private fun ListAvailableProductCardItem(
    item : ProductEntity,
    onClick: () -> Unit,
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Box(Modifier.padding(12.dp)) {
            Text(
                item.name,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}