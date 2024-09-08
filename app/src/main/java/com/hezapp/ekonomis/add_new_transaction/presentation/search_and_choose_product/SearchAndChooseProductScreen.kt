package com.hezapp.ekonomis.add_new_transaction.presentation.search_and_choose_product

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.add_new_transaction.presentation.component.RegisterNewProductNameBottomSheet
import com.hezapp.ekonomis.add_new_transaction.presentation.main_form.AddNewTransactionViewModel
import com.hezapp.ekonomis.core.domain.entity.ProductEntity
import com.hezapp.ekonomis.core.presentation.component.ResponseLoader

@Composable
fun SearchAndChooseProductScreen(
    navController : NavHostController,
    addNewTransactionViewModel: AddNewTransactionViewModel,
){
    val searchAndChooseProductViewModel = viewModel<SearchAndChooseProductViewModel>()
    val searchAndChooseProductUiState = searchAndChooseProductViewModel.state.collectAsState().value

    SearchAndChooseProductScreen(
        state = searchAndChooseProductUiState,
        onEvent = searchAndChooseProductViewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchAndChooseProductScreen(
    state: SearchAndChooseProductUiState,
    onEvent: (SearchAndChooseProductEvent) -> Unit,
){
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val secondaryColor = MaterialTheme.colorScheme.secondary
    var showRegisterNewProductNameBottomSheet by rememberSaveable { mutableStateOf(false) }

    val createNewProductLabel = remember {
        buildAnnotatedString {
            append("Nama barang tidak ketemu?\n")
            withLink(
                link = LinkAnnotation.Clickable(
                    tag = "create-new-person",
                    styles = TextLinkStyles(
                        style = SpanStyle(
                            color = secondaryColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                ) {
                    showRegisterNewProductNameBottomSheet = true
                },
            ) {
                append("Daftarkan nama baru disini")
            }
        }
    }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
      modifier = Modifier.padding(
          bottom = 16.dp, start = 24.dp, end = 24.dp,
      )
    ) {
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = {
                onEvent(SearchAndChooseProductEvent.ChangeSearchQuery(it))
            },
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
            response = state.availableProductsResponse,
            onRetry = {
                onEvent(SearchAndChooseProductEvent.LoadAvailableProducts)
            },
            modifier = Modifier.fillMaxSize()
        ) { data ->
            LazyColumn(
                contentPadding = PaddingValues(
                    bottom = 48.dp, top = 12.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                item {
                    Text(
                        createNewProductLabel,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

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


    RegisterNewProductNameBottomSheet(
        onDismiss = { showRegisterNewProductNameBottomSheet = false },
        isShowing = showRegisterNewProductNameBottomSheet,
        onEvent = onEvent,
        state = state,
    )
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