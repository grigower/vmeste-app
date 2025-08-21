package com.example.tzapp.data.planner

import android.content.Context
import kotlinx.coroutines.flow.Flow

class PlannerRepository(context: Context) {
    private val dao: PlannerDao = PlannerDatabase.get(context).plannerDao()

    fun events(): Flow<List<PlannerEventEntity>> = dao.getAllFlow()

    suspend fun addEvent(title: String, type: String, dateTimeText: String, reminderMinutes: Int?, repeat: String?) {
        dao.insert(
            PlannerEventEntity(
                title = title,
                type = type,
                dateTimeText = dateTimeText,
                reminderMinutes = reminderMinutes,
                repeat = repeat
            )
        )
    }
}

