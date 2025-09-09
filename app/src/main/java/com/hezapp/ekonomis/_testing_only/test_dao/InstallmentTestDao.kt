package com.hezapp.ekonomis._testing_only.test_dao

import androidx.room.Dao
import androidx.room.Query
import com.hezapp.ekonomis.core.data.database.TableNames
import com.hezapp.ekonomis.core.domain.invoice.entity.Installment

@Dao
interface InstallmentTestDao {
    @Query("SELECT COUNT(*) FROM ${TableNames.INSTALLMENT}")
    fun count() : Int

    @Query("SELECT * FROM ${TableNames.INSTALLMENT} WHERE invoice_id = :id")
    fun getByInvoiceId(id: Int) : Installment?
}