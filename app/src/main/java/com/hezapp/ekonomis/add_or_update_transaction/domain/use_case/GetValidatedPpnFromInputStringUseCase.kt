package com.hezapp.ekonomis.add_or_update_transaction.domain.use_case

class GetValidatedPpnFromInputStringUseCase {
    operator fun invoke(input : String) : Int {
        val intValue = input.toInt()
        if (intValue < 1 || intValue > 100)
            throw IllegalArgumentException("Invalid PPN Value")
        return intValue
    }
}