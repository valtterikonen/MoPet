package com.example.mobilepet.models

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "pet_prefs")

object PetKeys {
    val NAME = stringPreferencesKey("pet_name")
    val TYPE = stringPreferencesKey("pet_type")
    val MOOD = intPreferencesKey("mood")
    val ENERGY = intPreferencesKey("energy")
    val HUNGER = intPreferencesKey("hunger")
    val LAST_FEED_TIME = longPreferencesKey("last_feed_time")
}

class PetPreferences(private val context: Context) {
    val mood: Flow<Int> = context.dataStore.data.map { it[PetKeys.MOOD] ?: 25 }
    val energy: Flow<Int> = context.dataStore.data.map { it[PetKeys.ENERGY] ?: 40 }
    val hunger: Flow<Int> = context.dataStore.data.map { it[PetKeys.HUNGER] ?: 50 }
    val lastFeedTime: Flow<Long> = context.dataStore.data.map { it[PetKeys.LAST_FEED_TIME] ?: 0L }
    val name: Flow<String> = context.dataStore.data.map { it[PetKeys.NAME] ?: "" }
    val type: Flow<String> = context.dataStore.data.map { it[PetKeys.TYPE] ?: "" }

    suspend fun saveNameAndType(name: String, type: String) {
        context.dataStore.edit {
            it[PetKeys.NAME] = name
            it[PetKeys.TYPE] = type
        }
    }

    suspend fun saveStats(mood: Int, energy: Int, hunger: Int) {
        context.dataStore.edit {
            it[PetKeys.MOOD] = mood
            it[PetKeys.ENERGY] = energy
            it[PetKeys.HUNGER] = hunger
        }
    }

    suspend fun saveFeedTimestamp(timestamp: Long) {
        context.dataStore.edit {
            it[PetKeys.LAST_FEED_TIME] = timestamp
        }
    }
}
