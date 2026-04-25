package com.hezapp.ekonomis.edit_product_name_dialog.application.model

sealed class EditProductNameError {
    object EmptyName : EditProductNameError()
    object ProductIdNotFound : EditProductNameError()
    object ProductNameAlreadyExist : EditProductNameError()
}