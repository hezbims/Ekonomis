package com.hezapp.ekonomis.transaction_history.application.use_case.iface

import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.invoice.model.PreviewTransactionFilter
import com.hezapp.ekonomis.transaction_history.application.dto.PreviewTransactionHistory
import kotlinx.coroutines.flow.Flow

interface IGetPreviewTransactionHistoryUseCase {
    operator fun invoke(filter: PreviewTransactionFilter):
            Flow<ResponseWrapper<List<PreviewTransactionHistory>, MyBasicError>>
}