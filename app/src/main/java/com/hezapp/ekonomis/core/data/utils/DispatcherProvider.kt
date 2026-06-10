package com.hezapp.ekonomis.core.data.utils

import com.hezapp.ekonomis.core.application.utils.IDispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DispatcherProvider : IDispatcherProvider {
    override val io: CoroutineDispatcher
        get() = Dispatchers.IO
}