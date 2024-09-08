package com.hezapp.ekonomis.add_new_transaction.presentation.component

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.hezapp.ekonomis.add_new_transaction.presentation.search_and_choose_product.SearchAndChooseProductEvent
import com.hezapp.ekonomis.add_new_transaction.presentation.search_and_choose_product.SearchAndChooseProductUiState
import com.hezapp.ekonomis.core.domain.model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.product.InsertProductError

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterNewProductNameBottomSheet(
    onDismiss: () -> Unit,
    state: SearchAndChooseProductUiState,
    onEvent: (SearchAndChooseProductEvent) -> Unit,
    isShowing: Boolean,
){
    if (isShowing){
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = onDismiss,
        ) {
            var productName by rememberSaveable { mutableStateOf("") }
            val focusRequester = remember { FocusRequester() }
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
            val context = LocalContext.current
            val registerNewProductResponse = state.registerNewProductResponse
            var textFieldError by rememberSaveable { mutableStateOf<String?>(null) }
            LaunchedEffect(registerNewProductResponse) {
                when(registerNewProductResponse){
                    is ResponseWrapper.Failed -> {
                        onEvent(SearchAndChooseProductEvent.DoneHandlingRegisterProductResponse)
                        when (registerNewProductResponse.error) {
                            InsertProductError.AlreadyUsed ->
                                textFieldError = context.getString(R.string.name_already_registered)

                            InsertProductError.EmptyInputName ->
                                textFieldError = context.getString(R.string.name_cant_be_empty)

                            null ->
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.unknown_error_occured),
                                    Toast.LENGTH_LONG
                                ).show()
                        }
                    }
                    is ResponseWrapper.Loading -> Unit
                    is ResponseWrapper.Succeed -> {
                        onEvent(SearchAndChooseProductEvent.DoneHandlingRegisterProductResponse)
                        Toast.makeText(
                            context,
                            context.getString(R.string.register_new_product_succeed_label),
                            Toast.LENGTH_SHORT,
                        ).show()
                        onDismiss()
                    }
                    null -> Unit
                }
            }

            Column(
                Modifier.padding(
                    start = 24.dp, end = 24.dp, top = 6.dp, bottom = 24.dp
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back Icon")
                    }

                    Text(
                        stringResource(R.string.new_product_registration_label),
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                    )

                    Spacer(Modifier.width(32.dp))
                }

                Spacer(Modifier.height(24.dp))

                TextField(
                    value = productName,
                    onValueChange = { newProductName ->
                        productName = newProductName
                        textFieldError = null
                    },
                    label = { Text(stringResource(R.string.new_product_name_label)) },
                    isError = textFieldError != null,
                    supportingText = {
                        textFieldError?.let { Text(it) }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )

                OutlinedButton(
                    onClick = {
                        onEvent(
                            SearchAndChooseProductEvent.RegisterNewProduct(
                                productName = productName
                            )
                        )
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Simpan")
                }
            }
        }
    }
}