package com.hezapp.ekonomis.core.domain.product.model

import com.hezapp.ekonomis.core.domain.general_model.MyBasicError

sealed interface InsertProductError : MyBasicError {
    data object EmptyInputName : InsertProductError
    data object AlreadyUsed : InsertProductError
}