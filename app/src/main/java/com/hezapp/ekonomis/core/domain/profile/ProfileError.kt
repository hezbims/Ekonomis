package com.hezapp.ekonomis.core.domain.profile

import com.hezapp.ekonomis.core.domain.model.MyBasicError

sealed class CreateNewProfileError : MyBasicError {
    data object NameCantBeEmpty : CreateNewProfileError()
    data object NameAlreadyExist : CreateNewProfileError()
}