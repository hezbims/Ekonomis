package com.hezapp.ekonomis.core.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.hezapp.ekonomis.core.presentation.routing.MyRoutes

fun NavHostController.goBackSafely(){
    if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED){
        popBackStack()
    }
}

fun NavHostController.navigateOnce(routes: MyRoutes){
    navigate(routes){
        launchSingleTop = true
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.navGraphViewModel(
    navController: NavHostController,
    countParent: Int,
    factory: ViewModelProvider.Factory? = null,
    key: String? = null,
) : T? {
    var graph = destination.parent

    for (i in 2..countParent)
        graph = graph?.parent

    graph?.route?.let {
        val backStackEntry = remember(this) { navController.getBackStackEntry(it) }

        return viewModel(
            viewModelStoreOwner = backStackEntry,
            key = key,
            factory = factory,
        )
    } ?: return null
}