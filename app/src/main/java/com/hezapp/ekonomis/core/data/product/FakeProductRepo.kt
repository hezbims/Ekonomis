package com.hezapp.ekonomis.core.data.product

import com.hezapp.ekonomis.core.domain.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.product.IProductRepo
import com.hezapp.ekonomis.core.domain.product.InsertProductError
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

    override fun insertProduct(newProduct: ProductEntity): Flow<ResponseWrapper<Any?, InsertProductError>> =
        flow {
            emit(ResponseWrapper.Loading())
            delay(50L)
            if (newProduct.name.isEmpty()){
                emit(ResponseWrapper.Failed(InsertProductError.EmptyInputName))
            } else if(
                listProduct.firstOrNull { it.name.equals(newProduct.name, ignoreCase = true) } != null
            ) {
               emit(ResponseWrapper.Failed(InsertProductError.AlreadyUsed))
            }
            else {
                listProduct.add(newProduct.copy(id = id++))
                emit(ResponseWrapper.Succeed(null))
            }
        }

    private companion object {
        val listProduct = mutableListOf(
            ProductEntity(
                id = 0,
                name = "Tuna Deho"
            ),
            ProductEntity(
                id = 1,
                name = "White Heinz Vinegar"
            )
        )
        var id = 2
    }
}