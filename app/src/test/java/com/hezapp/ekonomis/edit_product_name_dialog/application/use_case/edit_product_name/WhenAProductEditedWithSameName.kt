package com.hezapp.ekonomis.edit_product_name_dialog.application.use_case.edit_product_name

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner

/**
 * EDGE CASE
 */
@Suppress("Junit4RunWithInspection")
@RunWith(ParameterizedRobolectricTestRunner::class)
class WhenAProductEditedWithSameName(
    private val productStartName : String,
    private val editName : String,
    private val productNameAfterEdit : String,
): _BaseEditProductNameUnitTest() {
    companion object {
        @Suppress("Unused")
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0} , {1} , {2}")
        fun parameter() = listOf(
            arrayOf("Crab", "Crab", "Crab"),
            arrayOf("Meat", "\t\nmeAt   \n ", "meAt")
        )
    }

    @Test
    fun `Should resulting in succeed`(){
        given {
            userHasProductWithNames(productStartName)
        }

        `when` {
            // can not edit (case-insensitive with trimmed)
            userEditProduct(withName = productStartName, toName = editName)
        }

        then {
            productsCountInDatabaseShouldBe(1)
            databaseShouldContainsProductWithNames(productNameAfterEdit)
            useCaseResultShouldBeSucceed()
        }
    }
}