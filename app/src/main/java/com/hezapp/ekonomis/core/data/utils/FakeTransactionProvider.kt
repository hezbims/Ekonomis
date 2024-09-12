package com.hezapp.ekonomis.core.data.utils

import com.hezapp.ekonomis.core.domain.utils.ITransactionProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class FakeTransactionProvider : ITransactionProvider {

    override suspend fun <R> withTransaction(block: suspend () -> R) {
        mutex.withLock {
            withContext(Dispatchers.IO){
                block()
            }
        }
    }

    private companion object {
        val mutex = Mutex()
    }
}