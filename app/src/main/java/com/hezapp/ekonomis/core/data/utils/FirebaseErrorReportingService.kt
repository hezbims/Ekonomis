package com.hezapp.ekonomis.core.data.utils

import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.crashlytics.recordException
import com.hezapp.ekonomis.core.domain.utils.IErrorReportingService

class FirebaseErrorReportingService : IErrorReportingService {
    override fun logNonFatalError(
        t: Throwable,
        additionalMessage: Map<String, Any?>?,
    ) {
        Firebase.crashlytics.recordException(t){
            additionalMessage?.forEach { k, v ->
                when(v){
                    is Int -> key(k, v)
                    is Long -> key(k, v)
                    is Float -> key(k, v)
                    is Double -> key(k, v)
                    is Boolean -> key(k, v)
                    is String -> key(k, v)
                    else -> key(k, v.toString())
                }
            }
        }
    }
}