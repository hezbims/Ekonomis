package com.hezapp.ekonomis.base_test_utils.gherkin

interface IGherkinSyntax {
    fun given(block : () -> Unit){
        block.invoke()
    }

    fun `when`(block : () -> Unit){
        block.invoke()
    }

    fun then(block : () -> Unit){
        block.invoke()
    }

    fun and(block : () -> Unit){
        block.invoke()
    }
}