package com.hezapp.ekonomis.core.domain.utils

interface IErrorReportingService {
    fun logNonFatalError(t: Throwable, additionalMessage: Map<String, Object>? = null)
}