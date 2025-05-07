package com.example.mobilepet.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database (entities = [Pet::class], version = 1)
abstract class PetDatabase: RoomDatabase () {
    abstract fun petDao(): PetDao
}

fun getDatabase(context: Context): PetDatabase {
    return Room.databaseBuilder(
        context.applicationContext,
        PetDatabase::class.java,
        "pet_database"
    ).build()
}
