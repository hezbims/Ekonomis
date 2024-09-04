package com.hezapp.ekonomis.add_new_transaction.domain.person.use_case

class GetValidatedPpnFromInputStringUseCase {
    operator fun invoke(input : String) : Int {
        val intValue = input.toInt()
        if (intValue < 1 || intValue > 100)
            throw IllegalArgumentException("Invalid PPN Value")
        return intValue
    }
}