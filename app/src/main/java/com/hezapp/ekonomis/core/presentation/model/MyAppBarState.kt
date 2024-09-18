package com.hezapp.ekonomis.core.presentation.model

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

data class MyAppBarState(
    val title: @Composable (() -> Unit) = {},
    val navigationIcon: @Composable (() -> Unit)? = null,
){
    fun withTitleText(text: String) =
        copy(title = { Text(text) })

}
