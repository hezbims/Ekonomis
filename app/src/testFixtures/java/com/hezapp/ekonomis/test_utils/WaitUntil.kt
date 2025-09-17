package com.hezapp.ekonomis.test_utils

import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Calendar

/**
 * Ini untuk nunggu IO operation, bukan UI operation (karena pakai `Dispatchers.IO`.
 * Kalau mau nunggu UI, pakai `composeRule.waitUntil`
 */
fun tryUntilSucceed(
    timeout : Long = TestConstant.SMALL_TIMEOUT,
    errorMessage: suspend () -> String? = { null },
    body: suspend () -> Unit,
) : Unit =  runBlocking {
    var latestException: Throwable? = null
    val isSucceed : Boolean = withContext(Dispatchers.IO) {
        val isRobolectric: Boolean = Build.FINGERPRINT.contains("robolectric", true)
        var currentTime = if (isRobolectric) 0 else Calendar.getInstance().timeInMillis
        val endTime = currentTime + timeout
        var isSucceed = false

        while (true) {
            try {
                body()
                isSucceed = true
            } catch (t: Throwable) {
                latestException = t

                currentTime =
                    if (isRobolectric)
                        currentTime + 50
                    else
                        Calendar.getInstance().timeInMillis

                if (currentTime < endTime) {
                    if (!isRobolectric)
                        delay(50L)
                    continue
                }
                else break
            }
        }

        isSucceed
    }

    if (isSucceed)
        return@runBlocking

    val errorMessage = withContext(Dispatchers.IO) { errorMessage() }

    if (errorMessage != null)
        throw RuntimeException(errorMessage())

    throw latestException ?: RuntimeException("Terjadi kesalahan tidak diketahui")
}