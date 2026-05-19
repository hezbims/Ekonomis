package com.hezapp.ekonomis.edit_product_name_dialog.application.model

sealed class GetProductByIdError {
    data object NotFound : GetProductByIdError()
}