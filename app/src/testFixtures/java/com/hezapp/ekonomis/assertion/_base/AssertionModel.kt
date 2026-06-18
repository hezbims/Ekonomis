package com.hezapp.ekonomis.assertion._base

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat

abstract class AssertionModel<T> {
    abstract fun matches(actual: T)
    protected fun verifyAll(vararg results: Throwable?) {
        val errors = results.filterNotNull()
        if (errors.isNotEmpty()) {
            val message = errors.joinToString("\n----------\n") { it.message ?: "Assertion failed" }
            throw AssertionError(message)
        }
    }

    protected fun <V> assertEquals(expected: V, actual: V) : Throwable? {
        try {
            assertThat(expected, equalTo(actual))
            return null
        } catch (t: Throwable) {
            // t.stackTrace = t.stackTrace.dropLast(1).toTypedArray()
            return t
        }
    }

    protected fun <V> assertAllMatch(
        actual: Collection<V>,
        expected: Collection<V>,
        matchCriteria: ((actualItem: V, expectedItem: V) -> Boolean)? = null
    ) : Error? {
        val errorBuilder = StringBuilder()
        if (actual.size != expected.size)
            errorBuilder.appendLine(
                "Actual size (${actual.size}) is not matching expected size (${expected.size})")

        var transformedMatchCriteria = matchCriteria
        if (matchCriteria == null)
            transformedMatchCriteria = { actualItem, expectedItem ->
                actualItem == expectedItem
            }

        for (expectedItem in expected) {
            var hasMatch = false

            for (actualItem in actual) {
                if (transformedMatchCriteria(actualItem, expectedItem)){
                    hasMatch = true
                    break
                }
            }

            if (hasMatch) continue

            errorBuilder.appendLine("\nExpected item has no match : $expectedItem")
        }

        if (errorBuilder.isEmpty())
            return null

        errorBuilder.appendLine("\nActual :")
        actual.forEachIndexed { index, t ->
            errorBuilder.appendLine("[${index + 1}] : $t")
        }

        return AssertionError(errorBuilder.toString())
    }
}