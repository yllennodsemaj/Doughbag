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
    var balance: Double = 1000000.0,
    var total_wins: Int = 0,
    var total_losses: Int = 0,
    var rWins: Int = 0,
    var rLosses: Int = 0,
    var cWins: Int = 0,
    var cLosses: Int = 0,
    var sWins: Int = 0,
    var sLosses: Int = 0,
    var winnings: Double = 0.0,
    var losses: Double = 0.0,
    var ownedSkinIds: String = "0,1,2"
)