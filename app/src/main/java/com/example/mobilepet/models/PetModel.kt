package com.example.mobilepet.models

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilepet.components.Pet
import kotlinx.coroutines.flow.first
import com.example.mobilepet.models.PetPreferences
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

sealed class FeedResult {
    object Success : FeedResult()
    data class TooEarly(val remainingTimeMs: Long) : FeedResult()
}


class PetModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PetPreferences(application.applicationContext)

    var pet: Pet? by mutableStateOf(null)
        private set


    private var lastFeedTime: Long = 0L

    init {
        viewModelScope.launch {
            val mood = prefs.mood.first()
            val energy = prefs.energy.first()
            val hunger = prefs.hunger.first()
            val name = prefs.name.firstOrNull() ?: ""
            val type = prefs.type.firstOrNull() ?: ""

            if (name.isNotBlank() && type.isNotBlank()) {
                pet = Pet(
                    name = name,
                    type = type,
                    age = 1,
                    hunger = hunger,
                    mood = mood,
                    energy = energy
                )
            }
        }
    }


    fun createPet(name: String, type: String) {
        pet = Pet(
            name = name,
            type = type,
            age = 1,
            hunger = 65,
            mood = 30,
            energy = 40
        )

        viewModelScope.launch {
            prefs.saveStats(pet!!.mood, pet!!.energy, pet!!.hunger)
            prefs.saveNameAndType(name, type)
        }
    }


    fun feedPet(): FeedResult {
        val now = System.currentTimeMillis()
        val timeSinceLastFeed = lastFeedTime
        val timeSinceLastFeedMs = now - timeSinceLastFeed

        if (timeSinceLastFeedMs < 30_000) {
            val remaining = 30_000 - timeSinceLastFeedMs
            return FeedResult.TooEarly(remaining)
        }

        pet?.let {
            pet = it.copy(
                hunger = (it.hunger - 13).coerceAtLeast(0),
                mood = (it.mood + 17).coerceAtMost(100),
                energy = (it.energy + 12).coerceAtMost(100)
            )

            lastFeedTime = now

            viewModelScope.launch {
                prefs.saveStats(pet!!.mood, pet!!.energy, pet!!.hunger)
                prefs.saveFeedTimestamp(now)
            }
        }
        return FeedResult.Success
    }



    fun exercisePet() {

    }
        fun restPet() {
            pet?.let {
             pet = it.copy(
                hunger = (it.hunger + 5).coerceAtMost(100),
                mood = (it.mood - 5).coerceAtLeast(0),
                energy = (it.energy + 20).coerceAtMost(100)
            )

            viewModelScope.launch {
                prefs.saveStats(it.mood, it.energy, it.hunger)
            }
        }
    }

    fun resetPet() {
        pet = null
        viewModelScope.launch {
            prefs.saveStats(25, 40, 50)
            prefs.saveNameAndType("", "")
            prefs.saveFeedTimestamp(0L)
        }
    }
}
