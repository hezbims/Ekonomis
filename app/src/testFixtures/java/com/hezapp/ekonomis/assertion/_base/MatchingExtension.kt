package com.hezapp.ekonomis.assertion._base

fun <T> T.matches(expected: AssertionModel<T>){
    expected.matches(this)
}

fun <T> Collection<T>.matchAny(expected: AssertionModel<T>) {
    val errorBuilder = StringBuilder()

    var hasMatch = false
    for (actualItem in this) {
        hasMatch = try {
            actualItem.matches(expected)
            true
        } catch (_: Throwable) {
            false
        }

        if (hasMatch)
            break
    }
    if (hasMatch)
        return

    errorBuilder.appendLine("Expected item has no match : $expected")
    errorBuilder.appendLine("\nActual :")
    this.forEachIndexed { index, t ->
        errorBuilder.appendLine("[${index + 1}] : $t")
    }

    throw AssertionError(errorBuilder.toString())
}

fun <T> Collection<T>.matchAll(expected: Collection<AssertionModel<T>>) {
    val errorBuilder = StringBuilder()
    if (this.size != expected.size)
        errorBuilder.appendLine(
            "Actual size (${this.size}) is not matching expected size (${expected.size})")

    for (expectedItem in expected) {
        var hasMatch = false

        for (actualItem in this) {
            hasMatch = try {
                actualItem.matches(expectedItem)
                true
            } catch (_ : Throwable) {
                false
            }

            if (hasMatch)
                break
        }

        if (hasMatch) continue

        errorBuilder.appendLine("\nExpected item has no match : $expectedItem")
    }

    if (errorBuilder.isEmpty())
        return

    errorBuilder.appendLine("\nActual :")
    this.forEachIndexed { index, t ->
        errorBuilder.appendLine("[${index + 1}] : $t")
    }

    throw AssertionError(errorBuilder.toString())
}