package com.happy.easter.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
internal interface HappyEasterDownloadDao {
    @Query("SELECT * FROM HappyEasterDownloadEntity")
    fun getAll(): List<HappyEasterDownloadEntity>

    @Insert
    fun insert(entity: HappyEasterDownloadEntity)

    @Update
    fun update(entities: List<HappyEasterDownloadEntity>)

    @Query("DELETE FROM HappyEasterDownloadEntity WHERE id IN (:ids)")
    fun delete(ids: List<Long>)
}
