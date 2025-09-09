package com.hezapp.ekonomis.core.data.installment.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hezapp.ekonomis.core.data.database.TableNames
import com.hezapp.ekonomis.core.domain.invoice.entity.Installment

@Dao
interface InstallmentDao {
    @Insert
    fun insert(data: Installment) : Long

    @Query("DELETE FROM ${TableNames.INSTALLMENT} WHERE invoice_id = :invoiceId")
    fun deleteByInvoiceId(invoiceId: Int)
}