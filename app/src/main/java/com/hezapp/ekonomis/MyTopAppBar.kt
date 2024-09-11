package com.hezapp.ekonomis

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import com.hezapp.ekonomis.core.domain.entity.support_enum.TransactionType
import com.hezapp.ekonomis.core.presentation.routing.MyRoutes
import com.hezapp.ekonomis.core.presentation.utils.getProfileStringId
import com.hezapp.ekonomis.core.presentation.utils.goBackSafely

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    navController : NavHostController,
    navBackStackEntry: NavBackStackEntry?
){
    TopAppBar(
        navigationIcon = {
            navController.previousBackStackEntry?.let {
                IconButton(
                    onClick = {
                        navController.goBackSafely()
                    }
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_icon_content_description)
                    )
                }
            }
        },
        title = {
            navBackStackEntry?.let { backStackEntry ->
                val titleId = MyRoutes.getScreenTitleId(backStackEntry)
                var args = arrayOf<Any>()
                if (backStackEntry.destination.hasRoute<MyRoutes.SearchAndChooseProfile>()){
                    val transactionType = TransactionType.fromId(
                        backStackEntry.toRoute<MyRoutes.SearchAndChooseProfile>().transactionTypeId
                    )
                    args = arrayOf(stringResource(transactionType.getProfileStringId()))
                }
                Text(stringResource(titleId, *args))
            }
        }
    )
}

