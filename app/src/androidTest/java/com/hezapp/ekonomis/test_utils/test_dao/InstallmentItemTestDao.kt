package com.hezapp.ekonomis.test_utils.test_dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface InstallmentItemTestDao {
    @Query("SELECT COUNT(*) FROM installment_items")
    fun count() : Int
}