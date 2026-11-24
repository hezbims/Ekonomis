@file:Suppress("FunctionName")
@file:OptIn(ExperimentalCoroutinesApi::class)

package com.hezapp.ekonomis.test_case.core.domain.utils

import com.hezapp.ekonomis.app_test_utils.core.application.mock.FakePerformanceReporter
import com.hezapp.ekonomis.core.domain.utils.traced
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class FlowPerformanceUnitTest {

    @Test
    fun `When the flow completes then the trace records its attributes and metric and is stopped`() = runTest {
        val reporter = FakePerformanceReporter()

        val emittedValues = flow {
            emit(1)
            emit(2)
        }
            .traced(
                reporter = reporter,
                name = "uc_test",
                attributes = mapOf("only_unpaid" to "true"),
                metricOf = { value -> "row_count" to value.toLong() },
            )
            .toList()

        assertEquals(listOf(1, 2), emittedValues)

        val trace = reporter.recordedTraces.single()
        assertEquals("uc_test", trace.name)
        assertEquals(mapOf("only_unpaid" to "true"), trace.attributes)
        assertEquals(3L, trace.metrics["row_count"])
        assertTrue(trace.stopped)
    }

    @Test
    fun `When the collection is cancelled then the trace is stopped`() = runTest {
        val reporter = FakePerformanceReporter()
        val gate = CompletableDeferred<Unit>()

        val job = launch {
            flow {
                emit(1)
                gate.await()
            }
                .traced(reporter = reporter, name = "uc_cancelled")
                .collect()
        }
        runCurrent()
        job.cancelAndJoin()

        val trace = reporter.recordedTraces.single()
        assertEquals("uc_cancelled", trace.name)
        assertTrue(trace.stopped)
    }

    @Test
    fun `When the flow fails then the trace is stopped and the error is propagated`() = runTest {
        val reporter = FakePerformanceReporter()
        val expectedError = IllegalStateException("boom")
        var caughtError: Throwable? = null

        flow<Int> { throw expectedError }
            .traced(reporter = reporter, name = "uc_failed")
            .catch { caughtError = it }
            .collect()

        assertSame(expectedError, caughtError)
        assertTrue(reporter.recordedTraces.single().stopped)
    }
}
