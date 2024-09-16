package com.hezapp.ekonomis.product_detail.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hezapp.ekonomis.core.domain.entity.support_enum.UnitType
import com.hezapp.ekonomis.core.domain.product.ProductDetail
import com.hezapp.ekonomis.core.domain.product.ProductTransaction
import com.hezapp.ekonomis.core.presentation.component.ResponseLoader
import com.hezapp.ekonomis.core.presentation.utils.getStringId
import com.hezapp.ekonomis.core.presentation.utils.toRupiah
import com.hezapp.ekonomis.core.presentation.utils.toShortDateString
import com.hezapp.ekonomis.ui.theme.EkonomisTheme
import java.util.Calendar

@Composable
fun ProductDetailScreen(
    productId: Int,
    viewModel: ProductDetailViewModel = viewModel {
        ProductDetailViewModel(productId = productId)
    }
){
    LaunchedEffect(Unit) {
        viewModel.onEvent(ProductDetailEvent.LoadDetailProduct)
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value

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

@Composable
private fun ProductDetailScreen(
    productDetail: ProductDetail,
){
    LazyColumn(
        contentPadding = PaddingValues(vertical = 24.dp, horizontal = 12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item { 
            Text(
                productDetail.name,
                style = MaterialTheme.typography.headlineLarge,
            )

            Spacer(Modifier.height(24.dp))
        }

        renderDetailsProductTransactionTable(
            isOutTableTransaction = false,
            productTransactions = productDetail.inProductTransactions,
        )

        item {
            Spacer(Modifier.height(36.dp))
        }

        renderDetailsProductTransactionTable(
            isOutTableTransaction = true,
            productTransactions = productDetail.outProductTransactions,
        )
    }
}

private val colWeight = listOf(2f, 2.5f, 1.5f)
private fun LazyListScope.renderDetailsProductTransactionTable(
    isOutTableTransaction: Boolean,
    productTransactions: List<ProductTransaction>,
){
    item {
        Text(
            "Rincian Barang ${if(isOutTableTransaction) "Keluar" else "Masuk"}",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(6.dp))
    }

    item {
        Row(
            modifier = Modifier
                .border(0.1.dp, color = Color.Black)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(vertical = 12.dp, horizontal = 6.dp)
        ) {
            Text(
                text = "Tanggal",
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(colWeight[0])
            )
            Text(
                text = "Harga ${if (isOutTableTransaction) "Jual" else "Beli"}",
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(colWeight[1])
            )
            Text(
                text = "Jumlah",
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(colWeight[2])
            )
        }
    }

    items(
        productTransactions,
        key = { it.id }
    ){
        Row(
            modifier = Modifier
                .border(width = 0.1.dp, color = Color.Black)
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 6.dp)
        ) {
            Row(
                modifier = Modifier.weight(colWeight[0])
            ){
                Text(
                    text = it.date.toShortDateString(),
                    textAlign = TextAlign.Start,
                )
            }

            Text(
                text = it.totalPrice.toRupiah(),
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(colWeight[1])
            )

            Text(
                text = "${it.quantity} ${stringResource(it.unitType.getStringId())}",
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(colWeight[2])
            )
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
                    name = "White Heinz Vinegar",
                    inProductTransactions = listOf(
                        ProductTransaction(
                            id = 4,
                            date = listDate[0],
                            ppn = null,
                            quantity = 12,
                            totalPrice = (500_000_000).toInt(),
                            unitType = UnitType.CARTON,
                        ),
                        ProductTransaction(
                            id = 5,
                            date = listDate[1],
                            ppn = null,
                            quantity = 150,
                            totalPrice = (500_000).toInt(),
                            unitType = UnitType.PIECE,
                        ),
                        ProductTransaction(
                            id = 6,
                            date = listDate[2],
                            ppn = null,
                            quantity = 1,
                            totalPrice = (45_000).toInt(),
                            unitType = UnitType.CARTON,
                        ),
                    ),
                    outProductTransactions = listOf(
                        ProductTransaction(
                            id = 0,
                            date = listDate[0],
                            ppn = null,
                            quantity = 12,
                            totalPrice = (500_000_000).toInt(),
                            unitType = UnitType.CARTON,
                        ),
                        ProductTransaction(
                            id = 1,
                            date = listDate[1],
                            ppn = null,
                            quantity = 150,
                            totalPrice = (500_000).toInt(),
                            unitType = UnitType.PIECE,
                        ),
                        ProductTransaction(
                            id = 2,
                            date = listDate[2],
                            ppn = null,
                            quantity = 1,
                            totalPrice = (45_000).toInt(),
                            unitType = UnitType.CARTON,
                        ),
                    ),
                )
            )
        }
    }
}
