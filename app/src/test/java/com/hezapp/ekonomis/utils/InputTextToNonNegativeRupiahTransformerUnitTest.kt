package com.hezapp.ekonomis.utils

import com.hezapp.ekonomis.core.presentation.utils.InputTextToNonNegativeRupiahTransformer
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    NonParameterized::class,
    UserInputNonNegativeValueSuite::class
)
class InputTextToNonNegativeRupiahTransformerUnitTest

internal class NonParameterized {
    val inputTextToNonNegativeRupiah = InputTextToNonNegativeRupiahTransformer()

    @Test
    fun `when user input negative value, the resulting integer must be same`(){
        val result = inputTextToNonNegativeRupiah("-1", 1)
        assertThat(result, equalTo(1))
    }


    @Test
    fun `when user input invalid (non integer) string, the resulting integer must be equal to default value`(){
        val result = inputTextToNonNegativeRupiah("1.0", 1)
        assertThat(result, equalTo(1))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `when user input negative integer as default value, it will throw exception`(){
        inputTextToNonNegativeRupiah("1", -1)
    }

    @Test
    fun `when user input empty string, the resulting integer must be null`(){
        val result = inputTextToNonNegativeRupiah("", 1)
        assertThat(result, equalTo(null))
    }
}


@RunWith(Parameterized::class)
internal class UserInputNonNegativeValueSuite(
    private val nonNegativeInteger: Int,
) {
    val inputTextToNonNegativeRupiah = InputTextToNonNegativeRupiahTransformer()

    @Test
    fun `when user input non negative integer string, the resulting integer must be equal to that value`(){
        val result = inputTextToNonNegativeRupiah(nonNegativeInteger.toString(), 9)
        assertThat(result, equalTo(nonNegativeInteger))
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(
                arrayOf(0),
                arrayOf(1),
                arrayOf(2000)
            )
        }
    }
}