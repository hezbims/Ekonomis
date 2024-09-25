package com.hezapp.ekonomis.core.data.invoice.repo

import com.hezapp.ekonomis.add_or_update_transaction.domain.model.InvoiceFormModel
import com.hezapp.ekonomis.core.data.invoice.dao.InvoiceDao
import com.hezapp.ekonomis.core.domain.invoice.model.PreviewTransactionFilter
import com.hezapp.ekonomis.core.domain.invoice.model.PreviewTransactionHistory
import com.hezapp.ekonomis.core.domain.invoice.relationship.FullInvoiceDetails
import com.hezapp.ekonomis.core.domain.invoice.repo.IInvoiceRepo
import com.hezapp.ekonomis.core.domain.utils.getNextMonthYear
import com.hezapp.ekonomis.core.domain.utils.toBeginningOfMonth
import com.hezapp.ekonomis.core.domain.utils.toCalendar

class InvoiceRepo(
    private val dao: InvoiceDao
) : IInvoiceRepo {
    override suspend fun createNewInvoice(newInvoice: InvoiceFormModel): Int {
        TODO("Not yet implemented")
    }

    override suspend fun updateInvoice(newInvoice: InvoiceFormModel): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getPreviewInvoices(filter: PreviewTransactionFilter): List<PreviewTransactionHistory> {
        val firstDayOfCurrentPeriod = filter.monthYear.toCalendar().toBeginningOfMonth().timeInMillis
        return dao.getPreviewTransactionHistory(
            firstDayOfMonth = firstDayOfCurrentPeriod,
            lastDayOfMonth = firstDayOfCurrentPeriod.getNextMonthYear()
        )
    }

    override suspend fun getFullInvoiceDetails(id: Int): FullInvoiceDetails {
        TODO("Not yet implemented")
    }

    override suspend fun deleteInvoice(id: Int) {
        TODO("Not yet implemented")
    }
}