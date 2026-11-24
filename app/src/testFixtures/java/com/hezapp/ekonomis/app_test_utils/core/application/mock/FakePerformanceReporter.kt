package com.hezapp.ekonomis.app_test_utils.core.application.mock

import com.hezapp.ekonomis.core.domain.utils.IPerformanceReporter
import com.hezapp.ekonomis.core.domain.utils.IPerformanceTrace

class FakePerformanceReporter : IPerformanceReporter {
    val recordedTraces = mutableListOf<RecordedTrace>()

    override fun startTrace(name: String): IPerformanceTrace {
        val record = RecordedTrace(name)
        recordedTraces += record

        return object : IPerformanceTrace {
            override fun putAttribute(key: String, value: String) {
                record.attributes[key] = value
            }

            override fun incrementMetric(name: String, by: Long) {
                record.metrics[name] = (record.metrics[name] ?: 0) + by
            }

            override fun stop() {
                record.stopped = true
            }
        }
    }

    class RecordedTrace(val name: String) {
        val attributes = mutableMapOf<String, String>()
        val metrics = mutableMapOf<String, Long>()
        var stopped = false
    }
}
