package com.hezapp.ekonomis.test_utils.rule

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class TestRepeatRule(
    private val times: Int
) : TestRule {

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                repeat(times) { iteration ->
                    println("GGG Running iteration ${iteration + 1} : ${description.methodName}")

                    base.evaluate()

                    println("GGG iteration ${iteration + 1} ended : ${description.methodName}")
                }
            }
        }
    }
}