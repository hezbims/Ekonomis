package com.hezapp.ekonomis.test_utils.test_dao

import androidx.room.Dao
import androidx.room.Query
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileEntity

@Dao
interface ProfileTestDao {
    @Query("SELECT * FROM profiles WHERE id = :profileId")
    suspend fun getById(profileId: Int) : ProfileEntity
}