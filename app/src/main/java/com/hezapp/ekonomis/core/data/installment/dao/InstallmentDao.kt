package com.hezapp.ekonomis.core.data.installment.dao

import androidx.room.Dao
import androidx.room.Insert
import com.hezapp.ekonomis.core.domain.invoice.entity.Installment

@Dao
interface InstallmentDao {
    @Insert
    fun insert(data: Installment) : Long
}