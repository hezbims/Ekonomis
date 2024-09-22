package com.hezapp.ekonomis.product_detail.domain.use_case

import com.hezapp.ekonomis.core.data.product.FakeProductRepo
import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.product.model.ProductDetail
import com.hezapp.ekonomis.core.domain.product.repo.IProductRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetProductDetailUseCase {
    private val repo : IProductRepo = FakeProductRepo()

    operator fun invoke(
        productId : Int,
        monthYearPeriod: Long,
    ) : Flow<ResponseWrapper<ProductDetail, MyBasicError>> =
    flow<ResponseWrapper<ProductDetail, MyBasicError>> {
        emit(ResponseWrapper.Loading())
        emit(ResponseWrapper.Succeed(
            repo.getProductDetail(
                productId = productId,
                monthYearPeriod = monthYearPeriod,
            )
        ))
    }.catch { emit(ResponseWrapper.Failed()) }
}