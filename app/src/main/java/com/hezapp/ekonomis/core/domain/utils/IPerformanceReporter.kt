package com.hezapp.ekonomis.core.domain.utils

interface IPerformanceReporter {
    fun startTrace(name: String): IPerformanceTrace
}

interface IPerformanceTrace {
    fun putAttribute(key: String, value: String)
    fun incrementMetric(name: String, by: Long = 1)
    fun stop()
}
