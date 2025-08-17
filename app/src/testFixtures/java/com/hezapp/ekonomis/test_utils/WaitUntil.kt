package com.hezapp.ekonomis.test_utils

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.Calendar

fun tryUntilSucceed(
    timeout : Long = TestConstant.SMALL_TIMEOUT,
    body: suspend () -> Unit,
) : Unit =  runBlocking {
    val startTime = Calendar.getInstance().timeInMillis
    var isSucceed = false

    while(Calendar.getInstance().timeInMillis < startTime + timeout && !isSucceed) {
        try {
            body()
            isSucceed = true
        } catch (_: Throwable) {
            delay(50L)
        }
    }

    if (!isSucceed)
        throw RuntimeException("Gagal mencoba berulang kali")
}