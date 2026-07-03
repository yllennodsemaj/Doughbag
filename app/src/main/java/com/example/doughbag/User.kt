package com.example.doughbag

import androidx.room.Entity
import androidx.room.PrimaryKey

// user database
@Entity(tableName = "users")
data class User(
    // autoincrement user ID
    @PrimaryKey(autoGenerate = true)
    val user_ID: Int = 0,
    val user_name: String,
    val balance: Double = 1000000.0,
    val total_wins: Int = 0,
    val total_losses: Int = 0,
    val rWins: Int = 0,
    val rLosses: Int = 0,
    val cWins: Int = 0,
    val cLosses: Int = 0,
    val sWins: Int = 0,
    val sLosses: Int = 0,
    val winnings: Int = 0,
    val losses: Int = 0
)