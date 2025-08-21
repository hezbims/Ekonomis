package com.hezapp.ekonomis.test_utils.test_dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface InstallmentTestDao {
    @Query("SELECT COUNT(*) FROM installments")
    fun count() : Int
}