package com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.input_form_manipulation

import com.hezapp.ekonomis.core.domain.invoice_item.entity.InvoiceItemEntity
import com.hezapp.ekonomis.core.domain.utils.contains

class GetDeletedInvoiceItemsUseCase {
    operator fun invoke(oldInvoiceItems : List<InvoiceItemEntity>, newInvoiceItem: List<InvoiceItemEntity>) : List<InvoiceItemEntity> =
        oldInvoiceItems.filter { oldItem ->
            !newInvoiceItem.contains { newItem -> newItem.id == oldItem.id }
        }
}