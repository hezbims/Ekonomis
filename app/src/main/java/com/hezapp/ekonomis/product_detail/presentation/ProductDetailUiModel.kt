package com.hezapp.ekonomis.product_detail.presentation

import com.hezapp.ekonomis.core.domain.product.ProductDetail
import com.hezapp.ekonomis.core.domain.product.ProductTransaction

data class ProductDetailUiModel(
    val id : Int,
    val name : String,
    val outProductTransactions : List<ProductTransactionUiModel>,
    val inProductTransactions : List<ProductTransactionUiModel>,
)

data class ProductTransactionUiModel(
    val data : ProductTransaction,
    val isExpanded : Boolean = false,
)

fun ProductDetail.toUiModel() : ProductDetailUiModel =
    ProductDetailUiModel(
        id = id,
        name = productName,
        outProductTransactions = outProductTransactions.map { it.toUiModel() },
        inProductTransactions = inProductTransactions.map { it.toUiModel() }
    )

fun ProductTransaction.toUiModel() : ProductTransactionUiModel =
    ProductTransactionUiModel(
        data = this,
        isExpanded = false,
    )