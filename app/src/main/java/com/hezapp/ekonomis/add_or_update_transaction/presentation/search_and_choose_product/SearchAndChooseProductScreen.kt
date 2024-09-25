package com.hezapp.ekonomis.add_or_update_transaction.presentation.search_and_choose_product

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.hezapp.ekonomis.MyScaffold
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.add_or_update_transaction.presentation.component.SpecifyProductQuantityAndPriceBottomSheet
import com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.AddOrUpdateTransactionEvent
import com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.AddOrUpdateTransactionViewModel
import com.hezapp.ekonomis.add_or_update_transaction.presentation.model.InvoiceItemUiModel
import com.hezapp.ekonomis.add_or_update_transaction.presentation.search_and_choose_product.component.RegisterNewProductNameBottomSheet
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity
import com.hezapp.ekonomis.core.presentation.component.ResponseLoader
import com.hezapp.ekonomis.core.presentation.model.MyScaffoldState
import com.hezapp.ekonomis.core.presentation.utils.goBackSafely
import com.hezapp.ekonomis.core.presentation.utils.rememberIsKeyboardOpen
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchAndChooseProductScreen(
    addOrUpdateTransactionViewModel: AddOrUpdateTransactionViewModel,
    navController : NavHostController,
){
    val searchAndChooseProductViewModel = koinViewModel<SearchAndChooseProductViewModel>()
    val searchAndChooseProductUiState = searchAndChooseProductViewModel.state.collectAsState().value
    val totalSelectedProduct = addOrUpdateTransactionViewModel.state.collectAsStateWithLifecycle().value.curFormData.invoiceItems.size

    val context = LocalContext.current
    val scaffoldState = remember {
        MyScaffoldState().withTitleText(
            context.getString(R.string.select_product_label)
        )
    }

    MyScaffold(
        scaffoldState = scaffoldState,
        navController = navController
    ) {
        SearchAndChooseProductScreen(
            state = searchAndChooseProductUiState,
            onEvent = searchAndChooseProductViewModel::onEvent,
            navController = navController,
            onProductSpecificationConfirmed = {
                addOrUpdateTransactionViewModel.onEvent(
                    AddOrUpdateTransactionEvent.AddNewInvoiceItem(it)
                )
                Toast.makeText(
                    context,
                    "Berhasil memilih barang : ${it.productName}",
                    Toast.LENGTH_SHORT
                ).show()
            },
            totalSelectedProduct = totalSelectedProduct
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SearchAndChooseProductScreen(
    state: SearchAndChooseProductUiState,
    onEvent: (SearchAndChooseProductEvent) -> Unit,
    onProductSpecificationConfirmed: (InvoiceItemUiModel) -> Unit,
    totalSelectedProduct: Int,
    navController: NavHostController,
){
    val secondaryColor = MaterialTheme.colorScheme.secondary
    var showRegisterNewProductNameBottomSheet by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val productNotFoundLabel = remember {
        buildAnnotatedString {
            append(context.getString(R.string.product_name_not_found_question))
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
                append(context.getString(R.string.register_new_product_here_label))
            }
        }
    }
    val productEmptyLabel = remember {
        buildAnnotatedString {
            append("Belum ada daftar barang.\n")
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
                append(context.getString(R.string.register_new_product_here_label))
            }
        }
    }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val isKeyboardOpen by rememberIsKeyboardOpen()
    Column(
      modifier = Modifier
          .fillMaxSize()
          .padding(
              start = 24.dp, end = 24.dp,
              bottom = if (isKeyboardOpen) 0.dp else 48.dp
          )
          .imePadding()
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
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { data ->
            if (data.isEmpty())
                Text(
                    if (state.searchQuery.isEmpty())
                        productEmptyLabel
                    else
                        productNotFoundLabel,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            else
                LazyColumn(
                    contentPadding = PaddingValues(
                        bottom = 48.dp, top = 12.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .imeNestedScroll(),
                ) {
                    item {
                        Text(
                            productNotFoundLabel,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(data){
                        ListAvailableProductCardItem(
                            it,
                            onClick = {
                                onEvent(SearchAndChooseProductEvent
                                    .SelectProductForSpecification(it))
                            }
                        )
                    }
                }
        }

        Button(
            contentPadding = PaddingValues(vertical = 12.dp),
            onClick = {
                navController.goBackSafely()
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                Icons.Outlined.CheckCircleOutline,
                contentDescription = stringResource(R.string.confirm_label)
            )

            Spacer(Modifier.width(4.dp))

            Text("Konfirmasi Pilihan ($totalSelectedProduct barang)")
        }
    }

    SpecifyProductQuantityAndPriceBottomSheet(
        product = state.currentChoosenProduct,
        onDismissRequest = {
            onEvent(SearchAndChooseProductEvent.DoneSelectProductSpecification)
        },
        onProductSpecificationConfirmed = {
            onProductSpecificationConfirmed(it)
        }
    )

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