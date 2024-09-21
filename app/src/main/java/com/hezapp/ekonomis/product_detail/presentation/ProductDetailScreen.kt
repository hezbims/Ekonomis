package com.hezapp.ekonomis.product_detail.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.hezapp.ekonomis.MyScaffold
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.product.model.ProductDetail
import com.hezapp.ekonomis.core.domain.product.model.ProductTransaction
import com.hezapp.ekonomis.core.presentation.component.ResponseLoader
import com.hezapp.ekonomis.core.presentation.model.MyScaffoldState
import com.hezapp.ekonomis.core.presentation.utils.getStringId
import com.hezapp.ekonomis.core.presentation.utils.toMyDateString
import com.hezapp.ekonomis.core.presentation.utils.toRupiah
import com.hezapp.ekonomis.ui.theme.EkonomisTheme
import java.util.Calendar

@Composable
fun ProductDetailScreen(
    productId: Int,
    navController: NavHostController,
    viewModel: ProductDetailViewModel = viewModel {
        ProductDetailViewModel(productId = productId)
    },
){
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.onEvent(ProductDetailEvent.LoadDetailProduct)
    }
    val scaffoldState = remember {
        MyScaffoldState().withTitleText(
            context.getString(R.string.detail_product_label)
        )
    }

    val state = viewModel.state.collectAsStateWithLifecycle().value

    MyScaffold(
        scaffoldState = scaffoldState,
        navController = navController,
    ) {
        ResponseLoader(
            response = state.detailProductResponse,
            onRetry = {
                viewModel.onEvent(ProductDetailEvent.LoadDetailProduct)
            },
            modifier = Modifier.fillMaxSize()
        ) {
            ProductDetailScreen(
                productDetail = it,
            )
        }
    }
}

@Composable
private fun ProductDetailScreen(
    productDetail: ProductDetail,
){
    LazyColumn(
        contentPadding = PaddingValues(vertical = 24.dp, horizontal = 24.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item { 
            Text(
                stringResource(R.string.product_label),
                style = MaterialTheme.typography.titleMedium,
            )

            Text(
                productDetail.productName,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(36.dp))
        }

        renderListProductTransaction(
            isOutTableTransaction = false,
            productTransactions = productDetail.inProductTransactions,
        )

        item {
            Spacer(Modifier.height(36.dp))
        }

        renderListProductTransaction(
            isOutTableTransaction = true,
            productTransactions = productDetail.outProductTransactions,
        )
    }
}

private fun LazyListScope.renderListProductTransaction(
    isOutTableTransaction: Boolean,
    productTransactions: List<ProductTransaction>,
){
    item {
        Text(
            text = "Rincian Barang ${if (isOutTableTransaction) "Keluar" else "Masuk"}",
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(Modifier.height(4.dp))
    }

    itemsIndexed(
        items = productTransactions,
        key = { _, item ->  item.id }
    ){ index, item ->
        DetailTransactionCardListItem(
            item = item,
            isOutTransaction = isOutTableTransaction,
        )

        if (index < productTransactions.lastIndex)
            Spacer(Modifier.height(8.dp))
    }

    if (productTransactions.isEmpty())
        item {
            Text(
                text = "Belum ada catatan transaksi",
                style = MaterialTheme.typography.bodyMedium
            )
        }
}


@Composable
private fun DetailTransactionCardListItem(
    item: ProductTransaction,
    isOutTransaction: Boolean,
    modifier: Modifier = Modifier,
    expandedInitial: Boolean = false,
){
    var expanded by rememberSaveable { mutableStateOf(expandedInitial) }
    val changeExpanded = { expanded = !expanded }

    OutlinedCard (
        onClick = changeExpanded,
        modifier = modifier,
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = item.date.toMyDateString(),
                style = MaterialTheme.typography.bodyMedium
            )

            IconButton(
                onClick = changeExpanded,
            ) {
                Icon(
                    imageVector =
                    if (expanded)
                        Icons.Filled.ArrowDropUp
                    else
                        Icons.Filled.ArrowDropDown,
                    contentDescription = "Dropdown",
                )
            }
        }

        if (expanded)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 26.dp, bottom = 20.dp)
            ) {
                Column {
                    val priceLabel = if (isOutTransaction)
                        stringResource(R.string.selling_price_label)
                    else
                        stringResource(R.string.purchase_price_label)

                    Text(
                        text = stringResource(
                            if (isOutTransaction) R.string.customer_label
                            else R.string.seller_label
                        ),
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = priceLabel,
                        style = MaterialTheme.typography.bodySmall,
                    )

                    Text(
                        text = stringResource(R.string.quantity_label),
                        style = MaterialTheme.typography.bodySmall,
                    )

                    item.ppn?.let {
                        Text(
                            stringResource(R.string.ppn_label),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }

                    Text(
                        if (isOutTransaction)
                            "$priceLabel per ${stringResource(item.unitType.getStringId())}"
                        else
                            "Harga pokok",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                Column {
                    for (i in 1..(if (item.ppn == null) 4 else 5))
                        Text(
                            "  :    ",
                            style = MaterialTheme.typography.bodySmall,
                        )
                }

                Column {
                    Text(
                        text = item.profileName,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = item.totalPrice.toRupiah(),
                        style = MaterialTheme.typography.bodySmall,
                    )

                    Text(
                        text = "${item.quantity} ${stringResource(item.unitType.getStringId())}",
                        style = MaterialTheme.typography.bodySmall,
                    )

                    item.ppn?.let {
                        Text(
                            text = "$it%",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }

                    Text(
                        text = item.costOfGoodsSold.toRupiah(),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

            }

    }
}

@Preview
@Composable
private fun PreviewProductDetailScreen(){
    EkonomisTheme {
        Surface {
            val listDate = List(3){
                Calendar.getInstance().apply {
                    set(Calendar.YEAR, 2020)
                    set(Calendar.MONTH, 1)
                    set(Calendar.DAY_OF_MONTH, 25 + it)
                }.timeInMillis
            }

            ProductDetailScreen(
                productDetail = ProductDetail(
                    id = 0,
                    productName = "White Heinz Vinegar",
                    inProductTransactions = listOf(
                        ProductTransaction(
                                id = 4,
                                date = listDate[0],
                                ppn = null,
                                quantity = 12,
                                totalPrice = (500_000_000).toInt(),
                                unitType = UnitType.CARTON,
                                profileName = "Bu Mega"
                        ),
                        ProductTransaction(
                                id = 5,
                                date = listDate[1],
                                ppn = null,
                                quantity = 150,
                                totalPrice = (500_000).toInt(),
                                unitType = UnitType.PIECE,
                                profileName = "Bu Mega"
                        ),
                        ProductTransaction(
                            id = 6,
                            date = listDate[2],
                            ppn = null,
                            quantity = 1,
                            totalPrice = (45_000).toInt(),
                            unitType = UnitType.CARTON,
                            profileName = "Bu Mega"
                        )
                    ),
                    outProductTransactions = listOf(
                        ProductTransaction(
                            id = 0,
                            date = listDate[0],
                            ppn = 12,
                            quantity = 12,
                            totalPrice = (500_000_000).toInt(),
                            unitType = UnitType.CARTON,
                            profileName = "Bu Mega"
                        ),
                        ProductTransaction(
                            id = 1,
                            date = listDate[1],
                            ppn = null,
                            quantity = 150,
                            totalPrice = (500_000).toInt(),
                            unitType = UnitType.PIECE,
                            profileName = "Bu Mega"
                        ),
                        ProductTransaction(
                            id = 2,
                            date = listDate[2],
                            ppn = null,
                            quantity = 1,
                            totalPrice = (45_000).toInt(),
                            unitType = UnitType.CARTON,
                            profileName = "Bu Mega"
                        ),
                    ),
                )
            )
        }
    }
}

@Preview
@Composable
private fun PreviewExpandedOutTransactionCard(){
    EkonomisTheme {
        Surface {
            DetailTransactionCardListItem(
                item = ProductTransaction(
                    id = 0,
                    date = Calendar.getInstance().timeInMillis,
                    ppn = 11,
                    profileName = "Feni",
                    quantity = 5,
                    totalPrice = 2_500_000,
                    unitType = UnitType.PIECE,
                ),
                isOutTransaction = true,
                expandedInitial = true,
                modifier = Modifier.padding(24.dp),
            )
        }
    }
}