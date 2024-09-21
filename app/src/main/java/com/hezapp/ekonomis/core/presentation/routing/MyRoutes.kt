package com.hezapp.ekonomis.core.presentation.routing

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.ui.graphics.vector.ImageVector
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.presentation.routing.MyRoutes.ProductPreview
import com.hezapp.ekonomis.core.presentation.routing.MyRoutes.TransactionHistory
import kotlinx.serialization.Serializable

@Serializable
sealed class MyRoutes {

    @Serializable
    data object TransactionHistory : MyRoutes(), MyBottomNavItem {
        override val icon: ImageVector
            get() = Icons.Filled.History
        override val labelStringId: Int
            get() = R.string.transaction_history_label
    }
    @Serializable
    data object ProductPreview : MyRoutes(), MyBottomNavItem {
        override val icon: ImageVector
            get() = Icons.Outlined.Inventory2
        override val labelStringId: Int
            get() = R.string.product_stock_label
    }

    @Serializable
    class AddOrUpdateTransactionForm(val id : Int?) : MyRoutes()

    @Serializable
    class SearchAndChooseProduct(val invoiceId : Int?) : MyRoutes()

    @Serializable
    class SearchAndChooseProfile(val transactionTypeId : Int, val invoiceId: Int?) : MyRoutes()

    @Serializable
    class DetailProduct(val productId: Int) : MyRoutes()

    object NavGraph {
        @Serializable
        data object AddOrUpdateTransaction
        @Serializable
        data object Transaction
    }

}

val bottomNavBarItems = listOf<MyBottomNavItem>(TransactionHistory, ProductPreview)

interface  MyBottomNavItem {
    val icon : ImageVector
    val labelStringId : Int
}