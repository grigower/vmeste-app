package com.example.tzapp.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.tzapp.NotificationHelper

class ReminderWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val title = inputData.getString(KEY_TITLE) ?: "Событие"
        val text = inputData.getString(KEY_TEXT) ?: "Напоминание"
        val id = inputData.getInt(KEY_ID, (System.currentTimeMillis() % Int.MAX_VALUE).toInt())
        NotificationHelper.show(applicationContext, title, text, id)
        return Result.success()
    }

    companion object {
        const val KEY_TITLE = "title"
        const val KEY_TEXT = "text"
        const val KEY_ID = "id"
    }
}

