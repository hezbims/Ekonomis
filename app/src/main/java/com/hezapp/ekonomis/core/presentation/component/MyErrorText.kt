package com.hezapp.ekonomis.core.presentation.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Suppress("FunctionName")
fun MyErrorText(error: String?) : (@Composable () -> Unit)? =
    if (error == null) null else {{ Text(error) }}