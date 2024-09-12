package com.hezapp.ekonomis.add_new_transaction.presentation.component

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.add_new_transaction.presentation.model.InvoiceItemUiModel
import com.hezapp.ekonomis.add_new_transaction.presentation.search_and_choose_product.SpecifyProductQuantityEvent
import com.hezapp.ekonomis.add_new_transaction.presentation.search_and_choose_product.SpecifyProductQuantityUiState
import com.hezapp.ekonomis.add_new_transaction.presentation.search_and_choose_product.SpecifyProductQuantityViewModel
import com.hezapp.ekonomis.add_new_transaction.presentation.utils.RupiahVisualTransformation
import com.hezapp.ekonomis.core.domain.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.entity.support_enum.UnitType
import com.hezapp.ekonomis.core.presentation.utils.getStringId
import com.hezapp.ekonomis.ui.theme.EkonomisTheme

@Composable
fun SpecifyProductQuantityAndPriceBottomSheet(
    product: ProductEntity?,
    onDismissRequest: () -> Unit,
    onProductSpecificationConfirmed: (InvoiceItemUiModel) -> Unit,
){
    product?.let {
        val viewModel = remember { SpecifyProductQuantityViewModel(product = it) }
        SpecifyProductQuantityAndPriceBottomSheet(
            viewModel = viewModel,
            onDismissRequest = onDismissRequest,
            onProductSpecificationConfirmed = onProductSpecificationConfirmed,
            onDeleteConfirmed = null,
        )
    }
}

@Composable
fun SpecifyProductQuantityAndPriceBottomSheet(
    invoiceItem: InvoiceItemUiModel,
    onDismissRequest: () -> Unit,
    onProductSpecificationConfirmed: (InvoiceItemUiModel) -> Unit,
    onDeleteConfirmed: () -> Unit,
){
    val viewModel = remember { SpecifyProductQuantityViewModel(invoiceItem) }
    SpecifyProductQuantityAndPriceBottomSheet(
        viewModel = viewModel,
        onDismissRequest = onDismissRequest,
        onProductSpecificationConfirmed = onProductSpecificationConfirmed,
        onDeleteConfirmed = onDeleteConfirmed,
    )
}

@Composable
private fun SpecifyProductQuantityAndPriceBottomSheet(
    viewModel: SpecifyProductQuantityViewModel,
    onDismissRequest: () -> Unit,
    onProductSpecificationConfirmed: (InvoiceItemUiModel) -> Unit,
    onDeleteConfirmed: (() -> Unit)?,
){
    val state = viewModel.state.collectAsState().value
    val isDataValid = state.isDataValid
    LaunchedEffect(isDataValid) {
        if (isDataValid){
            viewModel.onEvent(SpecifyProductQuantityEvent.DoneHandlingValidData)
            onProductSpecificationConfirmed(InvoiceItemUiModel.Factory(state.listId).create(
                id = state.id,
                price = state.price!!,
                productId = state.product.id,
                productName = state.product.name,
                quantity = state.quantity!!,
                unitType = state.unitType!!,
            ))
            onDismissRequest()
        }
    }

    SpecifyProductQuantityAndPriceBottomSheet(
        onDismissRequest = onDismissRequest,
        state = state,
        onEvent = viewModel::onEvent,
        onDeleteConfirmed = onDeleteConfirmed,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SpecifyProductQuantityAndPriceBottomSheet(
    onDismissRequest: () -> Unit,
    state: SpecifyProductQuantityUiState,
    onEvent: (SpecifyProductQuantityEvent) -> Unit,
    onDeleteConfirmed: (() -> Unit)?,
){
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest
    )  {
        Column(
            modifier = Modifier
                .padding(end = 24.dp, start = 24.dp, top = 6.dp)
                .scrollable(rememberScrollState(), Orientation.Vertical)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .fillMaxWidth(),
            ) {
                IconButton(
                    onClick = onDismissRequest,
                ) {
                    Icon(Icons.Rounded.Close, contentDescription = "Close")
                }

                Text(
                    stringResource(R.string.transaction_spesification_label),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                )

                if (onDeleteConfirmed == null)
                    Spacer(Modifier.width(32.dp))
                else
                    IconButton(
                        onClick = onDeleteConfirmed
                    ) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.delete_label),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
            }

            ProductField(
                product = state.product,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                DropdownUnitType(
                    value = state.unitType,
                    onValueChange = {
                        onEvent(SpecifyProductQuantityEvent.ChangeUnitType(it))
                    },
                    hasError = state.unitTypeHasError,
                    modifier = Modifier.weight(1f)
                )

                QuantityField(
                    value = state.quantity,
                    onValueChange = {
                        onEvent(SpecifyProductQuantityEvent.ChangeQuantity(it))
                    },
                    hasError = state.quantityHasError,
                    modifier = Modifier.weight(1f)
                )
            }

            PriceField(
                value = state.price,
                onValueChange = {
                    onEvent(SpecifyProductQuantityEvent.ChangePrice(it))
                },
                hasError = state.priceHasError,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                contentPadding = PaddingValues(
                    vertical = 16.dp
                ),
                modifier = Modifier
                    .padding(bottom = 36.dp, top = 24.dp)
                    .fillMaxWidth(),
                onClick = {
                    onEvent(SpecifyProductQuantityEvent.VerifyProductData)
                }
            ) {
                Text(stringResource(R.string.choose_label))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownUnitType(
    value: UnitType?,
    onValueChange: (UnitType) -> Unit,
    hasError: Boolean,
    modifier: Modifier = Modifier,
){
    var expanded by rememberSaveable { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        TextField(
            value = value?.let {
                stringResource(it.getStringId())
            } ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.unit_label)) },
            trailingIcon = {
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = "Dropdown Symbol"
                )
            },
            isError = hasError,
            supportingText = {
                if (hasError)
                    Text(stringResource(
                        R.string.cant_be_empty,
                        stringResource(R.string.unit_label)
                    ))
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            val unitTypes = remember { UnitType.entries }
            unitTypes.forEach { unitType ->
                DropdownMenuItem(
                    text = {
                        Text(stringResource(unitType.getStringId()))
                    },
                    onClick = {
                        onValueChange(unitType)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun PriceField(
    value : Int?,
    onValueChange: (String) -> Unit,
    hasError: Boolean,
    modifier: Modifier,
){
    TextField(
        value = value?.toString() ?: "",
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        supportingText = {
            if (hasError)
                Text(stringResource(
                    R.string.cant_be_empty,
                    stringResource(R.string.total_price_label)
                ))
        },
        isError = hasError,
        visualTransformation = RupiahVisualTransformation(),
        label = { Text(stringResource(R.string.total_price_label)) },
        modifier = modifier,
    )
}

@Composable
private fun ProductField(
    product: ProductEntity,
    modifier: Modifier = Modifier,
){
    TextField(
        readOnly = true,
        value = product.name,
        onValueChange = {},
        modifier = modifier,
        label = { Text(stringResource(R.string.product_label)) }
    )
}

@Composable
private fun QuantityField(
    value: Int?,
    onValueChange: (String) -> Unit,
    hasError: Boolean,
    modifier: Modifier = Modifier,
){
    TextField(
        value = value?.toString() ?: "",
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        isError = hasError,
        supportingText = {
            if (hasError)
                Text(stringResource(
                    R.string.cant_be_empty,
                    stringResource(R.string.quantity_label)
                ))
        },
        label = { Text(stringResource(R.string.quantity_label)) },
        modifier = modifier,
    )
}

@Preview
@Composable
private fun PreviewSpecifyProductQuantityAndPriceDialog(){
    EkonomisTheme {
        Surface {
            Box(Modifier.padding(48.dp)) {
                SpecifyProductQuantityAndPriceBottomSheet(
                    onDismissRequest = {},
                    state = SpecifyProductQuantityUiState(
                        product = ProductEntity(
                            id = 0, name = "Extra Virgin Olive Oil"
                        ),
                        price = null,
                        quantity = null,
                        unitType = null,
                        listId = null,
                        id = 0,
                    ),
                    onDeleteConfirmed = null,
                    onEvent = {},
                )
            }
        }
    }
}
