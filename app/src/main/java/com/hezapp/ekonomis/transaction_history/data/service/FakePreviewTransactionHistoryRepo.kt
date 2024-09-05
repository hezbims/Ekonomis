package com.hezapp.ekonomis.transaction_history.data.service

import com.hezapp.ekonomis.core.domain.model.MyBasicError
import com.hezapp.ekonomis.core.domain.entity.support_enum.ProfileType
import com.hezapp.ekonomis.core.domain.model.ResponseWrapper
import com.hezapp.ekonomis.transaction_history.domain.model.PreviewTransactionHistory
import com.hezapp.ekonomis.transaction_history.domain.service.IPreviewTransactionHistoryRepo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakePreviewTransactionHistoryRepo : IPreviewTransactionHistoryRepo {
    override fun getListPreviewTransactionHistory(): Flow<ResponseWrapper<List<PreviewTransactionHistory>, MyBasicError>> =
        flow {
            emit(ResponseWrapper.Loading())
            delay(500L)

            val listData = listOf(
                PreviewTransactionHistory(
                    id = 1,
                    personName = "Cik. Feni",
                    date = "Minggu, 11-Jan-2023",
                    personType = ProfileType.CUSTOMER
                ),
                PreviewTransactionHistory(
                    id = 1,
                    personName = "Om Beni",
                    date = "Senin, 1-Jan-2023",
                    personType = ProfileType.SUPPLIER
                ),
            )

            emit(ResponseWrapper.Succeed(data = listData))
        }
}