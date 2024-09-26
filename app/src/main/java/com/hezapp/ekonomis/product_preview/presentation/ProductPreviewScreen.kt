package com.hezapp.ekonomis.product_preview.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hezapp.ekonomis.MyScaffold
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.product.model.PreviewProductSummary
import com.hezapp.ekonomis.core.presentation.component.MyBottomNavBar
import com.hezapp.ekonomis.core.presentation.component.ResponseLoader
import com.hezapp.ekonomis.core.presentation.model.MyScaffoldState
import com.hezapp.ekonomis.core.presentation.routing.MyRoutes
import com.hezapp.ekonomis.core.presentation.utils.getStringId
import com.hezapp.ekonomis.core.presentation.utils.navigateOnce
import com.hezapp.ekonomis.core.presentation.utils.toRupiah
import com.hezapp.ekonomis.ui.theme.EkonomisTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProductPreviewScreen(
    navController : NavHostController,
) {
    val viewModel = koinViewModel<ProductPreviewViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle().value

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.onEvent(ProductPreviewEvent.LoadProducts)
    }
    val appBarState = remember {
        MyScaffoldState(
            bottomBar = {
                MyBottomNavBar(
                    currentIndex = 1,
                    navController = navController,
                )
            }
        ).withTitleText(
            context.getString(R.string.product_preview_title)
        )
    }

    MyScaffold(
        scaffoldState = appBarState,
        navController = navController,
    ) {
        ProductPreviewScreen(
            state = state,
            onEvent = viewModel::onEvent,
            navController = navController,
        )
    }
}

@Composable
private fun ProductPreviewScreen(
    state: ProductPreviewUiState,
    onEvent: (ProductPreviewEvent) -> Unit,
    navController: NavHostController,
){
    ResponseLoader(
        response = state.productsResponse,
        onRetry = {
            onEvent(ProductPreviewEvent.LoadProducts)
        },
        modifier = Modifier.fillMaxSize()
    ) { listData ->
        if (listData.isNotEmpty())
            LazyColumn(
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(listData){ item ->
                    PreviewCardItem(
                        item,
                        onClick = {
                            navController.navigateOnce(
                                MyRoutes.DetailProduct(item.id)
                            )
                        }
                    )
                }
            }
        else
            Text("Anda belum memiliki daftar barang")
    }
}

@Composable
private fun PreviewCardItem(
    item: PreviewProductSummary,
    onClick: () -> Unit,
){
    ListItem(
        headlineContent = {
            Text(item.name)
        },
        supportingContent = {
            Text(
                "Harga pokok : ${
                    item.costOfGoodsSold?.let { costOfGoodsSold ->
                        item.unitType?.let { unitType ->
                            "${costOfGoodsSold.toRupiah()}/${stringResource(unitType.getStringId())}"
                        } ?: "-"
                    } ?: "-"
                }"
            )
        },
        trailingContent = {

        },
        tonalElevation = 0.75.dp,
        modifier = Modifier.clickable {
            onClick()
        }
    )
}

@Preview
@Composable
private fun PreviewProductPreviewScreen(){
    EkonomisTheme {
        Surface {
            val listData = listOf(
                PreviewProductSummary(
                    id = 0,
                    ppn = 11,
                    price = (540_000).toInt(),
                    quantity = 3,
                    name = "BBQ Sauce",
                    unitType = UnitType.CARTON
                ),
                PreviewProductSummary(
                    id = 0,
                    ppn = 11,
                    price = 28_000,
                    quantity = 3,
                    name = "Extra Virgin Olive Oil Tomato Sauce",
                    unitType = UnitType.PIECE
                )
            )

            ProductPreviewScreen(
                state = ProductPreviewUiState(
                    productsResponse = ResponseWrapper.Succeed(listData),
                ),
                onEvent = {},
                navController = rememberNavController(),
            )
        }
    }
}