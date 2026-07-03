package com.example.doughbag

import androidx.room.Entity
import androidx.room.PrimaryKey

// skins database
@Entity(tableName = "owned_skins")
data class Owned_Skins(
    @PrimaryKey(autoGenerate = true)
    val skin_ID: Int = 0, // primary key
    val user_ID: Int, // foreign key
    val name: String
)