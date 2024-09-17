package com.hezapp.ekonomis.core.presentation.routing

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.toRoute
import com.hezapp.ekonomis.R
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
    data object SearchAndChooseProduct : MyRoutes()

    @Serializable
    class SearchAndChooseProfile(val transactionTypeId : Int) : MyRoutes()

    @Serializable
    class DetailProduct(val productId: Int) : MyRoutes()

    object NavGraph {
        @Serializable
        data object AddOrUpdateTransaction
        @Serializable
        data object Transaction
    }


    companion object {
        val navigationBarRoutes = listOf<MyBottomNavItem>(TransactionHistory, ProductPreview)

        fun getScreenTitleId(backStackEntry: NavBackStackEntry) : Int{
            val destination = backStackEntry.destination
            if (destination.hasRoute<TransactionHistory>())
                return R.string.transaction_history_title
            else if (destination.hasRoute<ProductPreview>())
                return R.string.product_preview_title
            else if (destination.hasRoute<AddOrUpdateTransactionForm>()){
                val id = backStackEntry.toRoute<AddOrUpdateTransactionForm>().id
                return if (id == null)
                    R.string.add_new_transaction_content_description
                else
                    R.string.edit_transaction_title
            }
            else if (destination.hasRoute<SearchAndChooseProduct>())
                return R.string.select_product_label
            else if (destination.hasRoute<SearchAndChooseProfile>()){
                return R.string.choose_profile_label
            } else if (destination.hasRoute<DetailProduct>()){
                return R.string.detail_product_label
            }
            else throw RuntimeException("Unknown Route Type")
        }
    }
}

interface  MyBottomNavItem {
    val icon : ImageVector
    val labelStringId : Int
}