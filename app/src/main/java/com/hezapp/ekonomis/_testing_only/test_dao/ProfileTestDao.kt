package com.hezapp.ekonomis._testing_only.test_dao

import androidx.room.Dao
import androidx.room.Query
import com.hezapp.ekonomis.core.data.database.TableNames
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileEntity

@Dao
interface ProfileTestDao {
    @Query("SELECT * FROM ${TableNames.PROFILE} WHERE id = :profileId")
    suspend fun getById(profileId: Int) : ProfileEntity

    @Query("SELECT COUNT(*) FROM ${TableNames.PROFILE}")
    suspend fun count() : Int
}