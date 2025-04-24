package com.example.mobilepet.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PetDao {
    @Query("SELECT * FROM Pets")
    suspend fun getAllPets(): List<Pet>

    @Query("SELECT * FROM Pets WHERE name = :name")
    suspend fun getPetByName(name: String): Pet?

    @Insert
    suspend fun insertPet(pet: Pet)

    @Delete
    suspend fun deletePet(pet: Pet)
}