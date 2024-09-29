package com.hezapp.ekonomis.product_detail.domain.use_case

import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.monthly_stock.entity.MonthlyStockEntity
import com.hezapp.ekonomis.core.domain.monthly_stock.entity.QuantityPerUnitType
import com.hezapp.ekonomis.core.domain.monthly_stock.repo.IMonthlyStockRepo
import com.hezapp.ekonomis.core.domain.utils.ITransactionProvider
import com.hezapp.ekonomis.product_detail.domain.model.EditMonthlyStockFieldError
import com.hezapp.ekonomis.product_detail.domain.model.EditMonthlyStockValidationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class EditMonthlyStockUseCase(
    private val repo : IMonthlyStockRepo,
    private val transactionProvider : ITransactionProvider,
) {
    suspend operator fun invoke(
        cartonQuantity: Int?,
        pieceQuantity: Int?,
        monthlyStockEntityId: Int,
        monthYearPeriod: Long,
        productId : Int,
    ) : Flow<ResponseWrapper<Any?, EditMonthlyStockValidationResult>> =
    flow<ResponseWrapper<Any?, EditMonthlyStockValidationResult>> {
        emit(ResponseWrapper.Loading())

        val cartonError = if (cartonQuantity == null) EditMonthlyStockFieldError.FieldEmpty
                            else null
        val pieceError = if (pieceQuantity == null) EditMonthlyStockFieldError.FieldEmpty
                            else null
        if (cartonError != null || pieceError != null){
            emit(ResponseWrapper.Failed(
                EditMonthlyStockValidationResult(
                    cartonError = cartonError,
                    pieceError = pieceError,
                )
            ))
            return@flow
        }

        transactionProvider.withTransaction {
            val result = repo.upsertMonthlyStock(
                monthlyStockEntity = MonthlyStockEntity(
                    id = monthlyStockEntityId,
                    monthYearPeriod = monthYearPeriod,
                    productId = productId,
                    quantityPerUnitType = QuantityPerUnitType(
                        cartonQuantity = cartonQuantity!!,
                        pieceQuantity = pieceQuantity!!,
                    ),
                )
            )
            assert(result == -1)
        }
        emit(ResponseWrapper.Succeed(null))
    }.catch { emit(ResponseWrapper.Failed()) }
}