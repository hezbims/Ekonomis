package com.hezapp.ekonomis.test_utils

import com.hezapp.ekonomis.core.application.utils.IDispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
class TestDispatcherProvider(
    private val testDispatcher: CoroutineDispatcher = UnconfinedTestDispatcher(),
) : IDispatcherProvider {

    override val io: CoroutineDispatcher
        get() = testDispatcher
}