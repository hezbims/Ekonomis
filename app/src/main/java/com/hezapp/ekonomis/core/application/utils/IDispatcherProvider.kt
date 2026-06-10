package com.hezapp.ekonomis.core.application.utils

import kotlinx.coroutines.CoroutineDispatcher

interface IDispatcherProvider {
    val io : CoroutineDispatcher
}