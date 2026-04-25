package com.hezapp.ekonomis.edit_product_name_dialog.application.use_case.iface

import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.edit_product_name_dialog.application.model.EditProductNameError
import kotlinx.coroutines.flow.Flow

interface IEditProductNameUseCase {
    operator fun invoke(
        productId: Int,
        name: String,
    ) : Flow<ResponseWrapper<Any?, EditProductNameError>>
}