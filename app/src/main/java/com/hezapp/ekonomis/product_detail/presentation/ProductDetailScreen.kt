package com.hezapp.ekonomis.product_detail.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.hezapp.ekonomis.MyScaffold
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.monthly_stock.entity.QuantityPerUnitType
import com.hezapp.ekonomis.core.domain.product.model.ProductDetail
import com.hezapp.ekonomis.core.domain.product.model.ProductTransaction
import com.hezapp.ekonomis.core.domain.product.model.TransactionSummary
import com.hezapp.ekonomis.core.domain.utils.PreviewCalendarProvider
import com.hezapp.ekonomis.core.presentation.component.ResponseLoader
import com.hezapp.ekonomis.core.presentation.model.MyScaffoldState
import com.hezapp.ekonomis.core.presentation.preview.PreviewKoin
import com.hezapp.ekonomis.core.presentation.routing.MyRoutes
import com.hezapp.ekonomis.core.presentation.utils.navigateOnce
import com.hezapp.ekonomis.core.presentation.utils.toFullMonthYearString
import com.hezapp.ekonomis.product_detail.presentation.component.ChangePeriodDialog
import com.hezapp.ekonomis.product_detail.presentation.component.CurrentPeriodTransactionSummary
import com.hezapp.ekonomis.product_detail.presentation.component.DetailTransactionCardListItem
import java.util.Calendar

@Composable
fun ProductDetailScreen(
    productId: Int,
    navController: NavHostController,
    viewModel: ProductDetailViewModel,
){
    val context = LocalContext.current
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
                currentPeriod = state.currentPeriod,
                onClickChangePeriodButton = {
                    viewModel.onEvent(ProductDetailEvent.ShowChangePeriodDialog)
                },
                onClickEditMonthlyStock = {
                    navController.navigateOnce(
                        MyRoutes.EditMonthlyStock(
                            cartonQuantity = it.firstDayOfMonthStock.cartonQuantity,
                            pieceQuantity = it.firstDayOfMonthStock.pieceQuantity,
                            monthlyStockId = it.monthlyStockId,
                            period = state.currentPeriod,
                            productId = productId
                        )
                    )
                }
            )
        }
    }

    if (state.showChangePeriodDialog)
        ChangePeriodDialog(
            initialPeriod = state.currentPeriod,
            onConfirmPeriod = {
                viewModel.onEvent(ProductDetailEvent.ChangeCurrentPeriod(it))
            },
            onDismissRequest = {
                viewModel.onEvent(ProductDetailEvent.DismissChangePeriodDialog)
            }
        )
}

@Composable
private fun ProductDetailScreen(
    productDetail: ProductDetail,
    currentPeriod: Long,
    onClickChangePeriodButton: () -> Unit,
    onClickEditMonthlyStock: () -> Unit,
){
    LazyColumn(
        contentPadding = PaddingValues(vertical = 24.dp, horizontal = 24.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.period_label),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = currentPeriod.toFullMonthYearString(),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                OutlinedButton(
                    onClick = onClickChangePeriodButton,
                ) {
                    Text(stringResource(R.string.change_label))
                }
            }

            Spacer(Modifier.height(36.dp))
        }

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

        item {
            CurrentPeriodTransactionSummary(
                productDetail = productDetail,
                onClickEditMonthlyStock = onClickEditMonthlyStock,
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
                text = stringResource(R.string.transaction_histories_doesnt_exists),
                style = MaterialTheme.typography.bodyMedium
            )
        }
}


@Preview
@Composable
private fun PreviewProductDetailScreen(){
    PreviewKoin {
        val listDate = List(3){
            PreviewCalendarProvider().getCalendar().apply {
                set(Calendar.YEAR, 2020)
                set(Calendar.MONTH, 1)
                set(Calendar.DAY_OF_MONTH, 25 + it)
            }.timeInMillis
        }

        ProductDetailScreen(
            onClickChangePeriodButton = {},
            currentPeriod = PreviewCalendarProvider().getCalendar().timeInMillis,
            productDetail = ProductDetail(
                id = 0,
                productName = "White Heinz Vinegar",
                TransactionSummary(
                    firstDayOfMonthStock = QuantityPerUnitType(cartonQuantity = 0, pieceQuantity = 0),
                    inProductTransactions = listOf(
                        ProductTransaction(
                            id = 4,
                            date = listDate[0],
                            ppn = null,
                            quantity = 12,
                            price = (324_000_000).toInt(),
                            unitType = UnitType.CARTON,
                            profileName = "Bu Mega"
                        ),
                        ProductTransaction(
                            id = 5,
                            date = listDate[1],
                            ppn = null,
                            quantity = 150,
                            price = (500_000).toInt(),
                            unitType = UnitType.PIECE,
                            profileName = "Bu Mega"
                        ),
                        ProductTransaction(
                            id = 6,
                            date = listDate[2],
                            ppn = null,
                            quantity = 1,
                            price = (45_000).toInt(),
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
                            price = (500_000_000).toInt(),
                            unitType = UnitType.CARTON,
                            profileName = "Bu Mega"
                        ),
                        ProductTransaction(
                            id = 1,
                            date = listDate[1],
                            ppn = null,
                            quantity = 150,
                            price = (500_000).toInt(),
                            unitType = UnitType.PIECE,
                            profileName = "Bu Mega"
                        ),
                        ProductTransaction(
                            id = 2,
                            date = listDate[2],
                            ppn = null,
                            quantity = 1,
                            price = (45_000).toInt(),
                            unitType = UnitType.CARTON,
                            profileName = "Bu Mega"
                        ),
                    ),
                    monthlyStockId = 1,
                )
            ),
            onClickEditMonthlyStock = {}
        )
    }
}