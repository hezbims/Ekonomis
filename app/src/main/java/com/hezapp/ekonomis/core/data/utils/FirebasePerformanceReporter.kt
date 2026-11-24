package com.hezapp.ekonomis.core.data.utils

import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import com.hezapp.ekonomis.core.domain.utils.IPerformanceReporter
import com.hezapp.ekonomis.core.domain.utils.IPerformanceTrace

class FirebasePerformanceReporter : IPerformanceReporter {
    override fun startTrace(name: String): IPerformanceTrace =
        FirebasePerformanceTrace(FirebasePerformance.getInstance().newTrace(name))
}

private class FirebasePerformanceTrace(
    private val trace: Trace,
) : IPerformanceTrace {
    init {
        trace.start()
    }

    override fun putAttribute(key: String, value: String) {
        trace.putAttribute(key, value)
    }

    override fun incrementMetric(name: String, by: Long) {
        trace.incrementMetric(name, by)
    }

    override fun stop() = trace.stop()
}
