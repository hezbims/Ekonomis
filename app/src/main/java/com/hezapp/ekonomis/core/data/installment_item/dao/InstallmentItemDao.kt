package com.hezapp.ekonomis.core.data.installment_item.dao

import androidx.room.Dao
import androidx.room.Insert
import com.hezapp.ekonomis.core.domain.invoice.entity.InstallmentItem

@Dao
interface InstallmentItemDao {
    @Insert
    fun insert(datas : List<InstallmentItem>) : List<Long>
}