package com.hezapp.ekonomis.core.data.repo

import com.hezapp.ekonomis.core.domain.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.model.MyBasicError
import com.hezapp.ekonomis.core.domain.model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.repo.IProductRepo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeProductRepo : IProductRepo {
    override fun getAllProduct(searchQuery: String): Flow<ResponseWrapper<List<ProductEntity>, MyBasicError>> =
        flow {
            emit(ResponseWrapper.Loading())
            delay(200L)
            emit(ResponseWrapper.Succeed(
                listProduct.filter {
                    it.name.contains(searchQuery, ignoreCase = true)
                }
            ))
        }

    private companion object {
        val listProduct = listOf(
            ProductEntity(
                id = 0,
                name = "Tuna Deho"
            ),
            ProductEntity(
                id = 1,
                name = "White Heinz Vinegar"
            )
        )
    }
}