package com.hezapp.ekonomis._testing_only.test_dao

import androidx.room.Dao
import androidx.room.Query
import com.hezapp.ekonomis.core.data.database.TableNames
import com.hezapp.ekonomis.core.domain.invoice.entity.InstallmentItem

@Dao
interface InstallmentItemTestDao {
    @Query("SELECT COUNT(*) FROM ${TableNames.INSTALLMENT_ITEMS}")
    fun count() : Int

    @Query("SELECT * FROM ${TableNames.INSTALLMENT_ITEMS} WHERE installment_id = :installmentId")
    fun getByInstallmentId(installmentId: Int) : List<InstallmentItem>
}