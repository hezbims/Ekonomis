package com.hezapp.ekonomis.test_utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Calendar

fun tryUntilSucceed(
    timeout : Long = TestConstant.SMALL_TIMEOUT,
    errorMessage: suspend () -> String? = { null },
    body: suspend () -> Unit,
) : Unit =  runBlocking {
    withContext(Dispatchers.IO) {
        val startTime = Calendar.getInstance().timeInMillis
        var isSucceed = false

        while (Calendar.getInstance().timeInMillis < startTime + timeout && !isSucceed) {
            try {
                body()
                isSucceed = true
            } catch (_: Throwable) {
                delay(50L)
            }
        }

        if (!isSucceed)
            throw RuntimeException(errorMessage() ?: "Gagal mencoba berulang kali")
    }
}