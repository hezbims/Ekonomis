package com.hezapp.ekonomis.core.presentation.utils

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController

fun NavHostController.goBackSafely(){
    if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED){
        popBackStack()
    }
}