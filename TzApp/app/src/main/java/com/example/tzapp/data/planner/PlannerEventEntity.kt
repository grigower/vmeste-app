package com.example.tzapp.data.planner

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "planner_events")
data class PlannerEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val type: String, // MEDS, DOCTOR, EVENT
    val dateTimeText: String, // хранение в виде строки для простоты ввода (русский поддерживается)
    val reminderMinutes: Int?,
    val repeat: String?, // Ежедневно, Еженедельно, null
    val createdAt: Long = System.currentTimeMillis()
)

