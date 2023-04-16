package com.happy.easter.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
internal interface HappyEasterMetricDao {
    @Query("SELECT * FROM metric")
    fun getAll(): List<HappyEasterMetricData>

    @Delete
    fun delete(metrics: List<HappyEasterMetricData>)

    @Insert
    fun insert(entity: HappyEasterMetricData)

    @Insert
    fun batchInsert(entities: List<HappyEasterMetricData>)
}
