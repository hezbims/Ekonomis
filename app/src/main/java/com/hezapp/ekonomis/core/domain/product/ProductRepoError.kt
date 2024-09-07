package com.hezapp.ekonomis.core.domain.product

import com.hezapp.ekonomis.core.domain.model.MyBasicError

sealed interface InsertProductError : MyBasicError {
    data object EmptyInputName : InsertProductError
    data object AlreadyUsed : InsertProductError
}