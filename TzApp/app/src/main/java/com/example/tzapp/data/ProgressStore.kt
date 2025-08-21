package com.example.tzapp.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.progressDataStore by preferencesDataStore(name = "trainer_progress")

object ProgressStore {
    private val KEY_POINTS = intPreferencesKey("points")
    private val KEY_STREAK = intPreferencesKey("streak")

    fun pointsFlow(context: Context): Flow<Int> = context.progressDataStore.data.map { it[KEY_POINTS] ?: 0 }
    fun streakFlow(context: Context): Flow<Int> = context.progressDataStore.data.map { it[KEY_STREAK] ?: 0 }

    suspend fun addPoints(context: Context, delta: Int) {
        context.progressDataStore.edit { prefs: Preferences ->
            val current = prefs[KEY_POINTS] ?: 0
            prefs[KEY_POINTS] = (current + delta).coerceAtLeast(0)
        }
    }

    suspend fun setStreak(context: Context, streak: Int) {
        context.progressDataStore.edit { prefs ->
            prefs[KEY_STREAK] = streak.coerceAtLeast(0)
        }
    }
}

