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
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.ParametersDefinition

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

/**
 * Membuat Koin View Model, dimana [ViewModel]-nya akan di host pada
 * parent ke-[countParent] agar bisa digunakan antar composable
 */
@Composable
inline fun <reified T: ViewModel> NavBackStackEntry.koinNavGraphViewModel(
    navController: NavHostController,
    countParent: Int,
    noinline parameters: ParametersDefinition? = null
) : T? {
    var graph = destination.parent

    for (i in 2..countParent)
        graph = graph?.parent

    graph?.route?.let {
        val backStackEntry = remember(this) { navController.getBackStackEntry(it) }

        return koinViewModel(
            viewModelStoreOwner = backStackEntry,
            parameters = parameters,
        )
    } ?: return null
}