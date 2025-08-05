package com.hezapp.ekonomis.core.data.profile.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileEntity

@Dao
interface ProfileDao {
    @Query("""
        SELECT * FROM profiles 
        WHERE name LIKE '%' || :profileName || '%' AND (
            :profileTypeId IS NULL OR type = :profileTypeId 
        ) 
    """)
    suspend fun getListProfile(
        profileName: String,
        profileTypeId: Int?,
    ) : List<ProfileEntity>

    @Insert
    suspend fun insertProfile(newProfile: ProfileEntity) : Long
    @Insert
    suspend fun insertProfiles(newProfiles: List<ProfileEntity>) : List<Long>
    @Query("""
        SELECT * FROM profiles
        WHERE id IN (:ids)
    """)
    suspend fun getProfilesByIds(ids: List<Int>) : List<ProfileEntity>
}