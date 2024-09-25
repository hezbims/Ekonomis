package com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.input_form_manipulation

class GetValidatedProductPriceUseCase {
    operator fun invoke(price : String) : Int {
        if (price.length > 9)
            throw IllegalArgumentException()

        val intPrice = price.toInt()
        if (intPrice < 0)
            throw IllegalArgumentException()
        return intPrice
    }
}