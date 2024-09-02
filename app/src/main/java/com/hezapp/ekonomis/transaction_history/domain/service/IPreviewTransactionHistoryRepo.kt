package com.hezapp.ekonomis.transaction_history.domain.service

import com.hezapp.ekonomis.core.domain.model.MyBasicError
import com.hezapp.ekonomis.core.domain.model.ResponseWrapper
import com.hezapp.ekonomis.transaction_history.domain.model.PreviewTransactionHistory
import kotlinx.coroutines.flow.Flow

interface IPreviewTransactionHistoryRepo {
    fun getListPreviewTransactionHistory() : Flow<ResponseWrapper<List<PreviewTransactionHistory> , MyBasicError>>
}