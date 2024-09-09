package com.hezapp.ekonomis.add_new_transaction.presentation.search_and_choose_product.component

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
import com.hezapp.ekonomis.add_new_transaction.presentation.search_and_choose_product.SpecifyProductQuantityEvent
import com.hezapp.ekonomis.add_new_transaction.presentation.search_and_choose_product.SpecifyProductQuantityUiState
import com.hezapp.ekonomis.add_new_transaction.presentation.search_and_choose_product.SpecifyProductQuantityViewModel
import com.hezapp.ekonomis.add_new_transaction.presentation.utils.RupiahVisualTransformation
import com.hezapp.ekonomis.core.domain.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.entity.support_enum.UnitType
import com.hezapp.ekonomis.core.presentation.utils.getStringId
import com.hezapp.ekonomis.core.presentation.utils.toRupiah
import com.hezapp.ekonomis.ui.theme.EkonomisTheme

@Composable
fun SpecifyProductQuantityAndPriceBottomSheet(
    product: ProductEntity?,
    onDismissRequest: () -> Unit,
){
    product?.let {
        val viewModel = remember { SpecifyProductQuantityViewModel(it) }
        SpecifyProductQuantityAndPriceBottomSheet(
            onDismissRequest = onDismissRequest,
            state = viewModel.state.collectAsState().value,
            onEvent = viewModel::onEvent
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SpecifyProductQuantityAndPriceBottomSheet(
    onDismissRequest: () -> Unit,
    state: SpecifyProductQuantityUiState,
    onEvent: (SpecifyProductQuantityEvent) -> Unit,
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

                Spacer(Modifier.width(32.dp))
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
                    error = state.unitTypeError,
                    modifier = Modifier.weight(1f)
                )

                QuantityField(
                    value = state.quantity,
                    onValueChange = {
                        onEvent(SpecifyProductQuantityEvent.ChangeQuantity(it))
                    },
                    error = state.quantityError,
                    modifier = Modifier.weight(1f)
                )
            }

            PriceField(
                value = state.price,
                onValueChange = {
                    onEvent(SpecifyProductQuantityEvent.ChangePrice(it))
                },
                error = state.priceError,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    contentPadding = PaddingValues(
                        vertical = 16.dp
                    ),
                    modifier = Modifier
                        .padding(bottom = 36.dp)
                        .fillMaxWidth(),
                    onClick = {
                        onDismissRequest()
                    }
                ) {
                    Text(stringResource(R.string.choose_label))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownUnitType(
    value: UnitType?,
    onValueChange: (UnitType) -> Unit,
    error: String?,
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
            isError = error != null,
            supportingText = { error?.let { Text(it) } },
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
    error: String?,
    modifier: Modifier,
){
    TextField(
        value = value?.toRupiah() ?: "",
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        supportingText = { error?.let { Text(it) } },
        isError = error != null,
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
    error: String?,
    modifier: Modifier = Modifier,
){
    TextField(
        value = value?.toString() ?: "",
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        supportingText = {
            error?.let { Text(it) }
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
                    state = SpecifyProductQuantityUiState.init(
                        product = ProductEntity(
                            id = 0, name = "Extra Virgin Olive Oil"
                        )
                    ),
                    onEvent = {}
                )
            }
        }
    }
}
