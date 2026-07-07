package com.example.doughbag

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface SkinDao {

    // 1. Give a user a new skin (when they buy it)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOwnedSkin(skin: Owned_Skins)

    // 2. Get just the list of skins a specific user owns
    @Query("SELECT * FROM owned_skins WHERE user_ID = :userId")
    fun getOwnedSkinsForUser(userId: Int): Flow<List<Owned_Skins>>

    // 3. (Optional but powerful) Get the User AND their Skins together in one object
    @Transaction
    @Query("SELECT * FROM users WHERE user_ID = :userId")
    fun getUserWithSkins(userId: Int): Flow<User_With_Skins>
}