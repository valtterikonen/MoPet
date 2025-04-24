package com.example.mobilepet.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "Pets")
data class Pet(
    @PrimaryKey (autoGenerate = true) val id: Int? = null,
    val name: String,
    val type: String?
)


