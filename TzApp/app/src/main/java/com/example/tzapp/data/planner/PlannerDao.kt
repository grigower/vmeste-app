package com.example.tzapp.data.planner

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlannerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: PlannerEventEntity)

    @Query("SELECT * FROM planner_events ORDER BY createdAt DESC")
    fun getAllFlow(): Flow<List<PlannerEventEntity>>
}

