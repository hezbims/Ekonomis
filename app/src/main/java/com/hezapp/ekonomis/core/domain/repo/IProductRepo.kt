package com.hezapp.ekonomis.core.domain.repo

import com.hezapp.ekonomis.core.domain.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.model.MyBasicError
import com.hezapp.ekonomis.core.domain.model.ResponseWrapper
import kotlinx.coroutines.flow.Flow

interface IProductRepo {
    fun getAllProduct(searchQuery : String) : Flow<ResponseWrapper<List<ProductEntity>, MyBasicError>>
}