package com.hezapp.ekonomis.core.domain.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

/**
 * Mengukur durasi collection sebuah cold Flow, dari mulai dikoleksi sampai
 * selesai / dibatalkan / gagal.
 *
 * Trace selalu di-stop lewat `finally`, sehingga trace tidak "nyangkut" ketika
 * collector dibatalkan (misalnya user pindah layar) dan tidak dibuang oleh
 * Firebase Performance.
 */
fun <T> Flow<T>.traced(
    reporter: IPerformanceReporter,
    name: String,
    attributes: Map<String, String> = emptyMap(),
    metricOf: (T) -> Pair<String, Long>? = { null },
): Flow<T> = flow {
    val trace = reporter.startTrace(name)
    attributes.forEach { (key, value) -> trace.putAttribute(key, value) }

    try {
        emitAll(
            this@traced.onEach { value ->
                metricOf(value)?.let { (metricName, amount) ->
                    trace.incrementMetric(metricName, amount)
                }
            }
        )
    } finally {
        trace.stop()
    }
}
