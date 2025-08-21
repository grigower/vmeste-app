package com.example.tzapp.data.planner

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PlannerEventEntity::class], version = 1, exportSchema = false)
abstract class PlannerDatabase : RoomDatabase() {
    abstract fun plannerDao(): PlannerDao

    companion object {
        @Volatile private var INSTANCE: PlannerDatabase? = null

        fun get(context: Context): PlannerDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                PlannerDatabase::class.java,
                "planner-db"
            ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
        }
    }
}

