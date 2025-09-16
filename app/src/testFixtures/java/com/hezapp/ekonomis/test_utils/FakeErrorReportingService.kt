package com.hezapp.ekonomis.test_utils

import android.util.Log
import com.hezapp.ekonomis.core.domain.utils.IErrorReportingService

class FakeErrorReportingService : IErrorReportingService {
    override fun logNonFatalError(
        t: Throwable,
        additionalMessage: Map<String, Any>?
    ) {
        Log.e("qqq test error", "Non fatal error occured", t)
        additionalMessage?.forEach { k , v ->
            Log.e(k, v.toString())
        }
    }
}